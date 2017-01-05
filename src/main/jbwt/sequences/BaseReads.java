package jbwt.sequences;

import jbwt.index.Alphabet;
import jbwt.index.DNASymbol;
import jbwt.index.Symbol;
import jbwt.utils.ParamUtils;
import htsjdk.samtools.SAMRecord;

/**
 * Created by valentin on 12/25/16.
 */
public final class BaseReads {

    private BaseReads() {
    }

    private static SymbolSequence fromRead(final SAMRecord read) {
        return new SymbolSequence() {

            @Override
            public long length() {
                return read.getReadLength();
            }

            @Override
            public Symbol getSymbol(long position) {
                return DNASymbol.valueOf(read.getReadBases()[(int) position]);
            }

            @Override
            public int getInt(long position) {
                return DNASymbol.valueOf(read.getReadBases()[(int) position]).toInt();
            }

            @Override
            public Alphabet alphabet() {
                return DNASymbol.ALPHABET;
            }
        };
    }
}
