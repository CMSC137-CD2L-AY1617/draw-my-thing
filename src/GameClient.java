import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import javax.swing.BorderFactory;
import java.io.ObjectInputStream;
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

  private DrawMyThing game;
  private HashMap<String, Object> playerDetails = new HashMap<String, Object>();
  private LinkedList<ColoredGeometry> shapesDrawn;
  private PlayerState playerState = PlayerState.READY;

  private static final int DELAY = 2000; //2 seconds

  private int port = 4446;
  private byte[] inBuff;
  private byte[] outBuff;
  private String ip = "127.0.0.1";
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

    initializeGame();
    initializeThreads();

  }

  private void log(String msg){
    System.out.print("\n[client udp log]: "+msg);
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
      port = GameServer.getClientPort();
      ip = "127.0.0.1";

      address = InetAddress.getByName(ip);

      socket = new DatagramSocket(port);

      outPort = GameServer.getServerPort();
      String outIP = GameServer.getServerAddress();
      outAddress = InetAddress.getByName(outIP);

      String details = "START_UDP_CLIENT"+Server.DELIMITER+
                        ip+Server.DELIMITER+
                        port+Server.DELIMITER+
                        "END_UDP_CLIENT";

      sendToServer(details);
      broadcastPermissions();

    } catch(IOException e){
      // e.printStackTrace();
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

  private void broadcastPermissions(){
    // broadcast permissions
    for(int i=0; i<ChatServerListener.clientNameList.size(); i++){
      String broadcast = "UPDATE_PERMISSION"+Server.DELIMITER+ChatServerListener.clientNameList.get(i)+Server.DELIMITER;
      if(i==0){
        broadcast = broadcast+"DRAW";
      }
      else{
        broadcast = broadcast+"GUESS";
      }

      sendToServer(broadcast);
    }
  }

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
        try{
          while(true){
            inBuff = new byte[256];
            packet = new DatagramPacket(inBuff,inBuff.length);

            //The receive method of DatagramSocket will indefinitely block until
            //a UDP datagram is received
            socket.receive(packet);


            received = new String(packet.getData(), 0, packet.getLength());
            log("received " + received);
            updateChatPane("SERVER: "+received);

            if(received.startsWith("UPDATE_PERMISSION")){
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


            if(game.playerState == PlayerState.DRAWING){
              sendToServer("UPDATE_TEXT"+Server.DELIMITER+muted);
            }


          }
        } catch(IOException ioe){
          System.out.println("\nError reading.");
        }
      }

    };


    // For outgoing messages
    this.outThread = new Thread(){
      public void run(){
        String message;
        try{
          while(true){
            broadcastPermissions();

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
}
