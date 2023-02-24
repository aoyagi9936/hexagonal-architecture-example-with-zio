package com.example.adapters.primary.graphql.apis

import com.example.application.constants._
import com.example.application.core.AuthorizationFilter
import com.example.application.models.ExampleData._
import com.example.application.services.BizDomainA
import com.example.ports.primary.ExampleApi

import zio.{ZIO, IO, ZLayer}
import zio.stream.ZStream

object ExampleApiLive {

  type Env = AuthorizationFilter.Service with BizDomainA.Service

  val layer: ZLayer[Env, PrimaryError, ExampleApi] = ZLayer {
    for {
      authZ <- ZIO.service[AuthorizationFilter.Service]
      token <- authZ.getToken
      _     <- ZIO.log(s"""Authz Token: ${token.getOrElse("Token not found.")}""")
      svc   <- ZIO.service[BizDomainA.Service]
    } yield new ExampleApi {
      def getCharacters(origin: Option[Origin]): IO[PrimaryError, List[Character]] =
        svc.getCharacters(origin).mapError(_ => InternalServerError)
      def findCharacter(name: String): IO[PrimaryError, Option[Character]] =
        svc.findCharacter(name).mapError(_ => InternalServerError)
      def deleteCharacter(name: String): IO[PrimaryError, Boolean] =
        svc.deleteCharacter(name).mapError(_ => InternalServerError)
      def deletedEvents: ZStream[Any, Nothing, String] =
        svc.deletedEvents
    }
  }

}
