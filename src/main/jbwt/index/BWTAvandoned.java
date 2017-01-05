package jbwt.index;

import jbwt.sequences.SymbolSequence;
import jbwt.utils.BitArray;
import jbwt.utils.MathUtils;
import jbwt.utils.ParamUtils;

/**
 * @Todo avandoned as I think the complexity of the BitArray is not worth it unless the index is rather big.
 */
public class BWTAvandoned<S extends Symbol> implements SymbolSequence<S> {

    private final int blockPadding;
    private Alphabet<S> alphabet;

    private BitArray bits;

    private final int bitsPerSymbol;
    private final int bitsPerRun;
    private final int runsPerBlock;
    private final int maximumRunLength;
    private final int symbolMask;
    private final long runMask;
    private final long runLengthMask;
    private long[] symbolCounts;
    private long length;
    private final int runLengthBits;

    public BWTAvandoned(final Alphabet<S> alphabet, final int maxiumRunLength, final int initialCapacity) {
        ParamUtils.requiresNonNull(alphabet);
        ParamUtils.requiresBetween(maxiumRunLength, 1, Integer.MAX_VALUE);
        this.maximumRunLength = maxiumRunLength;
        this.runLengthBits = MathUtils.bits(maxiumRunLength);
        bitsPerSymbol = alphabet.bitsPerSymbol();
        bitsPerRun = runLengthBits + bitsPerSymbol;
        symbolMask = (1 << bitsPerSymbol) - 1;
        runMask = (1 << bitsPerRun) - 1;
        runLengthMask = runMask ^ symbolMask;
        runsPerBlock = BitArray.BLOCK_SIZE / bitsPerRun;
        blockPadding = BitArray.BLOCK_SIZE - (runsPerBlock * bitsPerRun);
        symbolCounts = new long[alphabet.size()];
        bits = new BitArray(bitsPerRun * initialCapacity);
    }

    private class RunIterator {

        private long nextRun;
        private long lastRunPosition;
        private long lastRun;
        private int lastRunLength;
        private int lastRunSymbol;
        private long lastZeroLengthRunPosition = -1;
        private BitArray.Iterator arrayIterator;

        private RunIterator() {
            nextRun = 0;
            arrayIterator = bits.iterator();
        }

        private void goToEnd() {
            nextRun = bits.getLength() / bitsPerRun;
            arrayIterator.goToEnd();
        }

        private boolean hasPrevious() {
            return arrayIterator.hasPrevious();
        }

        private boolean hasNext() {
            return arrayIterator.hasMore();
        }

        private long next() {
            lastRunPosition = arrayIterator.position();
            lastRun = arrayIterator.nextLong(bitsPerRun);
            nextRun++;
            updateLastRunData();
            return lastRun;
        }

        private void updateLastRunData() {
            lastRunSymbol = (int) (lastRun & symbolMask);
            lastRunLength = (int) ((lastRun & runLengthMask) >>> bitsPerSymbol);
            // we take this oportunity to move 0-length runs forward.
            if (lastRunLength != 0) {
                if (lastZeroLengthRunPosition != -1) {
                    bits.set(lastZeroLengthRunPosition, lastRun, bitsPerRun);
                    final long newLastZeroLengthRunPosition = lastRunPosition;
                    lastRunPosition = lastZeroLengthRunPosition;
                    lastZeroLengthRunPosition = newLastZeroLengthRunPosition;
                }

            } else {
                lastZeroLengthRunPosition = lastRunPosition;
            }
        }

        private void insert(final int symbol, final int length) {
            final long run = (symbol & symbolMask) | ((length << bitsPerSymbol) | runLengthMask);
            if (lastZeroLengthRunPosition == lastRunPosition) {
                bits.set(lastZeroLengthRunPosition, run, bitsPerRun);
                lastZeroLengthRunPosition = -1;
            } else {
                final long block = run & runMask;
                arrayIterator.insert(block, BitArray.BLOCK_SIZE);
            }
            lastRun = run;
            lastRunSymbol = symbol;
            lastRunLength = length;
            nextRun++;
        }


        private S lastSymbol() {
            return alphabet.toSymbol((int) (lastRun & symbolMask));
        }

        private int lastInt() {
            return lastRunSymbol;
        }

        private int lastLength() {
            return lastRunLength;
        }

        private void setRun(final int symbol, final int length) {
            lastRunLength = length;
            lastRunSymbol = symbol;
            lastRun = (symbol & symbolMask) | (length << bitsPerSymbol);
            bits.set(lastRunPosition, lastRun, bitsPerRun);
        }

        private void setLength(final int length) {
            lastRunLength = length;
            lastRun = (lastRun & symbolMask) | (length << bitsPerSymbol);
            bits.set(lastRunPosition, lastRun, bitsPerRun);
        }

        private void setSymbol(final int symbol) {
            lastRunSymbol = symbol;
            lastRun = (lastRun & ~symbolMask) | (symbol & symbolMask);
            bits.set(lastRunPosition, lastRun, bitsPerRun);
        }

        public long previous() {
            nextRun--;
            lastRun = arrayIterator.previousLong(bitsPerRun);
            updateLastRunData();
            return lastRun;
        }

        public void set(final int currentSymbol, final int currentLength) {
            lastRunSymbol = currentSymbol & symbolMask;
            lastRunLength = currentLength;
            lastRun = (lastRunSymbol & 0);
            bits.set(lastRunPosition, lastRun, bitsPerRun);
        }
    }

    public void append(final SymbolSequence<S> sequence) {
        if (!this.alphabet.equals(sequence.alphabet()))
            throw new IllegalArgumentException("incompatible alphabet");
        final long sequenceLength = sequence.length();
        final RunIterator runIt = new RunIterator();
        int currentSymbol;
        int currentLength;
        if (sequenceLength > 0) {
            runIt.goToEnd();
            runIt.previous();
            currentSymbol = runIt.lastRunSymbol;
            currentLength = runIt.lastRunLength;
            symbolCounts[currentSymbol] -= currentLength;
            length -= currentLength;
        } else {
            runIt.insert(currentSymbol = sequence.getInt(0), currentLength = 0);
            runIt.next();
        }
        for (int i = 0; i < sequenceLength; i++) {
            final int nextBase = sequence.getInt(i);
            if (nextBase != currentSymbol || currentLength == maximumRunLength) {
                runIt.set(currentSymbol, currentLength);
                symbolCounts[currentSymbol] += currentLength;
                length += currentLength;
                currentSymbol = nextBase;
                currentLength = 1;
            } else {
                currentLength++;
            }
            symbolCounts[currentSymbol] += currentLength;
            length += currentLength;
            runIt.set(currentSymbol, currentLength);
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
    public Alphabet<S> alphabet() {
        return alphabet;
    }

    public long[] getCounts() {
        return symbolCounts.clone();
    }
}
