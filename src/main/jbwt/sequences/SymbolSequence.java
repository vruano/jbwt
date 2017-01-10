package jbwt.sequences;

import jbwt.index.Alphabet;
import jbwt.index.Symbol;
import jbwt.utils.ParamUtils;

import java.util.Iterator;
import java.util.ListIterator;
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

    @Override
    default Iterator<S> iterator() {
        return new SimpleIterator<S>(this, 0);
    }

    static <S extends Symbol> int hashCode(final AbstractSymbolSequence<S> s) {
        ParamUtils.requiresNonNull(s);
        int result = 0;
        for (final S symbol : s)
            result += 31 * result + symbol.toInt();
        return result;
    }

    interface Iterator<S extends Symbol> extends java.util.Iterator<S> {

        boolean hasNext();

        S next();

        boolean hasPrevious();

        S previous();

        long nextPosition();

        long previousPosition();
    }


    default Iterator<S> iterator(final long position) {
        ParamUtils.requiresBetween(position, 0, length());
        return new SimpleIterator<S>(this, position);
    }

    class SimpleIterator<S extends Symbol> implements Iterator<S> {

        private long nextPosition;
        private final SymbolSequence<S> sequence;

        SimpleIterator(final SymbolSequence<S> sequence, final long position) {
            this.sequence = ParamUtils.requiresNonNull(sequence);
            this.nextPosition = ParamUtils.requiresBetween(position, 0, sequence.length());
        }

        public boolean hasNext() {
            return nextPosition < sequence.length();
        }

        @Override
        public S next() {
            if (nextPosition >= sequence.length())
                throw new NoSuchElementException();
            return sequence.getSymbol(nextPosition++);
        }

        @Override
        public boolean hasPrevious() {
            return nextPosition > 0;
        }

        @Override
        public S previous() {
            if (nextPosition <= 0)
                throw new NoSuchElementException();
            return sequence.getSymbol(--nextPosition);
        }

        @Override
        public long nextPosition() {
            return nextPosition;
        }

        @Override
        public long previousPosition() {
            return nextPosition - 1;
        }
    }

    /**
     * Compares two sequences where it returns true iff both represent the same sequence of symbols.
     * @param a first sequence to compare.
     * @param b second sequence to compare.
     * @param <S> the symbol type.
     * @return {@code true} iff both sequences are identical.
     */
    static <S extends Symbol> boolean equals(final SymbolSequence<S> a, final SymbolSequence<S> b) {
        if (a == b)
            return a != null;
        else if (a == null || b == null)
            return false;
        else if (a.length() != b.length())
            return false;
        else {
            final Iterator<S> aIterator = a.iterator();
            final Iterator<S> bIterator = b.iterator();
            while (aIterator.hasNext()) {
                if (!aIterator.next().equals(bIterator.next()))
                    return false;
            }
            return true;
        }
    }

    /**
     * Returns a string representation of a sequence.
     * @param a first sequence to compare.
     * @param <S> the symbol type.
     * @return {@code "null"} if the input is {@code null}, otherwise the natural string presentation of the sequence.
     */
    static <S extends Symbol> String toString(final SymbolSequence<S> a) {
        if (a == null)
            return "null";
        else if (a.length() == 0)
            return "";
        else if (a.length() > Integer.MAX_VALUE)
            throw new UnsupportedOperationException("the sequence is two long to compose an string: " + a.length());
        else {
            final StringBuilder builder = new StringBuilder((int)a.length());
            final Iterator<S> it = a.iterator();
            for (final S s : a)
                builder.append(it.next().toString());
            return builder.toString();
        }
    }
}
