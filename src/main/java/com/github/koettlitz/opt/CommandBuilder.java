package com.github.koettlitz.opt;

import com.github.koettlitz.util.Util;

/**
 * @author David Koettlitz
 * <br>Erstellt am 07.08.2017
 */
public class CommandBuilder implements ArgumentBuilder {
   private final ArgumentParserBuilder parentBuilder;
   private CommandGroup group;
   private Command command;

   /**
    * Creates a new argument builder that belongs to the given <code>parentBuilder</code>.
    * The argument that this argument builder is building is passed to the <code>parentBuilder</code>.
    *
    * @param parentBuilder The argumentparser builder this argument builder belongs to
    * @param index The index of the argument it has in the order
    * @param name The name of the argument
    */
   protected CommandBuilder(ArgumentParserBuilder parentBuilder, CommandGroup group, short index, String name) throws NullPointerException {
      this.parentBuilder = parentBuilder;
      this.group = Util.nonNull(group, CommandGroup::new);
      this.command = new Command(index, name);
   }

   /**
    * Creates a new argument builder that belongs to the given <code>parentBuilder</code>.
    * The argument that this argument builder is building is passed to the <code>parentBuilder</code>.
    *
    * @param index The index of the argument it has in the order
    * @param name The name of the argument
    */
   protected CommandBuilder(short index, String name) {
      this(null, null, index, name);
   }

   /**
    * Builds the command and adds it to the parent argumentparser builder
    * this CommandBuilder was created from and returns the argumentparser builder.
    *
    * @return The argumentparser builder by which this argument builder was created.
    */
   @Override
   public ArgumentParserBuilder build() {
      if (command.getParser() == null)
         buildParser().build();

      group.add(command);

      return parentBuilder.setCommands(group);
   }

   @Override
   public Command buildAndGet() {
      build();
      return command;
   }

   @Override
   public CommandBuilder setDescription(String description) {
      command.setDescription(description);
      return this;
   }

   /**
    * Build an argumentparser with an argumentparser builder that is a child builder of this command builder.
    * The returned builders {@link ArgumentParserBuilder#build()} method will return this builder again.
    *
    * @return An <code>ArgumentParserBuilder</code> as a child builder of this builder
    */
   public ArgumentParserBuilder buildParser() {
      ArgumentParserBuilder childBuilder = new ArgumentParserBuilder(this);
      childBuilder.setIgnoreUnknown(true);
      return childBuilder;
   }

   /**
    * Set the argumentparser for the command.
    *
    * @param parser The argumentparser to parse the arguments of the command
    *
    * @return This command builder to go on
    */
   public CommandBuilder setParser(ArgumentParser parser) {
      command.setParser(parser);
      return this;
   }

   public CommandBuilder setMandatory(boolean mandatory) {
      group.setMandatory(mandatory);
      return this;
   }

   @Override
   public boolean isChild() {
      return parentBuilder != null;
   }
}
