package cmdreader.std

import cmdreader.Global

class Loader {
  def load = {
    Global.liblist("std").loadCmd("OAdd")
    Global.liblist("std").loadCmd("OSubt")
    Global.liblist("std").loadCmd("OAvg")
    Global.liblist("std").loadCmd("OMult")
    Global.liblist("std").loadCmd("ODiv")
    Global.liblist("std").loadCmd("OIDiv")
    Global.liblist("std").loadCmd("OMod")
    Global.liblist("std").loadCmd("HLuna")
    Global.liblist("std").loadCmd("OEq")
    Global.liblist("std").loadCmd("ONeq")
    Global.liblist("std").loadCmd("OLt")
    Global.liblist("std").loadCmd("OLe")
    Global.liblist("std").loadCmd("OGt")
    Global.liblist("std").loadCmd("OGe")
    Global.liblist("std").loadCmd("Car")
    Global.liblist("std").loadCmd("Cdr")
    Global.liblist("std").loadCmd("Cons")
    Global.liblist("std").loadCmd("ECar")
    Global.liblist("std").loadCmd("ECdr")
    Global.liblist("std").loadCmd("ECons")
    Global.liblist("std").loadCmd("UONegate")
    Global.liblist("std").loadCmd("UONot")
    Global.liblist("std").loadCmd("OTt")
    Global.liblist("std").loadCmd("OAnd")
    Global.liblist("std").loadCmd("OOr")
    Global.liblist("std").loadCmd("OXor")
    Global.liblist("std").loadCmd("Disassembly")
    Global.liblist("std").loadCmd("OAndB")
    Global.liblist("std").loadCmd("OOrB")
    Global.liblist("std").loadCmd("OXorB")
    Global.liblist("std").loadCmd("Now")
    Global.liblist("std").loadCmd("Sub")
    Global.liblist("std").loadCmd("Rrbzo")
    Global.liblist("std").loadCmd("Abs")
    Global.liblist("std").loadCmd("Floor")
    Global.liblist("std").loadCmd("FPart")
    Global.liblist("std").loadCmd("OShl")
    Global.liblist("std").loadCmd("OShr")
    Global.liblist("std").loadCmd("OMap")
    Global.liblist("std").loadCmd("Exp")
    Global.liblist("std").loadCmd("Ln")
    Global.liblist("std").loadCmd("Pi")
    List("Sin", "Cos", "Tan").map({
      s => List(s, s + "h", "A" + s, "A" + s + "h").map(Global.liblist("std").loadCmd(_))
    })
    Global.liblist("std").loadCmd("Expr")
  }
}