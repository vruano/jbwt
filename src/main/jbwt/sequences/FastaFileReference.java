package jbwt.sequences;

import htsjdk.samtools.SAMSequenceDictionary;
import htsjdk.samtools.SAMSequenceRecord;
import htsjdk.samtools.reference.IndexedFastaSequenceFile;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Created by valentin on 6/23/16.
 */
public class FastaFileReference implements Reference<IndexedFastaSequenceFile> {

    private final IndexedFastaSequenceFile source;

    private final Map<String, Contig<IndexedFastaSequenceFile>> contigs;

    public FastaFileReference(final IndexedFastaSequenceFile source) {
        this.source = Objects.requireNonNull(source);
        this.contigs = loadContigs(source);
    }

    private Map<String,Contig<IndexedFastaSequenceFile>> loadContigs(final IndexedFastaSequenceFile source) {
        final SAMSequenceDictionary dictionary = source.getSequenceDictionary();
        final Map<String, Contig<IndexedFastaSequenceFile>> result = new LinkedHashMap<>(dictionary.size());
        for (final SAMSequenceRecord sequenceRecord : dictionary.getSequences()) {
            final Contig<IndexedFastaSequenceFile> contig = new FastaFileContig(source, sequenceRecord);
            result.put(contig.name(), contig);
        }
        return result;
    }

    @Override
    public IndexedFastaSequenceFile source() {
        return source;
    }

    @Override
    public Map<String, Contig<IndexedFastaSequenceFile>> contigs() {
        return contigs;
    }
}
