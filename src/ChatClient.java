import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemListener;
import java.awt.event.ItemEvent;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.text.DefaultCaret;
import javax.swing.plaf.basic.BasicBorders.*;
import javax.swing.plaf.metal.MetalBorders.*;

public class ChatClient implements Runnable {
  private static int CHAT_ROWS = 8;
  private static int CHAT_COLS = 24;

  private static final int WINDOW_HEIGHT = 600;
  private static final int WINDOW_WIDTH = 1200;
  private static final int WINDOW_PROPORTION = WINDOW_WIDTH/3;
  private static final int SIDE_PANEL_SIZE = (int)(WINDOW_PROPORTION - WINDOW_PROPORTION*0.35);
  private static final int GAME_AREA_SIZE = WINDOW_WIDTH - (2*SIDE_PANEL_SIZE);

  private ChatState chatState = ChatState.DISCONNECTED;

  private Socket client;
  private String name;
  private Thread inThread;
  private Thread outThread;
  private String msg = "";
  private DataOutputStream out;

  private JFrame frame = new JFrame("[Client] Draw My Thing");
  private JPanel scorePanel = new JPanel();
  private JPanel gamePanel = new JPanel();
  private JPanel chatPanel = new JPanel();

  private JTextField textArea = new JTextField(CHAT_COLS);
  private JTextArea messageArea = new JTextArea(CHAT_ROWS, CHAT_COLS);
  private JScrollPane chatArea = new JScrollPane(messageArea);
  private JToggleButton toggleChat = new JToggleButton("Disconnect");
  private DefaultCaret caret = (DefaultCaret)messageArea.getCaret();

  public ChatClient() {

    try{

      String serverName = getServerAddress();
      int port = getServerPort();

      log("Connecting to " + serverName + " on port " + port);
      updateChatPane("Connecting to " + serverName + " on port " + port);

      this.client = new Socket(serverName, port);
      chatState = ChatState.CONNECTED;

      log("Just connected to " + this.client.getRemoteSocketAddress());
      updateChatPane("Just connected to " + this.client.getRemoteSocketAddress()+"\n");

      String userName = getUserAlias();
      this.name = userName;

      OutputStream outToServer = client.getOutputStream();
      out = new DataOutputStream(outToServer);

      out.writeUTF(name + " joined the conversation.");
      updateChatPane("You joined the conversation.");

      initializeThreads();

    } catch(UnknownHostException e) {
      System.out.println("Unknown Host.");
      System.exit(-1);
    } catch(IOException e){
      System.out.println("Cannot find Server");
      System.exit(-1);
    }

    // GUI
    caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);

    chatPanel.setBorder(new SplitPaneBorder(Color.BLACK, Color.WHITE));

    messageArea.setEditable(false);
    messageArea.setLineWrap(true);
    messageArea.setWrapStyleWord(true);

    chatPanel.setLayout(new BorderLayout());
    chatPanel.add(toggleChat, BorderLayout.NORTH);
    chatPanel.add(textArea, BorderLayout.SOUTH);
    chatPanel.add(chatArea, BorderLayout.CENTER);
    chatPanel.setBackground(Color.BLUE);
    chatPanel.setPreferredSize(new Dimension(SIDE_PANEL_SIZE,WINDOW_HEIGHT));

    gamePanel.setBackground(Color.YELLOW);
    gamePanel.setPreferredSize(new Dimension(GAME_AREA_SIZE,WINDOW_HEIGHT));

    scorePanel.setBackground(Color.RED);
    scorePanel.setPreferredSize(new Dimension(SIDE_PANEL_SIZE,WINDOW_HEIGHT));

    frame.getContentPane().add(chatPanel, "East");
    frame.getContentPane().add(scorePanel, "West");
    frame.getContentPane().add(gamePanel, "Center");

    frame.setSize(new Dimension(WINDOW_WIDTH,WINDOW_HEIGHT));
    frame.setResizable(false);
    frame.pack();

    frame.setLocationRelativeTo(null);
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    frame.requestFocus();
    textArea.requestFocusInWindow();

    // Add Listeners
    toggleChat.addItemListener(new ItemListener() {
      public void itemStateChanged(ItemEvent e) {
        if(e.getStateChange()==ItemEvent.SELECTED){
          toggleChat.setText("Connect");
          textArea.setEditable(false);

          chatState = ChatState.DISCONNECTED;
          disconnectChat();
        }
        else if(e.getStateChange()==ItemEvent.DESELECTED){
          toggleChat.setText("Disconnect");
          textArea.setEditable(true);

          frame.requestFocus();
          textArea.requestFocusInWindow();

          chatState = ChatState.CONNECTED;
          connectChat();
        }
      }
    });

    textArea.addActionListener(new ActionListener() {
      // listen for 'enter' key
      // set the msg variable to the message typed
      // clear text field for new message
      public void actionPerformed(ActionEvent e) {
        msg = textArea.getText();
        setMessage(msg);
        textArea.setText("");
      }
    });

  }

  private void setMessage(String msg){

    this.msg = msg;

  }

  private String getMessage(){

    return this.msg;

  }

  public void run(){

    this.inThread.start();
    this.outThread.start();
    this.frame.setVisible(true);

  }

  private String getServerAddress() {//throws IOException {
    String serverName = "";
    while(serverName.isEmpty()){
      serverName = JOptionPane.showInputDialog(
        this.frame,
        "Enter Server's IP Address:",
        "Welcome to Draw My Thing",
        JOptionPane.QUESTION_MESSAGE);
    }

    return serverName;
  }

  private int getServerPort() {//throws IOException {
    int port = -1;

    while(port < 0 || port < 1024){
      port = Integer.parseInt(JOptionPane.showInputDialog(
                              frame,
                              "Enter Server's Port:",
                              "Welcome to Draw My Thing",
                              JOptionPane.QUESTION_MESSAGE)
      );
    }

    return port;

  }

  private String getUserAlias() {//throws IOException {
    String userName = "";
    // while(userName.isEmpty()){
    while(userName.isEmpty() && !(ChatServer.existingClientName(userName)) ){
      userName = JOptionPane.showInputDialog(
        frame,
        "Choose your alias:",
        "Alias selection",
        JOptionPane.PLAIN_MESSAGE);

      ChatServer.addClientName(userName);
      ChatServer.printClientList();
      System.out.println(ChatServer.existingClientName(name));

    }

    return userName;

  }

  private void initializeThreads(){

    // For incoming messages
    this.inThread = new Thread(){

      public void run(){

        try{

          // Receive data from the ServerSocket
          InputStream inFromServer = client.getInputStream();
          DataInputStream in = new DataInputStream(inFromServer);

          while(true){
            String message = in.readUTF();

            log("\n" + message);
            updateChatPane(message);

            log(name + ": ");
          }

        } catch(Exception e){
          //e.printStackTrace();
        }

      }

    };

    // For outgoing messages
    this.outThread = new Thread(){

      public void run(){

        try{

          // Send data to the ServerSocket
          while(true){

            log(name + ": ");

            String message;

            while(true){
              // without logging / any System.out.println statements
              // messages do not get sent to the server
              log("waiting for new message to send");
              message=getMessage();
              if(message.compareTo("")!=0){
                setMessage("");
                break;
              }
            }

            if(message.compareTo("/paalam") == 0){
              toggleChat.doClick();

              while(chatState == ChatState.DISCONNECTED){
                log("waiting to reconnect chat");
              }

              connectChat();
              break;
            }

            out.writeUTF(name + ": " + message);
            updateChatPane("you: "+message);
            log(name + ": "+message);

          }

        } catch(Exception e){
          //e.printStackTrace();
        }

      }

    };
  }

  private void log(String msg){

    System.out.print("\n[client log]: "+msg);

  }

  private void updateChatPane(String message){

    String contents = this.messageArea.getText();
    contents += message+"\n";
    messageArea.setText(contents);

  }

  private void connectChat() {

    try{
      out.writeUTF(name + " resumed the conversation.");
      updateChatPane("You resumed the conversation.");
      log(name + " resumed the conversation.");
    } catch (IOException e){
      log("client name is missing");
      System.exit(-1);
    }


  }

  private void disconnectChat() {

    try{
      out.writeUTF(name + " left conversation.");
      updateChatPane("You left the conversation.");
      log(name + " left the conversation.");
    } catch (IOException e){
      log("client name is missing");
      System.exit(-1);
    }

  }



  public static void main(String [] args) {

    try {

      ChatClient chatClient = new ChatClient();

      Thread t = new Thread(chatClient);
      t.start();

    } catch(ArrayIndexOutOfBoundsException e) {
      System.out.println("Usage: java ChatClient");
    } catch(Exception e){
      System.out.println("Usage: java ChatClient");
    }

  }

}

/*
 * sources:
 *
 * threads
 * http://www.javatpoint.com/creating-thread
 * https://docs.oracle.com/javase/tutorial/essential/concurrency/runthread.html
 *
 * chat GUI
 * http://cs.lmu.edu/~ray/notes/javanetexamples/#chat
 *
 * auto scroll down of chat area
 * http://stackoverflow.com/questions/2483572
 *
 * jtogglebutton usage guide
 * http://stackoverflow.com/questions/7524536
 */