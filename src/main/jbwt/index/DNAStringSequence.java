package jbwt.index;

import jbwt.sequences.AbstractSymbolSequence;
import jbwt.sequences.SymbolSequence;
import jbwt.utils.ParamUtils;

/**
 * Created by valentin on 1/2/17.
 */
public class DNAStringSequence extends AbstractSymbolSequence<DNASymbol>  {

    private final CharSequence text;

    public DNAStringSequence(final CharSequence text) {
        super(DNASymbol.ALPHABET);
        this.text = text;
    }

    @Override
    public long length() {
        return text.length();
    }

    @Override
    public DNASymbol getSymbol(long position) {
        return DNASymbol.valueOf(text.charAt((int) position));
    }

    @Override
    public int getInt(long position) {
        return DNASymbol.valueOf(text.charAt((int) position)).toInt();
    }
}
