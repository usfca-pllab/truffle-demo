// LIR nodes

package lir
import Value.*

import com.oracle.truffle.api.{Option as _, *}
import com.oracle.truffle.api.interop.TruffleObject
import com.oracle.truffle.api.nodes.*
import com.oracle.truffle.api.frame.*
import scala.compiletime.ops.double

// Frames in Truffle are accessed via integer indices
type VarId = Int

// Value extends TruffleObject because we pass values around
abstract class Value extends TruffleObject

object Value:
  case class Ptr(ptsto: Value) extends Value
  case class IntV(value: Int) extends Value
  // function pointer
  case class Function(
      callTarget: CallTarget,
  ) extends Value:
    // this is not overriding Truffle's execute method
    def execute(): Object = ???

// An actual IR function
final case class Function(
  name: String,
  entry: BasicBlock,
  locals: Array[VarInfo],
) extends RootNode(Lir.instance(), Function.frameDescriptor(locals)):
  override def execute(frame: VirtualFrame): Object = entry.execute(frame)


object Function:
  private[Function] def frameDescriptor(locals: Array[VarInfo]) =
    val frameBuilder = FrameDescriptor.newBuilder(locals.size)
    for x <- locals do
      frameBuilder.addSlot(x.typ.kind, x.name, null)

    frameBuilder.build()

enum Typ:
  case Int
  case Ptr

  def kind = this match
    case Int => FrameSlotKind.Int
    case Ptr => FrameSlotKind.Object

case class VarInfo(name: String, typ: Typ)

// TODO: function call stmt?
// TODO: root function call stmt?

final case class BasicBlock(
  val insns: Array[Instruction],
  val term: Terminal
) extends ExecutableNode(Lir.instance()):

  override def execute(frame: VirtualFrame): Object =
    for insn <- insns do
      insn.execute(frame)

    term.execute(frame)
      

abstract class Instruction() extends ExecutableNode(Lir.instance())
final case class Add(lhs: VarId, op1: Operand, op2: Operand) extends Instruction():
  override def execute(frame: VirtualFrame): Object =
    frame.setInt(lhs, op1.executeInt(frame) + op2.executeInt((frame)))
    null
final case class Copy(lhs: VarId, op: Operand) extends Instruction():
  override def execute(frame: VirtualFrame): Object =
    // specialize on the type of the left-hand side
    if frame.getFrameDescriptor().getSlotKind(lhs) == FrameSlotKind.Int then
      frame.setInt(lhs, op.executeInt(frame))
    else
      frame.setObject(lhs, op.execute(frame))
    null

abstract class Terminal() extends ExecutableNode(Lir.instance())
final case class Ret(operand: Option[Operand]) extends Terminal():

  override def execute(frame: VirtualFrame): Object =
    operand.fold(null)(_.execute(frame))
final case class Jump(next: BasicBlock) extends Terminal():

  override def execute(frame: VirtualFrame): Object = next.execute(frame)

abstract class Operand extends ExecutableNode(null):
  def executeInt(frame: VirtualFrame): Int

final case class Var(id: VarId) extends Operand:
  override def execute(frame: VirtualFrame): Object =
    if frame.getFrameDescriptor().getSlotKind(id) == FrameSlotKind.Int then
      Integer.valueOf(frame.getInt(id))
    else
      frame.getObject(id)
  override def executeInt(frame: VirtualFrame): Int =
    println(s"${id}: ${frame.getFrameDescriptor().getSlotKind(id)}")
    frame.getInt(id)

final case class CInt(value: Int) extends Operand:
  override def execute(frame: VirtualFrame): Object = Integer(value)
  override def executeInt(frame: VirtualFrame): Int = value