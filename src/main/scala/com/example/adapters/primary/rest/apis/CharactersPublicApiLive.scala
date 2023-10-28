package com.example.adapters.primary.rest.apis

import com.example.application.constants._
import com.example.application.core.AuthorizationFilter
import com.example.application.models.CharactersData._
import com.example.application.services.CharactersService
import com.example.ports.primary.CharactersPublicApi

import zio.{ZIO, IO, ZLayer}
import zio.stream.ZStream

object CharactersPublicApiLive {

  val layer: ZLayer[CharactersService, PrimaryError, CharactersPublicApi] = ZLayer {
    for {
      svc   <- ZIO.service[CharactersService]
    } yield new CharactersPublicApi {
      def getCharacters(origin: Option[Origin]): IO[PrimaryError, List[Character]] =
        svc.getCharacters(origin)
          .foldZIO(
            error   => ZIO.fail(RestInternalServerError()),
            success => ZIO.succeed(success)
          )
      def findCharacter(id: CharacterId): IO[PrimaryError, Character] =
        svc.findCharacter(id)
          .foldZIO(
            error => error match {
              case _:CharacterNotFoundError => ZIO.fail(RestNotFoundError())
              case _:CharactersServiceError => ZIO.fail(RestInternalServerError())
            },
            success => ZIO.succeed(success)
          )
    }
  }

}
