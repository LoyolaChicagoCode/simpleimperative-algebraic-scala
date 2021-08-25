name := "simpleimperative-algebraic-scala"

version := "0.4"

scalaVersion := "3.0.1"

scalacOptions ++= Seq(
  "-deprecation",
  "-feature",
  "-unchecked",
  "-Yexplicit-nulls",
  "-language:higherKinds"
)

libraryDependencies ++= Seq(
  "io.higherkindness"          %% "droste-core"               % "0.9.0-M3",
  "io.chrisdavenport"          %% "cats-scalacheck"           % "0.3.1" % Test,
  "org.typelevel"              %% "cats-laws"                 % "2.6.1" % Test
)

scalacOptions ++= Seq("-rewrite", "-new-syntax")
