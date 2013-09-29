package models

import play.libs.Scala
import be.objectify.deadbolt.core.models.Subject
import scalaz._
import Scalaz._

/**
 *
 * @author Steve Chaloner (steve@objectify.be)
 */

class User(val userName: String) extends Subject
{
  def getRoles: java.util.List[SecurityRole] = {
    if ((userName === "pawan.kumar@gmail.com") || (userName === "pawan@acelrtech.com") || (userName === "thomson@acelrtech.com")){
      Scala.asJava(List(new SecurityRole(AcelrRole.ADMIN),
        new SecurityRole(AcelrRole.EMPLOYEE)))
    }
    else {
      Scala.asJava(List(new SecurityRole(AcelrRole.GUEST)))
    }
  }

  def getPermissions: java.util.List[UserPermission] = {
    Scala.asJava(List(new UserPermission("printers.edit")))
  }

  def getIdentifier: String = userName
}
