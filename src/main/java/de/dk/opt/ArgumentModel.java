package de.dk.opt;

import java.util.*;
import java.util.stream.Collectors;

import de.dk.util.Util;

import javax.swing.text.html.Option;

/**
 * Represents parsed arguments. This class is implementing the <code>Iterable</code> interface
 * to iterate over the argumentvalues.
 *
 * @author David Koettlitz
 * <br>Erstellt am 24.07.2017
 *
 * @see ArgumentParser
 * @see ArgumentParserBuilder
 */
public class ArgumentModel implements Iterable<String> {
   private final Map<String, ExpectedPlainArgument> arguments;
   private final List<String> varArgs;
   private final Map<Character, ExpectedOption> options;
   private final Map<String, ExpectedOption> longOptions;
   private final Command command;

    /**
     * Creates a new argument model with argument and options.
     *
     * @param arguments The arguments mapped by their names
     * @param options The options mapped by their key characters
     * @param longOptions The long options mapped by their long keys
     * @param command The provided command
     */
    public ArgumentModel(LinkedHashMap<String, ExpectedPlainArgument> arguments,
                         Map<Character, ExpectedOption> options,
                         Map<String, ExpectedOption> longOptions,
                         Command command) {
        this.arguments = Util.nonNull(arguments, Collections::emptySortedMap);
        this.varArgs = null;
        this.options = Util.nonNull(options, Collections::emptySortedMap);
        this.longOptions = Util.nonNull(longOptions, Collections::emptySortedMap);
        this.command = command;
    }

    /**
     * Creates a new argument model with argument and options.
     *
     * @param options The options mapped by their key characters
     * @param longOptions The long options mapped by their long keys
     * @param command The provided command
     */
    public ArgumentModel(List<String> varArgs,
                         Map<Character, ExpectedOption> options,
                         Map<String, ExpectedOption> longOptions,
                         Command command) {
        this.arguments = null;
        this.varArgs = varArgs;
        this.options = Optional.ofNullable(options).orElseGet(Collections::emptySortedMap);
        this.longOptions = Optional.ofNullable(longOptions).orElseGet(Collections::emptySortedMap);
        this.command = command;
    }

    public Collection<String> getPlainArguments() {
        if (varArgs != null)
            return varArgs;

        return arguments.values()
                        .stream()
                        .map(ExpectedPlainArgument::getValue)
                        .collect(Collectors.toList());
    }

   /**
    * Get the value of the argument with the given <code>name</code>.
    *
    * @param name The name of the argument
    *
    * @return The value of the argument or <code>null</code> if the argument
    * <code>name</code> was not specified.
    */
   public String getArgumentValue(String name) {
      return getOptionalArgumentValue(name).orElse(null);
   }

   /**
    * Get the value of an optional plain argument.
    *
    * @param name The name of the argument
    *
    * @return An optional that contains the arguments value if present.
    */
   public Optional<String> getOptionalArgumentValue(String name) {
       return Optional.ofNullable(arguments)
                      .flatMap(a -> Optional.ofNullable(a.get(name)))
                      .map(ExpectedPlainArgument::getValue);
   }

   /**
    * Find out whether a plain argument was given or not.
    *
    * @param name The name of the argument
    *
    * @return <code>true</code> if the agument was given. <code>false</code> otherwise
    */
   public boolean isArgumentPresent(String name) {
       return Optional.ofNullable(arguments)
                      .flatMap(a -> Optional.ofNullable(a.get(name)))
                      .map(ExpectedArgument::isPresent)
                      .orElse(false);
   }

   /**
    * Get the value of the option of the given <code>key</code>.
    *
    * @param key The key of the option
    *
    * @return The value of the option or <code>null</code> if the option was not specified.
    */
   public String getOptionValue(char key) {
      return Optional.ofNullable(options.get(key))
                     .map(ExpectedOption::getValue)
                     .orElse(null);
   }

   /**
    * Get the value of the option of the given <code>longKey</code>.
    *
    * @param longKey The long key of the option
    *
    * @return The value of the option or <code>null</code> if the option was not specified.
    */
   public String getOptionValue(String longKey) {
      return Optional.ofNullable(longOptions.get(longKey))
                     .map(ExpectedOption::getValue)
                     .orElse(null);
   }

   /**
    * Get the value of the option of the given <code>key</code>.
    *
    * @param key The key of the option
    *
    * @return The value of the option
    */
   public Optional<String> getOptionalValue(char key) {
      return Optional.ofNullable(options.get(key))
                     .map(ExpectedOption::getValue);
   }

   /**
    * Get the value of the option of the given <code>longKey</code>.
    *
    * @param longKey The long key of the option
    *
    * @return The value of the option
    */
   public Optional<String> getOptionalValue(String longKey) {
      return Optional.ofNullable(longOptions.get(longKey))
                     .map(ExpectedOption::getValue);
   }

   /**
    * Find out whether an option was set or not.
    *
    * @param key The key of the option
    *
    * @return <code>true</code> if the option was set. <code>false</code> otherwise
    */
    public boolean isOptionPresent(char key) {
       return Optional.ofNullable(options.get(key))
                      .map(ExpectedArgument::isPresent)
                      .orElse(false);
    }

    /**
     * Find out whether an option was set or not.
     *
     * @param longKey The longKey of the option
     *
     * @return <code>true</code> if the option was set. <code>false</code> otherwise
     */
     public boolean isOptionPresent(String longKey) {
        return Optional.ofNullable(longOptions.get(longKey))
                       .map(ExpectedArgument::isPresent)
                       .orElse(false);
     }

    /**
     * Get the parsed argument model of the command with the given <code>name</code>.
     *
     * @param name The name of the command
     *
     * @return The parsed argument model of the command or <code>null</code> if not present
     */
    public ArgumentModel getCommandValue(String name) {
       return getOptionalCommandValue(name).orElse(null);
    }

    /**
     * Get the parsed argument model of the command with the given <code>name</code>.
     *
     * @param name The name of the command
     *
     * @return An optional containing the parsed argument model of the command if present
     */
    public Optional<ArgumentModel> getOptionalCommandValue(String name) {
       return Optional.ofNullable(command)
                      .filter(cmd -> cmd.getName().equals(name))
                      .map(Command::getValue);
    }

    /**
     * Find out whether a command was given or not.
     *
     * @param name The name of the command
     *
     * @return <code>true</code> if the command was given. <code>false</code> otherwise
     */
    public boolean isCommandPresent(String name) {
       return Optional.ofNullable(command)
                      .filter(cmd -> cmd.getName().equals(name))
                      .map(ExpectedArgument::isPresent)
                      .orElse(false);
    }

    @Override
    public Iterator<String> iterator() {
        if (varArgs != null)
            return varArgs.iterator();

        return arguments.values()
                        .stream()
                        .map(ExpectedPlainArgument::getValue)
                        .iterator();
    }
}
