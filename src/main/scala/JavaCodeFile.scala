import java.util.Calendar

import SpecAST._
import Utils._

import scala.util.parsing.input.NoPosition

/**
  * Target predicative table as a Java class.
  *
  * @param pkg      class package. (`package ...` in Java)
  * @param imports  class dependencies. (`import ...` in Java)
  * @param cls      class name as well as heritages. (`class ... extends ... implements` in Java)
  * @param semValue a class to store semantic values, used as the return type for each
  *                 non-terminal parser.
  * @param start    starting symbol of CFG, used as the parser entry.
  * @param tokens   tokens (or terminals), will be obtained from the lexer.
  * @param tables   all non-terminal parsers.
  * @param specFile input specification file name.
  * @param options  custom options.
  */
class JavaCodeFile(val pkg: Package, val imports: Imports, val cls: Class, val semValue: SemValue,
                   val start: NonTerminal, val tokens: List[Token], val symbols: List[NonTerminal],
                   val follow: HashTable, val tables: List[NonTerminalPSTable],
                   val specFile: String, val options: String) extends Printable {
  override def printTo(writer: IndentWriter): Unit = {
    // Print info.
    writer.writeLn("/* This is auto-generated source by LL1-Parser-Gen.")
    writer.writeLn(s" * Specification file: $specFile")
    writer.writeLn(s" * Options: $options")
    writer.writeLn(s" * Generated at: ${Calendar.getInstance.getTime}")
    writer.writeLn(" * Please do NOT modify it!")
    writer.writeLn(" *")
    writer.writeLn(" * Project repository: https://github.com/paulzfm/LL1-Parser-Gen")
    writer.writeLn(s" * Version: special version for decaf-PA1-B")
    writer.writeLn(" * Author: Zhu Fengmin (Paul)")
    writer.writeLn(" */")
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
    writer.writeLn()

    val identTokens = tokens.filter(_.isIdent)
    val nonTerminalStartNumber = 257 + identTokens.length

    // Print tokens and symbols.
    writer.writeLn("/* tokens and symbols */")
    identTokens.zipWithIndex.foreach {
      case (t, i) =>
        writer.writeLn(s"public static final int $t = ${257 + i}; //# line ${t.pos.line}")
    }
    writer.writeLn()

    symbols.zipWithIndex.foreach {
      case (t, i) =>
        writer.writeLn(s"public static final int $t = ${nonTerminalStartNumber + i};")
    }
    writer.writeLn()

    writer.writeLn("/* start symbol */")
    writer.writeLn(s"public final int start = $start;")
    writer.writeLn()

    """/**
      |  * Judge if a symbol (within valid range) is non-terminal.
      |  *
      |  * @param symbol the symbol to be judged.
      |  * @return true if and only if the symbol is non-terminal.
      |  */
    """.stripMargin.split('\n').foreach(writer.writeLn)
    writer.writeLn("public boolean isNonTerminal(int symbol) {")
    writer.incIndent()
    writer.writeLn(s"return symbol >= $nonTerminalStartNumber;")
    writer.decIndent()
    writer.writeLn("}")
    writer.writeLn()

    writer.writeLn("private final String[] allSymbols = {")
    writer.incIndent()
    (identTokens ++ symbols).grouped(5).foreach {
      grp =>
        writer.writeLn(grp.map("\"" + _ + "\"").mkString(", ") + ",")
    }
    writer.decIndent()
    writer.writeLn("};")
    writer.writeLn()

    """/**
      |  * Debugging function (pretty print).
      |  * Get string presentation of some token or symbol.
      |  *
      |  * @param symbol either terminal or non-terminal.
      |  * @return its string presentation.
      |  */
    """.stripMargin.split('\n').foreach(writer.writeLn)
    writer.writeLn("public String name(int symbol) {")
    writer.incIndent()
    writer.writeLn("if (symbol == eof) return \"<eof>\";")
    writer.writeLn("if (symbol == eos) return \"<eos>\";")
    writer.writeLn("if (symbol > 0 && symbol <= 256) return \"'\" + (char) symbol + \"'\";")
    writer.writeLn("return allSymbols[symbol - 257];")
    writer.decIndent()
    writer.writeLn("}")
    writer.writeLn()

    // Print begin tokens.
    writer.writeLn("/* begin lookahead symbols */")
    writer.writeLn("private ArrayList<Set<Integer>> begin = new ArrayList<Set<Integer>>();")

    writer.writeLn("private final Integer[][] beginRaw = {")
    writer.incIndent()
    tables.foreach {
      case NonTerminalPSTable(_, cases) => writer.writeLn(
        s"{${cases.flatMap(_._1).map(_.toJavaCode).mkString(", ")}},")
    }
    writer.decIndent()
    writer.writeLn("};")
    writer.writeLn()

    """/**
      |  * Get begin lookahead tokens for `symbol`.
      |  *
      |  * @param symbol the non-terminal.
      |  * @return its begin lookahead tokens.
      |  */
    """.stripMargin.split('\n').foreach(writer.writeLn)
    writer.writeLn("public Set<Integer> beginSet(int symbol) {")
    writer.incIndent()
    writer.writeLn(s"return begin.get(symbol - $nonTerminalStartNumber);")
    writer.decIndent()
    writer.writeLn("}")
    writer.writeLn()

    // Print follow set.
    writer.writeLn("/* follow set */")
    writer.writeLn("private ArrayList<Set<Integer>> follow = new ArrayList<Set<Integer>>();")

    writer.writeLn("private final Integer[][] followRaw = {")
    writer.incIndent()
    symbols.foreach {
      s =>
        val set = follow(List(s))
        writer.writeLn(s"{${set.toList.map(_.toJavaCode).mkString(", ")}},")
    }
    writer.decIndent()
    writer.writeLn("};")
    writer.writeLn()

    """/**
      |  * Get follow set for `symbol`.
      |  *
      |  * @param symbol the non-terminal.
      |  * @return its follow set.
      |  */
    """.stripMargin.split('\n').foreach(writer.writeLn)
    writer.writeLn("public Set<Integer> followSet(int symbol) {")
    writer.incIndent()
    writer.writeLn(s"return follow.get(symbol - $nonTerminalStartNumber);")
    writer.decIndent()
    writer.writeLn("}")
    writer.writeLn()

    // Print initialize function.
    writer.writeLn(s"public ${cls.name}() {")
    writer.incIndent()
    writer.writeLn(s"for (int i = 0; i < ${follow.keys.toList.length}; i++) {")
    writer.incIndent()
    writer.writeLn("begin.add(new HashSet<>(Arrays.asList(beginRaw[i])));")
    writer.writeLn("follow.add(new HashSet<>(Arrays.asList(followRaw[i])));")
    writer.decIndent()
    writer.writeLn("}")
    writer.decIndent()
    writer.writeLn("}")
    writer.writeLn()

    val javaCodes = tables.flatMap(_.cases.map(_._3))
    val javaCodesWithIndex = javaCodes.zipWithIndex
    val codePosMap = javaCodes.map(_.pos).zipWithIndex.toMap

    // Print query function.
    """/**
      |  * Predictive table `M` query function.
      |  * `query(A, a)` will return the corresponding term `M(A, a)`, i.e., the target production
      |  * for non-terminal `A` when the lookahead token is `a`.
      |  *
      |  * @param nonTerminal   the non-terminal.
      |  * @param lookahead     the lookahead symbol.
      |  * @return a pair `<id, right>` where `right` is the right-hand side of the target
      |  * production `nonTerminal -> right`, and `id` is the corresponding action id. To execute
      |  * such action, call `act(id, params)`.
      |  * If the corresponding term is undefined in the table, `null` will be returned.
      |  */
    """.stripMargin.split('\n').foreach(writer.writeLn)
    writer.writeLn(s"public Map.Entry<Integer, List<Integer>> query(int nonTerminal, int lookahead) {")
    writer.incIndent()
    writer.writeLn("switch (nonTerminal) {")
    writer.incIndent()
    tables.foreach {
      case NonTerminalPSTable(symbol, cases) =>
        writer.writeLn(s"//# line ${symbol.symbol.pos.line}")
        writer.writeLn(s"case $symbol: {")
        writer.incIndent()
        writer.writeLn("switch (lookahead) {")
        writer.incIndent()
        cases.foreach {
          case (ts, s, c) =>
            ts.foreach {
              case Sharp =>
                writer.writeLn(s"case eof:")
                writer.writeLn(s"case eos:")
              case Term(t) => writer.writeLn(s"case $t:")
            }
            writer.incIndent()
            writer.writeLn(s"return new AbstractMap.SimpleEntry<>(${codePosMap(c.pos)}, Arrays.asList" +
              s"(${sentenceToJavaSeq(s)}));")
            writer.decIndent()
        }
        writer.writeLn("default: return null;")
        writer.decIndent()
        writer.writeLn("}")
        writer.decIndent()
        writer.writeLn("}")
    }
    writer.writeLn("default: return null;")
    writer.decIndent()
    writer.writeLn("}")
    writer.decIndent()
    writer.writeLn("}")
    writer.writeLn()

    // Print act function.
    """/**
      |  * Execute some user-defined semantic action on the specification file.
      |  * Note that `$$ = params[0], $1 = params[1], ...`. Nothing will be returned, so please
      |  * do not forget to store the parsed AST result in `params[0]`.
      |  *
      |  * @param id      the action id.
      |  * @param params  parameter array.
      |  */
    """.stripMargin.split('\n').foreach(writer.writeLn)
    writer.writeLn(s"public void act(int id, $semValue[] params) {")
    writer.incIndent()
    writer.writeLn("switch (id) {")
    writer.incIndent()
    javaCodesWithIndex.foreach {
      case (c, i) =>
        writer.writeLn(s"case $i: {")
        writer.incIndent()
        if (c.pos != NoPosition) writer.writeLn(s"//# line ${c.pos.line}")
        c.lines.foreach(writer.writeLn)
        writer.writeLn("return;")
        writer.decIndent()
        writer.writeLn("}")
    }
    writer.decIndent()
    writer.writeLn("}")
    writer.decIndent()
    writer.writeLn("}")

    // End class.
    writer.decIndent()
    writer.writeLn("}")

    // End of file.
    writer.writeLn("/* end of file */")
  }

}
