package de.dk.opt;

/**
 * A builder class to build an option for an {@link ArgumentParser}.
 * An option builder is a sub builder of an {@link ArgumentParserBuilder}.
 * The built option is given to the parent argumentparser builder.
 *
 * @author David Koettlitz
 * <br>Erstellt am 24.07.2017
 *
 * @see ArgumentParser
 * @see ArgumentParserBuilder
 */
public class OptionBuilder {
   private final ArgumentParserBuilder parentBuilder;

   private final ExpectedOption option;

   /**
    * Creates a new option builder that belongs to the given <code>parentBuilder</code>.
    * The option that this option builder is building is passed to the <code>parentBuilder</code>.
    *
    * @param parentBuilder The argumentparser builder this option builder belongs to
    * @param index The index of the option it has in the order
    * @param key The key of the option
    */
   protected OptionBuilder(ArgumentParserBuilder parentBuilder, short index, char key) {
      this.parentBuilder = parentBuilder;
      this.option = new ExpectedOption(index, key);
   }

   /**
    * Creates a new option builder that belongs to the given <code>parentBuilder</code>.
    * The option that this option builder is building is passed to the <code>parentBuilder</code>.
    *
    * @param parentBuilder The argumentparser builder this option builder belongs to
    * @param index The index of the option it has in the order
    * @param longKey The long key of the option
    */
   protected OptionBuilder(ArgumentParserBuilder parentBuilder, short index, String longKey) {
      this.parentBuilder = parentBuilder;
      this.option = new ExpectedOption(index, longKey);
   }

   /**
    * Builds the option and adds it to the argumentparser builder
    * by which this option builder was created and returns the argumentparser builder.
    *
    * @return The argumentparser builder by which this option builder was created.
    *
    * @throws IllegalArgumentException if the option doesn't have either a key nor a long key.
    */
   public ArgumentParserBuilder build() {
      return parentBuilder.addOption(option);
   }

   /**
    * Set the key of the option. Use {@link ExpectedOption#NO_KEY} to prohibit a for the option single character key.
    *
    * @param key The key of the option
    *
    * @return This option builder to go on
    */
   public OptionBuilder setKey(char key) {
      option.setKey(key);
      return this;
   }

   /**
    * Sets whether the option expects a value passed to it or not.
    * By default an option doesn't expects a value.<br>
    * Note: Setting the option to expecting a value is making it mandatory too!
    *
    * @param expectsValue Whether the option expects a value behind it or not
    *
    * @return This option builder to go on
    */
   public OptionBuilder setExpectsValue(boolean expectsValue) {
      option.setExpectsValue(expectsValue);
      return this;
   }

   /**
    * Set a long key for the option that can be specified with "--" instead of the single character key with "-".
    *
    * @param longKey The long key without "--" at the beginning
    *
    * @return This option builder to go on
    */
   public OptionBuilder setLongKey(String longKey) {
      option.setLongKey(longKey);
      return this;
   }

   /**
    * Makes the option mandatory. By default options are optional (not mandatory).
    * If an option is mandatory, the argumentparser is throwing a <code>MissingArgumentException</code>
    * if the option is not present.<br>
    * Note: A mandatory option is always expecting a value after it!
    *
    * @param mandatory Determines if the option is mandatory or not.
    *
    * @return This option builder to go on
    */
   public OptionBuilder setMandatory(boolean mandatory) {
      option.setMandatory(mandatory);
      return this;
   }

   /**
    * Set the description of the option.
    *
    * @param description The description of the option
    *
    * @return This option builder to go on
    */
   public OptionBuilder setDescription(String description) {
      option.setDescription(description);
      return this;
   }
}