package jbwt.index;

import jbwt.utils.ParamUtils;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by valentin on 12/25/16.
 */
public class Alphabet<A extends Symbol>  {

    private final int symbolBits;

    private final long mask;

    private final int longSymbols;

    private final int size;

    private final A[] symbols;

    public Alphabet(final A ... symbols) {
        this.symbols = checkOnSymbols(symbols);
        this.size = symbols.length;
        this.symbolBits = (int) Math.ceil(Math.log(this.size) / Math.log(2));
        this.longSymbols = Long.SIZE / this.symbolBits;
        this.mask = (long) Math.pow(2, this.symbolBits) - 1;
    }

    public A toSymbol(final int value) {
        if (value < 0 || value >= symbols.length)
            throw new IllegalArgumentException("");
        return symbols[value];
    }

    private static <A extends Symbol> A[] checkOnSymbols(final A ... symbols) {
        ParamUtils.containsNonNulls(symbols);
        if (symbols.length == 0)
            throw new IllegalArgumentException("there must be at least one symbol");
        if (!symbols[0].isSentinel())
            throw new IllegalArgumentException("the first symbol must be a sentinel");
        final Set<A> symbolSet = new HashSet<>();
        for (int i = 1; i < symbols.length; i++)
            if (symbols[i].isSentinel())
                throw new IllegalArgumentException("no other than the first symbol can be a sentinel");
        for (int i = 0; i < symbols.length; i++)
            if (!symbolSet.add(symbols[i]))
                throw new IllegalArgumentException("repeat symbol: " + symbols[i]);
        for (int i = 0; i < symbols.length; i++)
            if (symbols[i].toInt() != i)
                throw new IllegalArgumentException("the int does not correspond to the symbols index");
        if (symbolSet.size() != symbols.length) {
            throw new IllegalArgumentException("symbols cannot contain repeats");
        }
        return symbols.clone();
    }

    public A valueOf(int symbol) {
        if (symbol < 0 || symbol >= symbols.length)
            throw new IllegalArgumentException();
        else
            return symbols[symbol];
    }

    public int size() {
        return size;
    }

    public int bitsPerSymbol() {
        return symbolBits;
    }


    @SuppressWarnings("unchecked")
    public boolean equals(final Object other) {
        return other instanceof Alphabet && equals((Alphabet) other);
    }

    public boolean equals(final Alphabet<A> alphabet) {
        return alphabet == this || (alphabet != null && Arrays.equals(symbols, alphabet.symbols));
    }

}
