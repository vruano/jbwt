package jbwt.index;

import jbwt.sequences.SymbolSequence;
import jbwt.utils.ParamUtils;

import java.util.List;

/**
 * Common interface for symbol sequence indexes.
 */
public interface Index<A extends Symbol, S extends SymbolSequence<A>> {

    List<Location<S>> locate(final SymbolSequence<A> query);

    default long count(final SymbolSequence<A> query) {
        return locate(query).size();
    }

    class Location<S extends SymbolSequence<?>> {

        public final S sequence;

        public final long offset;

        Location(final S sequence, final long offset) {
            this.sequence = sequence;
            this.offset = offset;
        }

        boolean isNotFound() {
            return sequence == null;
        }

        boolean equals(final Location other) {
            return other != null && other.sequence == this.sequence
                    && (this.sequence == null || this.offset == other.offset);
        }
    }

}
