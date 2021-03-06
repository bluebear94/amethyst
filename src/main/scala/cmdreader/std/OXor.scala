package cmdreader.std

import cmdreader._
import types._
import util._
import java.math.BigInteger

class OXor extends CommandOperator {
  def getName(): String = "xor"
  def getOpAlias() = "^^"
  def isValidArg0(n: Int) = true
  def apply(args: Array[Type]): Type = {
    BTI.bti(args.map(_.toBoolean).fold(false)(_ ^ _))
  }
  def getPrecedence() = PStandard.DISJUNCTION
  def isReversed() = false
  def hasAssignmentEquiv() = false
  def getDoubleBase() = None
}