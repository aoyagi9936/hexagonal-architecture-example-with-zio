package com.example.adapters.primary.rest.endpoints

import org.http4s._

import sttp.tapir.PublicEndpoint
import sttp.tapir.server.http4s.ztapir.ZHttp4sServerInterpreter
import sttp.model.StatusCode

import com.example.ports.primary.ExampleReadApi
import com.example.application.models.ExampleData._
import com.example.application.constants._
import TapirSupport.{given, _}

import zio._

object ExampleReadEndpoint {

  type Apis = ExampleReadApi

  val baseEndpoint: PublicEndpoint[Unit, PrimaryError, Unit, Any] =
    endpoint
      .in("api" / "v1")
      .errorOut(
        oneOf[PrimaryError](
          oneOfVariant(
            statusCode(StatusCode.InternalServerError)
              .and(jsonBody[RestInternalServerError])
          )
        )
      )

  val charactersEndpoint: PublicEndpoint[Option[Origin], PrimaryError, List[Character], Any] =
    baseEndpoint.get
      .in("characters" / "list")
      .in(originMaybeQuery)
      .out(jsonBody[List[Character]])

  val charactersLogic = charactersEndpoint.zServerLogic {
    origin => ExampleReadApi.getCharacters(origin)
  }

}
