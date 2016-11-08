import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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
import javax.swing.text.DefaultCaret;
import javax.swing.plaf.basic.BasicBorders.*;
import javax.swing.plaf.metal.MetalBorders.*;

public class ChatClient implements Runnable {
  private Socket client;
  private String name;
  private Thread inThread;
  private Thread outThread;
  private String msg = "";

  private JFrame frame = new JFrame("[Client] Draw My Thing");
  private JPanel chatPanel = new JPanel();
  private JPanel scorePanel = new JPanel();
  private JPanel gamePanel = new JPanel();

  private JTextField textField = new JTextField(40);
  private JTextArea messageArea = new JTextArea(8, 40);
  private JScrollPane chatPane = new JScrollPane(messageArea);

  // public ChatClient(String userName, String serverName, int port){
  public ChatClient() {

    try{

      String serverName = getServerAddress();
      int port = getServerPort();

      this.client = new Socket(serverName, port);

      log("Connecting to " + serverName + " on port " + port);
      // updateChatPane("Connecting to " + serverName + " on port " + port);

      log("Just connected to " + this.client.getRemoteSocketAddress());
      // updateChatPane("Just connected to " + this.client.getRemoteSocketAddress()+"\n");

      String userName = getUserAlias();
      this.name = userName;

      OutputStream outToServer = client.getOutputStream();
      DataOutputStream out = new DataOutputStream(outToServer);

      out.writeUTF(name + " has joined the conversation.");
      updateChatPane("you have joined the conversation.");

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

    textArea.requestFocusInWindow();

    chatPanel.setLayout(new BorderLayout());
    chatPanel.add(textArea, BorderLayout.SOUTH);
    chatPanel.add(chatArea, BorderLayout.CENTER);
    chatPanel.setBackground(Color.BLUE);
    chatPanel.setPreferredSize(new Dimension(270,600));

    gamePanel.setBackground(Color.YELLOW);
    gamePanel.setPreferredSize(new Dimension(360,600));

    scorePanel.setBackground(Color.RED);
    scorePanel.setPreferredSize(new Dimension(240,600));

    frame.getContentPane().add(chatPanel, "East");
    frame.getContentPane().add(scorePanel, "West");
    frame.getContentPane().add(gamePanel, "Center");

    frame.setSize(new Dimension(1080,600));
    frame.setResizable(false);
    // frame.pack();

    // Add Listeners
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

    this.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

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
          OutputStream outToServer = client.getOutputStream();
          DataOutputStream out = new DataOutputStream(outToServer);

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
              out.writeUTF(name + " has ended conversation.");
              updateChatPane(name + " has ended conversation.");
              log(name + " has ended conversation.");
              client.close();
              frame.setVisible(false);
              System.exit(-1);
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

  public static void main(String [] args) {

    try {

      // String serverName = args[0];
      // int port = Integer.parseInt(args[1]);
      // String name = args[2];

      // ChatClient chatClient = new ChatClient(name, serverName, port);

      ChatClient chatClient = new ChatClient();

      Thread t = new Thread(chatClient);
      t.start();

    } catch(ArrayIndexOutOfBoundsException e) {
      // System.out.println("Usage: java ChatClient <server ip> <port no.> <name>");
      System.out.println("Usage: java ChatClient");
    } catch(Exception e){
      // System.out.println("Usage: java ChatClient <server ip> <port no.> <name>");
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
 */
