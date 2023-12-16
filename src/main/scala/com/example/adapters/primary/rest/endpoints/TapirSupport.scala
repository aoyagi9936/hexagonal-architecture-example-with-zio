package com.example.adapters.primary.rest.endpoints

import sttp.tapir._
import sttp.tapir.ztapir._
import sttp.tapir.json.zio._
import sttp.tapir.Codec.PlainCodec

import zio.json._

import com.example.application.constants._
import com.example.application.models.CharactersData._

import enumeratum._

object TapirSupport extends Tapir
    with ZTapir
    with TapirJsonZio {

  given PlainCodec[Origin]  = Codec.derivedEnumeration[String, Origin].defaultStringBased
  given JsonDecoder[Origin] = JsonDecoder[String].map(Origin.withName(_))
  given JsonEncoder[Origin] = JsonEncoder[String].contramap(_.toString)
  given JsonCodec[Role]     = DeriveJsonCodec.gen
  given JsonDecoder[CharacterId] = JsonDecoder[String].map(CharacterId(_))
  given JsonEncoder[CharacterId] = JsonEncoder[String].contramap(_.value)
  given JsonCodec[Character]     = DeriveJsonCodec.gen
  given JsonCodec[RoleReq]          = DeriveJsonCodec.gen
  given JsonCodec[AddCharacterArgs] = DeriveJsonCodec.gen

  given JsonEncoder[ErrorJson]                = JsonEncoder.derived
  given JsonDecoder[ForbiddenError.type]      = JsonDecoder.derived
  given JsonEncoder[ForbiddenError.type]      = JsonEncoder[ErrorJson].contramap(e => ErrorJson(e.code, e.message))
  given JsonDecoder[UnAuthorizedError.type]   = JsonDecoder.derived
  given JsonEncoder[UnAuthorizedError.type]   = JsonEncoder[ErrorJson].contramap(e => ErrorJson(e.code, e.message))
  given JsonDecoder[RoleBadRequestError.type] = JsonDecoder.derived
  given JsonEncoder[RoleBadRequestError.type] = JsonEncoder[ErrorJson].contramap(e => ErrorJson(e.code, e.message))
  given JsonDecoder[NotFoundError.type]       = JsonDecoder.derived
  given JsonEncoder[NotFoundError.type]       = JsonEncoder[ErrorJson].contramap(e => ErrorJson(e.code, e.message))
  given JsonDecoder[InternalServerError.type] = JsonDecoder.derived
  given JsonEncoder[InternalServerError.type] = JsonEncoder[ErrorJson].contramap(e => ErrorJson(e.code, e.message))

  given Schema[Origin]      = Schema.derivedEnumeration.defaultStringBased
  given Schema[Role]        = Schema.derived
  given Schema[CharacterId] = Schema.string
  given Schema[Character]   = Schema.derived
  given Schema[RoleReq]     = Schema.derived
  given Schema[AddCharacterArgs] = Schema.derived

  given Schema[ForbiddenError.type]      = Schema.derived
  given Schema[UnAuthorizedError.type]   = Schema.derived
  given Schema[RoleBadRequestError.type] = Schema.derived
  given Schema[NotFoundError.type]       = Schema.derived
  given Schema[InternalServerError.type] = Schema.derived

  val originMaybeQuery: EndpointInput.Query[Option[Origin]] =
    query[Option[Origin]]("origin")

  val characterIdQuery: EndpointInput.Query[CharacterId] =
    query[CharacterId]("id")

  final case class AddCharacterArgs(
    name: String, nicknames: List[String], origin: Origin, role: Option[RoleReq])

}

