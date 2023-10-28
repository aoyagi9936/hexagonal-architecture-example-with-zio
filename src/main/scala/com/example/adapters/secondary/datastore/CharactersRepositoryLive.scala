package com.example.adapters.secondary.datastore

import com.example.ports.secondary.CharactersRepository
import com.example.application.constants._
import com.example.application.models.CharactersData._

import zio.{ ZIO, IO, ZLayer, URLayer }
import io.getquill._
import io.getquill.jdbczio.Quill

final class CharactersRepositoryLive(quill: Quill.Postgres[SnakeCase]) extends CharactersRepository {

  import quill._
  import QuillSupport._

  override def add(data: Character): IO[RepositoryError, CharacterId] = ???

  override def delete(id: CharacterId): IO[RepositoryError, Long] = ???

  override def getAll(): IO[RepositoryError, List[Character]] = ???

  override def filter(origin: Origin): IO[RepositoryError, List[Character]] = ???

  override def getById(id: CharacterId): IO[RepositoryError, Option[Character]] = ???

  override def update(itemId: CharacterId, data: Character): IO[RepositoryError, Option[Unit]] = ???

}

object CharactersRepositoryLive {

  lazy val layer: URLayer[Quill.Postgres[SnakeCase], CharactersRepository] = ZLayer {
    for {
      quill <- ZIO.service[Quill.Postgres[SnakeCase]]
    } yield CharactersRepositoryLive(quill)
  }

}
