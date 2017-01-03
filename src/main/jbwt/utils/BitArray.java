package jbwt.utils;

import java.util.Arrays;
import java.util.NoSuchElementException;
import java.util.stream.LongStream;

/**
 * Bit array encoded in a long array.
 *
 * <p>
 *     Each element of the long array corresponds to 64bits.
 * </p>
 */
public class BitArray implements Cloneable {

    public static final int BLOCK_SIZE = Long.SIZE;
    public static final int BLOCK_SIZE_IN_BITS = (int) Math.round(Math.log(BLOCK_SIZE) * MathConstants.INV_LN_2);
    public static final long MAX_LENGTH = ((long) Integer.MAX_VALUE) * ((long) BLOCK_SIZE);

    private static long[] BIT_MASK = LongStream.range(0, BLOCK_SIZE)
            .map(l -> 1L << l).toArray();
    private static long[] LOW_MASK = LongStream.range(0, BLOCK_SIZE + 2)
            .map(l -> l >= BLOCK_SIZE ? -1L : (1L << l) -1L).toArray();
    public static final long BITOFFSET_MASK = LOW_MASK[BLOCK_SIZE_IN_BITS];
    private static long[] HIGH_MASK = LongStream.range(0, BLOCK_SIZE + 1)
            .map(l -> l == BLOCK_SIZE ? 0L : ~((1L << l) -1L)).toArray();

    private static long ALL_ONES = LOW_MASK[BLOCK_SIZE];

    private static long[] EMPTY_BLOCKS = new long[0];

    private static int MAX_INT_DIV_BY_2 = Integer.MAX_VALUE >>  1;

    public long[] blocks;

    private long length;

    public BitArray() {
        this(0);
    }

    public BitArray(final long capacity) {
        blocks = EMPTY_BLOCKS;
        length = 0;
        ensureCapacity(capacity);
    }

    public void set(final long position, final boolean value) {
        ParamUtils.validIndex(position, 0, length);
        final int blockIndex = (int) (position >> BLOCK_SIZE_IN_BITS);
        final int bitOffset = (int) (position & LOW_MASK[BLOCK_SIZE_IN_BITS]);
        if (value)
            blocks[blockIndex] |= BIT_MASK[bitOffset];
        else
            blocks[blockIndex] &= ~BIT_MASK[bitOffset];
    }

    public boolean getBoolean(final long position) {
        ParamUtils.validIndex(position, 0, length);
        final int blockIndex = (int) (position >> BLOCK_SIZE_IN_BITS);
        final int bitOffset = (int) (position & LOW_MASK[BLOCK_SIZE_IN_BITS]);
        return (blocks[blockIndex] & BIT_MASK[bitOffset]) != 0;
    }

    public void setLength(final long newLength) {
        ensureCapacity(newLength);
        if (newLength < length) // proactively clearing bits when reducing size is easier than clearing them it we expand again.
            setRangeUnchecked(newLength, length, false);
        length = newLength;
    }

    private void setRangeUnchecked(final long from, final long to, final boolean value) {
        if (from >= to)
            return;
        final int firstBlockIndex = (int) (from >> BLOCK_SIZE_IN_BITS);
        final int firstBitOffset = (int) (from & LOW_MASK[BLOCK_SIZE_IN_BITS]);
        final int lastBlockIndex = (int) (to >> BLOCK_SIZE_IN_BITS);
        final int lastBitOffset = (int) ((to & LOW_MASK[BLOCK_SIZE_IN_BITS]));
        if (lastBlockIndex - firstBlockIndex >= 2)
            Arrays.fill(blocks, firstBlockIndex + 1, lastBlockIndex, value ? ALL_ONES : 0L);
        if (lastBlockIndex > firstBlockIndex) {
            if (value) {
                blocks[firstBlockIndex] |= HIGH_MASK[firstBitOffset];
                blocks[lastBlockIndex] |= LOW_MASK[lastBitOffset];
            } else {
                blocks[firstBlockIndex] &= LOW_MASK[firstBitOffset];
                blocks[lastBlockIndex] &= HIGH_MASK[lastBitOffset];
            }
        } else {
            if (value) {
                blocks[firstBlockIndex] |= ~(HIGH_MASK[lastBitOffset] | LOW_MASK[firstBitOffset]);
            } else {
                blocks[firstBlockIndex] &= (HIGH_MASK[lastBitOffset] | LOW_MASK[firstBitOffset]);
            }
        }
    }

    public long getLength() {
        return length;
    }

    public void set(final long position, final long value) {
        set(position, value, Long.SIZE);
    }

    public long getLong(final long position) {
        return getLong(position, Long.SIZE);
    }

    public void set(final long position, final long value, final int length) {
        ParamUtils.requiresBetween(length, 0, Long.SIZE);
        ParamUtils.validIndex(position, 0, this.length);
        if (length > 0) { // if length == 0 there is nothing that needs to be done.
            final int blockIndex = (int) (position >> BLOCK_SIZE_IN_BITS);
            final int bitOffset = (byte) (position & BITOFFSET_MASK);
            setGivenBlockIndexAndBitOffset(value, length, blockIndex, bitOffset);
        }
    }

    private void setGivenBlockIndexAndBitOffset(final long value, final int length, final int blockIndex, final int bitOffset) {
        if (length == BLOCK_SIZE && bitOffset == 0)
            blocks[blockIndex] = value;
        else {
            final int overhang = bitOffset + length - BLOCK_SIZE;
            if (overhang <= 0) { // easy all the int falls within the same long.
                blocks[blockIndex] &= LOW_MASK[bitOffset] | HIGH_MASK[bitOffset + length]; // we keep the unaffected bits.
                blocks[blockIndex] |= (LOW_MASK[length] & value) << bitOffset;
            } else { // the bits split between two longs:
                final int blockIndexPlusOne = blockIndex + 1;
                if (blockIndexPlusOne >= blocks.length)
                    throw new IllegalArgumentException();
                blocks[blockIndex] &= LOW_MASK[bitOffset]; // we keep the unaffected bits.
                blocks[blockIndex] |= (LOW_MASK[length - overhang] & value) << bitOffset;
                blocks[blockIndexPlusOne] &= HIGH_MASK[overhang];
                blocks[blockIndexPlusOne] |= ((~(LOW_MASK[length - overhang] | HIGH_MASK[length])) & value) >>> (length - overhang);
            }
        }
    }

    public long getLong(final long position, final int length) {
        ParamUtils.validIndex(position, 0, this.length);
        ParamUtils.requiresBetween(length, 0, BLOCK_SIZE);
        return getLongUnchecked(position, length);
    }

    public long getLongUnchecked(final long position, final int length) {
        final int blockIndex = (int) (position >> BLOCK_SIZE_IN_BITS);
        final int bitOffset = (int) (position & LOW_MASK[BLOCK_SIZE_IN_BITS]);
        final int overhang = bitOffset + length - BLOCK_SIZE;
        if (overhang <= 0) { // easy all the int falls within the same long.
            return (blocks[blockIndex] & ~(LOW_MASK[bitOffset] | HIGH_MASK[bitOffset + length])) >>> bitOffset;
        } else {
            final long lowBits = (blocks[blockIndex] & HIGH_MASK[bitOffset]) >>> bitOffset;
            final long highBits = blocks[blockIndex + 1] & LOW_MASK[overhang + 1];
            return (lowBits + (highBits << (length - overhang)));
        }
    }

    public void remove(final long position, final int length) {
        ParamUtils.requiresBetween(length, 0, this.length);
        ParamUtils.requiresBetween(position, 0, this.length - length);
        if (length == 0) return;
        final int blockIndex = (int) (position >> BLOCK_SIZE_IN_BITS);
        final int bitOffset = (int) (position & BITOFFSET_MASK);
        final int blockGap = (length + BLOCK_SIZE - 1) / BLOCK_SIZE;
        final int lastBlockIndex = (int) ((this.length - 1) >> BLOCK_SIZE_IN_BITS);
        final int lengthModulo = length & (int) BITOFFSET_MASK;
            if (lengthModulo == 0) { // length is a multiple of BITOFFSET_MASK.
                final long firstBlockContent = blocks[blockIndex];
                System.arraycopy(blocks, blockIndex + blockGap, blocks, blockIndex, lastBlockIndex - blockIndex - blockGap + 1);
                if (bitOffset != 0) {
                    blocks[blockIndex] = (firstBlockContent & LOW_MASK[bitOffset]) | (blocks[blockIndex] & HIGH_MASK[bitOffset]);
                }
            } else { // when the length is not exactly BLOCK_SIZE is more difficult:
                final int effectiveBitOffset; // has the first bit in the first block that will contain stuff from the second block.
                final long firstBlockContent; // has the content of the first block that will remain there (up to effectiveBitOffset).
                final int underhang = BLOCK_SIZE - bitOffset - length;
                if (underhang > 0) { // removed region is fully contained in the first block and some of the end
                    // of the block need to be moved over resulting in a different effectiveBitOffset
                    effectiveBitOffset = bitOffset + underhang;
                    final int blockSizeMinusUnderhang = BLOCK_SIZE - underhang;
                    firstBlockContent = (blocks[blockIndex] & LOW_MASK[bitOffset])
                            | ((blocks[blockIndex] & HIGH_MASK[blockSizeMinusUnderhang]) >>> lengthModulo);
                } else {
                    effectiveBitOffset = bitOffset;
                    firstBlockContent = blocks[blockIndex];
                }
                final int blockSizeMinusLength = BLOCK_SIZE - lengthModulo;
                for (int i = blockIndex; i <= lastBlockIndex - blockGap; i++) {
                    blocks[i] = ((blocks[i + blockGap - 1] & HIGH_MASK[lengthModulo]) >>> lengthModulo)
                            | ((blocks[i + blockGap] & LOW_MASK[lengthModulo]) << blockSizeMinusLength);
                }
                blocks[lastBlockIndex - blockGap + 1] =
                        (blocks[lastBlockIndex] & HIGH_MASK[lengthModulo]) >>> lengthModulo;
                blocks[blockIndex] = (firstBlockContent & LOW_MASK[effectiveBitOffset])
                        | (blocks[blockIndex] & HIGH_MASK[effectiveBitOffset]);
            }
        setRangeUnchecked(this.length - length, this.length, false);
        this.length -= length;
    }

    public void insert(final long position, final long value, final int length) {
        ParamUtils.requiresBetween(length, 0, Long.SIZE);
        if (length == 0) return;
        ensureCapacity(length + this.length);
        final int blockIndex = (int) (position >> BLOCK_SIZE_IN_BITS);
        final int bitOffset = (int) (position & LOW_MASK[BLOCK_SIZE_IN_BITS]);
        if (length == BLOCK_SIZE) { // easy-case.
            System.arraycopy(blocks, blockIndex, blocks, blockIndex + 1, blocks.length - blockIndex - 1);
        } else { // not so easy case
            final int lastBlockIndex = (int) (this.length >> BLOCK_SIZE_IN_BITS); // round up int division.
            final int lastBitOffset = (int) ((this.length & LOW_MASK[BLOCK_SIZE_IN_BITS]));
            final int overhang = lastBitOffset + length - BLOCK_SIZE;
            if (overhang > 0) {
                blocks[lastBlockIndex + 1] = (blocks[lastBlockIndex] & HIGH_MASK[BLOCK_SIZE - overhang]) >>> (BLOCK_SIZE - overhang);
            }
            final int blockSizeMinusLength = BLOCK_SIZE - length;
            for (int i = lastBlockIndex; i > blockIndex; --i) {
                blocks[i] <<= length;
                blocks[i] |= (blocks[i - 1] & HIGH_MASK[blockSizeMinusLength]) >>> (blockSizeMinusLength);
            }
            blocks[blockIndex] = (blocks[blockIndex] & LOW_MASK[bitOffset]) | ((blocks[blockIndex] & HIGH_MASK[bitOffset]) << length);
        }
        setGivenBlockIndexAndBitOffset(value, length, blockIndex, bitOffset);
        this.length += length;
    }

    public BitArray clone() {
        try {
            final BitArray result = (BitArray) super.clone();
            result.blocks = blocks.clone();
            return result;
        } catch (CloneNotSupportedException e) {
            throw new UnsupportedOperationException();
        }
    }

    public void ensureCapacity(final long capacity) {
        ParamUtils.requiresBetween(capacity, 0, MAX_LENGTH);
        final int requiredBytes = requiredBlocks(capacity);
        if (requiredBytes > blocks.length) {
            int newByteCount = Math.max(1, blocks.length);
            while (newByteCount < requiredBytes) {
                if (newByteCount > MAX_INT_DIV_BY_2) {
                    newByteCount = Integer.MAX_VALUE;
                    break;
                } else {
                    newByteCount *= 2;
                }
            }
            blocks = Arrays.copyOf(blocks, newByteCount);
        }
    }

    public Iterator iterator() {
        return new Iterator();
    }

    public Iterator iterator(final long position) {
        return new Iterator(position);
    }

    public class Iterator {
        private long nextIndex;

        public Iterator() {
            nextIndex = 0;
        }

        public Iterator(final long position) {
            ParamUtils.requiresBetween(position, 0, BitArray.this.length);
            nextIndex = position;
        }

        public boolean nextBoolean() {
            if (nextIndex >= length)
                throw new NoSuchElementException();
            return BitArray.this.getBoolean(nextIndex++);
        }

        public long nextLong() {
            if (nextIndex >= length - Long.SIZE)
                throw new NoSuchElementException();
            final long  thisIndex = nextIndex;
            nextIndex += Long.SIZE;
            return BitArray.this.getLongUnchecked(thisIndex, Long.SIZE);
        }

        public long nextLong(final int length) {
            if (nextIndex >= BitArray.this.length - length)
                throw new NoSuchElementException();
            final long thisIndex = nextIndex;
            nextIndex += length;
            return BitArray.this.getLong(thisIndex, length);
        }

        public boolean hasNextBoolean() {
            return nextIndex < length;
        }

        public boolean hasNextLong() {
            return nextIndex < length - Long.SIZE + 1;
        }

        public boolean hasNextLong(final int length) {
            ParamUtils.requiresBetween(length, 0, Long.SIZE);
            return nextIndex < BitArray.this.length - length + 1;
        }

        public boolean hasMore() {
            return nextIndex < BitArray.this.length;
        }

        public long previousLong(final int length) {
            ParamUtils.requiresBetween(length, 0, Long.SIZE);
            if (nextIndex < length)
                throw new NoSuchElementException();
            nextIndex -= length;
            return BitArray.this.getLongUnchecked(nextIndex, length);
        }

        public void insert(final long value, final int length) {
            ParamUtils.requiresBetween(length, 0, Long.SIZE);
            BitArray.this.insert(nextIndex, value, length);
            nextIndex += length;
        }
    }

    private static int requiredBlocks(final long capacity) {
        return (int) ((capacity - 1L) / BLOCK_SIZE + 1L);
    }

}
