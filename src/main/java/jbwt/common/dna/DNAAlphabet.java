package jbwt.common.dna;

import jbwt.base.CharAlphabet;
import jbwt.common.EnumAlphabet;

/**
 * Created by valentin on 3/9/17.
 */
class DNAAlphabet extends EnumAlphabet<Nucleotide> implements CharAlphabet<Nucleotide> {

    DNAAlphabet() {
        super(Nucleotide.class);
    }

    @Override
    public int toCode(final char c) {
        final char u = Character.toUpperCase(c);
        switch (u) {
            case 'A' : return Nucleotide.A.ordinal();
            case 'C' : return Nucleotide.C.ordinal();
            case 'G' : return Nucleotide.G.ordinal();
            case 'T' : return Nucleotide.T.ordinal();
            default:
                return -1;
        }
    }

    @Override
    public Nucleotide toSymbol(final char c) {
        final char u = Character.toUpperCase(c);
        switch (u) {
            case 'A' : return Nucleotide.A;
            case 'C' : return Nucleotide.C;
            case 'G' : return Nucleotide.G;
            case 'T' : return Nucleotide.T;
            default:
                return null;
        }
    }

    @Override
    public char toChar(final int code) {
        return toSymbol(code).name().charAt(0);
    }

    @Override
    public char toChar(final Nucleotide symbol) {
        return symbol.name().charAt(0);
    }

    @Override
    public int toCode(final Nucleotide symbol) {
        return symbol.ordinal();
    }
}
