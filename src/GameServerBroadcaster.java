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
  // private DataInputStream inputStream;
  // private DataOutputStream outputStream;

  public GameServerBroadcaster() {
    try {

      while(serverName.isEmpty()){
        serverName = Server.serverAddress;
      }

      log("got server addr "+serverName);

      while(gamePort<1024){
        gamePort = Server.gamePort;
        broadcastPort = Server.broadcastPort;
      }

      log("got server port "+gamePort);
      log("got broadcast port "+broadcastPort);

      socket = new DatagramSocket(gamePort);
      group = InetAddress.getByName(serverName);

      // log(group.toString());

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
    while(true) {
      try {
        // LinkedList<Object> drawings = new LinkedList<Object>();

        // for(ColoredGeometry c : DrawPanel.getDrawings()){
        //   os.flush();
        //   os.writeObject( (Object)c );
        //   os.flush();

        //   //retrieves byte array
        //   byte[] sendBuf = byteStream.toByteArray();
        //   packet = new DatagramPacket(sendBuf, sendBuf.length, group, port);
        //   int byteCount = packet.getLength();
        //   socket.send(packet);

        //   log("sent "+packet.toString());
        // }

        log(group.toString());

        byte[] message = new byte[256];

        String s = "hi";

        message = s.getBytes();

        // send it
        packet = new DatagramPacket(message, message.length, group, broadcastPort);
        socket.send(packet);

        // log("sent list");

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
