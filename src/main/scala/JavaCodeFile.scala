import java.util.Calendar

import SpecAST._
import Utils._

/**
  * Target parser implemented as a Java public class.
  *
  * The following methods should be implemented in the base class:
  * - `int lex()`: get next token (an integer) from lexer
  *
  * - `T error(String token)`: handle syntax error when EOF or EOS expected, but `token` found
  * - `T error(String token, String expected)`: handle syntax error when `expected` is expected
  * but `token` is found
  * - `T error(String token, String[] acceptable)`: handle syntax error when one of `acceptable`
  * token is acceptable but `token` is found
  * Note that here `T` must be a subtype of `Exception`, presenting the compilation error to be
  * thrown.
  *
  * The parser entry function is:
  * `semValue parse()`
  * If no exception is thrown, then parsing is successful and the parsing result will be returned
  * in type `semValue`.
  *
  * @param pkg      class package. (`package ...` in Java)
  * @param imports  class dependencies. (`import ...` in Java)
  * @param cls      class name as well as heritages. (`class ... extends ... implements` in Java)
  * @param semValue a class to store semantic values, used as the return type for each
  *                 non-terminal parser.
  * @param start    starting symbol of CFG, used as the parser entry.
  * @param tokens   tokens (or terminals), will be obtained from the lexer.
  * @param parsers  all non-terminal parsers.
  * @param specFile input specification file name.
  * @param options  custom options.
  */
class JavaCodeFile(val pkg: Package, val imports: Imports, val cls: Class,
                   val semValue: SemValue, val start: NonTerminal,
                   val tokens: List[Token],
                   val parsers: List[NonTerminalParser],
                   val specFile: String, val options: String) extends Printable {
  val version: String = "1.0"

  override def printTo(writer: IndentWriter): Unit = {
    // Print info.
    printInfoTo(writer)
    writer.writeLn()

    // Print headers.
    writer.writeLn(s"package $pkg;")
    writer.writeLn()
    imports.classes.foreach(x => writer.writeLn(s"import $x;"))
    writer.writeLn()

    // Begin class.
    writer.writeLn(cls.toString)
    writer.writeLn(" {")
    writer.incIndent()

    // Print yy variables.
    writer.writeLn("public static final int eof = -1;")
    writer.writeLn("public static final int eos = 0;")
    writer.writeLn("public int lookahead = -1;")
    writer.writeLn(s"public $semValue val = new $semValue();")
    writer.writeLn()

    // Print tokens.
    writer.writeLn("/* tokens */")
    val identTokens = tokens.filter(_.isIdent)
    identTokens.zipWithIndex.foreach {
      case (t, i) =>
        writer.writeLn(s"public static final int $t = ${257 + i}; //# line ${t.pos.line}")
    }
    writer.writeLn()
    writer.writeLn("/* search token name */")
    writer.writeLn("private String[] tokens = {")
    writer.incIndent()
    identTokens.grouped(5).foreach {
      grp =>
        writer.writeLn(grp.map("\"" + _ + "\"").mkString(", ") + ",")
    }
    writer.decIndent()
    writer.writeLn("};")
    writer.writeLn()

    // Print helper functions.
    printFuncNameTo(writer)
    writer.writeLn()
    printFuncParseTo(writer)
    writer.writeLn()
    printFuncMatchTokenTo(writer)
    writer.writeLn()

    // Print parsers.
    writer.writeLn("/* parsers */")
    parsers.foreach {
      p =>
        p.printTo(writer)
        writer.writeLn()
    }

    // End class.
    writer.decIndent()
    writer.writeLn("}")

    // End of file.
    writer.writeLn("/* end of file */")
  }

  private def printInfoTo(writer: IndentWriter): Unit = {
    writer.writeLn("/* This is auto-generated Parser source by LL1-Parser-Gen.")
    writer.writeLn(s" * Specification file: $specFile")
    writer.writeLn(s" * Options: $options")
    writer.writeLn(s" * Generated at: ${Calendar.getInstance.getTime}")
    writer.writeLn(" * Please do NOT modify it!")
    writer.writeLn(" *")
    writer.writeLn(" * Project repository: https://github.com/paulzfm/LL1-Parser-Gen")
    writer.writeLn(s" * Version: $version")
    writer.writeLn(" * Author: Zhu Fengmin (Paul)")
    writer.writeLn(" */")
  }

  private def printFuncNameTo(writer: IndentWriter): Unit = {
    writer.writeLn("private String name(int token) {")
    writer.incIndent()
    writer.writeLn("if (token >= 0 && token <= 256) {")
    writer.incIndent()
    writer.writeLn("return \"'\" + (char) token + \"'\";")
    writer.decIndent()
    writer.writeLn("} else {")
    writer.incIndent()
    writer.writeLn("return tokens[token - 257];")
    writer.decIndent()
    writer.writeLn("}")
    writer.decIndent()
    writer.writeLn("}")
  }

  private def printFuncParseTo(writer: IndentWriter): Unit = {
    writer.writeLn(s"public $semValue parse() throws Exception {")
    writer.incIndent()
    writer.writeLn("if (lookahead < 0) {")
    writer.incIndent()
    writer.writeLn("lookahead = lex();")
    writer.decIndent()
    writer.writeLn("}")
    writer.writeLn(s"$semValue result = parse$start();")
    writer.writeLn("if (lookahead != eos && lookahead != eof) {")
    writer.incIndent()
    writer.writeLn(s"throw error(name(lookahead));")
    writer.decIndent()
    writer.writeLn("}")
    writer.writeLn("return result;")
    writer.decIndent()
    writer.writeLn("}")
  }

  private def printFuncMatchTokenTo(writer: IndentWriter): Unit = {
    writer.writeLn(s"private $semValue matchToken(int expected) throws Exception {")
    writer.incIndent()
    writer.writeLn(s"$semValue self = val;")
    writer.writeLn("if (lookahead == expected) {")
    writer.incIndent()
    writer.writeLn("lookahead = lex();")
    writer.decIndent()
    writer.writeLn("} else {")
    writer.incIndent()
    writer.writeLn(s"throw error(name(lookahead), name(expected));")
    writer.decIndent()
    writer.writeLn("}")
    writer.writeLn("return self;")
    writer.decIndent()
    writer.writeLn("}")
  }

}
