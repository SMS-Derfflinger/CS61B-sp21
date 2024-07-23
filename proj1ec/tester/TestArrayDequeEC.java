package tester;

import static org.junit.Assert.*;

import edu.princeton.cs.algs4.StdRandom;
import org.junit.Test;
import student.StudentArrayDeque;

public class TestArrayDequeEC {
    @Test
    public void randomizedTest() {
        ArrayDequeSolution<Integer> L = new ArrayDequeSolution<>();
        StudentArrayDeque<Integer> B = new StudentArrayDeque<>();
        StringBuilder message = new StringBuilder();

        int N = 5000;
        for (int i = 0; i < N; i += 1) {
            int operationNumber = StdRandom.uniform(0, 5);
            if (operationNumber == 0) {
                // addFirst
                int randVal = StdRandom.uniform(0, 100);
                L.addFirst(randVal);
                B.addFirst(randVal);
                message.append("addFirst(").append(randVal).append(")\n");
            } else if (operationNumber == 1) {
                // addLast
                int randVal = StdRandom.uniform(0, 100);
                L.addLast(randVal);
                B.addLast(randVal);
                message.append("addLast(").append(randVal).append(")\n");
            } else if (operationNumber == 2) {
                // size
                int size = L.size();
                assertEquals(size, B.size());
            } else if (L.isEmpty()) {
                continue;
            } else if (operationNumber == 3) {
                // removeFirst
                Integer temp = L.removeFirst();
                message.append("removeFirst()\n");
                assertEquals(String.valueOf(message), temp, B.removeFirst());
            } else if (operationNumber == 4) {
                // removeLast
                Integer temp = L.removeLast();
                message.append("removeLast()\n");
                assertEquals(String.valueOf(message), temp, B.removeLast());
            }
        }
    }
}
