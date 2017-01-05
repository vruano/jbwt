package jbwt.sequences;

import htsjdk.samtools.SAMSequenceRecord;
import htsjdk.samtools.reference.IndexedFastaSequenceFile;
import htsjdk.samtools.reference.ReferenceSequence;
import jbwt.index.Alphabet;
import jbwt.index.DNASymbol;

import java.util.Objects;

/**
 * Created by valentin on 6/23/16.
 */
public class FastaFileContig implements Contig {

    private static final int CACHE_AHEAD_MIN_SIZE = 1024;

    private ReferenceSequence cache;

    private int cacheStart;

    private final IndexedFastaSequenceFile source;

    private final SAMSequenceRecord record;

    public FastaFileContig(final IndexedFastaSequenceFile source, final SAMSequenceRecord sequenceRecord) {
        this.source = Objects.requireNonNull(source);
        this.record = Objects.requireNonNull(sequenceRecord);
    }

    @Override
    public String name() {
        return record.getSequenceName();
    }

    @Override
    public long length() {
        return record.getSequenceLength();
    }

    @Override
    public DNASymbol getSymbol(final long position) {
        throw new UnsupportedOperationException("not yet implemented");
    }

    @Override
    public int getInt(final long position) {
        throw new UnsupportedOperationException("not yet implemented");
    }

    @Override
    public Alphabet<DNASymbol> alphabet() {
        return DNASymbol.ALPHABET;
    }

    private void ensureCacheContains(final int start, final int length) {
        if (start < 0) {
            throw new IllegalArgumentException("the start cannot be negative");
        } else if (length + start - 1 >= length()) {
            throw new IllegalArgumentException(String.format("the proposed start and length goes beyond the end of the sequence: %d > %d", start + length + 1, length()));
        } else if (cache == null || start < cacheStart || (start + length - 1) > (cacheStart + cache.getBases().length - 1)) {
            cache = source.getSubsequenceAt(record.getSequenceName(), start + 1,
                    Math.min(Math.max(start + length, start + CACHE_AHEAD_MIN_SIZE), record.getSequenceLength()));
            cacheStart = start;
        }
    }

    public void copyBytes(final int start, final byte[] destination, final int destOffset, final int length) {
        ensureCacheContains(start, length);
        System.arraycopy(cache.getBases(), start - cacheStart, destination, destOffset, length);
    }
}
