package eloro

import types._
import cmdreader._
import scala.util.parsing.combinator._
import scala.collection.immutable.HashMap
import scala.util.parsing.input._
import java.io._
import java.util.Scanner
/**
 * Documentation generator. Called through the $:help command.
 * @author bluebear94
 */
object DocGen {
  def getProperties(cmd: Command): String = {
    val arglist = List.range(0, 20).filter(cmd.isValidArg0(_))
    val argstr = "\nValid argcounts: " + arglist.mkString(", ") + (if (arglist.filter(_ > 10).isEmpty) "" else ", ...")
    val opProperties = cmd match {
      case op: CommandOperator => {
        val alias = op.getOpAlias
        val hasae = op.hasAssignmentEquiv
        val db = op.getDoubleBase
        val prec = op.getPrecedence
        val un = op.isUnary
        val isrtl = op.isReversed
        val dm = db match {
          case Some(t) => {
            "\n" + alias + alias + "a is equivalent to a " + alias + "= " + t
          }
          case None => ""
        }
        "\nOperator equivalent: " + alias + "\nPrecedence:  " + prec + "\nUnary: " + (if (un) "Yes" else "No") + "\n" +
          (if (isrtl) "Right-to-left" else "Left-to-right") + (if (hasae) "\nAssignment Equiv.: " + alias + "=") + dm
      }
      case _ => ""
    }
    cmd.getName + argstr + opProperties
  }
  val cmdl: HashMap[String, (List[String]) => String] = HashMap(
    ("desc", _.head),
    ("vector", _ => "If applied to one or more lists, this function will apply element-by-element."),
    ("throws", (a: List[String]) => "Throws: " + a.mkString(", ")))
  def out(c: String, a: List[String]) = {
    if (cmdl.isDefinedAt(c)) cmdl(c)(a)
    else throw new NoSuchDocCommandException("Command not found: " + c)
  }
  def parseCmd(ln: String) = {
    val p = new EP
    import p._
    val pp = phrase(line)
    val ret = pp(new CharSequenceReader(ln))
    ret match {
      case Success(res, t) => out(res._1, res._2)
      case NoSuccess(msg, t) => {
        println(ret)
        throw new RuntimeException("Could not parse: " + msg + " in line: " + ln)
      }
    }
  }
  def getHelp(name: String) = {
    if (name.charAt(0) != '$' || name.indexOf(":") == -1) "Invalid command name"
    else {
      val bp = getProperties(Global.getCmdno(name.substring(1)))
      val l = name.substring(1, name.indexOf(":"))
      val n = name.substring(name.indexOf(":") + 1)
      val lib = if (l == "") "std" else l
      val s = new Scanner(new File("docs/" + lib + ".txt"))
      var txt = ""
      while (txt != ("#" + n) && s.hasNextLine) {
        txt = s.nextLine
      }
      var details = ""
      def readAndAppend = {
        txt = s.nextLine
        if (!txt.startsWith("#")) details += "\n" + parseCmd(txt)
      }
      if (s.hasNextLine) readAndAppend
      while (!txt.startsWith("#") && s.hasNextLine) {
        readAndAppend
      }
      bp + details
    }
  }
}
class NoSuchDocCommandException(msg: String) extends RuntimeException
/**
 * Parser class for doc commands.
 */
class EP extends RegexParsers with PackratParsers {
  override def skipWhitespace = false
  lazy val quoted: PackratParser[String] = "\"" ~> "[^\"]*".r <~ "\""
  lazy val unquoted: PackratParser[String] = regex("[^\"\\s]*".r)
  lazy val args: PackratParser[List[String]] = repsep(quoted | unquoted, " ")
  lazy val lineA: PackratParser[(String, List[String])] = unquoted ~ " " ~ args ^^ {
    case uq ~ _ ~ a => (uq, a)
  }
  lazy val lineB: PackratParser[(String, List[String])] = unquoted ^^ {s: String => (s, Nil)}
  lazy val line = lineA | lineB
}