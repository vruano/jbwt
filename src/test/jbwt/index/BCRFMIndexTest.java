package jbwt.index;

import jbwt.sequences.SymbolSequence;
import jbwt.utils.BaseTest;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by valentin on 1/9/17.
 */
public class BCRFMIndexTest extends BaseTest {

    @Test(dataProvider="bwtData")
    public void testBWT(final String sequence, final String expected) {
        final String[] sequences = sequence.split("\\$");
        final BCRFMIndex<DNASymbol, DNAStringSequence> index = new BCRFMIndex<>(DNASymbol.ALPHABET);
        index.addAll(Stream.of(sequences).map(DNAStringSequence::new).collect(Collectors.toList()));
        final SymbolSequence<DNASymbol> bwt = index.toBWTSequence();
        Assert.assertEquals(bwt.toString(), expected);
    }

    @DataProvider(name="bwtData")
    public Object[][] bwtData() {
        final TestData data = new TestData();
        //data.add("AACCGGTCTA", "AT$AACTCGCG");
        //data.add("ACGTACGTACGT$ACATTACATG", "TGT$TT$CCAAAAATCCCGTGGAA");
        //data.add("GGACTGGCCCCCCCGGCGGCCGCGGGCCGGGAGTCCGGGACGCCGCCCGGGGCATGTGCCCGGCGGCGTGTGCCCCGGCCACCCAGCCGCTCCCCGTGCCCCGCGGGGGCCCGCGCCGCCCGCCCGCCGGACCGCGGGGCCCGCGCCGGCGCGCGGGCTCGGACCGGGGCGGGGCCCCCGGGGCCTGGGCCCGCGCGGGCCTGCCATGGCTTGCCTGGCTCCGCCCGGGGGTGCGGCCAGGCCCCTGCCCGCGCGCCCGGCCGGCCCCCGCCCGCCCCCCGGGACCGGGTGGGCTGCGGGGCGCCGCAGGAGTCCCCGCCCGCCCCGCGGGGCGCCGGGCCCTGCCCCAGCGGGGCGTTCCGTCCGAGCACGGCGGGAG", "GCGGGGCGGGCCCCGGCCCGCCGCCGGCCGGACGGCGGCCGCTGGGCCCTGGGCCGGGGGGCCCGCGCGCGCGCTGCCCCGCGTCCCCCGACCGGCGCGCCCTGGCCACATCGCCGGCCCCCCCCGCCGGACGGGCCCCGCGGCCCCCTCGCGCCCCAGCCGCCGGGCGGCGGCGGCCCCGGGGGCCCGACCGAGGGGGGCGGACGGGTTGCGGCTTGCCCCGGTGCTCCGCCCGACGCGCGTGCGCCGCGCCTGGGCCCGCTCACGGCGGGGCCGG$GAGGCCTCGAGGGGCCGGGGGCCCCGCGTGGTCCCCGGGGTCCCGGGGGCTCCGCCCCCCCCGGCGGACACTTGGCCGCGCGTCCCGGCGTGCCCACGGAGC");
        data.add("GGACTGGCCCCCCCGGCGGCCG$CGGGCCGGGA$GTCCGGGACGCCGCCCGGGGCATG$TGCCCGGCGGCGTGT$GCCCCGGCCACCCAGCCGCTCCCCGTGCC$C$C$GCGGGGGCCCG$CGCCGCCCGCCCGCCGGACCGCGGGGCCCGCGCCGGCGCGCGGGCTCGGAC$CGGGGCGGGGCCCCCGGGGCCTGGGCC$CGCGCGGGCCTGCCA","GAGTCCCGCCAGCGCGGGCCC$$ACCCCGGGGGCAGCCGGCCTGGGGCCGGCCGCGCGCCAGGCGCCGTCCCGGCCCCC$ACGC$GGGCCTCCGCCCGCC$GGCGGC$GCGGGCACCTCGGGGGGTGTGGG$GCCGTCGCCACCGGGCGCCGGCCGC$GCGGCCG$GGCTGGGCGGGCCCGCGCCGTGGGCCGGCCCGCCCCT$CCGCGCAGC$CCG");
        //data.add("GGACTGGCCCCCCCGGCGGCCG$CGGGCCGGGA$GTCCGGGACGCCGCCCGGGGCATG$TGCCCGGCGGCGTGT$GCCCCGGCCACCCAGCCGCTCCCCGTGCC$C$C$GCGGGGGCCCG$CGCCGCCCGCCCGCCGGACCGCGGGGCCCGCGCCGGCGCGCGGGCTCGGAC$CGGGGCGGGGCCCCCGGGGCCTGGGCC$CGCGCGGGCCTGCCA$TGGCTTGCCTGGCTCCGCCCGGGGGTGCGGCCAGGCCCCTGCCCGCGCGCCCGGCCG$GCCCCCGCCCGCCCCCCGGGACCGGGTGGGCTGCGGGGCGCCGCAGGAGTCCCCGCCCGCCCCGCGGGGCGCCGGGCCCTGCCCCAGCGGGGCGTTCCGTCCGAGCACGGCGGGAG","CCGCGCGTCAGAGCGCGGGCGGCCCCGGCC$CA$CCGCCGCGGGGGCCGACGGCGGCCGCTGGCCCTGGGGCCGGGGCCGCGCGCGCGCCGGTGCCCCGCGTCCCACGGCCGCCGCTGCCCATCGCCGGCCCCCCCCCCCGCCG$ACGGGCC$GGGCCCTCCGGCCCCAGCCCGC$GGCGGCGG$GGCCCCGGGGGCCCGACCGCCCTGGGGGGCGGACGTGTGGTGC$GC$GGCCCCGTCTCCGGGCCCACCGCGTGCGCGCGCCTGGGCCGCTCA$GGCGGGGGCCGGGGAGGCCTGAGGGCCGGGGGCCCCGCGTGGTCCCACGTGGGCCCGGGGGCTCCGCCCCCCCCGGCGGTAC$CGGCCGGCGCGTCAGCCC$TGCCC$CGGGC");
        //data.add("GGACTGGCCCCCCCGGCGGCCGCGGGC", "CGGGCCCCGCCGCGAGGGGGC$GTCCCC");
        //data.add("CCCCGGCGGCCGCGGGC", "CG$CGCCGCGGGGCGCCC");
        //data.add("CCCCGGCGGCC", "CCG$CCGCGGCC");

        //data.add("AACCGGTCTA$","AT$AACTCGCG");
        return data.toArray();
    }
}
