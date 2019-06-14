package de.dk.opt;

import java.util.Objects;

/**
 * @author David Koettlitz
 * <br>Erstellt am 07.08.2017
 */
public class ExpectedPlainArgument implements ExpectedArgument, Cloneable {
   private final int index;
   private final String name;
   private boolean mandatory;
   private String description;

   private String value;

   public ExpectedPlainArgument(int index, String name, boolean mandatory, String description) throws NullPointerException {
      this.index = index;
      this.name = Objects.requireNonNull(name);
      this.mandatory = mandatory;
      setDescription(description);
   }

   protected ExpectedPlainArgument(int index, String name) throws NullPointerException {
      this(index, name, true, null);
   }

   public ExpectedPlainArgument(int index, String name, String description) throws NullPointerException {
      this(index, name, true, description);
   }

   public String getValue() {
      return value;
   }

   public void setValue(String value) {
      this.value = value;
   }

   @Override
   public int getIndex() {
      return index;
   }

   @Override
   public String getName() {
      return name;
   }

   @Override
   public boolean isMandatory() {
      return mandatory;
   }

   public void setMandatory(boolean mandatory) {
      this.mandatory = mandatory;
   }

   @Override
   public String getDescription() {
      return description;
   }

   public void setDescription(String description) {
      this.description = description == null || description.trim().isEmpty()
                         ? null
                         : description;
   }

   @Override
   public String fullName() {
      return "<" + getName() + ">";
   }

   @Override
   public boolean isPresent() {
      return value != null;
   }

   @Override
   public boolean isOption() {
      return false;
   }

   @Override
   public ExpectedPlainArgument clone() {
      try {
         return (ExpectedPlainArgument) super.clone();
      } catch (CloneNotSupportedException e) {
         String msg = "Error cloning this ExpectedPlainArgument. "
                      + "This error should never occur.";
         throw new Error(msg, e);
      }
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;
      ExpectedPlainArgument that = (ExpectedPlainArgument) o;
      return index == that.index &&
              mandatory == that.mandatory &&
              Objects.equals(name, that.name) &&
              Objects.equals(description, that.description) &&
              Objects.equals(value, that.value);
   }

   @Override
   public int hashCode() {
      return Objects.hash(index, name, mandatory, description, value);
   }

   @Override
   public String toString() {
      return "ExpectedPlainArgument { name=" + name
                                      + ", index=" + index
                                      + (value != null ? (", value=" + value) : "")
             + " }";
   }
}
