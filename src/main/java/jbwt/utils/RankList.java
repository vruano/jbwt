package jbwt.utils;

/**
 * A rank-list is a list of long values that are constrained to be between 0 and the length of the list - 1.
 */
public interface RankList {

    long length();

    long get(long i);

    void set(long i, long v);

    static RankList newInstance(final long length) {
        if (length < 0)
            throw new IllegalArgumentException("bad length");
        else if (length == 0)
            return new EmptyRankList();
        else if (length <= Integer.MAX_VALUE)
            return new IntegerRankList((int) length);
        else
            return new LongRankList(length);
    }
}

class EmptyRankList implements RankList {

    @Override
    public long length() {
        return 0;
    }

    @Override
    public long get(long i) {
        throw new IllegalArgumentException("invalid index " + i);
    }

    @Override
    public void set(long i, long v) {
        throw new IllegalArgumentException("invalid index " + i);
    }
}

class IntegerRankList implements RankList {

    private final int[] values;

    IntegerRankList(int length) {
        values = new int[length];
    }

    @Override
    public void set(long i, long v) {
        ParamUtils.validIndex(i, 0, values.length);
        ParamUtils.validIndex(v, 0, values.length);
        values[(int)i] = (int) v;
    }

    @Override
    public long get(long i) {
        ParamUtils.validIndex(i, 0, values.length);
        return values[(int)i];
    }

    @Override
    public long length() {
        return values.length;
    }
}

class LongRankList extends MatrixLongArray implements RankList {

    public LongRankList(long size) {
        super(size);
    }

    public void set(long i, long v) {
        ParamUtils.validIndex(v, 0, length);
        super.set(i, v);
    }
}
