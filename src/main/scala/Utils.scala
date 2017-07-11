import AST._

import scala.collection.mutable

/**
  * Utilities.
  */
object Utils {

  abstract class A

  case class Term(token: Token) extends A {
    override def toString: String = token.toString
  }

  case object Epsilon extends A

  case object Sharp extends A

  type Table = mutable.HashMap[Sentence, Set[A]]

  type PS = List[(NonTerminal, Table)]

  case class NonTerminalParser(semValue: SemValue, symbol: NonTerminal,
                               cases: List[(List[A], Sentence, JavaCode)]) extends Printable {
    override def printTo(writer: IndentWriter): Unit = {
      writer.writeLn(s"private $semValue Parse$symbol() {")
      writer.incIndent()
      writer.writeLn("switch (lookahead) {")
      writer.incIndent()
      cases.foreach {
        case (ts, s, c) =>
          ts.foreach {
            case Sharp =>
              writer.writeLn(s"case YYEOF:")
              writer.writeLn(s"case YYEOS:")
            case Term(t) => writer.writeLn(s"case $t:")
          }
          writer.writeLn("{")
          writer.incIndent()
          writer.writeLn(s"SemValue[] params = new SemValue[${s.length + 1}];")
          writer.writeLn(s"params[0] = new SemValue();")
          s.zipWithIndex.foreach {
            case (Terminal(t), i) =>
              writer.writeLn(s"params[${i + 1}] = MatchToken($t);")
            case (NonTerminal(t), i) =>
              writer.writeLn(s"params[${i + 1}] = Parse$t();")
          }
          c.printTo(writer)
          writer.writeLn("return params[0];")
          writer.decIndent()
          writer.writeLn("}")
      }
      writer.writeLn("default:")
      // TODO: fix error handler
      writer.writeLn("throw new RuntimeException(\"invalid lookahead: \" + lookahead);")
      writer.decIndent()
      writer.writeLn("}")
      writer.decIndent()
      writer.writeLn("}")
    }
  }

}
