package controllers

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

import models.{ContactUs}
import in.efoundry.constants._
import in.efoundry.utils.{EfoundryConfig, MiscUtils, EfoundryEmail, HtmlEmailMessage}
import scala.concurrent.Future

import security.MyDeadboltHandler
import be.objectify.deadbolt.scala.DeadboltActions



object Application extends Controller with DeadboltActions{

  def index = Action {implicit request =>
    if (Logger.isDebugEnabled)
      Logger.debug("Starting the homepage of ACELR..")
    Ok(views.html.index("Welcome to ACELR Tech Labs"))
  }


  def contactus = Action {implicit request =>
    Ok(views.html.contactus(contactForm,Map(),new MyDeadboltHandler()))
  }

  val contactForm: Form[ContactUs] = Form(
    mapping(
      "fullName" -> nonEmptyText,
      "email" -> nonEmptyText.verifying(Messages("contact.email.error"), MiscUtils.emailRegex.r.findFirstIn(_).getOrElse(Constants.NULLSTRING).size > 0),
      "phone" -> optional(text),
      "message" -> nonEmptyText)(ContactUs.apply)(ContactUs.unapply))

  def saveContactUs() = Action.async {  implicit request =>
    Future {
    contactForm.bindFromRequest.fold(
      errors => {
        var msg = contactForm.constraints.keySet.map(k => {
          errors.error(k.toString).map(e => {
            e.message
          })
        }).map(a => {"- " + a.getOrElse(Constants.NULLSTRING)}).filter(a => a.size >= 3).mkString("###")
        if (Logger.isDebugEnabled) {
          Logger.debug(s"Received error in Contact us data on form submit: $msg")
        }
            BadRequest(views.html.contactus(errors,Map(Constants.ERROR -> msg),new MyDeadboltHandler()))
      },
      fb => {
        if (Logger.isDebugEnabled) {
          Logger.debug("Received Contact us data on form submit")
        }
          val emailBodyForCustomer = views.html.contact.contactEmail(fb,Messages("default.company.name")(lang))
          EfoundryEmail.send(HtmlEmailMessage(to = Seq(fb.email), subject = Messages("contact.email.title.for.customer")(lang), body = emailBodyForCustomer.toString, from = EfoundryConfig.config.getString("mail.from")))
          EfoundryEmail.send(HtmlEmailMessage(to = Seq("pawan.kumar@gmail.com"), subject = Messages("contact.email.title")(lang), body = fb.toString, from = EfoundryConfig.config.getString("mail.from")))
            Ok(views.html.contactus(contactForm, Map(Constants.SUCCESS -> Messages("contact.form.success.msg")(lang)),new MyDeadboltHandler()))
      })
    }
  }

  def marketing = Action {implicit request =>
    Redirect("/")
  }
}
