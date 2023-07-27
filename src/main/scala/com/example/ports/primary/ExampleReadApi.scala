package com.example.ports.primary

import com.example.application.constants.PrimaryError
import com.example.application.models.ExampleData._
import zio.stream.ZStream
import zio.{ IO, ZIO }

trait ExampleReadApi:
    def getCharacters(origin: Option[Origin]): IO[PrimaryError, List[Character]]

    def findCharacter(name: String): IO[PrimaryError, Option[Character]]

object ExampleReadApi:
  def getCharacters(origin: Option[Origin]): ZIO[ExampleReadApi, PrimaryError, List[Character]] =
    ZIO.serviceWithZIO(_.getCharacters(origin))

  def findCharacter(name: String): ZIO[ExampleReadApi, PrimaryError, Option[Character]] =
    ZIO.serviceWithZIO(_.findCharacter(name))
