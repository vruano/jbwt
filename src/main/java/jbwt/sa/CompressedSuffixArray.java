package jbwt.sa;

import jbwt.SimpleLiterated;
import jbwt.base.Alphabet;
import jbwt.utils.ParamUtils;
import jbwt.utils.WordArray;

/**
 * Implements the CSA as described in [ref].
 */
public class CompressedSuffixArray<S> extends SimpleLiterated<S> {

    private Alphabet<S> alphabet;

    private WordArray[] q;
    private WordArray[] r;

    protected CompressedSuffixArray(final Alphabet<S> alphabet, final WordArray[] q, final WordArray[] r) {
        super(alphabet);
        this.q = ParamUtils.requiresNoNull(q);
        this.r = ParamUtils.requiresNoNull(r);
        if (q.length != alphabet.size()) throw new IllegalArgumentException("the q array must have as many elements as elements in the alphabet");
        if (r.length != alphabet.size()) throw new IllegalArgumentException("the r array must have as many elements as elements in the alphabet");
        for (int i = 0; i < q.length; i++)
            if (q[i].size() != r[i].size())
                throw new IllegalArgumentException("the corresponding q and r word-arrays must contain the same number of words");
    }


}
