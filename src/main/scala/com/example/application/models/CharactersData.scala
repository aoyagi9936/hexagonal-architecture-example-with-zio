package com.example.application.models

object CharactersData {

  final case class CharacterId(value: String) extends AnyVal

  final case class Character(characterId: CharacterId, name: String, nicknames: List[String], origin: Origin, role: Option[Role])

  sealed trait Origin
  object Origin {
    case object EARTH extends Origin
    case object MARS  extends Origin
    case object BELT  extends Origin
  }

  sealed trait Role
  object Role {
    case class Captain(shipName: String)  extends Role
    case class Pilot(shipName: String)    extends Role
    case class Engineer(shipName: String) extends Role
    case class Mechanic(shipName: String) extends Role
  }

}
