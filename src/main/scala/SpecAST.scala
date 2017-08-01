import scala.util.parsing.input.Positional

/**
  * Specification which illustrating how the syntax of the target language is like.
  * The language syntax is described with a LL1 parser and LL1-parser-gen will automatically
  * generate the corresponding parser implementation in Java.
  */
object SpecAST {

  abstract class Node extends Positional with Printable

  case class Ident(symbol: String) extends Node {
    override def printTo(writer: IndentWriter): Unit = writer.write(symbol)

    override def toString: String = symbol
  }

  case class Const(symbol: Char) extends Node {
    override def printTo(writer: IndentWriter): Unit = {
      writer.write("'")
      writer.write(symbol.toString)
      writer.write("'")
    }

    override def toString: String = s"'${symbol.toString}'"
  }

  case class JavaCode(source: String) extends Node {
    override def printTo(writer: IndentWriter): Unit = writer.writeLn(
      source.trim.replaceAll("[$]([1-9][0-9]*)", "params[$1]")
        .replaceAll("[$]{2}", "params[0]"))
  }

  case class Spec(headers: (Package, Imports, SemValue, Class, Tokens, Start),
                  rules: List[Rule]) extends Node {
    override def printTo(writer: IndentWriter): Unit = {
      //      headers.foreach(_.printTo(writer))
      rules.foreach(_.printTo(writer))
    }

    def pkg: Package = headers._1

    def imports: Imports = headers._2

    def sem: SemValue = headers._3

    def cls: Class = headers._4

    def tokens: List[Token] = headers._5.tokens

    def start: NonTerminal = headers._6.symbol
  }

  def Spec(headers: (Tokens, Start), rules: List[Rule]): Spec = {
    val (tokens, start) = headers
    Spec(
      (Package(Ident("")), Imports(Nil), SemValue(Ident("")), Class(Ident("")),
        tokens, start),
      rules
    )
  }

  abstract class Header extends Node

  case class Package(name: Ident) extends Header {
    override def printTo(writer: IndentWriter): Unit = {
      writer.write("package ")
      name.printTo(writer)
      writer.writeLn(";")
    }
  }

  case class Imports(classes: List[Ident]) extends Header {
    override def printTo(writer: IndentWriter): Unit = classes.foreach {
      cls =>
        writer.write("import ")
        cls.printTo(writer)
        writer.writeLn(";")
    }
  }

  case class SemValue(name: Ident) extends Header {
    override def printTo(writer: IndentWriter): Unit = {
      writer.write("sem ")
      name.printTo(writer)
      writer.writeLn(";")
    }

    override def toString: String = name.toString
  }

  case class ParseError(name: Ident) extends Header {
    override def printTo(writer: IndentWriter): Unit = {
      writer.write("err ")
      name.printTo(writer)
      writer.writeLn(";")
    }

    override def toString: String = name.toString
  }

  case class Class(name: Ident, superClass: Option[Ident] = None,
                   implements: Option[List[Ident]] = None) extends Header {
    override def printTo(writer: IndentWriter): Unit = {
      writer.write("public class ")
      name.printTo(writer)
      superClass match {
        case Some(cls) =>
          writer.write(" extends ")
          cls.printTo(writer)
        case None =>
      }
      implements match {
        case Some(is) =>
          writer.write(" implements ")
          printSep(writer, "", "")(is)
        case None =>
      }
    }
  }

  case class Tokens(tokens: List[Token]) extends Header {
    override def printTo(writer: IndentWriter): Unit = {
      writer.writeLn("tokens = [")
      writer.incIndent()
      val front :+ end = tokens
      front.foreach {
        t =>
          t.printTo(writer)
          writer.writeLn(",")
      }
      end.printTo(writer)
      writer.writeLn()
      writer.decIndent()
      writer.writeLn("];")
    }
  }

  /**
    * Lexer token.
    */
  abstract class Token extends Node {
    def isIdent: Boolean
  }

  /**
    * Token declared by an identifier.
    *
    * @param ident the identifier.
    */
  case class IdentToken(ident: Ident) extends Token {
    override def isIdent: Boolean = true

    def printTo(writer: IndentWriter): Unit = {
      ident.printTo(writer)
    }

    override def toString: String = ident.toString
  }

  def IdentToken(ident: String): IdentToken = IdentToken(Ident(ident))

  /**
    * Token declared by a single character (operator).
    *
    * @param const the character.
    */
  case class ConstToken(const: Const) extends Token {
    override def isIdent: Boolean = false

    def printTo(writer: IndentWriter): Unit = const.printTo(writer)

    override def toString: String = const.toString
  }

  def ConstToken(const: Char): Token = ConstToken(Const(const))

  case class Start(symbol: NonTerminal) extends Header {
    def printTo(writer: IndentWriter): Unit = {
      writer.write("start = ")
      symbol.printTo(writer)
      writer.writeLn(";")
    }
  }

  type Sentence = List[Term]

  /**
    * CFG rule.
    *
    * @param left   left non-terminal.
    * @param rights right terms with action (in format of Java code).
    */
  case class Rule(left: NonTerminal, rights: List[(Sentence, JavaCode)]) extends Node {
    def printTo(writer: IndentWriter): Unit = {
      left.printTo(writer)
      writer.writeLn(":")
      writer.incIndent()
      rights.foreach {
        case (ts, code) =>
          if (ts.isEmpty) writer.write("/* empty */")
          else printSep(writer, "", "", " ")(ts)
          writer.writeLn(" {")
          writer.incIndent()
          code.printTo(writer)
          writer.decIndent()
          writer.writeLn("}")
      }
      writer.decIndent()
    }
  }

  /**
    * Term of CFG rule.
    */
  abstract class Term extends Node {
    def nonTerminal: Boolean
  }

  /**
    * Terminal token.
    *
    * @param token the token.
    */
  case class Terminal(token: Token) extends Term {
    override def nonTerminal: Boolean = false

    override def printTo(writer: IndentWriter): Unit = token.printTo(writer)

    override def toString: String = token.toString
  }

  /**
    * Non-terminal symbol.
    *
    * @param symbol the symbol name.
    */
  case class NonTerminal(symbol: Ident) extends Term {
    override def nonTerminal: Boolean = true

    override def printTo(writer: IndentWriter): Unit = symbol.printTo(writer)

    override def toString: String = symbol.toString
  }

  def NonTerminal(symbol: String): NonTerminal = NonTerminal(Ident(symbol))
}
