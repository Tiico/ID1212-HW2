package ID1212.HW2.client.starter;

import ID1212.HW2.client.controller.CommandController;
import ID1212.HW2.client.net.ServerHandler;
import ID1212.HW2.client.view.CommandLineInterface;

import java.io.IOException;

/**
 * This class contains the main method, which will start the whole program.
 */

public class ClientApp {

    /**
     * Creates the clients controller CommandController and starts the whole program.
     * @param args
     */

    public static void main(String[] args) throws IOException {
        String host = args.length > 0 ? args[0] : "localhost";
        int port = args.length > 1 ? Integer.parseInt(args[1]) : 54321;

        ServerHandler serverHandler = new ServerHandler(port, host);
        CommandController commandController = null;

        commandController = new CommandController(serverHandler);

        new Thread(new CommandLineInterface(commandController)).start();
    }

}
