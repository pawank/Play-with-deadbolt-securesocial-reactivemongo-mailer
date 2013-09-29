package in.efoundry.utils

import org.scala_tools.time.Imports._

import akka.actor._
import akka.actor.Actor

import play.api.Logger
import play.api.Play.current


object EfoundryLogging { 
  lazy val config = play.Play.application().configuration()

  val isLog = config.getBoolean("app.log.enable")
  type INFO = String 
  type DEBUG = String 
  type WARN = String 
  type ERROR = String 

  val loggingSystem = ActorSystem("LoggingSystem")
  val loggingActor = loggingSystem.actorOf(Props[EfoundryLogging])

  def log(msg:BaseMessage) = if (isLog) loggingActor ! msg
  def info(msg:Any*) = if (isLog)loggingActor ! InfoMessage(createMessage(msg))
  def debug(msg:Any*) = if (isLog)loggingActor ! DebugMessage(createMessage(msg))
  def warn(msg:Any*) = if (isLog)loggingActor ! WarnMessage(createMessage(msg))
  //Always enable error messages
  def err(msg:Any*) = loggingActor ! ErrorMessage(createMessage(msg))
  //def err(msg:Any*) = if (isLog)loggingActor ! ErrorMessage(createMessage(msg))
  def error(msg:Any*) = loggingActor ! ErrorMessage(createMessage(msg))
  
  def logany(msg: Any*) = { 
    if (isLog) { 
      var s = ""
      for(a <- msg) { 
	s = s + a.toString + " "
      }
      s = s + " at " + DateTime.now
      loggingActor ! DebugMessage(s)
    }
  }
  private def createMessage(msg: Any*) = { 
    if (isLog) { 
      msg filter (_.toString.nonEmpty) mkString " "
    }
    else 
      ""
  }
}

sealed trait BaseMessage
case class InfoMessage(msg: String) extends BaseMessage{ 
  override def toString = msg + " at "  + DateTime.now
  def log = Logger.info(toString)
}
case class DebugMessage(msg: String) extends BaseMessage{ 
  override def toString = msg + " at "  + DateTime.now
  def log = Logger.debug(toString)
}
case class WarnMessage(msg: String) extends BaseMessage{ 
  override def toString = msg + " at "  + DateTime.now
  def log = Logger.warn(toString)
}
case class ErrorMessage(msg: String) extends BaseMessage{ 
  override def toString = msg + " at "  + DateTime.now
  def log = Logger.error(toString)
}

class EfoundryLogging extends Actor { 
  //def ||[T](t:T <:< BaseMessage) = self ! t

  def receive = { 
    case m@InfoMessage(msg) =>
      m.log
    case m@DebugMessage(msg) =>
      m.log
    case m@WarnMessage(msg) =>
      m.log
    case m@ErrorMessage(msg) =>
      m.log
    case _ =>
      Logger.warn("Invalid message is received")
  }



}



