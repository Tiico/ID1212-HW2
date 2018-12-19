package ID1212.HW2.server.net;

import ID1212.HW2.server.controller.ClientIO;
import ID1212.HW2.shared.Serializer;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.ArrayDeque;
import java.util.Queue;

/**
 * This is the main class for handling a single persistent connection.
 */
public class ClientHandler implements Runnable, ClientIO {

    private static int session_id = 0;

    private final GameHandler gameHandler;
    private final Queue<Object> sendObjects;
    private final Queue<String> receivedCommands;
    private final int sessionID = session_id++;
    private final Selector globalSelector;
    private final SocketChannel channel;

    /**
     * Creates a ID1212.HW2.client handler that orchestrates the communication network communication with the ID1212.HW2.client against
     * a GameController.
     * @param channel
     * @throws IOException If the communication with the ID1212.HW2.client fails, this exception is thrown.
     */
    public ClientHandler(Selector selector, SocketChannel channel, GameHandler gameHandler) throws IOException {
        this.gameHandler = gameHandler;
        this.globalSelector = selector;
        this.channel = channel;
        sendObjects = new ArrayDeque<>();
        receivedCommands = new ArrayDeque<>();

        System.out.printf("I got a task\n");
    }


    @Override
    public void receiveMessage(SelectionKey key) {
        SocketChannel client = (SocketChannel) key.channel();
        String message = "";
        try {
            message = readFromChannel(client);
        }catch (IOException e){
            key.cancel();
            System.out.println("Client has been disconnected");
            return;
        }catch (ClassNotFoundException e){
            e.printStackTrace();
        }
        receivedCommands.add(message);
        new Thread(this).start();


    }

    private String readFromChannel(SocketChannel client) throws IOException, ClassNotFoundException {
        ByteBuffer content = ByteBuffer.allocate(1024);
        client.read(content);
        return ((String) Serializer.deserialize(content.array())).trim();
    }

    @Override
    public void sendMessages(SelectionKey key) {
        SocketChannel client = (SocketChannel) key.channel();
        Object sendNext;
        while ((sendNext = this.sendObjects.poll()) != null) {
            try {
                writeToChannel(client, sendNext);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (this.sendObjects.size() == 0){
            key.interestOps(SelectionKey.OP_READ);
        }else{
            key.interestOps(SelectionKey.OP_WRITE | key.interestOps());
        }
    }

    private void writeToChannel(SocketChannel client, Object obj) throws IOException {
        ByteBuffer buffer = ByteBuffer.wrap(Serializer.serializeObject(obj));
        while (buffer.hasRemaining()){
            client.write(buffer);
        }
    }

    @Override
    public void addObjectToWrite(Object objectToWrite) {
        this.sendObjects.add(objectToWrite);
        this.channel.keyFor(globalSelector).interestOps(SelectionKey.OP_WRITE | SelectionKey.OP_READ);
        this.globalSelector.wakeup();
    }

    /**
     * Disconnects the ID1212.HW2.client.
     */
    public void disconnect() {
        try {
            channel.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            System.out.printf("Connection to client ended...\n");
        }
    }

    @Override
    public int getSessionID() {
        return this.sessionID;
    }

    /**
     * Continuously reads from the ID1212.HW2.client and passes what's read to the controller.
     */
    @Override
    public void run() {
        gameHandler.parseCommand(this.receivedCommands.poll(), this);
    }
}
