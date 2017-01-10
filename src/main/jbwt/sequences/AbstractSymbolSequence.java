package jbwt.sequences;

import jbwt.index.Alphabet;
import jbwt.index.Symbol;
import jbwt.utils.ParamUtils;
import sun.jvm.hotspot.debugger.cdbg.Sym;

/**
 * Created by valentin on 1/9/17.
 */
public abstract class AbstractSymbolSequence<S extends Symbol> implements SymbolSequence<S> {

    protected final Alphabet<S> alphabet;

    private int hash;

    public AbstractSymbolSequence(final Alphabet<S> alphabet) {
        this.alphabet = ParamUtils.requiresNonNull(alphabet);
    }

    @Override
    public Alphabet<S> alphabet() {
        return alphabet;
    }

    @Override
    public String toString() {
        return SymbolSequence.toString(this);
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean equals(final Object other) {
        if (other instanceof SymbolSequence) {
            return SymbolSequence.equals(this, (SymbolSequence<S>) other);
        } else {
            return super.equals(other);
        }
    }

    @Override
    public int hashCode() {
        if (hash != 0 || length() == 0)
            return hash;
        else
            return hash = SymbolSequence.hashCode(this);
    }
}
