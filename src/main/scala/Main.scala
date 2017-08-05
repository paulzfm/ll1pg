import scala.util.{Failure, Success, Try}

/**
  * Entry of parser generator.
  */
object Main {
  def main(args: Array[String]): Unit = {
    // Parse command line option and arguments.
    var specFile = ""
    var outputFile = ""
    var strictMode = false

    if (args(0) == "-strict") {
      if (args.length >= 3) {
        specFile = args(1)
        outputFile = args(2)
        strictMode = true
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

    // Read specification file.
    val source = scala.io.Source.fromFile(specFile)
    val lines = try source.mkString finally source.close()

    // Parse and generate.
    def parseAndGenerate(source: String): Try[Unit] = for {
      spec <- Parsers.parse(source)
      gen = new Generator(spec, strictMode)
      code <- gen.generate
    } yield {
      val writer = new IndentWriter
      code.printTo(writer)
      writer.outputToFile(outputFile)
    }

    parseAndGenerate(lines) match {
      case Success(_) =>
        println(s"""Parser is successfully generated and written to "$outputFile".""")
      case Failure(ex) =>
        Console.err.println(ex.getMessage)
        System.exit(1)
    }
  }

}
