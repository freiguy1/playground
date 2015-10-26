package models;

import play.api.db.DB
import play.api.Play.current
import play.api.libs.json._
import play.api.libs.functional.syntax._

import scala.slick.driver.MySQLDriver.simple._

import Database.dynamicSession

import org.joda.time.DateTime

import java.sql.Timestamp

import securesocial.core.{ IdentityId, Authenticator => SSAuthenticator }

object Authenticator {
  private lazy val database = Database.forDataSource(DB.getDataSource("default"))
  private lazy val authenticatorTable = TableQuery[db.AuthenticatorTable]

  def save(authenticator: SSAuthenticator): Unit = database.withDynSession {
    delete(authenticator.id)
    val dbAuth = db.Authenticator(
      0,
      authenticator.id,
      authenticator.identityId.userId,
      authenticator.identityId.providerId,
      new Timestamp(authenticator.creationDate.getMillis()),
      new Timestamp(authenticator.lastUsed.getMillis()),
      new Timestamp(authenticator.expirationDate.getMillis())
    )
    authenticatorTable += dbAuth
  }

  def find(id: String): Option[SSAuthenticator] = database.withDynSession {
    val dbAuthOpt = authenticatorTable.filter(_.identifier === id).firstOption
    dbAuthOpt.map { dbAuth => 
      SSAuthenticator(
        dbAuth.identifier,
        IdentityId(dbAuth.userId, dbAuth.providerId),
        new DateTime(dbAuth.creationDate.getTime),
        new DateTime(dbAuth.lastUsed.getTime),
        new DateTime(dbAuth.expirationDate.getTime)
      )
    }
  }

  def delete(id: String): Unit = database.withDynSession {
    authenticatorTable.filter(_.identifier === id).delete
  }
}
