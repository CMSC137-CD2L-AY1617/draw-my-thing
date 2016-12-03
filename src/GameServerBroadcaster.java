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

  // private InetAddress group;
  // private DatagramSocket socket;
  // private DatagramPacket packet;
  // private byte message[];
  // private String serverName = "";
  // private int gamePort = -1;
  // private int broadcastPort = -1;
  // private ByteArrayOutputStream byteStream;
  // ObjectOutputStream os;

  public static ArrayList<GameServerBroadcaster> clientList = new ArrayList<GameServerBroadcaster>();

  // protected BufferedReader in = null;
  protected boolean moreQuotes = true;

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
  // byte[] message;
  private String msg;
  private boolean broadcastPermission = false;

  private final static Object ipLock = new Object();
  private final static Object serverPortLock = new Object();
  private final static Object clientPortLock = new Object();


  public GameServerBroadcaster(DatagramSocket socket, InetAddress address, int port) throws IOException {
    this.port = port;
    this.address = address;
    this.socket = socket;
    // oneTimeSocket = new DatagramSocket(1501);

    start();
  }

  private static void log(String str){
    System.out.println("\n[server broadcaster udp log]: "+str);
  }

  public void run() {
    // String defaultUpdate = "broadcast to "+group.toString()+" every "+DELAY+" ms...";
    // byte[] message;
    // String msg = defaultUpdate;
    // boolean broadcastPermission = false;
    // while(true) {
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

        // send it
        // packet = new DatagramPacket(message, message.length, group, broadcastPort);

        // socket.send(packet);

        for(GameServerBroadcaster listener: clientList){
          // if(listener != this){
            packet = new DatagramPacket(message, message.length, listener.getClientAddress(), listener.getClientPort());
            // send it
            socket.send(packet);
          // }
        }

    //     log("sent: '"+msg+"'");

    //     msg = defaultUpdate;

    //     // sleep for a while
    //     try {
    //       Thread.sleep(DELAY);
    //     } catch (InterruptedException e){
    //       log("interrupted!");
    //     }

      } catch (IOException e) {
        e.printStackTrace();
        System.exit(0);
      }
    // }

    try{
      while(true){
        inBuff = new byte[256];
        packet = new DatagramPacket(inBuff,inBuff.length);

        //The receive method of DatagramSocket will indefinitely block until
        //a UDP datagram is received
        this.socket.receive(packet);
        received = new String(packet.getData(), 0, packet.getLength());



        log("received " + received);
        // updateChatPane("SERVER: "+received);

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

  // public static void oneTimeBroadcast(String msg){
  //   try{
  //     byte[] message = new byte[256];

  //     // msg = "UPDATE_TEXT>>"+msg;

  //     message = msg.getBytes();

  //     for(GameServerBroadcaster listener: clientList){
  //       DatagramPacket oneTimePacket = new DatagramPacket(message, message.length, listener.getClientAddress(), listener.getClientPort());
  //         // send it
  //         oneTimeSocket.send(oneTimePacket);
  //     }

  //     // log("sent "+msg+" to "+outAddress+" port "+outPort);
  //     // log("sent to "+outAddress+" port "+outPort);


  //   } catch(IOException e){
  //     e.printStackTrace();
  //     System.exit(-1);
  //   }
  // }

}
