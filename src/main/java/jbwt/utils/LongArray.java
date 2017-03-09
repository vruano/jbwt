package jbwt.utils;

/**
 * Created by valentin on 2/20/17.
 */
public interface LongArray {

    long length();

    long get(final long i);

    void set(final long i, final long v);

    /**
     * Returns a live view on a section of this array.
     * <p>Changes in the original array in any of the position overlapped by the returned subArray would also be visible through the latter and vice versa.</p>
     * @param from where the sub-array starts (inclusive.
     * @param to where the sub-array ends (exclusive).
     * @return
     */
    LongArray subArray(final long from, final long to);

    static LongArray newInstance(final long length) {
        ParamUtils.requiresNonNegative(length);
        if (length <= Integer.MAX_VALUE)
            return new StandardLongArray((int) length);
        else
            return new MatrixLongArray(length);
    }
}

final class StandardLongArray implements LongArray {

    private final long[] values;
    private final long length;

    StandardLongArray(final int length) {
        values = new long[length];
        this.length = values.length;
    }

    @Override
    public LongArray subArray(final long from, final long to) {
        ParamUtils.requiresNonNegative(from);
        ParamUtils.requiresBetween(to, from, length);
        final long length = to - from;
        return new StandardLongSubArray(length, from);
    }

    @Override
    public long length() {
        return values.length;
    }

    @Override
    public long get(long i) {
        ParamUtils.validIndex(i, 0, values.length);
        return values[(int) i];
    }

    @Override
    public void set(long i, long v) {
        ParamUtils.validIndex(i, 0, length);
        values[(int) i] = v;
    }

    private class StandardLongSubArray implements LongArray {

        private final long length;
        private final long from;

        public StandardLongSubArray(long length, long from) {
            this.length = length;
            this.from = from;
        }

        @Override
        public long length() {
            return length;
        }

        @Override
        public long get(long i) {
            ParamUtils.requiresBetween(i, 0, length - 1);
            return values[(int) (from + i)];
        }

        @Override
        public void set(long i, long v) {
            ParamUtils.requiresBetween(i, 0, length - 1);
            values[(int) (from + i)] = v;
        }

        @Override
        public LongArray subArray(long from, long to) {
            ParamUtils.requiresNonNegative(from);
            ParamUtils.requiresBetween(to, from, length);
            final long length = to - from;
            return new StandardLongSubArray(length, this.from + from);
        }
    }
}

class MatrixLongArray implements LongArray {

    private final long[][] values;
    private final int columnBits;
    private final int columnMask;
    private final int width;
    final long length;

    public MatrixLongArray(final long size) {
        if (size <= 1)
            throw new IllegalArgumentException("empty or one element array is not supported, use other approaches to handle such arrays");
        length = size;
        columnBits = (Long.SIZE - Long.numberOfLeadingZeros((long) Math.ceil(Math.sqrt(size)) - 1));
        columnMask = (1 << columnBits) - 1;
        width = 1 << columnBits;
        final int rows = (int) (Math.ceil(size / (double) width));
        values = new long[rows][width];
    }

    @Override
    public long length() {
        return length;
    }

    @Override
    public long get(long i) {
        ParamUtils.requiresBetween(i, 0, length - 1);
        final int row = (int)(i >>> columnBits);
        final int column = (int)(i & columnMask);
        return values[row][column];
    }

    @Override
    public void set(long i, long v) {
        ParamUtils.requiresBetween(i, 0, length - 1);
        if (i < width) {
            values[0][(int) i] = v;
        } else {
            final int row = (int) (i >> columnBits);
            final int column = (int) (i & columnMask);
            values[row][column] = v;
        }
    }

    @Override
    public LongArray subArray(final long from, final long to) {
        ParamUtils.requiresNonNegative(from);
        ParamUtils.requiresBetween(to, from, length);
        final long length = to - from;
        return new MatrixLongSubArray(length, from);
    }

    private class MatrixLongSubArray implements LongArray {
        private final long length;
        private final long from;
        public MatrixLongSubArray(final long length, final long from) {
            this.from = from;
            this.length = length;
        }

        @Override
        public long length() {
            return length;
        }

        @Override
        public long get(long i) {
            ParamUtils.requiresBetween(i, 0, length - 1);
            final int row = (int)((i + from) >> columnBits);
            final int column = (int)((i + from) & columnMask);
            return values[row][column];
        }

        @Override
        public void set(long i, long v) {
            ParamUtils.requiresBetween(i, 0, length - 1);
            final int row = (int)((i + from) >> columnBits);
            final int column = (int)((i + from) & columnMask);
            values[row][column] = v;
        }

        @Override
        public LongArray subArray(long from, long to) {
            ParamUtils.requiresNonNegative(from);
            ParamUtils.requiresBetween(to, from, length);
            return new MatrixLongSubArray(to - from, this.from + from);
        }
    }
}