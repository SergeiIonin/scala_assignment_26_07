package com.siriusxm.example.cart

import cats.effect.IOApp
import cats.effect.IO
import org.http4s.ember.client.EmberClientBuilder

object Main extends IOApp.Simple {
  val baseUri = "https://raw.githubusercontent.com/mattjanks16/shopping-cart-test-data/main"
  val tax = 0.125

  def run: IO[Unit] = {
    (for {
      client         <- EmberClientBuilder.default[IO].build
      shoppingCart   <- ShoppingCart.make[IO](baseUri, tax, client)
    } yield shoppingCart).use { cart =>
      for {
        _ <- cart.add("cornflakes", 2)
        _ <- cart.add("weetabix", 1)
        subTotal <- cart.calculateSubtotal()
        _ <- IO.println(s"Subtotal: $subTotal")
        tax <- cart.calculateTax()
        _ <- IO.println(s"Tax: $tax")
        total <- cart.calculateTotal()
        _ <- IO.println(s"Total: $total")
      } yield ()
    }
  }
}
