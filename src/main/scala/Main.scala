import lir.*

@main def main(): Unit =
  val lir = Lir()

  val program = Function("main", BasicBlock(
    Array(
      Copy(0, CInt(5)),
      Copy(1, CInt(3)),
      Add(2, Var(0), Var(1)),
      Add(0, Var(2), CInt(10))
    ),
    Ret(Some(Var(0)))
  ),
  Array(VarInfo("x", Typ.Int), VarInfo("y", Typ.Int), VarInfo("z", Typ.Int))
  )

  print(program.getCallTarget().call())
