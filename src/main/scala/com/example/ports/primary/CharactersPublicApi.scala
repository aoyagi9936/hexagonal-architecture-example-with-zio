package com.example.ports.primary

import com.example.application.constants.PrimaryError
import com.example.application.models.CharactersData._
import zio.{ IO, ZIO }

trait CharactersPublicApi:
  def getCharacters(origin: Option[Origin]): IO[PrimaryError, List[Character]]

  def findCharacter(id: CharacterId): IO[PrimaryError, Character]

  def addCharacter(name: String, nicknames: List[String], origin: Origin, role: Option[RoleReq]):  IO[PrimaryError, CharacterId]

object CharactersPublicApi:
  def getCharacters(origin: Option[Origin]): ZIO[CharactersPublicApi, PrimaryError, List[Character]] =
    ZIO.serviceWithZIO(_.getCharacters(origin))

  def findCharacter(id: CharacterId): ZIO[CharactersPublicApi, PrimaryError, Character] =
    ZIO.serviceWithZIO(_.findCharacter(id))

  def addCharacter(name: String, nicknames: List[String], origin: Origin, role: Option[RoleReq]):  ZIO[CharactersPublicApi, PrimaryError, CharacterId] =
   ZIO.serviceWithZIO(_.addCharacter(name, nicknames, origin, role))
