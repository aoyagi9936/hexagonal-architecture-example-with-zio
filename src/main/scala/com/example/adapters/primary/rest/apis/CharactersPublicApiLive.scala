package com.example.adapters.primary.rest.apis

import com.example.application.constants._
import com.example.application.core.AuthorizationFilter
import com.example.application.models.CharactersData._
import com.example.application.services.CharactersService
import com.example.ports.primary.CharactersPublicApi

import zio.{ZIO, IO, ZLayer}
import zio.stream.ZStream
import zio.uuid.*

object CharactersPublicApiLive {

  val layer: ZLayer[CharactersService & TypeIDGenerator, PrimaryError, CharactersPublicApi] = ZLayer {
    for {
      idGen <- ZIO.service[TypeIDGenerator]
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
              case _:DataNotFoundError => ZIO.fail(RestNotFoundError())
              case _                   => ZIO.fail(RestInternalServerError())
            },
            success => ZIO.succeed(success)
          )
      def addCharacter(name: String, nicknames: List[String], origin: Origin, role: Option[RoleReq]): IO[PrimaryError, CharacterId] =
        for {
          id <- idGen.typeid("pubchar")
          .mapError(_ => InternalServerError)
          c  <- ZIO.succeed(
            Character(
              CharacterId(id.value),
              name,
              nicknames,
              origin,
              role.map(v => Role.fromString(v.kind, v.shipName))
            )
          )
          r <- svc.addCharacter(c)
          .mapError(_ => InternalServerError)
        } yield r
    }
  }

}
