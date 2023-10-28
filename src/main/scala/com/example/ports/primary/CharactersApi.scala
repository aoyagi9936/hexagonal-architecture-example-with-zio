package com.example.ports.primary

import com.example.application.constants.PrimaryError
import com.example.application.models.CharactersData._
import zio.stream.ZStream
import zio.{ IO, ZIO }

trait CharactersApi:
    def getCharacters(origin: Option[Origin]): IO[PrimaryError, List[Character]]

    def findCharacter(id: CharacterId): IO[PrimaryError, Character]

    def deleteCharacter(id: CharacterId): IO[PrimaryError, Boolean]

    def deletedEvents: ZStream[Any, Nothing, String]

object CharactersApi:
  def getCharacters(origin: Option[Origin]): ZIO[CharactersApi, PrimaryError, List[Character]] =
    ZIO.serviceWithZIO(_.getCharacters(origin))

  def findCharacter(id: CharacterId): ZIO[CharactersApi, PrimaryError, Character] =
    ZIO.serviceWithZIO(_.findCharacter(id))

  def deleteCharacter(id: CharacterId): ZIO[CharactersApi, PrimaryError, Boolean] =
    ZIO.serviceWithZIO(_.deleteCharacter(id))

  def deletedEvents: ZStream[CharactersApi, Nothing, String] =
    ZStream.serviceWithStream(_.deletedEvents)
