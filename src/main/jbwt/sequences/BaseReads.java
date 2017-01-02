package jbwt.sequences;

import jbwt.utils.ParamUtils;
import htsjdk.samtools.SAMRecord;

/**
 * Created by valentin on 12/25/16.
 */
public final class BaseReads {

    private BaseReads() {
    }

    private static BaseSequence<SAMRecord> fromRead(final SAMRecord read) {
        return new BaseSequence<SAMRecord>() {
            @Override
            public SAMRecord source() {
                return read;
            }

            @Override
            public int sourceOffset() {
                return 0;
            }

            @Override
            public int length() {
                return read.getReadLength();
            }

            @Override
            public byte getByte(final int position) {
                ParamUtils.validIndex(position, 0, read.getReadLength());
                return read.getReadBases()[position];
            }
        };
    }
}
