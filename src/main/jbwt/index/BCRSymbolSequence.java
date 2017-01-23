package jbwt.index;

import jbwt.sequences.SymbolSequence;
import jbwt.utils.ParamUtils;

/**
 * Created by valentin on 1/5/17.
 */
public class BCRSymbolSequence<S extends Symbol> extends RLESymbolSequence<S> {

    private final long[] symbolCounts;

    public BCRSymbolSequence(final Alphabet<S> alphabet) {
        super(alphabet);
        symbolCounts = new long[alphabet.size()];
    }

    public long symbolCount(final S symbol) {
        ParamUtils.requiresNonNull(symbol);
        final int symbolIndex = ParamUtils.validIndex(symbol.toInt(), 0, symbolCounts.length);
        return symbolCounts[symbolIndex];
    }

    long symbolCountTo(final S symbol, final long to) {
        final int symbolInt = symbol.toInt();
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
    protected void append(final RLESymbolSequence<S> other) {
        super.append(other);
        if (other instanceof BCRSymbolSequence) {
            for (int i = 0; i < symbolCounts.length; i++)
                symbolCounts[i] += ((BCRSymbolSequence<S>)other).symbolCounts[i];
        } else {
            for (int run = 0; run < other.lengthInRuns; run++) {
                final int runSymbol = other.runs[run] & symbolMask;
                final int runLength = 1 + ((other.runs[run] & lengthMask) >>> bitsPerSymbol);
                symbolCounts[runSymbol] += runLength;
            }
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
}
