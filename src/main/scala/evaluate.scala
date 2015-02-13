package edu.luc.cs.cs372.simpleimperative

import scala.util.{Failure, Success, Try}
import ast._

/** An interpreter for expressions and statements. */
object evaluate {

  /** A cell for storing a value (either a number or an object). */
  case class Cell(var value: Value) {
    def get: Value = value
    def set(value: Value): Unit = this.value = value
  }

  /** A companion object defining a useful Cell instance. */
  object Cell {
    def apply(i: Int): Cell = Cell(Num(i)) // Left -> number, Right -> object
    val NULL = Cell(0)
  }

  /** A object (instance) is a mapping from variable names to storage cells. */
  type Instance = Map[String, Cell]

  /** A memory store is a special top-level object (instance). */
  type Store = Instance

  /** A run-time value is always a number for now. We represent NULL as 0. */
  sealed trait Value
  case class Num(value: Int) extends Value

  /** The result of a successful or failed computation. */
  type Result = Try[Cell]

  /** A delayed, on-demand computation. */
  type Thunk = () => Result
  // http://jtauber.com/blog/2008/03/30/thunks,_trampolines_and_continuation_passing/

  /** Looks up a variable in memory. */
  def lookup(store: Store)(name: String): Result =
    store.get(name).fold(
      Failure(new NoSuchFieldException(name)): Result
    )(
      Success(_)
    )

  /** Evaluates the two operands and applies the operator. */
  def binOp(left: Thunk, right: Thunk, op: (Int, Int) => Int): Result =
    for { Cell(Num(l)) <- left() ; Cell(Num(r)) <- right() } yield Cell(Num(op(l, r)))

  /**
   * Evaluates a program within the context of a given store.
   *
   * Note the absence of explicit recursion. Traversal of the entire
   * tree is achieved by plugging this F-algebra into the
   * universal catamorphism (generalized fold).
   */
  val evalAlgebra: Store => scalamu.Algebra[ExprF, Thunk] = (store: Store) => {
    case Constant(value) => () => Success(Cell(Num(value)))
    case Plus(left, right) => () => binOp(left, right, _ + _)
    case Minus(left, right) => () => binOp(left, right, _ - _)
    case Times(left, right) => () => binOp(left, right, _ * _)
    case Div(left, right) => () => binOp(left, right, _ / _)
    case Mod(left, right) => () => binOp(left, right, _ % _)
    case UMinus(expr) => () => for { Cell(Num(e)) <- expr() } yield Cell(Num(-e))
    case Variable(name) => () => lookup(store)(name)
    case Assign(left, right) =>
      () => for {
        lvalue <- lookup(store)(left)
        Cell(rvalue) <- right()
        _ <- Success(lvalue.set(rvalue))
      } yield Cell.NULL
    case Cond(guard, thenBranch, elseBranch) =>
      () => guard() match {
        case Success(Cell.NULL) => elseBranch()
        case Success(_) => thenBranch()
        case f@Failure(_) => f
      }
    case Block(expressions) =>
      // http://stackoverflow.com/questions/12892701/abort-early-in-a-fold
      def doSequence: Result = {
        val i = expressions.iterator
        var result: Cell = Cell.NULL
        while (i.hasNext) {
          i.next()() match {
            case Success(r) => result = r
            case f@Failure(_) => return f
          }
        }
        Success(result)
      }
      () => doSequence
    case Loop(guard, body) => {
      def doLoop: Result = {
        while (true) {
          guard() match {
            case Success(Cell.NULL) => return Success(Cell.NULL)
            case Success(v) => body()
            case f@Failure(_) => return f
          }
        }
        Success(Cell.NULL)
      }
      () => doLoop
    }
  }

  import scalamu.ToMuOps

  /** Evaluates the program by recursively applying the algebra to the tree. */
  def evaluate(store: Store)(expr: Expr): Result = expr.cata(evalAlgebra(store))()
}