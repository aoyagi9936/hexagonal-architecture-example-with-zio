package com.example.application.core

import com.example.application.constants.UnAuthorizedError

import zio._
import scala.annotation.meta.getter

object AuthorizationFilter {

  trait Service {
    def setToken(token: Option[String]): UIO[Unit]
    def getToken: UIO[Option[String]]
    def verify: IO[UnAuthorizedError.type, Boolean]
  }

  val layer: ZLayer[Any, Nothing, Service] = 
    ZLayer.scoped(
      FiberRef
        .make[Option[String]](None)
        .map { ref =>
          new Service {
            def setToken(token: Option[String]): UIO[Unit] = ref.set(token)
            def getToken: UIO[Option[String]] = ref.get
            def verify: IO[UnAuthorizedError.type, Boolean] = for {
              token <- this.getToken
              r     <- token match {
                case Some(_) => ZIO.succeed(true)
                case None => ZIO.fail(UnAuthorizedError)
              }
            } yield r
          }
        }
    )

}
