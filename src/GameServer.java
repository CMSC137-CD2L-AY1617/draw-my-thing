import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import java.util.Date;

public class GameServer extends Thread {

  // DatagramSocket socket;
  // private String serverName;
  // private int port;
  private GameServerBroadcaster broadcaster;
  // private Thread serverListenerThread;

  public GameServer() throws IOException {
    // serverName = getServerAddress();
    // port = getServerPort();
    // serverName = address;
    // port = port;
    // socket = new DatagramSocket(port);

    broadcaster = new GameServerBroadcaster();
      // Thread t = new Thread(serverListener);
  }

  // private int getServerPort() {
  //   int port = -1;
  //   String userInput = "";

  //   while(port < 0 || port < 1024 ){
  //       userInput = JOptionPane.showInputDialog(
  //                             null,
  //                             "Enter game server's port:",
  //                             "Welcome to Draw My Thing",
  //                             JOptionPane.QUESTION_MESSAGE);

  //     if(userInput == null){
  //       continue;
  //     }

  //     port = Integer.parseInt(userInput);
  //   }

  //   return port;
  // }

  // private String getServerAddress() {//throws IOException {
  //   String serverName = "";
  //   while(serverName.isEmpty()){
  //     serverName = JOptionPane.showInputDialog(null,
  //                                              "Enter Game Server's IP Address:",
  //                                              "Welcome to Draw My Thing",
  //                                              JOptionPane.QUESTION_MESSAGE);
  //   }

  //   return serverName;
  // }


  public void log(String msg){
    System.out.println("\n[server udp log]: "+msg);
  }

  public void run() {
    // try{
      // log("Getting ready to send game updates on "+serverName+":"+port);


      broadcaster.start();

      // byte buffer[] = new byte[256];
      // DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
      // socket.receive(packet);

      // log("Request received...sending update...");

      // String date = new Date().toString();
      // buffer = date.getBytes();

      // // send
      // InetAddress group = InetAddress.getByName(serverName);
      // packet = new DatagramPacket(buffer, buffer.length, group, port);
      // socket.send(packet);

      log("started on "+Server.serverAddress+":"+Server.gamePort);
    // } catch(IOException e){
    //   e.printStackTrace();
    // }
  }


  // public static void main(String [] args) {
  //   try {
  //      Thread gameServer = new GameServer();
  //      gameServer.start();
  //   } catch(IOException e) {
  //      // System.out.println("\nUsage: java GameServer");
  //   } catch(ArrayIndexOutOfBoundsException e) {
  //      // System.out.println("\nUsage: java GameServer");
  //   }
  // }

}
