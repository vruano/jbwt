package jbwt.common;

import jbwt.base.Alphabet;
import jbwt.base.Literate;
import jbwt.utils.ParamUtils;

/**
 * Created by valentin on 2/25/17.
 */
public class SimpleLiterate<A extends Alphabet<?>> implements Literate<A> {

    protected final A alphabet;

    public SimpleLiterate(final A alphabet) {
        this.alphabet = ParamUtils.requiresNonNull(alphabet);
    }

    @Override
    public A alphabet() {
        return alphabet;
    }
}
