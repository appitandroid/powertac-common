package org.powertac.common.command

import org.joda.time.DateTime

/**
 * Command object that can be used to report a server error to a client (i.e. broker)
 *
 * @author Carsten Block
 */
class ErrorCmd implements Serializable {
  String className
  String message
  String cause
  String stackTrace // does it make sense to report this to a Broker?
  DateTime dateCreated = new DateTime()

  public static ErrorCmd fromException (Exception ex) {
    if (!ex) return null
    return new ErrorCmd(className: ex.class.getName(), message: ex.message, cause: ex.cause.toString(), stackTrace: ex.stackTrace.toArrayString())
  }
}
