package com.example.application.services

import com.example.application.constants._
import com.example.application.models.CharactersData._
import com.example.ports.secondary.CharactersRepository

import zio.stream.ZStream
import zio._

trait CharactersService {
  def getCharacters(origin: Option[Origin]): IO[DomainError, List[Character]]
  def findCharacter(id: CharacterId): IO[DomainError, Character]
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
          case None    => ZIO.fail(CharacterNotFoundError())
        }
      )

    override def deleteCharacter(id: CharacterId): IO[DomainError, Boolean] = ???
    override def deletedEvents: ZStream[Any, Nothing, String] = ???
  }

  def layer: ZLayer[CharactersRepository, Nothing, CharactersService] = ZLayer {
    for {
      charRepo <- ZIO.service[CharactersRepository]
    } yield CharactersServiceImpl(charRepo)
  }

}
