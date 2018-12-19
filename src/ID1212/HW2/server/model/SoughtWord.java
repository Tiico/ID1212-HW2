package ID1212.HW2.server.model;

/**
 * An object representing the sought word in a hangman game.
 */
public class SoughtWord {

    private String word;
    private char[] progress;

    /**
     * Creates a new word for a hangman game, storing the guessing progress as well.
     * @param randomLineFromFile
     */
    public SoughtWord(String randomLineFromFile) {
        this.word = randomLineFromFile;
        progress = new char[word.length()];
    }

    public int length() {
        return word.length();
    }

    /**
     * Checks if a word matches the sought word.
     * @param otherWord The word to test.
     * @return True if the words match, otherwise false.
     */
    public boolean matches(String otherWord) {
        if (this.word.toLowerCase().equalsIgnoreCase(otherWord)) {
            this.progress = this.word.toCharArray();
            return true;
        }
        return false;
    }

    /**
     * Checks if a letter is in the word.
     * @param guess The letter to try.
     * @return Returns true if the letter is found in the word, otherwise false.
     */
    public boolean containsChar(char guess) {
        boolean found = false;
        for (int i = 0; i < this.progress.length; i++) {
            if (Character.toLowerCase(this.word.charAt(i)) == Character.toLowerCase(guess)) {
                this.progress[i] = this.word.charAt(i);
                found = true;
            }
        }
        return found;
    }

    /**
     * Returns the guessing-progress of the word.
     * @return A character array, containing null bytes where a matching letter has not been found.
     */
    public char[] getProgress() {
        return this.progress.clone();
    }

    @Override
    public String toString() {
        return this.word;
    }
}
