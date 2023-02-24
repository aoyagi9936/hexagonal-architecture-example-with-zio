package com.example

import caliban.Http4sAdapter
import cats.data.Kleisli
import com.comcast.ip4s._
import com.example.application.core.AppContext
import com.example.application.core.AuthorizationFilter
import com.example.adapters.primary.graphql.GraphqlResolver
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

import org.http4s.HttpRoutes
import org.http4s.Request
import cats.data.OptionT
import org.typelevel.ci.CIString

object Main extends ZIOAppDefault {

  override val bootstrap: ULayer[Unit] = Runtime.removeDefaultLoggers >>> SLF4J.slf4j

  import sttp.tapir.json.circe._

  type GqlAuthzTask[A] = RIO[AppContext.GqlEnv, A]

  object GqlAuthzMiddleware {
    def apply(route: HttpRoutes[GqlAuthzTask]): HttpRoutes[GqlAuthzTask] =
      Kleisli { (req: Request[GqlAuthzTask]) =>
        for {
          _ <- OptionT.liftF {
            val authzTask:GqlAuthzTask[Unit] = (for {
              _ <- req.headers.get(CIString("Authorization")) match {
                case Some(value) =>
                  ZIO.environmentWithZIO[AuthorizationFilter.Service](_.get.setToken(
                    Option(value.head.value)
                  ))
                case None => ZIO.unit
              }
            } yield ())
            authzTask
          }
          resp <- route(req)
        } yield resp
      }
  }

  override def run =
    ZIO
      .runtime[AppContext.GqlEnv]
      .flatMap(implicit runtime =>
        for {
          config      <- getConfig[ServerConfig]
          interpreter <- GraphqlResolver.api.interpreter
          _           <- EmberServerBuilder
          .default[GqlAuthzTask]
          .withHost(Host.fromString(config.host).getOrElse(host"localhost"))
          .withPort(Port.fromInt(config.port).getOrElse(port"8088"))
          .withHttpWebSocketApp(wsBuilder =>
            Router[GqlAuthzTask](
              "/api/graphql" -> GqlAuthzMiddleware(CORS.policy(Http4sAdapter.makeHttpService(interpreter))),
              "/ws/graphql"  -> GqlAuthzMiddleware(CORS.policy(Http4sAdapter.makeWebSocketService(wsBuilder, interpreter))),
              "/graphiql"    -> Kleisli.liftF(StaticFile.fromResource("/graphiql.html", None))
            ).orNotFound
          )
          .build
          .toScopedZIO *> ZIO.never
        } yield ()
      )
      .provideSomeLayer[Scope](AppContext.gqlLayer)
}

