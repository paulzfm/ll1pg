package decaf.tools.ll1pg

import decaf.tools.ll1pg.SpecAST._

import scala.collection.mutable
import scala.util.parsing.input.NoPosition

/**
  * Utilities.
  */
object Utils {

  /**
    * Lookahead symbol.
    */
  abstract class LASym {
    def toJavaCode: String = toString
  }

  /**
    * The symbol is a lexer `Token`.
    *
    * @param token the lexer token.
    */
  case class Term(token: Token) extends LASym {
    override def toString: String = token.toString

    override def toJavaCode: String = token match {
      case ConstToken(c) => s"Integer.valueOf($c)"
      case other => other.toString
    }
  }

  /**
    * Empty symbol `epsilon`.
    */
  case object Epsilon extends LASym {
    override def toString: String = "<empty>"
  }

  /**
    * End symbol `#`.
    */
  case object Sharp extends LASym {
    override def toString: String = "<#>"

    override def toJavaCode: String = "eof"
  }

  /**
    * A map to store first and follow sets of sentences.
    */
  type HashTable = mutable.HashMap[Sentence, Set[LASym]]

  /**
    * A list to store lookahead symbols for each sentence.
    */
  type Table = List[(Sentence, Set[LASym])]

  /**
    * A list to store predictive sets.
    */
  type PS = List[(NonTerminal, Table)]

  /**
    * Sentence to Java sequence.
    *
    * @param s sentence.
    * @return its Java sequence presentation.
    */
  def sentenceToJavaSeq(s: Sentence): String = s.map {
    case Terminal(ConstToken(c)) => s"Integer.valueOf($c)"
    case other => other.toString
  }.mkString(", ")

  /**
    * Predictive table for non-terminals.
    *
    * @param symbol   non-terminal symbol.
    * @param cases    list of cases, and a case is a tuple `(as, s, c)` where
    *                 - `as` is a list of lookahead symbols this case accepts;
    *                 - `s` is the right-hand side sentence of production `symbol -> s`;
    *                 - `c` is the Java code describing actions to do after parsing with this rule.
    */
  case class NonTerminalPSTable(symbol: NonTerminal,
                                cases: List[(List[LASym], Sentence, JavaCode)])

  /**
    * Parser for non-terminal implemented as a Java method `parse$symbol`.
    *
    * @param semValue a class to store semantic values, used as the return type for the parser.
    * @param symbol   non-terminal symbol.
    * @param cases    list of cases, and a case is a tuple `(as, s, c)` where
    *                 - `as` is a list of lookahead symbols this case accepts;
    *                 - `s` is the right-hand side sentence of production `symbol -> s`;
    *                 - `c` is the Java code describing actions to do after parsing with this rule.
    */
  case class NonTerminalParser(semValue: SemValue, symbol: NonTerminal,
                               cases: List[(List[LASym], Sentence, JavaCode)]) extends Printable {
    override def printTo(writer: IndentWriter): Unit = {
      writer.writeLn(s"//# line ${symbol.symbol.pos.line}")
      writer.writeLn(s"private $semValue parse$symbol() throws Exception {")
      writer.incIndent()
      writer.writeLn("switch (lookahead) {")
      writer.incIndent()
      cases.foreach {
        case (ts, s, c) =>
          ts.foreach {
            case Sharp =>
              writer.writeLn(s"case eof:")
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
          if (c.pos != NoPosition) writer.writeLn(s"//# line ${c.pos.line}")
          c.lines.foreach(writer.writeLn)
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
        }.map("name(" + _.toJavaCode + ")").mkString(", ")
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
