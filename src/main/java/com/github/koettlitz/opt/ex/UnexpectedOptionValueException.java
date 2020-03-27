package com.github.koettlitz.opt.ex;

import com.github.koettlitz.opt.ExpectedOption;

/**
 * Thrown to indicate, that an option was given a value, e.g. by <code>--longopt=value</code>,
 * but the option didn't expect any value.
 *
 * @author David Koettlitz
 * <br>Erstellt am 07.08.2017
 */
public class UnexpectedOptionValueException extends ArgumentParseException {
   private static final long serialVersionUID = -7461242904311426155L;

   private final String invalidToken;

   public UnexpectedOptionValueException(ExpectedOption opt, String token) {
      super("No value for option " + opt.fullName() + " expected. (" + token + ')');
      this.invalidToken = token;
   }

   public String getInvalidToken() {
      return invalidToken;
   }
}
