package decaf.tools.ll1pg

/**
  * An interface for type/class that can be printed to `IndentWriter`.
  */
trait Printable {
  def printTo(writer: IndentWriter): Unit
}
