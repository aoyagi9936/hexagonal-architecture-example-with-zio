val calibanVersion        = "2.5.2"
val tapirVersion          = "1.9.10"
val zioVersion            = "2.0.21"
val zioConfigVersion      = "4.0.1"
val zioLoggingVersion     = "2.2.2"
val zioUuidVersion        = "1.0.0"
val logbackClassicVersion = "1.5.0"
val postgresqlVersion     = "42.7.2"
val enumeratumVersion     = "1.7.3"
val quillVersion          = "4.8.1"
val testContainersVersion = "0.41.3"
val http4sVersion         = "0.23.25"

lazy val root = (project in file("."))
  .settings(
    inThisBuild(
      List(
        organization := "com.example",
        scalaVersion := "3.4.0",
        run / fork := true,
        run / connectInput := true,
      )
    ),
    name           := "zio-hexagon-example",
    libraryDependencies ++= Seq(
      "com.github.ghostdogpr"       %% "caliban-http4s"          % calibanVersion,
      "com.softwaremill.sttp.tapir" %% "tapir-json-zio"          % tapirVersion,
      "com.softwaremill.sttp.tapir" %% "tapir-swagger-ui-bundle" % tapirVersion,

      "org.http4s"                  %% "http4s-ember-server"     % http4sVersion,
      "io.getquill"                 %% "quill-jdbc-zio"          % quillVersion excludeAll (
        ExclusionRule(organization = "org.scala-lang.modules"),
        ExclusionRule(organization = "org.scalameta"),
      ),
      "org.postgresql"   % "postgresql"          % postgresqlVersion,
      "dev.zio"         %% "zio-config"          % zioConfigVersion,
      "dev.zio"         %% "zio-config-typesafe" % zioConfigVersion,
      "com.beachape"    %% "enumeratum"          % enumeratumVersion,
      "com.guizmaii"    %% "zio-uuid"            % zioUuidVersion,

      // logging
      "dev.zio"       %% "zio-logging"       % zioLoggingVersion,
      "dev.zio"       %% "zio-logging-slf4j" % zioLoggingVersion,
      "ch.qos.logback" % "logback-classic"   % logbackClassicVersion,

      // test
      "dev.zio"       %% "zio-test"          % zioVersion % Test,
      "dev.zio"       %% "zio-test-sbt"      % zioVersion % Test,
      "com.dimafeng"  %% "testcontainers-scala-postgresql" % testContainersVersion % Test,
    ),
    testFrameworks := Seq(new TestFramework("zio.test.sbt.ZTestFramework")),
  )
  .enablePlugins(JavaAppPackaging)
