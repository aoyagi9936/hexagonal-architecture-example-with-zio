package com.example.application.config

import com.typesafe.config.ConfigFactory
import zio._

object Configuration {

  final case class ServerConfig(host: String, port: Int)

  object GraphQLServerConfig {
    val config: Config[ServerConfig] =
      (Config.string.nested("host") ++
        Config.int.nested("port"))
        .map { case (host, port) =>
          ServerConfig(host, port)
        }
        .nested("graphql-server-config")
  }

  object RestServerConfig {
    val config: Config[ServerConfig] =
      (Config.string.nested("host") ++
        Config.int.nested("port"))
        .map { case (host, port) =>
          ServerConfig(host, port)
        }
        .nested("rest-server-config")
  }

}
