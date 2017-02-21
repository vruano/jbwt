package jbwt.utils;

/**
 * Created by valentin on 2/20/17.
 */
public interface BigArrayOfInts {

    long length();

    int get(final long i);

    void set(final long i, final int v);

    static BigArrayOfInts newInstance(final long length) {
        ParamUtils.requiresNonNegative(length);
        if (length <= Integer.MAX_VALUE)
            return new BigArrayOfIntsUsingArray((int) length);
        else
            return new BigArrayOfIntsUsingMatrix(length);
    }
}

final class BigArrayOfIntsUsingArray implements BigArrayOfInts {

    private final int[] values;
    private final int length;

    BigArrayOfIntsUsingArray(final int length) {
        values = new int[length];
        this.length = values.length;
    }

    @Override
    public long length() {
        return values.length;
    }

    @Override
    public int get(long i) {
        ParamUtils.validIndex(i, 0, values.length);
        return values[(int) i];
    }

    @Override
    public void set(long i, int v) {
        ParamUtils.validIndex(i, 0, length);
        values[(int) i] = v;
    }
}

final class BigArrayOfIntsUsingMatrix implements BigArrayOfInts {

    private final int[][] values;
    private final int columnBits;
    private final int columnMask;
    private final long length;

    public BigArrayOfIntsUsingMatrix(final long size) {
        if (size <= 1)
            throw new IllegalArgumentException("empty or one element array is not supported, use other approaches to handle such arrays");
        length = size;
        columnBits = (Long.SIZE - Long.numberOfLeadingZeros((long) Math.ceil(Math.sqrt(size)) - 1));
        columnMask = (1 << columnBits) - 1;
        final int columns = 1 << columnBits;
        final int rows = (int) (Math.ceil(size / (double) columns));
        values = new int[rows][columns];
    }

    @Override
    public long length() {
        return length;
    }

    @Override
    public int get(long i) {
        ParamUtils.requiresBetween(i, 0, length - 1);
        final int row = (int)(i >>> columnBits);
        final int column = (int)(i & columnMask);
        return values[row][column];
    }

    @Override
    public void set(long i, int v) {
        ParamUtils.requiresBetween(i, 0, length - 1);
        final int row = (int)(i >>> columnBits);
        final int column = (int)(i & columnMask);
        values[row][column] = v;
    }
}

final class BigArrayOfIntsUsingBitArray extends BitArray implements BigArrayOfInts {

    private final long length;

    public BigArrayOfIntsUsingBitArray(final long size) {
        super(size * Integer.SIZE);
        length = size;
        setLength(size * Integer.SIZE);
    }

    @Override
    public long length() {
        return length;
    }

    @Override
    public int get(long i) {
        return (int) getLong(i << 5, 32);
    }

    @Override
    public void set(long i, int v) {
        set(i << 5, v, 32);
    }
}