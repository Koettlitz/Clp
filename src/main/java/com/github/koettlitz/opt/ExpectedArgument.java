package com.github.koettlitz.opt;

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
   int getIndex();
   String getName();
   String fullName();
   String getDescription();
   boolean isMandatory();
   boolean isPresent();
   boolean isOption();
   ExpectedArgument clone();

   @Override
   default int compareTo(ExpectedArgument arg) {
      return getIndex() - arg.getIndex();
   }
}