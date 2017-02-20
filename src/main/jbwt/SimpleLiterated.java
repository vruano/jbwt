package jbwt;

import jbwt.index.Alphabet;
import jbwt.index.Symbol;
import jbwt.utils.ParamUtils;

/**
 * Simple implementation of {@link Literated} based on a reference to the
 * corresponding {@link Alphabet alphabet}.
 *
 * @type S the alphabet's symbol type.
 */
public class SimpleLiterated<S extends Symbol> implements Literated<S> {

    private final Alphabet<S> alphabet;

    public SimpleLiterated(final Alphabet<S> alphabet) {
        this.alphabet = ParamUtils.requiresNonNull(alphabet);
    }

    @Override
    public Alphabet<S> alphabet() {
        return alphabet;
    }
}
