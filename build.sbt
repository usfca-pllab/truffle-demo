val scala3Version = "3.3.1"

lazy val root = project
  .in(file("."))
  .settings(
    name := "truf",
    version := "0.1.0-SNAPSHOT",

    scalaVersion := scala3Version,

    scalacOptions += "-deprecation",

    libraryDependencies += "org.scalameta" %% "munit" % "0.7.29" % Test,
    libraryDependencies += "org.graalvm.truffle" % "truffle-api" % "22.1.0",
    libraryDependencies += "org.graalvm.truffle" % "truffle-dsl-processor" % "22.1.0"
  )
