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
    private int index = 0;

    private ChatClientTask(Callable<String> callable, ChatClient client){
        super(callable);
        this.list.add(client);
    }

    public static ChatClientTask create(ChatClient client, List<String> msgs, int wait){
        return new ChatClientTask(()-> {
            client.connect();
            client.send(client.getId() + " logged in");
            msgs.forEach((string)->{
                String response = client.send(string);
                try {
                    Thread.currentThread().wait(wait);
                    System.out.println(response);
                } catch (InterruptedException ex){
                    System.out.println(ex.getMessage());
                }
            });
            client.getChatView().append(client.send(null));
            return client.getChatView().toString();
        }, client);
    }

    public ChatClient getClient(){
        return list.get(index++);
    }
}
