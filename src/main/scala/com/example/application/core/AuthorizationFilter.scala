package com.example.application.core

import com.example.application.constants.UnAuthorizedError

import zio._
import scala.annotation.meta.getter

trait AuthorizationFilter {
  def setToken(token: Option[String]): UIO[Unit]
  def getToken: UIO[Option[String]]
  def verify: IO[UnAuthorizedError.type, Boolean]
}

object AuthorizationFilter {

  val layer: ZLayer[Any, Nothing, AuthorizationFilter] = 
    ZLayer.scoped(
      FiberRef
        .make[Option[String]](None)
        .map { ref =>
          new AuthorizationFilter {
            def setToken(token: Option[String]): UIO[Unit] = ref.set(token)
            def getToken: UIO[Option[String]] = ref.get
            def verify: IO[UnAuthorizedError.type, Boolean] = for {
              token <- this.getToken
              r     <- token match {
                case Some(_) => ZIO.succeed(true)
                case None    => ZIO.fail(UnAuthorizedError)
              }
            } yield r
          }
        }
    )

}
