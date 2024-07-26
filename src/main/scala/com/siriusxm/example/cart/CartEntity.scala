package com.siriusxm.example.cart

import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}

final case class CartEntity(title: String, price: Double)

object CartEntityFormatter {
  implicit val encoder = deriveEncoder[CartEntity]
  implicit val decoder = deriveDecoder[CartEntity]
}
