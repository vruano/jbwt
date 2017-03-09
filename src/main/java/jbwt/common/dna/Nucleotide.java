package jbwt.common.dna;

import jbwt.base.CharAlphabet;

/**
 * DNAAlphabet whose symbols can be represented by a single character.
 * <p>
 *     For example an alaphabet for DNA only requires a single character to represent each possible symbol.
 * </p>
 */
public enum Nucleotide  {

   A, C, G, T;

   public static final CharAlphabet<Nucleotide> ALPHABET = new DNAAlphabet();
}

