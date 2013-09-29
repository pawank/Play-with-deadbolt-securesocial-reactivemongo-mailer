package in.efoundry.utils

import org.scala_tools.time.Imports._

import akka.actor._
import akka.actor.Actor

import play.api.Logger
import play.api.Play.current

import org.apache.commons.mail.{HtmlEmail, DefaultAuthenticator, Email, SimpleEmail}
import java.util.Properties

object EfoundryEmail { 
  val emailSystem = ActorSystem("EmailSystem")
  val emailActor = emailSystem.actorOf(Props[EfoundryEmail])

  def send(msg:EmailMessage) = emailActor ! msg
  def send(msg:HtmlEmailMessage) = emailActor ! msg

  def test() = { 
    EfoundryEmail.send(HtmlEmailMessage(to=Seq("pawan.kumar@gmail.com"),subject="Test Email",body="<b>Dear pawan</b><br>This is a test<br><br><hr>", from="Pawan Kumar <pawan.kumar@gmail.com>"))
  }
}

sealed trait EmailBaseMessage
case class EmailMessage(to:Seq[String],subject:String,body:String) extends EmailBaseMessage{
}
case class HtmlEmailMessage(to:Seq[String],subject:String,body:String, from:String, bcc:Seq[String] = Seq.empty) extends EmailBaseMessage

class EfoundryEmail extends Actor { 
  def receive = { 
    case EmailMessage(to,sub,body) =>
      val email:HtmlEmail = new HtmlEmail()
    
    email.setHostName(EfoundryConfig.config.getString("mail.hostname"))
    email.setSmtpPort(EfoundryConfig.config.getInt("mail.port"))
    email.setAuthenticator(new DefaultAuthenticator(EfoundryConfig.config.getString("mail.user"), EfoundryConfig.config.getString("mail.password")))
    email.setTLS(true)
    email.addTo(to(0))
    email.setFrom(EfoundryConfig.config.getString("mail.from"), EfoundryConfig.config.getString("mail.sign"))
    email.setSubject(sub)
    email.setTextMsg(body)
    email.send()
    case HtmlEmailMessage(to,sub,body,from,bcc) =>
    val useTypesafeMailPlugin = true
    if (useTypesafeMailPlugin) {
     import com.typesafe.plugin._
     val mail = use[MailerPlugin].email
     mail.setSubject(sub)
     mail.addRecipient(to:_*)
     //or use a list
     mail.addBcc(bcc:_*)
      mail.addFrom(from)
     //sends html
     mail.sendHtml(body)
     //sends text/text
     //mail.send( "text" )
     //sends both text and html
     //mail.send( "text", "<html>html</html>")
    }
    else {
    val email:HtmlEmail = new HtmlEmail()
    email.setHostName(EfoundryConfig.config.getString("mail.hostname"))
    email.setSmtpPort(EfoundryConfig.config.getInt("mail.port"))
    email.setAuthenticator(new DefaultAuthenticator(EfoundryConfig.config.getString("mail.user"), EfoundryConfig.config.getString("mail.password")))
    email.setTLS(true)
    to.foreach(t =>     email.addTo(t))
    if (from.size > 0)
        email.setFrom(from)
    else
      email.setFrom(EfoundryConfig.config.getString("mail.from"), EfoundryConfig.config.getString("mail.sign"))
    email.setSubject(sub)
    email.setHtmlMsg(body)
    email.send()
    bcc.foreach(t => {
      val email:HtmlEmail = new HtmlEmail()
      email.setHostName(EfoundryConfig.config.getString("mail.hostname"))
      email.setSmtpPort(EfoundryConfig.config.getInt("mail.port"))
      email.setAuthenticator(new DefaultAuthenticator(EfoundryConfig.config.getString("mail.user"), EfoundryConfig.config.getString("mail.password")))
      email.setTLS(true)
      email.addTo(t)
      if (from.size > 0)
        email.setFrom(from)
      else
        email.setFrom(EfoundryConfig.config.getString("mail.from"), EfoundryConfig.config.getString("mail.sign"))
      email.setSubject(sub)
      email.setHtmlMsg(body)
      email.send()
      }
    )
  }
    case _ =>
      Logger.warn("Invalid message is received")
  }



}



