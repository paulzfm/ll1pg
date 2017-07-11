/**
  * Created by paul on 18/06/2017.
  */

import java.util.Calendar

import AST._
import Utils._

class JavaCodeFile(val pkg: Package, val imports: Imports, val cls: Class,
                   val semValue: SemValue, val start: NonTerminal,
                   val tokens: List[Token],
                   val parsers: List[NonTerminalParser]) extends Printable {
  override def printTo(writer: IndentWriter): Unit = {
    // Print info.
    printInfoTo(writer)
    writer.writeLn()

    // Print headers.
    pkg.printTo(writer)
    writer.writeLn()
    imports.printTo(writer)
    writer.writeLn()

    // Begin class.
    cls.printTo(writer)
    writer.writeLn(" {")
    writer.incIndent()

    // Print yy variables.
    writer.writeLn("public static final int YYEOF = -1;")
    writer.writeLn("public static final int YYEOS = 0;")
    writer.writeLn("public int lookahead = -1;")
    writer.writeLn(s"public $semValue yylval = new $semValue();")
    writer.writeLn()

    // Print tokens.
    writer.writeLn("/* tokens */")
    val identTokens = tokens.filter(_.isIdent)
    identTokens.zipWithIndex.foreach {
      case (t, i) => writer.writeLn(s"public static final int $t = ${257 + i};")
    }
    writer.writeLn()
    writer.writeLn("/* search token name */")
    writer.writeLn("String[] tokens = {")
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
    printFuncYYParseTo(writer)
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
    writer.writeLn(s" * Generated at: ${Calendar.getInstance.getTime}")
    writer.writeLn(" * Please do NOT modify it unless you know what you are doing!")
    writer.writeLn(" *")
    writer.writeLn(" * Project repository: https://github.com/paulzfm/LL1-Parser-Gen")
    writer.writeLn(" * Author: Zhu Fengmin (Paul)")
    writer.writeLn(" */")
  }

  private def printFuncNameTo(writer: IndentWriter): Unit = {
    writer.writeLn("private String name(int token) {")
    writer.incIndent()
    writer.writeLn("if (token >= 0 && token <= 256) {")
    writer.incIndent()
    writer.writeLn("return (char) token + \"\";")
    writer.decIndent()
    writer.writeLn("} else {")
    writer.incIndent()
    writer.writeLn("return tokens[token - 257];")
    writer.decIndent()
    writer.writeLn("}")
    writer.decIndent()
    writer.writeLn("}")
  }

  private def printFuncYYParseTo(writer: IndentWriter): Unit = {
    writer.writeLn("public int yyparse() {")
    writer.incIndent()
    writer.writeLn("if (lookahead < 0) {")
    writer.incIndent()
    writer.writeLn("lookahead = yylex();")
    writer.decIndent()
    writer.writeLn("}")
    writer.writeLn(s"Parse$start();")
    writer.writeLn("if (lookahead != YYEOS && lookahead != YYEOF) {")
    writer.incIndent()
    writer.writeLn("throw new RuntimeException(String.format(\"\\n***Syntax Error: Invalid tokens after:(%d)%s at %s\", lookahead, name(lookahead), yylval.loc));")
    writer.decIndent()
    writer.writeLn("}")
    writer.writeLn("return 0;")
    writer.decIndent()
    writer.writeLn("}")
  }

  private def printFuncMatchTokenTo(writer: IndentWriter): Unit = {
    writer.writeLn(s"public $semValue MatchToken(int expected) {")
    writer.incIndent()
    writer.writeLn(s"$semValue self = yylval;")
    writer.writeLn("if (lookahead == expected) {")
    writer.incIndent()
    writer.writeLn("lookahead = yylex(); // get next token")
    writer.decIndent()
    writer.writeLn("} else {")
    writer.incIndent()
    writer.writeLn("throw new RuntimeException(String.format(\"\\n***Syntax Error: Expecting:(%d)%s at %s, but [%s] given\", expected, name(expected), yylval.loc, name(lookahead)));")
    writer.decIndent()
    writer.writeLn("}")
    writer.writeLn("return self;")
    writer.decIndent()
    writer.writeLn("}")
  }

}
