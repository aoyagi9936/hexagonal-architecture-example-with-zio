package com.example.application.constants

sealed trait DomainError(message: String) extends Throwable

final case class ResolverError(cause: Throwable) extends DomainError(message = cause.getMessage)
final case class BizDomainError(cause: Throwable) extends DomainError(message = cause.getMessage)
final case class RepositoryError(cause: Throwable) extends DomainError(message = cause.getMessage)
final case class UnAuthorizedError(cause: Throwable) extends DomainError(message = cause.getMessage)
final case class ValidationError(message: String)  extends DomainError(message)
case object NotFoundError                          extends DomainError("NotFoundError")
