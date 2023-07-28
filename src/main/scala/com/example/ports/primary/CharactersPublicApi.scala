package com.example.ports.primary

import com.example.application.constants.PrimaryError
import com.example.application.models.CharactersData._
import zio.stream.ZStream
import zio.{ IO, ZIO }

trait CharactersPublicApi:
    def getCharacters(origin: Option[Origin]): IO[PrimaryError, List[Character]]

    def findCharacter(name: String): IO[PrimaryError, Option[Character]]

object CharactersPublicApi:
  def getCharacters(origin: Option[Origin]): ZIO[CharactersPublicApi, PrimaryError, List[Character]] =
    ZIO.serviceWithZIO(_.getCharacters(origin))

  def findCharacter(name: String): ZIO[CharactersPublicApi, PrimaryError, Option[Character]] =
    ZIO.serviceWithZIO(_.findCharacter(name))
