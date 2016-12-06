import java.io.IOException;
import java.util.HashMap;
import javax.swing.JOptionPane;

public class Server {

  public static final String DELIMITER = ">>";

  public static int minPlayers = 1;
  public static int maxPlayers = 0;
  public static int chatPort = 1234;
  public static int gamePort = 1500;
  public static String serverAddress;

  private static ChatServer chatServer;
  private static GameServer gameServer;

  public Server(){}

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

  private static String setServerAddress() {
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
    try {
      while(maxPlayers<minPlayers){
        maxPlayers = setMaxPlayers();
      }

      Server.chatServer = new ChatServer();
      Server.gameServer = new GameServer();

      Server.chatServer.start();
      Server.gameServer.start();

    } catch(IOException e) {
      System.out.println("Server error: Missing number of players.");
    }
  }
}
