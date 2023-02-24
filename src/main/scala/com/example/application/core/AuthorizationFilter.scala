package com.example.application.core

import com.example.application.constants.UnAuthorizedError

import zio._

object AuthorizationFilter {

  trait Service {
    def setToken(token: Option[String]): UIO[Unit]
    def getToken: UIO[Option[String]]
    def check: IO[UnAuthorizedError, Boolean]
  }

  val layer: ZLayer[Any, Nothing, Service] = 
    ZLayer.scoped(
      FiberRef
        .make[Option[String]](None)
        .map { ref =>
          new Service {
            def setToken(token: Option[String]): UIO[Unit] = ref.set(token)
            def getToken: UIO[Option[String]] = ref.get
            def check: IO[UnAuthorizedError, Boolean] = ???
          }
        }
    )

}
