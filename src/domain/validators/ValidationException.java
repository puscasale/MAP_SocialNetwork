package domain.validators;

/**
 * Custom exception class for handling validation errors.
 * Extends RuntimeException, allowing unchecked exception handling.
 */
public class ValidationException extends RuntimeException {

  /**
   * Default constructor for ValidationException.
   * Creates an instance without any message or cause.
   */
  public ValidationException() {
  }

  /**
   * Constructs a ValidationException with a specified message.
   * @param message the detail message for this exception
   */
  public ValidationException(String message) {
    super(message);
  }

  /**
   * Constructs a ValidationException with a specified message and cause.
   * @param message the detail message for this exception
   * @param cause the cause of this exception
   */
  public ValidationException(String message, Throwable cause) {
    super(message, cause);
  }

  /**
   * Constructs a ValidationException with a specified cause.
   * @param cause the cause of this exception
   */
  public ValidationException(Throwable cause) {
    super(cause);
  }

  /**
   * Constructs a ValidationException with a specified message, cause,
   * and options for suppression and writability of the stack trace.
   * @param message the detail message for this exception
   * @param cause the cause of this exception
   * @param enableSuppression whether suppression is enabled or disabled
   * @param writableStackTrace whether the stack trace should be writable
   */
  public ValidationException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }
}
