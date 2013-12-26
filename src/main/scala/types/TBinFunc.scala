package types

import run.RunningInstance

class TBinFunc(bytecode: Array[Byte], source: String, ci: RunningInstance) extends TFunction {
  def apply(args: Array[Type]): Type = new TVoid // TODO, yet another stub
  def getBytecode(): Array[Byte] = bytecode
  override def equals(that: Any): Boolean = {
    that match {
      case other: TBinFunc => other.getBytecode() == bytecode
      case _ => false
    }
  }
  override def toString(): String = "λ\n" + source + "\nEndλ"
  def >/< = new TBinFunc(bytecode.clone, new String(source), ci)
}