package jbwt.utils;

import java.util.Objects;

/**
 * Created by valentin on 12/25/16.
 */
public final class ParamUtils {

    private ParamUtils() {
    }

    public static <A> A requiresNonNull(final A value) {
        return Objects.requireNonNull(value);
    }

    public static int validIndex(final int value, final int from, final int to) {
        if (value < from || value >= to) {
            throw new IllegalArgumentException();
        }
        return value;
    }

    public static long validIndex(final long value, final long from, final long to) {
        if (value < from || value >= to) {
            throw new IllegalArgumentException();
        }
        return value;
    }

    public static void validLength(final Object[] array, final int min, final int max) {
        if (array.length < min || array.length > max) {
            throw new IllegalArgumentException();
        }
    }

    public static int requiresBetween(final int value, final int min, final int max) {
        if (value < min || value > max) {
            throw new IllegalArgumentException();
        }
        return value;
    }

    public static long requiresBetween(final long value, final long min, final long max) {
        if (value < min || value > max) {
            throw new IllegalArgumentException();
        }
        return value;
    }

    public static void validLength(final int[] array, final int min, int max) {
        if (array.length < min || array.length > max) {
            throw new IllegalArgumentException();
        }
    }

    public static void validLength(final long[] array, final int min, int max) {
        if (array.length < min || array.length > max) {
            throw new IllegalArgumentException();
        }
    }

    public static <E>  E[] requiresNoNull(final E ... array) {
        if (array == null) {
            throw new NullPointerException();
        } else {
            for (final Object o : array) {
                if (o == null) {
                    throw new NullPointerException();
                }
            }
        }
        return array;
    }

    public static int requiresGreaterThanZero(final int order) {
        if (order <= 0) throw new IllegalArgumentException();
        return order;
    }

    public static int requiresNonNegative(final int value) {
        if (value < 0) throw new IllegalArgumentException();
        return value;
    }

    public static long requiresNonNegative(final long value) {
        if (value < 0) throw new IllegalArgumentException();
        return value;
    }
}
