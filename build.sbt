name := "simpleimperative-algebraic-scala"

version := "0.1"

scalaVersion := "2.11.7"

scalacOptions ++= Seq("-deprecation", "-feature", "-unchecked")

resolvers += "laufer@bintray" at "http://dl.bintray.com/laufer/maven"

libraryDependencies ++= Seq(
  "edu.luc.etl" %% "scalamu" % "0.3.0",
  "org.scalatest" %% "scalatest" % "2.2.6" % Test,
  "org.scalacheck" %% "scalacheck" % "1.12.2" % Test,
  "org.scalaz" %% "scalaz-scalacheck-binding" % "7.2.0" % Test
)