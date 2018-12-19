package ID1212.HW2.server.starter;

import ID1212.HW2.server.controller.GameController;
import ID1212.HW2.server.controller.ServerController;
import ID1212.HW2.server.net.ConnectionHandler;
import ID1212.HW2.server.net.GameHandler;

/**
 * Starts up the ID1212.HW2.server.
 */
public class ServerApp {

    private static final int SERVER_PORT = 54321;

    /**
     * Starts the ID1212.HW2.server on specified port.
     * @param args
     */
    public static void main(String[] args) {
        int port = (args.length > 0) ? Integer.parseInt(args[0]) : SERVER_PORT;
        ConnectionHandler connectionHandler = new ConnectionHandler(port, new GameController());
        new ServerController(connectionHandler);
    }

}