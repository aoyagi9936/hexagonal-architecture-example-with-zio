package com.example.adapters.primary.rest

import org.http4s.HttpRoutes
import sttp.tapir.ztapir._
import sttp.tapir.server.http4s.ztapir.ZHttp4sServerInterpreter
import sttp.tapir.swagger.bundle.SwaggerInterpreter
import zio._
import zio.interop.catz._
import cats.syntax.all._

import com.example.adapters.primary.rest.endpoints._
import com.example.ports.primary.ExampleReadApi
import TapirSupport._

object RestResolver {

  type Apis = ExampleReadEndpoint.Apis

  val swaggerRoutes: HttpRoutes[RIO[Apis, *]] =
    ZHttp4sServerInterpreter()
      .from(
        SwaggerInterpreter().fromEndpoints[RIO[Apis, *]](
          List(
            ExampleReadEndpoint.charactersEndpoint
          ), "Example Rest API", "1.0")
      )
      .toRoutes

  val routes = ZHttp4sServerInterpreter()
    .from(
      List(
        ExampleReadEndpoint.charactersLogic.widen[Apis]
      )
    )
    .toRoutes <+> swaggerRoutes
}
