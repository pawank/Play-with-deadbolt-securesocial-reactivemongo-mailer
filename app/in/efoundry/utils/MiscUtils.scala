package in.efoundry.utils

import org.scala_tools.time.Imports._
import java.security.MessageDigest
import org.apache.commons.codec.binary.Base64
import org.apache.commons.io.FileUtils
import org.joda.time.DateTime

import com.github.kevinsawicki.timeago._

object MiscUtils {
  val emailRegex = """(?i)([a-zA-Z0-9!#$%*+'/=?^_-`{|}~.\x26]+)@([a-zA-Z0-9._-]+[a-zA-Z]{2,4})"""
  val nameRegex = """(?i)([a-zA-Z\s\']{1,30})"""
  val phoneRegex = """(?i)([0-9\+\-\.\(\)\s]{10,14})"""
  val allUpperCaseCharsRegex = """[A-Z]""".r

  def getTimeAgo(dateInSeconds:Long):String = {
    val t = new TimeAgo()
    val current = System.currentTimeMillis()
    t.timeAgo(dateInSeconds)
  }

  def getTimeAgo(date:DateTime):String = {
    getTimeAgo(date.millis)
  }

  def isLowerCaseString(input:String):Boolean = allUpperCaseCharsRegex.findFirstIn(input).size <= 0

  def capitalize(s: String) = {
    if (s == null || s.isEmpty) s
    else s(0).toUpper.toString + s.substring(1)
  }

  def capitalizeAllWords(s: String, delim:String = " ") = { s.split(delim).map(w => capitalize(w.trim)).mkString(delim) }



  def dateTimeToDDMMYYYY(dt:DateTime,format:String = "dd/MM/yyyy") = dt.toString(format)
  def dateTimeToCustomFormat(dt:DateTime,format:String = "dd MMM") = dt.toString(format)

  //DD, MM, YYYY format
  def toDateTokens(dt:String, splitter:String = "/"):Tuple3[Int,Int,Int] = { 
    val x = dt.split(splitter).toList
    (x(0).toInt, x(1).toInt, x(2).toInt)
  }
  
  def strDateToDateTime(dt:String, splitter:String = "/"):DateTime = { 
    val x = toDateTokens(dt,splitter)
    new DateTime(x._3, x._2, x._1, 0, 0)
  }

  def toDateTimeFromString(dt:String, splitter:String = "-"):DateTime = { 
    val x = dt.split(splitter).toList
    new DateTime(x(0).toInt, x(1).toInt, x(2).toInt, 0, 0)
  }

  def dateTimeToDateTokens(dt:DateTime):Tuple3[Int,Int,Int] = (dt.day.get, dt.month.get, dt.year.get)
  def makeDateTimeWrtNow(additionalYears:Int = 0, additionalMonths:Int = 0, additionalDays:Int = 0) = {
    val n = DateTime.now
    val toks = dateTimeToDateTokens(n)
    val dtnew = new DateTime(toks._3 + additionalYears, toks._2 + additionalMonths, toks._1 + additionalDays, n.hour.get, n.minute.get)
    dtnew
  }

  def md5(s: String): String = {
    val md5 = MessageDigest.getInstance("MD5")
    md5.reset()
    md5.update(s.getBytes)

    md5.digest().map(0xFF & _).map { "%02x".format(_) }.foldLeft("") { _ + _ }
  }

}
