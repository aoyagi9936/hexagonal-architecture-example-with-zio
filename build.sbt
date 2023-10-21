val calibanVersion        = "2.4.1"
val tapirVersion          = "1.8.2"
val zioVersion            = "2.0.18"
val zioConfigVersion      = "4.0.0-RC16"
val zioLoggingVersion     = "2.1.14"
val logbackClassicVersion = "1.4.4"
val postgresqlVersion     = "42.5.0"
val quillVersion          = "4.8.0"

lazy val root = (project in file("."))
  .settings(
    inThisBuild(
      List(
        organization := "com.example",
        scalaVersion := "3.3.1",
        run / fork := true,
        run / connectInput := true,
      )
    ),
    name           := "zio-hexagon-example",
    libraryDependencies ++= Seq(
      "com.github.ghostdogpr"       %% "caliban-http4s"          % calibanVersion,
      "com.softwaremill.sttp.tapir" %% "tapir-json-zio"          % tapirVersion,
      "com.softwaremill.sttp.tapir" %% "tapir-swagger-ui-bundle" % tapirVersion,

      "org.http4s"                  %% "http4s-ember-server"     % "0.23.23",
      "io.getquill"                 %% "quill-jdbc-zio"          % quillVersion excludeAll (
        ExclusionRule(organization = "org.scala-lang.modules"),
        ExclusionRule(organization = "org.scalameta"),
      ),
      "org.postgresql"   % "postgresql"          % postgresqlVersion,
      "dev.zio"         %% "zio-config"          % zioConfigVersion,
      "dev.zio"         %% "zio-config-typesafe" % zioConfigVersion,
      "com.github.ghik" %% "silencer-lib"        % "1.4.2" % Provided cross CrossVersion.for3Use2_13, // https://github.com/zio/zio-config/issues/1245

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
