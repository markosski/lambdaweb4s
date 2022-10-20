import Dependencies._
import xerial.sbt.Sonatype._

lazy val scala212 = "2.12.11"
lazy val scala213 = "2.13.7"
lazy val supportedScalaVersions = List(scala213)

ThisBuild / scalaVersion := scala213
ThisBuild / organization := "io.github.markosski"
ThisBuild / versionScheme := Some("semver-spec")
ThisBuild / assemblyMergeStrategy := {
  case PathList("javax", "servlet", xs @ _*)         => MergeStrategy.first
  case PathList(ps @ _*) if ps.last endsWith ".html" => MergeStrategy.first
  case "META-INF/io.netty.versions.properties"       => MergeStrategy.discard
  case "module-info.class"                           => MergeStrategy.first
  case "application.conf"                            => MergeStrategy.concat
  case x =>
    val oldStrategy = (ThisBuild / assemblyMergeStrategy).value
    oldStrategy(x)
}

lazy val artifactorySettings = Seq(
  publishMavenStyle := true,
  publishTo := sonatypePublishToBundle.value,
  sonatypeCredentialHost := "s01.oss.sonatype.org",
  sonatypeRepository := "https://s01.oss.sonatype.org/service/local",
  sonatypeProfileName := "io.github.markosski",
  sonatypeProjectHosting := Some(GitHubHosting("markosski", "lambdaweb4s", "marcin.kossakowski@gmail.com")),
)

lazy val root = (project in file("lambdaweb4s"))
  .settings(BuildHelper.stdSettings)
  .settings(artifactorySettings)
  .settings(
    name := "lambdaweb4s",
    developers := List(
      Developer(id="markosski", name="Marcin Kossakowski", email="marcin.kossakowski@gmail.com", url=url("https://www.marcinkossakowski.com"))
    ),
    scmInfo := Some(
      ScmInfo(
        url("https://github.com/markosski/lambdaweb4s"),
        "scm:git@github.com:markosski/lambdaweb4s.git"
      )
    ),
    licenses += ("MIT", url("http://opensource.org/licenses/MIT")),
    crossScalaVersions := supportedScalaVersions,
    libraryDependencies ++= Seq(
      `munit`,
      `jackson-core`,
      `jackson-databind`,
      `jackson-module-scala`,
      `aws-lambda-java-core`,
      `slf4j`
      )
  )

lazy val example = (project in file("example"))
  .dependsOn(root)
  .settings(BuildHelper.stdSettings)
  .settings(
    name := "example",
    Compile / run / mainClass := Some("example.Handler"),
    publish / skip := true,
    libraryDependencies ++= Seq(
      `logback-classic`
    ),
    assembly / mainClass := Some("example.Handler"),
    assembly / assemblyJarName := s"${name.value}.jar"
  )