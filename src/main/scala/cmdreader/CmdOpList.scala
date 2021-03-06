package cmdreader

import scala.collection.mutable.HashMap

class CmdOpList(libname: String) {
  def getLib(): String = libname
  var opList: HashMap[String, CommandOperator] = new HashMap[String, CommandOperator]()
  def loadOp(c: CommandOperator) {
    val opn = c.getOpAlias
    opList(opn) = c
    println(s"Loaded operator $opn")
  }
}