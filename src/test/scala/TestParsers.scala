import AST._
import org.junit.runner.RunWith
import org.scalatest.FunSuite
import org.scalatest.junit.JUnitRunner
import Parsers._

import scala.util.{Failure, Success}


@RunWith(classOf[JUnitRunner])
class TestParsers extends FunSuite {
  test("parse decaf.Location") {
    val p = new Parsers.TokenParser
    assert(p.parseAll(p.pkgName, "decaf.Location").get == "decaf.Location")
  }

  test("parse decaf.tree.Tree.*") {
    val p = new Parsers.TokenParser
    assert(p.parseAll(p.pkgName, "decaf.tree.Tree.*").get == "decaf.tree.Tree.*")
  }

  test("parse VOID") {
    val p = new Parsers.TokenParser
    assert(p.parseAll(p.token, "VOID").get == Ident("VOID"))
  }

  test("parse '%'") {
    val p = new Parsers.TokenParser
    assert(p.parseAll(p.token, "'%'").get == Op('%'))
  }

  test("parse") {
    val f = scala.io.Source.fromFile("/Users/paul/Workspace/decaf_PA1_B/src/decaf/frontend/decaf" +
      ".pg.out")
    val src = try f.mkString finally f.close()
    parse(src) match {
      case Success(syntax) =>
        val writer = new IndentWriter
        syntax.writeTo(writer)
        writer.printToConsole()
      case Failure(ex) => ex.printStackTrace()
    }
  }
}