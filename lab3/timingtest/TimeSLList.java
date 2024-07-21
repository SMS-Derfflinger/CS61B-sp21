package timingtest;
import edu.princeton.cs.algs4.Stopwatch;

/**
 * Created by hug.
 */
public class TimeSLList {
    private static void printTimingTable(AList<Integer> Ns, AList<Double> times, AList<Integer> opCounts) {
        System.out.printf("%12s %12s %12s %12s\n", "N", "time (s)", "# ops", "microsec/op");
        System.out.printf("------------------------------------------------------------\n");
        for (int i = 0; i < Ns.size(); i += 1) {
            int N = Ns.get(i);
            double time = times.get(i);
            int opCount = opCounts.get(i);
            double timePerOp = time / opCount * 1e6;
            System.out.printf("%12d %12.2f %12d %12.2f\n", N, time, opCount, timePerOp);
        }
    }

    public static void main(String[] args) {
        timeGetLast();
    }

    public static void timeGetLast() {
        // TODO: YOUR CODE HERE
        AList<Integer> Ns = new AList<Integer>();
        Ns.addLast(1000);
        Ns.addLast(2000);
        Ns.addLast(4000);
        Ns.addLast(8000);
        Ns.addLast(16000);
        Ns.addLast(32000);
        Ns.addLast(64000);
        Ns.addLast(128000);

        int M = 10000;
        AList<Integer> ops = new AList<Integer>();
        AList<Double> times = new AList<Double>();
        for (int i = 0; i < Ns.size(); i++) {
            SLList<Integer> temp = new SLList<Integer>();
            for (int j = 0; j < Ns.get(i); j++) {
                temp.addLast(j);
            }

            Stopwatch sw = new Stopwatch();
            for (int j = 0; j < M; j++) {
                temp.getLast();
            }
            double timeInSeconds = sw.elapsedTime();
            ops.addLast(M);
            times.addLast(timeInSeconds);
        }
        printTimingTable(Ns, times, ops);
    }

}
