package com.example

import caliban.Http4sAdapter
import cats.data.Kleisli
import com.comcast.ip4s._
import com.example.application.services.ServerService
import com.example.adapters.primary.graphql.GraphqlApi
import org.http4s.StaticFile
import org.http4s.ember.server.EmberServerBuilder
import org.http4s.implicits._
import org.http4s.server.Router
import org.http4s.server.middleware.CORS
import zio._
import zio.interop.catz._

import zio.config._
import com.example.application.config.Configuration._
import zio.logging.backend.SLF4J

object Main extends ZIOAppDefault {

  override val bootstrap: ULayer[Unit] = Runtime.removeDefaultLoggers >>> SLF4J.slf4j

  import sttp.tapir.json.circe._

  type GraphqlTask[A] = RIO[GraphqlApi.Env, A]

  override def run =
    ZIO
      .runtime[ServerService.Env]
      .flatMap(implicit runtime =>
        for {
          config      <- getConfig[ServerConfig]
          interpreter <- GraphqlApi.api.interpreter
          _           <- EmberServerBuilder
          .default[GraphqlTask]
          .withHost(Host.fromString(config.host).getOrElse(host"localhost"))
          .withPort(Port.fromInt(config.port).getOrElse(port"8088"))
          .withHttpWebSocketApp(wsBuilder =>
            Router[GraphqlTask](
              "/api/graphql" -> CORS.policy(Http4sAdapter.makeHttpService(interpreter)),
              "/ws/graphql"  -> CORS.policy(Http4sAdapter.makeWebSocketService(wsBuilder, interpreter)),
              "/graphiql"    -> Kleisli.liftF(StaticFile.fromResource("/graphiql.html", None))
            ).orNotFound
          )
          .build
          .toScopedZIO *> ZIO.never
        } yield ()
      )
      .provideSomeLayer[Scope](ServerService.layer)
}

