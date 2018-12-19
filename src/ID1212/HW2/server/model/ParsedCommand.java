package ID1212.HW2.server.model;

/**
 * A object combining the parsed command, and any arguments it might use.
 */
public class ParsedCommand {

    private GameCommand gameCommand;
    private String[] arguments;

    /**
     * Creates a combined objects that holds a command and any arguments it might use.
     * @param gameCommand The command that the ID1212.HW2.server should issue.
     * @param arguments Any arguments the command needs
     */
    public ParsedCommand(GameCommand gameCommand, String[] arguments) {
        this.gameCommand = gameCommand;
        this.arguments = arguments;
    }

    public GameCommand getGameCommand() {
        return gameCommand;
    }

    public String[] getArguments() {
        return arguments;
    }
}
