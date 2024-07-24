package gh2;
import edu.princeton.cs.algs4.StdAudio;
import edu.princeton.cs.algs4.StdDraw;

public class GuitarHero {
    private static final String KEYBOARD = "q2we4r5ty7u8i9op-[=zxdcfvgbnjmk,.;/' ";
    public static final double CONCERT = 440.0;

    public static void main(String[] args) {
        GuitarString[] string = new GuitarString[KEYBOARD.length()];

        for (int i = 0; i < KEYBOARD.length(); i++) {
            double frequency = CONCERT * Math.pow(2, (i - 24) / 12.0);
            string[i] = new GuitarString(frequency);
        }

        while (true) {

            if (StdDraw.hasNextKeyTyped()) {
                char key = StdDraw.nextKeyTyped();
                int index = KEYBOARD.indexOf(key);
                if (index > 0) {
                    string[index].pluck();
                }
            }

            double sample = 0;
            for (GuitarString s : string) {
                sample += s.sample();
            }

            StdAudio.play(sample);

            for (GuitarString s : string) {
                s.tic();
            }

        }
    }
}