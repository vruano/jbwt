package jbwt.index;

import jbwt.sequences.SymbolSequence;
import jbwt.utils.ParamUtils;

/**
 * Created by valentin on 1/2/17.
 */
public class DNAStringSequence implements SymbolSequence<DNASymbol>  {

    private final CharSequence text;

    public DNAStringSequence(final CharSequence text) {
        this.text = ParamUtils.requiresNonNull(text);
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

    @Override
    public Alphabet<DNASymbol> alphabet() {
        return DNASymbol.ALPHABET;
    }
}
