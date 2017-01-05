package jbwt.index;

import jbwt.sequences.SymbolSequence;
import jbwt.utils.ParamUtils;

import java.util.function.IntBinaryOperator;

/**
 * Created by valentin on 1/3/17.
 */
public final class RLESymbolSequence<S extends Symbol>  implements SymbolSequence<S> {

    private static final int INITAL_RUNS_SIZE = 128;

    private final int maximumRunLength;
    private final int bitsPerSymbol;
    private final int symbolMask;
    private final int lengthMask;
    private long length;
    private int lengthInRuns;

    private final Alphabet<S> alphabet;

    private byte[] runs;

    public RLESymbolSequence(final Alphabet<S> alphabet) {
        this.alphabet = ParamUtils.requiresNonNull(alphabet);
        bitsPerSymbol = alphabet.bitsPerSymbol();
        if (bitsPerSymbol >= Byte.SIZE - 1)
            throw new IllegalArgumentException("this alphabet has too many symbols to male a RL encoding worthwhile");
        final int bitsPerLength = Byte.SIZE - bitsPerSymbol;
        maximumRunLength = 1 << bitsPerLength;
        symbolMask = (1 << bitsPerSymbol) - 1;
        lengthMask = ((1 << Byte.SIZE) - 1) ^ symbolMask;
        runs = new byte[INITAL_RUNS_SIZE];
        length = 0;
        lengthInRuns = 0;
    }

    @Override
    public long length() {
        return length;
    }

    @Override
    public S getSymbol(final long position) {
        ParamUtils.validIndex(position, 0, length);
        final int run = search(position);
        return alphabet.toSymbol(runs[run] & symbolMask);
    }

    private int search(final long position) {
        if (position < (length >> 1)) {
            return searchFromBeginning(position, (run, __) -> run);
        } else {
            return searchFromEnd(position, (run, __) -> run);
        }
    }

    private int searchFromEnd(final long position, final IntBinaryOperator resultComposer) {
        if (length == position)
            return resultComposer.applyAsInt(lengthInRuns, 0);
        long remaining = length - position;
        for (int i = lengthInRuns - 1; i > 0; --i) {
            final int iLength = 1 + ((runs[i] & lengthMask) >>> bitsPerSymbol);
            if ((remaining -= iLength) <= 0)
                return resultComposer.applyAsInt(i, (int) -remaining);
        }
        return resultComposer.applyAsInt(0, 0);
    }

    private int searchFromBeginning(final long position, final IntBinaryOperator resultComposer) {
        long remaining = position;
        for (int i = 0; i < lengthInRuns; ++i) {
            final int iLength = 1 + ((runs[i] & lengthMask) >>> bitsPerSymbol);
            if ((remaining -= iLength) < 0)
                return resultComposer.applyAsInt(i, iLength - (int) remaining);
        }
        return resultComposer.applyAsInt(lengthInRuns, 0);
    }

    @Override
    public int getInt(final long position) {
        ParamUtils.validIndex(position, 0, length);
        final int run = search(position);
        return runs[run] & symbolMask;
    }

    @Override
    public Alphabet<S> alphabet() {
        return alphabet;
    }

    public void insert(final long position, final S symbol) {
        ParamUtils.requiresNonNull(symbol);
        ParamUtils.requiresBetween(position, 0, position);
        final int symbolInt = symbol.toInt();

        final IntBinaryOperator insertor = (run, offset) -> {
            insert(run, offset, symbolInt);
            return 0; // value is ignored.
        };

        if (position < (length >> 1)) {
            searchFromBeginning(position, insertor);
        } else {
            searchFromEnd(position, insertor);
        }
        length++;
    }

    private void insert(final int run, final int offset, final int symbolInt) {
        if (run >= lengthInRuns) {
            insertBlanks(run, 1);
            runs[run] = (byte) (symbolInt | (1 << bitsPerSymbol));
            return;
        }
        final int runSymbol = runs[run] & symbolMask;
        final int runLength = 1 + ((runs[run] & lengthMask) >>> bitsPerSymbol);
        if (runSymbol == symbolInt && runLength < maximumRunLength) {
            runs[run] = (byte) (runSymbol | (runLength  << bitsPerSymbol));
        } else if ((runLength == maximumRunLength || offset == 0) && run > 0 && (runs[run - 1] & symbolMask) == symbolInt && ((runs[run - 1] & lengthMask) >>> bitsPerSymbol) < maximumRunLength - 1) {
            runs[run - 1] = (byte) (symbolInt | ((runs[run - 1] >>> bitsPerSymbol) + 1) << bitsPerSymbol);
        } else if ((runLength == maximumRunLength || offset == runLength) && run < lengthInRuns - 1 && (runs[run + 1] & symbolMask) == symbolInt && ((runs[run + 1] & lengthMask) >>> bitsPerSymbol) < maximumRunLength - 1) {
            // offset == runLength may never happen now.
            runs[run + 1] = (byte) (symbolInt | (((runs[run + 1] & lengthMask) >>> bitsPerSymbol) + 1) << bitsPerSymbol);
        } else if (runSymbol == symbolInt) {
            // runLength (must be) == maximumRunLength.
            final int newLength = maximumRunLength >> 1;
            final int oldLength = maximumRunLength - newLength;
            runs[run] = (byte) (symbolInt | ((oldLength - 1) << bitsPerSymbol));
            insertBlanks(run, 1);
            runs[run] = (byte) (symbolInt | ((newLength - 1) << bitsPerSymbol));
        } else if (offset == 0) { // runSymbol != symbolInt
            insertBlanks(run, 1);
            runs[run] = (byte) symbolInt;
        } else if (offset == runLength) {
            // This may never happen now.
            insertBlanks(run + 1, 1);
            runs[run + 1] = (byte) symbolInt;
        } else {
            insertBlanks(run + 1, 2);
            runs[run] = (byte) (runSymbol | (offset - 1 << bitsPerSymbol));
            runs[run + 1] = (byte) symbolInt;
            runs[run + 2] = (byte) (runSymbol | ((runLength - offset - 1) << bitsPerSymbol));
        }
    }

    private void insertBlanks(final int run, final int count) {
        if (lengthInRuns + count >= runs.length) { // need to expand runs to increase capacity.
            final byte[] newRuns = new byte[runs.length << 1];
            System.arraycopy(runs, 0, newRuns, 0, run);
            System.arraycopy(runs, run, newRuns, run + count, lengthInRuns - run);
            runs = newRuns;
        } else {
            System.arraycopy(runs, run, runs, run + count, lengthInRuns - run);
        }
        lengthInRuns += count;
    }

    public void append(final S symbol) {
        ParamUtils.requiresNonNull(symbol);
        final int symbolInt = symbol.toInt();
        if (length == 0) {
            lengthInRuns = 1;
            length = 1;
            runs[0] = (byte) symbolInt;
        } else {
            final int lastRun = lengthInRuns - 1;
            final int lastSymbol = runs[lastRun] & symbolMask;
            final int lastLength = 1 + ((runs[lastRun] & lengthMask) >>> bitsPerSymbol);
            if (lastSymbol == symbolInt) {
                if (lastLength == maximumRunLength) {
                    final int rightLength = maximumRunLength >> 1;
                    final int leftLength = maximumRunLength - rightLength + 1;
                    insertBlanks(lengthInRuns, 1);
                    runs[lastRun] = (byte) (symbolInt | ((leftLength - 1) << bitsPerSymbol));
                    runs[lastRun + 1] = (byte) (symbolInt | ((rightLength - 1) << bitsPerSymbol));
                } else {
                    runs[lastRun] = (byte) (symbolInt | (lastLength << bitsPerSymbol));
                }
            } else {
                insertBlanks(lengthInRuns, 1);
                runs[lastRun + 1] = (byte) symbolInt;
            }
            length++;
        }
    }

    public void append(final CharSequence text) {
        ParamUtils.requiresNonNull(text);
        for (int i = 0; i < text.length(); i++)
            append(alphabet.valueOf(text.charAt(i)));
    }

    public void append(final SymbolSequence<S> sequence) {
        ParamUtils.requiresNonNull(sequence);
        for (final S symbol : sequence) {
            append(symbol);
        }
    }

    public void append(final RLESymbolSequence<S> other) {
        ParamUtils.requiresNonNull(other);
        if (other.length == 0) {
            return;
        } else if (other.bitsPerSymbol != this.bitsPerSymbol) {
            append((SymbolSequence<S>) other);
        } else {
            final int lastRun = lengthInRuns - 1;
            final int lastSymbol = runs[lastRun] & symbolMask;
            final int lastLength = 1 + ((runs[lastRun] & lengthMask) >>> bitsPerSymbol);
            final int otherFirstSymbol = other.runs[0] & symbolMask;
            final int otherFirstLength = 1 + ((other.runs[0] & lengthMask) >>> bitsPerSymbol);
            if (lastSymbol != otherFirstSymbol || lastLength + otherFirstLength >= maximumRunLength) {
                insertBlanks(lengthInRuns, other.lengthInRuns);
                System.arraycopy(other.runs, 0, this.runs, lastRun + 1, other.lengthInRuns);
            } else {
                insertBlanks(lengthInRuns, other.lengthInRuns - 1);
                runs[lastRun] = (byte) (lastSymbol | ((lastLength + otherFirstLength - 1) << bitsPerSymbol));
                System.arraycopy(other.runs, 1, this.runs, lastRun + 1, other.lengthInRuns - 1);
            }
            length += other.length;
        }
    }

    public String toRLEString() {
        final StringBuilder builder = new StringBuilder(lengthInRuns << 1);
        if (lengthInRuns == 0) return "";
        int length = 1 + ((runs[0] & lengthMask) >>> bitsPerSymbol);
        int symbol = runs[0] & symbolMask;
        for (int i = 1; i < lengthInRuns; i++) {
            final int nextSymbol = runs[i] & symbolMask;
            final int nextLength = 1 + ((runs[i] & lengthMask) >>> bitsPerSymbol);
            if (nextSymbol != symbol) {
                builder.append(length == 1 ? "" : length).append(alphabet.toSymbol(symbol));
                length = nextLength;
                symbol = nextSymbol;
            } else {
                length += nextLength;
            }
        }
        builder.append(length == 1 ? "" : length).append(alphabet.toSymbol(symbol));
        return builder.toString();
    }

    public String toString() {
        final StringBuilder builder = new StringBuilder(lengthInRuns << 1);

        if (lengthInRuns == 0) return "";
        int length = 1 + ((runs[0] & lengthMask) >>> bitsPerSymbol);
        int symbol = runs[0] & symbolMask;
        for (int i = 1; i < lengthInRuns; i++) {
            final int nextSymbol = runs[i] & symbolMask;
            final int nextLength = 1 + ((runs[i] & lengthMask) >>> bitsPerSymbol);
            if (nextSymbol != symbol) {
                toStringAppend(builder, length, symbol);
                length = nextLength;
                symbol = nextSymbol;
            } else {
                length += nextLength;
            }
        }
        toStringAppend(builder, length, symbol);
        return builder.toString();
    }

    private void toStringAppend(StringBuilder builder, int length, int symbol) {
        final String c = alphabet.toSymbol(symbol).toString();
        for (int j = 0; j < length; j++)
            builder.append(c);
        builder.append(length == 1 ? "" : length).append(alphabet.toSymbol(symbol));
    }


}