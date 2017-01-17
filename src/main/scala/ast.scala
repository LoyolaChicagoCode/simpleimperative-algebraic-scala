package edu.luc.cs.cs372.simpleimperative

/*
 * In this example, we represent simple imperative programs
 * as trees (initial algebra for the endofunctor defined next).
 */

object ast {

  import scalaz.{ Equal, Functor, Show }
  import scalaz.std.list._
  import scalamu._

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
   * Implicit value for declaring `ExprF` as an instance of
   * typeclass `Functor` in scalaz. This requires us to define
   * `map`.
   */
  implicit object ExprFFunctor extends Functor[ExprF] {
    def map[A, B](fa: ExprF[A])(f: A => B): ExprF[B] = fa match {
      case e @ Constant(v) => e
      case e @ Variable(n) => e
      case UMinus(r) => UMinus(f(r))
      case Plus(l, r) => Plus(f(l), f(r))
      case Minus(l, r) => Minus(f(l), f(r))
      case Times(l, r) => Times(f(l), f(r))
      case Div(l, r) => Div(f(l), f(r))
      case Mod(l, r) => Mod(f(l), f(r))
      case Block(es) => Block(es map f)
      case Cond(g, t, e) => Cond(f(g), f(t), f(e))
      case Loop(g, b) => Loop(f(g), f(b))
      case Assign(l, r) => Assign(l, f(r))
    }
  }

  /**
   * Implicit value for declaring `ExprF` as an instance of
   * scalaz typeclass `Equal` using structural equality.
   * This enables `===` and `assert_===` on `ExprF` instances.
   */
  implicit def exprFEqual[A](implicit A: Equal[A]): Equal[ExprF[A]] = Equal.equal {
    case (Constant(v), Constant(w)) => v == w
    case (Variable(m), Variable(n)) => m == n
    case (UMinus(r), UMinus(t)) => A.equal(r, t)
    case (Plus(l, r), Plus(s, t)) => A.equal(l, s) && A.equal(r, t)
    case (Minus(l, r), Minus(s, t)) => A.equal(l, s) && A.equal(r, t)
    case (Times(l, r), Times(s, t)) => A.equal(l, s) && A.equal(r, t)
    case (Div(l, r), Div(s, t)) => A.equal(l, s) && A.equal(r, t)
    case (Mod(l, r), Mod(s, t)) => A.equal(l, s) && A.equal(r, t)
    case (Block(r), Block(t)) => Equal[List[A]].equal(r, t)
    case (Cond(g, t, e), Cond(h, u, f)) => A.equal(g, h) && A.equal(t, u) && A.equal(e, f)
    case (Loop(g, b), Loop(h, c)) => A.equal(g, h) && A.equal(b, c)
    case (Assign(l, r), Assign(s, t)) => (l == s) && A.equal(r, t)
    case _ => false
  }

  /** Least fixpoint of `ExprF` as carrier object for the initial algebra. */
  type Expr = µ[ExprF]

  /** Factory for creating Expr instances. */
  object ExprFactory {
    def constant(c: Int) = In[ExprF](Constant(c))
    def variable(n: String) = In[ExprF](Variable(n))
    def uminus(r: Expr) = In[ExprF](UMinus(r))
    def plus(l: Expr, r: Expr) = In[ExprF](Plus(l, r))
    def minus(l: Expr, r: Expr) = In[ExprF](Minus(l, r))
    def times(l: Expr, r: Expr) = In[ExprF](Times(l, r))
    def div(l: Expr, r: Expr) = In[ExprF](Div(l, r))
    def mod(l: Expr, r: Expr) = In[ExprF](Mod(l, r))
    def block(es: Expr*) = In[ExprF](Block(es.toList))
    def cond(g: Expr, t: Expr, e: Expr) = In[ExprF](Cond(g, t, e))
    def loop(g: Expr, b: Expr) = In[ExprF](Loop(g, b))
    def assign(l: String, r: Expr) = In[ExprF](Assign(l, r))
  }
}