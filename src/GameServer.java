import java.io.IOException;
import java.net.UnknownHostException;
import java.net.SocketException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import java.util.Date;
import java.util.Vector;
import java.util.Random;

public class GameServer extends Thread {

  private DatagramSocket socket = null;

  private InetAddress address;
  private static String ip = "127.0.0.1";
  private static int port = 1500;

  private DatagramPacket packet;
  private String received;
  private byte[] buf;
  private String[] parsed;
  private boolean broadcastPermission = false;

  private final static Object ipLock = new Object();
  private final static Object serverPortLock = new Object();
  private final static Object clientPortLock = new Object();

  private static Vector<Integer> ipVector = new Vector<Integer>();

  private static Random random = new Random();

  public GameServer() throws IOException {
    try{
      address = InetAddress.getByName(ip);
      socket = new DatagramSocket(port);
      // socket = new DatagramSocket(port);
    } catch(UnknownHostException e){
      e.printStackTrace();
      System.exit(-1);
    } catch(SocketException se){
      se.printStackTrace();
      System.exit(-1);
    }
  }

  public static int getClientPort(){
    synchronized(clientPortLock){
      // synchronized(countLock){
        return generateRandomPort();
      // }
    }
  }

  synchronized private static int generateRandomPort(){
    int randPort = 0;
    while(ipVector.contains(randPort) ||
          randPort<2000){

      randPort = random.nextInt(63536)+2000;
    }

    ipVector.add(randPort);
    return randPort;
  }

  synchronized public static int getServerPort(){
    synchronized(serverPortLock){
      return port;
    }
  }

  synchronized public static String getServerAddress(){
    synchronized(ipLock){
      return ip;
    }
  }

  public void log(String msg){
    System.out.println("\n[server udp log]: "+msg);
  }

  public void run() {
    // broadcaster.start();
    // log("started on "+Server.serverAddress+":"+Server.gamePort);
    while(true){
      try{
        buf = new byte[256];
        packet = new DatagramPacket(buf, buf.length);

        socket.receive(packet);

        received = new String(packet.getData(), 0, packet.getLength());

        log("received "+received);

        if(received.startsWith("START_UDP_CLIENT")){
          parsed = received.split(">>");

          // expected
          // p[0]              p[1]   p[2]   p[4]
          // START_UDP_CLIENT>>[IP]>>[PORT]>>END_UDP_CLIENT

          // for(String s : parsed){
          //   System.out.println(s);
          // }

          if(parsed.length<4){
            continue;
          }
          else{
            if(parsed[0].compareTo("START_UDP_CLIENT")!=0||
               parsed[3].compareTo("END_UDP_CLIENT")!=0 ){
              continue;
            }
            else{
              InetAddress clientAddress = InetAddress.getByName(parsed[1]);
              int port = Integer.parseInt(parsed[2]);

              GameServerBroadcaster g = new GameServerBroadcaster(socket, clientAddress, port);
              GameServerBroadcaster.clientList.add(g);
            }
          }
        }

      } catch(IOException e){
        e.printStackTrace();
        System.exit(-1);
      }

    }
  }
}

