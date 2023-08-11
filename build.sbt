val calibanVersion        = "2.3.0"
val tapirVersion          = "1.7.0"
val zioVersion            = "2.0.15"
val zioConfigVersion      = "4.0.0-RC16"
val zioLoggingVersion     = "2.1.13"
val logbackClassicVersion = "1.4.4"
val postgresqlVersion     = "42.5.0"
val quillVersion          = "4.6.0.1"

lazy val root = (project in file("."))
  .settings(
    inThisBuild(
      List(
        organization := "com.example",
        scalaVersion := "3.3.0",
        run / fork := true,
        run / connectInput := true,
      )
    ),
    name           := "zio-hexagon-example",
    libraryDependencies ++= Seq(
      "com.github.ghostdogpr"       %% "caliban-http4s"          % calibanVersion,
      "com.softwaremill.sttp.tapir" %% "tapir-json-zio"          % tapirVersion,
      "com.softwaremill.sttp.tapir" %% "tapir-swagger-ui-bundle" % tapirVersion,

      "org.http4s"                  %% "http4s-ember-server"     % "0.23.19",
      "io.getquill"                 %% "quill-jdbc-zio"          % quillVersion excludeAll (
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
