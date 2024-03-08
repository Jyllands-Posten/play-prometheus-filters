import scala.collection.Seq

Global / onChangedBuildSource := ReloadOnSourceChanges
name := "play-prometheus-filters"
version := "1.0.0"
scalaVersion := "2.13.8"
val playVersion = "3.0.0"
val prometheusClientVersion = "0.16.0"

lazy val root = project in file(".")

// All publishing configuration resides in sonatype.sbt
publishTo := sonatypePublishToBundle.value
credentials += Credentials(Path.userHome / ".sbt" / ".credentials.sonatype")

// sbt-github-actions defaults to using JDK 8 for testing and publishing.
// The following adds JDK 17 for testing.
ThisBuild / githubWorkflowJavaVersions += JavaSpec.temurin("17")

// sbt-ci
inThisBuild(List(
  organization := "io.github.sdudzin",
  homepage := Some(url("https://github.com/sdudzin/play-prometheus-filters")),
  // Alternatively License.Apache2 see https://github.com/sbt/librarymanagement/blob/develop/core/src/main/scala/sbt/librarymanagement/License.scala
  licenses := List("MIT License" -> url("http://www.opensource.org/licenses/mit-license.php")),
  developers := List(
    Developer(
      "sdudzin",
      "Siarhei Dudzin",
      "sdudzin@hiveteq.com",
      url("https://github.com/sdudzin/")
    )
  )
))
ThisBuild / sonatypeCredentialHost := "s01.oss.sonatype.org"
sonatypeRepository := "https://s01.oss.sonatype.org/service/local"

// sbt-github-actions
ThisBuild / githubWorkflowTargetTags ++= Seq("v*")
ThisBuild / githubWorkflowPublishTargetBranches := Seq(RefPredicate.StartsWith(Ref.Tag("v")))
ThisBuild / githubWorkflowPublish := Seq(
  WorkflowStep.Sbt(
    commands = List("ci-release"),
    name = Some("Publish project"),
  )
)
ThisBuild / githubWorkflowBuild := Seq(WorkflowStep.Sbt(List("test", "scripted")))

libraryDependencies ++= Seq(
  "io.prometheus" % "simpleclient" % prometheusClientVersion,
  "io.prometheus" % "simpleclient_hotspot" % prometheusClientVersion,
  "io.prometheus" % "simpleclient_servlet" % prometheusClientVersion,

  // Play libs. Are provided not to enforce a specific version.
  "org.playframework" %% "play" % playVersion % Provided,
  "org.playframework" %% "play-guice" % playVersion % Provided,

  // This library makes some Scala 2.13 APIs available on Scala 2.11 and 2.12.
  "org.scala-lang.modules" %% "scala-collection-compat" % "2.8.1"
)

libraryDependencies ++= Seq(
  "org.scalatestplus.play" %% "scalatestplus-play" % "7.0.0" % Test,
  "org.scalatestplus" %% "mockito-3-4" % "3.2.10.0" % Test,
  "org.mockito" % "mockito-core" % "5.8.0" % Test
)
