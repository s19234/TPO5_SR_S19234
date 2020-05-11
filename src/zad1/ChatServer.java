/**
 *
 *  @author Szewczyk Ryszard S19234
 *
 */

package zad1;


import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class ChatServer {
    private StringBuilder log;
    private ServerSocketChannel socketChannel;
    private Selector selector;
    private static Charset charset = StandardCharsets.UTF_8;
    private boolean isRunning;
    private String host;
    private int port;
    private Map<SocketChannel, Connection> connectionMap;
    private String time = null;

    public ChatServer(String host, int port){
        this.host = host;
        this.port = port;
        this.connectionMap = new HashMap<>();
        this.log = new StringBuilder();
    }

    public void startServer() throws IOException {
        socketChannel = ServerSocketChannel.open();
        socketChannel.socket().bind(new InetSocketAddress(host, port));
        socketChannel.configureBlocking(false);
        selector = Selector.open();
        socketChannel.register(selector, SelectionKey.OP_ACCEPT);
        new Thread(()->{
            isRunning = true;
            System.out.println("Server started\n");
            while(isRunning){
                try {
                    selector.select();
                    Set<SelectionKey> keySet = selector.selectedKeys();
                    Iterator<SelectionKey> iterator = keySet.iterator();
                    while(iterator.hasNext()){
                        SelectionKey key = iterator.next();
                        iterator.remove();
                        if(key.isAcceptable()){
                            SocketChannel client = socketChannel.accept();
                            client.configureBlocking(false);
                            client.register(selector, SelectionKey.OP_READ | SelectionKey.OP_WRITE);
                            continue;
                        }
                        if(key.isReadable()){
                            SocketChannel client = (SocketChannel)key.channel();
                            handleRequest(client);
                        }
                    }
                } catch (IOException ex){
                    System.out.println(ex.getMessage());
                }
            }
        }).start();
    }

    private void handleRequest(SocketChannel client) throws IOException {
        StringBuilder response = new StringBuilder();
        String request = prepareResponse(client);
        log.append(time = DateTimeFormatter.ofPattern("HH:mm:ss:SSS").format(LocalDateTime.now())).append(" ");
        String[] arr = request.split(" ");
        if(request.contains("/login ")){
            connectionMap.putIfAbsent(client, new Connection(arr[1]));
            response.append(arr[1]).append(" logged in");
            log.append(arr[1]).append(" logged in");
        } else if(request.contains("/logout ")){
            response.append(arr[1]).append(" logged out");
            log.append(connectionMap.get(client).id).append(": ");
            log.append(arr[1]).append(" logged out");
            sendResponse(client, response.toString() + "\n");
            connectionMap.remove(client);
        } else {
            response.append(connectionMap.get(client).id).append(": ").append(request);
            log.append(connectionMap.get(client).id).append(": ");
            log.append(request);
        }
        log.append("\n");
        response.append("\n");

        broadcast(response.toString());
    }

    private String prepareResponse(SocketChannel client) throws IOException {
        ByteBuffer inBuf = ByteBuffer.allocateDirect(4096);
        StringBuilder req = new StringBuilder();
        for(int bytesRead = client.read(inBuf); bytesRead > 0; bytesRead = client.read(inBuf)){
            inBuf.flip();
            req.append(charset.decode(inBuf));
        }
        return req.toString();
    }

    private void broadcast(String message){
        connectionMap.forEach((client, connection) -> {
            try {
                sendResponse(client, message);
            } catch (IOException ex){
                System.out.println(ex.getMessage());
            }
        });
    }

    private void sendResponse(SocketChannel client, String response) throws IOException {
        ByteBuffer outBuf = ByteBuffer.allocateDirect(response.getBytes().length);
        outBuf.put(charset.encode(response));
        outBuf.flip();
        client.write(outBuf);
    }

    public String getServerLog(){
        return log.toString();
    }

    public void stopServer(){
        isRunning = false;
        System.out.println("Server stopped");
    }
    private static class Connection {
        private String id;

        public Connection(String id){
            this.id = id;
        }
    }
}
