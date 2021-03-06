package util
import types._
import scala.math.BigInt
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
      case _ => throw new RuntimeException(s"$t cannot be decoded to a list")
    }
  }
  def encodeFromList(l: List[Type], mode: Int): Type = {
    mode match {
      case 1 => {
        var n = 0
        for (e <- l) {
          e match {
            case e: TNumerical => {
              n = (n << 1) + e.intValue
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
  def findAllOccurrences[T](x: List[T], l: List[T], gap: Int): List[Int] = {
    if (l.isEmpty) Nil
    else if (l.startsWith(x)) 0 :: findAllOccurrences(x, l.drop(gap), gap).map(_ + gap)
    else findAllOccurrences(x, l.tail, gap).map(_ + 1)
  }
  def findAllOccurrences[T](x: List[T], l: List[T], overlapping: Boolean = true): List[Int] = {
    findAllOccurrences(x, l, if (overlapping) 1 else x.length)
  }
  def replaceFirstOccurrenceR[T](x: T, y: T, l: List[T], accum: List[T]): Option[List[T]] = l match {
    case Nil => None
    case h :: t => {
      if (h == x) Some((t.reverse :+ y) ++ accum)
      else replaceFirstOccurrenceR(x, y, t, h :: accum)
    }
  }
  def replaceFirstOccurrence[T](x: T, y: T, l: List[T]): Option[List[T]] = replaceFirstOccurrenceR(x, y, l, Nil) map {(l: List[T]) => l.reverse}
  def replaceFirstOccurrenceR[T](x: List[T], y: List[T], l: List[T], accum: List[T]): Option[List[T]] = {
    val xd = x.length
    if (l.isEmpty) None
    else if (l startsWith x) Some(y ++ (l drop xd).reverse ++ accum)
    else replaceFirstOccurrenceR(x, y, l.tail, l.head :: accum)
  }
  def replaceFirstOccurrence[T](x: List[T], y: List[T], l: List[T]): Option[List[T]] = replaceFirstOccurrenceR(x, y, l, Nil) map {(l: List[T]) => l.reverse}
  def replaceAllOccurrencesR[T](x: T, y: T, l: List[T], accum: List[T]): List[T] = l match {
    case Nil => accum
    case h :: t => {
      if (h == x) replaceAllOccurrencesR(x, y, t, y :: accum)
      else replaceAllOccurrencesR(x, y, t, h :: accum)
    }
  }
  def replaceAllOccurrences[T](x: T, y: T, l: List[T]): List[T] = replaceAllOccurrencesR(x, y, l, Nil).reverse
  def replaceAllOccurrencesR[T](x: List[T], y: List[T], l: List[T], accum: List[T]): List[T] = {
    val xd = x.length
    if (l.isEmpty) accum
    else if (l startsWith x) replaceAllOccurrencesR(x, y, l drop xd, y.reverse ++ accum)
    else replaceAllOccurrencesR(x, y, l.tail, l.head :: accum)
  }
  def replaceAllOccurrences[T](x: List[T], y: List[T], l: List[T]): List[T] = replaceAllOccurrencesR(x, y, l, Nil).reverse
}
