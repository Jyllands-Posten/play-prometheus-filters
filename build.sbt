Global / onChangedBuildSource := ReloadOnSourceChanges

name := "play-prometheus-filters"
organization := "io.github.jyllands-posten"

version := "1.0.2"

lazy val root = project in file(".")

// All publishing configuration resides in sonatype.sbt
publishTo := sonatypePublishToBundle.value
credentials += Credentials(Path.userHome / ".sbt" / ".credentials.sonatype")

scalaVersion := "2.13.13"

val playVersion = "3.0.2"
val prometheusClientVersion = "0.16.0"

libraryDependencies ++= Seq(
  "io.prometheus" % "simpleclient" % prometheusClientVersion,
  "io.prometheus" % "simpleclient_hotspot" % prometheusClientVersion,
  "io.prometheus" % "simpleclient_servlet" % prometheusClientVersion,

  // Play libs. Are provided not to enforce a specific version.
  "org.playframework" %% "play" % playVersion % Provided,
  "org.playframework" %% "play-guice" % playVersion % Provided,
)

libraryDependencies ++= Seq(
  "org.scalatestplus.play" %% "scalatestplus-play" % "7.0.1" % Test,
  "org.mockito" % "mockito-core" % "5.11.0" % Test
)
