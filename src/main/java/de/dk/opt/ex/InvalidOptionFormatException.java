package de.dk.opt.ex;

public class InvalidOptionFormatException extends ArgumentParseException {
   private static final long serialVersionUID = -5555744636097197302L;

   public InvalidOptionFormatException(String token, char optionKey) {
      super("Invalid token " + token + ". The option " + optionKey +
            " expects a value and therefore needs to be the last letter of the token.");
   }
}
