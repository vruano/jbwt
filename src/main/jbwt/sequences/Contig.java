package jbwt.sequences;

import jbwt.index.DNASymbol;

/**
 * Created by valentin on 6/23/16.
 */
public interface Contig extends SymbolSequence<DNASymbol> {

    String name();
}
