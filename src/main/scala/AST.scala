/**
  * Created by paul on 07/04/2017.
  *
  * Context Free Grammar (CFG).
  */

import scala.util.parsing.input.Positional

object AST {

  abstract class Node extends Positional with Printable

  case class Ident(symbol: String) extends Node {
    override def printTo(writer: IndentWriter): Unit = writer.write(symbol)
  }

  case class Const(symbol: Char) extends Node {
    override def printTo(writer: IndentWriter): Unit = writer.write(symbol.toString)
  }

  case class JavaCode(source: String) extends Node {
    override def printTo(writer: IndentWriter): Unit = writer.writeLn(source.trim)
  }

  case class Spec(headers: List[Header], rules: List[Rule]) extends Node {
    override def printTo(writer: IndentWriter): Unit = {
      headers.foreach(_.printTo(writer))
      rules.foreach(_.printTo(writer))
    }
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

  case class Class(name: Ident, superClass: Option[Ident] = None,
                   implements: Option[List[Ident]] = None) extends Header {
    override def printTo(writer: IndentWriter): Unit = {
      writer.write("class ")
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
      writer.writeLn(";")
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
  abstract class Token extends Node

  /**
    * Token declared by an identifier.
    *
    * @param ident the identifier.
    */
  case class IdentToken(ident: Ident) extends Token {
    def printTo(writer: IndentWriter): Unit = ident.printTo(writer)
  }

  def IdentToken(ident: String): IdentToken = IdentToken(Ident(ident))

  /**
    * Token declared by a single character (operator).
    *
    * @param const the character.
    */
  case class ConstToken(const: Const) extends Token {
    def printTo(writer: IndentWriter): Unit = const.printTo(writer)
  }

  def ConstToken(const: Char): Token = ConstToken(Const(const))

  case class Start(symbol: NonTerminal) extends Header {
    def printTo(writer: IndentWriter): Unit = {
      writer.write("start = ")
      symbol.printTo(writer)
      writer.writeLn(";")
    }
  }

  /**
    * CFG rule.
    *
    * @param left   left non-terminal.
    * @param rights right terms with action (in format of Java code).
    */
  case class Rule(left: NonTerminal, rights: List[(List[Term], JavaCode)]) extends Node {
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
  abstract class Term extends Node

  /**
    * Terminal token.
    *
    * @param token the token.
    */
  case class Terminal(token: Token) extends Term {
    override def printTo(writer: IndentWriter): Unit = token.printTo(writer)
  }

  /**
    * Non-terminal symbol.
    *
    * @param symbol the symbol name.
    */
  case class NonTerminal(symbol: Ident) extends Term {
    override def printTo(writer: IndentWriter): Unit = symbol.printTo(writer)
  }

  def NonTerminal(symbol: String): Term = NonTerminal(Ident(symbol))
}
