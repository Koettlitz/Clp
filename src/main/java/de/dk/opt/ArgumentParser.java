package de.dk.opt;

import static de.dk.opt.ExpectedOption.NO_KEY;
import static java.util.stream.Collectors.toMap;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.stream.Stream;

import de.dk.opt.ex.MissingArgumentException;
import de.dk.opt.ex.MissingOptionValueException;
import de.dk.opt.ex.UnexpectedOptionValueException;
import de.dk.opt.ex.UnknownArgumentException;
import de.dk.util.ArrayIterator;
import de.dk.util.PeekableIterator;

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
   private static final String HELP = "--help";
   private static final String SHORT_HELP = "-h";

   private final List<ExpectedPlainArgument> arguments;
   private final Map<Character, ExpectedOption> options;
   private final Map<String, ExpectedOption> longOptions;
   private final Map<String, Command> commands;

   private ExpectedArgument[] allArguments;

   private boolean ignoreUnknown = false;

   public ArgumentParser(List<ExpectedPlainArgument> arguments,
                         Map<Character, ExpectedOption> options,
                         Map<String, ExpectedOption> longOptions,
                         Map<String, Command> commands) {
      this.arguments = Objects.requireNonNull(arguments);
      this.options = Objects.requireNonNull(options);
      this.longOptions = Objects.requireNonNull(longOptions);
      this.commands = commands == null ? new HashMap<>(0) : commands;

      this.allArguments = Stream.of(arguments,
                                    options.values(),
                                    longOptions.values(),
                                    commands.values())
                                .flatMap(Collection::stream)
                                .sorted((a, b) -> a.getIndex() - b.getIndex())
                                .distinct()
                                .toArray(ExpectedArgument[]::new);
   }

   public ArgumentParser(List<ExpectedPlainArgument> arguments,
                         Map<Character, ExpectedOption> options,
                         Map<String, ExpectedOption> longOptions) {
      this(arguments, options, longOptions, null);
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
    */
   public ArgumentModel parseArguments(int offset, String... args) throws MissingArgumentException,
                                                                          MissingOptionValueException,
                                                                          UnknownArgumentException,
                                                                          UnexpectedOptionValueException {
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
    */
   public ArgumentModel parseArguments(String... args) throws MissingArgumentException,
                                                              MissingOptionValueException,
                                                              UnknownArgumentException,
                                                              UnexpectedOptionValueException {
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
    */
   public ArgumentModel parseArguments(PeekableIterator<String> iterator) throws MissingArgumentException,
                                                                                 MissingOptionValueException,
                                                                                 UnknownArgumentException,
                                                                                 UnexpectedOptionValueException {
      return parseArguments(iterator, createModelBuilder());
   }

   protected ArgumentModel parseArguments(PeekableIterator<String> iterator,
                                          ArgumentModelBuilder builder) throws MissingArgumentException,
                                                                               MissingOptionValueException,
                                                                               UnknownArgumentException,
                                                                               UnexpectedOptionValueException {
      String arg = null;
      try {
         while (iterator.hasNext()) {
            arg = iterator.peek();
            if (!arg.startsWith("-") || builder.isMinusMinusPresent()) {
               if (builder.expectsCommand(arg)) {
                  iterator.next();
                  builder.parseCommand(arg, iterator);
               } else {
                  handlePlainArgument(arg, builder);
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

   private static void handlePlainArgument(String arg,
                                           ArgumentModelBuilder builder) throws NoSuchElementException {
      ExpectedPlainArgument expected;
      expected = builder.nextArg();
      expected.setValue(arg);
   }

   private static ExpectedOption handleOption(String arg,
                                              ArgumentModelBuilder builder) throws MissingOptionValueException,
                                                                                   NoSuchElementException,
                                                                                   UnexpectedOptionValueException {
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
               throw new MissingOptionValueException(result);
         }
      // Can only be '-'
      } else {
         result = builder.getOption(ExpectedOption.NO_KEY)
                         .orElseThrow(() -> new NoSuchElementException(arg));
      }

      result.setPresent(true);
      return result;
   }

   private synchronized ArgumentModelBuilder createModelBuilder() {
      List<ExpectedPlainArgument> arguments = this.arguments
                                                  .stream()
                                                  .map(ExpectedPlainArgument::clone)
                                                  .collect(ArrayList::new,
                                                           Collection::add,
                                                           Collection::addAll);

      Map<Character, ExpectedOption> options = this.options
                                                   .entrySet()
                                                   .stream()
                                                   .collect(toMap(Entry::getKey,
                                                                  e -> e.getValue().clone()));

      short count = (short) (arguments.size() + options.size() + commands.size());

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

      Map<String, Command> commands = this.commands
                                          .entrySet()
                                          .stream()
                                          .collect(toMap(Entry::getKey,
                                                         e -> e.getValue().clone()));

      if (!options.containsKey('-')) {
         options.put('-', new ExpectedOption(count,
                                             '-',
                                             "--",
                                             "Indicates, that the following arguments are plain arguments"
                                             + "and no options, even if they have a leading \'-\'"));
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
      if (args == null || args.length < 1)
         return false;

      return args[0].equals(HELP) || args[0].equals(SHORT_HELP);
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
         if (arg.isMandatory()) {
            builder.append(arg.fullName())
                   .append(" ");
         } else {
            builder.append('[')
                   .append(arg.fullName())
                   .append("] ");
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

}