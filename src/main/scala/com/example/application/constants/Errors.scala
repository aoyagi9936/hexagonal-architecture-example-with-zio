package com.example.application.constants

// Primary Layer
sealed trait PrimaryError(errorCode: String, errorMessage: String) extends Throwable {
  def code    = errorCode
  def message = errorMessage
}
final case class ErrorJson(errorCode: String, errorMessage: String)
//400 Bad Request Error
case object RoleBadRequestError extends PrimaryError(
  "INVARID_ROLE_ERROR",
  "Invalid role requested. Please check available roles.")
//401 Unauthorized
case object UnAuthorizedError extends PrimaryError(
  "UNAUTHORIZED_ERROR",
  "You are not authenticated. You need to create an account or accept an invitation.")
//403 Forbidden
case object ForbiddenError extends PrimaryError(
  "FORBIDDEN_ERROR",
  "Permission denied for this resource. Add the roles you need to access the resource.")
//404 NotFound
case object NotFoundError extends PrimaryError(
  "NOTFOUND_ERROR",
  "Data is not found.")
//500 Internal Server Error
case object InternalServerError extends PrimaryError(
  "INTERNAL_SERVER_ERROR",
  "An Internal Server Error has occurred. Please contact support.")


// Application Layer
sealed trait DomainError(message: String)
final case class DataNotFoundError(cause: Throwable = new Throwable) extends DomainError(message = cause.getMessage)
final case class DataConflictError(cause: Throwable = new Throwable) extends DomainError(message = cause.getMessage)
final case class CharactersServiceError(cause: Throwable = new Throwable) extends DomainError(message = cause.getMessage)

// Secondary Layer
sealed trait SecondaryError(message: String)
final case class RepositoryError(cause: Throwable = new Throwable) extends SecondaryError(message = cause.getMessage)
final case class DuplicateDataError(cause: Throwable = new Throwable) extends SecondaryError(message = cause.getMessage)
