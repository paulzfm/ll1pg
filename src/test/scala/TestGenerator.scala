import AST._
import Utils._
import org.junit.runner.RunWith
import org.scalatest.FunSuite
import org.scalatest.junit.JUnitRunner


@RunWith(classOf[JUnitRunner])
class TestGenerator extends FunSuite {
  def strToSentence(str: String): Sentence = str.map {
    case c if c.isLower => Terminal(IdentToken(c.toString))
    case c if c.isUpper => NonTerminal(c.toString)
  }.toList

  def createRule(left: String, rights: List[String]): Rule = Rule(NonTerminal(left), rights.map {
    s => (strToSentence(s), JavaCode(""))
  })

  def createHeaders(tokens: String, start: String): (Tokens, Start) =
    (Tokens(tokens.map(i => IdentToken(i.toString)).toList), Start(NonTerminal(start)))

  def checkTable(expected: List[(String, List[String])], table: Table): Unit = {
    expected.foreach {
      case (s1, s2) =>
        val sentence = strToSentence(s1)
        val ans = table(sentence).map {
          case Epsilon => " "
          case Sharp => "#"
          case Term(IdentToken(Ident(s))) => s
        }
        assert(ans.toList.sorted == s2.sorted)
    }
  }

  {

    /* Grammar:
      S -> AB
      A -> Da|
      B -> cC
      C -> aADC|
      D -> b|
     */
    val spec = Spec(createHeaders("abc", "S"), List(
      createRule("S", List("AB")),
      createRule("A", List("Da", "")),
      createRule("B", List("cC")),
      createRule("C", List("aADC", "")),
      createRule("D", List("b", ""))
    ))

    val gen = new Generator(spec)
    val first = gen.computeFirstSet
    val follow = gen.computeFollowSet(first)

    test("example 1: compute first") {
      checkTable(List(
        ("", List(" ")),
        ("S", List("b", "a", "c")),
        ("A", List(" ", "b", "a")),
        ("B", List("c")),
        ("C", List(" ", "a")),
        ("D", List(" ", "b")),
        ("a", List("a")),
        ("b", List("b")),
        ("c", List("c")),
        ("AB", List("b", "a", "c")),
        ("Da", List("b", "a")),
        ("cC", List("c")),
        ("aADC", List("a")),
        ("ADC", List(" ", "b", "a")),
        ("DC", List(" ", "b", "a"))
      ), first)
    }

    test("example 1: compute follow") {
      checkTable(List(
        ("S", List("#")),
        ("A", List("c", "b", "a", "#")),
        ("B", List("#")),
        ("C", List("#")),
        ("D", List("a", "#"))
      ), follow)
    }

    test("example 1: check LL1") {

    }
  }

}