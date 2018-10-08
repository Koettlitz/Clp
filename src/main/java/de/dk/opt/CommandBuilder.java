package de.dk.opt;

import java.util.HashSet;
import java.util.Set;

/**
 * @author David Koettlitz
 * <br>Erstellt am 07.08.2017
 */
public class CommandBuilder implements ArgumentBuilder {
   private final ArgumentParserBuilder parentBuilder;
   private Command command;
   private Set<Command> alternatives;

   /**
    * Creates a new argument builder that belongs to the given <code>parentBuilder</code>.
    * The argument that this argument builder is building is passed to the <code>parentBuilder</code>.
    *
    * @param parentBuilder The argumentparser builder this argument builder belongs to
    * @param index The index of the argument it has in the order
    * @param name The name of the argument
    */
   protected CommandBuilder(ArgumentParserBuilder parentBuilder, short index, String name) throws NullPointerException {
      this.parentBuilder = parentBuilder;
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
      this(null, index, name);
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

      if (alternatives != null) {
         alternatives.add(command);
         command.setAlternatives(alternatives);
      }

      return parentBuilder.addCommand(command);
   }

   @Override
   public Command buildAndGet() {
      build();
      return command;
   }

   /**
    * Builds the command and adds it to the parent argumentparser builder
    * this CommandBuilder was created from.<br>
    * Afterwards a command builder is returned to build another command,
    * that is an alternative command to the currently built command.
    * The parent builder of the returned CommandBuilder will be the same as
    * the parent builder of this CommandBuilder, so the {@link #build()}
    * method of it will return the ArgumentParserBuilder this
    * CommandBuilder was created from.
    *
    * @param name the name of the command to build next
    *
    * @return A commandbuilder to build another command,
    * that is an alternative command to the currently built one
    */
   public CommandBuilder buildAndNextAlternative(String name) {
      if (alternatives == null)
         alternatives = new HashSet<>();

      build();

      this.command = new Command(command.getIndex() + 1, name);
      return this;
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
      command.setMandatory(mandatory);
      return this;
   }

   @Override
   public boolean isChild() {
      return parentBuilder != null;
   }
}
