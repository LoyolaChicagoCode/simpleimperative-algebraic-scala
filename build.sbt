name := "simpleimperative-algebraic-scala"

version := "0.2"

scalaVersion := "2.12.12"

scalacOptions ++= Seq(
  "-deprecation",
  "-feature",
  "-unchecked",
  "-language:higherKinds",
  "-Ypartial-unification"
)

addCompilerPlugin("org.typelevel" % "kind-projector_2.12.12" % "0.11.0")

libraryDependencies ++= Seq(
  "org.scalaz"     %% "scalaz-core"           % "7.3.2",
  "com.slamdata"   %% "matryoshka-core"       % "0.21.3",
  "org.scalaz"     %% "scalaz-scalacheck-binding" % "7.3.2"  % Test,
  "com.slamdata"   %% "matryoshka-scalacheck"     % "0.21.3" % Test,
  "org.scalatest"  %% "scalatest"                 % "3.2.2"  % Test
)