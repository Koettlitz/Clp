package de.dk.opt;

import java.util.Objects;

/**
 * @author David Koettlitz
 * <br>Erstellt am 07.08.2017
 */
public class Command implements ExpectedArgument, Cloneable {
   private String name;
   private final int index;
   private String description;
   private ArgumentModel value;
   private ArgumentParser parser;
   private CommandGroup group;

   public Command(CommandGroup group,
                  int index,
                  String name,
                  String description,
                  ArgumentParser parser) throws NullPointerException {

      this.group = group;
      this.index = index;
      this.name = Objects.requireNonNull(name);
      this.description = description;
      this.parser = Objects.requireNonNull(parser);
   }

   public Command(CommandGroup group, int index, String name, ArgumentParser parser) throws NullPointerException {
      this(group, index, name, null, parser);
   }

   Command(int index, String name) throws NullPointerException {
      this.index = index;
      this.name = Objects.requireNonNull(name);
   }

   @Override
   public String fullName() {
      return name + " " + parser.syntax();
   }

   public ArgumentParser getParser() {
      return parser;
   }

   void setParser(ArgumentParser parser) {
      this.parser = parser;
   }

   @Override
   public boolean isPresent() {
      return value != null;
   }

   @Override
   public String getName() {
      return name;
   }

   public void setName(String name) {
      this.name = name;
   }

   @Override
   public int getIndex() {
      return index;
   }

   @Override
   public String getDescription() {
      return description;
   }

   public void setDescription(String description) {
      this.description = description;
   }

   @Override
   public boolean isMandatory() {
      return group.isMandatory();
   }

   public ArgumentModel getValue() {
      return value;
   }

   void setValue(ArgumentModel value) {
      this.value = value;
      group.setPresent(isPresent());
   }

   void setGroup(CommandGroup group) {
      this.group = group;
   }

   @Override
   public boolean isOption() {
      return false;
   }

   @Override
   public Command clone() {
      Command clone = new Command(index, name);
      clone.description = description;
      clone.parser = parser;
      return clone;
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((this.name == null) ? 0 : this.name.hashCode());
      return result;
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj)
         return true;
      if (obj == null)
         return false;
      if (getClass() != obj.getClass())
         return false;
      Command other = (Command) obj;
      if (this.name == null) {
         if (other.name != null)
            return false;
      } else if (!this.name.equals(other.name))
         return false;
      return true;
   }

   @Override
   public String toString() {
      String string = "Command { ";
      if (parser != null)
         string += name + " " + parser.syntax();
      else
         string += "name=" + name;

      return string + " }";
   }
}
