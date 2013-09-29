package in.efoundry.utils

import play.api.Logger
import play.api.Configuration
import play.api.Mode._
import scalaz._
import Scalaz._
import controllers.routes

object EfoundryConfig {
  //lazy val config = Configuration.load(new java.io.File("conf/application.conf"))
  //lazy val config = Configuration.load()
  lazy val config = play.Play.application().configuration()
  val APP_URL =config.getString("app.url")
  val STATIC_CONTENT_PREFIX_APP_URL =config.getString("static.content.prefix.url")
  val CDN1_STATIC_CONTENT_PREFIX_APP_URL =config.getString("static.content.cdn1.prefix")
  val CDN2_STATIC_CONTENT_PREFIX_APP_URL =config.getString("static.content.cdn2.prefix")
  val CDN3_STATIC_CONTENT_PREFIX_APP_URL =config.getString("static.content.cdn3.prefix")
  val CONTENT_VERSION =config.getString("app.content.version")
  val APP_MODE =config.getString("app.mode")
  val USE_CDN =config.getBoolean("app.use.cdn")
  val USE_ANALYTICS =config.getString("application.analytics.google").toBoolean
  val USE_PIWIK_ANALYTICS =config.getString("application.analytics.piwik").toBoolean
  val IS_APP_MODE_PRODUCTION = APP_MODE === "PRODUCTION"
  val DEFAULT_DS = config.getString("default.datastore")
  val APP_CACHE_FOLDER =config.getString("app.cache.folder")
  val DEMO_RESTICTION = play.Play.application().configuration().getBoolean("demo.restriction")
  val APP_CONTENT_EXTERNAL_URL =config.getString("app.content.external.url")
  val APP_CONTENT_STORAGE_FOLDER =config.getString("app.content.storage.folder")

  def get_asset_url(url:String, forceLocal:Boolean = false, cdn:Int = 1) = {
    if((EfoundryConfig.IS_APP_MODE_PRODUCTION) && (!forceLocal)){
      if (EfoundryConfig.USE_CDN) {
        cdn match {
          case 1 =>
            EfoundryConfig.CDN1_STATIC_CONTENT_PREFIX_APP_URL + "/" + url + "?v=" + EfoundryConfig.CONTENT_VERSION
          case 2 =>
            EfoundryConfig.CDN2_STATIC_CONTENT_PREFIX_APP_URL + "/" + url + "?v=" + EfoundryConfig.CONTENT_VERSION
          case _ =>
            EfoundryConfig.CDN3_STATIC_CONTENT_PREFIX_APP_URL + "/" + url + "?v=" + EfoundryConfig.CONTENT_VERSION
        }
      } else
        EfoundryConfig.STATIC_CONTENT_PREFIX_APP_URL + "/" + url + "?v=" + EfoundryConfig.CONTENT_VERSION
    }else {
      routes.Assets.at(url + "?v=" + EfoundryConfig.CONTENT_VERSION)
    }
  }
}
