package jbwt.sequences;

import jbwt.index.DNASymbol;

import java.util.Collections;
import java.util.Map;

/**
 * Created by valentin on 6/23/16.
 */
public interface Reference {

    Map<String, Contig> contigs();

    Reference EMPTY = new Reference() {

        @Override
        public Map<String, Contig> contigs() {
            return Collections.emptyMap();
        }
    };

    default SymbolSequence<DNASymbol> subSequence(final String contigName, final int offset, final int length) {
        if (!contigs().containsKey(contigName)) {
            throw new IllegalArgumentException("unknown contig '" + contigName + "'");
        } else {
            final Contig contig = contigs().get(contigName);
            final long contigLength = contig.length();
            if (offset + length >= contigLength) {
                throw new IllegalArgumentException(String.format("the requested stop position (%d) is beyond the contig end (%d)", offset + length, contigLength));
            } else {
                return contig.subSequence(offset, length);
            }
        }
    }
}
