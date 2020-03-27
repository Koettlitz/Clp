package com.github.koettlitz.opt;

import static com.github.koettlitz.opt.ExpectedOption.NO_KEY;

import java.util.*;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.github.koettlitz.opt.ex.InvalidOptionFormatException;
import com.github.koettlitz.opt.ex.MissingArgumentException;
import com.github.koettlitz.opt.ex.MissingOptionValueException;
import com.github.koettlitz.opt.ex.UnexpectedOptionValueException;
import com.github.koettlitz.opt.ex.UnknownArgumentException;
import com.github.koettlitz.util.PeekableIterator;

/**
 * Builder class to build an argument model.
 *
 * @author David Koettlitz
 * <br>Erstellt am 24.07.2017
 */
public class ArgumentModelBuilder {
   private final List<ExpectedPlainArgument> arguments;
   private final List<String> varArgs;
   private final Map<Character, ExpectedOption> options;
   private final Map<String, ExpectedOption> longOptions;
   private final CommandGroup commands;
   private final Set<ExpectedArgument> mandatories;

   private boolean minusMinusPresent = false;

   private int plainArgIndex;

   /**
    * Create a new argumentmodel builder with plain arguments, options and long options.
    *
    * @param arguments The expected arguments
    * @param options The expected options mapped by their key
    * @param longOptions The expected options mapped by their long key
    * @param commands The expeced commands mapped by their name
    * @param plainArgIndex The index of the next plain argument that will be parsed
    */
   public ArgumentModelBuilder(List<ExpectedPlainArgument> arguments,
                               Map<Character, ExpectedOption> options,
                               Map<String, ExpectedOption> longOptions,
                               CommandGroup commands,
                               int plainArgIndex) {
      this.arguments = arguments;
      this.varArgs = arguments == null ? new LinkedList<>() : null;
      this.options = Objects.requireNonNull(options);
      this.longOptions = Objects.requireNonNull(longOptions);
      this.commands = commands;
      this.plainArgIndex = plainArgIndex;
      this.mandatories = Stream.of(Optional.ofNullable(arguments).orElse(Collections.emptyList()),
                                   commands == null ? Collections.<Command>emptySet() : commands.asCollection())
                               .flatMap(Collection::stream)
                               .filter(ExpectedArgument::isMandatory)
                               .collect(Collectors.toSet());
   }

   /**
    * Create a new argumentmodel builder with plain arguments, options and long options.
    *
    * @param arguments The expected arguments
    * @param options The expected options mapped by their key
    * @param longOptions The expected options mapped by their long key
    * @param plainArgIndex The index of the next plain argument that will be parsed
    */
   public ArgumentModelBuilder(List<ExpectedPlainArgument> arguments,
                               Map<Character, ExpectedOption> options,
                               Map<String, ExpectedOption> longOptions,
                               int plainArgIndex) {
      this(arguments, options, longOptions, null, plainArgIndex);
   }

   /**
    * Create a new argumentmodel builder with plain arguments, options and long options.
    *
    * @param arguments The expected arguments
    * @param options The expected options mapped by their key
    * @param longOptions The expected options mapped by their long key
    * @param commands The expeced commands mapped by their name
    */
   public ArgumentModelBuilder(List<ExpectedPlainArgument> arguments,
                               Map<Character, ExpectedOption> options,
                               Map<String, ExpectedOption> longOptions,
                               CommandGroup commands) {

      this(arguments, options, longOptions, commands, 0);
   }

   /**
    * Create a new argumentmodel builder with plain arguments, options and long options.
    *
    * @param arguments The expected arguments
    * @param options The expected options mapped by their key
    * @param longOptions The expected options mapped by their long key
    */
   public ArgumentModelBuilder(List<ExpectedPlainArgument> arguments,
                               Map<Character, ExpectedOption> options,
                               Map<String, ExpectedOption> longOptions) {

      this(arguments, options, longOptions, null, 0);
   }

   public ArgumentModelBuilder(Map<Character, ExpectedOption> options, Map<String, ExpectedOption> longOptions) {
      this(null, options, longOptions);
   }

   public static ExpectedOption getValueFor(Entry<String, ExpectedOption> longOptionEntry,
                                            Map<Character, ExpectedOption> options) {

      if (longOptionEntry.getValue().getKey() == NO_KEY)
         return longOptionEntry.getValue();

      return options.getOrDefault(longOptionEntry.getValue().getKey(),
                                  longOptionEntry.getValue());
   }

   /**
    * Builds the argumentmodel with all the given arguments and options.
    * Fails if some mandatory arguments (including mandatory options) are missing.
    *
    * @return An argumentmodel with all the parsed results
    *
    * @throws MissingArgumentException If any of the mandatory arguments is missing
    */
   public ArgumentModel build() throws MissingArgumentException {
      if (!allMandatoriesPresent())
         throw new MissingArgumentException(mandatories);

      Map<Character, ExpectedOption> options = this.options
                                                   .entrySet()
                                                   .stream()
                                                   .filter(e -> e.getValue().isPresent())
                                                   .collect(Collectors.toMap(Entry::getKey, Entry::getValue));

      Map<String, ExpectedOption> longOptions = this.longOptions
                                                    .entrySet()
                                                    .stream()
                                                    .filter(e -> e.getValue().isPresent())
                                                    .collect(Collectors.toMap(Entry::getKey,
                                                                              e -> getValueFor(e, options)));

      if (isVarArgs())
         return new ArgumentModel(varArgs, options, longOptions, commands == null ? null : commands.getPresent());


      LinkedHashMap<String, ExpectedPlainArgument> arguments = new LinkedHashMap<>();
      for (ExpectedPlainArgument arg : this.arguments)
         arguments.put(arg.getName(), arg);

      return new ArgumentModel(arguments, options, longOptions, commands == null ? null : commands.getPresent());
   }

   /**
    * Get the next argument and assume it as parsed.
    * If the argument is mandatory it is assumed to be present.
    * Only call this method if a next argument is present.
    *
    * @return The next plain argument
    *
    * @throws NoSuchElementException If no more arguments are expected
    */
   public ExpectedPlainArgument nextArg(String arg) throws NoSuchElementException {
      if (isVarArgs()) {
         varArgs.add(arg);
         return null;
      }

      if (plainArgIndex >= arguments.size())
         throw new NoSuchElementException("No Argument left");

      ExpectedPlainArgument next = arguments.get(plainArgIndex++);
      mandatories.remove(next);
      next.setValue(arg);
      return next;
   }

   /**
    * Determines whether all mandatory arguments (including mandatory options) are present.
    *
    * @return <code>true</code> if all mandatory arguments are present, <code>false</code> otherwise.
    */
   public boolean allMandatoriesPresent() {
      mandatories.removeIf(ExpectedArgument::isPresent);
      return mandatories.isEmpty();
   }

   /**
    * Get all the expected arguments in their current state.
    *
    * @return all expected args
    */
   public Iterable<ExpectedPlainArgument> getArguments() {
      return arguments;
   }

   /**
    * Get all the expected options mapped by their key in their current state.
    *
    * @return all expected options
    */
   public Map<Character, ExpectedOption> getOptions() {
      return options;
   }

   /**
    * Get all the expected options mapped by their long key in their current state.
    *
    * @return all expected options
    */
   public Map<String, ExpectedOption> getLongOptions() {
      return longOptions;
   }

   /**
    * Get all the mandatory arguments (including mandatory options).
    *
    * @return all the mandatory args
    */
   public Iterable<ExpectedArgument> getMandatories() {
      return mandatories;
   }

   /**
    * Get the index of the current parsed plain argument.
    *
    * @return The index of the current pain arg
    */
   public int getPlainArgIndex() {
      return plainArgIndex;
   }

   /**
    * Sets the index of the current parsed plain argument.
    * This method can be used to skip some arguments or to parse some arguments more than once.
    *
    * @param mandatoryArgIndex The index of the currently parsed plain argument
    */
   public void setPlainArgIndex(int mandatoryArgIndex) {
      this.plainArgIndex = mandatoryArgIndex;
   }

   /**
    * Increments the index of the currently parsed plain argument
    */
   public void incrementPlainArgIndex() {
      plainArgIndex++;
   }

   /**
    * Find out of an option of the given <code>key</code> is expected by this argumentmodel builder.
    *
    * @param key The key to be expected or not
    *
    * @return <code>true</code> if an option of the given <code>key</code> is expected, <code>false</code> otherwise.
    */
   public boolean isOptionExpected(char key) {
      return options.containsKey(key);
   }

   /**
    * Find out of an option of the given <code>longKey</code> is expected by this argumentmodel builder.
    *
    * @param longKey The key to be expected or not
    *
    * @return <code>true</code> if an option of the given <code>longKey</code> is expected, <code>false</code> otherwise.
    */
   public boolean isOptionExpected(String longKey) {
      return longOptions.containsKey(longKey);
   }

   /**
    * Get the option of the given <code>key</code>.
    *
    * @param key The key of the option
    *
    * @return An optional that contains the option if it is expected
    */
   public Optional<ExpectedOption> getOption(char key) {
      return Optional.ofNullable(options.get(key));
   }

   /**
    * Get the option of the given <code>longKey</code>.
    *
    * @param longKey The long key of the option
    *
    * @return An optional that contains the option if it is expected
    */
   public Optional<ExpectedOption> getOption(String longKey) {
      return Optional.ofNullable(longOptions.get(longKey));
   }

   /**
    * Find out of a command with the given <code>name</code> is expected by this argumentmodel builder.
    *
    * @param name The name of the command to be expected or not
    *
    * @return <code>true</code> if the command <code>name</code> is expected, <code>false</code> otherwise.
    */
   public boolean expectsCommand(String name) {
      return commands == null || commands.isPresent() ? false : commands.contains(name);
   }

   /**
    * Parses the command with the given <code>name</code> sets the parsed ArgumentModel to the command.
    *
    * @param name The name of the command
    * @param argIterator The iterator over the arguments to parse
    * The next argument returned by the iterators <code>next</code> method has to be the first argument for the command.
    *
    * @return <code>true</code> if the command was successfully parsed.
    * <code>false</code> if no command with the given <code>name</code> was found
    *
    * @throws MissingArgumentException If a mandatory argument (or mandatory option) is missing
    * @throws MissingOptionValueException If an option that has to go with a following value was no value given
    * @throws UnknownArgumentException If an unknown argument was discovered before the arguments were fully parsed
    * @throws UnexpectedOptionValueException If an option value for an option that doesn't expect any value was supplied
    * e.g. If an option <code>[-f --foo]</code> is just intended as a simple flag, that doesn't expect any value
    * is given like <code>--foo=bar</code>
    * @throws InvalidOptionFormatException If a token with multiple options in it contains an option,
    * that expects a value and is not the last char of the token, e.g. <code>-abc</code> is given
    * where option <code>a</code> expects a value
    */
   public boolean parseCommand(String name, PeekableIterator<String> argIterator) throws MissingArgumentException,
                                                                                         MissingOptionValueException,
                                                                                         UnknownArgumentException,
                                                                                         UnexpectedOptionValueException,
                                                                                         InvalidOptionFormatException {
      Command cmd = commands.getCommand(name);
      if (cmd == null)
         return false;

      ArgumentModel result = cmd.getParser()
                                .parseArguments(argIterator);

      cmd.setValue(result);
      return true;
   }

   public boolean isMinusMinusPresent() {
      if (!minusMinusPresent) {
         minusMinusPresent = Optional.ofNullable(options.get('-'))
                                     .map(ExpectedArgument::isPresent)
                                     .orElse(false);
      }
      return minusMinusPresent;
   }

   public boolean isVarArgs() {
      return arguments == null;
   }
}