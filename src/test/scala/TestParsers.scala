import Parsers._
import SpecAST._
import org.junit.runner.RunWith
import org.scalatest.FunSuite
import org.scalatest.junit.JUnitRunner


@RunWith(classOf[JUnitRunner])
class TestParsers extends FunSuite {
  val p = new Parsers.TokenParser

  test("parse package name: decaf.Location") {
    assert(p.parseAll(p.pkgName, "decaf.Location").get.symbol == "decaf.Location")
  }

  test("parse ident token: VOID") {
    val p = new Parsers.TokenParser
    assert(p.parseAll(p.token, "VOID").get == IdentToken("VOID"))
  }

  test("parse const token: '%'") {
    val p = new Parsers.TokenParser
    assert(p.parseAll(p.token, "'%'").get == ConstToken('%'))
  }

  test("parse package: %package decaf.tree.Tree.*") {
    val p = new Parsers.HeaderParsers
    assert(p.parseAll(p.pkg, "%package decaf.tree.Tree.*").get ==
      Package(Ident("decaf.tree.Tree.*")))
  }

  test("parse imports: %import decaf.tree.Tree.* java.util.*") {
    val p = new Parsers.HeaderParsers
    assert(p.parseAll(p.imports, "%import decaf.tree.Tree.* java.util.*").get ==
      Imports(List(
        Ident("decaf.tree.Tree.*"),
        Ident("java.util.*")
      ))
    )
  }

  test("parse semValue: %sem SemValue") {
    val p = new Parsers.HeaderParsers
    assert(p.parseAll(p.semValue, "%sem SemValue").get == SemValue(Ident("SemValue")))
  }

  test("parse class: %class Parser") {
    val p = new Parsers.HeaderParsers
    assert(p.parseAll(p.cls, "%class Parser").get ==
      Class(Ident("Parser")))
  }

  test("parse start: %start TopLevel") {
    val p = new Parsers.HeaderParsers
    assert(p.parseAll(p.start, "%start TopLevel").get == Start(NonTerminal(Ident("TopLevel"))))
  }

  test("parse tokens: %tokens IF ELSE '+' '-'") {
    val p = new Parsers.HeaderParsers
    assert(p.parseAll(p.tokens, "%tokens IF ELSE '+' '-'").get ==
      Tokens(List(
        IdentToken("IF"),
        IdentToken("ELSE"),
        ConstToken('+'),
        ConstToken('-')
      ))
    )
  }

  test("parse single rule: ite") {
    val tokens = List("IF", "THEN", "ELSE").map(IdentToken)
    val code =
      """ite : IF boolExpr THEN expr ELSE expr
        >      {
        >          $$ = new ITE($2, $4, $6);
        >      }
      """.stripMargin('>')
    val p = new RuleParser(tokens)

    val rule = p.parseAll(p.rule, code).get
    assert(rule.left.symbol.symbol == "ite")
    val (terms, _) :: Nil = rule.rights
    assert(terms == List(
      Terminal(IdentToken("IF")), NonTerminal("boolExpr"),
      Terminal(IdentToken("THEN")), NonTerminal("expr"),
      Terminal(IdentToken("ELSE")), NonTerminal("expr"))
    )
  }

  test("parse multiple rule: boolExpr") {
    val tokens = List("AND", "OR", "NOT").map(IdentToken)
    val code =
      """boolExpr : boolExpr AND boolExpr
        >           {
        >               $$ = new And($1, $3);
        >           }
        >         | boolExpr OR boolExpr
        >           {
        >               $$ = new OR($1, $3);
        >           }
        >         | NOT boolExpr
        >           {
        >               $$ = new Not($2);
        >           }
      """.stripMargin('>')
    val p = new RuleParser(tokens)

    val rule = p.parseAll(p.rule, code).get
    assert(rule.left.symbol.symbol == "boolExpr")
    val (terms1, _) :: (terms2, _) :: (terms3, _) :: Nil = rule.rights
    assert(terms1 == List(
      NonTerminal("boolExpr"), Terminal(IdentToken("AND")), NonTerminal("boolExpr"))
    )
    assert(terms2 == List(
      NonTerminal("boolExpr"), Terminal(IdentToken("OR")), NonTerminal("boolExpr"))
    )
    assert(terms3 == List(
      Terminal(IdentToken("NOT")), NonTerminal("boolExpr"))
    )
  }

  test("parse rule with if Java code: boolExpr") {
    val tokens = List(';').map(ConstToken)
    val code =
      """stmt : simpleStmt ';'
      >       {
      >           if ($$.stmt == null) {
      >               $$.stmt = new Skip;
      >           }
      >       }
    """.stripMargin('>')
    val p = new RuleParser(tokens)

    val rule = p.parseAll(p.rule, code).get
    assert(rule.left.symbol.symbol == "stmt")
    val (terms, _) :: Nil = rule.rights
    assert(terms == List(
      NonTerminal("simpleStmt"), Terminal(ConstToken(';'))
    ))
  }

  test("parse rule with incomplete Java code: boolExpr") {
    val tokens = List(';').map(ConstToken)
    val code =
      """stmt : simpleStmt ';'
      >       {
      >           if ($$.stmt == null) {
      >               $$.stmt = new Skip;
      >       }
    """.stripMargin('>')
    val p = new RuleParser(tokens)

    val r = p.parseAll(p.rule, code)

    assert(r.isEmpty)
    r match {
      case p.Failure(msg, _) => System.err.println(msg)
    }
  }
}