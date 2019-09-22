package decaf.tools.pg

/**
  * An interface for type/class that can be printed to `IndentWriter`.
  */
trait Printable {
  def printTo(writer: IndentWriter): Unit
}
