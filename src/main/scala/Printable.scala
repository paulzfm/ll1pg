trait Printable {
  def printTo(writer: IndentWriter): Unit

  protected def printSep(writer: IndentWriter, b: String = "(", e: String = ")",
                         sep: String = ", "): List[Printable] => Unit = {
    case Nil => writer.write(b + e)
    case x :: Nil =>
      writer.write(b)
      x.printTo(writer)
      writer.write(e)
    case xs :+ y =>
      writer.write(b)
      xs.foreach {
        x =>
          x.printTo(writer)
          writer.write(sep)
      }
      y.printTo(writer)
      writer.write(e)
  }
}
