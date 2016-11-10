import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

public class ChatServer extends Thread {
  private ServerSocket serverSocket;
  private JFrame frame = new JFrame("[Server] Draw My Thing");

  public ChatServer() throws IOException {
    int port = getServerPort();
    serverSocket = new ServerSocket(port);
  }

  private int getServerPort() {
    int port = -1;
    String userInput = "";

    while(port < 0 || port < 1024 ){
        userInput = JOptionPane.showInputDialog(
                              frame,
                              "Enter chat server's port:",
                              "Welcome to Draw My Thing",
                              JOptionPane.QUESTION_MESSAGE);

      if(userInput == null){
        continue;
      }

      port = Integer.parseInt(userInput);
    }

    return port;
  }

  private void log(String msg){

    System.out.print("\n[server log]: "+msg);

  }

  public void run() {
    // continuously waits for clients to connect
    while(true) {
      try {
        log("Waiting for client on port " + serverSocket.getLocalPort() + "...");

        Socket client = serverSocket.accept();

        // Set serverListener and add new client to clientList
        ChatServerListener serverListener = new ChatServerListener(client);
        ChatServerListener.clientList.add(serverListener);

        Thread t = new Thread(serverListener);
        t.start();

        log("Just connected to " + client.getRemoteSocketAddress());

      } catch(IOException e) {
        System.out.println("\nUsage: java ChatServer");
        break;
      }
    }
  }

  public static void main(String [] args) {
    try {
       Thread t = new ChatServer();
       t.start();
    } catch(IOException e) {
       System.out.println("\nUsage: java ChatServer");
    } catch(ArrayIndexOutOfBoundsException e) {
       System.out.println("\nUsage: java ChatServer");
    }
  }
}
