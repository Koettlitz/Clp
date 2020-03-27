package com.github.koettlitz.util;

import java.util.function.Supplier;

public class Util {

    /**
     * Ensures that an object is created, if <code>null</code>
     *
     * @param object the object to do the nullcheck on
     * @param supplier the supplier of the object, if <code>object</code> is <code>null</code>
     * @param <E> the objects type
     *
     * @return the object if not <code>null</code>, otherwise the created object from the given
     * supplier
     */
    public static <E> E nonNull(E object, Supplier<E> supplier) {
        return object != null ? object : supplier.get();
    }

    /**
     * Checks whether the string is <code>null</code>, empty or
     * contains only whitespaces.
     *
     * @param string The string to be checked if blank
     *
     * @return <code>true</code> if the string is either <code>null</code>,
     * has a length of 0 or contains only whitespace characters
     * <code>false</code> otherwiese
     */
    public static boolean isBlank(String string) {
        return string == null || string.trim().isEmpty();
    }
}
