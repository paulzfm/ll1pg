package decaf.tools.ll1pg

import java.nio.file.Paths

import decaf.tools.ll1pg.Runner.Config

/**
  * Entry of parser generator.
  */
object Main {
  def main(args: Array[String]): Unit = {
    val usage = "Usage: java -jar ll1pg.jar [-parser | -table | -check] <spec file> <output dir>"

    if (args.length == 0) {
      Console.err.println(usage)
      System.exit(1)
    }

    if (Set("-parser", "-table", "-check").contains(args(0))) {
      if (args.length >= 3) {
        val config = Config(Paths.get(args(1)), Paths.get(args(2)))
        args(0) match {
          case "-parser" => Runner.runParserGen(config)
          case "-table" => Runner.runTableGen(config)
          case "-check" => Runner.runGrammarCheck(config)
        }
        return
      }

      Console.err.println(usage)
      System.exit(1)
    }

    Console.err.println(s"Invalid option: ${args(0)}")
    System.exit(1)
  }
}