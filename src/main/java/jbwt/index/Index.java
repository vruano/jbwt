package jbwt.index;

import jbwt.base.Text;

import java.util.List;

/**
 * Common interface for symbol sequence indexes.
 */
public interface Index<A, S extends Text<A>> {

    List<Location<S>> locate(final Text<A> query);

    default long count(final Text<A> query) {
        return locate(query).size();
    }

    class Location<S extends Text<?>> {

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
