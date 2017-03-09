package jbwt.sequences;

import htsjdk.samtools.SAMSequenceDictionary;
import htsjdk.samtools.SAMSequenceRecord;
import htsjdk.samtools.reference.IndexedFastaSequenceFile;
import jbwt.base.Alphabet;
import jbwt.base.Text;
import jbwt.common.dna.Nucleotide;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Created by valentin on 6/23/16.
 */
public class FastaFileReference implements Reference {

    private final IndexedFastaSequenceFile source;

    private final Map<String, Contig> contigs;

    public FastaFileReference(final IndexedFastaSequenceFile source) {
        this.source = Objects.requireNonNull(source);
        this.contigs = loadContigs(source);
    }

    private Map<String,Contig> loadContigs(final IndexedFastaSequenceFile source) {
        final SAMSequenceDictionary dictionary = source.getSequenceDictionary();
        final Map<String, Contig> result = new LinkedHashMap<>(dictionary.size());
        for (final SAMSequenceRecord sequenceRecord : dictionary.getSequences()) {
            final Contig contig = new FastaFileContig(source, sequenceRecord);
            result.put(contig.name(), contig);
        }
        return result;
    }

    @Override
    public Map<String, Contig> contigs() {
        return contigs;
    }

}
