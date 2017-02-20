package jbwt.index.utils;

import java.util.Arrays;

/**
 * Created by valentin on 1/23/17.
 */
public class BCRSuffixArray<E> {

    public static final int INITIAL_CAPACITY = 8;

    private long[] positions;
    private E[] elements;
    private int size;

    public BCRSuffixArray() {
        this(INITIAL_CAPACITY);
    }

    @SuppressWarnings("unchecked")
    public BCRSuffixArray(final int initialCapacity) {
        final int actualInitialCapacity = Math.max(initialCapacity, 8);
        positions = new long[actualInitialCapacity];
        elements = (E[]) new Object[actualInitialCapacity];
        // present = null; // implicit.
        // size = 0; // implicit.
    }

    public int size() {
        return size;
    }

    public E get(final long key) {
        final int index = Arrays.binarySearch(positions, key);
        return index >= 0 ? elements[index] : null;
    }

    public void insert(final long key, final E value) {
        final int index = Arrays.binarySearch(positions, key);
        final int insertionPoint = index >= 0 ? index : -index -1;
        if (positions.length == size) {
            final int newLength = positions.length << 1;
            increaseCapacity(newLength);
        }
        for (int i = size - 1; i >= insertionPoint; --i)
            positions[i + 1] = positions[i] + 1;
        System.arraycopy(elements, insertionPoint, elements, insertionPoint + 1, size - insertionPoint);
        positions[insertionPoint] = key;
        elements[insertionPoint] = value;
        size++;
    }

    private void increaseCapacity(int newLength) {
        positions = Arrays.copyOf(positions, newLength);
        elements = Arrays.copyOf(elements, newLength);
    }

    /**
     * Update the positions from a particular position insertion point to
     * reflect a "padding" or insertion of elements without an annotation.
     * @param key
     * @param pad
     */
    public void insert(final long key, final long pad) {
        final int index = Arrays.binarySearch(positions, key);
        final int insertionPoint = index >= 0 ? index : -index - 1;
        for (int i = insertionPoint; i < size; i++) {
            positions[i] += pad;
        }
    }

    /**
     * Splits the content of the array in two, bottom (lower positions) half is kept in this
     * array whereas the top half is returned in a new array object.
     * @return
     */
    public BCRSuffixArray<E> splitTop(final long position) {
        final int index = Arrays.binarySearch(positions, 0, size, position);
        final int newSize = index >= 0 ? index : -index -1;
        final int resultSize = size - newSize;
        final BCRSuffixArray<E> result = new BCRSuffixArray<E>(size);
        for (int i = 0; i < resultSize; i++)
            result.positions[i] = this.positions[i + newSize] - position;
        System.arraycopy(elements, newSize, result.elements, 0, resultSize);
        result.size = resultSize;
        this.size  = newSize;
        return result;
    }

    public void append(final long position, final BCRSuffixArray<E> suffixArray) {
        if (size != 0 && position <= positions[size - 1])
            throw new IllegalArgumentException("appending position is not at the end of this suffix");

        for (int i = 0; i < suffixArray.size; i++)
            positions[size++] = suffixArray.positions[i] - position;
        System.arraycopy(suffixArray.elements, 0, elements, size, suffixArray.size);
        size += suffixArray.size;
    }
}
