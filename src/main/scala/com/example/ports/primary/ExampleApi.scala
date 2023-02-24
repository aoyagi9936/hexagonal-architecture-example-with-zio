package com.example.ports.primary

import com.example.application.constants._
import com.example.application.models.ExampleData._
import zio.stream.ZStream
import zio.{ Hub, Ref, UIO, URIO, IO, ZIO, ZLayer }

trait ExampleApi:
    def getCharacters(origin: Option[Origin]): IO[ResolverError, List[Character]]

    def findCharacter(name: String): IO[ResolverError, Option[Character]]

    def deleteCharacter(name: String): IO[ResolverError, Boolean]

    def deletedEvents: ZStream[Any, Nothing, String]

object ExampleApi:
  def getCharacters(origin: Option[Origin]): ZIO[ExampleApi, ResolverError, List[Character]] =
    ZIO.serviceWithZIO(_.getCharacters(origin))

  def findCharacter(name: String): ZIO[ExampleApi, ResolverError, Option[Character]] =
    ZIO.serviceWithZIO(_.findCharacter(name))

  def deleteCharacter(name: String): ZIO[ExampleApi, ResolverError, Boolean] =
    ZIO.serviceWithZIO(_.deleteCharacter(name))

  def deletedEvents: ZStream[ExampleApi, Nothing, String] =
    ZStream.serviceWithStream(_.deletedEvents)
