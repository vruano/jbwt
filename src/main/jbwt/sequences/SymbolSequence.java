package jbwt.sequences;

import jbwt.index.Alphabet;
import jbwt.index.Symbol;
import jbwt.utils.ParamUtils;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Created by valentin on 6/23/16.
 */
public interface SymbolSequence<S extends Symbol> extends Iterable<S> {

    long length();

    S getSymbol(final long position);

    int getInt(final long position);

    default void setSymbol(final long position, final S newValue) {
        throw new UnsupportedOperationException();
    }

    default SymbolSequence<S> subSequence(final long offset, final long length) {
        final SymbolSequence<S> parent = this;
        return new SymbolSequence<S>() {


            @Override
            public long length() {
                return length;
            }

            @Override
            public S getSymbol(final long position) {
                return parent.getSymbol(offset + position);
            }

            @Override
            public int getInt(final long position) { return parent.getInt(offset + position); }

            @Override
            public Alphabet<S> alphabet() {
                return parent.alphabet();
            }
        };
    }

    Alphabet<S> alphabet();

    default byte[] getBytes(final long position, final int length) {
        ParamUtils.requiresNonNegative(length);
        final byte[] result = new byte[length];
        for (int i = 0; i < length; i++) {
            result[i] = (byte) getSymbol(length).toString().charAt(i);
        }
        return result;
    }

    default byte[] getBytes() {
        final long longLength = length();
        if (longLength > Integer.MAX_VALUE)
            throw new UnsupportedOperationException("the sequence is too larger to return an a single array");
        final int length = (int) longLength;
        final byte[] result = new byte[length];
        for (int i = 0; i < length; i++) {
            result[i] = (byte) getSymbol(length).toString().charAt(i);
        }
        return result;
    }


    default Iterator<S> iterator() {
        return new Iterator<S>() {

            private long position = 0;

            @Override
            public boolean hasNext() {
                return position < length();
            }

            @Override
            public S next() {
                if (!hasNext())
                    throw new NoSuchElementException();
                return getSymbol(position++);
            }
        };
    }
}
