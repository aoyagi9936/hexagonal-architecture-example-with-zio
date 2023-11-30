package com.example.secondary

import com.example.application.models.CharactersData._
import com.example.ports.secondary.CharactersRepository
import com.example.adapters.secondary.datastore.postgresql.CharactersRepositoryLive

import io.getquill.PluralizedTableNames
import io.getquill._
import io.getquill.jdbczio.Quill

import zio.test._
import zio.test.Assertion._
import zio.test.TestAspect._
import zio._

import postgresql._

object CharactersRepositoryLiveSpec extends ZIOSpecDefault {

  val containerLayer  = ZLayer.scoped(PostgresContainer.make())
  val dataSourceLayer = ZLayer(ZIO.service[DataSourceBuilder].map(_.dataSource))
  val postgresLayer   = Quill.Postgres.fromNamingStrategy(SnakeCase)
  val repoLayer       = CharactersRepositoryLive.layer

  override def spec =
    suite("character repository test with postgres test container")(

      test("save characters returns their ids") {
        for {
          id1 <- CharactersRepository.add(
            Character(
              CharacterId("100000"),
              "James Holden",
              List("Jim", "Hoss"),
              Origin.EARTH,
              Some(Role.Captain("Rocinante")))
          )
          id2 <- CharactersRepository.add(
            Character(
              CharacterId("200000"),
              "Naomi Nagata",
              Nil,
              Origin.BELT,
              Some(Role.Engineer("Rocinante")))
          )
          id3 <- CharactersRepository.add(
            Character(
              CharacterId("300000"),
              "Amos Burton",
              Nil,
              Origin.EARTH,
              Some(Role.Mechanic("Rocinante")))
          )
        } yield assert(id1)(equalTo(CharacterId("100000")))
        && assert(id2)(equalTo(CharacterId("200000")))
        && assert(id3)(equalTo(CharacterId("300000")))
      },

      test("get all returns 3 characters") {
        for {
          list <- CharactersRepository.getAll()
        } yield assert(list)(hasSize(equalTo(3)))
      },

      test("delete first character") {
        for {
          _ <- CharactersRepository.delete(CharacterId("100000"))
          c <- CharactersRepository.getById(CharacterId("100000"))
        } yield assert(c)(isNone)
      },

      test("get character that has Origin.BELT") {
        for {
          list <- CharactersRepository.filter(Origin.BELT)
        } yield assert(list)(hasSize(equalTo(1))) &&
        assert(list.head.name)(equalTo("Naomi Nagata")) &&
        assert(list.head.role)(equalTo(Some(Role.Engineer("Rocinante"))))
      },

      test("update character 3") {
        for {
          _ <- CharactersRepository.update(CharacterId("300000"),
            Character(
              CharacterId("300000"),
              "Aoyagi san",
              Nil,
              Origin.BELT,
              Some(Role.Captain("Starship"))))
          c <- CharactersRepository.getById(CharacterId("300000"))
        } yield assert(c)(isSome) &&
        assert(c.get.name)(equalTo("Aoyagi san")) &&
        assert(c.get.origin)(equalTo(Origin.BELT)) &&
        assert(c.get.role.get)(equalTo(Role.Captain("Starship")))
      },

    ).provideShared(
      containerLayer,
      DataSourceBuilderLive.layer,
      dataSourceLayer,
      postgresLayer,
      repoLayer,
    ) @@ sequential

}
