import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;
import java.util.ArrayList;

public class ChatServerListener implements Runnable {
  public static ArrayList<ChatServerListener> clientList = new ArrayList<ChatServerListener>();

  private Socket socket;
  private DataInputStream inputStream;
  private DataOutputStream outputStream;

  public ChatServerListener(Socket socket) {
    try {
      // Set up input and output streams for broadcasting.
      this.socket = socket;
      this.inputStream = new DataInputStream(this.socket.getInputStream());
      this.outputStream = new DataOutputStream(this.socket.getOutputStream());
    } catch(Exception e) {

    }

  }

  private static void log(String str){
    System.out.println("\n[server listener log]: received "+str);
  }

  public void run() {
    try {
      while(true){
        String message = inputStream.readUTF();
        log(message);

        // Broadcast each message sent by each client in the inputStream to each other clients on the client list.
        for(ChatServerListener listener: clientList){
          if(listener != this){
            listener.outputStream.writeUTF(message);
          }
        }

      }

    } catch(Exception e) {

    }
  }
}
