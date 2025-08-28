name := "simpleimperative-algebraic-scala"

version := "0.4"

libraryDependencies ++= Seq(
  "io.higherkindness" %% "droste-core"     % "0.9.0",
  "io.chrisdavenport" %% "cats-scalacheck" % "0.3.2"  % Test,
  "org.typelevel"     %% "cats-laws"       % "2.12.0" % Test
)
