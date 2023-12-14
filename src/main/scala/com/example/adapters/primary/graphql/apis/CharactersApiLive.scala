package com.example.adapters.primary.graphql.apis

import com.example.application.constants._
import com.example.application.core.AuthorizationFilter
import com.example.application.models.CharactersData._
import com.example.application.services.CharactersService
import com.example.ports.primary.CharactersApi
import com.example.adapters.primary.graphql.schemas.RoleArg

import zio.{ZIO, IO, ZLayer, Random}
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
              case _:DataNotFoundError => NotFoundError
              case _                   => InternalServerError
          }
      def addCharacter(name: String, nicknames: List[String], origin: Origin, role: Option[RoleArg]): IO[PrimaryError, CharacterId] =
        for {
          id <- Random.nextUUID
          c  <- ZIO.succeed(
            Character(
              CharacterId(id.toString()),
              name,
              nicknames,
              origin,
              role.map(v => Role.fromString(v.kind, v.shipName))
            )
          )
          r <- svc.addCharacter(c)
          .mapError(_ => InternalServerError)
        } yield r

      def updateCharacter(id: CharacterId, name: String, nicknames: List[String], origin: Origin, role: Option[RoleArg]): IO[PrimaryError, Boolean] =
        svc.updateCharacter(Character(
          id,
          name,
          nicknames,
          origin,
          role.map(v => Role.fromString(v.kind, v.shipName))
        ))
          .mapError {
              case _:DataNotFoundError => NotFoundError
              case _                   => InternalServerError
          }
      def deleteCharacter(id: CharacterId): IO[PrimaryError, Boolean] =
        svc.deleteCharacter(id)
          .mapError {
              case _:DataNotFoundError => NotFoundError
              case _                   => InternalServerError
          }
      def deletedEvents: ZStream[Any, Nothing, String] =
        svc.deletedEvents
    }
  }

}
