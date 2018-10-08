package de.dk.opt.ex;

import de.dk.opt.ExpectedArgument;

/**
 * Thrown to indicate that a mandatory argument was absent.
 *
 * @author David Koettlitz
 * <br>Erstellt am 24.07.2017
 */
public class MissingArgumentException extends ArgumentParseException {
   private static final long serialVersionUID = 1L;

   private final Iterable<? extends ExpectedArgument> missingArguments;

   public MissingArgumentException(Iterable<? extends ExpectedArgument> missingArguments) {
      super(createMessage(missingArguments));
      this.missingArguments = missingArguments;
   }

   private static String createMessage(Iterable<? extends ExpectedArgument> missingArguments) {
      StringBuilder builder = new StringBuilder("Missing Arguments:");
      for (ExpectedArgument arg : missingArguments) {
         builder.append(" <")
                .append(arg.getName())
                .append('>');
      }

      return builder.toString();
   }

   public Iterable<? extends ExpectedArgument> getMissingArguments() {
      return missingArguments;
   }

}