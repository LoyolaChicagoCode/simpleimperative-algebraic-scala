name := "simpleimperative-algebraic-scala"

version := "0.4"

scalaVersion := "3.3.0"

scalacOptions += "@.scalacOptions.txt"

libraryDependencies ++= Seq(
  "io.higherkindness" %% "droste-core"     % "0.9.0",
  "io.chrisdavenport" %% "cats-scalacheck" % "0.3.2"  % Test,
  "org.typelevel"     %% "cats-laws"       % "2.10.0" % Test
)
