package ID1212.HW2.server.net;

import ID1212.HW2.server.controller.GameController;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

/**
 * The class for accepting ID1212.HW2.client requests and handling them.
 */
public class ConnectionHandler {

    private static final int LINGER_TIME = 5000;
    private final int port;
    private Selector selector;
    private GameHandler gameHandler;

    /**
     * Creates a ID1212.HW2.server socket that listens for connections and accepts them and then registers them to our selector.
     * @param port The port to listen on.
     * @param gameHandler The <code>GameHandler</code> to be used by the connecting clients.
     */
    public ConnectionHandler(int port, GameHandler gameHandler) {
        // This listens for incoming connections and handles them in a separate thread in the net layer.
        this.gameHandler = gameHandler;
        this.port = port;
    }

    public void startServer() throws IOException {
        InetSocketAddress address = new InetSocketAddress(port);
        ServerSocketChannel channel = ServerSocketChannel.open();
        channel.configureBlocking(false);
        channel.bind(address);

        this.selector = Selector.open();
        channel.register(this.selector, SelectionKey.OP_ACCEPT);

        listener();
    }

    private void listener() throws IOException {
        while (true){
            selector.select();
            Iterator<SelectionKey> keySet = selector.selectedKeys().iterator();
            while(keySet.hasNext()){
                SelectionKey key = keySet.next();
                keySet.remove();
                selector.selectedKeys().remove(key);

                if (!key.isValid()){
                    continue;
                }

                if (key.isAcceptable()){
                    System.out.println("Accepting " + key.channel());

                    acceptClient(key);
                }else if(key.isWritable() || key.isReadable()){
                    startHandler(key);
                }
            }
        }
    }

    private void acceptClient(SelectionKey key) throws IOException {
        ServerSocketChannel server = (ServerSocketChannel) key.channel();
        SocketChannel clientConnection = server.accept();
        clientConnection.configureBlocking(false);
        clientConnection.register(selector, SelectionKey.OP_READ, new ClientHandler(this.selector, clientConnection, this.gameHandler));
    }

    private void startHandler(SelectionKey key) {
        ClientHandler clientHandler = (ClientHandler) key.attachment();
        if (key.isReadable()){
            clientHandler.receiveMessage(key);
        }else if(key.isWritable()){
            clientHandler.sendMessages(key);
        }
    }

}
