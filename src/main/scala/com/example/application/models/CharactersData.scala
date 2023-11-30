package com.example.application.models

object CharactersData {

  final case class CharacterId(value: String) extends AnyVal

  final case class Character(characterId: CharacterId, name: String, nicknames: List[String], origin: Origin, role: Option[Role])

  sealed trait Origin
  object Origin {
    case object EARTH extends Origin
    case object MARS  extends Origin
    case object BELT  extends Origin

    def fromString(s: String): Origin = s match {
      case "EARTH" => EARTH
      case "MARS"  => MARS
      case "BELT"  => BELT
    }
  }

  sealed trait Role
  object Role {
    case class Captain(shipName: String)  extends Role
    case class Pilot(shipName: String)    extends Role
    case class Engineer(shipName: String) extends Role
    case class Mechanic(shipName: String) extends Role

    def fromString(s: String, n: String): Role = s match {
      case "Captain"  => Captain(n)
      case "Pilot"    => Pilot(n)
      case "Engineer" => Engineer(n)
      case "Mechanic" => Mechanic(n)
    }

    def getString(r: Role): String = r match {
      case _:Captain  => "Captain"
      case _:Pilot    => "Pilot"
      case _:Engineer => "Engineer"
      case _:Mechanic => "Mechanic"
    }

    def getShipName(r: Role): String = r match {
      case Captain(shipName)  => shipName
      case Pilot(shipName)    => shipName
      case Engineer(shipName) => shipName
      case Mechanic(shipName) => shipName
    }

  }

}
