package jbwt.common;

import jbwt.base.Alphabet;
import jbwt.utils.ParamUtils;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Created by valentin on 2/25/17.
 */
public class EnumAlphabet<E extends Enum<E>> implements Alphabet<E> {

    private Class<E> clazz;
    private final int size;
    private final E[] symbols;
    private final List<E> symbolsAsUnmodifiableList;

    public EnumAlphabet(final Class<E> clazz) {
        this.clazz = clazz;
        symbols = clazz.getEnumConstants();
        size = symbols.length;
        symbolsAsUnmodifiableList = Collections.unmodifiableList(Arrays.asList(symbols));
    }

    public int size() {
        return size;
    }

    public int toCode(final E symbol) {
        return ParamUtils.requiresNonNull(symbol).ordinal();
    }

    public E toSymbol(final int index) {
        return symbols[ParamUtils.validIndex(index, 0, symbols.length)];
    }

    @Override
    public List<E> symbols() {
        return symbolsAsUnmodifiableList;
    }

    @Override
    public boolean equals(final Object o) {
        return o instanceof EnumAlphabet && ((EnumAlphabet) o).clazz.equals(clazz);
    }

    @Override
    public int hashCode() {
        return clazz.hashCode();
    }

    @Override
    public String toString() {
        return Arrays.toString(symbols);
    }
}

