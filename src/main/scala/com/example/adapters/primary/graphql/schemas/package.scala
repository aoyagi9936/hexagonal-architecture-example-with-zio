package com.example.adapters.primary.graphql

package object schemas {

  import com.example.application.models.CharactersData._

  final case class GetCharactersArgs(origin: Option[Origin])
  final case class GetCharacterArgs(id: CharacterId)
  final case class AddCharacterArgs(
    name: String, nicknames: List[String], origin: Origin, role: Option[RoleReq])
  final case class UpdCharacterArgs(
    id: CharacterId, name: String, nicknames: List[String], origin: Origin, role: Option[RoleReq])
  final case class DelCharacterArgs(id: CharacterId)

}

