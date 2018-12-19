package ID1212.HW2.server.integration;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Random;

/**
 * A class that is used to read words from a text file.
 */
public class LineReader {

    /**
     * Reads a random word (one word per line) from a text file placed
     * in the res/word.txt
     * @return A random word in the form of a String.
     */
    public static String getRandomLineFromFile() {
        int length = 0;
        List<String> lines = null;
        try {
            lines = Files.readAllLines(Paths.get("res/words.txt"));
            length = lines.size();
        } catch (IOException e) {
            System.out.println("Could not read read word from file.");
            e.printStackTrace();
        }
        return lines.get(new Random().nextInt(length));
    }
}
