import java.awt.*;
import java.awt.event.*;
// import java.io.BufferedReader;
// import java.io.IOException;
// import java.io.InputStreamReader;
// import java.io.PrintWriter;
// import java.net.Socket;

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

  // BufferedReader in;
  // PrintWriter out;

  JFrame frame = new JFrame("Chatter");
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
      // updateChatPane(name + " has joined the conversation.");

      initializeThreads();

    } catch(UnknownHostException e) {
      System.out.println("Unknown Host.");
    } catch(IOException e){
      System.out.println("Cannot find Server");
    }

    setMessage("");

    // Layout GUI
    // textField.setEditable(false);
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
        // log(msg);
        // updateChatPane(name+": "+msg);
        textField.setText("");
      }
    });

    this.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

  }

  public void setMessage(String msg){

    // log("set "+msg);
    this.msg = msg;
    // log("done set "+this.msg);

  }

  public String getMessage(){

    // log("return "+this.msg);
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

      Scanner sc = new Scanner(System.in);
      // StringReader reader = new StringReader(textField.getText());

      // BufferedReader reader = new BufferedReader(new InputStream(System.in));

      // messageArea.read(reader, );

      public void run(){

        try{

          // Send data to the ServerSocket
          OutputStream outToServer = client.getOutputStream();
          DataOutputStream out = new DataOutputStream(outToServer);

          while(true){

            log(name + ": ");

            // String message = sc.nextLine();
            String message;

            // System.out.println("andito 0");
            // setMessage("1");

            while(true){
              // System.out.println("andito 0.5");
              // log(getMessage());
              log("waiting for new message to send");
              message=getMessage();
              if(message.compareTo("")!=0){
                setMessage("");
                break;
              }
            }

            // setMessage("");
            // System.out.println("andito 1");


            // String message = getMessage();

            // while(getMessage().compareTo("")==0){
            //   System.out.println("======"+message2+"=======");
            // }

            // System.out.println(message2);
            // updateChatPane("you: "+message2);

            // System.out.println("========"+message+"========");

            if(message.compareTo("/paalam") == 0){
              out.writeUTF(name + " has ended conversation.");
              updateChatPane(name + " has ended conversation.");
              log(name + " has ended conversation.");
              client.close();
              frame.setVisible(false);
              break;
            }

            // System.out.println("andito 2");


            out.writeUTF(name + ": " + message);
            // updateChatPane(name + ": "+message);
            updateChatPane("you: "+message);
            log(name + ": "+message);

            // System.out.println("andito 3");




            // System.out.println("~~~~~~~~~~~~~~~~~~~~");
            // out.writeUTF(name + ": " + message2);
            // System.out.println(message2);
            // System.out.println("~~~~~~~~~~~~~~~~~~~~");


          }

        } catch(Exception e){
          //e.printStackTrace();
        }

      }

    };
  }

  private void log(String msg){

    System.out.print("\n[log]: "+msg);

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

//sources: http://www.javatpoint.com/creating-thread
//           https://docs.oracle.com/javase/tutorial/essential/concurrency/runthread.html
