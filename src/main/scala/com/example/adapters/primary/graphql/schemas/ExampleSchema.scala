package com.example.adapters.primary.graphql.schemas

import com.example.application.constants._
import com.example.application.models.ExampleData._
import com.example.ports.primary.ExampleApi

import caliban.graphQL
import caliban.RootResolver
import caliban.schema.Annotations.{ GQLDeprecated, GQLDescription }
import caliban.schema.{ ArgBuilder, Schema }

import zio._
import zio.stream.ZStream

import scala.language.postfixOps

object ExampleSchema {

  type Apis = ExampleApi

  case class Queries(
    @GQLDescription("Return all characters from a given origin")
    characters: CharactersArgs => ZIO[Apis, PrimaryError, List[Character]],
    @GQLDeprecated("Use `characters`")
    character: CharacterArgs   => ZIO[Apis, PrimaryError, Option[Character]]
  )
  case class Mutations(deleteCharacter: CharacterArgs => ZIO[Apis, PrimaryError, Boolean])
  case class Subscriptions(characterDeleted: ZStream[Apis, Nothing, String])

  // Enum/Union
  given Schema[Any, Origin]         = Schema.Auto.derived
  given Schema[Any, Role]           = Schema.Auto.derived

  // Request
  given Schema[Any, CharacterArgs]  = Schema.gen
  given ArgBuilder[CharacterArgs]   = ArgBuilder.gen
  given Schema[Any, CharactersArgs] = Schema.gen
  given ArgBuilder[CharactersArgs]  = ArgBuilder.Auto.derived

  // Response
  given Schema[Any, Character]      = Schema.gen

  // Schema
  given Schema[Apis, Queries]       = Schema.gen
  given Schema[Apis, Mutations]     = Schema.gen
  given Schema[Apis, Subscriptions] = Schema.gen

  val api =
    graphQL(
      RootResolver(
        Queries(
          args => ExampleApi.getCharacters(args.origin),
          args => ExampleApi.findCharacter(args.name)
        ),
        Mutations(args => ExampleApi.deleteCharacter(args.name)),
        Subscriptions(ExampleApi.deletedEvents)
      )
    )

}
