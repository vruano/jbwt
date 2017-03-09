package jbwt.base;

import jbwt.base.Text;

/**
 * Symbol iterator over {@link Text} instances.
 */
public interface SymbolIterator<S> extends java.util.Iterator<S> {

    boolean hasPrevious();

    long nextPosition();

    long previousPosition();

    S previous();
}
