package com.example.adapters.primary.graphql.schemas

import com.example.application.constants._
import com.example.application.models.ExampleData._
import com.example.ports.primary.ExampleApi

import caliban.GraphQL.graphQL
import caliban.RootResolver
import caliban.schema.Annotations.{ GQLDeprecated, GQLDescription }
import caliban.schema.Schema

import zio._
import zio.stream.ZStream

import scala.language.postfixOps

object ExampleSchema {

  case class Queries(
    @GQLDescription("Return all characters from a given origin")
    characters: CharactersArgs => ZIO[ExampleApi, PrimaryError, List[Character]],
    @GQLDeprecated("Use `characters`")
    character: CharacterArgs => ZIO[ExampleApi, PrimaryError, Option[Character]]
  )
  case class Mutations(deleteCharacter: CharacterArgs => ZIO[ExampleApi, PrimaryError, Boolean])
  case class Subscriptions(characterDeleted: ZStream[ExampleApi, Nothing, String])

  implicit val roleSchema: Schema[Any, Role]                     = Schema.gen
  implicit val characterSchema: Schema[Any, Character]           = Schema.gen
  implicit val characterArgsSchema: Schema[Any, CharacterArgs]   = Schema.gen
  implicit val charactersArgsSchema: Schema[Any, CharactersArgs] = Schema.gen

  val api =
    graphQL[ExampleApi, Queries, Unit, Unit](
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
