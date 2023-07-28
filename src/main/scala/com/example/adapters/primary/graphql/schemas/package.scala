package com.example.adapters.primary.graphql

package object schemas {

  import com.example.application.models.CharactersData._

  sealed case class CharactersArgs(origin: Option[Origin])
  sealed case class CharacterArgs(name: String)

}

