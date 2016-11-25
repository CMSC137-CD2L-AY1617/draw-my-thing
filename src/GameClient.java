import java.io.IOException;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;
import java.net.DatagramPacket;
import java.net.MulticastSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import javax.swing.JOptionPane;
import java.util.LinkedList;
import java.util.HashMap;

public class GameClient implements Runnable {

  private DrawMyThing game;
  private HashMap<String, Object> playerDetails = new HashMap<String, Object>();
  private LinkedList<ColoredGeometry> shapesDrawn;
  private static final int DELAY = 2000; //2 seconds
  int port = 1236;
  private String serverName = "230.0.0.1";
  byte message[];
  InetAddress address;
  DatagramPacket packet;
  MulticastSocket socket;
  ByteArrayInputStream byteStream;
  ObjectInputStream is;

  private Thread inThread;


  private PlayerState playerState = PlayerState.READY;

  GameClient() {
    initializeGame();

  }

  private void log(String msg){
    System.out.print("\n[client udp log]: "+msg);
  }

  public void setGameInstance(DrawMyThing game){
    this.game = game;
  }

  public void setUpClientDetails(String name){
    playerDetails.put("alias", name);
    playerDetails.put("score", 0);
    playerDetails.put("permission", "GUESS"); // depends on player state
  }

  public void initializeGame(){
    try{
      while(serverName.isEmpty()){
        serverName = Server.serverAddress;
      }

      while(port<1024){
        port = Server.gamePort+1;
      }

      socket = new MulticastSocket(port);
      address = InetAddress.getByName(serverName);

      socket.joinGroup(address);

    } catch(UnknownHostException e) {
      System.out.println("\nGame client: Unknown Host.");
      System.exit(-1);
    } catch(IOException e){
      System.out.println("\nGame client: Cannot find Server");
      System.exit(-1);
    }
  }

  public Object getValue(Object key){
    return playerDetails.get(key);
  }

  public void setValue(String key, Object value){
    playerDetails.put(key, value);
  }

  public void run(){
    String received = "";
    String[] parsed;
    String muted = "";
    boolean setPermission = false;
    try{
      while(true){
        message = new byte[256];
        packet = new DatagramPacket(message, message.length);

        //The receive method of DatagramSocket will indefinitely block until
        //a UDP datagram is received
        socket.receive(packet);

        received = new String(packet.getData(), 0, packet.getLength());
        log("received " + received);

        if(received.startsWith("UPDATE_PERMISSION")){
          parsed = received.split(">>");

          String name = parsed[1];
          String newPermission = parsed[2];

          String targetClient = (String)playerDetails.get("alias");

          if(targetClient.compareTo(name)==0 && !setPermission){
            playerDetails.put("permission", newPermission);
            if(newPermission.compareTo("DRAW")==0){
              this.game.setDrawPermissions();

              muted = this.game.getMutedWordToBroadcast();

              setPermission = true;
            }
            else if(newPermission.compareTo("GUESS")==0){
              this.game.setGuessPermissions();
              setPermission = true;
            }
          }
        }
        else if(received.startsWith("UPDATE_TEXT") &&
                this.game.playerState != PlayerState.DRAWING){
          String word = received.split(">>")[1];
          this.game.updateRenderedText(word);
        }

        if(this.game.playerState == PlayerState.DRAWING){
          broadcastToServer(muted);
        }

      }
    } catch(IOException ioe){
      System.out.println("\nError reading.");
    }
  }

  private void broadcastToServer(String msg){
    try{
      byte[] message = new byte[256];

      msg = "UPDATE_TEXT>>"+msg;

      message = msg.getBytes();

      // send it
      packet = new DatagramPacket(message, message.length, address, port);

      socket.send(packet);

    } catch(IOException e){
      // e.printStackTrace();
      log("Muted word missing.");
    }
  }

  private String getServerAddress() {//throws IOException {
    String serverName = "";
    while(serverName.isEmpty()){
      serverName = JOptionPane.showInputDialog(null,
                                               "Enter Game Server's IP Address:",
                                               "Welcome to Draw My Thing",
                                               JOptionPane.QUESTION_MESSAGE);
    }

    return serverName;
  }

  private int getServerPort() {//throws IOException {
    int port = -1;

    while(port < 0 || port < 1024){
      port = Integer.parseInt(JOptionPane.showInputDialog(
                              null,
                              "Enter Game Server's Port:",
                              "Welcome to Draw My Thing",
                              JOptionPane.QUESTION_MESSAGE)
      );
    }

    return port;

  }

  private String getUserAlias() {//throws IOException {
    String userName = "";
    while(userName.isEmpty()){
      userName = JOptionPane.showInputDialog(null,
                                             "Choose your alias:",
                                             "Alias selection",
                                             JOptionPane.PLAIN_MESSAGE);
    }

    return userName;

  }

  private void initializeThreads(){

    // For incoming messages
    this.inThread = new Thread(){

      public void run(){

        try{

          while(true){
            byte[] recvBuf = new byte[5000];
            DatagramPacket packet = new DatagramPacket(recvBuf, recvBuf.length);
            socket.receive(packet);
            int byteCount = packet.getLength();

            byteStream = new ByteArrayInputStream(recvBuf);
            is = new ObjectInputStream(new BufferedInputStream(byteStream));

            Object o = is.readObject();

            System.out.println(o.toString());
          }

        } catch(Exception e){
          //e.printStackTrace();
        }

      }

    };
  }
}
