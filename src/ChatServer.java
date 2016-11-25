import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

public class ChatServer extends Thread {
  private ServerSocket serverSocket;
  private JFrame frame = new JFrame("[Server] Draw My Thing");

  public ChatServer() throws IOException {
    serverSocket = new ServerSocket(Server.chatPort);
  }

  private void log(String msg){
    System.out.print("\n[server tcp log]: "+msg);
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

}
