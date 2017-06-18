/**
  * Created by paul on 18/06/2017.
  */

import AST._
import scala.collection.mutable

object Utils {

  abstract class A

  case class Term(token: Token) extends A {
    override def toString: String = token.toString
  }

  case object Epsilon extends A

  case object Sharp extends A

  type Table = mutable.HashMap[Sentence, Set[A]]

  type PS = List[(NonTerminal, Table)]

  case class NonTerminalParser(symbol: NonTerminal,
                               cases: List[(List[A], Sentence, JavaCode)])

}
