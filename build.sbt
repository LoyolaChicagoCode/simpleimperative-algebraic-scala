name := "simpleimperative-algebraic-scala"

version := "0.1"

scalaVersion := "2.11.8"

scalacOptions ++= Seq("-deprecation", "-feature", "-unchecked")

resolvers += "laufer@bintray" at "http://dl.bintray.com/laufer/maven"

libraryDependencies ++= Seq(
  "edu.luc.etl" %% "scalamu" % "0.4.5",
  "org.scalatest" %% "scalatest" % "3.0.1" % Test,
  "org.scalacheck" %% "scalacheck" % "1.12.6" % Test,
  "org.scalaz" %% "scalaz-scalacheck-binding" % "7.2.8" % Test
)