package com.example

import cats.data.Kleisli
import com.comcast.ip4s._
import com.example.application.constants.PrimaryError
import com.example.application.core.{ AppContext, AuthorizationFilter }
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

import caliban.{ Http4sAdapter, GraphQLInterpreter, CalibanError }
import caliban.interop.tapir.{ HttpInterpreter, WebSocketInterpreter }
import caliban.CalibanError.ExecutionError
import caliban.ResponseValue.ObjectValue
import caliban.Value.StringValue
import caliban.CalibanError.{ ValidationError, ParsingError }

object Main extends ZIOAppDefault {

  override val bootstrap: ULayer[Unit] = Runtime.removeDefaultLoggers >>> SLF4J.slf4j

  import sttp.tapir.json.zio._

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

  def withErrorCodeExtensions[R <: AppContext.GqlEnv](
    interpreter: GraphQLInterpreter[R, CalibanError]
  ): GraphQLInterpreter[R, CalibanError] = interpreter.mapError {
    case err @ ExecutionError(_, _, _, Some(primaryError: PrimaryError), _) =>
      err.copy(extensions = Some(ObjectValue(List(("errorCode", StringValue(primaryError.code))))))
    case err: ExecutionError =>
      err.copy(extensions = Some(ObjectValue(List(("errorCode", StringValue("EXECUTION_ERROR"))))))
    case err: ValidationError =>
      err.copy(extensions = Some(ObjectValue(List(("errorCode", StringValue("VALIDATION_ERROR"))))))
    case err: ParsingError =>
      err.copy(extensions = Some(ObjectValue(List(("errorCode", StringValue("PARSING_ERROR"))))))
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
              "/api/graphql" -> 
                CORS.policy(
                  GqlAuthzMiddleware(
                    Http4sAdapter.makeHttpService(
                      HttpInterpreter(
                        withErrorCodeExtensions[AppContext.GqlEnv](interpreter)
                      )
                    )
                  )
                ),
              "/ws/graphql" ->
                CORS.policy(
                  GqlAuthzMiddleware(
                    Http4sAdapter.makeWebSocketService(wsBuilder,
                      WebSocketInterpreter(
                        withErrorCodeExtensions[AppContext.GqlEnv](interpreter)
                      )
                    )
                  )
                ),
              "/graphiql" -> Kleisli.liftF(StaticFile.fromResource("/graphiql.html", None))
            ).orNotFound
          )
          .build
          .toScopedZIO *> ZIO.never
        } yield ()
      )
      .provideSomeLayer[Scope](AppContext.gqlLayer)
}

