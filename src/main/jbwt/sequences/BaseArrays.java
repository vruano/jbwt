package jbwt.sequences;

import jbwt.index.Alphabet;
import jbwt.index.DNASymbol;
import jbwt.index.Symbol;
import jbwt.utils.ParamUtils;

/**
 * Created by valentin on 12/25/16.
 */
public final class BaseArrays {

    // prevent instantiation.
    private BaseArrays() {
    }

    private SymbolSequence fromArray(final byte[] bases) {
        return fromArray(bases, 0, bases.length);
    }

    private SymbolSequence fromArray(final byte[] bases, final int offset, final int length) {
        ParamUtils.validIndex(offset, 0, bases.length);
        ParamUtils.validIndex(length, 0, bases.length - offset + 1);
        return new SymbolSequence() {

            @Override
            public long length() {
                return length;
            }

            @Override
            public Symbol getSymbol(final long position) {
                return DNASymbol.valueOf(bases[offset + (int) position]);
            }

            @Override
            public int getInt(long position) {
                return DNASymbol.valueOf(bases[offset + (int) position]).toInt();
            }

            @Override
            public Alphabet getAlphabet() {
                return DNASymbol.ALPHABET;
            }

        };
    }
}
