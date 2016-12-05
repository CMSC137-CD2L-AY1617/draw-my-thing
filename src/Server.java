import java.io.IOException;
import java.util.HashMap;
import javax.swing.JOptionPane;

public class Server {

  public static final String DELIMITER = ">>";

  public static int minPlayers = 1;
  public static int maxPlayers = 0;
  public static int chatPort = 1234;
  public static int gamePort = 1500;
  // volatile public static int broadcastPort = -1;
  public static String serverAddress;

  private static ChatServer chatServer;
  private static GameServer gameServer;



  public Server(){
    // try {

    //   while(maxPlayers<minPlayers){
    //     maxPlayers = setMaxPlayers();
    //   }

    //   while(Server.chatPort<1024){
    //     int port = setServerPort();
    //     Server.chatPort = port;
    //     Server.gamePort = port+1;
    //     // Server.broadcastPort = port+2;
    //   }

    //   while(Server.serverAddress == null || Server.serverAddress.isEmpty()){
    //     String address = setServerAddress();
    //     Server.serverAddress = address;
    //   }

    //   Server.chatServer = new ChatServer();
    //   Server.gameServer = new GameServer();

    //   Server.chatServer.start();
    //   Server.gameServer.start();


    // } catch(IOException e) {
    //   System.out.println("Server error: Missing port or server address.");
    //    // System.out.println("\nUsage: java ChatServer");
    // } catch(ArrayIndexOutOfBoundsException e) {
    //    // System.out.println("\nUsage: java ChatServer");
    // }
  }

  private static int setServerPort() {
    int port = -1;
    String userInput = "";

    while(port < 0 || port < 1024 ){
        userInput = JOptionPane.showInputDialog(
                              null,
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

  private static int setMaxPlayers() {
    int max = -1;
    String userInput = "";

    while(max < 1){
        userInput = JOptionPane.showInputDialog(
                              null,
                              "Enter max players:",
                              "Welcome to Draw My Thing",
                              JOptionPane.QUESTION_MESSAGE);

      if(userInput == null){
        continue;
      }

      try{
        max = Integer.parseInt(userInput);
      } catch(NumberFormatException e){
        return setMaxPlayers();
      }

    }

    return max;
  }

  private static String setServerAddress() {//throws IOException {
    String serverName = "";
    while(serverName.isEmpty()){
      serverName = JOptionPane.showInputDialog(
                              null,
                              "Enter Server's IP Address:",
                              "Welcome to Draw My Thing",
                              JOptionPane.QUESTION_MESSAGE);
    }

    return serverName;
  }

  private void log(String msg){
    System.out.print("\n[server log]: "+msg);
  }

  public static String[] parseData(String data){
    return data.split(Server.DELIMITER);
  }

  synchronized public static int getMaxPlayers(){
    return maxPlayers;
  }

  synchronized public static String getServerAddress(){
    return serverAddress;
  }

  synchronized public static void main(String [] args) {
    // new Server();
    try {

      while(maxPlayers<minPlayers){
        maxPlayers = setMaxPlayers();
      }

      // while(Server.chatPort<1024){
      //   int port = setServerPort();
      //   Server.chatPort = port;
      //   Server.gamePort = port+1;
      //   // Server.broadcastPort = port+2;
      // }

      // while(Server.serverAddress == null ||
      //       Server.serverAddress.isEmpty()){
      //   String address = setServerAddress();
      //   Server.serverAddress = address;
      // }

      Server.chatServer = new ChatServer();
      Server.gameServer = new GameServer();

      Server.chatServer.start();
      Server.gameServer.start();


    } catch(IOException e) {
      System.out.println("Server error: Missing port or server address.");
       // System.out.println("\nUsage: java ChatServer");
    }
  }
}
