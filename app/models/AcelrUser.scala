package models

/**
 * Created with IntelliJ IDEA.
 * User: pawanacelr
 * Date: 17/09/13
 * Time: 3:33 PM
 * To change this template use File | Settings | File Templates.
 */

import org.scala_tools.time.Imports._
import securesocial.core._
import securesocial.core.providers._
import scala.Some
import play.libs.Scala

//Play Salat
import com.novus.salat._
import com.novus.salat.annotations._
import com.novus.salat.dao._
import com.mongodb.casbah.Imports._
import com.mongodb.casbah.MongoConnection
import mongoContext._
import play.api.Play.current
import org.bson.types.ObjectId

import be.objectify.deadbolt.core.models.Subject

object AcelrRole {
  val ADMIN = "ADMIN"
  val GUEST = "GUEST"
  val EMPLOYEE = "EMPLOYEE"
  val ANNONYMOUS = "ANNONYMOUS"

  def isAdminRole(roles:Seq[String]):Boolean = {
    import scalaz._
    import Scalaz._
    roles.filter(a => a === AcelrRole.ADMIN).size > 0
  }

}

case class AcelrRole(titles:Seq[String])

case class AcelrUser(id: ObjectId = new ObjectId,fullName:String, email:String, identity:UserIdFromProvider, method:AuthenticationMethod, roles:Seq[String] = Seq(AcelrRole.GUEST), createdOn:DateTime = DateTime.now, passwordInfo:Option[PasswordInfo] = None)
//@Salat
//case class AcelrUser(id: ObjectId = new ObjectId,fullName:String, email:String, identity:IdentityId, socialUser:Option[Identity], createdOn:DateTime = DateTime.now)

object AcelrUser {
  com.mongodb.casbah.commons.conversions.scala.RegisterConversionHelpers()
  com.mongodb.casbah.commons.conversions.scala.RegisterJodaTimeConversionHelpers()

  val collection = MongoConnection()("acelr")("users")
  val dao = new SalatDAO[AcelrUser, ObjectId](collection = collection) {}

  val adminEmailList:List[String] = List("pawan@acelrtech.com","pawan.kumar@gmail.com","thomson@acelrtech.com","thomon.skariah@acelrtech.com")
  def isAdmin(email:String):Boolean = {
    import scalaz._
    import Scalaz._
    adminEmailList.filter(a => a === email).size > 0
  }
  def toAcelrUser(socialuser:Identity):AcelrUser = {
    val roles = if (isAdmin(socialuser.email.get)) Seq(AcelrRole.ADMIN,AcelrRole.EMPLOYEE) else Seq(AcelrRole.GUEST)
    AcelrUser(fullName = socialuser.fullName,email = socialuser.email.get,identity = socialuser.userIdFromProvider, method = socialuser.authMethod, roles = roles, passwordInfo = socialuser.passwordInfo)
  }


  def toIdentity(user:AcelrUser):Identity = SocialUser(userIdFromProvider = user.identity,firstName = user.fullName,
    lastName = user.fullName,
    fullName = user.fullName,
    email = Some(user.email),
    authMethod = user.method, avatarUrl = None, passwordInfo = user.passwordInfo)

  def find(identity:UserIdFromProvider):Option[Identity] = {
    dao.findOne(MongoDBObject("identity.authId" -> identity.authId, "identity.providerId" -> identity.providerId)) match {
      case Some(user) =>
        Some(toIdentity(user))
      case _ =>
        None
    }
  }

  def findByEmailAndProvider(email:String,provider:String):Option[Identity] = {
    println(s"Email - $email and provider - $provider")
    dao.findOne(MongoDBObject("email" -> email, "identity.providerId" -> provider)) match {
      case Some(user) =>
        Some(toIdentity(user))
      case _ =>
        None
    }
  }

  def findUserByEmailAndProvider(email:String,provider:String):Option[AcelrUser] = {
    dao.findOne(MongoDBObject("email" -> email, "identity.providerId" -> provider))
  }
}


import org.scala_tools.time.Imports._
object TokenDAO {
  val collection = MongoConnection()("acelr")("tokens")
  val dao = new SalatDAO[Token, String](collection = collection) {}

  def findToken(uuid:String):Option[Token] = dao.findOne(MongoDBObject("uuid" -> uuid))

  def deleteExpiredTokens() {
    val now = DateTime.now
    dao.find(MongoDBObject("expirationTime" -> MongoDBObject("$lte" -> now))).foreach(
      TokenDAO.dao.remove(_)
    )
  }
}
