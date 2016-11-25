import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import java.util.Date;

public class GameServer extends Thread {

  private GameServerBroadcaster broadcaster;

  public GameServer() throws IOException {
    broadcaster = new GameServerBroadcaster();
  }

  public void log(String msg){
    System.out.println("\n[server udp log]: "+msg);
  }

  public void run() {
    broadcaster.start();
    log("started on "+Server.serverAddress+":"+Server.gamePort);
  }

}
