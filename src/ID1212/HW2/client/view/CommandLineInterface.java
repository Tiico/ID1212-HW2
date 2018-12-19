package ID1212.HW2.client.view;

import ID1212.HW2.client.controller.CommandController;
import ID1212.HW2.shared.*;

import java.io.IOException;
import java.util.Scanner;

/**
 * This class takes input and output represented as a command line.
 */

public class CommandLineInterface implements Runnable, GameView {

    private Scanner in;
    private CommandController commandController;
    private volatile boolean running;

    private final String PROMPT = ">> ";

    /**
     * Creates the command line interface with the specified commandController.
     * @param commandController the view controller
     */

    public CommandLineInterface(CommandController commandController) {
        this.commandController = commandController;
        this.commandController.setGameView(this);
        this.running = true;
        this.in = new Scanner(System.in);
    }

    /**
     * Will continuously run and collect what the user writes and sends the input to the controller.
     */

    @Override
    public void run() {
        String input;
        while(running) {
            System.out.print(PROMPT);
            input = this.in.nextLine();
            try {
                commandController.handleInput(input);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Writes what happens to the ID1212.HW2.client screen.
     * @param gameActionFeedback
     */

    @Override
    public void displayGameFeedback(GameActionFeedback gameActionFeedback) {
        switch (gameActionFeedback) {
            case DUPLICATE_GUESS:
                System.out.println("You already guessed this.");
                break;
            case GAME_LOST:
                System.out.println("YOU LOST!");
                break;
            case GAME_WON:
                System.out.println("YOU WON! :D");
                break;
            case GAME_RESTARTED:
                System.out.println("Game restarted.");
                break;
            case GAME_STARTED:
                System.out.println("Game started.");
                break;
            case NO_GAME_STARTED:
                System.out.println("You must start a game before you issue that command.");
                break;
            case INVALID_COMMAND:
                System.out.println("That's not a recognized command.");
                break;
            case HELP:
                String help = "To start a game, use the \"start\" command.\n" +
                        "Guess by entering either \"guess [letter]\" or \"guess [entire word]\"\n" +
                        "To guess one letter, just type it alone\n" +
                        "If you give up on your current word, use \"restart\"\n" +
                        "Or to quit, type \"exit\"";
                System.out.println(help);
                break;
            case GAME_QUIT:
                System.out.println("Bye bye!");
                break;
        }
    }

    /**
     * Writes the progress of the current game to the ID1212.HW2.client screen.
     * @param gameInfo
     */

    @Override
    public void displayGameInfo(GameInfo gameInfo) {
        StringBuilder sb = new StringBuilder();
        char[] wordProgress = gameInfo.getWordProgress();
        for (char c : wordProgress) {
            if (c == '\u0000')
                sb.append("_");
            else
                sb.append(c);
            sb.append(" ");
        }
        sb.trimToSize();
        System.out.printf("Progress: %s | Remaining guesses: %d | Score: %d\n%s",sb.toString(), gameInfo.getRemainingAttempts(),
                gameInfo.getScore(), PROMPT);
        if (gameInfo.getGameState() == GameState.GAME_LOST)
            System.out.printf("The word was: %s\n", gameInfo.getSecretWord());
    }

    /**
     * Writes information regarding the technical aspects whichs does not have anything to do with the actual game.
     * @param string
     */

    @Override
    public void displayTechnicalFeedback(String string) {
        System.out.println(string + "\n" + PROMPT);
    }
}
