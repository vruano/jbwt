package jbwt.sequences;

import java.util.Collections;
import java.util.Map;

/**
 * Created by valentin on 6/23/16.
 */
public interface Reference<S> {

    S source();

    Map<String, Contig<S>> contigs();

    Reference<String> EMPTY = new Reference() {
        @Override
        public String source() {
            return "NONE";
        }

        @Override
        public Map<String, Contig> contigs() {
            return Collections.emptyMap();
        }
    };

    default BaseSequence<S> subSequence(final String contigName, final int offset, final int length) {
        if (!contigs().containsKey(contigName)) {
            throw new IllegalArgumentException("unknown contig '" + contigName + "'");
        } else {
            final Contig<S> contig = contigs().get(contigName);
            final int contigLength = contig.length();
            if (offset + length >= contigLength) {
                throw new IllegalArgumentException(String.format("the requested stop position (%d) is beyond the contig end (%d)", offset + length, contigLength));
            } else {
                return contig.subSequence(offset, length);
            }
        }
    }
}
