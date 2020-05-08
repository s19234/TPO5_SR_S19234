/**
 *
 *  @author Szewczyk Ryszard S19234
 *
 */

package zad1;


import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class ChatServer {
    private String host;
    private int port;
    private ServerSocketChannel socketChannel;
    private StringBuilder log;
    private boolean isRunning;
    private Selector selector;
    private SelectionKey selectionKey;
    private Map<SocketChannel, Connection> map;

    public ChatServer(String host, int port) throws IOException {
        this.host = host;
        this.port = port;
        this.socketChannel = ServerSocketChannel.open();
        this.socketChannel.socket().bind(new InetSocketAddress(this.host, this.port));
        this.socketChannel.configureBlocking(false);
        this.selector = Selector.open();
        this.selectionKey = socketChannel.register(selector, SelectionKey.OP_ACCEPT);
        this.log = new StringBuilder();
        this.map = new HashMap<>();
    }

    public void startServer(){
        new Thread(()->{
            isRunning = true;
            while(isRunning){
                try {
                    selector.select();

                    Set<SelectionKey> selectionKeys = selector.selectedKeys();
                    Iterator<SelectionKey> iterator = selectionKeys.iterator();

                    while(iterator.hasNext()){
                        SelectionKey key = iterator.next();
                        iterator.remove();

                        if(key.isAcceptable()){
                            SocketChannel chatClient = socketChannel.accept();
                            chatClient.configureBlocking(false);
                            chatClient.register(selector, SelectionKey.OP_READ | SelectionKey.OP_WRITE);
                            continue;
                        }
                        if(key.isReadable()){
                            SocketChannel chatClient = (SocketChannel)key.channel();
                            readBytes(chatClient);
                        }
                    }
                } catch (IOException ex){
                    System.out.println(ex.getMessage());
                }
            }
        }).start();
    }

    private StringBuilder request = new StringBuilder();
    private ByteBuffer byteBuffer = ByteBuffer.allocateDirect(1024);
    private static Charset charset = StandardCharsets.UTF_8;
    private void readBytes(SocketChannel chatClient) throws IOException {
        if(!chatClient.isOpen()) return;
        request.setLength(0);
        byteBuffer.clear();

        for(int bytesRead = chatClient.read(byteBuffer); bytesRead > 0; bytesRead = chatClient.read(byteBuffer)){
            byteBuffer.flip();
            CharBuffer charBuffer = charset.decode(byteBuffer);
            request.append(charBuffer);
        }

        createResponse(request.toString());
    }

    private void createResponse(String text){
        StringBuilder response = new StringBuilder();
        String[] arr = new String[4];
        arr[3] = "";

    }

    private void addLogClient(SocketChannel client, String log){
        if(!map.containsKey(client)){
            map.put(client, new Connection(log));
        } else {
            map.get(client).log.append(log).append("\n");
        }
    }

    private void log(String log){
        this.log.append(log).append("\n");
    }

    public void stopServer(){
        this.isRunning = false;
    }

    public String getServerLog() {
        return log.toString();
    }

    private static class Connection {
        private StringBuilder log;
        private String id;

        Connection(String id){
            this.id = id;
            this.log = new StringBuilder("\n=== " + id + " log start ===\n");
        }

        public void close() {
            this.log.append("=== ").append(id).append(" log end ===\n");
        }

        @Override
        public String toString() {
            return log.toString();
        }
    }
}
