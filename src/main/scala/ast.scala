package edu.luc.cs.cs371.simpleimperative

/*
 * In this example, we represent simple imperative programs
 * as trees (initial algebra for the endofunctor defined next).
 */

object ast {

  import scalaz.{Applicative, Equal, Functor, Traverse}
  import scalaz.std.list._
  import matryoshka.Delay
  import matryoshka.data.Fix

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
  sealed trait ExprF[+A]
  case class Constant(value: Int) extends ExprF[Nothing]
  case class Variable(name: String) extends ExprF[Nothing]
  case class UMinus[A](expr: A) extends ExprF[A]
  case class Plus[A](left: A, right: A) extends ExprF[A]
  case class Minus[A](left: A, right: A) extends ExprF[A]
  case class Times[A](left: A, right: A) extends ExprF[A]
  case class Div[A](left: A, right: A) extends ExprF[A]
  case class Mod[A](left: A, right: A) extends ExprF[A]
  case class Block[A](expressions: List[A]) extends ExprF[A]
  case class Cond[A](guard: A, thenBranch: A, elseBranch: A) extends ExprF[A]
  case class Loop[A](guard: A, body: A) extends ExprF[A]
  case class Assign[A](left: String, right: A) extends ExprF[A]

  /**
    * Implicit object for declaring `ExprF` as an instance of
    * typeclass `Functor` in scalaz. This requires us to define
    * `map`.
    */
  object exprFFunctor extends Functor[ExprF] {
    import scalaz.syntax.functor._ // ∘ = map
    def map[A, B](fa: ExprF[A])(f: A => B): ExprF[B] = fa match {
      case e @ Constant(_) => e
      case e @ Variable(_) => e
      case UMinus(r)       => UMinus(f(r))
      case Plus(l, r)      => Plus(f(l), f(r))
      case Minus(l, r)     => Minus(f(l), f(r))
      case Times(l, r)     => Times(f(l), f(r))
      case Div(l, r)       => Div(f(l), f(r))
      case Mod(l, r)       => Mod(f(l), f(r))
      case Block(es)       => Block(es ∘ f)
      case Cond(g, t, e)   => Cond(f(g), f(t), f(e))
      case Loop(g, b)      => Loop(f(g), f(b))
      case Assign(l, r)    => Assign(l, f(r))
    }
  }

  /**
    * Object for declaring `ExprF` as an instance of
    * typeclass `Traverse` in scalaz. This requires us to define
    * `traverseImpl`.
    */
  implicit object exprFTraverse extends Traverse[ExprF] {
    import scalaz.syntax.applicative._ // η = point, ∘ = map, ⊛ = apply2
    def traverseImpl[G[_], A, B](fa: ExprF[A])(f: A => G[B])(implicit a: Applicative[G]): G[ExprF[B]] = fa match {
      case e @ Constant(_) => (e: ExprF[B]).η[G[?]]
      case e @ Variable(_) => (e: ExprF[B]).η[G[?]]
      case UMinus(r)       => f(r) ∘ (UMinus(_))
      case Plus(l, r)      => (f(l) ⊛ f(r))(Plus(_, _))
      case Minus(l, r)     => (f(l) ⊛ f(r))(Minus(_, _))
      case Times(l, r)     => (f(l) ⊛ f(r))(Times(_, _))
      case Div(l, r)       => (f(l) ⊛ f(r))(Div(_, _))
      case Mod(l, r)       => (f(l) ⊛ f(r))(Mod(_, _))
      case Block(es)       => a.traverse(es)(f) ∘ (Block(_))
      case Cond(g, t, e)   => (f(g) ⊛ f(t) ⊛ f(e))(Cond(_, _, _))
      case Loop(g, b)      => (f(g) ⊛ f(b))(Loop(_, _))
      case Assign(l, r)    => f(r) ∘ (Assign(l, _))
    }
  }

  /**
    * Implicit value for declaring `ExprF` as an instance of
    * scalaz typeclass `Equal` using structural equality.
    * This enables `===` and `assert_===` on `ExprF` instances.
    */
  implicit object exprFEqualD extends Delay[Equal, ExprF] {
    override def apply[T](eq: Equal[T]) = Equal.equalA[ExprF[T]]
  }

  /** Least fixpoint of `ExprF` as carrier object for the initial algebra. */
  type Expr = Fix[ExprF]

  /** Factory for creating Expr instances. */
  object factory {
    def constant(c: Int) = Fix[ExprF](Constant(c))
    def variable(n: String) = Fix[ExprF](Variable(n))
    def uminus(r: Expr) = Fix[ExprF](UMinus(r))
    def plus(l: Expr, r: Expr) = Fix[ExprF](Plus(l, r))
    def minus(l: Expr, r: Expr) = Fix[ExprF](Minus(l, r))
    def times(l: Expr, r: Expr) = Fix[ExprF](Times(l, r))
    def div(l: Expr, r: Expr) = Fix[ExprF](Div(l, r))
    def mod(l: Expr, r: Expr) = Fix[ExprF](Mod(l, r))
    def block(es: Expr*) = Fix[ExprF](Block(es.toList))
    def cond(g: Expr, t: Expr, e: Expr) = Fix[ExprF](Cond(g, t, e))
    def loop(g: Expr, b: Expr) = Fix[ExprF](Loop(g, b))
    def assign(l: String, r: Expr) = Fix[ExprF](Assign(l, r))
  }
}
