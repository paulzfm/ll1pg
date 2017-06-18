/**
  * Created by paul on 07/04/2017.
  */

import AST._

import scala.util.matching.Regex
import scala.util.parsing.combinator.RegexParsers
import scala.util.parsing.input.{NoPosition, Position}
import scala.util.{Failure, Success, Try}

object Parsers {

  class CodeLineParser extends RegexParsers {
    def code: Parser[JavaCode] = new Parser[JavaCode] {
      def apply(in: Input) = {
        val source = in.source
        val offset = in.offset
        val start = handleWhiteSpace(source, offset)
        val length = source.length()

        def scan(index: Int, count: Int): ParseResult[JavaCode] = {
          if (index >= length)
            Failure("Java code not closed with correct number of `}'s", in.drop(start - offset))
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
  }

  class TokenParser extends CodeLineParser {
    def ident: Parser[Ident] =
      """[_A-Za-z][_A-Za-z0-9]*""".r ^^ Ident

    def const: Parser[Const] = "'" ~> """.""".r <~ "'" ^^ {
      str => Const(str.head)
    }

    def token: Parser[Token] = ident ^^ IdentToken | const ^^ ConstToken

    def pkgName: Parser[Ident] = """[a-zA-Z0-9_.*]+""".r ^^ Ident

    // to ignore Java-style comments
    protected override val whiteSpace: Regex =
      """(\s|//.*|(?m)/\*(\*(?!/)|[^*])*\*/)+""".r
  }

  class HeaderParsers extends TokenParser {
    def pkg: Parser[Package] = "%package" ~> pkgName ^^ Package

    def imports: Parser[Imports] = "%import" ~> pkgName.* ^^ Imports

    def semValue: Parser[SemValue] = "%sem" ~> ident ^^ SemValue

    def cls: Parser[Class] =
      "%class" ~> ident ~ ("extends" ~> ident).? ~ ("implements" ~> ident.+).? ^^ {
        case c ~ e ~ is => Class(c, e, is)
      }

    def start: Parser[Start] = "%start" ~> ident ^^ (NonTerminal andThen Start)

    def tokens: Parser[Tokens] = "%tokens" ~> token.* ^^ Tokens

    def header: Parser[Header] = pkg | imports | semValue | cls | start | tokens

    def headers: Parser[List[Header]] = header.*
  }

  class RuleParser(tokens: List[Token]) extends HeaderParsers {
    def right: Parser[(List[Term], JavaCode)] =
      token.* ~ ("{" ~> code <~ "}").? ^^ {
        case ts ~ src =>
          val terms = ts.map {
            case IdentToken(t) =>
              if (tokens.contains(IdentToken(t))) Terminal(IdentToken(t))
              else NonTerminal(t)
            case ConstToken(c) =>
              if (tokens.contains(ConstToken(c))) Terminal(ConstToken(c))
              else throw new Exception("")
          }
          (terms, src.getOrElse(JavaCode("")))
      }

    def rule: Parser[Rule] =
      (ident <~ ":") ~ rep1sep(right, "|") <~ ";".? ^^ {
        case l ~ rs => Rule(NonTerminal(l), rs)
      }

    def rules: Parser[List[Rule]] = "%%" ~> rule.*
  }

  case class ParsingError(msg: String, pos: Position) extends Exception {
    override def getMessage: String = pos match {
      case NoPosition => s"$msg"
      case _ => s"At (${pos.line}, ${pos.column}) $msg:\n${pos.longString}"
    }
  }

  def parse(source: String): Try[Spec] = {
    val p1 = new HeaderParsers
    p1.parse(p1.headers, source) match {
      case p1.Success(headers: List[Header], in) =>
        val tokens = headers.flatMap {
          case Tokens(ts) => ts
          case _ => Nil
        }
        val p2 = new RuleParser(tokens)
        p2.parseAll(p2.rules, in) match {
          case p2.Success(rules: List[Rule], _) => ???
            //Success(Spec(headers, rules))
          case p2.Failure(msg, next) => Failure(ParsingError(msg, next.pos))
          case p2.Error(msg, next) => Failure(ParsingError(msg, next.pos))
        }
      case p1.Failure(msg, next) => Failure(ParsingError(msg, next.pos))
      case p1.Error(msg, next) => Failure(ParsingError(msg, next.pos))
    }
  }
}
