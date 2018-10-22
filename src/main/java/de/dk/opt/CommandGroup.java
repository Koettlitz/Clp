package de.dk.opt;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class CommandGroup implements Cloneable {
   private Map<String, Command> commands = new HashMap<>();
   private boolean mandatory;
   private boolean present;

   public void add(Command command) {
      this.commands.put(command.getName(), command);
   }

   public Command getCommand(String name) {
      return commands.get(name);
   }

   public String fullName() {
      StringBuilder builder = new StringBuilder();
      Iterator<Command> iter = commands.values()
                                       .iterator();

      while (iter.hasNext()) {
         Command cmd = iter.next();
         builder.append(cmd.getName());
         if (iter.hasNext())
            builder.append(" | ");
      }

      return builder.toString();
   }

   public boolean isPresent() {
      return present;
   }

   public void setPresent(boolean present) {
      this.present = present;
   }

   public Command getPresent() {
      return commands.values()
                     .stream()
                     .filter(Command::isPresent)
                     .findAny()
                     .orElse(null);
   }

   public boolean isMandatory() {
      return mandatory;
   }

   public void setMandatory(boolean mandatory) {
      this.mandatory = mandatory;
   }

   public boolean contains(String name) {
      return commands.containsKey(name);
   }

   public Collection<Command> asCollection() {
      return commands.values();
   }

   @Override
   protected CommandGroup clone() {
      CommandGroup clone = new CommandGroup();
      clone.mandatory = mandatory;
      clone.present = present;

      for (Command cmd : commands.values()) {
         Command cloneCmd = cmd.clone();
         cloneCmd.setGroup(clone);
         clone.add(cloneCmd);
      }

      return clone;
   }

   public int size() {
      return commands.size();
   }
}
