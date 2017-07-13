/**
  * An interface for type/class that can be printed to `IndentWriter`.
  */
trait Printable {
  def printTo(writer: IndentWriter): Unit

  /**
    * Print a list with separator.
    *
    * @param writer target indent writer.
    * @param b      string beginning.
    * @param e      string ending.
    * @param sep    separator for list.
    * @return a function `f(xs)` that print `b x1 sep x2 sep ... xn e` to `writer`.
    */
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
