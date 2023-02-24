package com.example.application.core

import com.example.adapters.primary.graphql.GraphqlResolver
import com.example.application.models.ExampleData._
import com.example.application.services.BizDomainA
import com.example.application.config.Configuration.ServerConfig
import com.example.adapters.primary.graphql.apis._
import com.example.adapters.secondary.postgres.ItemRepositoryMock

import zio.{Scope, ZLayer}

object AppContext {

  type GqlEnv = GraphqlResolver.Env with ServerConfig with AuthorizationFilter.Service

  def gqlLayer: ZLayer[Any, Throwable, GqlEnv] = ZLayer.make[GqlEnv](
    // Core
    AuthorizationFilter.layer,
    ServerConfig.layer,

    // Primary
    ExampleApiLive.layer,

    // Domain
    BizDomainA.layer,

    // Secondary
    new ItemRepositoryMock().layer

  )

}
