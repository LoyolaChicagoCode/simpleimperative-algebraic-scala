package edu.luc.cs.cs372.simpleImperative

object TestFixtures {

  import ast.ExprFactory._
  import evaluate.Cell

  def store() = Map[String, Cell](
    "x" -> Cell(2),
    "y" -> Cell(3),
    "r" -> Cell(0)
  )

  val e1 = plus(constant(3), constant(4))

  val e2 = assign("x", constant(4))

  val e3 =
    block(
      assign("r", plus(variable("r"), variable("x"))),
      assign("y", minus(variable("y"), constant(1)))
    )

  val e4a =
    cond(constant(4),
      assign("r", plus(variable("r"), variable("x"))),
      assign("y", minus(variable("y"), constant(1)))
    )

  val e4b =
    cond(constant(0),
      assign("r", plus(variable("r"), variable("x"))),
      assign("y", minus(variable("y"), constant(1)))
    )

  val e5 =
    loop(variable("y"),
      block(
        assign("r", plus(variable("r"), variable("x"))),
        assign("y", minus(variable("y"), constant(1)))
      )
    )
}