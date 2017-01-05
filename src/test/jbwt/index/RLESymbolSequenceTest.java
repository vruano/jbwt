package jbwt.index;

import com.google.common.base.Strings;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by valentin on 1/3/17.
 */
public class RLESymbolSequenceTest {

    @Test(dataProvider="inoutStringData")
    public void testToRLEString(final String input, final String output) {
        final RLESymbolSequence<DNASymbol> subject = new RLESymbolSequence<>(DNASymbol.ALPHABET);
        for (final byte b : input.getBytes()) {
            subject.append(DNASymbol.valueOf(b));
        }
        Assert.assertEquals(subject.toRLEString(), output);
    }

    @DataProvider(name="inoutStringData")
    public Object[][] inoutStringData() {
        final List<Object[]> result = new ArrayList<>();
        result.add(new Object[] { "ATAGGATCATATTTAGGGGGGAATAA", "ATA2GATCATA3TA6G2AT2A"});
        result.add(new Object[] { "CAAACCCTAGGGTTG", "C3A3CTA3G2TG"});
        result.add(new Object[] { Strings.repeat("A", 200), "200A" });
        result.add(new Object[] { "", ""});
        result.add(new Object[] { });
        return result.toArray(new Object[result.size()][]);
    }
}
