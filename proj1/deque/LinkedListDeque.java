package deque;

public class LinkedListDeque<T> implements Deque<T> {
    // two sentinel nodes
    private final InnerNode<T> first;
    private final InnerNode<T> last;
    private int size;

    public LinkedListDeque(){
        first = new InnerNode<>(null);
        last = new InnerNode<>(null);
        first.next = last;
        last.prev = first;
        size = 0;
    }

    @Override
    public void addFirst(T item) {
        InnerNode<T> newNode = new InnerNode<>(item);
        newNode.next = first.next;
        newNode.prev = first;
        first.next.prev = newNode;
        first.next = newNode;
        size++;
    }

    @Override
    public void addLast(T item) {
        InnerNode<T> newNode = new InnerNode<>(item);
        newNode.prev = last.prev;
        newNode.next = last;
        last.prev.next = newNode;
        last.prev = newNode;
        size++;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public void printDeque() {
        InnerNode<T> current = first.next;
        while (current != last) {
            System.out.print(current.item + " ");
            current = current.next;
        }
    }

    private void removeInnerNode(InnerNode<T> removeNode) {
        removeNode.prev.next = removeNode.next;
        removeNode.next.prev = removeNode.prev;
        removeNode.prev = null;
        removeNode.next = null;
        size--;
    }

    @Override
    public T removeFirst() {
        if (first.next == last) {
            return null;
        }
        InnerNode<T> removeNode = first.next;
        T item = removeNode.item;
        removeInnerNode(removeNode);

        return item;
    }

    @Override
    public T removeLast() {
        if (last.prev == first) {
            return null;
        }
        InnerNode<T> removeNode = last.prev;
        T item = removeNode.item;
        removeInnerNode(removeNode);

        return item;
    }

    @Override
    public T get(int index) {
        InnerNode<T> current = first.next;
        while (index != 0 && current != last) {
            current = current.next;
            index--;
        }

        if (current == last) {
            return null;
        }
        return current.item;
    }

    public T getFirst() {
        if (first.next == last) {
            return null;
        }
        return first.next.item;
    }

    public T getLast() {
        if (last.prev == first) {
            return null;
        }
        return last.prev.item;
    }

    public T getRecursive(int index) {
        if (index < 0 || index > size - 1) {
            return null;
        }
        return recursive(index, first.next);
    }

    private T recursive(int index, InnerNode<T> current) {
        if (index == 0) {
            return current.item;
        }
        return recursive(index - 1, current.next);
    }

    public boolean equals(Object o) {
        if (!(o instanceof LinkedListDeque)) {
            return false;
        }
        LinkedListDeque<T> deque = (LinkedListDeque<T>) o;
        for (int i = 0; i < deque.size(); i++) {
            if (deque.get(i) != this.get(i)) {
                return false;
            }
        }
        return true;
    }

    private class InnerNode<T> {
        public T item;
        public InnerNode<T> prev;
        public InnerNode<T> next;
        public InnerNode() {
            item = null;
            prev = null;
            next = null;
        }
        public InnerNode(T t) {
            item = t;
            prev = null;
            next = null;
        }
    }
}
