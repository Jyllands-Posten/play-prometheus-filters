Global / onChangedBuildSource := ReloadOnSourceChanges

name := "play-prometheus-filters"
// TODO: should I change the organisation to `to.wetransform`?
organization := "io.github.jyllands-posten"

version := "0.7.0"

lazy val root = (project in file("."))

// All publishing configuration resides in sonatype.sbt
//publishTo := sonatypePublishToBundle.value
publishTo := {
  val artifactory = "https://artifactory.wetransform.to/artifactory/"
  if (isSnapshot.value)
    Some("snapshots" at artifactory + "private-snapshot-local;build.timestamp=" + new java.util.Date().getTime)
  else
    Some("releases"  at artifactory + "private-release-local")
}

resolvers += "Typesafe Releases" at "https://repo.typesafe.com/typesafe/releases/"

resolvers += "wetransform Releases" at "https://artifactory.wetransform.to/artifactory/private-release-local/"

resolvers += "wetransform Snapshots" at "https://artifactory.wetransform.to/artifactory/private-snapshot-local/"

credentials += Credentials(Path.userHome / ".wetfArtifactory")

//credentials += Credentials(Path.userHome / ".sbt" / ".credentials.sonatype")

scalaVersion := "2.13.12"
crossScalaVersions := Seq(scalaVersion.value, "2.12.15")

val playVersion = "2.9.1"
val prometheusClientVersion = "0.9.0"

libraryDependencies ++= Seq(
  "io.prometheus" % "simpleclient" % prometheusClientVersion,
  "io.prometheus" % "simpleclient_hotspot" % prometheusClientVersion,
  "io.prometheus" % "simpleclient_servlet" % prometheusClientVersion,

  // Play libs. Are provided not to enforce a specific version.
  "com.typesafe.play" %% "play" % playVersion % Provided,
  "com.typesafe.play" %% "play-guice" % playVersion % Provided,

  // This library makes some Scala 2.13 APIs available on Scala 2.11 and 2.12.
  "org.scala-lang.modules" %% "scala-collection-compat" % "2.6.0"
)

libraryDependencies ++= Seq(
  "org.scalatestplus.play" %% "scalatestplus-play" % "5.1.0" % Test,
  "org.scalatestplus" %% "mockito-3-4" % "3.2.10.0" % Test,
  "org.mockito" % "mockito-core" % "4.2.0" % Test
)
