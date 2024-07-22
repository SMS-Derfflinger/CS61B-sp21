package deque;

public class ArrayDeque<T> implements Deque<T> {
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
            if (first + 1 + i < items.length) {
                a[i] = items[first + 1 + i];
            } else {
                a[i] = items[first + 1 + i - items.length];
            }
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

    }

    public T getFirst() {
        if (first == items.length - 1) {
            return items[0];
        }
        return items[first + 1];
    }

    @Override
    public T removeFirst() {
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

    public T getLast() {
        if (last == 0) {
            return items[items.length - 1];
        }
        return items[last - 1];
    }

    @Override
    public T removeLast() {
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
}
