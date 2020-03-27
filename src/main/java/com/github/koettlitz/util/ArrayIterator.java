package com.github.koettlitz.util;

import java.util.NoSuchElementException;
import java.util.Objects;

/**
 * A {@link PeekableIterator} which iterates over an array.
 * Class contains factory methods to create instances.
 *
 * @param <E> The element type
 *
 * @author David Koettlitz
 * <br>Erstellt am 07.08.2017
 */
public class ArrayIterator<E> implements PeekableIterator<E> {
    private final E[] array;
    private int index;
    private int length;

    protected ArrayIterator(int offset, int length, E[] array) throws IllegalArgumentException {
        this.array = Objects.requireNonNull(array);
        if (offset < 0)
            throw new IllegalArgumentException("offset can not be less than 0. Was " + offset);
        if (length < 0 || offset + length > array.length)
            throw new IllegalArgumentException("Length cannot be less than 0 or greater than the length of the array. Was " + length);

        this.index = offset;
        this.length = offset + length;
    }

    /**
     * Creates a new ArrayIterator which contains the elements of the array.
     *
     * @param array The array that contains the elements to iterate over
     * @param <E> The element type
     *
     * @return A new ArrayIterator which iterates over the given array
     *
     * @throws IllegalArgumentException if the array is either <code>null</code> or empty.
     *
     * @see #ofNullable(Object[])
     */
    public static <E> ArrayIterator<E> of(E[] array) throws IllegalArgumentException {
        return new ArrayIterator<>(0, Objects.requireNonNull(array).length, array);
    }

    /**
     * Creates a new ArrayIterator which iterates over the elements of the given array
     * starting at <code>offset</code> until <code>length</code>.
     *
     * @param array The array that contains the elements to iterate over
     * @param offset the index of the element of the <code>array</code> to start with.
     * @param length The number of elements to iterate over until the iterator is finished.
     * @param <E> The element type
     *
     * @return A new ArrayIterator which iterates over the given array
     * starting at <code>offset</code> and contains the next <code>length</code> elements
     * of the array
     *
     * @throws IllegalArgumentException
     * 1. if the array is either <code>null</code> or empty.
     * 2. if <code>offset &lt; 0 || offset &gt; array.length</code>
     * 3. if <code>offset + length &gt;= array.length</code>
     *
     *
     * @see #ofNullable(int, int, Object[])
     */
    public static <E> ArrayIterator<E> of(int offset,
                                          int length,
                                          E[] array)  throws IllegalArgumentException {

        if (array == null || array.length < 1)
            throw new IllegalArgumentException("array was empty or null.");

        return new ArrayIterator<>(offset, length, array);
    }

    /**
     * Creates a new {@link PeekableIterator} which iterates over the given <code>array</code>.
     * If the <code>array</code> is <code>null</code> or empty an empty iterator is created
     * and returned instead.
     *
     * @param array The array to iterate over
     * @param <E> The element type
     *
     * @return A new PeekableIterator over the elements of <code>array</code>
     *
     * @see #of(Object[])
     */
    public static <E> PeekableIterator<E> ofNullable(E[] array) {
        if (array == null || array.length < 1)
            return new EmptyIterator<>();

        return new ArrayIterator<>(0, array.length, array);
    }

    /**
     * Creates a new {@link PeekableIterator} which iterates over the given <code>array</code>,
     * starting at the elements at index <code>offset</code> and containing <code>length</code>
     * elements.
     * If the <code>array</code> is <code>null</code> or empty an empty iterator is created
     * and returned instead.
     *
     * @param array The array to iterate over
     * @param offset the index of the element of the <code>array</code> to start with.
     * @param length The number of elements to iterate over until the iterator is finished.
     * @param <E> The element type
     *
     * @return A new <code>PeekableIterator</code> which iterates over the given array
     * starting at <code>offset</code> and contains the next <code>length</code> elements
     * of the array
     *
     * @throws IllegalArgumentException
     * 1. if <code>offset &lt; 0 || offset &gt; array.length</code>
     * 2. if <code>offset + length &gt;= array.length</code>
     */
    public static <E> PeekableIterator<E> ofNullable(int offset,
                                                     int length,
                                                     E[] array) throws IllegalArgumentException {
        if (array == null || array.length < 1)
            return new EmptyIterator<>();

        return new ArrayIterator<>(offset, length, array);
    }

    @Override
    public E peek() {
        if (index >= length)
            throw new NoSuchElementException();

        return array[index];
    }

    @Override
    public E next() {
        if (index >= length)
            throw new NoSuchElementException();

        return array[index++];
    }

    @Override
    public boolean hasNext() {
        return index < length;
    }

    public int getIndex() {
        return index;
    }

    public int length() {
        return length;
    }

    private static class EmptyIterator<E> implements PeekableIterator<E> {
        @Override
        public E peek() {
            throw new NoSuchElementException();
        }
        @Override
        public E next() {
            throw new NoSuchElementException();
        }
        @Override
        public boolean hasNext() {
            return false;
        }
    }
}
