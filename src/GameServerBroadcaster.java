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

  public static ArrayList<GameServerBroadcaster> clientList = new ArrayList<GameServerBroadcaster>();

  private long FIVE_SECONDS = 5000;

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
  private boolean broadcastPermission = false;

  private final static Object ipLock = new Object();
  private final static Object serverPortLock = new Object();
  private final static Object clientPortLock = new Object();

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
      try {
        byte[] message = new byte[256];
        String msg = "";

        while(ChatServerListener.clientNameList.isEmpty()){
          log("waiting");
        }

        if(ChatServerListener.clientNameList.size()==1 && !broadcastPermission){
          msg = "UPDATE_PERMISSION>>"+ChatServerListener.clientNameList.get(0)+">>DRAW";
          broadcastPermission = true;
        }
        // else{
        //   msg = "UPDATE_PERMISSION>>"+ChatServerListener.clientNameList.get(0)+">>GUESS";
        // }

        message = msg.getBytes();

        for(GameServerBroadcaster listener: clientList){
            packet = new DatagramPacket(message, message.length, listener.getClientAddress(), listener.getClientPort());
            // send it
            socket.send(packet);
        }

      } catch (IOException e) {
        e.printStackTrace();
        System.exit(0);
      }

    try{
      while(true){
        inBuff = new byte[256];
        packet = new DatagramPacket(inBuff,inBuff.length);

        //The receive method of DatagramSocket will indefinitely block until
        //a UDP datagram is received
        this.socket.receive(packet);
        received = new String(packet.getData(), 0, packet.getLength());

        log("received " + received);

        outBuff = new byte[256];
        outBuff = received.getBytes();

        for(GameServerBroadcaster listener: clientList){
          if(listener != this){
            packet = new DatagramPacket(outBuff, outBuff.length, listener.getClientAddress(), listener.getClientPort());

            // send it
            socket.send(packet);
          }
        }
      }
    } catch(IOException ioe){
      System.out.println("\nError reading.");
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

}
