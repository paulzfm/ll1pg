import AST._
import Utils._

import scala.collection.mutable

/**
  * Created by paul on 18/06/2017.
  */

class Generator(spec: Spec) {
  val ruleMap: Map[NonTerminal, Set[Sentence]] =
    spec.rules.groupBy(_.left).mapValues(_.flatMap(_.rights).unzip._1.toSet)

  def computeFirstSet: Table = {
    val suffix = for {
      Rule(_, rs) <- spec.rules
      (s, _) <- rs
      if s.nonEmpty
      n <- s.indices
    } yield s.drop(n)

    val sentences = spec.tokens.map(t => List(Terminal(t))).toSet ++
      ruleMap.keys.map(List(_)).toSet ++ Set(Nil) ++ suffix.toSet

    val first: Table = new mutable.HashMap[Sentence, Set[A]]
    sentences.foreach {
      case Nil => first.update(Nil, Set(Epsilon))
      case List(Terminal(t)) => first.update(List(Terminal(t)), Set(Term(t)))
      case other => first.update(other, Set())
    }

    var changed = true
    while (changed) {
      changed = false
      first.foreach {
        case (y, s) => y match {
          case List(NonTerminal(sym)) =>
            ruleMap.get(NonTerminal(sym)) match {
              case None =>
              case Some(ss) =>
                val updated = s ++ ss.flatMap(first(_))
                if (updated != s) {
                  first.update(y, updated)
                  changed = true
                }
            }
          case x if x.length >= 2 =>
            val sub = y.takeWhile(i => first(List(i)).contains(Epsilon))
            val updated = sub.flatMap(i => first(List(i))).toSet
            if (sub.length == x.length) {
              if (updated != s) {
                first.update(y, updated)
                changed = true
              }
            } else {
              val updated1 = updated ++ first(List(y(sub.length))) -- Set(Epsilon)
              if (updated1 != s) {
                first.update(y, updated1)
                changed = true
              }
            }
          case _ =>
        }
      }
    }

    first
  }

  def computeFollowSet: Table = {
    val follow: Table = new mutable.HashMap[Sentence, Set[A]]
    val nonTerminals = ruleMap.keys.map(List(_)).toSet - spec.start


    follow
  }

}
