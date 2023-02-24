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

  val layer: ZLayer[Env, UnAuthorizedError, ExampleApi] = ZLayer {
    for {
      authZ <- ZIO.service[AuthorizationFilter.Service]
      token <- authZ.getToken
      _     <- ZIO.log(token.getOrElse(""))
      svc   <- ZIO.service[BizDomainA.Service]
    } yield new ExampleApi {
      def getCharacters(origin: Option[Origin]): IO[ResolverError, List[Character]] = svc.getCharacters(origin).mapError(e => ResolverError(e.cause))
      def findCharacter(name: String): IO[ResolverError, Option[Character]] = svc.findCharacter(name).mapError(e => ResolverError(e.cause))
      def deleteCharacter(name: String): IO[ResolverError, Boolean] = svc.deleteCharacter(name).mapError(e => ResolverError(e.cause))
      def deletedEvents: ZStream[Any, Nothing, String] = svc.deletedEvents
    }
  }

}
