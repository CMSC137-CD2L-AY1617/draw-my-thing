import java.io.IOException;
import java.net.UnknownHostException;
import java.net.SocketException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

public class GameServer extends Thread {

  private DatagramSocket socket = null;

  private InetAddress address;
  private static String ip;
  private static int port = Server.gamePort;

  private DatagramPacket packet;
  private String received;
  private byte[] inBuff;
  private byte[] outBuff;
  private String[] parsed;
  // private boolean broadcastPermission = false;

  private final static Object ipLock = new Object();
  private final static Object serverPortLock = new Object();

  // private final static Object clientPortLock = new Object();

  // private static Vector<Integer> ipVector = new Vector<Integer>();

  // private static Random random = new Random();

  public GameServer() throws IOException {
    try{
      socket = new DatagramSocket(port);
    } catch(SocketException se){
      se.printStackTrace();
      System.exit(-1);
    }
  }

  // public static int getClientPort(){
  //   synchronized(clientPortLock){
  //     // synchronized(countLock){
  //       return generateRandomPort();
  //     // }
  //   }
  // }

  // synchronized private static int generateRandomPort(){
  //   int randPort = 0;
  //   while(ipVector.contains(randPort) ||
  //         randPort<2000){

  //     randPort = random.nextInt(63536)+2000;
  //   }

  //   ipVector.add(randPort);
  //   return randPort;
  // }

  synchronized public static int getServerPort(){
    synchronized(serverPortLock){
      return port;
    }
  }

  // synchronized public static String getServerAddress(){
  //   synchronized(ipLock){
  //     return ip;
  //   }
  // }

  public void log(String msg){
    System.out.println("\n[server udp log]: "+msg);
  }

  public void run() {
    while(true){
      try{


        log("Waiting for client on "+socket.getLocalSocketAddress()+" on port " + socket.getLocalPort() + "...");

        received = receiveData();

        log("Just connected on "+socket.getLocalSocketAddress()+" on port " + socket.getLocalPort() + "...");

        if(received.startsWith("START_UDP_CLIENT")){
          parsed = Server.parseData(received);

          // expected
          // p[0]              p[1]   p[2]   p[4]
          // START_UDP_CLIENT>>[IP]>>[PORT]>>END_UDP_CLIENT

          if(parsed.length<4){
            continue;
          }
          else{
            if(parsed[0].compareTo("START_UDP_CLIENT")!=0 ||
               parsed[3].compareTo("END_UDP_CLIENT")!=0 ){
              continue;
            }
            else{
              InetAddress clientAddress = InetAddress.getByName(parsed[1]);
              int port = Integer.parseInt(parsed[2]);

              GameServerBroadcaster g = new GameServerBroadcaster(socket, clientAddress, port);
              GameServerBroadcaster.clientList.add(g);

              // log("max "+Server.getMaxPlayers());
              // log("size "+GameServerBroadcaster.clientList.size());

              if(GameServerBroadcaster.clientList.size() < Server.getMaxPlayers()){
                broadcastState(GameState.WAITING.name());
              }
              else{
                broadcastState(GameState.INGAME.name());
              }

              broadcastPermissions(0);
            }
          }
        }

      } catch(IOException e){
        e.printStackTrace();
        System.exit(-1);
      }

    }
  }

  private void broadcastPermissions(int loner){
    // broadcast permissions
    for(int i=0; i<ChatServerListener.clientNameList.size(); i++){
      String broadcast = "UPDATE_PERMISSION"+Server.DELIMITER+ChatServerListener.clientNameList.get(i)+Server.DELIMITER;
      if(i==loner){
        broadcast = broadcast+"DRAW";
      }
      else{
        broadcast = broadcast+"GUESS";
      }

      outBuff = prepareData(broadcast);
      sendData(outBuff);

    }
  }

  private void broadcastState(String state){
    // broadcast permissions
    // for(int i=0; i<ChatServerListener.clientNameList.size(); i++){
      String broadcast = "STATE"+Server.DELIMITER+state;

      outBuff = prepareData(broadcast);
      sendData(outBuff);
    // }
  }


  private String receiveData(){
    try{
      inBuff = new byte[256];
      packet = new DatagramPacket(inBuff,inBuff.length);

      //The receive method of DatagramSocket will indefinitely block until
      //a UDP datagram is received
      this.socket.receive(packet);
      received = new String(packet.getData(), 0, packet.getLength());

      log("received " + received);

    } catch(IOException ioe){
      System.out.println("\nError reading.");
    }

    return received;
  }

  private void sendData(byte[] outBuff){
    try{
      for(GameServerBroadcaster listener: GameServerBroadcaster.clientList){
        packet = new DatagramPacket(outBuff, outBuff.length, listener.getClientAddress(), listener.getClientPort());

          // send it
          socket.send(packet);
        }
    } catch(IOException ioe){
      System.out.println("\nError reading.");
    }
  }

  private byte[] prepareData(String received){
    outBuff = new byte[256];
    outBuff = received.getBytes();
    return outBuff;
  }

  // private String setServerAddress() {//throws IOException {
  //   String serverAddress = "";
  //   while(serverAddress.isEmpty()){
  //     serverAddress = JOptionPane.showInputDialog(
  //                             null,
  //                             "Enter Server's IP Address:",
  //                             "Welcome to Draw My Thing",
  //                             JOptionPane.QUESTION_MESSAGE);
  //   }

  //   return serverAddress;
  // }
}

