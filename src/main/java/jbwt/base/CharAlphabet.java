package jbwt.base;

import jbwt.base.Alphabet;

/**
 * Common interface for those alphabets with symbols
 * that can be represented using a character. For example
 * The DNA alphabet is composed of 4 possible nucleotide
 * that in turn are represented with four letter 'a', 'c', 'g' and 't'.
 */
public interface CharAlphabet<S> extends Alphabet<S> {

    int toCode(final char c);

    S toSymbol(final char c);

    char toChar(final int code);

    char toChar(final S symbol);

    default <T extends Text<S>> String toString(final T text) {
        if (!text.alphabet().equals(this))
            throw new IllegalArgumentException("cannot translate a text written in a different alphabet");
        if (text.length() > Integer.MAX_VALUE)
            throw new UnsupportedOperationException("the input text is too long");
        final StringBuilder builder = new StringBuilder((int) text.length());
        for (int i = 0; i < text.length(); i++)
            builder.append(toChar(i));
        return builder.toString();
    }
}
