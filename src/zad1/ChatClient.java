/**
 *
 *  @author Szewczyk Ryszard S19234
 *
 */

package zad1;


import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class ChatClient {
    private String id;
    private StringBuilder chatLog;
    private InetSocketAddress socketAddress;
    private SocketChannel client;
    private volatile boolean isBlocked;
    private volatile boolean isRunning;
    private static Charset charset = StandardCharsets.UTF_8;

    public ChatClient(String host, int port, String id){
        this.id = id;
        this.chatLog = new StringBuilder("=== " + id + " chat view\n");
        this.socketAddress = new InetSocketAddress(host, port);
        try {
            client = SocketChannel.open(socketAddress);
            client.configureBlocking(false);
        } catch (IOException ex){
            System.out.println(ex.getMessage());
        }

        new Thread(()->{
            isRunning = true;
            while(isRunning){
                try {
                    String response = getResponse();
                    if(!response.isEmpty()){
                        isBlocked = false;
                        chatLog.append(response);
                    }
                } catch (IOException ex){
                    System.out.println(ex.getMessage());
                }
            }
        }).start();
    }

    public void send(String message) throws IOException {
        ByteBuffer outBuf = ByteBuffer.allocateDirect(message.getBytes().length);
        outBuf.put(charset.encode(message));
        outBuf.flip();
        client.write(outBuf);
        isBlocked = true;
        while(isBlocked);
    }

    public void login(){
        try {
            send("/login " + id);
        } catch (IOException ex){
            System.out.println(ex.getMessage());
        }
    }

    public void logout() throws IOException {
        send("/logout " + id);
        isRunning = false;
        while(!chatLog.toString().contains(id + " logged out")){
            for(String response = getResponse(); !response.isEmpty(); response = getResponse()){
                chatLog.append(response);
            }
        }
    }

    public String getChatView(){
        return chatLog.toString();
    }

    private String getResponse() throws IOException {
        ByteBuffer inBuf = ByteBuffer.allocateDirect(4096);
        StringBuilder response = new StringBuilder();
        while(client.read(inBuf) > 0){
            inBuf.flip();
            response.append(charset.decode(inBuf));
            inBuf.clear();
        }

        return response.toString();
    }
}
