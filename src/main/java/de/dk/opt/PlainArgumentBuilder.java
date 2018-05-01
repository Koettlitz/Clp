package de.dk.opt;

/**
 * A builder class to build an argument for an {@link ArgumentParser}.
 * An argument builder is a sub builder of an {@link ArgumentParserBuilder}.
 * The built argument is given to the parent argumentparser builder.
 *
 * @author David Koettlitz
 * <br>Erstellt am 24.07.2017
 *
 * @see ArgumentParser
 * @see ArgumentParserBuilder
 */
public class PlainArgumentBuilder implements ArgumentBuilder {
   private final ArgumentParserBuilder parentBuilder;
   private final ExpectedPlainArgument argument;

   /**
    * Creates a new argument builder that belongs to the given <code>parentBuilder</code>.
    * The argument that this argument builder is building is passed to the <code>parentBuilder</code>.
    *
    * @param parentBuilder The argumentparser builder this argument builder belongs to
    * @param index The index of the argument it has in the order
    * @param name The name of the argument
    */
   protected PlainArgumentBuilder(ArgumentParserBuilder parentBuilder, short index, String name) {
      this.parentBuilder = parentBuilder;
      this.argument = new ExpectedPlainArgument(index, name);
   }

   @Override
   public ArgumentParserBuilder build() {
      return parentBuilder.addArgument(argument);
   }

   @Override
   public ExpectedPlainArgument buildAndGet() {
      parentBuilder.addArgument(argument);
      return argument;
   }

   /**
    * Makes the argument mandatory. By default plain arguments are mandatory.
    * If an argument is mandatory, the argumentparser is throwing a <code>MissingArgumentException</code>
    * if the argument is not present.
    *
    * @param mandatory Determines if the argument is mandatory or not.
    *
    * @return This argument builder to go on
    */
   @Override
   public PlainArgumentBuilder setMandatory(boolean mandatory) {
      argument.setMandatory(mandatory);
      return this;
   }

   @Override
   public PlainArgumentBuilder setDescription(String description) {
      argument.setDescription(description);
      return this;
   }

   @Override
   public boolean isChild() {
      return parentBuilder != null;
   }
}