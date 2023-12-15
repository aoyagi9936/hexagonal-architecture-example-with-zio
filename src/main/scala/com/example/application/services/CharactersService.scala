package com.example.application.services

import com.example.application.constants._
import com.example.application.models.CharactersData._
import com.example.ports.secondary.CharactersRepository

import zio.stream.ZStream
import zio._

trait CharactersService {
  def getCharacters(origin: Option[Origin]): IO[DomainError, List[Character]]
  def findCharacter(id: CharacterId): IO[DomainError, Character]
  def addCharacter(data: Character): IO[DomainError, CharacterId]
  def updateCharacter(data: Character): IO[DomainError, Boolean]
  def deleteCharacter(id: CharacterId): IO[DomainError, Boolean]
  def deletedEvents: ZStream[Any, Nothing, String]
}

object CharactersService {

  class CharactersServiceImpl(charRepo: CharactersRepository) extends CharactersService {
    override def getCharacters(origin: Option[Origin]): IO[DomainError, List[Character]] =
      for {
        items <- origin match {
          case Some(o) => charRepo.filter(o)
              .mapError(e => CharactersServiceError())
          case None    => charRepo.getAll()
              .mapError(e => CharactersServiceError())
        }
    } yield items

    override def findCharacter(id: CharacterId): IO[DomainError, Character] =
      charRepo.getById(id).foldZIO(
        failure => ZIO.fail(CharactersServiceError()),
        success => success match {
          case Some(v) => ZIO.succeed(v)
          case None    => ZIO.fail(DataNotFoundError())
        }
      )

    override def addCharacter(data: Character): IO[DomainError, CharacterId] =
      charRepo.add(data).foldZIO(
        failure => failure match {
          case _: DuplicateDataError => ZIO.fail(DataConflictError())
          case _                     => ZIO.fail(CharactersServiceError())
        },
        success => ZIO.succeed(success),
      )

    override def updateCharacter(data: Character): IO[DomainError, Boolean] =
      charRepo.update(data.characterId, data).foldZIO(
        failure => ZIO.fail(CharactersServiceError()),
        success => success match {
          case Some(v) => ZIO.succeed(true)
          case None    => ZIO.fail(DataNotFoundError())
        }
      )

    override def deleteCharacter(id: CharacterId): IO[DomainError, Boolean] =
      charRepo.delete(id).foldZIO(
        failure => ZIO.fail(CharactersServiceError()),
        success => success match {
          case 1L => ZIO.succeed(true)
          case 0L => ZIO.fail(DataNotFoundError())
        }
      )

    override def deletedEvents: ZStream[Any, Nothing, String] = ???
  }

  def layer: ZLayer[CharactersRepository, Nothing, CharactersService] = ZLayer {
    for {
      charRepo <- ZIO.service[CharactersRepository]
    } yield CharactersServiceImpl(charRepo)
  }

}
