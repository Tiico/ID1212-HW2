package ID1212.HW2.shared;

import java.io.Serializable;

/**
 * A class containing the public information of a hangman game at a point in time.
 */
public class GameInfo implements Serializable {

    private GameState gameState;
    private char[] wordProgress;
    private int remainingAttempts;
    private int score;
    private String secretWord;

    /**
     * Creates a public knowledge snapshot of the hangman game.
     * @param wordProgress The word-guessing progress.
     * @param remainingAttempts The remaining guess attempts.
     * @param score The session score.
     * @param word The actual word, revealed
     * @param gs The game-state, such as Ongoing, Won or Lost.
     */
    public GameInfo(char[] wordProgress, int remainingAttempts, int score, String word, GameState gs) {
        this.secretWord = word;
        this.wordProgress = wordProgress;
        this.remainingAttempts = remainingAttempts;
        this.score = score;
        this.gameState = gs;
    }

    public char[] getWordProgress() {
        return this.wordProgress;
    }

    public int getRemainingAttempts() {
        return remainingAttempts;
    }

    public int getScore() {
        return score;
    }

    public String getSecretWord() {
        return secretWord;
    }

    public GameState getGameState() {
        return gameState;
    }

    @Override
    public String toString() {
        return String.format("Word: %s | Remaining attempts: %d | Score: %d",
                this.wordProgress, this.remainingAttempts, this.score);
    }
}
