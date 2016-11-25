import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;
import java.util.ArrayList;

public class ChatServerListener implements Runnable {
  public static ArrayList<ChatServerListener> clientList = new ArrayList<ChatServerListener>();
  public static ArrayList<String> clientNameList = new ArrayList<String>();
  public static int maxPlayers = -1;

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

  synchronized public static boolean waitingForClients(){
    return clientNameList.size() != maxPlayers;
  }

  private static void log(String str){
    System.out.println("\n[server listener tcp log]: received "+str);
  }

  public void run() {
    try {
      while(true){
        String message = inputStream.readUTF();
        log(message);

        if(message.startsWith("SET_ALIAS>>")){
          message = message.replaceFirst("SET_ALIAS>>", "");

          if(clientNameList.contains(message)){
            this.outputStream.writeUTF("REJECT_ALIAS>>"+message);
            this.socket.shutdownInput();
            this.socket.shutdownOutput();
            socket.close();
          }

          this.outputStream.writeUTF("ACCEPT_ALIAS>>"+message);
          clientNameList.add(message);
        }

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
