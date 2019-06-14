package de.dk.util;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * An {@link Iterator} that which you can peek elements from using the {@link #peek()} method.
 *
 * @author David Koettlitz
 * <br>Erstellt am 07.08.2017
 */
public interface PeekableIterator<E> extends Iterator<E> {
    /**
     * Get the next element from this iterator without moving it further.
     *
     * @return the next element of this iterator
     *
     * @throws NoSuchElementException if this iterator does not have any more elements.
     */
    public E peek() throws NoSuchElementException;
}
