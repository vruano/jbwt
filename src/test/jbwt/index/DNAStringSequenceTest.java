package jbwt.index;

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
        final DNAStringSequence seq = new DNAStringSequence(text);
        Assert.assertEquals(text.length(), (int) seq.length());
        for (int i = 0; i < text.length(); i++) {
            final Symbol s = seq.getSymbol(i);
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
