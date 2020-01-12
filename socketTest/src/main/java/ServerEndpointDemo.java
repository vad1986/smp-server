import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.server.ServerEndpoint;

@ServerEndpoint("/serverendpointdemo")
public class ServerEndpointDemo {

   @OnOpen
   public void handleOpen(){
      System.out.println("client is now connected.....");
   }

   @OnMessage
   public String handleMessage(String message){
      System.out.println("receive from client: "+message);
      String replymessage="echo"+message;
      return null;
   }

}
