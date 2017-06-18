import AST._
import Utils._

import scala.collection.mutable

/**
  * Created by paul on 18/06/2017.
  */

class Generator(spec: Spec) {
  val ruleMap: Map[NonTerminal, Set[Sentence]] =
    spec.rules.groupBy(_.left).mapValues(_.flatMap(_.rights).unzip._1.toSet)

  /**
    * Compute first set for all terminals, non-terminals, \epsilon (the empty sentence), and all
    * suffix for production's right-hand side.
    *
    * @return a table presenting first sets.
    */
  def computeFirstSet: Table = {
    // Suffix for production's right-hand side.
    val suffix = for {
      Rule(_, rs) <- spec.rules
      (r, _) <- rs
      if r.nonEmpty
      n <- r.indices
    } yield r.drop(n)

    // All terminals, non-terminals, \epsilon and suffix.
    val sentences = (Nil :: spec.tokens.map(t => List(Terminal(t))) ++
      ruleMap.keys.map(List(_)) ++ suffix).distinct

    val first: Table = new mutable.HashMap[Sentence, Set[A]]
    // Initialize: first(x) = {x} if x is terminal or x is \epsilon.
    //             first(x) = {}  otherwise.
    sentences.foreach {
      case Nil => first.update(Nil, Set(Epsilon))
      case List(Terminal(t)) => first.update(List(Terminal(t)), Set(Term(t)))
      case other => first.update(other, Set())
    }

    var changed = true
    // Loop until no changes.
    while (changed) {
      changed = false
      sentences.foreach {
        y =>
          y match {
            // 1. y = A where A is non-terminal
            case List(NonTerminal(sym)) =>
              ruleMap.get(NonTerminal(sym)) match {
                case None =>
                case Some(ss) =>
                  val updated = first(y) ++ ss.flatMap(first(_))
                  if (updated != first(y)) {
                    // first(A) = first(A) + first(r1) + first(r2) + ... + first(rn)
                    // where A -> r1 | r2 | ... | rn
                    first.update(y, updated)
                    changed = true
                  }
              }
            // 2. y = y1 y2 ... yk where k >= 2
            case x if x.length >= 2 =>
              val sub = y.takeWhile(i => first(List(i)).contains(Epsilon))
              val updated = sub.flatMap(i => first(List(i))).toSet
              if (sub.length == x.length) { // 2.1. For 1 <= j <= k, \epsilon \in first(yj)
                if (updated != first(y)) {
                  // first(y) = first(y1) + first(y2) + ... + first(yn)
                  first.update(y, updated)
                  changed = true
                }
              } else { // 2.2. For 1 <= j <= i - 1, \epsilon \in first(yj),
                // but \epsilon \not\in first(yi)
                val updated1 = updated ++ first(List(y(sub.length))) - Epsilon
                if (updated1 != first(y)) {
                  // first(y) = first(y1) + first(y2) + ... + first(yi-1) + first(yi) - {\epsilon}
                  first.update(y, updated1)
                  changed = true
                }
              }
            case _ => // no changes
          }
      }
    }

    // Return.
    first
  }

  /**
    * Compute follow set for all non-terminals.
    *
    * @param first a table presenting first sets.
    * @return a table presenting follow sets.
    */
  def computeFollowSet(first: Table): Table = {
    val follow: Table = new mutable.HashMap[Sentence, Set[A]]
    // Initialize.
    // 1. follow(S) = {#}
    follow.update(List(spec.start), Set(Sharp))
    // 2. follow(A) = {} where A is NonTerminal, A /= S
    ruleMap.keys.filterNot(_ == spec.start).toList.distinct.
      map(List(_)).foreach(follow.update(_, Set()))

    var changed = true
    // Loop until no changes.
    while (changed) {
      changed = false
      for {
        (l, rs) <- ruleMap
        r <- rs // rule: l -> r
        i <- r.indices
        if r(i).nonTerminal
      } yield {
        val a = List(l)
        val b = List(r(i))
        val beta = r.drop(i + 1)
        // rule: a -> \alpha b \beta
        val updated1 = follow(b) ++ (first(beta) - Epsilon)
        if (updated1 != follow(b)) {
          // follow(b) = follow(b) + (first(\beta) – {\epsilon})
          follow.update(b, updated1)
          changed = true
        }
        if (first(beta).contains(Epsilon)) { // \epsilon \in first(\beta)
          val updated2 = follow(b) ++ follow(a)
          if (updated2 != follow(b)) {
            // follow(b) = follow(b) + follow(a)
            follow.update(b, updated2)
            changed = true
          }
        }
      }
    }

    // Return.
    follow
  }

}
