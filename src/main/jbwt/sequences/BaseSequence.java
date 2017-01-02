package jbwt.sequences;

import java.util.Objects;

/**
 * Created by valentin on 6/23/16.
 */
public interface BaseSequence<S> {

    S source();

    int sourceOffset();

    int length();

    byte getByte(final int position);

    default void setByte(final int position, final byte newValue) {
        throw new UnsupportedOperationException();
    }

    default BaseSequence<S> subSequence(final int offset, final int length) {
        final S source = source();
        final BaseSequence<S> parent = this;
        final int sourceOffset = sourceOffset() + offset;
        return new BaseSequence<S>() {

            @Override
            public S source() {
                return source;
            }

            @Override
            public int sourceOffset() {
                return sourceOffset;
            }

            @Override
            public int length() {
                return length;
            }

            @Override
            public byte getByte(final int position) {
                return parent.getByte(offset + position);
            }

            @Override
            public void setByte(final int position, final byte value) { parent.setByte(offset + position, value); }
        };
    }

    default void copyBytes(final int start, final byte[] dest, final int destOffset, final int length) {
        Objects.requireNonNull(dest);
        for (int i = 0; i < length; i++) {
            dest[destOffset + i] = getByte(start + i);
        }
    }

    default void setBytes(final int start, final byte[] source, final int sourceOffset, final int length) {
        Objects.requireNonNull(source);
        for (int i = 0; i < length; i++) {
            setByte(start + i, source[sourceOffset + i]);
        }
    }

    default byte[] getBytes(final int start, final int length) {
        final byte[] result = new byte[length];
        copyBytes(start, result, 0, length);
        return result;
    }

    default byte[] getBytes() {
        final byte[] result = new byte[length()];
        copyBytes(0, result, 0, result.length);
        return result;
    }
}
