import scala.util.{Failure, Success}

/**
  * Entry of parser generator.
  */
object Main {
  def main(args: Array[String]): Unit = {
    if (args.length != 2) {
      Console.err.println(s"Usage: java -jar pg.jar <spec file> <output file>")
      System.exit(1)
    }

    val source = scala.io.Source.fromFile(args(0))
    val lines = try source.mkString finally source.close()

    Parsers.parse(lines) match {
      case Success(spec) =>
        val gen = new Generator(spec)
        val code = gen.generate
        val writer = new IndentWriter
        code.printTo(writer)
        writer.outputToFile(args(1))
      case Failure(ex) => ex.printStackTrace()
    }

  }

}
