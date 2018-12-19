package ID1212.HW2.server.controller;

import ID1212.HW2.server.net.ConnectionHandler;

import java.io.IOException;

/**
 * Creates a Game Controller to orchestrate the game and a connection handler to accept ID1212.HW2.client connections.
 */
public class ServerController {

    public ServerController(ConnectionHandler connectionHandler) {
        // Create the game controller for the application
        try {
            connectionHandler.startServer();
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("ServerController: Something went wrong with starting the server");
        }
    }

}
