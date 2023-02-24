package com.example.application.services

import com.example.application.constants._
import com.example.application.models.ExampleData._
import zio.stream.ZStream
import zio.{ Hub, Ref, UIO, URIO, IO, ZIO, ZLayer }
import com.example.ports.secondary.ItemRepository

object BizDomainA {

  trait Service {
    def getCharacters(origin: Option[Origin]): IO[BizDomainError, List[Character]]
    def findCharacter(name: String): IO[BizDomainError, Option[Character]]
    def deleteCharacter(name: String): IO[BizDomainError, Boolean]
    def deletedEvents: ZStream[Any, Nothing, String]
  }

  case class BizDomainImpl(itemRepo: ItemRepository) extends Service {
    override def getCharacters(origin: Option[Origin]): IO[BizDomainError, List[Character]] = for {
      items <- itemRepo.getAll().mapError(e => BizDomainError(e.cause))
      chars <- ZIO.succeed(items.map(v => Character(v.name, List.empty[String], Origin.EARTH, None)))
    } yield chars

      override def findCharacter(name: String): IO[BizDomainError, Option[Character]] = ???

      override def deleteCharacter(name: String): IO[BizDomainError, Boolean] = ???

      override def deletedEvents: ZStream[Any, Nothing, String] = ???
  }

  def layer: ZLayer[ItemRepository, Nothing, Service] = ZLayer {
    for {
      itemRepo <- ZIO.service[ItemRepository]
    } yield BizDomainImpl(itemRepo)
  }

  // def make(initial: List[Character]): ZLayer[Any, Nothing, ExampleServiceApi] = ZLayer {
  //   for {
  //     characters  <- Ref.make(initial)
  //     subscribers <- Hub.unbounded[String]
  //   } yield new ExampleServiceApi {

  //     def getCharacters(origin: Option[Origin]): UIO[List[Character]] =
  //       characters.get.map(_.filter(c => origin.forall(c.origin == _)))

  //     def findCharacter(name: String): UIO[Option[Character]] = characters.get.map(_.find(c => c.name == name))

  //     def deleteCharacter(name: String): UIO[Boolean] =
  //       characters
  //         .modify(list =>
  //           if (list.exists(_.name == name)) (true, list.filterNot(_.name == name))
  //           else (false, list)
  //         )
  //         .tap(deleted => ZIO.when(deleted)(subscribers.publish(name)))

  //     def deletedEvents: ZStream[Any, Nothing, String] =
  //       ZStream.scoped(subscribers.subscribe).flatMap(ZStream.fromQueue(_))
  //   }
  // }
}
