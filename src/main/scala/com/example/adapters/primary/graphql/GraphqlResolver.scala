package com.example.adapters.primary.graphql

import caliban.GraphQL
import caliban.wrappers.ApolloTracing.apolloTracing
import caliban.wrappers.Wrappers._

import com.example.adapters.primary.graphql.schemas._

import zio._

import scala.language.postfixOps

object GraphqlResolver {

  type Apis = CharactersSchema.Apis

  val api: GraphQL[Apis] = CharactersSchema.api // |+| OtherApi.api
   @@
      maxFields(200) @@               // query analyzer that limit query fields
      maxDepth(30) @@                 // query analyzer that limit query depth
      timeout(3 seconds) @@           // wrapper that fails slow queries
      printSlowQueries(500 millis) @@ // wrapper that logs slow queries
      printErrors @@                  // wrapper that logs errors
      apolloTracing                   // wrapper for https://github.com/apollographql/apollo-tracing
}

