package jbwt.utils;

/**
 * Log base 2 utility methods.
 */
public final class Log2 {

    private Log2() {};

    /**
     * Returns number of bits required to encode positive integers given the largest value possible.
     * @param v the maximum value to encode.
     * @return 0 or greater.
     */
    public static int bits(final int v) {
        if (v > 0)
            return Integer.SIZE - Integer.numberOfLeadingZeros(v - 1);
        else if (v == 0)
            return 0;
        else
            throw new IllegalArgumentException("input cannot be negative");
    }


    /**
     *
     * @param v the input value
     * @throws IllegalArgumentException for 0 or negative inputs, this operation does not make sense as
     *  one cannot code the {@link Double#NaN} into a int value.
     */
    public static int ceilAsInt(final int v) {
        if (v > 0)
            return Integer.SIZE - Integer.numberOfLeadingZeros(v - 1);
        else if (v == 0)
            return Integer.MIN_VALUE;
        else
            throw new IllegalArgumentException("input cannot be negative");
    }

    /**
     *
     * @param v the input value
     * @throws IllegalArgumentException for 0 or negative inputs, this operation does not make sense as
     *  one cannot code the {@link Double#NaN} into a int value.
     */
    public static int floorAsInt(final int v) {
        if (v > 0)
            return Integer.SIZE - 1 - Integer.numberOfLeadingZeros(v);
        else if (v == 0)
            return Integer.MAX_VALUE;
        else
            throw new IllegalArgumentException();
    }
}
