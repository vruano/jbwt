package jbwt.index;

import jbwt.base.Alphabet;
import jbwt.utils.ParamUtils;

import java.util.AbstractList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by valentin on 3/8/17.
 */
class ArrayAlphabet<A> implements Alphabet<A> {

    /**
     * Return the number of bits required to encode each element of the alphabet.
     * @return 0 or greater.
     */
    private final int symbolBits;

    private final long mask;

    private final int longSymbols;

    private final int size;

    private final A[] symbols;

    private final List<A> symbolsAsUnmodifiableList;

    public ArrayAlphabet(final A ... symbols) {
        this.symbols = checkOnSymbols(symbols);
        this.size = symbols.length;
        this.symbolBits = (int) Math.ceil(Math.log(this.size) / Math.log(2));
        this.longSymbols = Long.SIZE / this.symbolBits;
        this.mask = (long) Math.pow(2, this.symbolBits) - 1;
        this.symbolsAsUnmodifiableList = new AbstractList<A>() {

            @Override
            public int size() {
                return symbols.length;
            }

            @Override
            public A get(int index) {
                ParamUtils.validIndex(index, 0, symbols.length);
                return symbols[index];
            }
        };
    }

    public A toSymbol(final int value) {
        if (value < 0 || value >= symbols.length)
            throw new IllegalArgumentException("");
        return symbols[value];
    }

    public List<A> symbols() {
        return symbolsAsUnmodifiableList;
    }

    private static <A> A[] checkOnSymbols(final A ... symbols) {
        ParamUtils.requiresNoNull(symbols);
        if (symbols.length == 0)
            throw new IllegalArgumentException("there must be at least one symbol");
        final Set<A> symbolSet = new HashSet<>();
        for (int i = 0; i < symbols.length; i++)
            if (!symbolSet.add(symbols[i]))
                throw new IllegalArgumentException("repeat symbol: " + symbols[i]);
        if (symbolSet.size() != symbols.length) {
            throw new IllegalArgumentException("symbols cannot contain repeats");
        }
        return symbols.clone();
    }

    public int size() {
        return size;
    }

    @Override
    public int toCode(A symbol) {
        return 0;
    }

    public int bits() {
        return symbolBits;
    }

    @SuppressWarnings("unchecked")
    public boolean equals(final Object other) {
        return other instanceof Alphabet && equals((Alphabet) other);
    }

    public boolean equals(final Alphabet<A> alphabet) {
        if (alphabet == this)
            return true;
        else if (alphabet == null)
            return false;
        else if (alphabet.size() != this.size())
            return false;
        else {
            for (int i = 0; i < alphabet.size(); i++) {
                if (!toSymbol(i).equals(alphabet.toSymbol(i)))
                    return false;
            }
            return true;
        }
    }

}
