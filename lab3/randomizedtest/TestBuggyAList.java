package randomizedtest;

import edu.princeton.cs.algs4.StdRandom;
import org.junit.Test;

import java.util.Optional;

import static org.junit.Assert.assertEquals;

/**
 * Created by hug.
 */
public class TestBuggyAList {
  // YOUR TESTS HERE
    @Test
    public void testThreeAddThreeRemove() {
        AListNoResizing<Integer> correct = new AListNoResizing<Integer>();
        BuggyAList<Integer> errors = new BuggyAList<Integer>();
        for (int i = 4; i < 7; i++) {
            correct.addLast(i);
            errors.addLast(i);
        }
        assertEquals(correct.removeLast(), errors.removeLast());
        assertEquals(correct.removeLast(), errors.removeLast());
        assertEquals(correct.removeLast(), errors.removeLast());
    }

    @Test
    public void randomizedTest() {
        AListNoResizing<Integer> L = new AListNoResizing<>();
        BuggyAList<Integer> B = new BuggyAList<>();

        int N = 5000;
        for (int i = 0; i < N; i += 1) {
            int operationNumber = StdRandom.uniform(0, 4);
            if (operationNumber == 0) {
                // addLast
                int randVal = StdRandom.uniform(0, 100);
                L.addLast(randVal);
                B.addLast(randVal);
            } else if (operationNumber == 1) {
                // size
                int size = L.size();
                assertEquals(size, B.size());
            } else if (L.size() == 0) {
                continue;
            } else if (operationNumber == 2) {
                // getLast
                assertEquals(L.getLast(), B.getLast());
            } else if (operationNumber == 3) {
                // removeLast
                Integer temp = L.removeLast();
                assertEquals(temp, B.removeLast());
            }
        }
    }
}
