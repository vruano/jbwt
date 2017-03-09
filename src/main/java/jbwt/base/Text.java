package jbwt.base;

import jbwt.utils.ParamUtils;

import java.util.NoSuchElementException;

/**
 *
 */
public interface Text<S> extends Literate<Alphabet<S>>, Iterable<S> {

    long length();

    int codeAt(long position);

    S symbolAt(long position);

    Text<S> subtext(long from, long to);

    default long[] counts() {
        return counts(null);
    }

    default long[] counts(final long[] init) {
        final long[] result = init == null ? new long[alphabet().size()] : init;
        if (init != null && init.length < alphabet().size())
            throw new IllegalArgumentException("the input initial counts array does not have enough elements for this alphabet");
        for (long i = 0; i < length(); i++) {
            result[codeAt(i)]++;
        }
        return result;
    }

    default SymbolIterator<S> iterator() {
        return iterator(0);
    }

    default SymbolIterator<S> iterator(final long p) {
        ParamUtils.requiresBetween(p, 0, length());
        return new SymbolIterator<S>() {

            private long nextPosition = p;

            @Override
            public boolean hasNext() {
                return nextPosition < length();
            }

            @Override
            public S next() {
                if (nextPosition >= length())
                    throw new NoSuchElementException();
                else
                    return symbolAt(nextPosition++);
            }

            @Override
            public S previous() {
                if (nextPosition <= 0)
                    throw new NoSuchElementException();
                else
                    return symbolAt(--nextPosition);
            }

            @Override
            public long nextPosition() {
                return nextPosition;
            }

            @Override
            public long previousPosition() {
                return nextPosition - 1;
            }

            @Override
            public boolean hasPrevious() {
                return nextPosition > 0;
            }
        };
    }

}
