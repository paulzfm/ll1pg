package decaf.tools.ll1pg

import decaf.tools.ll1pg.SpecAST._
import decaf.tools.ll1pg.Utils._

import scala.collection.mutable

/**
  * Parser generator from the specification defined with `AST`.
  * This generator implements the algorithms to compute First Set, Follow Set and Predictive Set.
  * Parsers will be constructed according to the Predictive Set.
  *
  * @param spec specification illustrating the CFG.
  * @param file specification file name.
  */
class Generator(spec: Spec, file: String = "<string>") {
  /**
    * Map non-terminals to their corresponding rules.
    */
  val rulesMap: Map[NonTerminal, List[Rule]] = spec.rules.groupBy(_.left)

  /**
    * Map non-terminals to their corresponding sentences (right-hand sides).
    */
  val sentencesMap: Map[NonTerminal, List[Sentence]] =
    rulesMap.mapValues(_.flatMap(_.rights).unzip._1)

  /**
    * Compute first set for all terminals, non-terminals, \epsilon (the empty sentence), and all
    * suffix for production's right-hand side.
    *
    * @return a table presenting first sets.
    */
  def computeFirstSet: HashTable = {
    // Suffix for production's right-hand side.
    val suffix = for {
      Rule(_, rs) <- spec.rules
      (r, _) <- rs
      if r.nonEmpty
      n <- r.indices
    } yield r.drop(n)

    // All terminals, non-terminals, \epsilon and suffix.
    val sentences = (Nil :: spec.tokens.map(t => List(Terminal(t))) ++
      sentencesMap.keys.map(List(_)) ++ suffix).distinct

    val first: HashTable = new mutable.HashMap[Sentence, Set[LASym]]
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
              sentencesMap.get(NonTerminal(sym)) match {
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
  def computeFollowSet(first: HashTable): HashTable = {
    val follow: HashTable = new mutable.HashMap[Sentence, Set[LASym]]
    // Initialize.
    // 1. follow(S) = {#}
    follow.update(List(spec.start), Set(Sharp))
    // 2. follow(A) = {} where A is NonTerminal, A /= S
    sentencesMap.keys.filterNot(_ == spec.start).toList.distinct.
      map(List(_)).foreach(follow.update(_, Set()))

    var changed = true
    // Loop until no changes.
    while (changed) {
      changed = false
      for {
        (l, rs) <- sentencesMap
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

  /**
    * Compute predictive set (PS) for all productions.
    *
    * @param first  a table presenting first sets.
    * @param follow a table presenting follow sets.
    * @return a list presenting predictive sets:
    *         e.g. item (A, table(a1 -> s1, a2 -> s2)) means PS(A -> a1) = s1 and PS(A -> a2) = s2.
    */
  def computePredictiveSet(first: HashTable, follow: HashTable): PS =
    sentencesMap.map {
      case (l, rs) =>
        val a = List(l)
        val ps = rs.map {
          alpha =>
            if (first(alpha).contains(Epsilon)) { // \epsilon \in first(\alpha)
              // PS(a -> \alpha) = (first(\alpha) – {\epsilon}) + follow(a)
              (alpha, first(alpha) - Epsilon ++ follow(a))
            } else { // \epsilon \not\in first(\alpha)
              // PS(a -> \alpha) = first(\alpha)
              (alpha, first(alpha))
            }
        }
        (l, ps)
    }.toList

  // Helper functions to pretty print collections.
  def listToString[T](l: List[T]): String =
    if (l.isEmpty) "<empty>"
    else l.mkString(" ")

  def setToString[T](l: Set[T]): String = s"{${l.mkString(", ")}}"

  /**
    * Check whether the grammar is a LL(1) grammar.
    *
    * @param ps a list presenting predictive sets.
    * @return - `None` if the grammar is a LL(1) grammar.
    *         - `Some((A, a1, s1, a2, s2))` if the grammar is not a LL(1) grammar, and an counter
    *         example is given as PS(A -> a1) = s1, PS(A -> a2) = s2, but s1 & s2 is non empty.
    */
  def checkLL1(ps: PS): Option[(NonTerminal, Sentence, Set[LASym], Sentence, Set[LASym])] =
    if (ps.isEmpty) None
    else {
      val (l, t) :: ts = ps
      val results = for {
        (x, tx) <- t
        (y, ty) <- t
        if x != y
        z = tx & ty
      } yield (x, y, z.nonEmpty, tx, ty)
      results.find(_._3) match {
        case None => checkLL1(ts)
        case Some((x, y, _, tx, ty)) => Some((l, x, tx, y, ty))
      }
    }

  /**
    * If the given grammar is not LL(1), we reduce conflicts by specifying the precedence of the
    * production being used.
    *
    * For example, grammar G[S]:
    * {{{
    *   S -> if C t S E
    *   E -> e S | `epsilon`
    * }}}
    * is not LL(1), because PS(E -> e S) & PS(E -> `epsilon`) = {E} is non-empty.
    * By always selecting `E -> e S` when `E` is the lookahead symbol, we solve the conflicts
    * and substitute the new predictive set PS'(E -> `epsilon`) by PS(E -> `epsilon`) - {E},
    * where now PS(E -> e S) & PS'(E -> `epsilon`) is empty.
    *
    * @param ps current predictive sets with potential conflicts.
    * @return the transformed predictive sets without any conflict.
    */
  def transformLL1(ps: PS): PS = {
    def deconflict(table: Table): Table = {
      def iterate(table: Table, acc: Table, appeared: Set[LASym]): Table = table match {
        case Nil => acc
        case (s, symbols) :: ts => iterate(ts, acc :+ (s, symbols -- appeared), appeared ++ symbols)
      }

      iterate(table, Nil, Set())
    }

    ps.map {
      case (l, t) =>
        val t1 = deconflict(t)
        if (t1 != t) {
          Console.err.println(s"Warning: conflict productions at line ${l.symbol.pos.line}:")
          for {
            (s, _) <- t
          } yield Console.err.println(s"$l -> ${listToString(s)}")
        }
        (l, t1)
    }
  }

  /**
    * Generate target LL table.
    *
    * @return Java LL table.
    */
  def generateTable: JavaTable = {
    val first = computeFirstSet
    val follow = computeFollowSet(first)
    val ps = computePredictiveSet(first, follow)
    val ps1 = transformLL1(ps)

    val parsers = ps1.map {
      case (nt, tb) =>
        val codeMap = rulesMap(nt).flatMap(_.rights).toMap
        val cases = tb.flatMap {
          case (s, terms) => Some((terms.toList, s, codeMap(s)))
        }
        NonTerminalPSTable(nt, cases)
    }
    new JavaTable(spec.pkg, spec.imports, spec.cls, spec.sem, spec.start, spec.tokens, rulesMap.keys.toList, follow, parsers, file)
  }

  /**
    * Generate target LL parser.
    *
    * @return Java parser.
    */
  def generateParser: JavaParser = {
    val first = computeFirstSet
    val follow = computeFollowSet(first)
    val ps = computePredictiveSet(first, follow)
    val ps1 = transformLL1(ps)

    val parsers = ps1.map {
      case (nt, tb) =>
        val codeMap = rulesMap(nt).flatMap(_.rights).toMap
        val cases = tb.map {
          case (s, terms) => (terms.toList, s, codeMap(s))
        }
        NonTerminalParser(spec.sem, nt, cases)
    }
    new JavaParser(spec.pkg, spec.imports, spec.cls, spec.sem, spec.start, spec.tokens, parsers, file)
  }
}
