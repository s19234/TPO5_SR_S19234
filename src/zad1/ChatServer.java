/**
 *
 *  @author Szewczyk Ryszard S19234
 *
 */

package zad1;


import java.nio.channels.SocketChannel;

public class ChatServer {
    private String host;
    private int port;
    private SocketChannel socketChannel;
    private StringBuilder log;
    private boolean isRunning;

    public ChatServer(String host, int port){
        this.host = host;
        this.port = port;
    }

    public void startServer(){
        this.isRunning = true;
    }

    public void stopServer(){
        this.isRunning = false;
    }

    public String getServerLog() {
        return log.toString();
    }


}
