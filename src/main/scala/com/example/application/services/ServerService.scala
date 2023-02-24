package com.example.application.services

import com.example.adapters.primary.graphql.GraphqlApi
import com.example.application.models.ExampleData._
import com.example.application.services.ExampleService
import com.example.application.config.Configuration.ServerConfig
import com.example.adapters.secondary.postgres.ItemRepositoryMock

import zio.ZLayer

object ServerService {

  type Env = GraphqlApi.Env with ServerConfig 

  def layer: ZLayer[Any, Throwable, Env] = ZLayer.make[Env](
    ExampleService.make(sampleCharacters),
    //ExampleService.layer,
    ServerConfig.layer,
    //new ItemRepositoryMock().layer,
  )

}
