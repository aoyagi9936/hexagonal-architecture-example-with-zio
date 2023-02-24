package com.example.application.services

import com.example.application.constants._
import com.example.application.models.ExampleData._
import com.example.ports.primary.ExampleServiceApi
import zio.stream.ZStream
import zio.{ Hub, Ref, UIO, URIO, IO, ZIO, ZLayer }
import com.example.ports.secondary.ItemRepository

object ExampleService {

  def layer: ZLayer[ItemRepository, Nothing, ExampleServiceApi] = ZLayer {
    for {
      repo <- ZIO.service[ItemRepository]
    } yield new ExampleServiceApi {

      def getCharacters(origin: Option[Origin]): UIO[List[Character]] = ???

      def findCharacter(name: String): UIO[Option[Character]] = ???

      def deleteCharacter(name: String): UIO[Boolean] = ???

      def deletedEvents: ZStream[Any, Nothing, String] = ???

    }
  }

  def make(initial: List[Character]): ZLayer[Any, Nothing, ExampleServiceApi] = ZLayer {
    for {
      characters  <- Ref.make(initial)
      subscribers <- Hub.unbounded[String]
    } yield new ExampleServiceApi {

      def getCharacters(origin: Option[Origin]): UIO[List[Character]] =
        characters.get.map(_.filter(c => origin.forall(c.origin == _)))

      def findCharacter(name: String): UIO[Option[Character]] = characters.get.map(_.find(c => c.name == name))

      def deleteCharacter(name: String): UIO[Boolean] =
        characters
          .modify(list =>
            if (list.exists(_.name == name)) (true, list.filterNot(_.name == name))
            else (false, list)
          )
          .tap(deleted => ZIO.when(deleted)(subscribers.publish(name)))

      def deletedEvents: ZStream[Any, Nothing, String] =
        ZStream.scoped(subscribers.subscribe).flatMap(ZStream.fromQueue(_))
    }
  }
}
