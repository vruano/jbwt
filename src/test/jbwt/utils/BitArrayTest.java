package jbwt.utils;

import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Created by valentin on 12/29/16.
 */
public class BitArrayTest {

    @Test
    public void testCreation() {
        final BitArray subject = new BitArray();
    }

    @Test(dataProvider="capacityData")
    public void testCreation(final long capacity) {
        final BitArray subject = new BitArray(capacity);
    }

    @Test(dataProvider="capacityData")
    public void testEnsureCapacity(final long capacity) {
        final BitArray subject = new BitArray(5);
        subject.ensureCapacity(capacity);
    }

    @Test(dataProvider="badCapacityData", expectedExceptions = {IllegalArgumentException.class})
    public void testCreationFailure(final long badCapacity) {
        final BitArray subject = new BitArray(badCapacity);
    }

    @Test(dataProvider="badCapacityData", expectedExceptions = {IllegalArgumentException.class})
    public void testEnsureCapacityFailure(final long badCapacity){
        final BitArray subject= new BitArray(0);
        subject.ensureCapacity(badCapacity);
    }

    @Test(dependsOnMethods = "testCreation")
    public void testInitialLength() {
        final BitArray subject = new BitArray();
        Assert.assertEquals(subject.getLength(), 0);
    }

    @Test(dependsOnMethods = "testCreation", dataProvider = "capacityData")
    public void testInitialLength(final long capacity) {
        final BitArray subject = new BitArray(capacity);
        Assert.assertEquals(subject.getLength(), 0);
    }

    @Test(dependsOnMethods = "testCreation")
    public void testSetLength() {
        final BitArray subject = new BitArray();
        Assert.assertEquals(subject.getLength(), 0);
        subject.setLength(10);
        Assert.assertEquals(subject.getLength(), 10);
        subject.setLength(5);
        Assert.assertEquals(subject.getLength(), 5);
        subject.setLength(1000);
        Assert.assertEquals(subject.getLength(), 1000);
        subject.setLength(0);
        Assert.assertEquals(subject.getLength(), 0);
    }

    @Test(dependsOnMethods = {"testSetLength", "testSetGetBit"})
    public void testSetLengthBitClearing() {
        final BitArray subject = new BitArray(100);
        subject.setLength(10);
        for (int i = 0; i < subject.getLength(); i++) {
            Assert.assertFalse(subject.getBoolean(i));
            subject.set(i, true);
        }
        subject.setLength(5);
        subject.setLength(15);
        for (int i = 0; i < 5; i++) {
            Assert.assertTrue(subject.getBoolean(i));
        }
        for (int i = 5; i < 15; i++) {
            Assert.assertFalse(subject.getBoolean(i));
        }

        subject.setLength(100);
        for (int i = 0; i < subject.getLength(); i++) {
            subject.set(i, true);
        }
        subject.setLength(25);
        subject.setLength(125);
        for (int i = 0; i < 25; i++) {
            Assert.assertTrue(subject.getBoolean(i));
        }
        for (int i = 25; i < 125; i++) {
            Assert.assertFalse(subject.getBoolean(i));
        }

        subject.setLength(1250);
        for (int i = 0; i < subject.getLength(); i++) {
            subject.set(i, true);
        }
        subject.setLength(725);
        subject.setLength(923);
        for (int i = 0; i < 725; i++) {
            Assert.assertTrue(subject.getBoolean(i));
        }
        for (int i = 725; i < 923; i++) {
            Assert.assertFalse(subject.getBoolean(i));
        }
    }

    @Test(dataProvider = "bitSetData", dependsOnMethods = "testCreation")
    public void testSetGetBit(final BitSet template) {
        final BitArray subject = new BitArray();
        subject.setLength(template.length());
        final Random rdn = new Random(11 + template.length());
        final List<Integer> indexes = IntStream.range(0, template.length())
                .boxed().collect(Collectors.toList());
        while (!indexes.isEmpty()) {
            final int index = indexes.remove(rdn.nextInt(indexes.size()));
            subject.set(index, template.get(index));
        }
        for (int index = 0; index < template.length(); index++) {
            Assert.assertEquals(subject.getBoolean(index), template.get(index));
        }
    }

    @Test(dataProvider = "bitSetData", dependsOnMethods = "testCreation")
    public void testSetGetLong(final BitSet template) {
        final BitArray subject = new BitArray();
        subject.setLength(template.length());
        final Random rdn = new Random(11 + template.length());
        final List<Integer> indexes = IntStream.range(0, template.length())
                .boxed().collect(Collectors.toList());
        while (!indexes.isEmpty()) {
            final int index = indexes.remove(rdn.nextInt(indexes.size()));
            final int lsize = template.length() - index < Long.SIZE ? template.length() - index : rdn.nextInt(Long.SIZE) + 1;
            Assert.assertTrue(lsize > 0);
            final long[] ll = template.get(index, index + lsize).toLongArray();
            final long l = ll.length == 0 ? 0L : ll[0];
            subject.set(index, l, lsize);
            final long lrecovered = subject.getLong(index, lsize);
            Assert.assertTrue( (((1L << lsize) - 1L) & l) == (((1L << lsize) - 1L) & lrecovered));
        }
        for (int index = 0; index < template.length(); index++) {
            Assert.assertEquals(subject.getBoolean(index), template.get(index));
        }
    }

    @Test(dataProvider = "bitSetData", dependsOnMethods = "testCreation")
    public void testRemove(final BitSet template) {
        BitArray subject = new BitArray();
        subject.setLength(template.length());
        final Random rdn = new Random(11 + template.length());
        List<Integer> indexes = IntStream.range(0, template.length())
                .boxed().collect(Collectors.toList());
        while (!indexes.isEmpty()) {
            final int index = indexes.remove(rdn.nextInt(indexes.size()));
            subject.set(index, template.get(index));
        }
        indexes = IntStream.range(0, template.length())
                .boxed().collect(Collectors.toList());
        while (!indexes.isEmpty()) {
            final int index = (int) Math.min(subject.getLength(), indexes.remove(rdn.nextInt(indexes.size())));
            final long value = rdn.nextLong();
            final int length = (int) Math.min((rdn.nextInt(3) + 1) *rdn.nextInt(Long.SIZE + 1),subject.getLength() - index);
            final long startComp = Math.max(0, index - 65);
            final long endComp = Math.min(subject.getLength(), index + length + 65);
            final BitSet expected = new BitSet((int) (endComp - startComp));
            for (long i = startComp; i < index; i++) {
                expected.set((int)i, subject.getBoolean(i));
            }

            for (long i = index + length; i < endComp; i++) {
                expected.set((int)i - length, subject.getBoolean(i));
            }
            final long beforeSize = subject.getLength();
            final BitArray clone = subject.clone();
            subject.remove(index, length);
            final long afterSize = subject.getLength();
            Assert.assertEquals(afterSize, beforeSize - length);
            for (int i = (int) startComp; i < Math.min(endComp - length, subject.getLength()); i++) {
                if (subject.getBoolean(i) != expected.get(i)){
                    clone.remove(index, length);
                }
                Assert.assertEquals(subject.getBoolean(i), expected.get(i));
            }
            subject = clone;
        }

    }

    @Test(dataProvider = "bitSetData", dependsOnMethods = "testCreation")
    public void testInsertLong(final BitSet template) {
        final BitArray subject = new BitArray();
        subject.setLength(template.length());
        final Random rdn = new Random(11 + template.length());
        List<Integer> indexes = IntStream.range(0, template.length())
                .boxed().collect(Collectors.toList());
        while (!indexes.isEmpty()) {
            final int index = indexes.remove(rdn.nextInt(indexes.size()));
            subject.set(index, template.get(index));
        }
        indexes = IntStream.range(0, template.length())
                .boxed().collect(Collectors.toList());
        while (!indexes.isEmpty()) {
            final int index = indexes.remove(rdn.nextInt(indexes.size()));
            final long value = rdn.nextLong();
            final int length = rdn.nextInt(Long.SIZE + 1);
            final long startComp = Math.max(0, index - 65);
            final long endComp = Math.min(subject.getLength(), index + length + 65);
            final BitSet expected = new BitSet((int) (endComp - startComp));
            for (long i = startComp; i < index; i++) {
                expected.set((int)i, subject.getBoolean(i));
            }
            for (int i = 0; i < length; i++) {
                expected.set(index + i, ((value >>> i) & 1) != 0);
            }
            for (long i = index + length; i < endComp; i++) {
                expected.set((int)i, subject.getBoolean(i - length));
            }
            final long beforeSize = subject.getLength();
            final BitArray clone = subject.clone();
            subject.insert(index, value, length);
            final long afterSize = subject.getLength();
            Assert.assertEquals(afterSize, beforeSize + length);
            for (int i = (int) startComp; i < endComp; i++) {
                if (subject.getBoolean(i) != expected.get(i)){
                    clone.insert(index, value, length);
                }
                Assert.assertEquals(subject.getBoolean(i), expected.get(i));
            }
        }

    }

    @DataProvider(name="capacityData")
    public Object[][] capacityData() {
        final List<Object[]> result = new ArrayList<>();
        result.add(new Object[] { 0L });
        result.add(new Object[] { 1L });
        result.add(new Object[] { 3L });
        result.add(new Object[] { 100L });
        result.add(new Object[] { 1024L });
        result.add(new Object[] { ((long) Integer.MAX_VALUE) + 1L });
        return result.toArray(new Object[result.size()][]);
    }

    @DataProvider(name="badCapacityData")
    public Object[][] badCapacityData() {
        final List<Object[]> result = new ArrayList<>();
        result.add(new Object[] { -1L });
        result.add(new Object[] { -10L });
        result.add(new Object[] { ((long) Integer.MAX_VALUE) *  100L });
        return result.toArray(new Object[result.size()][]);
    }

    @DataProvider(name="bitSetData")
    public Object[][] bitSetData() {
        final List<Object[]> result = new ArrayList<>();
        final Random rdn = new Random(13);
        for (int b = 0; b < 100; b++) {
            final int length = rdn.nextInt(10000);
            final BitSet bitSet = new BitSet(length);
            for (int i = 0; i < length; i++) {
                bitSet.set(i, rdn.nextBoolean());
            }
            result.add(new Object[]{bitSet});
        }
        return result.toArray(new Object[result.size()][]);
    }

}
