package com.example.ports.primary

import com.example.application.constants.PrimaryError
import com.example.application.models.ExampleData._
import zio.stream.ZStream
import zio.{ IO, ZIO }

trait ExampleApi:
    def getCharacters(origin: Option[Origin]): IO[PrimaryError, List[Character]]

    def findCharacter(name: String): IO[PrimaryError, Option[Character]]

    def deleteCharacter(name: String): IO[PrimaryError, Boolean]

    def deletedEvents: ZStream[Any, Nothing, String]

object ExampleApi:
  def getCharacters(origin: Option[Origin]): ZIO[ExampleApi, PrimaryError, List[Character]] =
    ZIO.serviceWithZIO(_.getCharacters(origin))

  def findCharacter(name: String): ZIO[ExampleApi, PrimaryError, Option[Character]] =
    ZIO.serviceWithZIO(_.findCharacter(name))

  def deleteCharacter(name: String): ZIO[ExampleApi, PrimaryError, Boolean] =
    ZIO.serviceWithZIO(_.deleteCharacter(name))

  def deletedEvents: ZStream[ExampleApi, Nothing, String] =
    ZStream.serviceWithStream(_.deletedEvents)
