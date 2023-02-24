package com.example.application.config

import com.typesafe.config.ConfigFactory
import zio._
import zio.config._
import zio.config.ConfigSource._
import zio.config.ConfigDescriptor._
import zio.config.typesafe.TypesafeConfigSource
import com.example.application.config.Configuration.ServerConfig

object Configuration {

  final case class ServerConfig(host: String, port: Int)

  object ServerConfig {
    private val serverConfigDescription =
      nested("server-config") {
        string("host") zip int("port")
      }.to[ServerConfig]

    val layer = ZLayer(
      read(
        serverConfigDescription.from(
          TypesafeConfigSource.fromTypesafeConfig(
            ZIO.attempt(ConfigFactory.defaultApplication())
          )
        )
      )
    )
  }

}
