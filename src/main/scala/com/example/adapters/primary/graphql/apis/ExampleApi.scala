package com.example.adapters.primary.graphql.apis

import com.example.application.constants._
import com.example.application.models.ExampleData._
import com.example.ports.primary.ExampleServiceApi

import caliban.GraphQL.graphQL
import caliban.RootResolver
import caliban.schema.Annotations.{ GQLDeprecated, GQLDescription }
import caliban.schema.Schema

import zio._
import zio.stream.ZStream

import scala.language.postfixOps

object ExampleApi {

  case class Queries(
    @GQLDescription("Return all characters from a given origin")
    characters: CharactersArgs => URIO[ExampleServiceApi, List[Character]],
    @GQLDeprecated("Use `characters`")
    character: CharacterArgs => URIO[ExampleServiceApi, Option[Character]]
  )
  case class Mutations(deleteCharacter: CharacterArgs => URIO[ExampleServiceApi, Boolean])
  case class Subscriptions(characterDeleted: ZStream[ExampleServiceApi, Nothing, String])

  implicit val roleSchema: Schema[Any, Role]                     = Schema.gen
  implicit val characterSchema: Schema[Any, Character]           = Schema.gen
  implicit val characterArgsSchema: Schema[Any, CharacterArgs]   = Schema.gen
  implicit val charactersArgsSchema: Schema[Any, CharactersArgs] = Schema.gen

  val api =
    graphQL[ExampleServiceApi, Queries, Unit, Unit](
      RootResolver(
        Queries(
          args => ExampleServiceApi.getCharacters(args.origin),
          args => ExampleServiceApi.findCharacter(args.name)
        ),
        Mutations(args => ExampleServiceApi.deleteCharacter(args.name)),
        Subscriptions(ExampleServiceApi.deletedEvents)
      )
    )

}
