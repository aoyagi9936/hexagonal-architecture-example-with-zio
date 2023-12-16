package com.example.adapters.primary.graphql.apis

import com.example.application.constants._
import com.example.application.core.AuthorizationFilter
import com.example.application.models.CharactersData._
import com.example.application.services.CharactersService
import com.example.ports.primary.CharactersApi

import zio.{ZIO, IO, ZLayer, Random}
import zio.stream.ZStream
import zio.uuid.*

object CharactersApiLive {

  val layer: ZLayer[AuthorizationFilter & CharactersService & TypeIDGenerator, PrimaryError, CharactersApi] = ZLayer {
    for {
      idGen <- ZIO.service[TypeIDGenerator]
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
      def addCharacter(name: String, nicknames: List[String], origin: Origin, role: Option[RoleReq]): IO[PrimaryError, CharacterId] =
        for {
          id <- idGen.typeid("char")
          .mapError(_ => InternalServerError)
          c  <- ZIO.attempt(
            Character(
              CharacterId(id.value),
              name,
              nicknames,
              origin,
              role.map(v => Role.fromString(v.kind, v.shipName))
            )
          ).catchAll {
            case e: MatchError => ZIO.fail(RoleBadRequestError)
          }
          r <- svc.addCharacter(c)
          .mapError(_ => InternalServerError)
        } yield r

      def updateCharacter(id: CharacterId, name: String, nicknames: List[String], origin: Origin, role: Option[RoleReq]): IO[PrimaryError, Boolean] =
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
