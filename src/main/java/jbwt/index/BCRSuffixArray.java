package jbwt.index;

import jbwt.base.Alphabet;
import jbwt.utils.ParamUtils;
import sun.security.util.BitArray;

/**
 * Created by valentin on 12/27/16.
 */
public class BCRSuffixArray<A extends Alphabet<?>> {

    private final A alphabet;

    private final int order;

    public BCRSuffixArray(final A alphabet, final int order) {
        this.alphabet = ParamUtils.requiresNonNull(alphabet);
        this.order  = ParamUtils.requiresGreaterThanZero(order);
    }

    private class Node {
        private Count count;
        private Node[] children;

    }

    private class Count {
        public BitArray bits;
        public byte[] values;
    }
}
