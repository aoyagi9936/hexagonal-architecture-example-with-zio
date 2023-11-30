package com.example.application.core

import com.example.adapters.primary.graphql.GraphqlResolver
import com.example.adapters.primary.rest.RestResolver
import com.example.application.models.CharactersData._
import com.example.application.services._
import com.example.application.config.Configuration._
import com.example.adapters.primary.graphql.apis._
import com.example.adapters.primary.rest.apis._
import com.example.adapters.secondary.datastore.postgresql._

import zio._
import io.getquill.jdbczio.Quill
import io.getquill.SnakeCase

object AppContext {

  type GqlApp  = ServerConfig & AuthorizationFilter & GraphqlResolver.Apis
  type RestApp = ServerConfig & RestResolver.Apis

  private val graphqlServerConfig = ZLayer.fromZIO(ZIO.config[ServerConfig](GraphQLServerConfig.config))
  private val restServerConfig    = ZLayer.fromZIO(ZIO.config[ServerConfig](RestServerConfig.config))
  private val dataSourceLayer     = Quill.DataSource.fromPrefix("postgres-db")
  private val postgresLayer       = Quill.Postgres.fromNamingStrategy(SnakeCase)

  def gqlLayer: ZLayer[Any, Throwable, GqlApp] = ZLayer.make[GqlApp](
    // Inbound
    CharactersApiLive.layer,

    // Application
    AuthorizationFilter.layer,
    graphqlServerConfig,
    CharactersService.layer,
    // dataSourceLayer,
    // postgresLayer,

    // Outbound
    CharactersRepositoryMock.layer,
    // CharactersRepositoryLive.layer,
  )

  def restLayer: ZLayer[Any, Throwable, RestApp] = ZLayer.make[RestApp](
    // Inbound
    CharactersPublicApiLive.layer,

    // Application
    restServerConfig,
    CharactersService.layer,
    // dataSourceLayer,
    // postgresLayer,

    // Outbound
    CharactersRepositoryMock.layer,
    // CharactersRepositoryLive.layer,
  )

}
