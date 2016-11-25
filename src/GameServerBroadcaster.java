import java.net.DatagramPacket;
import javax.swing.JOptionPane;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.LinkedList;
import java.io.IOException;
import java.io.ByteArrayOutputStream;
import java.io.BufferedOutputStream;
import java.io.ObjectOutputStream;

public class GameServerBroadcaster extends Thread {
  private static final int DELAY = 2000;

  private InetAddress group;
  private DatagramSocket socket;
  private DatagramPacket packet;
  private byte message[];
  private String serverName = "";
  private int gamePort = -1;
  private int broadcastPort = -1;
  private ByteArrayOutputStream byteStream;
  ObjectOutputStream os;

  public GameServerBroadcaster() {
    try {

      while(serverName.isEmpty()){
        serverName = Server.serverAddress;
      }

      while(gamePort<1024){
        gamePort = Server.gamePort;
        broadcastPort = Server.broadcastPort;
      }

      socket = new DatagramSocket(gamePort);
      group = InetAddress.getByName(serverName);

      log("broadcasting to "+group.toString());

      byteStream = new ByteArrayOutputStream(5000);
      os = new ObjectOutputStream(new BufferedOutputStream(byteStream));
    } catch(IOException e) {
      e.printStackTrace();
      System.exit(0);
    }

  }

  private static void log(String str){
    System.out.println("\n[server broadcaster udp log]: "+str);
  }

  public void run() {
    String defaultUpdate = "broadcast to "+group.toString()+" every "+DELAY+" ms...";
    byte[] message;
    String msg = defaultUpdate;
    boolean broadcastPermission = false;
    while(true) {
      try {
        message = new byte[256];

        if(ChatServerListener.clientNameList.size()==1 && !broadcastPermission){
          msg = "UPDATE_PERMISSION>>"+ChatServerListener.clientNameList.get(0)+">>DRAW";
          broadcastPermission = true;
        }

        message = msg.getBytes();

        // send it
        packet = new DatagramPacket(message, message.length, group, broadcastPort);

        socket.send(packet);
        log("sent: '"+msg+"'");

        msg = defaultUpdate;

        // sleep for a while
        try {
          Thread.sleep(DELAY);
        } catch (InterruptedException e){
          log("interrupted!");
        }

      } catch (IOException e) {
        e.printStackTrace();
        System.exit(0);
      }
    }
  }
}
