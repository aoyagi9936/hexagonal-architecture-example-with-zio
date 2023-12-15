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
    characters: GetCharactersArgs => ZIO[Apis, PrimaryError, List[Character]],
    @GQLDeprecated("Use `characters`")
    character: GetCharacterArgs   => ZIO[Apis, PrimaryError, Character],
  )
  case class Mutations(
    addCharacter: AddCharacterArgs    => ZIO[Apis, PrimaryError, CharacterId],
    updateCharacter: UpdCharacterArgs => ZIO[Apis, PrimaryError, Boolean],
    deleteCharacter: DelCharacterArgs => ZIO[Apis, PrimaryError, Boolean],
  )
  case class Subscriptions(characterDeleted: ZStream[Apis, Nothing, String])

  // Enum/Union
  given Schema[Any, Origin] = Schema.Auto.derived
  given Schema[Any, Role]   = Schema.Auto.derived

  // Request
  given Schema[Any, RoleReq]          = Schema.gen
  given Schema[Any, GetCharacterArgs] = Schema.gen
  given ArgBuilder[CharacterId] = {
    case Value.StringValue(value) =>
      Try(CharacterId(value))
        .fold(ex => Left(ExecutionError(s"Can't parse $value into a CharacterId", innerThrowable = Some(ex))), Right(_))
    case other => Left(ExecutionError(s"Can't build a CharacterId from input $other"))
  }
  given ArgBuilder[GetCharacterArgs]   = ArgBuilder.derived
  given Schema[Any, GetCharactersArgs] = Schema.gen
  given ArgBuilder[GetCharactersArgs]  = ArgBuilder.Auto.derived
  given Schema[Any, AddCharacterArgs]  = Schema.gen
  given ArgBuilder[AddCharacterArgs]   = ArgBuilder.Auto.derived
  given Schema[Any, UpdCharacterArgs]  = Schema.gen
  given ArgBuilder[UpdCharacterArgs]   = ArgBuilder.Auto.derived
  given Schema[Any, DelCharacterArgs]  = Schema.gen
  given ArgBuilder[DelCharacterArgs]   = ArgBuilder.derived

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
          args => CharactersApi.findCharacter(args.id),
        ),
        Mutations(
          args => CharactersApi.addCharacter(args.name, args.nicknames, args.origin, args.role),
          args => CharactersApi.updateCharacter(args.id, args.name, args.nicknames, args.origin, args.role),
          args => CharactersApi.deleteCharacter(args.id),
        ),
        Subscriptions(CharactersApi.deletedEvents)
      )
    )

}
