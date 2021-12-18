package dev.socketmods.nuclearwinter;

import org.checkerframework.checker.nullness.qual.Nullable;
import sun.misc.Unsafe;

import java.lang.reflect.Field;

/**
 * A collection of utilities for using the {@link sun.misc.Unsafe}.
 *
 * @author sciwhiz12
 */
public class UnsafeHelper {
    private static final Unsafe UNSAFE;

    static {
        try {
            final Field theUnsafe = Unsafe.class.getDeclaredField("theUnsafe");
            theUnsafe.setAccessible(true);
            UNSAFE = (Unsafe) theUnsafe.get(null);
        } catch (IllegalAccessException | NoSuchFieldException e) {
            throw new RuntimeException("Failed to retrieve the theUnsafe", e);
        }
    }

    /**
     * Fetches a reference value from the given field.
     *
     * @param field    the field to calculate the base and offset for
     * @param instance an instance of the owning class of the field for instance fields, or {@code null} for static fields
     * @return the value of the field
     */
    public static <T> @Nullable T getObject(final Field field, @Nullable final Object instance) {
        return cast(calculateReturn(field, instance, UNSAFE::getObject));
    }

    /**
     * Stores a reference value into the given field.
     *
     * @param field    the field to calculate the base and offset for
     * @param instance an instance of the owning class of the field for instance fields, or {@code null} for static fields
     * @param newValue the new value to be stored in the field
     */
    public static void putObject(final Field field, @Nullable final Object instance, final Object newValue) {
        calculate(field, instance, (base, offset) -> UNSAFE.putObject(base, offset, newValue));
    }

    // Helper functions

    /**
     * Unsafely casts the object to the generic target type.
     *
     * @param obj the object to cast
     * @param <T> the target type
     * @return the object cast to the target type
     */
    @SuppressWarnings("unchecked")
    private static <T> @Nullable T cast(@Nullable Object obj) {
        return (T) obj;
    }

    /**
     * Calculates the base and offset for the provided field and instance and passes these to the consumer.
     *
     * @param field    the field to calculate the base and offset for
     * @param instance an instance of the owning class of the field for instance fields, or {@code null} for static fields
     * @param consumer the consumer to call
     */
    private static void calculate(final Field field, @Nullable final Object instance, final UnsafeInfoConsumer consumer) {
        calculateReturn(field, instance, (UnsafeInfoFunction<?>) consumer);
    }

    /**
     * Calculates the base and offset for the provided field and instance and passes these to the function.
     *
     * @param field    the field to calculate the base and offset for
     * @param instance an instance of the owning class of the field for instance fields, or {@code null} for static fields
     * @param function the function to call
     * @param <T>      the return type of the function
     * @return the return value of the function
     */
    private static <T> @Nullable T calculateReturn(final Field field, @Nullable final Object instance, final UnsafeInfoFunction<T> function) {
        Object base;
        long offset;
        if (instance == null) { // Static field
            base = UNSAFE.staticFieldBase(field);
            offset = UNSAFE.staticFieldOffset(field);
        } else {
            base = instance;
            offset = UNSAFE.objectFieldOffset(field);
        }

        return function.apply(base, offset);
    }

    /**
     * Represents a function that accepts a base and offset and produces a result.
     *
     * @param <T> the type of the result of the function
     * @see Unsafe#getInt(Object, long)
     */
    @FunctionalInterface
    private interface UnsafeInfoFunction<T> {
        @Nullable T apply(Object base, long offset);
    }

    /**
     * Represents an operation that accepts a base and offset and returns no result
     *
     * @see Unsafe#getInt(Object, long)
     */
    @FunctionalInterface
    private interface UnsafeInfoConsumer extends UnsafeInfoFunction<Void> {
        @Override
        default Void apply(Object base, long offset) {
            accept(base, offset);
            return null;
        }

        void accept(Object base, long offset);
    }
}
