package com.example.ports.primary

import com.example.application.constants.PrimaryError
import com.example.application.models.CharactersData._
import zio.stream.ZStream
import zio.{ IO, ZIO }

trait CharactersApi:
  def getCharacters(origin: Option[Origin]): IO[PrimaryError, List[Character]]

  def findCharacter(id: CharacterId): IO[PrimaryError, Character]

  def addCharacter(name: String, nicknames: List[String], origin: Origin, role: Option[RoleReq]):  IO[PrimaryError, CharacterId]

  def updateCharacter(id: CharacterId, name: String, nicknames: List[String], origin: Origin, role: Option[RoleReq]):  IO[PrimaryError, Boolean]

  def deleteCharacter(id: CharacterId): IO[PrimaryError, Boolean]

  def deletedEvents: ZStream[Any, Nothing, String]

object CharactersApi:
  def getCharacters(origin: Option[Origin]): ZIO[CharactersApi, PrimaryError, List[Character]] =
    ZIO.serviceWithZIO(_.getCharacters(origin))

  def findCharacter(id: CharacterId): ZIO[CharactersApi, PrimaryError, Character] =
    ZIO.serviceWithZIO(_.findCharacter(id))

  def addCharacter(name: String, nicknames: List[String], origin: Origin, role: Option[RoleReq]):  ZIO[CharactersApi, PrimaryError, CharacterId] =
   ZIO.serviceWithZIO(_.addCharacter(name, nicknames, origin, role))

  def updateCharacter(id: CharacterId, name: String, nicknames: List[String], origin: Origin, role: Option[RoleReq]):  ZIO[CharactersApi, PrimaryError, Boolean] =
    ZIO.serviceWithZIO(_.updateCharacter(id, name, nicknames, origin, role))

  def deleteCharacter(id: CharacterId): ZIO[CharactersApi, PrimaryError, Boolean] =
    ZIO.serviceWithZIO(_.deleteCharacter(id))

  def deletedEvents: ZStream[CharactersApi, Nothing, String] =
    ZStream.serviceWithStream(_.deletedEvents)
