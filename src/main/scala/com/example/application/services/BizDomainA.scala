package com.example.application.services

import com.example.application.constants._
import com.example.application.models.ExampleData._
import zio.stream.ZStream
import zio.{ Hub, Ref, UIO, URIO, IO, ZIO, ZLayer }
import com.example.ports.secondary.ItemRepository

object BizDomainA {

  trait Service {
    def getCharacters(origin: Option[Origin]): IO[BizDomainAError, List[Character]]
    def findCharacter(name: String): IO[BizDomainAError, Option[Character]]
    def deleteCharacter(name: String): IO[BizDomainAError, Boolean]
    def deletedEvents: ZStream[Any, Nothing, String]
  }

  case class BizDomainImpl(itemRepo: ItemRepository) extends Service {
    override def getCharacters(origin: Option[Origin]): IO[BizDomainAError, List[Character]] = for {
      items <- itemRepo.getAll().mapError(e => BizDomainAError())
      chars <- ZIO.succeed(items.map(v => Character(v.name, List.empty[String], Origin.EARTH, None)))
    } yield chars

      override def findCharacter(name: String): IO[BizDomainAError, Option[Character]] = ???

      override def deleteCharacter(name: String): IO[BizDomainAError, Boolean] = ???

      override def deletedEvents: ZStream[Any, Nothing, String] = ???
  }

  def layer: ZLayer[ItemRepository, Nothing, Service] = ZLayer {
    for {
      itemRepo <- ZIO.service[ItemRepository]
    } yield BizDomainImpl(itemRepo)
  }

}
