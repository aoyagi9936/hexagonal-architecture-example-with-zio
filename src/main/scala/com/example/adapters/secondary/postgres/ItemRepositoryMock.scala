package com.example.adapters.secondary.postgres

import com.example.ports.secondary.ItemRepository
import com.example.application.constants._
import com.example.application.models.{ItemId, Item, ItemData}

import zio.{ ZIO, UIO, ZLayer, ULayer }

final class ItemRepositoryMock extends ItemRepository {

  override def add(data: ItemData): UIO[ItemId] = ???

  override def delete(id: ItemId): UIO[Long] = ???

  override def getAll(): UIO[List[Item]] = ZIO.succeed(
    List(
      Item(ItemId(100000), "test1", 1000),
      Item(ItemId(200000), "test2", 2000),
      Item(ItemId(300000), "test3", 3000),
      Item(ItemId(400000), "test4", 4000),
      Item(ItemId(400000), "test4", 5000)
    )
  )

  override def getById(id: ItemId): UIO[Option[Item]] = ???

  override def update(itemId: ItemId, data: ItemData): UIO[Option[Unit]] = ???

  val layer: ULayer[ItemRepository] = ZLayer.succeed(
    ItemRepositoryMock()
  )
}
