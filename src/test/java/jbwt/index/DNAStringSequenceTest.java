package jbwt.index;

import jbwt.common.dna.Nucleotide;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by valentin on 1/2/17.
 */
public class DNAStringSequenceTest {

    @Test(dataProvider = "stringData")
    public void simpleTest(final String text) {
        final CharSequenceText<Nucleotide> seq = new CharSequenceText<>(Nucleotide.ALPHABET, text);
        Assert.assertEquals(text.length(), (int) seq.length());
        for (int i = 0; i < text.length(); i++) {
            final Nucleotide s = seq.symbolAt(i);
            Assert.assertTrue(s.toString().equals("" + Character.toUpperCase(text.charAt(i))));
        }
    }

    @DataProvider(name = "stringData")
    public Object[][] stringData() {
        final List<Object[]> result = new ArrayList<>();
        result.add(new Object[] {""});
        result.add(new Object[] {"ACGTNRABANT"});
        return result.toArray(new Object[result.size()][]);
    }
}
