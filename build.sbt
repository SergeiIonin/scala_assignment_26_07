
ThisBuild / organization := "com.example"
ThisBuild / scalaVersion := "2.13.5"

val versions = new {
  val catsEffect = "3.3.12"
  val circe = "0.14.9"
  val http4s = "0.23.6"
}

lazy val circeDependencies =
  Seq(
    "io.circe" %% "circe-core" % versions.circe,
    "io.circe" %% "circe-generic" % versions.circe,
  )

lazy val http4sDependencies =
  Seq(
    "org.http4s" %% "http4s-ember-server" % versions.http4s,
    "org.http4s" %% "http4s-ember-client" % versions.http4s,
    "org.http4s" %% "http4s-circe" % versions.http4s,
    "org.http4s" %% "http4s-dsl" % versions.http4s,
  )

lazy val root = (project in file(".")).settings(
  name := "cats-effect-3-quick-start",
  libraryDependencies ++= Seq(
    // "core" module - IO, IOApp, schedulers
    // This pulls in the kernel and std modules automatically.
    "org.typelevel" %% "cats-effect" % versions.catsEffect,
    // concurrency abstractions and primitives (Concurrent, Sync, Async etc.)
    "org.typelevel" %% "cats-effect-kernel" % versions.catsEffect,
    // standard "effect" library (Queues, Console, Random etc.)
    "org.typelevel" %% "cats-effect-std" % versions.catsEffect,
    // better monadic for compiler plugin as suggested by documentation
    "org.typelevel" %% "cats-effect-testing-specs2" % "1.5.0" % Test,

    compilerPlugin("com.olegpy" %% "better-monadic-for" % "0.3.1"),
    // add http4s
    "org.typelevel" %% "munit-cats-effect-3" % "1.0.7" % Test
  ) ++ circeDependencies ++ http4sDependencies
)
