name := "simpleimperative-algebraic-scala"

version := "0.4"

scalaVersion := "3.2.0"

scalacOptions ++= Seq(
  "-deprecation",
  "-feature",
  "-unchecked",
  "-Yexplicit-nulls",
  "-Ysafe-init",
  "-language:strictEquality",
  "-language:higherKinds"
)

libraryDependencies ++= Seq(
  "io.higherkindness"          %% "droste-core"               % "0.9.0",
  "io.chrisdavenport"          %% "cats-scalacheck"           % "0.3.1" % Test,
  "org.typelevel"              %% "cats-laws"                 % "2.8.0" % Test
)
