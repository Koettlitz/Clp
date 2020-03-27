package com.github.koettlitz.opt.ex;

import com.github.koettlitz.opt.ExpectedOption;

/**
 * Thrown to indicate that an option that has to go with a following value was no value given.
 *
 * @author David Koettlitz
 * <br>Erstellt am 24.07.2017
 */
public class MissingOptionValueException extends ArgumentParseException {
   private static final long serialVersionUID = 1L;

   private final ExpectedOption option;

   public MissingOptionValueException(ExpectedOption option) {
      super("Missing value for option " + option.fullName());
      this.option = option;
   }

   public ExpectedOption getOption() {
      return option;
   }


}