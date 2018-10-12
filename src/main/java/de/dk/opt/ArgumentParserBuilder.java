package de.dk.opt;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A builder class to build an {@link ArgumentParser}.
 * To build arguments and options for the argumentparser this builder creates subbuilders for arguments and options.
 * Those subbuilders return this parentbuilder in their <code>build()</code> method
 * to get back to building new Arguments and options.
 * An example of the usage:<br><br>
 * <code>
 * ArgumentParserBuilder builder = new ArgumentParserBuilder();<br>
 * ArgumentParser parser = ArgumentParserBuilder.begin()<br>
   &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
   &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
   &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
   &nbsp;&nbsp;&nbsp;.buildArgument("foo")<br>
   &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
   &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
   &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
   &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;.setMandatory(false)<br>
   &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
   &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
   &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
   &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;.setDescription("description of foo")<br>
   &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
   &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
   &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
   &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;.build()<br>
   &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
   &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
   &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
   &nbsp;&nbsp;&nbsp;.buildOption('b', "bar")<br>
   &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
   &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
   &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
   &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;.setDescription("description of option bar")<br>
   &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
   &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
   &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
   &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;.setExpectsValue(true)<br>
   &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
   &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
   &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
   &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;.build()<br>
   &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
   &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
   &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
   &nbsp;&nbsp;&nbsp;.buildAndGet();<br>
 * </code>
 *
 * @author David Koettlitz
 * <br>Erstellt am 24.07.2017
 */
public class ArgumentParserBuilder {
   private List<ExpectedPlainArgument> arguments;
   private Map<Character, ExpectedOption> options;
   private Map<String, ExpectedOption> longOptions;
   private Map<String, Command> commands;

   private CommandBuilder parentBuilder;
   private boolean ignoreUnknown;
   private String[] helpArgs;

   private short argCount;

   public ArgumentParserBuilder(CommandBuilder parentBuilder) {
      this.parentBuilder = parentBuilder;
   }

   public ArgumentParserBuilder() {
      this(null);
   }

   /**
    * This method is equivalent to the empty constructor.
    * Its just to make the code more readable.
    *
    * @return A new ArgumentParserBuilder
    */
   public static ArgumentParserBuilder begin() {
      return new ArgumentParserBuilder();
   }

   /**
    * Builds the argumentparser, that will be able to parse all the arguments and options specified.
    *
    * @return A new argumentparser
    */
   public ArgumentParser buildAndGet() {
      ArgumentParser parser = new ArgumentParser(arguments, options, longOptions, commands);
      parser.setIgnoreUnknown(ignoreUnknown);
      if (helpArgs != null)
         parser.setHelpArgs(Arrays.asList(helpArgs));

      if (parentBuilder != null)
         parentBuilder.setParser(parser);

      return parser;
   }

   /**
    * Builds the ArgumentParser and adds it to the parent builder.
    * This method can only be called from a child builder.
    * If this builder doesn't have a parent builder use the {@link #buildAndGet()}
    * method instead.
    *
    * @return The parent builder of this builder
    *
    * @throws UnsupportedOperationException if this builder is not a child builder
    * (doesn't have a parent builder)
    *
    * @see #buildAndGet()
    */
   public CommandBuilder build() throws UnsupportedOperationException {
      if (parentBuilder == null)
         throw new UnsupportedOperationException("This ArgumentParserBuilder didn't have a parent builder.");

      buildAndGet();
      return parentBuilder;
   }

   /**
    * Creates a new argument builder to build an argument.
    * The build method of that argument builder will return this argumentparser builder again.
    *
    * @param name The name of the argument
    *
    * @return An argument builder that will be a child builder of this builder
    *
    * @throws NullPointerException If the given <code>name</code> is <code>null</code>
    */
   public PlainArgumentBuilder buildArgument(String name) throws NullPointerException {
      return new PlainArgumentBuilder(this, argCount++, name);
   }

   /**
    * Adds an argument to this builder, that the resulting argument parser will be able to parse.
    * The argument will be a simple mandatory argument.
    * To create define specific arguments use the {@link ArgumentParserBuilder#buildArgument(String)} method.
    *
    * @param name The name of the argument
    *
    * @return This argumentparser builder to go on
    *
    * @throws NullPointerException If the given <code>name</code> is <code>null</code>
    */
   public ArgumentParserBuilder addArgument(String name) throws NullPointerException {
      return addArgument(new ExpectedPlainArgument(argCount++, name));
   }

   /**
    * Adds an argument to this builder, that the resulting argument parser will be able to parse.
    * The argument will be a simple mandatory argument.
    * To create define specific arguments use the {@link ArgumentParserBuilder#buildArgument(String)} method.
    *
    * @param name The name of the argument
    * @param description The description of the argument
    *
    * @return This argumentparser builder to go on
    *
    * @throws NullPointerException If the given <code>name</code> is <code>null</code>
    */
   public ArgumentParserBuilder addArgument(String name, String description) throws NullPointerException {
      return addArgument(new ExpectedPlainArgument(argCount++, name, description));
   }

   /**
    * Adds an argument to this builder, that the resulting argument parser will be able to parse.
    *
    * @param name The name of the argument
    * @param mandatory If the argument is mandatory or not
    * @param description The description of the argument
    *
    * @return This argumentparser builder to go on
    *
    * @throws NullPointerException If the given <code>name</code> is <code>null</code>
    */
   public ArgumentParserBuilder addArgument(String name, boolean mandatory, String description) throws NullPointerException {
      return addArgument(new ExpectedPlainArgument(argCount++, name, mandatory, description));
   }

   /**
    * Adds an argument to this builder, that the resulting argument parser will be able to parse.
    *
    * @param argument The argument to be added
    *
    * @return This argumentparser builder to go on
    */
   protected ArgumentParserBuilder addArgument(ExpectedPlainArgument argument) {
      if (arguments == null)
         arguments = new ArrayList<>();

      arguments.add(argument);
      return this;
   }

   /**
    * Creates a new option builder to build an option.
    * The build method of that option builder will return this argumentparser builder again.
    *
    * @param key The key of the option
    *
    * @return An option builder that will be a child builder of this builder
    */
   public OptionBuilder buildOption(char key) {
      return new OptionBuilder(this, argCount++, key);
   }

   /**
    * Creates a new option builder to build an option.
    * The build method of that option builder will return this argumentparser builder again.
    *
    * @param longKey The long key of the option
    *
    * @return An option builder that will be a child builder of this builder
    */
   public OptionBuilder buildOption(String longKey) {
      return new OptionBuilder(this, argCount++, longKey);
   }

   /**
    * Adds an option to this builder, that the resulting argument parser will be able to parse.
    * The option will be a simple flag without any value.
    * To add a more specific option use the {@link ArgumentParserBuilder#buildOption(char)} method.
    *
    * @param key The key of the option
    *
    * @return This argumentparser builder to go on
    */

   public ArgumentParserBuilder addOption(char key) {
      return addOption(new ExpectedOption(argCount++, key));
   }

   /**
    * Adds an option to this builder, that the resulting argument parser will be able to parse.
    * The option will be a simple flag without any value.
    * To add a more specific option use the {@link ArgumentParserBuilder#buildOption(char)} method.
    *
    * @param longKey The longKey of the option
    *
    * @return This argumentparser builder to go on
    */
   public ArgumentParserBuilder addOption(String longKey) throws NullPointerException {
      return addOption(new ExpectedOption(argCount++, longKey));
   }

   /**
    * Adds an option to this builder, that the resulting argument parser will be able to parse.
    * The option will be a simple flag without any value.
    * To add a more specific option use the {@link ArgumentParserBuilder#buildOption(char)} method.
    *
    * @param key The key of the option
    * @param description The description of the option
    *
    * @return This argumentparser builder to go on
    */
   public ArgumentParserBuilder addOption(char key, String description) {
      return addOption(new ExpectedOption(argCount++, key, description));
   }

   /**
    * Adds an option to this builder, that the resulting argument parser will be able to parse.
    *
    * @param option The option to be added
    *
    * @return This argumentparser builder to go on
    */
   protected ArgumentParserBuilder addOption(ExpectedOption option) {
      if (option.getKey() != ExpectedOption.NO_KEY || option.getLongKey() == null) {
         if (options == null)
            options = new HashMap<>();

         options.put(option.getKey(), option);
      }

      if (option.getLongKey() != null) {
         if (longOptions == null)
            longOptions = new HashMap<>();

         longOptions.put(option.getLongKey(), option);
      }

      return this;
   }

   /**
    * Creates a new command builder to build a command.
    * The build method of that command builder will return this argumentparser builder again.
    *
    * @param name The name of the command
    *
    * @return A command builder that will be a child builder of this builder
    *
    * @throws NullPointerException If the given <code>name</code> is <code>null</code>
    */
   public CommandBuilder buildCommand(String name) throws NullPointerException {
      return new CommandBuilder(this, argCount++, name);
   }

   protected ArgumentParserBuilder addCommand(Command command) {
      if (commands == null)
         commands = new HashMap<>();

      commands.put(command.getName(), command);
      return this;
   }

   /**
    * Set whether unknown/unexpected arguments should be ignored
    * or an Exception should be thrown.
    * If set to <code>true</code>, any of the <code>parseArguments</code>
    * methods will throw an Exception if an unknown/unexpected argument was found.
    *
    * @param ignoreUnknown flag if unknown arguments should be ignored or not
    *
    * @return This argumentparser builder to go on
    */
   public ArgumentParserBuilder setIgnoreUnknown(boolean ignoreUnknown) {
      this.ignoreUnknown = ignoreUnknown;
      return this;
   }

   /**
    * Set the flag whether unknown/unexpected arguments should be ignored
    * to <code>true</code>.
    * If set to <code>true</code>, any of the <code>parseArguments</code>
    * methods will throw an Exception if an unknown/unexpected argument was found.
    *
    * @return This argumentparser builder to go on
    */
   public ArgumentParserBuilder ignoreUnknown() {
      return setIgnoreUnknown(true);
   }

   public ArgumentParserBuilder setHelpArgs(String... helpArgs) {
      this.helpArgs = helpArgs;
      return this;
   }
}