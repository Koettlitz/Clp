package de.dk.opt;

/**
 * Represents an expected argument, that can be parsed by a {@link ArgumentParser}.
 *
 * @author David Koettlitz
 * <br>Erstellt am 24.07.2017
 *
 * @see ArgumentParser
 * @see ArgumentParserBuilder
 */
public interface ExpectedArgument extends Comparable<ExpectedArgument> {
   public int getIndex();
   public String getName();
   public String fullName();
   public String getDescription();
   public boolean isMandatory();
   public boolean isPresent();
   public boolean isOption();
   public ExpectedArgument clone();

   @Override
   public default int compareTo(ExpectedArgument arg) {
      return getIndex() - arg.getIndex();
   }
}