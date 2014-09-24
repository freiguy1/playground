package service

import play.api.Application
import play.api.Play.current
import play.api.cache.Cache
import securesocial.core.{ AuthenticatorStore, Authenticator }

class InDbAuthenticatorStore(app: Application) extends AuthenticatorStore(app) {
  def save(authenticator: Authenticator): Either[Error, Unit] = {
    models.Authenticator.save(authenticator)
    Right(())
  }

  def find(id: String): Either[Error, Option[Authenticator]] = {
    Right(models.Authenticator.find(id))
  }

  def delete(id: String): Either[Error, Unit] = {
    models.Authenticator.delete(id)
    Right(())
  }

}
