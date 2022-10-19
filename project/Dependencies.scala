import sbt._

object Dependencies {
  val `munit` = "org.scalameta" %% "munit" % "0.7.29" % Test
  val `aws-lambda-java-core` = "com.amazonaws" % "aws-lambda-java-core" % "1.2.1"
  val `slf4j` = "org.slf4j" % "slf4j-api" % "1.7.30"
  val `logback-classic` = "ch.qos.logback" % "logback-classic" % "1.2.11"
  val `jackson-module-scala` = "com.fasterxml.jackson.module" %% "jackson-module-scala" % "2.12.6"
  val `jackson-core` = "com.fasterxml.jackson.core" % "jackson-core" % "2.12.6"
  val `jackson-databind` = "com.fasterxml.jackson.core" % "jackson-databind" % "2.12.6.1"
}