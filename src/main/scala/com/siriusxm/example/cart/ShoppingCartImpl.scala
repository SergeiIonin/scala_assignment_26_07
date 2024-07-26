package com.siriusxm.example.cart

import cats.effect.kernel.Async
import cats.effect.std.Console
import cats.syntax.applicative._
import cats.syntax.applicativeError._
import cats.syntax.functor._
import com.siriusxm.example.cart.CartEntityFormatter._
import org.http4s.circe._
import org.http4s.client.Client
import org.http4s.{EntityDecoder, EntityEncoder, Request, Uri}

import scala.collection.mutable

class ShoppingCartImpl[F[_] : Async : Console](baseUri: String, tax: Double, client: Client[F]) extends ShoppingCart[F] {
  implicit val cartEntityEncoder: EntityEncoder[F, CartEntity] = jsonEncoderOf[F, CartEntity]
  implicit val cartEntityDecoder: EntityDecoder[F, CartEntity] = jsonOf[F, CartEntity]
  private val underlying = mutable.Map.empty[String, CartEntityWithAmount]

  private def ceil(d: Double): Double = Math.ceil(d * 100) / 100

  override def add(product: String, amount: Int): F[Unit] = {
    val uri = Uri.unsafeFromString(s"$baseUri/$product.json")
    if (amount < 0) {
      new RuntimeException("Amount must be positive").raiseError[F, Unit]
    } else {
      client.run(Request(uri = uri)).use { response =>
        response.as[CartEntity].map { cartEntity =>
          val currentAmount = underlying.get(cartEntity.title).map(_.amount).getOrElse(0)
          underlying.update(cartEntity.title, CartEntityWithAmount(cartEntity, currentAmount + amount))
        }.handleErrorWith(_ => Console[F].println(s"Failed to add $product to the cart"))
      }
    }
  }

  override def calculateSubtotal(): F[Double] = {
    val sum = underlying.values.map(cartEntityWithAmount => cartEntityWithAmount.cartEntity.price * cartEntityWithAmount.amount).sum
    ceil(sum).pure[F]
  }

  override def calculateTax(): F[Double] =
    calculateSubtotal().map(_ * tax).map(ceil) // for simplicity, subtotal could be hashed actually

  override def calculateTotal(): F[Double] =
    calculateSubtotal().map(_ * (1.0 + tax)).map(ceil)

  override def removeAll(): F[Unit] = {
    underlying.clear()
    ().pure[F]
  }

}