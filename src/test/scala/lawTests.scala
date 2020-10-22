package edu.luc.cs.cs371.simpleimperative

import scalaz.syntax.applicative._
import scalaz.std.anyVal._
import scalaz.scalacheck.ScalaCheckBinding._
import scalaz.scalacheck.ScalazProperties._
import org.scalacheck.{Arbitrary, Gen, Prop, Properties}
import Gen._
import Arbitrary._

object lawTests extends Properties("lawTests") {

  import ast._
  import ast.factory._

  property("equals1") = Prop { Constant(3) == Constant(3) }
  property("equals2") = Prop { constant(3) == constant(3) }
  property("equals3") = Prop { uminus(constant(3)) == uminus(constant(3)) }

  property("toString1") = Prop { UMinus(Constant(3)).toString == "UMinus(Constant(3))" }
  property("toString1") = Prop { uminus(constant(3)).toString == "Fix(UMinus(Fix(Constant(3))))" }

  // TODO check if Traverse can help with this

  def genConstant(g: Gen[Int]) = g ∘ (Constant(_))
  def genVariable(g: Gen[String]) = g ∘ (Variable(_))
  def genUMinus[A](g: Gen[A]) = g ∘ (UMinus(_))
  def genPlus[A](g: Gen[A]) = (g ⊛ g)(Plus(_, _))
  def genMinus[A](g: Gen[A]) = (g ⊛ g)(Minus(_, _))
  def genTimes[A](g: Gen[A]) = (g ⊛ g)(Times(_, _))
  def genDiv[A](g: Gen[A]) = (g ⊛ g)(Div(_, _))
  def genMod[A](g: Gen[A]) = (g ⊛ g)(Mod(_, _))
  def genBlock[A](g: Gen[A]) = (g ⊛ g)((s, t) => Block(List(s, t))) // TODO generate lists of varying length
  def genCond[A](g: Gen[A]) = (g ⊛ g ⊛ g)(Cond(_, _, _))
  def genLoop[A](g: Gen[A]) = (g ⊛ g)(Loop(_, _))
  def genAssign[A](f: Gen[String], g: Gen[A]) = (f ⊛ g)(Assign(_, _))

  // required for non-Delay implicits to arise automatically from Delay ones
  import matryoshka._
  import matryoshka.implicits._
  import matryoshka.scalacheck.arbitrary._

  implicit object exprFArbitraryD extends Delay[Arbitrary, ExprF] {
    def apply[A](a: Arbitrary[A]) = Arbitrary {
      val i = arbInt.arbitrary
      val g = a.arbitrary
      oneOf(genConstant(i), genUMinus(g), genPlus(g), genMinus(g), genTimes(g), genDiv(g), genMod(g))
    }
  }

  include(equal.laws[ExprF[Int]], "equalExprFInt.")
  include(equal.laws[ExprF[ExprF[Int]]], "equalExprF2Int.")
  include(equal.laws[ExprF[ExprF[ExprF[Int]]]], "equalExprF3Int.")

  // FIXME https://github.com/LoyolaChicagoCode/expressions-algebraic-scala/issues/15
  //include(equal.laws[Expr], "equalExpr.")

  include(functor.laws[ExprF], "functorExprF.")
  include(traverse.laws[ExprF], "traverseExprF.")
}
