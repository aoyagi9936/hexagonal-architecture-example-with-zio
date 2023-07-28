package com.example.application.core

import com.example.adapters.primary.graphql.GraphqlResolver
import com.example.adapters.primary.rest.RestResolver
import com.example.application.models.CharactersData._
import com.example.application.services._
import com.example.application.config.Configuration._
import com.example.adapters.primary.graphql.apis._
import com.example.adapters.primary.rest.apis._
import com.example.adapters.secondary.datastore._

import zio._

object AppContext {

  type GqlApp  = GraphqlResolver.Apis with ServerConfig with AuthorizationFilter.Service
  type RestApp = RestResolver.Apis with ServerConfig with AuthorizationFilter.Service

  private val graphqlServerConfig = ZLayer.fromZIO(ZIO.config[ServerConfig](GraphQLServerConfig.config))
  private val restServerConfig = ZLayer.fromZIO(ZIO.config[ServerConfig](RestServerConfig.config))

  def gqlLayer: ZLayer[Any, Throwable, GqlApp] = ZLayer.make[GqlApp](
    // Inbound
    CharactersApiLive.layer,

    // Application
    AuthorizationFilter.layer,
    graphqlServerConfig,
    CharactersService.layer,

    // Outbound
    CharactersRepositoryMock.layer
  )

  def restLayer: ZLayer[Any, Throwable, RestApp] = ZLayer.make[RestApp](
    // Inbound
    CharactersPublicApiLive.layer,

    // Application
    AuthorizationFilter.layer,
    restServerConfig,
    CharactersService.layer,

    // Outbound
    CharactersRepositoryMock.layer
  )

}
