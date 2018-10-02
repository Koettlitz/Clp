package de.dk.opt;

/**
 * @author David Koettlitz
 * <br>Erstellt am 07.08.2017
 */
public interface ArgumentBuilder {

   /**
    * Builds the argument and adds it to the parent argumentparser builder
    * by which this argument builder was created and returns the argumentparser builder.
    * If this builder is not a child builder use the {@link ArgumentBuilder#buildAndGet()} method instead.
    *
    * @return The argumentparser builder by which this argument builder was created.
    *
    * @throws UnsupportedOperationException If this argument builder is not a child builder of an
    * <code>ArgumentParserBuilder</code>
    * @throws IllegalStateException If the argument could not be build
    */
   public ArgumentParserBuilder build() throws UnsupportedOperationException, IllegalStateException;

   /**
    * Builds the argument and passes it to the parent argumentparser builder.
    * It is recommended to use the {@link ArgumentBuilder#build()} method instead.
    *
    * @return The built argument.
    *
    * @throws IllegalStateException If the argument could not be build
    */
   public ExpectedArgument buildAndGet() throws IllegalStateException;

   /**
    * Set a description of the argument. This description can be printed for the user to help him.
    *
    * @param description The description of the argument
    *
    * @return This argument builder to go on
    */
   public ArgumentBuilder setDescription(String description);

   /**
    * Informs if this ArgumentBuilder is a children of another builder or not.
    *
    * @return <code>true</code> if this builder has a parentbuilder the result is passed to, <code>false</code> otherwise.
    */
   public boolean isChild();
}
