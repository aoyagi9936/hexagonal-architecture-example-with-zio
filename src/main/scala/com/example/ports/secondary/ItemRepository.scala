package com.example.ports.secondary

import com.example.application.constants.SecondaryError
import com.example.application.models.{ItemId, Item, ItemData}

import zio._

trait ItemRepository:
  def add(data: ItemData): IO[SecondaryError, ItemId]

  def delete(id: ItemId): IO[SecondaryError, Long]

  def getAll(): IO[SecondaryError, List[Item]]

  def getById(id: ItemId): IO[SecondaryError, Option[Item]]

  def update(itemId: ItemId, data: ItemData): IO[SecondaryError, Option[Unit]]

object ItemRepository:
  def add(data: ItemData): ZIO[ItemRepository, SecondaryError, ItemId] =
    ZIO.serviceWithZIO[ItemRepository](_.add(data))

  def delete(id: ItemId): ZIO[ItemRepository, SecondaryError, Long] =
    ZIO.serviceWithZIO[ItemRepository](_.delete(id))

  def getAll(): ZIO[ItemRepository, SecondaryError, List[Item]] =
    ZIO.serviceWithZIO[ItemRepository](_.getAll())

  def getById(id: ItemId): ZIO[ItemRepository, SecondaryError, Option[Item]] =
    ZIO.serviceWithZIO[ItemRepository](_.getById(id))

  def update(itemId: ItemId, data: ItemData): ZIO[ItemRepository, SecondaryError, Option[Unit]] =
    ZIO.serviceWithZIO[ItemRepository](_.update(itemId, data))
