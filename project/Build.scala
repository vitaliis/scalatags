import sbt._
import Keys._
import org.scalajs.sbtplugin.ScalaJSPlugin
import org.scalajs.sbtplugin.ScalaJSPlugin._
import org.scalajs.sbtplugin.ScalaJSPlugin.autoImport._
import twirl.sbt.TwirlPlugin._
import Twirl._
object Build extends sbt.Build{
  val cross = new utest.jsrunner.JsCrossBuild(
    organization := "com.lihaoyi",
    name := "scalatags",
    scalaVersion := "2.11.5",
    autoCompilerPlugins := true,
    libraryDependencies += "com.lihaoyi" %% "acyclic" % "0.1.2" % "provided",
    addCompilerPlugin("com.lihaoyi" %% "acyclic" % "0.1.2"),

    libraryDependencies ++= (
      if (scalaVersion.value.startsWith("2.10")) Nil
      else Seq("org.scala-lang.modules" %% "scala-xml" % "1.0.1" % "test")
    ),

    // Sonatype
    version := "0.4.3-RC1",
    publishTo := Some("releases"  at "https://oss.sonatype.org/service/local/staging/deploy/maven2"),

    pomExtra :=
      <url>https://github.com/lihaoyi/scalatags</url>
        <licenses>
          <license>
            <name>MIT license</name>
            <url>http://www.opensource.org/licenses/mit-license.php</url>
          </license>
        </licenses>
        <scm>
          <url>git://github.com/lihaoyi/scalatags.git</url>
          <connection>scm:git://github.com/lihaoyi/scalatags.git</connection>
        </scm>
        <developers>
          <developer>
            <id>lihaoyi</id>
            <name>Li Haoyi</name>
            <url>https://github.com/lihaoyi</url>
          </developer>
        </developers>

  )

  def sourceMapsToGithub: Project => Project =
    p => p.settings(
      scalacOptions ++= (if (isSnapshot.value) Seq.empty else Seq({
        val a = p.base.toURI.toString.replaceFirst("[^/]+/?$", "")
        val g = "https://raw.githubusercontent.com/lihaoyi/scalatags"
        s"-P:scalajs:mapSourceURI:$a->$g/v${version.value}/"
      }))
    )

  lazy val root = cross.root.enablePlugins(ScalaJSPlugin)

  lazy val js = cross.js.settings(
    libraryDependencies ++= Seq(
      "org.scala-js" %%% "scalajs-dom" % "0.7.1-SNAPSHOT"
    ),
    resolvers += Resolver.sonatypeRepo("releases"),
    scalaJSStage in Test := FullOptStage,
    requiresDOM := true
  ).configure(sourceMapsToGithub)


  lazy val jvm = cross.jvm

  lazy val example = project.in(file("example"))
                            .dependsOn(js)
                            .enablePlugins(ScalaJSPlugin)
                            .settings(
      scalaVersion := "2.11.5",
      libraryDependencies += "org.scala-js" %%% "scalajs-dom" % "0.7.1-SNAPSHOT"
    )
}
