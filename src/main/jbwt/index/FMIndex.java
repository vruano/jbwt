package jbwt.index;

import jbwt.utils.ParamUtils;

/**
 * Created by valentin on 12/25/16.
 */
public class FMIndex<A extends FMAlphabet<?>> {

    private final A alphabet;

    private FMIndex(final A alphabet, final int[] F, final long[] L) {
        this.alphabet = ParamUtils.requiresNonNull(alphabet);
        ParamUtils.validLength(F, alphabet.size(), alphabet.size());
        ParamUtils.requiresNonNull(L);
    }

}
