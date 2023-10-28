package com.example.adapters.primary.rest.endpoints

import sttp.tapir._
import sttp.tapir.ztapir._
import sttp.tapir.json.zio._
import sttp.tapir.Codec.PlainCodec

import zio.json._

import com.example.application.constants._
import com.example.application.models.CharactersData._

object TapirSupport extends Tapir
    with ZTapir
    with TapirJsonZio {

  given JsonCodec[Origin]  = DeriveJsonCodec.gen
  given PlainCodec[Origin] = Codec.derivedEnumeration[String, Origin].defaultStringBased
  given JsonCodec[Role]    = DeriveJsonCodec.gen

  given JsonDecoder[CharacterId] = JsonDecoder[String].map(CharacterId(_))
  given JsonEncoder[CharacterId] = JsonEncoder[String].contramap(_.value)
  given JsonCodec[Character]     = DeriveJsonCodec.gen
  given JsonCodec[RestInternalServerError] = DeriveJsonCodec.gen
  given JsonCodec[RestNotFoundError] = DeriveJsonCodec.gen

  given Schema[Origin]      = Schema.derivedEnumeration.defaultStringBased
  given Schema[Role]        = Schema.derived
  given Schema[CharacterId] = Schema.string
  given Schema[Character]   = Schema.derived
  given Schema[RestInternalServerError] = Schema.derived
  given Schema[RestNotFoundError]       = Schema.derived

  val originMaybeQuery: EndpointInput.Query[Option[Origin]] =
    query[Option[Origin]]("origin")

  val characterIdQuery: EndpointInput.Query[CharacterId] =
    query[CharacterId]("id")

}

