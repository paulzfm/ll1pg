package decaf.tools.pg

import scala.util.parsing.input.Positional

/**
  * Specification that illustrates how the syntax of the target language is like.
  * The language syntax is described with a LL1 parser and LL1-parser-gen will automatically
  * generate the corresponding parser implementation in Java.
  */
object SpecAST {

  /**
    * AST node with position information.
    */
  abstract class Node extends Positional

  /**
    * Identifier.
    *
    * @param symbol the name of the identifier.
    */
  case class Ident(symbol: String) extends Node {
    override def toString: String = symbol
  }

  /**
    * Constant character.
    *
    * @param symbol the character.
    */
  case class Const(symbol: Char) extends Node {
    override def toString: String = s"'${symbol.toString}'"
  }

  /**
    * User-defined action code.
    *
    * @param source Java source code.
    */
  case class JavaCode(source: String) extends Node {
    // Split source code to non-empty lines.
    val rawLines: List[String] = source
      .replaceAll("[$]([1-9][0-9]*)", "params[$1]")
      .replaceAll("[$]{2}", "params[0]")
      .split("\n")
      .toList
      .filterNot(_.matches("^\\s*$"))

    // Trim extra spaces for each line.
    val lines: List[String] = if (rawLines.isEmpty) Nil
    else {
      val spaces = rawLines.map(
        _.takeWhile(List(' ', '\f', '\r', '\t').contains).length
      ).min // number of spaces to be trimmed
      rawLines.map(_.substring(spaces))
    }

    override def toString: String = lines.mkString("\n")
  }

  /**
    * Complete headers.
    */
  type Headers = (Package, Imports, SemValue, Class, OutputFile, Tokens, Start)

  /**
    * Top level node presenting the whole specification.
    *
    * @param headers headers.
    * @param rules   rules.
    */
  case class Spec(headers: Headers, rules: List[Rule]) extends Node {
    def pkg: Package = headers._1

    def imports: Imports = headers._2

    def sem: SemValue = headers._3

    def cls: Class = headers._4

    def output: OutputFile = headers._5

    def tokens: List[Token] = headers._6.tokens

    def start: NonTerminal = headers._7.symbol
  }

  /**
    * Constructor with minimal headers.
    *
    * @param headers minimal headers.
    * @param rules   rules.
    * @return a specification.
    */
  def Spec(headers: (Tokens, Start), rules: List[Rule]): Spec = {
    val (tokens, start) = headers
    Spec(
      (Package(Ident("")), Imports(Nil), SemValue(Ident("")), Class(Ident(""), ""), OutputFile(Ident("")), tokens, start),
      rules
    )
  }

  /**
    * Header.
    */
  abstract class Header extends Node

  /**
    * Package where the parser situates in.
    *
    * @param name identifier presenting the package name.
    */
  case class Package(name: Ident) extends Header {
    override def toString: String = name.toString
  }

  /**
    * Packages where the parser dependes on.
    *
    * @param classes packages shall be imported.
    */
  case class Imports(classes: List[Ident]) extends Header

  /**
    * Semantic value for the parser.
    *
    * @param name identifier presenting the name of the semantic value type (class).
    */
  case class SemValue(name: Ident) extends Header {
    override def toString: String = name.toString
  }

  /**
    * Class for the parser.
    *
    * @param name class name
    * @param decl class declaration line, e.g. `public class Parser extends BaseParser implements IParser`.
    */
  case class Class(name: Ident, decl: String) extends Header {
    override def toString: String = decl
  }

  /**
    * Output file path for the parser.
    *
    * @param path file name
    */
  case class OutputFile(path: Ident) extends Header {
    override def toString: String = path.toString
  }

  /**
    * Tokens declared as terminals.
    *
    * @param tokens token list.
    */
  case class Tokens(tokens: List[Token]) extends Header {
    override def toString: String = tokens.mkString(", ")
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

    override def toString: String = const.toString
  }

  def ConstToken(const: Char): Token = ConstToken(Const(const))

  /**
    * Start symbol of the CFG.
    *
    * @param symbol an non-terminal presenting the start symbol.
    */
  case class Start(symbol: NonTerminal) extends Header {
    override def toString: String = symbol.toString
  }

  /**
    * A sentence is a sequence of terms.
    */
  type Sentence = List[Term]

  /**
    * CFG rule.
    *
    * @param left   left non-terminal.
    * @param rights right terms with action (in format of Java code).
    */
  case class Rule(left: NonTerminal, rights: List[(Sentence, JavaCode)]) extends Node

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

    override def toString: String = token.toString
  }

  /**
    * Non-terminal symbol.
    *
    * @param symbol the symbol name.
    */
  case class NonTerminal(symbol: Ident) extends Term {
    override def nonTerminal: Boolean = true

    override def toString: String = symbol.toString
  }

  def NonTerminal(symbol: String): NonTerminal = NonTerminal(Ident(symbol))
}
