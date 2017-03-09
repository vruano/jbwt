package jbwt.index;

import com.google.common.base.Strings;
import com.google.inject.internal.Nullability;
import jbwt.common.RLEText;
import jbwt.common.dna.Nucleotide;
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
        final RLEText<Nucleotide> subject = new RLEText<>(Nucleotide.ALPHABET);
        for (final byte b : input.getBytes()) {
            subject.append(Nucleotide.ALPHABET.toSymbol(b));
        }
        Assert.assertEquals(subject.toRLEString(), output);
    }

    @Test(dataProvider="insertData")
    public void testInsert(final String input, final String insert) {
        for (int insertPosition  = 0; insertPosition <= input.length(); insertPosition++) {
            final String expected = input.substring(0, insertPosition)
                    + insert + input.substring(insertPosition);
            final RLEText<Nucleotide> rle = new RLEText<>(Nucleotide.ALPHABET);
            rle.append(new CharSequenceText<>(Nucleotide.ALPHABET, input));
            for (int i = insert.length() - 1; i >= 0; i--)
                rle.insert(insertPosition, Nucleotide.ALPHABET.toSymbol(insert.charAt(i)));
            Assert.assertEquals(rle.toString(), expected);
            Assert.assertEquals(rle.length(), expected.length());
        }
    }

    @Test(dataProvider="insertData")
    public void testAppend(final String input, final String insert) {
        Comparable<String> x;
        Comparable<Object> o;

        final String expected = input + insert;
        final RLEText<Nucleotide> subject = new RLEText<>(Nucleotide.ALPHABET);
        subject.append(new CharSequenceText<>(Nucleotide.ALPHABET, input));
        subject.append(new CharSequenceText<>(Nucleotide.ALPHABET, insert));
        Assert.assertEquals(subject.toString(), expected);
        Assert.assertEquals(subject.length(), expected.length());
    }

    @DataProvider(name="insertData")
    public Object[][] insertData() {
        final List<Object[]> result = new ArrayList<>();
        final List<String> inserts = new ArrayList<>();
        for (final Nucleotide symbol : Nucleotide.values())
            inserts.add(symbol.toString());
        final Random rdn = new Random(13);
        for (int i = 0; i < 50; i++) {
            final int insertLength = rdn.nextInt(10) + 1;
            final boolean mono = rdn.nextBoolean();
            if (mono) {
                inserts.add(Strings.repeat(Nucleotide.values()[rdn.nextInt(Nucleotide.values().length)].toString(), insertLength));
            } else {
                final StringBuilder sb = new StringBuilder(insertLength);
                for (int j = 0; j < insertLength; j++) {
                    sb.append(Nucleotide.values()[rdn.nextInt(Nucleotide.values().length)].toString());
                }
                inserts.add(sb.toString());
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
