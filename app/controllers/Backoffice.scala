package controllers

import scala.concurrent._
import play.Logger
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import play.api.mvc.Results._
//import play.api.cache._
import format.Formats._
import validation.Constraints._

import play.api.libs.json._
import play.api.libs.json.Json._
import play.api.libs.iteratee._
import play.api.libs.concurrent._
import play.api.Play.current
import play.api.libs.concurrent.Execution.Implicits._
import play.api.i18n.Messages

import models.{AcelrRole, AcelrUser, ContactUs}
import in.efoundry.constants._
import in.efoundry.utils.{MiscUtils, EfoundryEmail, HtmlEmailMessage}
import scala.concurrent.Future

import securesocial.core.{Identity, Authorization}

import scalaz._
import Scalaz._

/*Reactivemongo */
import reactivemongo.api._
import reactivemongo.bson.BSONObjectID
import play.modules.reactivemongo._
import play.modules.reactivemongo.json.collection.JSONCollection


object Backoffice extends Controller with securesocial.core.SecureSocial {
  def index = SecuredAction { implicit request =>
    val user = request.user
      val email = user.email.getOrElse("")
    if (Logger.isDebugEnabled) {
      Logger.debug(s"Called once authorization is successful with name - ${user.fullName} and email - $email")
    }
      val acelrUser = AcelrUser.toAcelrUser(user)
      val isAdmin = acelrUser.roles.filter(a => a === AcelrRole.ADMIN).size >= 0
    val msg:Option[String] = if (!isAdmin) Some(Messages("auth.error")(lang)) else None
      Ok(views.html.dashboard("Welcome",acelrUser, msg)).withSession(session + ("admin" -> isAdmin.toString) + ("username" -> email))
  }
}
