/**
 *
 *  @author Szewczyk Ryszard S19234
 *
 */

package zad1;


import java.io.IOException;
import java.util.List;
import java.util.concurrent.FutureTask;

public class ChatClientTask extends FutureTask<String> {
    private ChatClient chatClient;

    private ChatClientTask(ChatClient client, List<String> msgs, int wait){
        super(()->{
            client.login();
            if(wait != 0)
                Thread.sleep(wait);
            msgs.forEach((string)->{
                try {
                    client.send(string);
                    if(wait != 0)
                        Thread.sleep(wait);
                } catch (IOException | InterruptedException ex){
                    System.out.println(ex.getMessage());
                }
            });
            client.logout();
            return "null";
        });
        this.chatClient = client;
    }

    public static ChatClientTask create(ChatClient client, List<String> msgs, int wait){
        return new ChatClientTask(client, msgs, wait);
    }

    public ChatClient getClient(){
        return chatClient;
    }
}
