package decaf.tools.ll1pg.gradle

import org.gradle.api.plugins.JavaPluginConvention
import org.gradle.api.{Plugin, Project}

class LL1pgPlugin extends Plugin[Project] {
  override def apply(project: Project): Unit = {
    project.getPlugins.apply("java")
    val tasks = project.getTasks
    val task = tasks.create("ll1pg", classOf[LL1pgTask])
    tasks.getByName("compileJava").dependsOn(task)
    project.getConvention.getPlugin(classOf[JavaPluginConvention]).getSourceSets
      .getByName("main").getJava.srcDir(task.outputDir)
  }
}
