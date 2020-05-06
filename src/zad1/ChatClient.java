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
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class ChatClient {
    private String host;
    private int port;
    private String id;
    private StringBuilder log;
    private SocketChannel socketChannel;
    private static Charset charset = StandardCharsets.UTF_8;

    public ChatClient(String host, int port, String id){
        this.host = host;
        this.port = port;
        this.id = id;
        this.log = new StringBuilder();
    }

    public void connect(){
        try {
            socketChannel = SocketChannel.open();
            socketChannel.configureBlocking(false);
            socketChannel.connect(new InetSocketAddress(host, port));
            while((!socketChannel.finishConnect()));
        } catch (IOException ex){
            System.out.println(ex.getMessage());
        }
    }

    public String send(String text){
        ByteBuffer outBuffer = ByteBuffer.allocateDirect(text.getBytes().length);
        ByteBuffer inBuffer = ByteBuffer.allocateDirect(4096);
        StringBuilder response = new StringBuilder();
        try {
            outBuffer.put(charset.encode(text));
            outBuffer.flip();
            socketChannel.write(outBuffer);
        } catch (IOException ex){
            System.out.println(ex.getMessage());
        }

        inBuffer.clear();

        try {
            int readBytes;

            while((readBytes = socketChannel.read(inBuffer)) < 1);
            for(; readBytes > 0; readBytes = socketChannel.read(inBuffer)) {
                inBuffer.flip();
                CharBuffer charBuffer = charset.decode(inBuffer);
                response.append(charBuffer);
            }
        } catch (IOException ex){
            System.out.println(ex.getMessage());
        }
        return response.toString();
    }

    public StringBuilder getLog() {
        return log;
    }

    public String getChatView(){
        return null;
    }
}
