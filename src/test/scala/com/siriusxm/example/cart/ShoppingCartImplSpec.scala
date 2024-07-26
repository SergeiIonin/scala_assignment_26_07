package com.siriusxm.example.cart

import cats.effect.IO
import cats.effect.testing.specs2.CatsEffect
import org.http4s.ember.client.EmberClientBuilder
import org.specs2.mutable.Specification

class ShoppingCartImplSpec extends Specification with CatsEffect {
  val baseUri = "https://raw.githubusercontent.com/mattjanks16/shopping-cart-test-data/main"
  val tax = 0.125
  val clientResource = EmberClientBuilder.default[IO].build
  "CartImpl" should {
    "return a cart with 2 items" in {
      (for {
        client <- clientResource
        cart = new ShoppingCartImpl[IO](baseUri, tax, client)
      } yield cart).use { cart =>
        for {
          _ <- cart.add("cornflakes", 2)
          _ <- cart.add("weetabix", 1)
          subTotal <- cart.calculateSubtotal()
          _ = subTotal must beEqualTo(15.02)
          tax <- cart.calculateTax()
          _ = tax must beEqualTo(1.88)
          total <- cart.calculateTotal()
          _ = total must beEqualTo(16.90)
        } yield true
      }
    }
  }
  "CartImpl" should {
    "fail to add an item if it's not available (we can't fetch it's price)" in {
      (for {
        client <- clientResource
        cart = new ShoppingCartImpl[IO](baseUri, tax, client)
      } yield cart).use { cart =>
        for {
          _ <- cart.add("inca-cola", 2)
          subTotal <- cart.calculateSubtotal()
          _ = subTotal must beEqualTo(0.0)
          tax <- cart.calculateTax()
          _ = tax must beEqualTo(0.0)
          total <- cart.calculateTotal()
          _ = total must beEqualTo(0.0)
        } yield true
      }
    }
  }
  "CartImpl" should {
    "throw error if the number of items is negative" in {
      (for {
        client <- clientResource
        cart = new ShoppingCartImpl[IO](baseUri, tax, client)
      } yield cart).use { cart =>
        for {
          res <- cart.add("cornflakes", -2).handleErrorWith(e => IO.pure(e.getMessage)).map(_.toString)
          _ = res must beEqualTo("Amount must be positive")
        } yield true
      }
    }
  }
}