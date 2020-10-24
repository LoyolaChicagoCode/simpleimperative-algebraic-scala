name := "simpleimperative-algebraic-scala"

version := "0.3"

scalaVersion := "2.13.3"

scalacOptions ++= Seq(
  "-deprecation",
  "-feature",
  "-unchecked",
  "-language:higherKinds"
)

addCompilerPlugin("org.typelevel" %% "kind-projector" % "0.11.0" cross CrossVersion.full)

libraryDependencies ++= Seq(
  "io.higherkindness"          %% "droste-core"               % "0.8.0",
  "io.chrisdavenport"          %% "cats-scalacheck"           % "0.3.0" % Test,
  "org.typelevel"              %% "cats-laws"                 % "2.0.0" % Test,
  "com.github.alexarchambault" %% "scalacheck-shapeless_1.14" % "1.2.3" % Test
)