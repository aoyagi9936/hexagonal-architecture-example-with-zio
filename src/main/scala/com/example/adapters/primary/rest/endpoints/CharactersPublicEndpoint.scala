package com.example.adapters.primary.rest.endpoints

import org.http4s._

import sttp.tapir.PublicEndpoint
import sttp.tapir.server.http4s.ztapir.ZHttp4sServerInterpreter
import sttp.model.StatusCode

import com.example.ports.primary.CharactersPublicApi
import com.example.application.models.CharactersData._
import com.example.application.constants._
import TapirSupport.{given, _}

import zio._

object CharactersPublicEndpoint {

  type Apis = CharactersPublicApi

  val baseEndpoint: PublicEndpoint[Unit, PrimaryError, Unit, Any] =
    endpoint
      .in("api" / "v1")
      .errorOut(
        oneOf[PrimaryError](
          oneOfVariant(
            statusCode(StatusCode.NotFound)
              .and(jsonBody[RestNotFoundError])
          ),
          oneOfVariant(
            statusCode(StatusCode.InternalServerError)
              .and(jsonBody[RestInternalServerError])
          )
        )
      )

  val charactersEndpoint: PublicEndpoint[Option[Origin], PrimaryError, List[Character], Any] =
    baseEndpoint.get
      .in("characters" / "list")
      .in(originMaybeQuery)
      .out(jsonBody[List[Character]])

  val characterEndpoint: PublicEndpoint[CharacterId, PrimaryError, Character, Any] =
    baseEndpoint.get
      .in("character" / "get")
      .in(characterIdQuery)
      .out(jsonBody[Character])

  val charactersLogic = charactersEndpoint.zServerLogic {
    origin => CharactersPublicApi.getCharacters(origin)
  }

  val characterLogic = characterEndpoint.zServerLogic {
    id => CharactersPublicApi.findCharacter(id)
  }

}
