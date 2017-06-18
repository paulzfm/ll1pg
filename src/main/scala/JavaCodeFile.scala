/**
  * Created by paul on 18/06/2017.
  */

import AST._
import Utils._

class JavaCodeFile(val pkg: Package, val imports: Imports, val cls: Class,
                   val semValue: SemValue,
                   val tokens: List[Token],
                   val parsers: List[NonTerminalParser]) {
}
