package edu.luc.cs.cs371.simpleimperative

import cats.implicits._
import org.scalacheck.cats.implicits._
import org.scalacheck.{Arbitrary, Gen, Prop, Properties}

object lawTests extends Properties("lawTests") {

  import ast._
  import ast.factory._

  property("equals1") = Prop { Constant(3) == Constant(3) }
  property("equals2") = Prop { constant(3) == constant(3) }
  property("equals3") = Prop { uminus(constant(3)) == uminus(constant(3)) }

  property("toString1") = Prop { UMinus(Constant(3)).toString == "UMinus(Constant(3))" }
  property("toString2") = Prop { uminus(constant(3)).toString == "UMinus(Constant(3))" }

  // TODO check if Traverse can help with this

  def genConstant(g: Gen[Int]) = g.map(Constant(_))
  def genVariable(g: Gen[String]) = g.map(Variable(_))
  def genUMinus[A](g: Gen[A]) = g.map(UMinus(_))
  def genPlus[A](g: Gen[A]) = (g, g).mapN(Plus(_, _))
  def genMinus[A](g: Gen[A]) = (g, g).mapN(Minus(_, _))
  def genTimes[A](g: Gen[A]) = (g, g).mapN(Times(_, _))
  def genDiv[A](g: Gen[A]) = (g, g).mapN(Div(_, _))
  def genMod[A](g: Gen[A]) = (g, g).mapN(Mod(_, _))
  def genBlock[A](g: Gen[A]) = (g, g).mapN((s, t) => Block(List(s, t))) // TODO generate lists of varying length
  def genCond[A](g: Gen[A]) = (g, g, g).mapN(Cond(_, _, _))
  def genLoop[A](g: Gen[A]) = (g, g).mapN(Loop(_, _))
  def genAssign[A](f: Gen[String], g: Gen[A]) = (f, g).mapN(Assign(_, _))

  implicit def exprFArbitrary[A: Arbitrary]: Arbitrary[ExprF[A]] = Arbitrary {
    val i = Arbitrary.arbInt.arbitrary
    val s = Arbitrary.arbString.arbitrary
    val g = Arbitrary.arbitrary[A]
    Gen.oneOf(genConstant(i), genVariable(s),
      genUMinus(g), genPlus(g), genMinus(g), genTimes(g), genDiv(g), genMod(g),
      genBlock(g), genCond(g), genLoop(g), genAssign(s, g))
  }

  include(cats.laws.discipline.FunctorTests[ExprF].functor[Int, Int, Int].all)
  // TODO reinclude after fixing Traverse
  // include(cats.laws.discipline.TraverseTests[ExprF].traverse[Int, Int, Int, Int, Option, Option].all)
}
