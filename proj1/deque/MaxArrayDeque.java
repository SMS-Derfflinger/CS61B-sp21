package deque;

import java.util.Comparator;

public class MaxArrayDeque<T> extends ArrayDeque {

    private final Comparator<T> comparator;

    public MaxArrayDeque(Comparator<T> c) {
        comparator = c;
    }

    public T max() {
        return max(comparator);
    }

    public T max(Comparator<T> c) {
        if (isEmpty()) {
            return null;
        }

        int maxIndex = 0;
        for (int i = 0; i < size(); i++) {
            if (c.compare((T) get(i), (T) get(maxIndex)) > 0) {
                maxIndex = i;
            }
        }
        return (T) get(maxIndex);
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof MaxArrayDeque)) {
            return false;
        }
        if (o == this) {
            return true;
        }
        if (((MaxArrayDeque<?>) o).max() != max()) {
            return false;
        }
        return super.equals(o);
    }
}
