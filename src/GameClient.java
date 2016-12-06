import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.Point;
import java.io.IOException;
import javax.swing.BorderFactory;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import javax.swing.JOptionPane;
import java.util.LinkedList;
import java.util.HashMap;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JTextArea;
import javax.swing.JScrollPane;
import java.awt.BorderLayout;
import javax.swing.SwingUtilities;
import javax.swing.text.DefaultCaret;

public class GameClient extends JPanel implements Runnable {

  private final static long serialVersionUID = 1L;

  private DrawMyThing game;
  private HashMap<String, Object> playerDetails = new HashMap<String, Object>();
  private LinkedList<ColoredGeometry> shapesDrawn;
  private PlayerState playerState = PlayerState.READY;

  private static final int DELAY = 2000; //2 seconds

  private int port = 4446;
  private byte[] inBuff;
  private byte[] outBuff;
  private String ip;
  private InetAddress address;
  private DatagramPacket packet;
  private DatagramSocket socket;
  private String received = "";
  private String[] parsed;
  private String muted = "";
  private boolean setPermission = false;

  private String msg = "";
  private String name = "";

  private Thread inThread;
  private Thread outThread;

  private int outPort;
  private String serverAddress;
  private InetAddress outAddress;

  private static int ANSWER_ROWS = 8;
  private static int ANSWER_COLS = 24;

  private static int UPDATE_AREA_BORDER_TOP = 0;
  private static int UPDATE_AREA_BORDER_LEFT = 10;
  private static int UPDATE_AREA_BORDER_BOTTOM = 0;
  private static int UPDATE_AREA_BORDER_RIGHT = 0;

  private JTextField guessArea = new JTextField(ANSWER_COLS);
  private JTextArea updateArea = new JTextArea(ANSWER_ROWS, ANSWER_COLS);
  private JScrollPane updatePane = new JScrollPane(updateArea);
  private DefaultCaret caret = (DefaultCaret)updateArea.getCaret();

  GameClient() {
    // GUI
    caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);

    updateArea.setEditable(false);
    updateArea.setLineWrap(true);
    updateArea.setWrapStyleWord(true);
    updateArea.setBorder(BorderFactory.createEmptyBorder(UPDATE_AREA_BORDER_TOP, UPDATE_AREA_BORDER_LEFT, UPDATE_AREA_BORDER_BOTTOM, UPDATE_AREA_BORDER_RIGHT));

    setLayout(new BorderLayout());

    add(guessArea, BorderLayout.SOUTH);
    add(updatePane, BorderLayout.CENTER);

    // Add Listeners
    guessArea.addActionListener(new ActionListener() {
      // listen for 'enter' key
      // set the msg variable to the update typed
      // clear guess field for new update
      public void actionPerformed(ActionEvent e) {
        msg = guessArea.getText();
        setMessage(msg);
        guessArea.setText("");
      }
    });

  }

  public void setAlias(String name){
    this.name = name;
  }

  private void log(String msg){
    System.out.print("\n[client udp log]: "+msg);
  }


  public void setClientDetails(Client client){
    this.serverAddress = client.serverAddress;
    this.ip = client.clientAddress;
    this.port = client.gamePort;
    // this.name = client.name;
  }

  public void setGameInstance(DrawMyThing game){
    this.game = game;
  }

  public void setUpClientDetails(String name){
    playerDetails.put("alias", name);
    playerDetails.put("score", 0);
    playerDetails.put("permission", "GUESS"); // depends on player state
  }

  public void initializeGame(){
    try{
      // port = GameServer.getClientPort();

      // something
      // ip = clientAddress;
      // ip = setAddress();

      address = InetAddress.getByName(ip);

      log("Connecting to " + ip + " on port " + port);

      socket = new DatagramSocket(port);
      // socket = new DatagramSocket(port, address);

      log("Just connected to " + this.socket.getLocalSocketAddress());

      outPort = GameServer.getServerPort();
      String outIP = serverAddress;
      outAddress = InetAddress.getByName(outIP);

      String details = "START_UDP_CLIENT"+Server.DELIMITER+
                        ip+Server.DELIMITER+
                        port+Server.DELIMITER+
                        "END_UDP_CLIENT";

      sendToServer(details);
      // broadcastPermissions();

      initializeThreads();

    } catch(IOException e){
      e.printStackTrace();
      System.exit(-1);
    }
  }

  public Object getValue(Object key){
    return playerDetails.get(key);
  }

  public void setValue(String key, Object value){
    playerDetails.put(key, value);
  }

  public void run(){
    this.inThread.start();
    this.outThread.start();
  }

  private void sendDrawUpdate(String action, int x, int y, String tool, int rgb){

    String broadcastUpdate = "START_DRAW_UPDATE"+Server.DELIMITER+
                              action+Server.DELIMITER+
                              x+Server.DELIMITER+
                              y+Server.DELIMITER+
                              tool+Server.DELIMITER+
                              rgb+Server.DELIMITER+
                              "END_DRAW_UPDATE";

    sendToServer(broadcastUpdate);
  }

  // private void broadcastPermissions(){
  //   // broadcast permissions
  //   for(int i=0; i<ChatServerListener.clientNameList.size(); i++){
  //     String broadcast = "UPDATE_PERMISSION"+Server.DELIMITER+ChatServerListener.clientNameList.get(i)+Server.DELIMITER;
  //     if(i==0){
  //       broadcast = broadcast+"DRAW";
  //     }
  //     else{
  //       broadcast = broadcast+"GUESS";
  //     }

  //     sendToServer(broadcast);
  //   }
  // }

  private void sendToServer(String msg){
    try{
      byte[] message = new byte[256];
      message = msg.getBytes();

      // send it
      packet = new DatagramPacket(message, message.length, outAddress, outPort);

      socket.send(packet);
    } catch(IOException e){
      e.printStackTrace();
      System.exit(-1);
    }
  }

  public void sendUpdate(String update){
    sendToServer(update);
  }

  public void sendUpdate(byte[] message){
    try{
      // send it
      packet = new DatagramPacket(message, message.length, outAddress, outPort);

      socket.send(packet);
    } catch(IOException e){
      e.printStackTrace();
      System.exit(-1);
    }
  }

  // private String setAddress() {//throws IOException {
  //   String ip = "";
  //   while(ip.isEmpty()){
  //     ip = JOptionPane.showInputDialog(null,
  //                                              "Enter IP Address:",
  //                                              "Welcome to Draw My Thing",
  //                                              JOptionPane.QUESTION_MESSAGE);
  //   }

  //   return ip;
  // }

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

  private void setMessage(String msg){

    this.msg = msg;

  }

  private String getMessage(){

    return this.msg;

  }

  private void updateChatPane(String message){

    String contents = this.updateArea.getText();
    contents += message+"\n\n";
    updateArea.setText(contents);

  }

  private void initializeThreads(){

    // For incoming messages
    this.inThread = new Thread(){
      public void run(){
        // try{
          while(true){
            received = receiveData();

            updateChatPane("SERVER: "+received);

            if(received.startsWith("STATE"+Server.DELIMITER)){
              received = received.replaceFirst("STATE"+Server.DELIMITER, "");
              game.setGameState(GameState.valueOf(received));

              // System.exit(-1);

            }
            else if(received.startsWith("UPDATE_PERMISSION")){
              parsed = Server.parseData(received);

              String name = parsed[1];
              String newPermission = parsed[2];

              String targetClient = (String)playerDetails.get("alias");

              if(targetClient.compareTo(name)==0 && !setPermission){
                playerDetails.put("permission", newPermission);
                if(newPermission.compareTo("DRAW")==0){
                  game.setDrawPermissions();

                  muted = game.getMutedWordToBroadcast();

                  setPermission = true;
                }
                else if(newPermission.compareTo("GUESS")==0){
                  game.setGuessPermissions();
                  setPermission = true;
                }
              }
            }
            else if(received.startsWith("UPDATE_TEXT") && game.playerState != PlayerState.DRAWING){
              String word = Server.parseData(received)[1];
              game.updateRenderedText(word);
            }
            else if(received.startsWith("START_DRAW_UPDATE")){
              parsed = Server.parseData(received);

              // expected
              // START_DRAW_UPDATE>> p[0]
              // [action]>>          p[1]
              // [x coord]>>         p[2]
              // [y coord]>>         p[3]
              // [geometry / tool]   p[4]
              // [color in rgb]      p[5]
              // END_DRAW_UPDATE     p[6]

              Mimic m = Mimic.valueOf(parsed[1]);

              int x = Integer.parseInt(parsed[2]);
              int y = Integer.parseInt(parsed[3]);
              Point p = new Point(x, y);

              Geometry g = Geometry.valueOf(parsed[4]);

              int rgb = Integer.parseInt(parsed[5]);
              Color c = new Color(rgb);

              game.gamePanel.setDrawTools(g, c);

              if(m == Mimic.PRESS){
                game.gamePanel.doMousePress(p);
              }
              else if(m == Mimic.RELEASE){
                game.gamePanel.doMouseRelease(p);
              }
              else if(m == Mimic.DRAG){
                game.gamePanel.doMouseDrag(p);
              }

            }


            if(game.playerState == PlayerState.DRAWING){
              sendToServer("UPDATE_TEXT"+Server.DELIMITER+muted);
            }


          }
        // } catch(IOException ioe){
        //   System.out.println("\nError reading.");
        // }
      }

    };


    // For outgoing messages
    this.outThread = new Thread(){
      public void run(){
        String message;
        try{
          while(true){
            // broadcastPermissions();

            outBuff = new byte[256];

            while(true){
              // without logging / any System.out.println statements
              // messages do not get sent to the server
              log("waiting for new message");
              message=getMessage();
              if(message.compareTo("")!=0){
                setMessage("");
                break;
              }
            }

            message = message.toUpperCase();
            message = "GUESS"+Server.DELIMITER+message;

            outBuff = message.getBytes();

            // send it
            packet = new DatagramPacket(outBuff, outBuff.length, address, 4446);

            socket.send(packet);

            updateChatPane("You guessed "+message);
            log(name + ": "+message);
          }

        } catch(IOException e){
          e.printStackTrace();
          System.exit(-1);
        }
      }
    };
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
}
