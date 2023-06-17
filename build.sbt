val calibanVersion        = "2.2.1"
val tapirJsonZioVersion   = "1.2.13"
val zioVersion            = "2.0.15"
val zioConfigVersion      = "3.0.7"
val zioLoggingVersion     = "2.1.13"
val logbackClassicVersion = "1.4.4"
val postgresqlVersion     = "42.5.0"
val quillVersion          = "4.6.0.1"

lazy val root = (project in file("."))
  .settings(
    inThisBuild(
      List(
        organization := "com.example",
        scalaVersion := "3.2.2",
        run / fork := true,
        run / connectInput := true,
      )
    ),
    name           := "zio-hexagon-example",
    libraryDependencies ++= Seq(
      "com.github.ghostdogpr"       %% "caliban-http4s"      % calibanVersion,
      "com.softwaremill.sttp.tapir" %% "tapir-json-zio"      % tapirJsonZioVersion,
      "org.http4s"                  %% "http4s-ember-server" % "0.23.16",
      "io.getquill"                 %% "quill-jdbc-zio"      % quillVersion excludeAll (
        ExclusionRule(organization = "org.scala-lang.modules"),
        ExclusionRule(organization = "org.scalameta"),
      ),
      "org.postgresql" % "postgresql"          % postgresqlVersion,
      "dev.zio"       %% "zio-config"          % zioConfigVersion,
      "dev.zio"       %% "zio-config-typesafe" % zioConfigVersion,

      // logging
      "dev.zio"       %% "zio-logging"       % zioLoggingVersion,
      "dev.zio"       %% "zio-logging-slf4j" % zioLoggingVersion,
      "ch.qos.logback" % "logback-classic"   % logbackClassicVersion,

      // test
      "dev.zio"       %% "zio-test"          % zioVersion % Test,
      "dev.zio"       %% "zio-test-sbt"      % zioVersion % Test,
    ),
    testFrameworks := Seq(new TestFramework("zio.test.sbt.ZTestFramework")),
  )
  .enablePlugins(JavaAppPackaging)
