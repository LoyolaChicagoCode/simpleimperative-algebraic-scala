package edu.luc.cs.cs371.simpleimperative

/*
 * In this example, we represent simple imperative programs
 * as trees (initial algebra for the endofunctor defined next).
 */

import cats.{Eq, Functor, Show, Traverse}
import higherkindness.droste.data.Fix

object ast:

  /**
    * An abstraction of a program element.
    *
    * Endofunctor for (nongeneric) F-algebra in the category Scala types.
    * Note that `A` is not a generic item type of the resulting algebraic
    * data type. As can be seen below, once we form `Expr` as the least
    * fixpoint of `ExprF`, `A` will go away.
    *
    * @tparam A argument of the endofunctor
    */
  enum ExprF[+A] derives CanEqual:
    case Constant(value: Int) extends ExprF[Nothing]
    case Variable(name: String) extends ExprF[Nothing]
    case UMinus(expr: A)
    case Plus(left: A, right: A)
    case Minus(left: A, right: A)
    case Times(left: A, right: A)
    case Div(left: A, right: A)
    case Mod(left: A, right: A)
    case Block(expressions: List[A])
    case Cond(guard: A, thenBranch: A, elseBranch: A)
    case Loop(guard: A, body: A)
    case Assign(left: String, right: A)
  end ExprF
  
  import ExprF.*

  /**
    * Declaration of `ExprF` as an instance of typeclass `Functor` in Cats.
    * This requires us to define `map`.
    */
  given Functor[ExprF] = new Functor[ExprF]:
    def map[A, B](fa: ExprF[A])(f: A => B): ExprF[B] = fa match
      case e @ Constant(_) => e
      case e @ Variable(_) => e
      case UMinus(r)       => UMinus(f(r))
      case Plus(l, r)      => Plus(f(l), f(r))
      case Minus(l, r)     => Minus(f(l), f(r))
      case Times(l, r)     => Times(f(l), f(r))
      case Div(l, r)       => Div(f(l), f(r))
      case Mod(l, r)       => Mod(f(l), f(r))
      case Block(es)       => Block(es.map(f))
      case Cond(g, t, e)   => Cond(f(g), f(t), f(e))
      case Loop(g, b)      => Loop(f(g), f(b))
      case Assign(l, r)    => Assign(l, f(r))

  /**
    * Declaration of `ExprF` as an instance of typeclass `Traverse` in Cats.
    * This requires us to define `traverse`.
    */
  given Traverse[ExprF] = new Traverse[ExprF]:
    import cats.*
    import cats.implicits.* // η = point, ∘ = map, ⊛ = apply2
    override def traverse[G[_]: Applicative, A, B](fa: ExprF[A])(f: A => G[B]): G[ExprF[B]] = fa match
      case c @ Constant(_) => c.pure[G]
      case v @ Variable(_) => v.pure[G]
      case UMinus(r)       => f(r).map(UMinus(_))
      case Plus(l, r)      => (f(l), f(r)).mapN(Plus(_, _))
      case Minus(l, r)     => (f(l), f(r)).mapN(Minus(_, _))
      case Times(l, r)     => (f(l), f(r)).mapN(Times(_, _))
      case Div(l, r)       => (f(l), f(r)).mapN(Div(_, _))
      case Mod(l, r)       => (f(l), f(r)).mapN(Mod(_, _))
      case Block(es)       => es.traverse(f).map(Block(_))
      case Cond(g, t, e)   => (f(g), f(t), f(e)).mapN(Cond(_, _, _))
      case Loop(g, b)      => (f(g), f(b)).mapN(Loop(_, _))
      case Assign(l, r)    => f(r).map(Assign(l, _))

    // TODO working implementations of foldRight and foldLeft
    // see also expressions-algebraic-scala
    def foldRight[A, B](fa: ExprF[A], lb: Eval[B])(f: (A, Eval[B]) => Eval[B]): Eval[B] = ???
    def foldLeft[A, B](fa: ExprF[A], b: B)(f: (B, A) => B): B = ???

  given [A](using Eq[A]): Eq[ExprF[A]] = Eq.fromUniversalEquals

  given [A](using Show[A]): Show[ExprF[A]] = Show.fromToString

  /** Least fixpoint of `ExprF` as carrier object for the initial algebra. */
  type Expr = Fix[ExprF]

  /** Enable typesafe equality for `Expr` (not yet part of `Fix`). */
  given CanEqual[Expr, Expr] = CanEqual.derived

  /** Factory for creating Expr instances. */
  object factory:
    def constant(c: Int) = Fix(Constant(c))
    def variable(n: String) = Fix(Variable(n))
    def uminus(r: Expr) = Fix(UMinus(r))
    def plus(l: Expr, r: Expr) = Fix(Plus(l, r))
    def minus(l: Expr, r: Expr) = Fix(Minus(l, r))
    def times(l: Expr, r: Expr) = Fix(Times(l, r))
    def div(l: Expr, r: Expr) = Fix(Div(l, r))
    def mod(l: Expr, r: Expr) = Fix(Mod(l, r))
    def block(es: Expr*) = Fix(Block(es.toList))
    def cond(g: Expr, t: Expr, e: Expr) = Fix(Cond(g, t, e))
    def loop(g: Expr, b: Expr) = Fix(Loop(g, b))
    def assign(l: String, r: Expr) = Fix(Assign(l, r))
  end factory
  
end ast