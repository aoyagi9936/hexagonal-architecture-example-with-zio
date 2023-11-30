package com.example.adapters.secondary.datastore.postgresql

import com.example.ports.secondary.CharactersRepository
import com.example.application.constants._
import com.example.application.models.CharactersData._
import entities.{ Character => CharacterTbl, Role => RoleTbl }

import zio.{ ZIO, IO, ZLayer, URLayer }
import io.getquill._
import io.getquill.jdbczio.Quill

import java.sql.SQLException

final class CharactersRepositoryLive(quill: Quill.Postgres[SnakeCase]) extends CharactersRepository {

  import quill._
  import QuillSupport._

  inline def character = quote { query[CharacterTbl] }
  inline def role      = quote { query[RoleTbl]      }

  override def add(data: Character): IO[SecondaryError, CharacterId] = transaction {
    for {
      r1 <- run {
        quote {
          character
            .insertValue(
              lift(CharacterTbl(
                data.characterId.value,
                data.name,
                data.nicknames,
                data.origin.toString,
              ))
            )
            .returning(_.characterId)
        }
      }
      r2 <- data.role match {
        case Some(r) => run {
          role.insertValue(
            lift(RoleTbl(
              data.characterId.value,
              Role.getString(r),
              Role.getShipName(r),
            ))
          )
        }
        case None => ZIO.succeed(0)
      }
    } yield r1 }
    .refineOrDie {
      case e: NullPointerException => RepositoryError(e)
    }
    .map {
      case id: String => CharacterId(id)
    }

  override def delete(id: CharacterId): IO[SecondaryError, Long] = run {
    quote {
      character.filter(c => c.characterId == lift(id.value)).delete
    }}
    .refineOrDie {
      case e: SQLException => RepositoryError(e)
    }

  override def getAll(): IO[SecondaryError, List[Character]] = run {
    quote {
      character
        .leftJoin(role)
        .on((c, r) => c.characterId == r.characterId)
    }}
    .either
    .resurrect
    .refineOrDie {
      case e: SQLException => RepositoryError(e)
    }
    .flatMap {
      case Left(e: Exception) => ZIO.fail(RepositoryError(e))
      case Right(list)        => ZIO.succeed(
        list.map(v => Character(
          CharacterId(v._1.characterId),
          v._1.characterName,
          v._1.nicknames,
          Origin.fromString(v._1.originType),
          v._2.map(r => Role.fromString(r.roleType, r.shipName)),
        ))
      )
    }

  override def filter(origin: Origin): IO[SecondaryError, List[Character]] = run {
    quote {
      character
        .filter(_.originType == lift(origin.toString()))
        .leftJoin(role)
        .on((c, r) => c.characterId == r.characterId)
    }}
    .refineOrDie {
      case e: SQLException => RepositoryError(e)
    }
    .flatMap {
      list => ZIO.succeed(
        list.map(v => Character(
          CharacterId(v._1.characterId),
          v._1.characterName,
          v._1.nicknames,
          Origin.fromString(v._1.originType),
          v._2.map(r => Role.fromString(r.roleType, r.shipName)),
        ))
      )
    }

  override def getById(id: CharacterId): IO[SecondaryError, Option[Character]] = run {
    quote {
      character
        .filter(_.characterId == lift(id.value))
        .leftJoin(role)
        .on((c, r) => c.characterId == r.characterId)
    }}
    .map(_.headOption.map(
      v => Character(
        CharacterId(v._1.characterId),
        v._1.characterName,
        v._1.nicknames,
        Origin.fromString(v._1.originType),
        v._2.map(r => Role.fromString(r.roleType, r.shipName)),
      )
    ))
    .refineOrDie {
      case e: SQLException => RepositoryError(e)
    }

  override def update(id: CharacterId, data: Character): IO[SecondaryError, Option[Unit]] = transaction {
    for {
      r1 <- run {
        quote {
          character
            .filter(_.characterId == lift(id.value))
            .updateValue(lift(CharacterTbl(
              id.value,
              data.name,
              data.nicknames,
              data.origin.toString(),
            )))
        }
      }
      r2 <- run {
        data.role match {
          case Some(r) =>
            quote {
              role
                .filter(_.characterId == lift(id.value))
                .updateValue(lift(RoleTbl(
                  id.value,
                  Role.getString(r),
                  Role.getShipName(r),
                )))
            }
          case None =>
            quote {
              role
                .filter(_.characterId == lift(id.value))
                .delete
            }
        }
      }

    } yield (r1, r2) }
    .refineOrDie {
      case e: SQLException => RepositoryError(e)
    }
    .map {
      case (r1, r2) if r1 + r2 > 0 => Some(())
      case _ => None
    }

}

object CharactersRepositoryLive {

  lazy val layer: URLayer[Quill.Postgres[SnakeCase], CharactersRepository] = ZLayer {
    for {
      quill <- ZIO.service[Quill.Postgres[SnakeCase]]
    } yield CharactersRepositoryLive(quill)
  }

}
