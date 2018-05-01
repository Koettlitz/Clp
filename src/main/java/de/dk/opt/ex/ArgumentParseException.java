package de.dk.opt.ex;

/**
 * Thrown to indicate that something went wrong while parsing arguments.
 *
 * @author David Koettlitz
 * <br>Erstellt am 24.07.2017
 */
public class ArgumentParseException extends Exception {
   private static final long serialVersionUID = 1L;

   public ArgumentParseException() {

   }

   public ArgumentParseException(String message) {
      super(message);
   }

   public ArgumentParseException(Throwable cause) {
      super(cause);
   }

   public ArgumentParseException(String message, Throwable cause) {
      super(message, cause);
   }

   public ArgumentParseException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
      super(message, cause, enableSuppression, writableStackTrace);
   }
}