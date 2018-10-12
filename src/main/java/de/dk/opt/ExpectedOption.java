package de.dk.opt;

import de.dk.util.StringUtils;

/**
 * @author David Koettlitz
 * <br>Erstellt am 07.08.2017
 */
public class ExpectedOption implements ExpectedArgument, Cloneable {
   public static final char NO_KEY = '\0';

   private final int index;
   private char key;
   private String longKey;
   private boolean expectsValue;
   private String description;

   private String value;
   private boolean present;

   public ExpectedOption(int index,
                         char key,
                         String longKey,
                         String description) {
      this.index = index;
      this.key = key;
      setLongKey(longKey);
      setDescription(description);
   }

   public ExpectedOption(int index, char key, String description) {
      this(index, key, null, description);
   }

   public ExpectedOption(int index, char key) {
      this(index, key, null);
   }

   public ExpectedOption(int index, String longKey) {
      this(index, NO_KEY, longKey, null);
   }

   public void setValue(String value) throws UnsupportedOperationException {
         if (!expectsValue)
            throw new UnsupportedOperationException("No value for this option expected.");

      this.value = value;
   }

   public String getValue() {
      return value;
   }

   public void setDescription(String description) {
      this.description = StringUtils.isBlank(description) ? null : description;
   }

   @Override
   public boolean isMandatory() {
      return false;
   }

   @Override
   public boolean isPresent() {
      return present;
   }

   public boolean expectsValue() {
      return expectsValue;
   }

   public void setExpectsValue(boolean expectsValue) {
      this.expectsValue = expectsValue;
   }

   public void setPresent(boolean present) {
      this.present = present;
   }

   public char getKey() {
      return key;
   }

   public void setKey(char key) {
      this.key = key;
   }

   public String getLongKey() {
      return longKey;
   }

   public void setLongKey(String longKey) {
      this.longKey = StringUtils.isBlank(longKey) ? null : longKey;
   }

   @Override
   public int getIndex() {
      return index;
   }

   @Override
   public String getName() {
      if (longKey != null)
         return longKey;

      return "" + key;
   }

   @Override
   public String getDescription() {
      return description;
   }

   @Override
   public boolean isOption() {
      return true;
   }

   @Override
   public String fullName() {
      String fullName;
      if (key != NO_KEY)
         fullName = "-" + key + (expectsValue ? " <" + getName() + ">" : "");
      else if (longKey != null)
         fullName = "--" + longKey + (expectsValue ? "=<" + getName() + ">" : "");
      else
         fullName = "-" + (expectsValue ? " <" + getName() + ">" : "");

      return fullName;
   }

   @Override
   public ExpectedOption clone() {
      try {
         return (ExpectedOption) super.clone();
      } catch (CloneNotSupportedException e) {
         String msg = "Error cloning this ExpectedOption. "
                      + "This error should never occur.";
         throw new Error(msg, e);
      }
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + this.key;
      result = prime * result + ((this.longKey == null) ? 0 : this.longKey.hashCode());
      result = prime * result + ((this.value == null) ? 0 : this.value.hashCode());
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
      ExpectedOption other = (ExpectedOption) obj;
      if (this.key != other.key)
         return false;
      if (this.longKey == null) {
         if (other.longKey != null)
            return false;
      } else if (!this.longKey.equals(other.longKey))
         return false;
      if (this.value == null) {
         if (other.value != null)
            return false;
      } else if (!this.value.equals(other.value))
         return false;
      return true;
   }

   @Override
   public String toString() {
      return "ExpectedOption { index=" + index
                               + ", key=" + key
                               + ", longKey=" + longKey
                               + (value != null ? (", value=" + value) : "")
              + " }";
   }
}
