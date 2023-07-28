package com.example.adapters.secondary.datastore

import com.example.ports.secondary.CharactersRepository
import com.example.application.constants._
import com.example.application.models.CharactersData._

import zio.{ ZIO, IO, ZLayer, ULayer }

final class CharactersRepositoryMock extends CharactersRepository {

  override def add(data: Character): IO[RepositoryError, CharacterId] = ???

  override def delete(id: CharacterId): IO[RepositoryError, Long] = ???

  override def getAll(): IO[RepositoryError, List[Character]] = ZIO.succeed(
    List(
      Character(CharacterId("100000"), "James Holden", List("Jim", "Hoss"), Origin.EARTH, Some(Role.Captain("Rocinante"))),
      Character(CharacterId("200000"), "Naomi Nagata", Nil, Origin.BELT, Some(Role.Engineer("Rocinante"))),
      Character(CharacterId("300000"), "Amos Burton", Nil, Origin.EARTH, Some(Role.Mechanic("Rocinante"))),
      Character(CharacterId("400000"), "Alex Kamal", Nil, Origin.MARS, Some(Role.Pilot("Rocinante"))),
      Character(CharacterId("500000"), "Chrisjen Avasarala", Nil, Origin.EARTH, None),
      Character(CharacterId("600000"), "Josephus Miller", List("Joe"), Origin.BELT, None),
      Character(CharacterId("700000"), "Roberta Draper", List("Bobbie", "Gunny"), Origin.MARS, None)
    )
  )

  override def getById(id: CharacterId): IO[RepositoryError, Option[Character]] = ???

  override def update(itemId: CharacterId, data: Character): IO[RepositoryError, Option[Unit]] = ???
}

object CharactersRepositoryMock {
  val layer: ULayer[CharactersRepository] = ZLayer.succeed(
    CharactersRepositoryMock()
  )
}
