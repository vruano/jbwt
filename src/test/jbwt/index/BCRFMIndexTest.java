package jbwt.index;

import jbwt.sequences.SymbolSequence;
import jbwt.utils.BaseTest;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by valentin on 1/9/17.
 */
public class BCRFMIndexTest extends BaseTest {

    @Test(dataProvider="bwtData")
    public void testBWT(final String sequence, final String expected) {
        final DNAStringSequence dnaSequence = new DNAStringSequence(sequence);
        final BCRFMIndex<DNASymbol, DNAStringSequence> index = new BCRFMIndex<>(DNASymbol.ALPHABET);
        index.add(dnaSequence);
        final SymbolSequence<DNASymbol> bwt = index.toBWTSequence();
        Assert.assertEquals(bwt.toString(), expected);
    }

    @DataProvider(name="bwtData")
    public Object[][] bwtData() {
        final TestData data = new TestData();
        data.add("AACCGGTCTA", "AT$AACTCGCG");
        data.add("AACCGGTCTA$","AT$AACTCGCG");
        return data.toArray();
    }
}
