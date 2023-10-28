package com.example.adapters.primary.graphql.schemas

import com.example.application.constants._
import com.example.application.models.CharactersData._
import com.example.ports.primary.CharactersApi

import caliban.graphQL
import caliban.RootResolver
import caliban.Value
import caliban.CalibanError.ExecutionError
import caliban.schema.Annotations.{ GQLDeprecated, GQLDescription }
import caliban.schema.{ ArgBuilder, Schema }

import zio._
import zio.stream.ZStream

import scala.util.Try
import scala.language.postfixOps

object CharactersSchema {

  type Apis = CharactersApi

  case class Queries(
    @GQLDescription("Return all characters from a given origin")
    characters: CharactersArgs => ZIO[Apis, PrimaryError, List[Character]],
    @GQLDeprecated("Use `characters`")
    character: CharacterArgs   => ZIO[Apis, PrimaryError, Character]
  )
  case class Mutations(deleteCharacter: CharacterArgs => ZIO[Apis, PrimaryError, Boolean])
  case class Subscriptions(characterDeleted: ZStream[Apis, Nothing, String])

  // Enum/Union
  given Schema[Any, Origin]         = Schema.Auto.derived
  given Schema[Any, Role]           = Schema.Auto.derived

  // Request
  given Schema[Any, CharacterArgs]  = Schema.gen
  given ArgBuilder[CharacterId] = {
    case Value.StringValue(value) =>
      Try(CharacterId(value))
        .fold(ex => Left(ExecutionError(s"Can't parse $value into a CharacterId", innerThrowable = Some(ex))), Right(_))
    case other => Left(ExecutionError(s"Can't build a CharacterId from input $other"))
  }
  given ArgBuilder[CharacterArgs]   = ArgBuilder.derived
  given Schema[Any, CharactersArgs] = Schema.gen
  given ArgBuilder[CharactersArgs]  = ArgBuilder.Auto.derived

  // Response
  given Schema[Any, CharacterId]    = Schema.stringSchema.contramap(_.value)
  given Schema[Any, Character]      = Schema.gen

  // Schema
  given Schema[Apis, Queries]       = Schema.gen
  given Schema[Apis, Mutations]     = Schema.gen
  given Schema[Apis, Subscriptions] = Schema.gen

  val api =
    graphQL(
      RootResolver(
        Queries(
          args => CharactersApi.getCharacters(args.origin),
          args => CharactersApi.findCharacter(args.id)
        ),
        Mutations(args => CharactersApi.deleteCharacter(args.id)),
        Subscriptions(CharactersApi.deletedEvents)
      )
    )

}
