/**
 *
 *  @author Szewczyk Ryszard S19234
 *
 */

package zad1;


import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;

public class ChatClientTask extends FutureTask<String> {
    private List<ChatClient> list = new LinkedList<>();

    private ChatClientTask(Callable<String> callable, ChatClient client){
        super(callable);
        this.list.add(client);
    }

    public static ChatClientTask create(ChatClient client, List<String> msgs, int wait){
        return new ChatClientTask(()-> {
            client.connect();
            client.send(null);
            msgs.forEach((string)->{
                String response = client.send(string);
                try {
                    Thread.currentThread().wait(wait);
                } catch (InterruptedException ex){
                    System.out.println(ex.getMessage());
                }
            });
            client.getLog().append(client.send(null));
            return client.getLog().toString();
        }, client);
    }

    public ChatClient getClient(){
        return null;
    }
}
