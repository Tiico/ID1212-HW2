package ID1212.HW2.server.controller;

import java.nio.channels.SelectionKey;

public interface ClientIO {
    void receiveMessage(SelectionKey key);
    void sendMessages(SelectionKey key);
    void addObjectToWrite(Object objectToWrite);
    void disconnect();
    int getSessionID();
}
