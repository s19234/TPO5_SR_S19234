/**
 *
 *  @author Szewczyk Ryszard S19234
 *
 */

package zad1;


import java.io.IOException;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;

public class ChatClientTask extends FutureTask<Void> {
    private ChatClient chatClient;

    private ChatClientTask(Callable<Void> callable, ChatClient client){
        super(callable);
        this.chatClient = client;
    }

    public static ChatClientTask create(ChatClient client, List<String> msgs, int wait){
        return new ChatClientTask(()->{
            client.login();
            Thread.sleep(wait);
            msgs.forEach((string)->{
                try {
                    client.send(string);
                    Thread.sleep(wait);
                } catch (IOException | InterruptedException ex){
                    System.out.println(ex.getMessage());
                }
            });
            client.logout();
            return null;
        }, client);
    }

    public ChatClient getClient(){
        return chatClient;
    }
}
