package ID1212.HW2.shared;

/**
 * Enum that contains all possible game feedbacks for the ID1212.HW2.client to parse.
 */
public enum GameActionFeedback {
    GAME_INFO, GAME_STARTED, GAME_RESTARTED, GAME_QUIT, DUPLICATE_GUESS, GAME_ONGOING, GAME_WON, GAME_LOST,
    NO_GAME_STARTED, INVALID_COMMAND, HELP;

    public String getMessage() {
        return toString();
    }
}
