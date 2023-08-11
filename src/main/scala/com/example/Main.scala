package com.example

import cats.data.Kleisli
import com.comcast.ip4s._
import com.example.application.constants.PrimaryError
import com.example.application.core.{ AppContext, AuthorizationFilter }
import com.example.adapters.primary.graphql.GraphqlResolver
import com.example.adapters.primary.rest.RestResolver
import com.example.application.config.Configuration._
import org.http4s.StaticFile
import org.http4s.ember.server.EmberServerBuilder
import org.http4s.implicits._
import org.http4s.server.Router
import org.http4s.server.middleware.CORS

import zio._
import zio.interop.catz._
import zio.logging.backend.SLF4J
import zio.config.typesafe.TypesafeConfigProvider

import org.http4s.HttpRoutes
import org.http4s.Request
import cats.data.OptionT
import org.typelevel.ci.CIString
import org.http4s.implicits._
import fs2.io.net.Network

import caliban.{ Http4sAdapter, GraphQLInterpreter, CalibanError }
import caliban.interop.tapir.{ HttpInterpreter, WebSocketInterpreter }
import caliban.CalibanError.ExecutionError
import caliban.ResponseValue.ObjectValue
import caliban.Value.StringValue
import caliban.CalibanError.{ ValidationError, ParsingError }

object Main extends ZIOAppDefault {  

  import sttp.tapir.json.zio._

  override val bootstrap: ULayer[Unit] =
    Runtime.setConfigProvider(
      TypesafeConfigProvider
        .fromResourcePath()
    ) >>> Runtime.removeDefaultLoggers >>> SLF4J.slf4j

  type GqlAuthzTask[A] = RIO[AppContext.GqlApp, A]

  object AuthzMiddleware {
    def apply(route: HttpRoutes[GqlAuthzTask]): HttpRoutes[GqlAuthzTask] =
      Kleisli { (req: Request[GqlAuthzTask]) =>
        for {
          _ <- OptionT.liftF {
            val authzTask: GqlAuthzTask[Unit] = (for {
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

  def withErrorCodeExtensions[R <: AppContext.GqlApp](
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

  private implicit val nwGql: Network[GqlAuthzTask] = Network.forAsync
  private implicit val nwRest: Network[RIO[RestResolver.Apis, *]] = Network.forAsync

  val graphql = (args: Chunk[String]) =>
    ZIO
      .runtime[AppContext.GqlApp]
      .flatMap(implicit runtime =>
        for {
          config      <- ZIO.service[ServerConfig]
          interpreter <- GraphqlResolver.api.interpreter
          _           <- EmberServerBuilder
          .default[GqlAuthzTask]
          .withHost(Host.fromString(config.host).getOrElse(host"localhost"))
          .withPort(Port.fromInt(config.port).getOrElse(port"8088"))
          .withHttpWebSocketApp(wsBuilder =>
            Router[GqlAuthzTask](
              "/api/graphql" -> 
                CORS.policy(
                  AuthzMiddleware(
                    Http4sAdapter.makeHttpService(
                      HttpInterpreter(
                        withErrorCodeExtensions[AppContext.GqlApp](interpreter)
                      )
                    )
                  )
                ),
              "/ws/graphql" ->
                CORS.policy(
                  AuthzMiddleware(
                    Http4sAdapter.makeWebSocketService(wsBuilder,
                      WebSocketInterpreter(
                        withErrorCodeExtensions[AppContext.GqlApp](interpreter)
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

  val rest = (args: Chunk[String]) =>
    ZIO
      .runtime[AppContext.RestApp]
      .flatMap(implicit runtime =>
        for {
          config      <- ZIO.service[ServerConfig]
          _           <- EmberServerBuilder
          .default[RIO[RestResolver.Apis, *]]
          .withHost(Host.fromString(config.host).getOrElse(host"localhost"))
          .withPort(Port.fromInt(config.port).getOrElse(port"9000"))
          .withHttpApp(
            Router("/" -> RestResolver.routes).orNotFound
          )
          .build
          .toScopedZIO *> ZIO.never
        } yield ()
      )
      .provideSomeLayer[Scope](AppContext.restLayer)

  override def run =
    (for {
      args <- getArgs
      _    <- graphql(args) zipPar rest(args)
    } yield()).exitCode

}
