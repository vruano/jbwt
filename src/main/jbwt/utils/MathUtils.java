package jbwt.utils;

/**
 * Created by valentin on 12/28/16.
 */
public final class MathUtils {

    private MathUtils() {
    }

    public static int bits(final int symbolCount) {
        return (int) Math.ceil(Math.log(symbolCount) * MathConstants.INV_LN_2);
    }
}
