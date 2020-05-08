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
    private static List<ChatClient> list = new LinkedList<>();
    private int index = 0;

    private ChatClientTask(Callable<String> callable){
        super(callable);
    }

    public static ChatClientTask create(ChatClient client, List<String> msgs, int wait){
        return new ChatClientTask(()-> {
            client.connect();
            client.send(client.getId() + " logged in");
            StringBuilder response = new StringBuilder();
            msgs.forEach((string)->{
                response.append(client.send(string));
                try {
                    Thread.currentThread().wait(wait);
                    System.out.println(response);
                } catch (InterruptedException ex){
                    System.out.println(ex.getMessage());
                }
            });
            client.getChatView().append(client.send(response.toString()));
            list.add(client);
            return client.getChatView().toString();
        });
    }

    public ChatClient getClient(){
        return list.get(index++);
    }
}
