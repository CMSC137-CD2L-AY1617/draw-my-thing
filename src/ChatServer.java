import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

public class ChatServer extends Thread {
  private ServerSocket serverSocket;
  private JFrame frame = new JFrame("[Server] Draw My Thing");
  private static ArrayList<String> userList = new ArrayList<String>();

  public ChatServer() throws IOException {
    int port = getServerPort();
    serverSocket = new ServerSocket(port);
    System.out.println(serverSocket.getLocalSocketAddress());
    System.out.println(serverSocket.getInetAddress());
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

  public static void printClientList() {

    for(int i=0; i<userList.size(); i++){
      System.out.println(userList.get(i));
    }

  }

  synchronized public static void addClientName(String name) {

    if(!existingClientName(name)){
    // if(existingClientName(name)<0){
      printClientList();
      userList.add(name);
      printClientList();
    }

  }

  synchronized public static boolean existingClientName(String clientName) {
  // public static int existingClientName(String clientName) {

    return userList.contains((Object)clientName);
    // return userList.indexOf((Object)(clientName));

  }

  public void run() {
    // continuously waits for clients to connect
    while(true) {
      try {
        System.out.println("Waiting for client on port " + serverSocket.getLocalPort() + "...");

        Socket client = serverSocket.accept();
        printClientList();

        // Set serverListener and add new client to clientList
        ChatServerListener serverListener = new ChatServerListener(client);
        ChatServerListener.clientList.add(serverListener);

        Thread t = new Thread(serverListener);
        t.start();

        System.out.println("Just connected to " + client.getRemoteSocketAddress());

      } catch(IOException e) {
        System.out.println("Usage: java ChatServer");
        break;
      }
    }
  }

  public static void main(String [] args) {
    try {
       int port = Integer.parseInt(args[0]);

       Thread t = new ChatServer();

       t.start();
    } catch(IOException e) {
       System.out.println("Usage: java ChatServer <port no.>");
    } catch(ArrayIndexOutOfBoundsException e) {
       System.out.println("Usage: java ChatServer <port no.> ");
    }
  }
}
