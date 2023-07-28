package com.example.application.services

import com.example.application.constants._
import com.example.application.models.CharactersData._
import com.example.ports.secondary.CharactersRepository

import zio.stream.ZStream
import zio._

trait CharactersService {
  def getCharacters(origin: Option[Origin]): IO[CharactersServiceError, List[Character]]
  def findCharacter(name: String): IO[CharactersServiceError, Option[Character]]
  def deleteCharacter(name: String): IO[CharactersServiceError, Boolean]
  def deletedEvents: ZStream[Any, Nothing, String]
}

object CharactersService {

  case class CharactersServiceImpl(charRepo: CharactersRepository) extends CharactersService {
    override def getCharacters(origin: Option[Origin]): IO[CharactersServiceError, List[Character]] = for {
      items <- charRepo.getAll()
      .mapError(e => CharactersServiceError())
    } yield items

    override def findCharacter(name: String): IO[CharactersServiceError, Option[Character]] = ???
    override def deleteCharacter(name: String): IO[CharactersServiceError, Boolean] = ???
    override def deletedEvents: ZStream[Any, Nothing, String] = ???
  }

  def layer: ZLayer[CharactersRepository, Nothing, CharactersService] = ZLayer {
    for {
      charRepo <- ZIO.service[CharactersRepository]
    } yield CharactersServiceImpl(charRepo)
  }

}
