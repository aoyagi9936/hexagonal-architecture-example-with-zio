package com.example.adapters.secondary.datastore.postgresql

import com.example.ports.secondary.CharactersRepository
import com.example.application.constants._
import com.example.application.models.CharactersData._

import zio.{ ZIO, IO, ZLayer, ULayer, Ref }

final class CharactersRepositoryMock(
  storeRef: Ref[Map[CharacterId, Character]]
) extends CharactersRepository {

  override def add(data: Character): IO[SecondaryError, CharacterId] =
    for {
      store <- storeRef.get
      _ <- ZIO.when(store.contains(data.characterId))(ZIO.fail(DuplicateDataError()))
      _ <- storeRef.update(store => store + (data.characterId -> data))
    } yield data.characterId

  override def delete(id: CharacterId): IO[SecondaryError, Long] =
    storeRef.modify { store =>
      if (!store.contains(id)) (0L, store)
      else (1L, store.removed(id))
    }

  override def getAll(): IO[SecondaryError, List[Character]] =
    storeRef.get map { store =>
      store.toList.map(kv => kv._2)
    }

  override def filter(origin: Origin): IO[SecondaryError, List[Character]] =
    for {
      store <- storeRef.get
      list  <- ZIO.succeed(
        store.toList.map(kv => kv._2).filter(c => c.origin == origin)
      )
    } yield list

  override def getById(id: CharacterId): IO[SecondaryError, Option[Character]] =
    for {
      store <- storeRef.get
      maybe = store.get(id)
    } yield maybe

  override def update(id: CharacterId, data: Character): IO[SecondaryError, Option[Unit]] =
    storeRef.modify { store =>
      if (!store.contains(id)) (None, store)
      else (Some(()), store.updated(id, data))
    }
}

object CharactersRepositoryMock {

  private val data = List(
    Character(CharacterId("100000"), "James Holden", List("Jim", "Hoss"), Origin.EARTH, Some(Role.Captain("Rocinante"))),
    Character(CharacterId("200000"), "Naomi Nagata", Nil, Origin.BELT, Some(Role.Engineer("Rocinante"))),
    Character(CharacterId("300000"), "Amos Burton", Nil, Origin.EARTH, Some(Role.Mechanic("Rocinante"))),
    Character(CharacterId("400000"), "Alex Kamal", Nil, Origin.MARS, Some(Role.Pilot("Rocinante"))),
    Character(CharacterId("500000"), "Chrisjen Avasarala", Nil, Origin.EARTH, None),
    Character(CharacterId("600000"), "Josephus Miller", List("Joe"), Origin.BELT, None),
    Character(CharacterId("700000"), "Roberta Draper", List("Bobbie", "Gunny"), Origin.MARS, None)
  )

  val layer: ULayer[CharactersRepository] = ZLayer {
    for {
      storeRef <- Ref.make(data.map(c => (c.characterId, c)).toMap)
    } yield CharactersRepositoryMock(storeRef)
  }

}
