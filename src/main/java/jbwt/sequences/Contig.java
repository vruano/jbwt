package jbwt.sequences;


import jbwt.common.dna.Nucleotide;
import jbwt.base.Text;

/**
 * Created by valentin on 6/23/16.
 */
public interface Contig extends Text<Nucleotide> {

    String name();
}
