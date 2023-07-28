package com.example.adapters.primary.rest.apis

import com.example.application.constants._
import com.example.application.core.AuthorizationFilter
import com.example.application.models.CharactersData._
import com.example.application.services.CharactersService
import com.example.ports.primary.CharactersPublicApi

import zio.{ZIO, IO, ZLayer}
import zio.stream.ZStream

object CharactersPublicApiLive {

  val layer: ZLayer[AuthorizationFilter.Service & CharactersService, PrimaryError, CharactersPublicApi] = ZLayer {
    for {
      authZ <- ZIO.service[AuthorizationFilter.Service]
      token <- authZ.getToken
      _     <- ZIO.log(s"""Authz Token: ${token.getOrElse("Token not found.")}""")
      svc   <- ZIO.service[CharactersService]
    } yield new CharactersPublicApi {
      def getCharacters(origin: Option[Origin]): IO[PrimaryError, List[Character]] =
        svc.getCharacters(origin)
          .mapError(_ => InternalServerError)
      def findCharacter(name: String): IO[PrimaryError, Option[Character]] =
        svc.findCharacter(name)
          .mapError(_ => InternalServerError)
    }
  }

}
