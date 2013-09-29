package controllers

import play.api._
import play.api.mvc._
import views.html._
import play.api.Play.current
import play.api.libs.concurrent.Execution.Implicits._
import play.api.libs.json.Json

import java.io._
import scala.concurrent.Future
import in.efoundry.utils.EfoundryConfig

object UploadController extends Controller {
  val uploadForRedactor = true
  def uploadFile = Action.async(parse.multipartFormData) { request =>
      Future {
        Logger.debug("Getting files to be saved..")
        var filesSavedList:List[String] = List.empty
        var isOnlyImages = false
        val fileName = if (uploadForRedactor) "file" else "files[]"
        request.body.file(fileName).map { inputfile =>
          val xfile = inputfile.filename
          val contentType = inputfile.contentType.get
          Logger.debug(s"xfile - $xfile has content type - $contentType")
          if (contentType.toLowerCase.indexOf("images/") > 0) {
            isOnlyImages = true
          }
          inputfile.ref.moveTo(new File(EfoundryConfig.APP_CONTENT_STORAGE_FOLDER + inputfile.filename),true)
          filesSavedList = filesSavedList ++ List(inputfile.filename)
        }.getOrElse {
          Redirect(routes.Application.index)
        }
        val result = uploadForRedactor match {
          case true =>
            isOnlyImages match {
              case true =>
                Json.obj(
                  //"filelink"-> filesSavedList.map(a => {request.host + "/acelrcontent/" + a})
                  "filelink"-> {EfoundryConfig.APP_CONTENT_EXTERNAL_URL + filesSavedList.headOption.getOrElse("")}
                )
              case _ =>
                Json.obj(
                  "filelink"-> {EfoundryConfig.APP_CONTENT_EXTERNAL_URL + filesSavedList.headOption.getOrElse("")},
                  "filename"-> {if (filesSavedList.isEmpty) "" else filesSavedList(0)}
                )
            }
          case _ =>
            Json.obj(
              "files" -> filesSavedList.map(x => {
                val size = new File(x).length() / 1024
                Json.obj(
                  "url"-> {EfoundryConfig.APP_CONTENT_EXTERNAL_URL + x},
                  "thumbnailUrl"-> {EfoundryConfig.APP_CONTENT_EXTERNAL_URL + x},
                  "name"->x,
                  "type"->"image/jpeg",
                  "size"->size,
                  "deleteUrl"-> {EfoundryConfig.APP_CONTENT_EXTERNAL_URL + x + "/delete"},
                  "deleteType"->"DELETE"
                )
              })
            )
        }
        Ok(result)
      }
    }
}
