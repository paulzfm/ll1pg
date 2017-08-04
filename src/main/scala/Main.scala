import Parsers.ParsingError

import scala.util.{Failure, Success}

/**
  * Entry of parser generator.
  */
object Main {
  def main(args: Array[String]): Unit = {
    var specFile = ""
    var outputFile = ""
    var strict = false

    if (args(0) == "-strict") {
      if (args.length >= 3) {
        specFile = args(1)
        outputFile = args(2)
        strict = true
      } else {
        Console.err.println(s"Usage: java -jar pg.jar [-strict] <spec file> <output file>")
        System.exit(1)
      }
    } else if (args.length == 2) {
      specFile = args(0)
      outputFile = args(1)
    } else if (args.length == 3) {
      Console.err.println(s"Invalid option: ${args(0)}")
      System.exit(1)
    } else {
      Console.err.println(s"Usage: java -jar pg.jar [-strict] <spec file> <output file>")
      System.exit(1)
    }

    val source = scala.io.Source.fromFile(specFile)
    val lines = try source.mkString finally source.close()

    Parsers.parse(lines) match {
      case Success(spec) =>
        val gen = new Generator(spec, strict)
        try {
          val code = gen.generate
          val writer = new IndentWriter
          code.printTo(writer)
          writer.outputToFile(outputFile)
        } catch {
          case ex: ParsingError => Console.err.println(ex.getMessage)
          case ex => ex.printStackTrace()
        }
      case Failure(ex) => Console.err.println(ex.getMessage)
    }

  }

}
