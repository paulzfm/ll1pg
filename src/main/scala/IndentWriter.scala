/**
  * Created by paul on 08/04/2017.
  */
class IndentWriter(val indent: Int = 4) {
  private var buffer: String = ""

  private var currentLevel: Int = 0

  private var newLine: Boolean = true

  private val endOfLine: String = "\n"

  private def printIndentsIfNewLine(): Unit =
    if (newLine) buffer += " " * indent * currentLevel

  def write(code: String = ""): Unit = {
    printIndentsIfNewLine()
    buffer += code
    newLine = false
  }

  def writeLn(line: String = ""): Unit = {
    write(line)
    buffer += endOfLine
    newLine = true
  }

  def incIndent(level: Int = 1): Unit = currentLevel += 1

  def decIndent(level: Int = 1): Unit = if (currentLevel > 0) currentLevel -= 1

  def next(): Unit = {
    if (!newLine) writeLn("")
    writeLn("")
  }

  def printToConsole(): Unit = print(buffer)

  def outputToFile(path: String): Unit = ???
}
