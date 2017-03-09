package jbwt.index;

import jbwt.base.Alphabet;
import jbwt.common.RLEText;
import jbwt.index.utils.BCRSuffixArray;
import jbwt.utils.ParamUtils;

/**
 * Created by valentin on 1/5/17.
 */
public class BCRSymbolSequence<S, A> extends RLEText<S> {

    private BCRSuffixArray<A> suffixArray;

    private final long[] symbolCounts;

    public BCRSymbolSequence(final Alphabet<S> alphabet) {
        super(alphabet);
        symbolCounts = new long[alphabet.size()];
        suffixArray = new BCRSuffixArray<>();
    }

    private BCRSymbolSequence(final Alphabet<S> alphabet, final byte[] runs, final int lengthInRuns, final long length, final long[] symbolCounts) {
        super(alphabet, runs, lengthInRuns, length);
        this.symbolCounts = symbolCounts;
    }

    public long symbolCount(final S symbol) {
        ParamUtils.requiresNonNull(symbol);
        final int symbolIndex = ParamUtils.validIndex(alphabet.toCode(symbol), 0, symbolCounts.length);
        return symbolCounts[symbolIndex];
    }

    long symbolCountTo(final S symbol, final long to) {
        final int symbolInt = alphabet.toCode(symbol);
        return symbolCountTo(symbolInt, to);
    }

    long symbolCountTo(final int symbolInt, final long to) {
        final long symbolCount = symbolCounts[symbolInt];
        if (to < symbolCount >> 1) {
            return symbolCountToFromTheBeginning(symbolInt, to);
        } else {
            return symbolCount - symbolCountToFromEnd(symbolInt, to);
        }
    }

    private long symbolCountToFromTheBeginning(final int symbolInt, final long to) {
        long remaining = to;
        long result = 0;
        for (int run = 0; run < lengthInRuns; run++) {
            final int runSymbol = runs[run] & symbolMask;
            final int runLength = 1 + ((runs[run] & lengthMask) >>> bitsPerSymbol);
            if (remaining <= runLength) {
                return runSymbol == symbolInt ? result + remaining : result;
            } else {
                remaining -= runLength;
                if (runSymbol == symbolInt)
                    result += runLength;
            }
        }
        return result;
    }

    @Override
    public void insert(final long position, final S symbol) {
        super.insert(position, symbol);
        suffixArray.insert(position, 1);
    }

    public void insert(final long position, final S symbol, final A annotationValue) {
        super.insert(position, symbol);
        suffixArray.insert(position, annotationValue);
    }

    @Override
    protected void insert(final int run, final int offset, final int symbol) {
        super.insert(run, offset, symbol);
        symbolCounts[symbol]++;
    }

    @Override
    protected void append(final int symbol) {
        super.append(symbol);
        symbolCounts[symbol]++;
    }

    @Override
    protected void append(final RLEText<S> other) {
        super.append(other);
        if (other instanceof BCRSymbolSequence) {
            for (int i = 0; i < symbolCounts.length; i++)
                symbolCounts[i] += ((BCRSymbolSequence<S, A>)other).symbolCounts[i];
            suffixArray.append(length, ((BCRSymbolSequence<S, A>)other).suffixArray);
        } else {
            other.counts(symbolCounts);
        }
    }

    private long symbolCountToFromEnd(final int symbolInt, final long to) {
        long remaining = length - to;
        long result = 0;
        for (int run = lengthInRuns - 1; run >= 0; run--) {
            final int runSymbol = runs[run] & symbolMask;
            final int runLength = 1 + ((runs[run] & lengthMask) >>> bitsPerSymbol);
            if (remaining <= runLength) {
                return runSymbol == symbolInt ? result + remaining : result;
            } else {
                remaining -= runLength;
                if (runSymbol == symbolInt)
                    result += runLength;
            }   
        }
        return result;
    }

    public void copySymbolCounts(final long[] destination) {
        ParamUtils.requiresNonNull(destination);
        ParamUtils.validLength(destination, symbolCounts.length, symbolCounts.length);
        System.arraycopy(symbolCounts, 0, destination, 0, symbolCounts.length);
    }

    public BCRSymbolSequence<S, A> splitTop() {
        final int newLengthInRuns = (lengthInRuns + 1) >> 1;
        final int resultLengthInRuns = lengthInRuns - newLengthInRuns;
        final long[] resultSymbolCounts = new long[symbolCounts.length];
        final byte[] resultRuns = new byte[runs.length];
        System.arraycopy(runs, newLengthInRuns, resultRuns, 0, resultLengthInRuns);

        long resultLength = 0;
        for (int i = newLengthInRuns; i < lengthInRuns; i++) {
            final int length =  1 + ((runs[i] & lengthMask) >>> bitsPerSymbol);
            final int symbol = runs[i] & symbolMask;
            resultSymbolCounts[symbol] += length;
            resultLength += length;
        }
        length -= resultLength;
        lengthInRuns -= resultLengthInRuns;
        for (int i = 0; i < symbolCounts.length; i++)
            symbolCounts[i] -= resultSymbolCounts[i];
        return new BCRSymbolSequence<>(alphabet, resultRuns, resultLengthInRuns, resultLength, resultSymbolCounts);
    }

    private class Annotation<A> {
        private long position;

        private A value;

        public Annotation(final long position) {
            this.position = position;
        }

        public Annotation(final long position, final A annotationValue) {
            this.position = position;
            this.value = annotationValue;
        }
    }
}
