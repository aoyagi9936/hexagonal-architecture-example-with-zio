package com.example.adapters.secondary.datastore.postgresql.entities

case class Character(characterId: String, characterName: String, nicknames: List[String], originType: String)
