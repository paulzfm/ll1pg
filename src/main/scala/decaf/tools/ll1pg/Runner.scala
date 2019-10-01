package decaf.tools.ll1pg

import java.nio.file.Path

import scala.util.{Failure, Success}

object Runner {

  case class Config(inputSpec: Path, outputDir: Path)

  private def parse(config: Config): Option[Generator] = {
    val source = scala.io.Source.fromFile(config.inputSpec.toFile)
    val lines = try source.mkString finally source.close()

    Parsers.parse(lines) match {
      case Failure(ex) =>
        Console.err.println(ex.getMessage)
        None
      case Success(spec) =>
        val generator = new Generator(spec, config.inputSpec.toAbsolutePath.toString)
        Some(generator)
    }
  }

  def runGrammarCheck(config: Config): Unit = {
    parse(config).foreach {
      generator =>
        generator.generateTable
    }
  }

  def runTableGen(config: Config): Unit = {
    parse(config).foreach {
      generator =>
        val table = generator.generateTable
        val writer = new IndentWriter
        table.printTo(writer)
        val outputFile = config.outputDir.resolve(table.cls.name + ".java").toFile
        writer.outputToFile(outputFile)
        println("Table is successfully generated and written to \"" + outputFile + "\"")
    }
  }

  def runParserGen(config: Config): Unit = {
    parse(config).foreach {
      generator =>
        val parser = generator.generateParser
        val writer = new IndentWriter
        parser.printTo(writer)
        val outputFile = config.outputDir.resolve(parser.cls.name + ".java").toFile
        writer.outputToFile(outputFile)
        println("Parser is successfully generated and written to \"" + outputFile + "\"")
    }
  }
}
