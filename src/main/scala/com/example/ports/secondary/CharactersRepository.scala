package com.example.ports.secondary

import com.example.application.constants.SecondaryError
import com.example.application.models.CharactersData._

import zio._

trait CharactersRepository:
  def add(data: Character): IO[SecondaryError, CharacterId]

  def delete(id: CharacterId): IO[SecondaryError, Long]

  def getAll(): IO[SecondaryError, List[Character]]

  def filter(origin: Origin): IO[SecondaryError, List[Character]]

  def getById(id: CharacterId): IO[SecondaryError, Option[Character]]

  def update(itemId: CharacterId, data: Character): IO[SecondaryError, Option[Unit]]

object CharactersRepository:
  def add(data: Character): ZIO[CharactersRepository, SecondaryError, CharacterId] =
    ZIO.serviceWithZIO[CharactersRepository](_.add(data))

  def delete(id: CharacterId): ZIO[CharactersRepository, SecondaryError, Long] =
    ZIO.serviceWithZIO[CharactersRepository](_.delete(id))

  def getAll(): ZIO[CharactersRepository, SecondaryError, List[Character]] =
    ZIO.serviceWithZIO[CharactersRepository](_.getAll())

  def filter(origin: Origin): ZIO[CharactersRepository, SecondaryError, List[Character]] =
    ZIO.serviceWithZIO[CharactersRepository](_.filter(origin))

  def getById(id: CharacterId): ZIO[CharactersRepository, SecondaryError, Option[Character]] =
    ZIO.serviceWithZIO[CharactersRepository](_.getById(id))

  def update(itemId: CharacterId, data: Character): ZIO[CharactersRepository, SecondaryError, Option[Unit]] =
    ZIO.serviceWithZIO[CharactersRepository](_.update(itemId, data))
