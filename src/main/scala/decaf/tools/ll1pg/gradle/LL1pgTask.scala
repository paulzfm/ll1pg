package decaf.tools.ll1pg.gradle

import java.io.File
import java.util

import decaf.tools.ll1pg.Runner
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

class LL1pgTask extends DefaultTask {
  /**
    * Source directory. Place your *.spec files in this directory.
    * Default: src/main/ll1pg.
    */
  var sourceDir: File = getProject.file("src/main/ll1pg")

  def setSourceDir(dir: File): Unit = sourceDir = dir

  /**
    * Output directory for generated Java source code.
    * Default: $buildDir/generated-src/ll1pg.
    */
  var outputDir: File = getProject.file(getProject.getBuildDir.toPath
    .resolve("generated-src").resolve("ll1pg"))

  def setOutputDir(dir: File): Unit = outputDir = dir

  /**
    * Execution target. Available targets are:
    *
    * - `table`: generate an LL(1) prediction table
    * - `parser`: generate a bare LL(1) parser (without any error recovery)
    * - `check`: only check if the input grammar is LL(1) (no code will be generated)
    */
  var target: String = "table"

  def setTarget(name: String): Unit = target = name

  @TaskAction
  def run(): Unit = {
    if (!sourceDir.exists) {
      Console.err.println(s"Error: source directory $sourceDir not exist")
      return
    }

    if (!outputDir.exists) {
      getProject.mkdir(outputDir)
    }

    val runner = target match {
      case "table" => Runner.runTableGen _
      case "parser" => Runner.runParserGen _
      case "check" => Runner.runGrammarCheck _
      case other => throw new IllegalArgumentException(s"invalid target: $other, available: table/parser/check")
    }

    val filter = new util.TreeMap[String, Object]
    filter.put("dir", sourceDir)
    filter.put("include", "**/*.spec")
    val files = getProject.fileTree(filter)
    if (files.isEmpty) Console.err.println(s"Warning: no *.spec file found in $sourceDir, skip")
    else files.visit { f =>
      if (!f.isDirectory) {
        println("Loading file " + f.getPath)
        runner(Runner.Config(f.getFile.toPath, outputDir.toPath))
      }
    }
  }
}
