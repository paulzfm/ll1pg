import SpecAST._

import scala.util.matching.Regex
import scala.util.parsing.combinator.RegexParsers
import scala.util.parsing.input.{NoPosition, Position}
import scala.util.{Failure, Success, Try}

/**
  * Parsers to parse a specification file.
  */
object Parsers {

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

  class TokenParser extends CodeLineParser {
    def ident: Parser[Ident] =
      positioned("""[_A-Za-z][_A-Za-z0-9]*""".r ^^ Ident)

    def const: Parser[Const] = positioned("'" ~> """.""".r <~ "'" ^^ {
      str => Const(str.head)
    })

    def token: Parser[Token] = positioned(ident ^^ IdentToken | const ^^ ConstToken)

    def pkgName: Parser[Ident] = positioned("""[a-zA-Z0-9_.*]+""".r ^^ Ident)

    // to ignore Java-style comments
    protected override val whiteSpace: Regex =
      """(\s|//.*|(?m)/\*(\*(?!/)|[^*])*\*/)+""".r
  }

  class HeaderParsers extends TokenParser {
    def pkg: Parser[Package] = positioned("%package" ~> pkgName ^^ Package)

    def imports: Parser[Imports] = positioned("%import" ~> pkgName.* ^^ Imports)

    def semValue: Parser[SemValue] = positioned("%sem" ~> ident ^^ SemValue)

    def cls: Parser[Class] = positioned(
      "%class" ~> ident ~ ("extends" ~> ident).? ~ ("implements" ~> ident.+).? ^^ {
        case c ~ e ~ is => Class(c, e, is)
      })

    def start: Parser[Start] = positioned("%start" ~> ident ^^ (NonTerminal andThen Start))

    def tokens: Parser[Tokens] = positioned("%tokens" ~> token.* ^^ Tokens)

    def header: Parser[Header] = positioned(pkg | imports | semValue | cls | start | tokens)

    def headers: Parser[List[Header]] = header.* <~ "%%"
  }

  class RuleParser(tokens: List[Token]) extends HeaderParsers {
    def action: Parser[JavaCode] = positioned("{" ~> code <~ "}")

    def term: Parser[Term] = in => {
      val p = new TokenParser
      p.parse(p.token, in) match {
        case p.Success(token: Token, next) => token match {
          case IdentToken(t) =>
            if (tokens.contains(token)) Success(Terminal(token), next)
            else Success(NonTerminal(t), next)
          case ConstToken(_) =>
            if (tokens.contains(token)) Success(Terminal(token), next)
            else Failure(s"undefined token: $token", in)
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

  case class ParsingError(msg: String, pos: Position = NoPosition) extends Exception {
    override def getMessage: String = pos match {
      case NoPosition => s"Error: $msg"
      case _ => s"Error: at (${pos.line}, ${pos.column}): $msg:\n${pos.longString}"
    }
  }

  def checkNonTerminals(rules: List[Rule]): Unit = {
    val defined = rules.map(_.left.symbol)
    for {
      Rule(_, rs) <- rules
      (ts, _) <- rs
      NonTerminal(t) <- ts
    } yield if (!defined.contains(t)) throw ParsingError(s"undefined non-terminal: $t", t.pos)
  }

  def parse(source: String): Try[Spec] = {
    val p1 = new HeaderParsers
    p1.parse(p1.headers, source) match {
      case p1.Success(hds: List[Header], in) =>
        val tokens = hds.flatMap {
          case Tokens(ts) => ts
          case _ => Nil
        }

        def find[T <: Header](tester: Header => Boolean, header: String, definition: String): T = {
          hds.find(tester) match {
            case Some(h) => h.asInstanceOf[T]
            case None => throw ParsingError(s"$header undefined, define it by `$definition'")
          }
        }

        //Package, Imports, SemValue, Class, Tokens, Start
        val headers = (
          find[Package](x => x.isInstanceOf[Package], "package", "%package"),
          find[Imports](x => x.isInstanceOf[Imports], "imports", "%import"),
          find[SemValue](x => x.isInstanceOf[SemValue], "semantic value", "%sem"),
          find[Class](x => x.isInstanceOf[Class], "class", "%class"),
          find[Tokens](x => x.isInstanceOf[Tokens], "tokens", "%tokens"),
          find[Start](x => x.isInstanceOf[Start], "start symbol", "%start")
        )

        val p2 = new RuleParser(tokens)
        p2.parseAll(p2.rules, in) match {
          case p2.Success(rules: List[Rule], _) =>
            Try(checkNonTerminals(rules)) match {
              case Success(_) => Success(Spec(headers, rules))
              case Failure(ex) => Failure(ex)
            }
          case p2.Failure(msg, next) => Failure(ParsingError(msg, next.pos))
          case p2.Error(msg, next) => Failure(ParsingError(msg, next.pos))
        }
      case p1.Failure(msg, next) => Failure(ParsingError(msg, next.pos))
      case p1.Error(msg, next) => Failure(ParsingError(msg, next.pos))
    }
  }
}
