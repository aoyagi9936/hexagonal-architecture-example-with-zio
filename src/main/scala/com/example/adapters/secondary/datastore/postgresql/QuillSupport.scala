package com.example.adapters.secondary.datastore.postgresql

import io.getquill._
import java.time.LocalDateTime

object QuillSupport {

  extension (inline left: LocalDateTime) {
    inline def >(right: LocalDateTime) = quote(sql"$left > $right".as[Boolean])
    inline def <(right: LocalDateTime) = quote(sql"$left < $right".as[Boolean])
  }

}
