/**
  * Created by paul on 07/04/2017.
  */

import scala.util.parsing.combinator.RegexParsers
import AST._

import scala.util.{Failure, Success, Try}

object Parsers {

  class TokenParser extends RegexParsers {
    def ident: Parser[String] =
      """[_A-Za-z][_A-Za-z0-9]*""".r

    def char: Parser[Char] = "'" ~> """.""".r <~ "'" ^^ {
      _.head
    }

    def token: Parser[Token] = ident ^^ Ident | char ^^ Op

    def line: Parser[String] = ".*"

    def pkgName: Parser[String] = """[a-zA-Z0-9_.*]+""".r

    // to ignore Java-style comments
    protected override val whiteSpace =
      """(\s|//.*|(?m)/\*(\*(?!/)|[^*])*\*/)+""".r
  }

  class HeadersParser extends TokenParser {
    def pkg: Parser[Package] = "%package" ~> pkgName ^^ Package

    def imports: Parser[Imports] = "%import" ~> pkgName.* ^^ Imports

    def cls: Parser[Class] = "%class" ~> ident ~ ("extends" ~> ident).? ^^ {
      case c ~ e => Class(c, e)
    }

    def tokens: Parser[List[Token]] = "%tokens" ~> token.*

    def start: Parser[Start] = "%start" ~> ident ^^ (NonTerminal andThen Start)

    def headers: Parser[Headers] =
      pkg.? ~ imports ~ cls ~ tokens ~ start ^^ {
        case p ~ i ~ c ~ t ~ s => (p, i, c, t, s)
      }
  }

  class RulesParser(tokens: List[Token]) extends HeadersParser {
    def right: Parser[(List[Term], List[String])] =
      token.* ~ ("{" ~> line.* <~ "}") ^^ {
        case ts ~ ls =>
          val terms = ts.map {
            case Ident(t) =>
              if (tokens.contains(Ident(t))) Terminal(Ident(t))
              else NonTerminal(t)
            case Op(c) =>
              if (tokens.contains(Op(c))) Terminal(Op(c))
              else throw new Exception("")
          }
          (terms, ls)
      }

    def rule: Parser[Rule] =
      (ident <~ ":") ~ rep1sep(right, "|") <~ ";" ^^ {
        case l ~ rs => Rule(NonTerminal(l), rs)
      }

    def rules: Parser[List[Rule]] = "%rules" ~> rule.*
  }

  case class ParsingError(msg: String) extends Exception {
    override def getMessage: String = msg
  }

  def parse(source: String): Try[Syntax] = {
    val p1 = new HeadersParser
    p1.parse(p1.headers, source) match {
      case p1.Success(headers: Headers, in) =>
        val tokens = headers._4
        val p2 = new RulesParser(tokens)
        p2.parseAll(p2.rules, in) match {
          case p2.Success(rules: List[Rule], _) => Success(Syntax(headers, rules))
          case p2.Failure(msg, next) => Failure(ParsingError(msg + ":\n" + next.pos.longString))
          case p2.Error(msg, next) => Failure(ParsingError(msg + ":\n" + next.pos.longString))
        }
      case p1.Failure(msg, next) => Failure(ParsingError(msg + ":\n" + next.pos.longString))
      case p1.Error(msg, next) => Failure(ParsingError(msg + ":\n" + next.pos.longString))
    }
  }

}
