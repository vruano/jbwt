package jbwt.utils;

/**
 * Created by valentin on 2/20/17.
 */
public interface BigArrayOfLongs {

    long length();

    long get(final long i);

    void set(final long i, final long v);

    static BigArrayOfLongs newInstance(final long length) {
        ParamUtils.requiresNonNegative(length);
        if (length <= Integer.MAX_VALUE)
            return new BigArrayOfLongsUsingArray((int) length);
        else
            return new BigArrayOfLongsUsingMatrix(length);
    }
}

final class BigArrayOfLongsUsingArray implements BigArrayOfLongs {

    private final long[] values;
    private final long length;

    BigArrayOfLongsUsingArray(final int length) {
        values = new long[length];
        this.length = values.length;
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
}

class BigArrayOfLongsUsingMatrix implements BigArrayOfLongs {

    private final long[][] values;
    private final int columnBits;
    private final int columnMask;
    final long length;

    public BigArrayOfLongsUsingMatrix(final long size) {
        if (size <= 1)
            throw new IllegalArgumentException("empty or one element array is not supported, use other approaches to handle such arrays");
        length = size;
        columnBits = (Long.SIZE - Long.numberOfLeadingZeros((long) Math.ceil(Math.sqrt(size)) - 1));
        columnMask = (1 << columnBits) - 1;
        final int columns = 1 << columnBits;
        final int rows = (int) (Math.ceil(size / (double) columns));
        values = new long[rows][columns];
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
        final int row = (int)(i >>> columnBits);
        final int column = (int)(i & columnMask);
        values[row][column] = v;
    }
}