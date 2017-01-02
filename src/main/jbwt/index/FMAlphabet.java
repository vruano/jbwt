package jbwt.index;

import jbwt.utils.ParamUtils;

/**
 * Created by valentin on 12/25/16.
 */
public class FMAlphabet<A extends Enum<A> & FMSymbol> {

    private final Class<A> symbolClass;

    private A[] symbolConstants;

    private final int symbolBits;

    private final long mask;

    private final int longSymbols;

    private final int size;

    public FMAlphabet(final Class<A> symbols) {
        this.symbolClass = ParamUtils.requiresNonNull(symbols);
        this.symbolConstants = symbols.getEnumConstants();
        this.size = symbols.getEnumConstants().length;
        this.symbolBits = (int) Math.ceil(Math.log(this.size + 1) / Math.log(2));
        this.longSymbols = Long.SIZE / this.symbolBits;
        this.mask = (long) Math.pow(2, this.symbolBits) - 1;
    }

    public int size() {
        return symbolClass.getEnumConstants().length;
    }

    public A symbolAt(final long[] words, final long offset) {
        ParamUtils.requiresNonNull(words);
        if (offset < 0 || offset >= longSymbols * words.length) {
            throw new IllegalArgumentException();
        }
        final int wordIndex = (int) offset / longSymbols;
        return symbolAt(words[wordIndex], (int) (offset - wordIndex * longSymbols));
    }

    public A symbolAt(final long word, final int offset) {
        ParamUtils.validIndex(offset, 0, longSymbols);
        return symbolConstants[(int) (mask & (word >>> (offset * symbolBits)))];
    }

    public long toLong(final A ... symbols) {
        ParamUtils.validLength(symbols, 0, longSymbols);
        ParamUtils.containsNonNulls(symbols);
        long result = 0;
        for (int i = symbols.length - 1; i >= 0; --i) {
            result <<= symbolBits;
            result += symbols[i].ordinal();
        }
        return result;
    }

    public void fromLong(final long word, final A[] dest) {
        ParamUtils.validLength(dest, 0, longSymbols);
        long buffer = word;
        for (int i = 0; i < dest.length; i++) {
            final int index = (int) (buffer & mask);
            if (index == 0) {
                dest[i] = null;
            } else {
                dest[i] = symbolConstants[ParamUtils.validIndex(index, 0, size)];
            }
            buffer >>>= symbolBits;
        }
    }
}
