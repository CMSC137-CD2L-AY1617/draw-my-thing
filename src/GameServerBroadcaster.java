import java.net.DatagramPacket;
import javax.swing.JOptionPane;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.LinkedList;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

public class GameServerBroadcaster extends Thread {
  private static final int DELAY = 2000;

  public static ArrayList<GameServerBroadcaster> clientList = new ArrayList<GameServerBroadcaster>();

  private static int maxPlayers = -1;

  private Thread inThread;
  private Thread outThread;

  protected int port;
  private byte[] inBuff;
  private byte[] outBuff;
  private InetAddress address;
  private DatagramPacket packet;
  private DatagramSocket socket;
  private static DatagramSocket oneTimeSocket;

  private String received;
  private String defaultUpdate;
  private String msg;

  private final static Object clientPortLock = new Object();
  public static ArrayList<Integer> portList = new ArrayList<Integer>();
  private static Random random = new Random();

  private final static Object ipLock = new Object();

  public GameServerBroadcaster(DatagramSocket socket, InetAddress address, int port) throws IOException {
    this.port = port;
    this.address = address;
    this.socket = socket;

    start();
  }

  private static void log(String str){
    System.out.println("\n[server broadcaster udp log]: "+str);
  }

  public void run() {
    // block
    while(ChatServerListener.clientNameList.isEmpty()){
      log("waiting");
    }

    // game proper
    while(true){
      received = receiveData();
      received = receiveObject();
      outBuff = prepareData(received);
      sendData(outBuff);
    }

  }

  synchronized public int getClientPort(){
    synchronized(clientPortLock){
      return port;
    }
  }

  synchronized public InetAddress getClientAddress(){
    synchronized(ipLock){
      return address;
    }
  }

  private void sendData(byte[] outBuff){
    try{
      for(GameServerBroadcaster listener: clientList){
        if(listener != this){
          packet = new DatagramPacket(outBuff, outBuff.length, listener.getClientAddress(), listener.getClientPort());

          // send it
          socket.send(packet);
        }
      }
    } catch(IOException ioe){
      System.out.println("\nError reading.");
    }
  }

  private String receiveData(){
    try{
      inBuff = new byte[256];
      packet = new DatagramPacket(inBuff,inBuff.length);

      //The receive method of DatagramSocket will indefinitely block until
      //a UDP datagram is received
      this.socket.receive(packet);
      received = new String(packet.getData(), 0, packet.getLength());

      // ColoredGeometry cg = ColoredGeometry.getObject(inBuff);

      log("received str " + received);

    } catch(IOException ioe){
      System.out.println("\nError reading.");
    }

    return received;
  }

  private String receiveObject(){
    try{
      inBuff = new byte[256];
      packet = new DatagramPacket(inBuff,inBuff.length);

      //The receive method of DatagramSocket will indefinitely block until
      //a UDP datagram is received
      this.socket.receive(packet);

      // ColoredGeometry cg = ColoredGeometry.getObject(inBuff);

      log("received obj " + received);

    } catch(IOException ioe){
      System.out.println("\nError reading.");
    }

    return received;
  }

  private byte[] prepareData(String received){
    outBuff = new byte[256];
    outBuff = received.getBytes();
    return outBuff;
  }

  synchronized public static int getGamePort(){
    return generateRandomPort();
  }

  synchronized private static int generateRandomPort(){
    int randPort = 0;
    while(portList.contains(randPort) ||
          randPort<2000){

      randPort = random.nextInt(63536)+2000;
    }

    portList.add(randPort);
    return randPort;
  }

}
