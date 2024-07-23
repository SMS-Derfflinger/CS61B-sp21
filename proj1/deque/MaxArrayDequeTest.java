package deque;

import org.junit.Test;
import java.util.Comparator;

import static java.lang.Math.min;
import static org.junit.Assert.assertEquals;

public class MaxArrayDequeTest {
    @Test
    public void maxTest() {
        MaxArrayDeque<Integer> B = new MaxArrayDeque<>(new IntComparator());

        for (int i = 0; i < 5; i++) {
            B.addLast(i);
        }
        assertEquals((Integer) 4, B.max());
    }

    @Test
    public void comparatorTest() {
        MaxArrayDeque<String> B = new MaxArrayDeque<>(new StringComparator());

        B.addLast("1111");
        B.addLast("111");

        assertEquals("1111", B.max());
        assertEquals("1111", B.max(new StringComparator()));
    }

    private static class IntComparator implements Comparator<Integer> {
        @Override
        public int compare(Integer a, Integer b) {
            return a - b;
        }
    }

    private static class StringComparator implements Comparator<String> {
        @Override
        public int compare(String a, String b) {
            for (int i = 0; i < min(a.length(), b.length()); i++) {
                char aChar = a.charAt(i);
                char bChar = b.charAt(i);
                if (aChar != bChar) {
                    return aChar - bChar;
                }
            }

            return a.length() - b.length();
        }
    }
}
