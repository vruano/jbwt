package jbwt.utils;

/**
 * Created by valentin on 2/6/17.
 */
public class WordArray {

    protected final int wordSize;
    protected long size;

    protected final BitArray bits;

    public static long DEFAULT_CAPACITY = 32;

    public WordArray(final int wordSize) {
        this(ParamUtils.requiresBetween(wordSize, 0, BitArray.BLOCK_SIZE), DEFAULT_CAPACITY);
    }

    public WordArray(final int wordSize, final long initialCapacity) {
        this.bits = new BitArray(ParamUtils.requiresGreaterThanZero(wordSize) * ParamUtils.requiresNonNegative(initialCapacity));
        this.wordSize = wordSize;
        this.size = 0;
    }

    public int wordSize() {
        return wordSize;
    }

    public long size() {
        return size;
    }

    public long getLong(final long position) {
        ParamUtils.validIndex(position, 0, size - 1);
        return bits.getLongUnchecked(position * wordSize, wordSize);
    }

    public void set(final long position, final long value) {
        ParamUtils.validIndex(position, 0, size - 1);
        bits.set(position * wordSize, value, wordSize);
    }

    public void insert(final long position, final long value) {
        ParamUtils.validIndex(position, 0, size);
        bits.insert(position * wordSize, value, wordSize);
    }
}
