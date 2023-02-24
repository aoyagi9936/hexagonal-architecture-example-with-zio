package com.example.ports.primary

import com.example.application.constants._
import com.example.application.models.ExampleData._
import zio.stream.ZStream
import zio.{ Hub, Ref, UIO, URIO, IO, ZIO, ZLayer }

trait ExampleServiceApi:
    def getCharacters(origin: Option[Origin]): UIO[List[Character]]

    def findCharacter(name: String): UIO[Option[Character]]

    def deleteCharacter(name: String): UIO[Boolean]

    def deletedEvents: ZStream[Any, Nothing, String]

object ExampleServiceApi:
  def getCharacters(origin: Option[Origin]): URIO[ExampleServiceApi, List[Character]] =
    ZIO.serviceWithZIO(_.getCharacters(origin))

  def findCharacter(name: String): URIO[ExampleServiceApi, Option[Character]] =
    ZIO.serviceWithZIO(_.findCharacter(name))

  def deleteCharacter(name: String): URIO[ExampleServiceApi, Boolean] =
    ZIO.serviceWithZIO(_.deleteCharacter(name))

  def deletedEvents: ZStream[ExampleServiceApi, Nothing, String] =
    ZStream.serviceWithStream(_.deletedEvents)
