package com.example.ports.primary

import com.example.application.constants.PrimaryError
import com.example.application.models.CharactersData._
import zio.stream.ZStream
import zio.{ IO, ZIO }

trait CharactersApi:
    def getCharacters(origin: Option[Origin]): IO[PrimaryError, List[Character]]

    def findCharacter(name: String): IO[PrimaryError, Option[Character]]

    def deleteCharacter(name: String): IO[PrimaryError, Boolean]

    def deletedEvents: ZStream[Any, Nothing, String]

object CharactersApi:
  def getCharacters(origin: Option[Origin]): ZIO[CharactersApi, PrimaryError, List[Character]] =
    ZIO.serviceWithZIO(_.getCharacters(origin))

  def findCharacter(name: String): ZIO[CharactersApi, PrimaryError, Option[Character]] =
    ZIO.serviceWithZIO(_.findCharacter(name))

  def deleteCharacter(name: String): ZIO[CharactersApi, PrimaryError, Boolean] =
    ZIO.serviceWithZIO(_.deleteCharacter(name))

  def deletedEvents: ZStream[CharactersApi, Nothing, String] =
    ZStream.serviceWithStream(_.deletedEvents)
