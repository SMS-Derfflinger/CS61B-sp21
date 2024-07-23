package deque;

import edu.princeton.cs.algs4.StdRandom;
import org.junit.Test;

import java.util.LinkedList;

import static org.junit.Assert.assertEquals;

public class ArrayDequeTest {
    @Test
    public void randomizedTest() {
        java.util.LinkedList<Integer> L = new LinkedList<>();
        ArrayDeque<Integer> B = new ArrayDeque<>();

        int N = 500000;
        for (int i = 0; i < N; i += 1) {
            int operationNumber = StdRandom.uniform(0, 8);
            if (operationNumber == 0) {
                // addFirst
                int randVal = StdRandom.uniform(0, 100);
                L.addFirst(randVal);
                B.addFirst(randVal);
            } else if (operationNumber == 1) {
                // addLast
                int randVal = StdRandom.uniform(0, 100);
                L.addLast(randVal);
                B.addLast(randVal);
            } else if (operationNumber == 2) {
                // size
                assertEquals(L.size(), B.size());
            } else if (L.isEmpty()) {
                continue;
            } else if (operationNumber == 3) {
                // getFirst
                assertEquals(L.getFirst(), B.getFirst());
            } else if (operationNumber == 4) {
                // getLast
                assertEquals(L.getLast(), B.getLast());
            } else if (operationNumber == 5) {
                // removeFirst
                Integer temp = L.removeFirst();
                assertEquals(temp, B.removeFirst());
            } else if (operationNumber == 6) {
                // removeLast
                Integer temp = L.removeLast();
                assertEquals(temp, B.removeLast());
            } else if (operationNumber == 7) {
                // get
                int randVal = StdRandom.uniform(0, L.size());
                assertEquals(L.get(randVal), B.get(randVal));
            }
        }
    }
}
