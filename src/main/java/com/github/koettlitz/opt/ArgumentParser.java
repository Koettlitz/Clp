package com.github.koettlitz.opt;

import static com.github.koettlitz.opt.ExpectedOption.NO_KEY;
import static java.util.stream.Collectors.toMap;

import java.io.PrintStream;
import java.util.*;
import java.util.Map.Entry;
import java.util.stream.Stream;

import com.github.koettlitz.opt.ex.InvalidOptionFormatException;
import com.github.koettlitz.opt.ex.MissingArgumentException;
import com.github.koettlitz.opt.ex.MissingOptionValueException;
import com.github.koettlitz.opt.ex.UnexpectedOptionValueException;
import com.github.koettlitz.opt.ex.UnknownArgumentException;
import com.github.koettlitz.util.ArrayIterator;
import com.github.koettlitz.util.PeekableIterator;
import com.github.koettlitz.util.Util;

/**
 * A class to parse command line arguments and options.
 * Supports plain arguments, options (a dash followed by 0 or more characters)
 * and long options (two dashes followed by a string)
 *
 * @author David Koettlitz
 * <br>Erstellt am 24.07.2017
 *
 * @see ArgumentParserBuilder
 */
public class ArgumentParser {
   private static final String[] DEFAULT_HELP_ARGS = new String[] {
      "-h",
      "--help",
      "?"
   };

   private final List<ExpectedPlainArgument> arguments;
   private final Map<Character, ExpectedOption> options;
   private final Map<String, ExpectedOption> longOptions;
   private final CommandGroup commands;

   private final ExpectedArgument[] allArguments;

   private Collection<String> helpArgs = new LinkedList<>(Arrays.asList(DEFAULT_HELP_ARGS));
   private boolean ignoreUnknown;
   private boolean varArgs;

   public ArgumentParser(List<ExpectedPlainArgument> arguments,
                         Map<Character, ExpectedOption> options,
                         Map<String, ExpectedOption> longOptions,
                         CommandGroup commands,
                         boolean varArgs) {
      this.arguments = arguments;
      this.options = Util.nonNull(options, Collections::emptyMap);
      this.longOptions = Util.nonNull(longOptions, Collections::emptyMap);
      this.commands = commands;
      this.varArgs = varArgs;

      this.allArguments = Stream.of(this.arguments == null ? new LinkedList<ExpectedPlainArgument>() : this.arguments,
                                    this.options.values(),
                                    this.longOptions.values(),
                                    commands == null ? Collections.<Command>emptySet() : commands.asCollection())
                                .flatMap(Collection::stream)
                                .sorted(Comparator.comparingInt(ExpectedArgument::getIndex))
                                .distinct()
                                .toArray(ExpectedArgument[]::new);
   }

   public ArgumentParser(List<ExpectedPlainArgument> arguments,
                         Map<Character, ExpectedOption> options,
                         Map<String, ExpectedOption> longOptions,
                         boolean varArgs) {
      this(arguments, options, longOptions, null, varArgs);
   }

   /**
    * Parses the given <code>args</code> and returns the results as an argument model.
    *
    * @param offset The offset to start from.
    * The offset is equivalent to the index of the first argument.
    * @param args The arguments to parse
    *
    * @return An argument model containing the results.
    *
    * @throws MissingArgumentException If a mandatory argument (or mandatory option) is missing
    * @throws MissingOptionValueException If an option that has to go with a following value was no value given
    * @throws UnknownArgumentException If an unknown argument was discovered before the arguments were fully parsed
    * @throws UnexpectedOptionValueException If an option value for an option that doesn't expect any value was supplied
    * e.g. If an option <code>[-f --foo]</code> is just intended as a simple flag, that doesn't expect any value
    * is given like <code>--foo=bar</code>
    * @throws IllegalArgumentException  if <code>args</code> is <code>null</code> or empty.
    * @throws InvalidOptionFormatException If a token with multiple options in it contains an option,
    * that expects a value and is not the last char of the token, e.g. <code>-abc</code> is given
    * where option <code>a</code> expects a value
    */
   public ArgumentModel parseArguments(int offset, String... args) throws MissingArgumentException,
                                                                          MissingOptionValueException,
                                                                          UnknownArgumentException,
                                                                          UnexpectedOptionValueException,
                                                                          InvalidOptionFormatException,
                                                                          IllegalArgumentException {
      return parseArguments(ArrayIterator.of(offset, args.length - offset, args));
   }

   /**
    * Parses the given <code>args</code> and returns the results as an argument model.
    *
    * @param args The arguments to parse
    *
    * @return An argument model containing the results.
    *
    * @throws MissingArgumentException If a mandatory argument (or mandatory option) is missing
    * @throws MissingOptionValueException If an option that has to go with a following value
    * was no value given
    * @throws UnknownArgumentException If an unknown argument was discovered
    * before the arguments were fully parsed
    * @throws UnexpectedOptionValueException If an option value for an option
    * that doesn't expect any value was supplied e.g. If an option <code>[-f --foo]</code>
    * is just intended as a simple flag, that doesn't expect any value
    * is given like <code>--foo=bar</code>
    * @throws IllegalArgumentException  if <code>args</code> is <code>null</code> or empty.
    * @throws InvalidOptionFormatException If a token with multiple options in it contains an option,
    * that expects a value and is not the last char of the token, e.g. <code>-abc</code> is given
    * where option <code>a</code> expects a value
    */
   public ArgumentModel parseArguments(String... args) throws MissingArgumentException,
                                                              MissingOptionValueException,
                                                              UnknownArgumentException,
                                                              UnexpectedOptionValueException,
                                                              InvalidOptionFormatException,
                                                              IllegalArgumentException {
      return parseArguments(ArrayIterator.of(args));
   }

   /**
    * Parses the args given by the <code>iterator</code> and returns the results as an argument model.
    * Unknown additional argument at the end (and only at the end!) of the argument list
    * will be ignored.
    *
    * @param iterator The arguments to parse
    *
    * @return An argument model containing the results.
    *
    * @throws MissingArgumentException If a mandatory argument (or mandatory option) is missing
    * @throws MissingOptionValueException If an option that has to go with a following value
    * was no value given
    * @throws UnknownArgumentException If an unknown argument was discovered before the arguments
    * were fully parsed
    * @throws UnexpectedOptionValueException If an option value for an option
    * that doesn't expect any value was supplied
    * e.g. If an option <code>[-f --foo]</code> is just intended as a simple flag,
    * that doesn't expect any value is given like <code>--foo=bar</code>
    * @throws InvalidOptionFormatException If a token with multiple options in it contains an option,
    * that expects a value and is not the last char of the token, e.g. <code>-abc</code> is given
    * where option <code>a</code> expects a value
    */
   public ArgumentModel parseArguments(PeekableIterator<String> iterator) throws MissingArgumentException,
                                                                                 MissingOptionValueException,
                                                                                 UnknownArgumentException,
                                                                                 UnexpectedOptionValueException,
                                                                                 InvalidOptionFormatException {
      return parseArguments(iterator, createModelBuilder());
   }

   protected ArgumentModel parseArguments(PeekableIterator<String> iterator,
                                          ArgumentModelBuilder builder) throws MissingArgumentException,
                                                                               MissingOptionValueException,
                                                                               UnknownArgumentException,
                                                                               UnexpectedOptionValueException,
                                                                               InvalidOptionFormatException {
      String arg = null;
      try {
         while (iterator.hasNext()) {
            arg = iterator.peek();
            if (!arg.startsWith("-") || builder.isMinusMinusPresent()) {
               if (builder.expectsCommand(arg)) {
                  iterator.next();
                  builder.parseCommand(arg, iterator);
               } else {
                  builder.nextArg(arg);
                  iterator.next();
               }
            } else {
               ExpectedOption option = handleOption(arg, builder);
               iterator.next();
               if (option.expectsValue() && option.getValue() == null) {
                  if (!iterator.hasNext())
                     throw new MissingOptionValueException(option);

                  option.setValue(iterator.next());
               }
            }
         }
      } catch (NoSuchElementException e) {
         if (!ignoreUnknown)
            throw new UnknownArgumentException(arg);
      }

      return builder.build();
   }

   private static ExpectedOption handleOption(String arg,
                                              ArgumentModelBuilder builder) throws MissingOptionValueException,
                                                                                   NoSuchElementException,
                                                                                   UnexpectedOptionValueException,
                                                                                   InvalidOptionFormatException {
      ExpectedOption result = null;

      // long options e.g. '--longOpt'
      if (arg.length() > 2 && arg.charAt(1) == '-') {
         int equalsIndex = arg.indexOf('=');
         if (equalsIndex == -1) {
            result = builder.getOption(arg.substring(2))
                            .orElseThrow(() -> new NoSuchElementException(arg));

            if (result.expectsValue())
               throw new MissingOptionValueException(result);
         } else {
            result = builder.getOption(arg.substring(2, equalsIndex))
                            .orElseThrow(() -> new NoSuchElementException(arg));

            if (arg.length() > equalsIndex + 1) {
               if (result.expectsValue())
                  result.setValue(arg.substring(equalsIndex + 1));
               else
                  throw new UnexpectedOptionValueException(result, arg);
            }
         }
      // Single char options e.g. '-a', '-abc' or '--'
      } else if (arg.length() >= 2) {
         for (int i = 1; i < arg.length(); i++) {
            char key = arg.charAt(i);
            result = builder.getOption(key)
                            .orElseThrow(() -> new NoSuchElementException(arg));

            result.setPresent(true);
            if (result.expectsValue() && i + 1 < arg.length())
               throw new InvalidOptionFormatException(arg, key);
         }
      // Can only be '-'
      } else {
         result = builder.getOption(ExpectedOption.NO_KEY)
                         .orElseThrow(() -> new NoSuchElementException(arg));
      }

      result.setPresent(true);
      return result;
   }

   private ArgumentModelBuilder createModelBuilder() {
      List<ExpectedPlainArgument> arguments;
      if (this.arguments != null) {
         arguments = this.arguments
                         .stream()
                         .map(ExpectedPlainArgument::clone)
                         .collect(ArrayList::new,
                                  Collection::add,
                                  Collection::addAll);
      } else {
         arguments = varArgs ? null : new LinkedList<>();
      }

      Map<Character, ExpectedOption> options = this.options
                                                   .entrySet()
                                                   .stream()
                                                   .collect(toMap(Entry::getKey,
                                                                  e -> e.getValue().clone()));

      short count = (short) ((arguments == null ? 0 : arguments.size()) + options.size() + (commands == null ? 0 : 1));

      Map<String, ExpectedOption> longOptions = new HashMap<>();
      for (Entry<String, ExpectedOption> e : this.longOptions.entrySet()) {
         ExpectedOption opt = e.getValue();
         ExpectedOption shortOpt = opt.getKey() == NO_KEY ? null : options.get(opt.getKey());
         if (shortOpt != null) {
            longOptions.put(e.getKey(), shortOpt);
         } else {
            longOptions.put(e.getKey(), opt);
            count++;
         }
      }

      CommandGroup commands = this.commands == null ? null : this.commands.clone();

      if (!options.containsKey('-')) {
         options.put('-', new ExpectedOption(count,
                                             '-',
                                             "--",
                                             "Indicates, that the following arguments are plain arguments"
                                             + "and no options, even if they have a leading '-'"));
      }

      return new ArgumentModelBuilder(arguments, options, longOptions, commands);
   }

   /**
    * Prints the syntax of the arguments with descriptions if the <code>args</code> match "help" or "h".
    *
    * @param out The printstream to print the help message on
    * @param args The given arguments
    *
    * @return <code>true</code> if the arguments match "help" or "h", <code>false</code> otherwise.
    *
    * @throws NullPointerException If <code>out</code> is <code>null</code>
    */
   public boolean printUsageIfHelpRequested(PrintStream out, String... args) throws NullPointerException {
      if (!isHelp(args))
         return false;

      printUsage(out);
      return true;
   }

   /**
    * Finds out if the given <code>args</code> match "help" or "h".
    *
    * @param args The given arguments
    *
    * @return <code>true</code> if the arguments match "help" or "h", <code>false</code> otherwise.
    */
   public boolean isHelp(String... args) {
      if (args == null || args.length < 1 || helpArgs == null)
         return false;

      return helpArgs.stream()
                     .anyMatch(args[0]::equals);
   }

   /**
    * Prints the syntax of the expected commandline arguments with names and descriptions.
    *
    * @param out The printstream to print on
    *
    * @throws NullPointerException If <code>out</code> is <code>null</code>
    */
   public synchronized void printUsage(PrintStream out) throws NullPointerException {
      Objects.requireNonNull(out);
      out.println("Syntax:");
      out.println(syntax());
      for (ExpectedArgument arg : allArguments) {
         out.println();
         out.println(arg.fullName());
         if (!Util.isBlank(arg.getDescription()))
            out.println(arg.getDescription());
      }
   }

   /**
    * Get the string that describes the syntax of the expected arguments.
    *
    * @return The syntax string
    */
   public synchronized String syntax() {
      StringBuilder builder = new StringBuilder();
      for (ExpectedArgument arg : allArguments) {
         if (arg instanceof Command)
            continue;

         if (arg.isMandatory()) {
            builder.append(arg.fullName())
                   .append(" ");
         } else {
            builder.append('[')
                   .append(arg.fullName())
                   .append("] ");
         }
      }
      if (commands != null) {
         if (commands.isMandatory()) {
            builder.append(commands.fullName());
         } else {
            builder.append('[')
                   .append(commands.fullName())
                   .append(']');
         }
      }

      return builder.toString()
                    .trim();
   }

   /**
    * Get whether unknown/unexpected arguments should be ignored
    * or an Exception should be thrown.
    * If this method returns <code>true</code>, any of the <code>parseArguments</code>
    * methods will throw an Exception if an unknown/unexpected argument was found.
    *
    * @return If unknown arguments should be ignored or not
    */
   public boolean isIgnoreUnknown() {
      return ignoreUnknown;
   }

   /**
    * Set whether unknown/unexpected arguments should be ignored
    * or an Exception should be thrown.
    * If set to <code>true</code>, any of the <code>parseArguments</code>
    * methods will throw an Exception if an unknown/unexpected argument was found.
    *
    * @param ignoreUnknown flag if unknown arguments should be ignored or not
    */
   public void setIgnoreUnknown(boolean ignoreUnknown) {
      this.ignoreUnknown = ignoreUnknown;
   }

   /**
    * Get the arguments that indicate that help is requested.
    * If the first given argument equals one of the helpArgs
    * {@link #isHelp(String...)} returns <code>true</code>
    * By default it is <code>"-h"</code>, <code>"--help"</code> and <code>"?"</code>.
    * This method returns a reference to the actual collection that is used by this ArgumentParser,
    * so changes on the returned collection will have effect.
    *
    * @return The arguments that indicate help
    *
    * @see #printUsage(PrintStream)
    * @see #printUsageIfHelpRequested(PrintStream, String...)
    */
   public Collection<String> getHelpArgs() {
      return helpArgs;
   }

   /**
    * Set the arguments that indicate that help is requested.
    * If the first given argument equals one of the <code>helpArgs</code>
    * {@link #isHelp(String...)} returns <code>true</code>
    * By default it is <code>"-h"</code>, <code>"--help"</code> and <code>"?"</code>.
    *
    * @param helpArgs The arguments that indicate the need of help.
    *
    * @see #printUsage(PrintStream)
    * @see #printUsageIfHelpRequested(PrintStream, String...)
    */
   public void setHelpArgs(Collection<String> helpArgs) {
      this.helpArgs = helpArgs;
   }

}