package deque;

import java.util.Iterator;

public class ArrayDeque<T> implements Deque<T>, Iterable<T> {
    private final int INITIAL_SIZE = 8;
    private T[] items;
    private int first;
    private int last;
    private int size;

    public ArrayDeque() {
        items = (T[]) new Object[INITIAL_SIZE];
        first = INITIAL_SIZE / 2 - 1;
        last = INITIAL_SIZE / 2;
        size = 0;
    }

    private void resize(int capacity) {
        T[] a = (T[]) new Object[capacity];
        for (int i = 0; i < size; i++) {
            a[i] = get(i);
        }
        items = a;
        first = items.length - 1;
        if (size == items.length) {
            last = 0;
        } else {
            last = size;
        }
    }

    @Override
    public void addFirst(T item) {
        if (size == items.length) {
            resize(size * 2);
        }

        items[first] = item;
        if (first == 0) {
            first = items.length - 1;
        } else {
            first--;
        }
        size++;
    }

    @Override
    public void addLast(T item) {
        if (size == items.length) {
            resize(size * 2);
        }

        items[last] = item;
        if (last == items.length - 1) {
            last = 0;
        } else {
            last++;
        }
        size++;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public void printDeque() {
        for (int i = 0; i < size; i++) {
            System.out.print(get(i) + " ");
        }
    }

    private T getFirst() {
        return get(0);
    }

    @Override
    public T removeFirst() {
        if (size == 0) {
            return null;
        }

        if ((size < items.length / 4) && (size > 4)) {
            resize(size);
        }

        T removeItem = getFirst();
        if (first == items.length - 1) {
            first = 0;
        } else {
            first++;
        }
        size--;
        return removeItem;
    }

    private T getLast() {
        return get(size - 1);
    }

    @Override
    public T removeLast() {
        if (size == 0) {
            return null;
        }

        if ((size < items.length / 4) && (size > 4)) {
            resize(size);
        }

        T removeItem = getLast();
        if (last == 0) {
            last = items.length - 1;
        } else {
            last--;
        }
        size--;
        return removeItem;
    }

    @Override
    public T get(int index) {
        if (index > size) {
            return null;
        }
        if (first + index + 1 < items.length) {
            return items[first + index + 1];
        } else {
            return items[first + index + 1 - items.length];
        }
    }

    public boolean equals(Object o) {
        if (!(o instanceof ArrayDeque)) {
            return false;
        }
        ArrayDeque<T> deque = (ArrayDeque<T>) o;
        for (int i = 0; i < deque.size(); i++) {
            if (deque.get(i) != this.get(i)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public Iterator<T> iterator() {
        return new DequeIterator();
    }

    private class DequeIterator implements Iterator<T> {
        private int index;

        DequeIterator() {
            index = 0;
        }

        @Override
        public boolean hasNext() {
            return index < size;
        }

        @Override
        public T next() {
            if (hasNext()) {
                T item = get(index);
                index++;
                return item;
            }
            return null;
        }
    }
}
