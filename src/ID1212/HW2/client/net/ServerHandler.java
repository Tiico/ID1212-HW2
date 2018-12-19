package ID1212.HW2.client.net;

import ID1212.HW2.client.view.GameView;
import ID1212.HW2.shared.GameActionFeedback;
import ID1212.HW2.shared.GameInfo;
import ID1212.HW2.shared.Serializer;
import com.sun.org.apache.bcel.internal.generic.Select;

import java.io.*;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.ArrayDeque;
import java.util.Iterator;
import java.util.Queue;

/**
 * This class handles all the communication between the ID1212.HW2.client and the ID1212.HW2.server. For example when the ID1212.HW2.client
 * connects and disconnects from the ID1212.HW2.server.
 */

public class ServerHandler {

    private final int port;
    private final String host;
    private Selector selector;
    private Communicator communicator;
    private SocketChannel server;

    private boolean connected = false;

    public ServerHandler(int port, String host){
        this.host = host;
        this.port = port;
    }


    public void connect() throws IOException {
        this.communicator = new Communicator();
        this.server = SocketChannel.open();
        server.configureBlocking(false);
        server.connect(new InetSocketAddress(host, port));

        this.selector = Selector.open();
        server.register(selector, SelectionKey.OP_CONNECT);

        this.connected = true;
        new Thread(this.communicator).start();
    }
    /**
     *  Will be used when the ID1212.HW2.client has typed in a command and then send it to the ID1212.HW2.server.
     * @param msg The command that the user typed in.
     * @throws IOException
     */

    public void sendMessage(String msg) throws IOException {
        this.communicator.addMessageToWrite(msg);
    }

    /**
     * Specifies which view to pass the information received from the ID1212.HW2.server.
     * @param gameView
     */

    public void setGameView(GameView gameView) {
        this.communicator.setGameView(gameView);
    }

    /**
     * A private class which continuously reads data from the ID1212.HW2.server and specifies which view
     * to pass the information.
     */

    private class Communicator implements Runnable {

        private GameView gameView;
        private final Queue<String> writeBuffer;

        Communicator(){
            this.writeBuffer = new ArrayDeque<>();
        }

        @Override
        public void run() {
            try {
                while (connected) {
                    if (this.writeBuffer.size() > 0){
                        server.keyFor(selector).interestOps(SelectionKey.OP_WRITE);
                    }
                    int numSelected = selector.select();
                    if (numSelected == 0){
                        continue;
                    }
                    Iterator<SelectionKey> keys = selector.selectedKeys().iterator();
                    while(keys.hasNext()){
                        SelectionKey key = keys.next();
                        keys.remove();

                        if (!key.isValid()){
                            continue;
                        }
                        if (key.isConnectable()){
                            ((SocketChannel) key.channel()).finishConnect();
                        }else if (key.isReadable()){
                            readFromServer(key);
                        }else if (key.isWritable()){
                            writeToServer(key);
                            key.interestOps(SelectionKey.OP_READ);
                        }
                    }
                }
            } catch (IOException e) {
                gameView.displayTechnicalFeedback("Lost connection to server");
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                gameView.displayTechnicalFeedback("Could not find the class");
                e.printStackTrace();
            }
        }

        private void readFromServer(SelectionKey key) throws IOException, ClassNotFoundException {
            SocketChannel channel = (SocketChannel) key.channel();
            ByteBuffer buffer = ByteBuffer.allocate(1024);
            channel.read(buffer);
            buffer.flip();

            Object receivedObj = Serializer.deserialize(buffer.array());
            if (receivedObj instanceof GameInfo){
                gameView.displayGameInfo((GameInfo) receivedObj);
            }else if (receivedObj instanceof GameActionFeedback){
                gameView.displayGameFeedback((GameActionFeedback) receivedObj);
            }
        }

        private void writeToServer(SelectionKey key) throws IOException {
            SocketChannel channel = (SocketChannel) key.channel();
            Object sendNext;
            while((sendNext = this.writeBuffer.poll()) != null){
                ByteBuffer buffer = ByteBuffer.wrap(Serializer.serializeObject(sendNext));
                while (buffer.hasRemaining()){
                    channel.write(buffer);
                }
            }
        }

        /**
         * Specifies which view to pass the information received from the ID1212.HW2.server.
         * @param gameView
         */

        void setGameView(GameView gameView) {
            this.gameView = gameView;
        }

        void addMessageToWrite(String message){
            synchronized (this.writeBuffer){
                this.writeBuffer.add(message);
            }
            selector.wakeup();
        }
    }

}
