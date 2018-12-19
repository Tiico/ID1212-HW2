package ID1212.HW2.server.controller;

import ID1212.HW2.server.exceptions.AlreadyGuessedException;
import ID1212.HW2.server.exceptions.OngoingGameException;
import ID1212.HW2.server.model.Game;
import ID1212.HW2.server.model.GameCommand;
import ID1212.HW2.server.model.ParsedCommand;
import ID1212.HW2.server.net.ClientHandler;
import ID1212.HW2.server.net.GameHandler;
import ID1212.HW2.shared.GameActionFeedback;
import ID1212.HW2.shared.GameInfo;
import ID1212.HW2.shared.GameState;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * This class controls the flow of the game, and what should be sent to the ID1212.HW2.client.
 */
public class GameController implements GameHandler {

    private Map<Integer, Game> games;

    /**
     * Creates a new game controller
     */
    public GameController() {
        this.games = new HashMap<>();
    }

    /**
     * Makes a word or a letter guess in the hangman game.
     * @param guess The word or letter to guess.
     * @return The the <code>GameActionFeedback</code> which indicates the status to the ID1212.HW2.client.
     */
    private GameActionFeedback makeGuess(String guess, int sessionID) {
        Game game;
        synchronized (this) {
            game = this.games.get(sessionID);
        }
        try {
            if (game.getGameState() == GameState.GAME_LOST || game.getGameState() == GameState.GAME_WON)
                return GameActionFeedback.NO_GAME_STARTED;
            else
                game.makeGuess(guess);
        } catch (AlreadyGuessedException e) {
            return GameActionFeedback.DUPLICATE_GUESS;
        }
        if (game.getGameState() == GameState.GAME_LOST) {
            return GameActionFeedback.GAME_LOST;
        } else if (game.getGameState() == GameState.GAME_WON) {
            return GameActionFeedback.GAME_WON;
        } else {
            return GameActionFeedback.GAME_INFO;
        }
    }

    /**
     * Starts a game session for the player.
     * If a game is already ongoing an exception will be thrown
     * @throws OngoingGameException Exception indicating that a game is already ongoing, and no new game will
     * be started.
     */
    private void startGame(int session) throws OngoingGameException {
        synchronized (this) {
            if (this.games.containsKey(session)) {
                if (this.games.get(session).getGameState() == GameState.GAME_ONGOING) {
                    throw new OngoingGameException("Game already in progress");
                } else {
                    this.games.put(session, new Game(this.games.get(session)));
                }
            } else {
                this.games.put(session, new Game());
            }
        }
    }

    /**
     * Restarts the game, which results in -1 points. Or starts a new game if there's no ongoing game.
     */
    private void restartGame(int session) {
        synchronized (this) {
            if (this.games.containsKey(session)) {
                Game oldGame = this.games.get(session);
                if (oldGame.getGameState() == GameState.GAME_ONGOING) {
                    oldGame.concede();
                }
                this.games.put(session, new Game(oldGame));
            } else {
                try {
                    startGame(session);
                } catch (OngoingGameException e) {
                    e.printStackTrace(); // Won't happen though
                }
            }
        }
    }

    /**
     * Returns the game info of the game.
     * @return A <code>GameInfo</code> object that indicates the current state of the game.
     */
    public GameInfo getGameInfo(int session) {
        Game game;
        synchronized (this) {
            game = this.games.get(session);
        }
        return game.getGameInfo();
    }


    public void parseCommand(String command, ClientIO clientIO) {
        ParsedCommand parsedCommand = getParsedCommand(command);
        GameCommand gc = parsedCommand.getGameCommand();
        int session = clientIO.getSessionID();

        // Take action on the command
        switch (gc) {
            case START_GAME:
                try {
                    startGame(session);
                    clientIO.addObjectToWrite(getGameInfo(session));
                } catch (OngoingGameException e) {
                    clientIO.addObjectToWrite(GameActionFeedback.GAME_ONGOING);
                    e.printStackTrace();
                }
                break;
            case RESTART:
                restartGame(session);
                String finalWord;
                clientIO.addObjectToWrite(GameActionFeedback.GAME_RESTARTED);
                clientIO.addObjectToWrite(getGameInfo(session));
                break;
            case MAKE_GUESS:
                GameActionFeedback gaf = makeGuess(parsedCommand.getArguments()[0], session);
                clientIO.addObjectToWrite(getGameInfo(session));
                clientIO.addObjectToWrite(gaf);
                break;
            case FETCH_INFO:
                // The boolean is introduced to avoid deadlocking since getGameInfo uses "this" as lock as well.
                boolean gameOngoing = false;
                synchronized (this) {
                    if (this.games.get(Thread.currentThread().getId()) == null) {
                        clientIO.addObjectToWrite(GameActionFeedback.NO_GAME_STARTED);
                    } else {
                        gameOngoing = true;
                    }
                }
                if (gameOngoing)
                    clientIO.addObjectToWrite(getGameInfo(session));
                break;
            case EXIT:
                System.out.println("Quitting job(thread) #" + session);
                synchronized (this) {
                    this.games.remove(session);
                }
                clientIO.disconnect();
                break;
            case INVALID_COMMAND:
                System.out.println("Invalid command received");
                clientIO.addObjectToWrite(GameActionFeedback.INVALID_COMMAND);
                break;
            default:
                System.out.println("This should never happen, but happened.");
                break;
        }
    }

    /**
     * Parses the a raw String sent to the ID1212.HW2.server and creates a ParsedCommand which may be
     * used for performing the requested actions.
     * @param command The raw string command.
     * @return A ParsedCommand object which includes the Command and any arguments.
     */
    private ParsedCommand getParsedCommand(String command) {
        String[] commandsArray = command.split(" ");
        String[] arguments = null;
        GameCommand gc;
        if (commandsArray.length == 1 && commandsArray[0].length() == 1) {
            gc = GameCommand.MAKE_GUESS;
            arguments = new String[]{commandsArray[0]};
        } else if (commandsArray[0].equalsIgnoreCase("start")) {
            gc = GameCommand.START_GAME;
        } else if (commandsArray[0].equalsIgnoreCase("guess")) {
            gc = GameCommand.MAKE_GUESS;
            arguments = Arrays.copyOfRange(commandsArray, 1, commandsArray.length);
        } else if (commandsArray[0].equalsIgnoreCase("exit")) {
            gc = GameCommand.EXIT;
        } else if (commandsArray[0].equalsIgnoreCase("info")) {
            gc = GameCommand.FETCH_INFO;
        } else if(commandsArray[0].equalsIgnoreCase("restart")) {
            gc = GameCommand.RESTART;
        } else {
            gc = GameCommand.INVALID_COMMAND;
        }
        return new ParsedCommand(gc, arguments);
    }

}
