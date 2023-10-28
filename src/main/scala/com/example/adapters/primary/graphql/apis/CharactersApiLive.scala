package com.example.adapters.primary.graphql.apis

import com.example.application.constants._
import com.example.application.core.AuthorizationFilter
import com.example.application.models.CharactersData._
import com.example.application.services.CharactersService
import com.example.ports.primary.CharactersApi

import zio.{ZIO, IO, ZLayer}
import zio.stream.ZStream

object CharactersApiLive {

  val layer: ZLayer[AuthorizationFilter & CharactersService, PrimaryError, CharactersApi] = ZLayer {
    for {
      authZ <- ZIO.service[AuthorizationFilter]
      svc   <- ZIO.service[CharactersService]
    } yield new CharactersApi {
      def getCharacters(origin: Option[Origin]): IO[PrimaryError, List[Character]] =
        svc.getCharacters(origin)
          .mapError(_ => InternalServerError)
      def findCharacter(id: CharacterId): IO[PrimaryError, Character] =
        svc.findCharacter(id)
          .mapError {
              case _:CharacterNotFoundError => NotFoundError
              case _:CharactersServiceError => InternalServerError
          }
      def deleteCharacter(id: CharacterId): IO[PrimaryError, Boolean] =
        svc.deleteCharacter(id)
          .mapError {
              case _:CharacterNotFoundError => NotFoundError
              case _:CharactersServiceError => InternalServerError
          }
      def deletedEvents: ZStream[Any, Nothing, String] =
        svc.deletedEvents
    }
  }

}
