package jbwt.index;

import com.google.common.base.Strings;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

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

    @Test(dataProvider="insertData")
    public void testInsert(final String input, final String insert) {
        for (int insertPosition  = 0; insertPosition <= input.length(); insertPosition++) {
            final String expected = input.substring(0, insertPosition)
                    + insert + input.substring(insertPosition);
            final RLESymbolSequence<DNASymbol> rle = new RLESymbolSequence<>(DNASymbol.ALPHABET);
            rle.append(input);
            for (int i = insert.length() - 1; i >= 0; i--)
                rle.insert(insertPosition, DNASymbol.valueOf(insert.charAt(i)));
            Assert.assertEquals(rle.toString(), expected);
            Assert.assertEquals(rle.length(), expected.length());
        }
    }

    @Test(dataProvider="insertData")
    public void testAppend(final String input, final String insert) {
        final String expected = input + insert;
        final RLESymbolSequence<DNASymbol> subject = new RLESymbolSequence<>(DNASymbol.ALPHABET);
        subject.append(input);
        subject.append(insert);
        Assert.assertEquals(subject.toString(), expected);
        Assert.assertEquals(subject.length(), expected.length());
    }

    @DataProvider(name="insertData")
    public Object[][] insertData() {
        final List<Object[]> result = new ArrayList<>();
        final List<String> inserts = new ArrayList<>();
        for (final DNASymbol symbol : DNASymbol.values())
            inserts.add(symbol.toString());
        final Random rdn = new Random(13);
        for (int i = 0; i < 50; i++) {
            final int insertLength = rdn.nextInt(10) + 1;
            final boolean mono = rdn.nextBoolean();
            if (mono) {
                inserts.add(Strings.repeat(DNASymbol.values()[rdn.nextInt(DNASymbol.values().length)].toString(), insertLength));
            } else {
                final StringBuilder sb = new StringBuilder(insertLength);
                for (int j = 0; j < insertLength; j++) {
                    sb.append(DNASymbol.values()[rdn.nextInt(DNASymbol.values().length)].toString());
                }
            }
        }

        final List<String> sequences = new ArrayList<>();
        sequences.add("");
        sequences.add("ATATGCATAGTTAGCCCATTTAACC$$$$A$CTG$AAATTT");
        for (final String sequence : sequences)
            for (final String insert : inserts)
                result.add(new Object[] { sequence, insert});
        return result.toArray(new Object[result.size()][]);
    }

    @DataProvider(name="inoutStringData")
    public Object[][] inoutStringData() {
        final List<Object[]> result = new ArrayList<>();
        result.add(new Object[] { "ATAGGATCATATTTAGGGGGGAATAA", "ATA2GATCATA3TA6G2AT2A"});
        result.add(new Object[] { "CAAACCCTAGGGTTG", "C3A3CTA3G2TG"});
        result.add(new Object[] { Strings.repeat("A", 200), "200A" });
        result.add(new Object[] { "", ""});
        return result.toArray(new Object[result.size()][]);
    }
}
