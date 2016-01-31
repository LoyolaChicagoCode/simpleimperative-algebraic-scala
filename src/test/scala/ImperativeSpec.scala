package edu.luc.cs.cs372.simpleimperative

import scala.util.Success
import org.scalatest.WordSpec

class ImperativeSpec extends WordSpec {

  import evaluate._
  import TestFixtures._

  "The evaluator" when {
    "invoked with the given store" should {

      "not do anything unless triggered" in {
        import scalamu._
        val s = store()
        val s0 = store()
        e2.cata(evalAlgebra(s))
        assert { s == s0 }
      }

      "correctly evaluate a side-effect free expression" in {
        val s = store()
        val s0 = store()
        assert { evaluate(s)(e1) == Success(Cell(Num(7))) }
        assert { s == s0 }
      }

      "correctly evaluate a simple assignment" in {
        val s = store()
        val s0 = store()
        evaluate(s)(e2)
        assert { s - "x" == s0 - "x" }
        assert { s0("x") == Cell(Num(2)) }
        assert { s("x") == Cell(Num(4)) }
      }

      "correctly evaluate a statement block" in {
        val s = store()
        val s0 = store()
        evaluate(s)(e3)
        assert { s - "r" - "y" == s0 - "r" - "y" }
        assert { s("r") == Cell(Num(2)) }
        assert { s("y") == Cell(Num(2)) }
      }

      "correctly evaluate a conditional" in {
        val s = store()
        val s0 = store()
        evaluate(s)(e4a)
        assert { s - "r" == s0 - "r" }
        assert { s("r") == Cell(Num(2)) }
      }

      "correctly evaluate another conditional" in {
        val s = store()
        val s0 = store()
        evaluate(s)(e4b)
        assert { s - "y" == s0 - "y" }
        assert { s("y") == Cell(Num(2)) }
      }

      "correctly evaluate a loop" in {
        val s = store()
        val s0 = store()
        evaluate(s)(e5)
        assert { s - "y" - "r" == s0 - "y" - "r" }
        assert { s("r") == Cell(Num(6)) }
        assert { s("y") == Cell(Num(0)) }
      }
    }
  }
}