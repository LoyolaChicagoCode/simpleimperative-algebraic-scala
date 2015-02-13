name := "minic-scala"

version := "0.1"

scalaVersion := "2.10.4"

scalacOptions ++= Seq("-deprecation", "-feature", "-unchecked")

resolvers += "laufer@bintray" at "http://dl.bintray.com/laufer/maven"

libraryDependencies ++= Seq(
  "edu.luc.etl" %% "scalamu" % "0.2.2",
  "org.scalatest" %% "scalatest" % "2.2.4" % "test",
  "org.scalacheck" %% "scalacheck" % "1.12.2" % "test",
  "org.scalaz" %% "scalaz-scalacheck-binding" % "7.0.6"
)