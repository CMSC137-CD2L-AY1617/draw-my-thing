import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.net.*;
import java.io.*;
import java.util.*;

public class ChatServer extends Thread {
  private ServerSocket serverSocket;
  private JFrame frame = new JFrame("[Server] Draw My Thing");
  private static ArrayList<String> userList = new ArrayList<String>();

  public ChatServer(int port) throws IOException {
    serverSocket = new ServerSocket(port);
  }

  private String setServerPort() {
      return JOptionPane.showInputDialog(
          frame,
          "Enter port where server will listen",
          "Set server's port",
          JOptionPane.QUESTION_MESSAGE);
  }

  public static void printClientList() {

    for(int i=0; i<userList.size(); i++){
      System.out.println(userList.get(i));
    }

  }

  public static void addClientName(String name) {

    if(!existingClientName(name)){
    // if(existingClientName(name)<0){
      userList.add(name);
    }

  }

  public static boolean existingClientName(String clientName) {
  // public static int existingClientName(String clientName) {

    return userList.contains((Object)clientName);
    // return userList.indexOf((Object)(clientName));

  }

  public void run() {
    while(true) { // continuously waits for clients to connect
      try {
        System.out.println("Waiting for client on port " + serverSocket.getLocalPort() + "...");

        Socket client = serverSocket.accept();
        // printClientList();

        // Set serverListener and add new client to clientList
        ChatServerListener serverListener = new ChatServerListener(client);
        ChatServerListener.clientList.add(serverListener);

        Thread t = new Thread(serverListener);
        t.start();

        System.out.println("Just connected to " + client.getRemoteSocketAddress());

      } catch(IOException e) {
        // System.out.println("Usage: java ChatServer <port no.>");
        System.out.println("Usage: java ChatServer");
        break;
      }
    }
  }

  public static void main(String [] args) {
    try {
       int port = Integer.parseInt(args[0]);
       // int port = Integer.parseInt(setServerPort());

       Thread t = new ChatServer(port);
       t.start();
    } catch(IOException e) {
       System.out.println("Usage: java ChatServer <port no.>");
    } catch(ArrayIndexOutOfBoundsException e) {
       System.out.println("Usage: java ChatServer <port no.> ");
    }
  }
}

