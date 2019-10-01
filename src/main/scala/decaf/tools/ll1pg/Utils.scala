package decaf.tools.ll1pg

import decaf.tools.ll1pg.SpecAST._

import scala.collection.mutable

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

    override def toJavaCode: String = "eof, eos"
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

}
