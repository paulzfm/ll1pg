import java.io.{BufferedWriter, File, FileWriter}

/**
  * Indent writer.
  * You can control the indentation by calling `incIndent` or `decIndent` to increase or decrease
  * one level.
  *
  * @param indent    number of blanks for each indent level, i.e, `indent * i` blanks will be
  *                  padded first for the i-th level indentation.
  * @param endOfLine end of line marker. Default is `\n`.
  */
class IndentWriter(val indent: Int = 4, val endOfLine: String = "\n") {
  /**
    * Content buffer.
    */
  private var buffer: String = ""

  /**
    * Current indentation level. Start from 0.
    */
  private var currentLevel: Int = 0

  /**
    * Whether a new line will be added.
    */
  private var newLine: Boolean = true

  /**
    * Print the padding blanks.
    */
  private def printIndentsIfNewLine(): Unit =
    if (newLine) buffer += " " * indent * currentLevel

  /**
    * Write contents to buffer.
    *
    * @param code the contents to be written.
    */
  def write(code: String = ""): Unit = {
    printIndentsIfNewLine()
    buffer += code
    newLine = false
  }

  /**
    * Similar to `write`, but an `endOfLine` will be appended after writing the contents.
    *
    * @param line the contents to be written.
    */
  def writeLn(line: String = ""): Unit = {
    write(line)
    buffer += endOfLine
    newLine = true
  }

  /**
    * Increase indentation `step` levels.
    *
    * @param step increasing delta.
    */
  def incIndent(step: Int = 1): Unit = currentLevel += step

  /**
    * Decrease indentation `step` levels.
    *
    * @param step decreasing delta.
    */
  def decIndent(step: Int = 1): Unit = if (currentLevel - step >= 0) currentLevel -= step

  /**
    * Write an empty line to buffer.
    */
  def next(): Unit = {
    if (!newLine) writeLn()
    writeLn()
  }

  /**
    * Print buffer to console.
    */
  def printToConsole(): Unit = print(buffer)

  /**
    * Output buffer to file.
    *
    * @param file output file.
    */
  def outputToFile(file: File): Unit = {
    val bw = new BufferedWriter(new FileWriter(file))
    bw.write(buffer)
    bw.close()
  }
}
