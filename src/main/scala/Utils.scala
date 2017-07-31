import SpecAST._

import scala.collection.mutable

/**
  * Utilities.
  */
object Utils {

  /**
    * Lookahead symbol.
    */
  abstract class LASym

  /**
    * The symbol is a lexer `Token`.
    *
    * @param token the lexer token.
    */
  case class Term(token: Token) extends LASym {
    override def toString: String = token.toString
  }

  /**
    * Empty symbol `epsilon`.
    */
  case object Epsilon extends LASym {
    override def toString: String = "."
  }

  /**
    * End symbol `#`.
    */
  case object Sharp extends LASym {
    override def toString: String = "'#'"
  }

  /**
    * A map to store first and follow sets of sentences.
    */
  type Table = mutable.HashMap[Sentence, Set[LASym]]

  /**
    * A list to store predictive sets.
    */
  type PS = List[(NonTerminal, Table)]

  /**
    * Parser for non-terminal implemented as a Java method `parse$symbol`.
    *
    * @param semValue a class to store semantic values, used as the return type for the parser.
    * @param parseErr a class to store compile error information.
    * @param symbol   non-terminal symbol.
    * @param cases    list of cases, and a case is a tuple `(as, s, c)` where
    *                 - `as` is a list of lookahead symbols this case accepts;
    *                 - `s` is the right-hand side sentence of production `symbol -> s`;
    *                 - `c` is the Java code describing actions to do after parsing with this rule.
    */
  case class NonTerminalParser(semValue: SemValue, parseErr: ParseError, symbol: NonTerminal,
                               cases: List[(List[LASym], Sentence, JavaCode)]) extends Printable {
    override def printTo(writer: IndentWriter): Unit = {
      writer.writeLn(s"private $semValue parse$symbol() throws $parseErr {")
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
          writer.writeLn("{")
          writer.incIndent()
          writer.writeLn(s"$semValue[] params = new $semValue[${s.length + 1}];")
          writer.writeLn(s"params[0] = new $semValue();")
          s.zipWithIndex.foreach {
            case (Terminal(t), i) =>
              writer.writeLn(s"params[${i + 1}] = matchToken($t);")
            case (NonTerminal(t), i) =>
              writer.writeLn(s"params[${i + 1}] = parse$t();")
          }
          c.printTo(writer)
          writer.writeLn("return params[0];")
          writer.decIndent()
          writer.writeLn("}")
      }
      writer.writeLn("default:")
      writer.writeLn("{")
      writer.incIndent()
      writer.writeLn(s"String[] acc = {${
        cases.flatMap {
          case (ts, _, _) => ts
        }.map("name(" + _ + ")").mkString(", ")
      }};")
      writer.writeLn(s"throw error(name(lookahead), acc);")
      writer.decIndent()
      writer.writeLn("}")
      writer.decIndent()
      writer.writeLn("}")
      writer.decIndent()
      writer.writeLn("}")
    }
  }

}
