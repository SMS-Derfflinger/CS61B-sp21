package bstmap;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class BSTMap<K extends Comparable<K>, V> implements Map61B<K, V> {
    private InnerNode root;
    private int size;
    @Override
    public void clear() {
        root = null;
        size = 0;
    }

    @Override
    public boolean containsKey(K key) {
        return contain(root, key);
    }

    private boolean contain(InnerNode node, K findKey) {
        if (node == null) {
            return false;
        }
        int compare = findKey.compareTo(node.key);
        if (compare > 0) {
            return contain(node.right, findKey);
        } else if (compare < 0) {
            return contain(node.left, findKey);
        }
        return true;
    }

    @Override
    public V get(K key) {
        return getRecursion(root, key);
    }

    private V getRecursion(InnerNode node, K findKey) {
        if (node == null) {
            return null;
        }
        int compare = findKey.compareTo(node.key);
        if (compare > 0) {
            return getRecursion(node.right, findKey);
        } else if (compare < 0) {
            return getRecursion(node.left, findKey);
        }
        return node.value;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public void put(K key, V value) {
        InnerNode newNode = new InnerNode(key, value);
        root = putRecursion(root, newNode);
        size++;
    }

    private InnerNode putRecursion(InnerNode node, InnerNode newNode) {
        if (node == null) {
            return newNode;
        }
        int compare = newNode.key.compareTo(node.key);
        if (compare < 0) {
            node.left = putRecursion(node.left, newNode);
        } else if (compare > 0) {
            node.right = putRecursion(node.right, newNode);
        }
        return node;
    }

    @Override
    public Set<K> keySet() {
        Set<K> keySet = new HashSet<>();
        addKeySet(root, keySet);
        return keySet;
    }

    private void addKeySet(InnerNode node, Set<K> keySet) {
        if (node == null) {
            return;
        }
        keySet.add(node.key);
        addKeySet(node.left, keySet);
        addKeySet(node.right, keySet);
    }

    @Override
    public V remove(K key) {
        if (containsKey(key)) {
            V value = get(key);
            root = removeRecursion(root, key);
            size--;
            return value;
        }
        return null;
    }

    private InnerNode removeRecursion(InnerNode node, K removeKey) {
        if (node == null) {
            return null;
        }
        int compare = removeKey.compareTo(node.key);
        if (compare < 0) {
            node.left = removeRecursion(node.left, removeKey);
        } else if (compare > 0) {
            node.right = removeRecursion(node.right, removeKey);
        }

        if (node.left == null) {
            return node.right;
        }
        if (node.right == null) {
            return node.left;
        }

        InnerNode leftMaxNode = getLeftMaxNode(node.left);
        leftMaxNode.right = node.right;
        leftMaxNode.left = removeRecursion(node, leftMaxNode.key);
        return leftMaxNode;
    }

    private InnerNode getLeftMaxNode(InnerNode node) {
        if (node.right == null) {
            return node;
        }
        return getLeftMaxNode(node.right);
    }

    @Override
    public V remove(K key, V value) {
        if (containsKey(key)) {
            V removeValue = get(key);
            if (removeValue.equals(value)) {
                root = removeRecursion(root, key);
                size--;
                return removeValue;
            }
        }
        return null;
    }

    @Override
    public Iterator<K> iterator() {
        return keySet().iterator();
    }

    public void printInOrder() {
        printLnOrderRecursion(root);
    }

    private void printLnOrderRecursion(InnerNode node) {
        if (node == null) {
            return;
        }
        printLnOrderRecursion(node.left);
        System.out.println("key: " + node.key.toString() + ", value: " + node.value.toString());
        printLnOrderRecursion(node.right);
    }

    private class InnerNode {
        public K key;
        public V value;
        public InnerNode left;
        public InnerNode right;

        InnerNode(K key, V value) {
            this.key = key;
            this.value = value;
        }
    }
}
