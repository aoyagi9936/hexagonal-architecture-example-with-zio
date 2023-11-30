package com.example.secondary

import com.example.application.models.CharactersData._
import com.example.ports.secondary.CharactersRepository
import com.example.adapters.secondary.datastore.postgresql.CharactersRepositoryMock

import zio.test._
import zio.test.Assertion._
import zio.test.TestAspect._
import zio._

object CharactersRepositoryMockSpec extends ZIOSpecDefault {

  val repoLayer = CharactersRepositoryMock.layer

  override def spec =
    suite("character mock repository test")(

      test("save characters returns their ids") {
        for {
          id1 <- CharactersRepository.add(
            Character(
              CharacterId("800000"),
              "Kuroyagi san",
              List("kuro", "KURO"),
              Origin.EARTH,
              Some(Role.Engineer("GOAT")))
          )
          id2 <- CharactersRepository.add(
            Character(
              CharacterId("900000"),
              "Shiroyagi san",
              Nil,
              Origin.BELT,
              Some(Role.Engineer("GOAT")))
          )
        } yield assert(id1)(equalTo(CharacterId("800000")))
        && assert(id2)(equalTo(CharacterId("900000")))
      },

      test("get all returns 3 characters") {
        for {
          list <- CharactersRepository.getAll()
        } yield assert(list)(hasSize(equalTo(9)))
      },

      test("delete first character") {
        for {
          _ <- CharactersRepository.delete(CharacterId("100000"))
          c <- CharactersRepository.getById(CharacterId("100000"))
        } yield assert(c)(isNone)
      },

      test("get character Origin.BELT") {
        for {
          list <- CharactersRepository.filter(Origin.BELT)
        } yield assert(list)(hasSize(equalTo(3)))
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
      repoLayer,
    ) @@ sequential

}
