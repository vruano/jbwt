package jbwt.index.utils;

/**
 * Created by valentin on 1/24/17.
 */
public class BCRSuffixHash<E> {

    private static final float LOAD = 0.75f;
    private static final double INV_LOAD = 1.0 / LOAD;
    private int size;
    private int nextExpandSize;
    private long hashMask;
    private Node<E>[] table;

    @SuppressWarnings("unchecked")
    public BCRSuffixHash(final int initialCapacity) {
        final int actualCapacity = (int) Math.ceil(Math.log(Math.max(32, initialCapacity * 1.0/LOAD)) / Math.log(2));
        table = (Node<E>[]) new Node[initialCapacity];
        hashMask = (1L << actualCapacity) - 1L;
        //size = 0; // implicity;
    }

    public void put(final long key, final E value) {
        final Node<E> node = search(key, true);
        node.value = value;
        if (size == nextExpandSize) expand();
    }

    private void expand() {
        final int newCapacity = table.length << 1;
        final long newHashMask =  newCapacity - 1;
        @SuppressWarnings("unchecked")
        final Node<E>[] newTable = new Node[newCapacity];
        for (int i = 0; i < table.length; i++) {
            Node<E> node = table[i];
            while (node != null) {
                final int newTableIndex = (int) (node.key & newHashMask);
                final Node<E> nextNode = node.next;
                node.next = newTable[newTableIndex];
                newTable[newTableIndex] = node;
                node = nextNode;
            }
        }
        table = newTable;
        hashMask = newHashMask;
        nextExpandSize = (int) Math.ceil(newCapacity * INV_LOAD);
    }

    public E get(final long key, final E value) {
        final Node<E> node = search(key, false);
        return node == null ? null : node.value;
    }

    private Node<E> search(final long key, final boolean create) {
        final int tableIndex = (int) (key & hashMask);
        Node<E> result = table[tableIndex];
        if (result == null) {
            if (create) {
                size++;
                return table[tableIndex] = new Node<>(key, null, null);
            } else
                return null;
        } else
            do {
                if (result.key == key)
                    return result;
                else if (result.next == null) {
                    if (create) {
                        size++;
                        result.next = new Node<>(key, null, null);
                        return result.next;
                    } else
                        return null;
                } else
                    result = result.next;
            } while (true);
    }

    class Node<E> {
        private final long key;
        private E value;
        private Node<E> next;

        public Node(final long key, final E value, final Node<E> next) {
            this.key = key;
            this.value = value;
            this.next = next;
        }
    }
}
