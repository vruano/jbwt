package jbwt.index;

import jbwt.sequences.SymbolSequence;
import jbwt.utils.BitArray;
import jbwt.utils.MathUtils;
import jbwt.utils.ParamUtils;

/**
 * Created by valentin on 1/1/17.
 */
public class BWTArray<S extends Symbol> implements SymbolSequence<S> {

    private final int blockPadding;
    private Alphabet<S> alphabet;

    private BitArray bits;

    private final int bitsPerSymbol;
    private final int bitsPerRun;
    private final int runsPerBlock;
    private final long symbolMask;
    private long[] symbolCounts;
    private long length;

    private static final int MAXIMUM_RUN_LENGTH = 16;
    private static final int RUN_LENGTH_BITS = MathUtils.bits(MAXIMUM_RUN_LENGTH);

    public BWTArray(final Alphabet<S> alphabet, final int initialCapacity) {
        ParamUtils.requiresNonNull(alphabet);
        bitsPerSymbol = alphabet.bitsPerSymbol();
        symbolMask = (1 << bitsPerSymbol) - 1;
        bitsPerRun = RUN_LENGTH_BITS + bitsPerSymbol;
        runsPerBlock = BitArray.BLOCK_SIZE / bitsPerRun;
        blockPadding = BitArray.BLOCK_SIZE - (runsPerBlock * bitsPerRun);
        symbolCounts = new long[alphabet.size()];
        bits = new BitArray(bitsPerRun * initialCapacity);
    }

    public void append(final SymbolSequence<S> sequence) {
        if (!this.alphabet.equals(sequence.getAlphabet()))
            throw new IllegalArgumentException("incompatible alphabet");
        final long sequenceLength = sequence.length();
        int runsInBlock = 0;
        if (sequenceLength > 0) {
            final BitArray.Iterator it = bits.iterator(bits.getLength());
            long currentBase;
            long currentLength;
            if (length > 0) {
                final long previousRun = it.previousLong(bitsPerRun);
                currentBase = previousRun & symbolMask;
                currentLength = previousRun >>> bitsPerSymbol;
                length -= currentLength;
                symbolCounts[(int) currentBase] -= currentLength;
                it.nextLong(bitsPerRun);
            } else {
                currentBase = sequence.getInt(0);
                currentLength = 0;
            }
            for (int i = 0; i < sequenceLength; i++) {
                final long nextBase = sequence.getInt(i);
                if (nextBase != currentBase || currentLength == MAXIMUM_RUN_LENGTH) {
                    it.insert(currentLength << bitsPerSymbol | currentBase, bitsPerRun);
                    symbolCounts[(int) currentBase] += currentLength;
                    length += currentLength;
                    currentBase = nextBase;
                    currentLength = 1;
                } else {
                    currentLength++;
                }
            }
            symbolCounts[(int) currentBase] += currentLength;
            length += currentLength;
            it.insert(currentLength << bitsPerSymbol | currentBase, bitsPerRun);
        }
    }

    @Override
    public long length() {
        return length;
    }

    @Override
    public S getSymbol(final long position) {
        return alphabet.toSymbol(getInt(position));
    }

    @Override
    public int getInt(final long position) {
        ParamUtils.requiresBetween(position, 0, length - 1);
        final BitArray.Iterator it = bits.iterator();
        long currentPosition = -1;
        while( it.hasMore()) {
            final long run = it.nextLong(bitsPerRun);
            final long runLength = run >>> bitsPerSymbol;
            currentPosition += runLength;
            if (currentPosition >= position) {
                return (int) (run & symbolMask);
            }
        }
        throw new IllegalStateException("length must be wrong");
    }

    @Override
    public Alphabet<S> getAlphabet() {
        return alphabet;
    }

    public long[] getCounts() {
        return symbolCounts.clone();
    }
}
