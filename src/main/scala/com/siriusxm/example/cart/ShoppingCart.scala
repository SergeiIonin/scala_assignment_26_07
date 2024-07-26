package com.siriusxm.example.cart

import cats.effect.Resource
import cats.effect.kernel.Async
import cats.effect.std.Console
import org.http4s.client.Client

trait ShoppingCart[F[_]] {
  def add(product: String, amount: Int): F[Unit]
  def calculateSubtotal(): F[Double]
  def calculateTax(): F[Double]
  def calculateTotal(): F[Double]
  def removeAll(): F[Unit]
}

object ShoppingCart {
 def make[F[_] : Async : Console](productsRoute: String, tax: Double, client: Client[F]): Resource[F, ShoppingCart[F]] =
   Resource.pure[F, ShoppingCart[F]](new ShoppingCartImpl[F](productsRoute, tax, client))
}