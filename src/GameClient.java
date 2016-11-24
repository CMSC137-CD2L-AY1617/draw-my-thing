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

public class GameClient implements Runnable {

  private LinkedList<ColoredGeometry> shapesDrawn;
  private static final int DELAY = 2000; //2 seconds
  private String name = "Pia";
  int port = 1236;
  private String serverName = "230.0.0.1";
  byte message[] = new byte[256];
  InetAddress address;// = InetAddress.getByName(host);
  DatagramPacket packet;// =  new DatagramPacket(message, message.length, address, port);
  MulticastSocket socket;
  ByteArrayInputStream byteStream;
  ObjectInputStream is;

  private Thread inThread;
  // private Thread outThread;

  private PlayerState playerState = PlayerState.READY;

  GameClient() {
    initializeGame();
  }

  private void log(String msg){

    System.out.print("\n[client udp log]: "+msg);

  }

  public void initializeGame(){
    try{

      // serverName = getServerAddress();
      // port = getServerPort();

      // socket = new MulticastSocket(port);
      // socket = new MulticastSocket(1235);
      // address = InetAddress.getByName(serverName);

      while(serverName.isEmpty()){
        serverName = Server.serverAddress;
      }

      log("got server addr "+serverName);

      while(port<1024){
        port = Server.gamePort+1;
      }

      log("got server port "+port);

      socket = new MulticastSocket(port);
      address = InetAddress.getByName(serverName);

      socket.joinGroup(address);

      // name = getUserAlias();

      // initializeThreads();

    } catch(UnknownHostException e) {
      System.out.println("\nGame client: Unknown Host.");
      System.exit(-1);
    } catch(IOException e){
      System.out.println("\nGame client: Cannot find Server");
      System.exit(-1);
    }
  }

  public void run(){

    try{
      while(true){
        message = new byte[256];
        packet = new DatagramPacket(message, message.length);

        //The receive method of DatagramSocket will indefinitely block until
        //a UDP datagram is received
        socket.receive(packet);

        String received = new String(packet.getData(), 0, packet.getLength());
        log("received " + received);

        System.exit(0);

        // byte[] recvBuf = new byte[5000];
        // DatagramPacket packet = new DatagramPacket(recvBuf, recvBuf.length);
        // socket.receive(packet);
        // int byteCount = packet.getLength();

        // byteStream = new ByteArrayInputStream(recvBuf);
        // is = new ObjectInputStream(new BufferedInputStream(byteStream));

        // Object o = is.readObject();

        // System.out.println(o.toString());

        // return(o);

      }
    } catch(IOException ioe){
      System.out.println("\nError reading.");
    }
    // catch(ClassNotFoundException cnfe){
    //   System.out.println("\nClass not found.");
    // }

    // this.inThread.start();
    // this.outThread.start();

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
            // message = new byte[256];
            // packet = new DatagramPacket(message, message.length);

            // //The receive method of DatagramSocket will indefinitely block until
            // //a UDP datagram is received
            // socket.receive(packet);

            // String received = new String(packet.getData(), 0, packet.getLength());
            // log("received " + received);

            byte[] recvBuf = new byte[5000];
            DatagramPacket packet = new DatagramPacket(recvBuf, recvBuf.length);
            socket.receive(packet);
            int byteCount = packet.getLength();

            byteStream = new ByteArrayInputStream(recvBuf);
            is = new ObjectInputStream(new BufferedInputStream(byteStream));

            Object o = is.readObject();

            System.out.println(o.toString());

            // return(o);

          }

        } catch(Exception e){
          //e.printStackTrace();
        }

      }

    };

    // // For outgoing messages
    // this.outThread = new Thread(){

    //   public void run(){

    //     try{

    //       // Send data to the DatagramSocket
    //       while(true){

    //         // after every 2 seconds
    //         Thread.sleep(DELAY);

    //         // get representation of list of shapes

    //         packet =  new DatagramPacket(message, message.length, address, port);

    //         log(name + ": sent list of drawn shapes");

    //         socket.send(packet);

    //       }

    //     } catch(Exception e){
    //       //e.printStackTrace();
    //     }

    //   }

    // };

  }

  public static void main(String [] args) {
    try {
       GameClient gameClient = new GameClient();

       Thread gameThread = new Thread(gameClient);
       gameThread.start();
    } catch(ArrayIndexOutOfBoundsException e) {
       // System.out.println("\nUsage: java ChatServer");
    }
  }

}
