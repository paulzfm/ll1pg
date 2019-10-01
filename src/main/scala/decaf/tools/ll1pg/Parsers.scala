package decaf.tools.ll1pg

import decaf.tools.ll1pg.SpecAST.{Class, Const, ConstToken, Header, Headers, Ident, IdentToken, Imports, JavaCode, NonTerminal, OutputFile, Package, Rule, SemValue, Spec, Start, Term, Terminal, Token, Tokens}

import scala.util.matching.Regex
import scala.util.parsing.combinator.RegexParsers
import scala.util.parsing.input.{NoPosition, Position}
import scala.util.{Failure, Success, Try}

/**
  * Parsers to parse a specification file.
  */
object Parsers {

  /**
    * Java source code parser.
    */
  class CodeLineParser extends RegexParsers {
    def code: Parser[JavaCode] = in => {
      val source = in.source
      val offset = in.offset
      val start = offset
      val length = source.length()

      def scan(index: Int, count: Int): ParseResult[JavaCode] = {
        if (index >= length)
          Failure("Java code unclosed with correct number of `}'s", in.drop(start - offset))
        else
          source.charAt(index) match {
            case '{' => scan(index + 1, count + 1)
            case '}' =>
              if (count == 0)
                Success(JavaCode(source.subSequence(start, index).toString),
                  in.drop(index - offset))
              else scan(index + 1, count - 1)
            case _ => scan(index + 1, count)
          }
      }

      scan(start, 0)
    }
  }

  /**
    * Token and literal parsers.
    */
  class TokenParser extends CodeLineParser {
    def ident: Parser[Ident] =
      positioned("""[_A-Za-z][_A-Za-z0-9]*""".r ^^ Ident)

    def constChar: Parser[Const] = positioned("'" ~> """.""".r <~ "'" ^^ {
      str => Const(str.head)
    })

    def token: Parser[Token] = positioned(ident ^^ IdentToken | constChar ^^ ConstToken)

    def pkgName: Parser[Ident] = positioned("""[a-zA-Z0-9_.*]+""".r ^^ Ident)

    def constStr: Parser[Ident] = positioned("\"" ~> """[^"]*""".r <~ "\"" ^^ Ident)

    // to ignore Java-style comments
    protected override val whiteSpace: Regex =
      """(\s|//.*|(?m)/\*(\*(?!/)|[^*])*\*/)+""".r
  }

  /**
    * Header parsers.
    */
  class HeaderParsers extends TokenParser {
    def pkg: Parser[Package] = positioned("%package" ~> pkgName ^^ Package)

    def imports: Parser[Imports] = positioned("%import" ~> pkgName.* ^^ Imports)

    def semValue: Parser[SemValue] = positioned("%sem" ~> ident ^^ SemValue)

    def javaClassMod: Parser[Ident] = positioned {
      ("public" | "abstract" | "final") ^^ Ident
    }

    def cls: Parser[Class] = positioned("%class" ~> javaClassMod.* ~ "class" ~ ident ~ ident.* ^^ {
      case mods ~ _ ~ name ~ others => Class(name, (mods ++ List("class", name) ++ others).mkString(" "))
    })

    def outputFile: Parser[OutputFile] = positioned("%output" ~> constStr ^^ OutputFile)

    def start: Parser[Start] = positioned("%start" ~> ident ^^ (NonTerminal andThen Start))

    def tokens: Parser[Tokens] = positioned("%tokens" ~> token.* ^^ Tokens)

    def header: Parser[Header] = positioned(pkg | imports | semValue | cls | outputFile | start | tokens)

    def headers: Parser[List[Header]] = header.* <~ "%%"
  }

  /**
    * Rule parsers.
    *
    * @param tokens declared terminals.
    */
  class RuleParser(tokens: List[Token]) extends HeaderParsers {
    def action: Parser[JavaCode] = positioned("{" ~> code <~ "}")

    def term: Parser[Term] = in => {
      val p = new TokenParser
      p.parse(p.token, in) match {
        case p.Success(token: Token, next) => token match {
          case IdentToken(t) =>
            val r = if (tokens.contains(token)) Terminal(token) else NonTerminal(t)
            Success(r, next)
          case ConstToken(_) =>
            if (tokens.contains(token)) Success(Terminal(token), next)
            else Error(s"undefined token: $token", in)
        }
        case p.Error(msg, next) => Error(msg, next)
        case p.Failure(msg, next) => Failure(msg, next)
      }
    }

    def right: Parser[(List[Term], JavaCode)] =
      term.* ~ action.? ^^ {
        case ts ~ Some(a) => (ts, a)
        case ts ~ None =>
          val emptyAction = JavaCode("/* no action */")
          if (ts.nonEmpty) emptyAction.setPos(ts.last.pos)
          (ts, emptyAction)
      }

    def rule: Parser[Rule] = positioned(
      (ident <~ ":") ~ rep1sep(right, "|") <~ ";".? ^^ {
        case l ~ rs => Rule(NonTerminal(l), rs)
      })

    def rules: Parser[List[Rule]] = rule.*
  }

  /**
    * Error type.
    *
    * @param msg error message.
    * @param pos error location.
    */
  case class Error(msg: String, pos: Position = NoPosition) extends Exception {
    override def getMessage: String = pos match {
      case NoPosition => s"Error: $msg"
      case _ => s"Error: at (${pos.line}, ${pos.column}): $msg:\n${pos.longString}"
    }
  }

  /**
    * Check if some undefined non-terminal, i.e., which has no right-hand side, are used by some
    * rule.
    *
    * @param rules all rules parsed.
    */
  def checkNonTerminals(rules: List[Rule]): Unit = {
    val defined = rules.map(_.left.symbol)
    for {
      Rule(_, rs) <- rules
      (ts, _) <- rs
      NonTerminal(t) <- ts
    } yield if (!defined.contains(t)) throw Error(s"undefined non-terminal: $t", t.pos)
  }

  /**
    * First step of parsing: parse headers.
    *
    * We create `parseHeaders` and `parseRules` to transform `ParseResult[T]` to `Try[T]`,
    * so that we can simply use `for ... yield ...` in `parse` function to combine the execution
    * steps that potentially throw exceptions.
    *
    * @param source specification file as text.
    * @return - `Success(hds, next)` if succeeds, and `hds` are the parsed `Header`s, and `next`
    *         is the remain text.
    *         - `Failure(ex)` if fails, and `ex` shows the error.
    */
  def parseHeaders(source: String): Try[(List[Header], HeaderParsers#Input)] = {
    val p = new HeaderParsers
    p.parse(p.headers, source) match {
      case p.Success(hds: List[Header], in) => Success(hds, in)
      case p.Failure(msg, next) => Failure(Error(msg, next.pos))
      case p.Error(msg, next) => Failure(Error(msg, next.pos))
    }
  }

  /**
    * Second step of parsing: parse rules.
    *
    * @param source remain specification file (without headers).
    * @return - `Success(rules)` if succeeds, and `rules` are the parsed `Rule`s.
    *         - `Failure(ex)` if fails, and `ex` shows the error.
    */
  def parseRules(source: HeaderParsers#Input, headers: Headers): Try[List[Rule]] = {
    val tokens = headers._6.tokens
    val p = new RuleParser(tokens)
    p.parseAll(p.rules, source) match {
      case p.Success(rules: List[Rule], _) => Success(rules)
      case p.Failure(msg, next) => Failure(Error(msg, next.pos))
      case p.Error(msg, next) => Failure(Error(msg, next.pos))
    }
  }

  /**
    * Entry parser. Call this to parse a specification file.
    *
    * @param source specification file as text.
    * @return - `Success(spec)` if succeeds, and `spec` is the parsed `Spec`.
    *         - `Failure(ex)` if fails, and `ex` shows the error.
    */
  def parse(source: String): Try[Spec] = {
    def find[T <: Header](hds: List[Header], tester: Header => Boolean,
                          header: String, definition: String): Try[T] = {
      hds.find(tester) match {
        case Some(h) => Success(h.asInstanceOf[T])
        case None => Failure(Error(s"$header undefined, define it by `$definition'"))
      }
    }

    for {
      (hds, next) <- parseHeaders(source)
      h1 <- find[Package](hds, x => x.isInstanceOf[Package], "package", "%package")
      h2 <- find[Imports](hds, x => x.isInstanceOf[Imports], "imports", "%import")
      h3 <- find[SemValue](hds, x => x.isInstanceOf[SemValue], "semantic value", "%sem")
      h4 <- find[Class](hds, x => x.isInstanceOf[Class], "class", "%class")
      h5 <- find[OutputFile](hds, x => x.isInstanceOf[OutputFile], "output file path", "%output")
      h6 <- find[Tokens](hds, x => x.isInstanceOf[Tokens], "tokens", "%tokens")
      h7 <- find[Start](hds, x => x.isInstanceOf[Start], "start symbol", "%start")
      headers = (h1, h2, h3, h4, h5, h6, h7)
      rules <- parseRules(next, headers)
      _ <- Try(checkNonTerminals(rules))
    } yield Spec(headers, rules)
  }
}
