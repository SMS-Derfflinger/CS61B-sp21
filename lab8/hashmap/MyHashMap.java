package hashmap;

import java.util.*;


/**
 *  A hash table-backed Map implementation. Provides amortized constant time
 *  access to elements via get(), remove(), and put() in the best case.
 *
 *  Assumes null keys will never be inserted, and does not resize down upon remove().
 *  @author SMS-Derfflinger
 */
public class MyHashMap<K, V> implements Map61B<K, V> {

    /**
     * Protected helper class to store key/value pairs
     * The protected qualifier allows subclass access
     */
    protected class Node {
        K key;
        V value;

        Node(K k, V v) {
            key = k;
            value = v;
        }
    }

    /* Instance Variables */
    private Collection<Node>[] buckets;
    // You should probably define some more!
    private static final int INITIAL_SIZE = 16;
    private static final double LOAD_FACTOR = 0.75;
    private final double loadFactor;
    private int size = 0;

    /** Constructors */
    public MyHashMap() {
        this(INITIAL_SIZE, LOAD_FACTOR);
    }

    public MyHashMap(int initialSize) {
        this(initialSize, LOAD_FACTOR);
    }

    /**
     * MyHashMap constructor that creates a backing array of initialSize.
     * The load factor (# items / # buckets) should always be <= loadFactor
     *
     * @param initialSize initial size of backing array
     * @param maxLoad maximum load factor
     */
    public MyHashMap(int initialSize, double maxLoad) {
        buckets = createTable(initialSize);
        loadFactor = maxLoad;
    }

    /**
     * Returns a new node to be placed in a hash table bucket
     */
    private Node createNode(K key, V value) {
        return new Node(key, value);
    }

    /**
     * Returns a data structure to be a hash table bucket
     *
     * The only requirements of a hash table bucket are that we can:
     *  1. Insert items (`add` method)
     *  2. Remove items (`remove` method)
     *  3. Iterate through items (`iterator` method)
     *
     * Each of these methods is supported by java.util.Collection,
     * Most data structures in Java inherit from Collection, so we
     * can use almost any data structure as our buckets.
     *
     * Override this method to use different data structures as
     * the underlying bucket type
     *
     * BE SURE TO CALL THIS FACTORY METHOD INSTEAD OF CREATING YOUR
     * OWN BUCKET DATA STRUCTURES WITH THE NEW OPERATOR!
     */
    protected Collection<Node> createBucket() {
        return new LinkedList<>();
    }

    /**
     * Returns a table to back our hash table. As per the comment
     * above, this table can be an array of Collection objects
     *
     * BE SURE TO CALL THIS FACTORY METHOD WHEN CREATING A TABLE SO
     * THAT ALL BUCKET TYPES ARE OF JAVA.UTIL.COLLECTION
     *
     * @param tableSize the size of the table to create
     */
    private Collection<Node>[] createTable(int tableSize) {
        Collection<Node>[] table = new Collection[tableSize];
        for (int i = 0; i < tableSize; i++) {
            table[i] = createBucket();
        }
        return table;
    }

    // TODO: Implement the methods of the Map61B Interface below
    // Your code won't compile until you do so!
    @Override
    public void clear() {
        buckets = createTable(INITIAL_SIZE);
        size = 0;
    }

    @Override
    public boolean containsKey(K key) {
        int index = getIndex(key, this.buckets);
        Node targetNode = getNode(key, index);
        return targetNode != null;
    }

    private int getIndex(K key, Collection<Node>[] buckets) {
        int code = key.hashCode();
        return Math.floorMod(code, buckets.length);
    }

    private Node getNode(K key, int index) {
        for (Node node : buckets[index]) {
            if (node.key.equals(key)) {
                return node;
            }
        }
        return null;
    }

    @Override
    public V get(K key) {
        int index = getIndex(key, this.buckets);
        Node targetNode = getNode(key, index);
        if (targetNode != null) {
            return targetNode.value;
        }
        return null;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public void put(K key, V value) {
        int index = getIndex(key, this.buckets);
        Node targetNode = getNode(key, index);
        if (targetNode != null) {
            targetNode.value = value;
            return;
        }

        Node newNode = createNode(key, value);
        buckets[index].add(newNode);
        size++;
        if (isOverLoad()) {
            resize(buckets.length * 2);
        }
    }

    private boolean isOverLoad() {
        double factor = (double) size / buckets.length;
        return factor > loadFactor;
    }

    private void resize(int newSize) {
        Collection<Node>[] newBuckets = createTable(newSize);
        Iterator<Node> NodeIterator = new NodeIterator();
        while(NodeIterator.hasNext()) {
            Node node = NodeIterator.next();
            int index = getIndex(node.key, newBuckets);
            newBuckets[index].add(node);
        }
        buckets = newBuckets;
    }

    @Override
    public Set<K> keySet() {
        Set<K> keySet = new HashSet<>();
        Iterator<K> MapIterator = new MapIterator();
        while (MapIterator.hasNext()) {
            keySet.add(MapIterator.next());
        }
        return keySet;
    }

    @Override
    public V remove(K key) {
        int index = getIndex(key, this.buckets);
        Node targetNode = getNode(key, index);
        if (targetNode == null) {
            return null;
        }
        return removeNode(index, targetNode);
    }

    @Override
    public V remove(K key, V value) {
        int index = getIndex(key, this.buckets);
        Node targetNode = getNode(key, index);
        if (targetNode == null || targetNode.value != value) {
            return null;
        }
        return removeNode(index, targetNode);
    }

    private V removeNode(int index, Node node) {
        buckets[index].remove(node);
        size--;
        return node.value;
    }

    @Override
    public Iterator<K> iterator() {
        return new MapIterator();
    }

    private class MapIterator implements Iterator<K> {
        private final Iterator<Node> nodeIterator;

        @Override
        public boolean hasNext() {
            return nodeIterator.hasNext();
        }

        @Override
        public K next() {
            return nodeIterator.next().key;
        }

        MapIterator() {
            nodeIterator = new NodeIterator();
        }
    }

    private class NodeIterator implements Iterator<Node> {
        private final Iterator<Collection<Node>> bucketsIterator;
        private Iterator<Node> bucketIterator;
        private int remainingNode;
        @Override
        public boolean hasNext() {
            return remainingNode > 0;
        }

        @Override
        public Node next() {
            if (bucketIterator == null || !bucketIterator.hasNext()) {
                Collection<Node> nextBucket = bucketsIterator.next();
                while (nextBucket.isEmpty()) {
                    nextBucket = bucketsIterator.next();
                }
                bucketIterator = nextBucket.iterator();
            }
            remainingNode--;
            return bucketIterator.next();
        }

        NodeIterator() {
            bucketsIterator = Arrays.stream(buckets).iterator();
            remainingNode = size;
        }
    }
}
