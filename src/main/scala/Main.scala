import java.io.File

import scala.util.{Failure, Success, Try}

/**
  * Entry of parser generator.
  */
object Main {
  def main(args: Array[String]): Unit = {
    // Parse command line option and arguments.
    var specFileName = ""
    var parserFileName = ""
    var strictMode = false

    if (args.length == 0) {
      Console.err.println(s"Usage: java -jar pg.jar [-strict] <spec file> <output file>")
      System.exit(1)
    } else if (args(0) == "-strict") {
      if (args.length >= 3) {
        specFileName = args(1)
        parserFileName = args(2)
        strictMode = true
      } else {
        Console.err.println(s"Usage: java -jar pg.jar [-strict] <spec file> <output file>")
        System.exit(1)
      }
    } else if (args.length == 2) {
      specFileName = args(0)
      parserFileName = args(1)
    } else if (args.length == 3) {
      Console.err.println(s"Invalid option: ${args(0)}")
      System.exit(1)
    } else {
      Console.err.println(s"Usage: java -jar pg.jar [-strict] <spec file> <output file>")
      System.exit(1)
    }

    // Read specification file.
    val inputFile = new File(specFileName)
    val source = scala.io.Source.fromFile(inputFile)
    val lines = try source.mkString finally source.close()

    val outputFile = new File(parserFileName)

    // Parse and generate.
    def parseAndGenerate(source: String): Try[Unit] = for {
      spec <- Parsers.parse(source)
      gen = new Generator(spec, inputFile.getAbsolutePath, strictMode)
      code <- gen.generate
    } yield {
      val writer = new IndentWriter
      code.printTo(writer)
      writer.outputToFile(outputFile)
    }

    parseAndGenerate(lines) match {
      case Success(_) =>
        println("Parser is successfully generated and written to \"" +
          outputFile.getAbsolutePath + "\"")
      case Failure(ex) =>
        Console.err.println(ex.getMessage)
        System.exit(1)
    }
  }

}
