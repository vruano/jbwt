package jbwt.base;

import jbwt.base.Alphabet;

/**
 * A literate object is the one that possesses an alphabet such a {@link Text}.
 */
public interface Literate<A extends Alphabet<?>> {

    /**
     * Returns the alphabet for this literate.
     * @return never {@code null}.
     */
    A alphabet();
}
