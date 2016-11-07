import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.net.*;
import java.io.*;
import java.util.*;

public class ChatClient implements Runnable {
  Socket client;
  String name;
  Thread inThread;
  Thread outThread;
  String msg = "";

  JFrame frame = new JFrame("Draw My Thing Chat Module");
  JPanel chatPanel = new JPanel();
  JTextField textField = new JTextField(40);
  JTextArea messageArea = new JTextArea(8, 40);
  JScrollPane chatPane = new JScrollPane(messageArea);

  public ChatClient(String name, String serverName, int port){

    try{

      this.name = name;
      this.client = new Socket(serverName, port);

      log("Connecting to " + serverName + " on port " + port);
      updateChatPane("Connecting to " + serverName + " on port " + port);

      log("Just connected to " + this.client.getRemoteSocketAddress());
      updateChatPane("Just connected to " + this.client.getRemoteSocketAddress());

      OutputStream outToServer = client.getOutputStream();
      DataOutputStream out = new DataOutputStream(outToServer);

      out.writeUTF(name + " has joined the conversation.");
      updateChatPane("you have joined the conversation.");

      initializeThreads();

    } catch(UnknownHostException e) {
      System.out.println("Unknown Host.");
    } catch(IOException e){
      System.out.println("Cannot find Server");
    }

    messageArea.setEditable(false);
    frame.getContentPane().add(textField, "South");
    frame.getContentPane().add(chatPane, "Center");
    frame.pack();

    // Add Listeners
    textField.addActionListener(new ActionListener() {
      // listen for 'enter' key
      // set the msg variable to the message typed
      // clear text field for new message
      public void actionPerformed(ActionEvent e) {
        msg = textField.getText();
        setMessage(msg);
        textField.setText("");
      }
    });

    this.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

  }

  public void setMessage(String msg){

    this.msg = msg;

  }

  public String getMessage(){

    return this.msg;

  }

  public void run(){

    this.inThread.start();
    this.outThread.start();
    this.frame.setVisible(true);

  }

  public void initializeThreads(){

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

      String serverName = args[0];
      int port = Integer.parseInt(args[1]);
      String name = args[2];

      ChatClient chatClient = new ChatClient(name, serverName, port);
      Thread t = new Thread(chatClient);
      t.start();

    } catch(ArrayIndexOutOfBoundsException e) {
      System.out.println("Usage: java chatClientent <server ip> <port no.> <name>");
    } catch(Exception e){
      System.out.println("Usage: java ChatClient <server ip> <port no.> <name>");
    }

  }

}

/*
 * sources:
 * http://www.javatpoint.com/creating-thread
 * https://docs.oracle.com/javase/tutorial/essential/concurrency/runthread.html
 * http://cs.lmu.edu/~ray/notes/javanetexamples/#chat
 */
