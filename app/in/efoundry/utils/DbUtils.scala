package in.efoundry.utils

/**
 * Created with IntelliJ IDEA.
 * User: pawanacelr
 * Date: 18/09/13
 * Time: 11:04 AM
 * To change this template use File | Settings | File Templates.
 */
import org.scala_tools.time.Imports._
import java.security.MessageDigest
import org.apache.commons.codec.binary.Base64
import org.apache.commons.io.FileUtils
import org.joda.time.DateTime
import org.bson.types.ObjectId

object DbUtils {
  def getObjectId(id:Option[String]):ObjectId = id match {
    case Some(x) => new ObjectId(x)
    case _ => new ObjectId
  }
}
