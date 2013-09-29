package security

import play.Logger
import play.api.mvc._
import be.objectify.deadbolt.scala.{DynamicResourceHandler, DeadboltHandler}
import play.api.mvc.{Request, Result, Results}
import be.objectify.deadbolt.core.models.Subject
import models.User

import scala.concurrent._
import scala.concurrent.ExecutionContext.Implicits.global

/**
 *
 * @author Steve Chaloner (steve@objectify.be)
 */
class MyDeadboltHandler(dynamicResourceHandler: Option[DynamicResourceHandler] = None) extends DeadboltHandler {
  private def email(request: RequestHeader):Option[String] = request.session.get(Security.username)

  def beforeAuthCheck[A](request: Request[A]) = None

  override def getDynamicResourceHandler[A](request: Request[A]): Option[DynamicResourceHandler] = {
    if (dynamicResourceHandler.isDefined) dynamicResourceHandler
    else Some(new MyDynamicResourceHandler())
  }

  override def getSubject[A](request: Request[A]): Option[Subject] = {
    val emailOpt = email(request)
    if (Logger.isDebugEnabled) {
      Logger.debug(s"Getting subject as - ${emailOpt.toString}")
    }
    emailOpt match {
      case Some(user) =>
        Some(new User(user))
      case _ => None
    }
  }

  def onAuthFailure[A](request: Request[A]): Future[SimpleResult] = {
    Future {
    if (Logger.isDebugEnabled)
      Logger.debug("Authentication failure")
    Results.Forbidden(views.html.accessFailed("Access Denied",Some("Access Denied")))
    }
  }
}
