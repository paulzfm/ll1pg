/**
  * Created by paul on 07/04/2017.
  *
  * Context Free Grammar (CFG).
  */

import JavaStmtAST.JavaStmt

object AST {

  /**
    * Headers definition.
    *
    * @param package Java file package.
    * @param imports Java file imports.
    * @param class   Java class.
    * @param tokens  CFG tokens (terminals).
    * @param start   CFG start symbol.
    */
  type Headers = (Option[Package], Imports, Class, List[Token], Start)

  /**
    * The CFG file AST.
    *
    * @param headers headers.
    * @param rules   CFG rules.
    */
  case class Syntax(headers: Headers, rules: List[Rule]) {
    val (pkg, imports, cls, tokens, start) = headers

    def writeTo(writer: IndentWriter): Unit = {
      // package
      pkg match {
        case Some(p) =>
          p.writeTo(writer)
          writer.next()
        case None =>
      }

      // imports
      imports.writeTo(writer)
      writer.next()

      // class begin
      cls.writeTo(writer)
      writer.writeLn("{")
      writer.incIndent()

      // class body

      // class end
      writer.decIndent()
      writer.writeLn("}")
    }
  }

  /**
    * Package of the parser class.
    *
    * @param name package name.
    */
  case class Package(name: String) {
    def writeTo(writer: IndentWriter): Unit = writer.writeLn(s"package $name;")
  }

  /**
    * Dependencies of the parser class.
    *
    * @param dependencies dependent classes.
    */
  case class Imports(dependencies: List[String] = Nil) {
    def writeTo(writer: IndentWriter): Unit =
      dependencies.map("import " + _ + ";").foreach(writer.writeLn)
  }

  /**
    * Parser class.
    *
    * @param name   class name.
    * @param extend class extending (can be `None`).
    */
  case class Class(name: String, extend: Option[String]) {
    def writeTo(writer: IndentWriter): Unit = writer.write(s"public class $name${
      extend match {
        case Some(c) => s" extends $c"
        case None => ""
      }
    }")
  }

  /**
    * Lexer token.
    */
  abstract class Token {
    def writeTo(writer: IndentWriter): Unit
  }

  /**
    * Token declared by an identifier.
    *
    * @param token the identifier.
    */
  case class Ident(token: String) extends Token {
    override def writeTo(writer: IndentWriter): Unit = writer.write(token)

    override def toString: String = token
  }

  /**
    * Token declared by a single character (operator).
    *
    * @param token the character.
    */
  case class Op(token: Char) extends Token {
    override def writeTo(writer: IndentWriter): Unit = writer.write(s"'$token'")

    override def toString: String = s"'$token'"
  }

  /**
    * Start symbol of CFG.
    *
    * @param symbol start non-terminal.
    */
  case class Start(symbol: NonTerminal)

  /**
    * Term of CFG rule.
    */
  abstract class Term {
    def writeNameTo(writer: IndentWriter): Unit

    def writeMatcherTo(writer: IndentWriter): Unit
  }

  /**
    * Terminal token.
    *
    * @param term the token.
    */
  case class Terminal(term: Token) extends Term {
    override def writeNameTo(writer: IndentWriter): Unit = term.writeTo(writer)

    override def writeMatcherTo(writer: IndentWriter): Unit = {
      writer.write(s"matchToken(")
      term.writeTo(writer)
      writer.write(")")
    }
  }

  /**
    * Non-terminal symbol.
    *
    * @param term the symbol name.
    */
  case class NonTerminal(term: String) extends Term {
    override def writeNameTo(writer: IndentWriter): Unit = writer.write(term)

    override def writeMatcherTo(writer: IndentWriter): Unit = {
      writer.write(s"parse$term()")
    }
  }

  /**
    * CFG rule.
    *
    * @param left   left non-terminal.
    * @param rights right terms with action (in format of Java code).
    */
  case class Rule(left: NonTerminal, rights: List[(List[Term], List[String])]) {
    def writeTo(writer: IndentWriter): Unit = {
      // method begins
      writer.write("private SemValue ")
      left.writeMatcherTo(writer)
      writer.writeLn("{")
      writer.incIndent()

      // method body
      if (rights.length == 1) {
        writeBodyTo(writer, rights.head)
      } else {
        writer.write("switch(lookahead) {")
        writer.incIndent()
        rights.foreach {
          xs =>
            writer.writeLn("case ???: {")
            writer.incIndent()
            writeBodyTo(writer, xs)
            writer.decIndent()
            writer.writeLn("}")
        }
        writer.decIndent()
        writer.write("}")
      }

      // method ends
      writer.decIndent()
      writer.writeLn("}")
    }

    def writeBodyTo(writer: IndentWriter, right: (List[Term], List[JavaStmt])): Unit = {
      val (terms, actions) = right
      writer.writeLn(s"SemValue[] params = new SemValue[${terms.length + 1}];")
      terms.zipWithIndex.foreach {
        case (t, i) =>
          writer.write(s"params[$i] = ")
          t.writeMatcherTo(writer)
          writer.writeLn(";")
      }

      actions.map(_.toJavaCode).foreach(writer.writeLn)
      writer.writeLn("return params[0];")
    }
  }

}
