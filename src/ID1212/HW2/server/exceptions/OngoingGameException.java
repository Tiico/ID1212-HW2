package ID1212.HW2.server.exceptions;

/**
 * Thrown if a user prompts to start a game, but there is already one ongoing.
 */
public class OngoingGameException extends Throwable {
    public OngoingGameException(String s) {
        super(s);
    }
}
