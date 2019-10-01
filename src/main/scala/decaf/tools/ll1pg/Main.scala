package decaf.tools.ll1pg

import java.io.File
import java.nio.file.Paths

import scala.util.{Failure, Success, Try}

/**
  * Entry of parser generator.
  */
object Main {
  def main(args: Array[String]): Unit = {
    // Parse command line option and arguments.
    var specFileName = ""
    var outputDir = ""
    var strictMode = false

    val usage = "Usage: java -jar ll1-pg.jar [-strict] <spec file> <output dir>"

    if (args.length == 0) {
      Console.err.println(usage)
      System.exit(1)
    } else if (args(0) == "-strict") {
      if (args.length >= 3) {
        specFileName = args(1)
        outputDir = args(2)
        strictMode = true
      } else {
        Console.err.println(usage)
        System.exit(1)
      }
    } else if (args.length == 2) {
      specFileName = args(0)
      outputDir = args(1)
    } else if (args.length == 3) {
      Console.err.println(s"Invalid option: ${args(0)}")
      System.exit(1)
    } else {
      Console.err.println(usage)
      System.exit(1)
    }

    // Read specification file.
    val inputFile = new File(specFileName)
    val source = scala.io.Source.fromFile(inputFile)
    val lines = try source.mkString finally source.close()

    // Parse and generate.
    def parseAndGenerate(source: String): Try[String] = for {
      spec <- Parsers.parse(source)
      gen = new Generator(spec, inputFile.getAbsolutePath, strictMode)
      code <- gen.generate
    } yield {
      val writer = new IndentWriter
      code.printTo(writer)
      val outputFile = Paths.get(outputDir).resolve(spec.output.path.symbol).toFile
      writer.outputToFile(outputFile)
      outputFile.toString
    }

    parseAndGenerate(lines) match {
      case Success(outputFile) =>
        println("Parser is successfully generated and written to \"" + outputFile + "\"")
      case Failure(ex) =>
        Console.err.println(ex.getMessage)
        System.exit(1)
    }
  }
}
