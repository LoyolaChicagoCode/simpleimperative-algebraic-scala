package edu.luc.cs.cs371.simpleimperative

import cats.Eq
import cats.implicits._
import higherkindness.droste._
import org.scalacheck.{Prop, Properties}

import scala.util.Success

object imperativeTests extends Properties("imperativeTests") {

  import TestFixtures._
  import evaluate._, Value.Num

  /** Enable typesafe equality for `Map[String, Cell]`. */
  given CanEqual[Map[String, Cell], Map[String, Cell]] = CanEqual.derived

  /** Enable typesafe equality for `Result`. */
  given CanEqual[Result, Success[Cell]] = CanEqual.derived

  property("don't do anything unless triggered") = Prop {
    val s = store()
    val s0 = store()
    val ev = scheme.cata(evalAlgebra(s))
    ev(e2)
    s == s0
  }

  property("correctly evaluate a side-effect free expression") = Prop {
    val s = store()
    val s0 = store()
    evaluate(s)(e1) == Success(Cell(Num(7))) &&
      s == s0
  }

  property("correctly evaluate a simple assignment") = Prop {
    val s = store()
    val s0 = store()
    evaluate(s)(e2)
    s - "x" == s0 - "x" &&
      s0("x") == Cell(Num(2)) &&
      s("x") == Cell(Num(4))
  }

  property("correctly evaluate a statement block") = Prop {
    val s = store()
    val s0 = store()
    evaluate(s)(e3)
    s - "r" - "y" == s0 - "r" - "y" &&
      s("r") == Cell(Num(2)) &&
      s("y") == Cell(Num(2))
  }

  property("correctly evaluate a conditional") = Prop {
    val s = store()
    val s0 = store()
    evaluate(s)(e4a)
    s - "r" == s0 - "r" &&
      s("r") == Cell(Num(2))
  }

  property("correctly evaluate another conditional") = Prop {
    val s = store()
    val s0 = store()
    evaluate(s)(e4b)
    s - "y" == s0 - "y" &&
      s("y") == Cell(Num(2))
  }

  property("correctly evaluate a loop") = Prop {
    val s = store()
    val s0 = store()
    evaluate(s)(e5)
    s - "y" - "r" == s0 - "y" - "r" &&
      s("r") == Cell(Num(6)) &&
      s("y") == Cell(Num(0))
  }
}
