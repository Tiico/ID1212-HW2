package ID1212.HW2.server.net;

import ID1212.HW2.server.controller.ClientIO;

public interface GameHandler {
    void parseCommand(String input, ClientIO clientIO);
}
