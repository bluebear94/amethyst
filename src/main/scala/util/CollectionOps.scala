package util
import types._
import java.math.BigInteger
import scala.collection.mutable._

object CollectionOps {
  def decodeToList(t: Type): List[Type] = {
    t match {
      case t: TMountain => {
        val n = t.getVal
        List.range(0, n.bitCount).map(b => BTI.btl(n.testBit(b)))
      }
      case t: THill => {
        val n = t.getVal
        List.range(0, 64).map(b => BTI.btl((n & (1L << b)) != 0L))
      }
      case t: TString => {
        t.getVal.toCharArray.toList.map(i => new THill(i))
      }
      case t: LList => {
        t.l.toList
      }
      case t: TByteString => {
        t.a.toList.map(THill(_))
      }
    }
  }
  def encodeFromList(l: List[Type], mode: Int): Type = {
    mode match {
      case 1 => {
        var n = BigInteger.ZERO
        for (e <- l) {
          e match {
            case e: TNumerical => {
              n = n.shiftLeft(1).add(BigInteger.valueOf(e.intValue))
            }
            case _ => return new TError(1)
          }
        }
        TMountain(n)
      }
      case 2 => {
        new THill(l.map(_ match {
          case e: TNumerical => {
            e.intValue
          }
          case _ => return new TError(1)
        }).foldLeft(0L)((a, b) => (a << 1) + b))
      }
      case 3 => {
        new TString(new String(l.map(_ match {
          case e: TNumerical => {
            e.intValue.toChar
          }
          case _ => return new TError(1)
        }).toArray))
      }
      case 5 =>
        new LArray(l.to[ArrayBuffer])
      case 6 =>
        new LLinked(l.to[ListBuffer])
      case 9 => {
        new TByteString(l.map(_.asInstanceOf[TNumerical].intValue.toByte).toArray)
      }
    }
  }
  def ctv[T](f: (List[Type]) => T): (Type) => T = {
    (t: Type) =>
      f(decodeToList(t))
  }
  def ctc(f: (List[Type]) => List[Type]): (Type) => Type = {
    (t: Type) =>
      encodeFromList(ctv(f)(t), t.getType)
  }
  def findAllOccurrences[T](x: T, l: List[T]): List[Int] = l match {
    case Nil => Nil
    case f :: r => {
      val rr = findAllOccurrences(x, r).map(_ + 1)
      if (f == x) 0 :: rr else rr
    }
  }
  def findAllOccurrences[T](x: List[T], l: List[T], overlapping: Boolean = true): List[Int] = {
    val n = if (overlapping) 1 else x.length
    if (l.startsWith(x)) 0 :: findAllOccurrences(x, l.drop(n), overlapping).map(_ + n)
    else findAllOccurrences(x, l.tail, overlapping)
  }
}