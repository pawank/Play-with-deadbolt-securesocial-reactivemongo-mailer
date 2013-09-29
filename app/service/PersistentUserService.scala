/**
 * Copyright 2012 Jorge Aliss (jaliss at gmail dot com) - twitter: @jaliss
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package service

import play.api.{Logger, Application}
import securesocial.core._
import securesocial.core.providers.Token

import play.api._
import play.api.mvc._
import models.{TokenDAO, AcelrUser}

import play.api.Play.current


/**
 * MongoDB backend persistent user service
 */
class PersistentUserService(application: Application) extends UserServicePlugin(application) {
  private var users = Map[String, Identity]()
  private var tokens = Map[String, Token]()

  def find(id:UserIdFromProvider): Option[Identity] = {
    if ( Logger.isDebugEnabled ) {
      Logger.debug("users = %s".format(users))
    }
    //users.get(id.userId + id.providerId)
    AcelrUser.find(id)
  }

  def findByEmailAndProvider(email: String, providerId: String): Option[Identity] = {
    if ( Logger.isDebugEnabled ) {
      Logger.debug(s"Email - $email and provider - $providerId")
      Logger.debug("users = %s".format(users))
    }
    //users.values.find( u => u.email.map( e => e == email && u.identityId.providerId == providerId).getOrElse(false))
    AcelrUser.findByEmailAndProvider(email,providerId)
  }

  def save(user: Identity): Identity = {
    //users = users + (user.identityId.userId + user.identityId.providerId -> user)
    // this sample returns the same user object, but you could return an instance of your own class
    // here as long as it implements the Identity trait. This will allow you to use your own class in the protected
    // actions and event callbacks. The same goes for the find(id: UserId) method.
    val acelr = AcelrUser.toAcelrUser(user)
    findByEmailAndProvider(acelr.email,acelr.identity.providerId) match {
      case Some(idn) =>
        val existinguser = AcelrUser.findUserByEmailAndProvider(acelr.email,acelr.identity.providerId).get
        AcelrUser.dao.save(existinguser.copy(identity = user.userIdFromProvider, passwordInfo = user.passwordInfo))
      case _ =>
        AcelrUser.dao.save(acelr)
    }
    if ( Logger.isDebugEnabled ) {
      Logger.debug("Saved AcelrUser = %s".format(acelr))
    }
    user
  }

  def save(token: Token) {
    TokenDAO.dao.save(token)
  }

  def findToken(token: String): Option[Token] = {
    TokenDAO.findToken(token)
  }

  def deleteToken(uuid: String) {
    //tokens -= uuid
    val token = TokenDAO.findToken(uuid)
    if(token.isDefined){
      TokenDAO.dao.remove(token.get)
    }
  }

  def deleteTokens() {
    tokens = Map()
  }

  def deleteExpiredTokens() {
    //tokens = tokens.filter(!_._2.isExpired)
    TokenDAO.deleteExpiredTokens()
  }
}
