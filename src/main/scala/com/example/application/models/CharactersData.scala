package com.example.application.models

import enumeratum._

object CharactersData {

  final case class CharacterId(value: String) extends AnyVal
  final case class Character(characterId: CharacterId, name: String, nicknames: List[String], origin: Origin, role: Option[Role])
  final case class RoleReq(kind: String, shipName: String)

  sealed trait Origin extends EnumEntry
  object Origin extends Enum[Origin] {
    case object EARTH extends Origin
    case object MARS  extends Origin
    case object BELT  extends Origin

    val values = findValues
  }

  sealed trait Role {
    def shipName: String
    def roleName: String
  }
  object Role {
    case class Captain(shipName: String)  extends Role {
      override def roleName: String = "Captain"
    }
    case class Pilot(shipName: String)    extends Role {
      override def roleName: String = "Pilot"
    }
    case class Engineer(shipName: String) extends Role {
      override def roleName: String = "Engineer"
    }
    case class Mechanic(shipName: String) extends Role {
      override def roleName: String = "Mechanic"
    }

    def fromString(roleName: String, shipName: String): Role = roleName.toLowerCase match {
      case "captain"  => Captain(shipName)
      case "pilot"    => Pilot(shipName)
      case "engineer" => Engineer(shipName)
      case "mechanic" => Mechanic(shipName)
    }

  }

}
