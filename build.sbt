name := "simpleimperative-algebraic-scala"

version := "0.2"

scalaVersion := "2.12.4"

scalacOptions ++= Seq(
  "-deprecation",
  "-feature",
  "-unchecked",
  "-language:higherKinds",
  "-Ypartial-unification"
)

addCompilerPlugin("org.spire-math" %% "kind-projector" % "0.9.6")

resolvers += Resolver.sonatypeRepo("snapshots")

libraryDependencies ++= Seq(
  "org.scalaz"     %% "scalaz-core"           % "7.2.20",
  "com.slamdata"   %% "matryoshka-core"       % "0.17.2",
  "com.slamdata"   %% "matryoshka-scalacheck" % "0.17.2" % Test,
  "org.scalatest"  %% "scalatest"             % "3.0.5"  % Test
)