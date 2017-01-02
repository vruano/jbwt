package jbwt.sequences;

import jbwt.utils.ParamUtils;

/**
 * Created by valentin on 12/25/16.
 */
public final class BaseArrays {

    // prevent instantiation.
    private BaseArrays() {
    }

    private BaseSequence<byte[]> fromArray(final byte[] bases) {
        return fromArray(bases, 0, bases.length);
    }

    private BaseSequence<byte[]> fromArray(final byte[] bases, final int offset, final int length) {
        ParamUtils.validIndex(offset, 0, bases.length);
        ParamUtils.validIndex(length, 0, bases.length - offset + 1);
        return new BaseSequence<byte[]>() {
            @Override
            public byte[] source() {
                return bases;
            }

            @Override
            public int sourceOffset() {
                return offset;
            }

            @Override
            public int length() {
                return length;
            }

            @Override
            public void setByte(final int position, final byte newValue) {
                final int offsetPosition = offset + position;
                ParamUtils.validIndex(offsetPosition, 0, bases.length);
                bases[offsetPosition] = newValue;
            }

            @Override
            public byte getByte(final int position) {
                ParamUtils.validIndex(position, 0, length);
                return bases[offset + position];
            }
        };
    }
}
