package com.example.adapters.primary.rest.endpoints

import sttp.tapir._
import sttp.tapir.ztapir._
import sttp.tapir.json.zio._
import sttp.capabilities.WebSockets
import sttp.capabilities.zio.ZioStreams
import zio.json._
import sttp.tapir.server.http4s.ztapir.ZHttp4sServerInterpreter
import sttp.tapir.swagger.bundle.SwaggerInterpreter

import com.example.application.constants._
import com.example.application.models.ExampleData._

object TapirSupport extends Tapir
    with ZTapir
    with TapirJsonZio {

  given JsonCodec[Origin] = DeriveJsonCodec.gen[Origin]
  given JsonCodec[Role] = DeriveJsonCodec.gen[Role]
  given JsonCodec[Character] = DeriveJsonCodec.gen[Character]
  given JsonCodec[RestInternalServerError] = DeriveJsonCodec.gen[RestInternalServerError]

  given Schema[Origin] = Schema.derived[Origin]
  given Schema[Role] = Schema.derived[Role]
  given Schema[Character] = Schema.derived[Character]
  given Schema[RestInternalServerError] = Schema.derived[RestInternalServerError]

  val originMaybeQuery: EndpointInput.Query[Option[Origin]] =
    jsonQuery[Option[Origin]]("origin")

}
