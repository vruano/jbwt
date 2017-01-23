package jbwt.index;

import jbwt.sequences.AbstractSymbolSequence;
import jbwt.sequences.SymbolSequence;
import jbwt.utils.ParamUtils;
import scala.Array;

import java.util.NoSuchElementException;

import static sun.management.snmp.jvminstr.JvmThreadInstanceEntryImpl.ThreadStateMap.Byte1.other;

/**
 * Created by valentin on 1/3/17.
 */
public class RLESymbolSequence<S extends Symbol>  extends AbstractSymbolSequence<S> {

    private static final int INITAL_RUNS_SIZE = 128;

    private final int maximumRunLength;
    protected final int bitsPerSymbol;
    protected final int symbolMask;
    protected final int lengthMask;
    protected long length;
    protected int lengthInRuns;

    protected byte[] runs;

    public RLESymbolSequence(final Alphabet<S> alphabet) {
        super(alphabet);
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

    @FunctionalInterface
    interface BinaryIntToObjFunction<R> {
        R apply(final int a, final int b);
    }

    private int search(final long position) {
        if (position < (length >> 1)) {
            return searchFromBeginning(position, (run, __) -> run);
        } else {
            return searchFromEnd(position, (run, __) -> run);
        }
    }

    protected final <R> R searchFromEnd(final long position, final BinaryIntToObjFunction<R> resultComposer) {
        if (length == position)
            return resultComposer.apply(lengthInRuns, 0);
        long remaining = length - position;
        for (int i = lengthInRuns - 1; i > 0; --i) {
            final int iLength = 1 + ((runs[i] & lengthMask) >>> bitsPerSymbol);
            if ((remaining -= iLength) <= 0)
                return resultComposer.apply(i, (int) -remaining);
        }
        return resultComposer.apply(0, 0);
    }

    protected final <R> R searchFromBeginning(final long position, final BinaryIntToObjFunction<R> resultComposer) {
        long remaining = position;
        for (int i = 0; i < lengthInRuns; ++i) {
            final int iLength = 1 + ((runs[i] & lengthMask) >>> bitsPerSymbol);
            if ((remaining -= iLength) < 0)
                return resultComposer.apply(i, iLength + (int) remaining);
        }
        return resultComposer.apply(lengthInRuns, 0);
    }

    @Override
    public int getInt(final long position) {
        ParamUtils.validIndex(position, 0, length);
        final int run = search(position);
        return runs[run] & symbolMask;
    }

    public final void insert(final long position, final S symbol) {
        ParamUtils.requiresNonNull(symbol);
        ParamUtils.requiresBetween(position, 0, position);
        final int symbolInt = symbol.toInt();

        final BinaryIntToObjFunction<Void> insertor = (run, offset) -> {
            insert(run, offset, symbolInt);
            return null;
        };

        if (position < (length >> 1)) {
            searchFromBeginning(position, insertor);
        } else {
            searchFromEnd(position, insertor);
        }
        length++;
    }

    protected void insert(final int run, final int offset, final int symbolInt) {
        if (run >= lengthInRuns) {
            insertBlanks(run, 1);
            runs[run] = (byte) (symbolInt);
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

    public final void append(final S symbol) {
        ParamUtils.requiresNonNull(symbol);
        append(symbol.toInt());
    }

    protected void append(final int symbolInt) {
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

    public final void append(final CharSequence text) {
        ParamUtils.requiresNonNull(text);
        for (int i = 0; i < text.length(); i++)
            append(alphabet.valueOf(text.charAt(i)));
    }

    public final void append(final SymbolSequence<S> sequence) {
        ParamUtils.requiresNonNull(sequence);
        if ((sequence instanceof RLESymbolSequence) && ((RLESymbolSequence) sequence).alphabet == this.alphabet) {
            append((RLESymbolSequence<S>) sequence);
        } else {
            for (final S symbol : sequence) {
                append(symbol);
            }
        }
    }

    protected void append(final RLESymbolSequence<S> other) {
        if (other.length == 0) {
            return;
        } else if (!other.alphabet.equals(other.alphabet)) {
            append((SymbolSequence<S>) other);
        } else if (length == 0) {
            this.runs = other.runs.clone();
            this.length = other.length;
            this.lengthInRuns = other.lengthInRuns;
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




    @FunctionalInterface
    private interface ToStringRunAppender {
            void apply(final StringBuilder builder, final int length, final int symbolCode);
    }

    private String toString(final ToStringRunAppender appender) {
        final StringBuilder builder = new StringBuilder(lengthInRuns << 1);

        if (lengthInRuns == 0) return "";
        int length = 1 + ((runs[0] & lengthMask) >>> bitsPerSymbol);
        int symbol = runs[0] & symbolMask;
        for (int i = 1; i < lengthInRuns; i++) {
            final int nextSymbol = runs[i] & symbolMask;
            final int nextLength = 1 + ((runs[i] & lengthMask) >>> bitsPerSymbol);
            if (nextSymbol != symbol) {
                appender.apply(builder, length, symbol);
                length = nextLength;
                symbol = nextSymbol;
            } else {
                length += nextLength;
            }
        }
        appender.apply(builder, length, symbol);
        return builder.toString();


    }

    @Override
    public String toString() {
        return toString((b, l, s) -> {
            final String str = alphabet.toSymbol(s).toString();
            for (int i = 0; i < l; i++) {
                b.append(str);
            }
        });
    }

    public String toRLEString() {
        return toString((b, l, s) -> {
            if (l != 1)
                b.append(l);
            b.append(alphabet.toSymbol(s).toString());
        });
    }

    interface Run<S extends Symbol> {
        S symbol();
        int symbolCode();
        long length();
    }


    public Iterator<S> iterator(final long position) {

        if (position < length >> 1)
            return searchFromBeginning(position, (run, offset) -> new RLEIterator(position, run, offset));
        else
            return searchFromEnd(position, (run, offset) -> new RLEIterator(position, run, offset));
    }


    private class RLEIterator implements Iterator<S> {

        private long nextPosition;
        private int currentRunIndex;
        private int currentRunLength;
        private S currentRunSymbol;
        private int nextOffsetInRun;

        public RLEIterator(final long position, final int run, final int offset) {
            nextPosition = position;
            currentRunIndex = run;
            currentRunSymbol = alphabet.toSymbol(runs[run] & symbolMask);
            currentRunLength = 1 + ((runs[run] & lengthMask) >>> bitsPerSymbol);
            nextOffsetInRun = offset;
        }

        @Override
        public boolean hasNext() {
            return nextPosition < length;
        }

        @Override
        public S next() {
            if (nextPosition >= length())
                throw new NoSuchElementException();
            if (currentRunLength == nextOffsetInRun) {
                currentRunIndex++;
                currentRunSymbol = alphabet.toSymbol(runs[currentRunIndex] & symbolMask);
                currentRunLength = 1 + ((runs[currentRunIndex] & lengthMask) >>> bitsPerSymbol);
                nextOffsetInRun = 0;
            }
            nextPosition++;
            nextOffsetInRun++;
            return currentRunSymbol;
        }

        @Override
        public boolean hasPrevious() {
            return nextPosition > 0;
        }

        @Override
        public S previous() {
            if (nextPosition <= 0)
                throw new NoSuchElementException();
            if (nextOffsetInRun == 0) {
                currentRunIndex--;
                currentRunSymbol = alphabet.toSymbol(runs[currentRunIndex] & symbolMask);
                currentRunLength = 1 + ((runs[currentRunIndex] & lengthMask) >>> bitsPerSymbol);
                nextOffsetInRun = currentRunLength;
            }
            nextPosition--;
            nextOffsetInRun--;
            return currentRunSymbol;
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
}
