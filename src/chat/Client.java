// package chat;

import java.net.*;
import java.io.*;
import java.util.*;

public class Client extends Thread implements Chat, Writer, Reader {
  String serverName;
  int port;
  String clientName;
  Socket client;
  // String message;

  public Client(String[] args) throws IOException{
    // this.serverSocket = new ServerSocket(port);
    // serverSocket.setSoTimeout(this.timeout_ms);
    this.serverName = args[0]; //get IP address of server from first param

    this.port = Integer.parseInt(args[1]); //get port from second param

    this.clientName = args[2]; //get message from the third param

    // this.message = args[3]; //get message from the fourth param

    run();
  }

  public static void main(String [] args){
    try{
        Thread c = new Client(args);
        c.start();
    }
    catch(IOException e){
      //e.printStackTrace();
        System.out.println("Usage: java Client <server ip> <port no.> 'preferred nickname' <your message to the server>'");
    }
    catch(ArrayIndexOutOfBoundsException e){
        // System.out.println("Usage: java Client <server ip> <port no.> 'preferred nickname' <your message to the server>'");
        System.out.println("Usage: java Client <server ip> <port no.> 'preferred nickname'");

    }
  }

  public void start(){
    Socket client = null;

    try{
      this.client = connect();

      OutputStream outToServer = this.client.getOutputStream();
      DataOutputStream out = new DataOutputStream(outToServer);
      out.writeUTF(this.clientName);

      // partially working
      // while(true){
      //   read();
      // }


      while(true){
        // write();
        read();
      }

      // /* Receive data from the ServerSocket */
      // InputStream inFromServer = client.getInputStream();
      // DataInputStream in = new DataInputStream(inFromServer);
      // System.out.println("Server says " + in.readUTF());
      // client.close();
    }
    catch(IOException e){
      e.printStackTrace();
      System.out.println("Cannot find (or disconnected from) Server");

    }
  }

  public Socket connect(){
    Socket c = null;

    try{
      /* Open a ClientSocket and connect to ServerSocket */
      log("connecting to " + this.serverName + " on port " + this.port);

      c = new Socket(this.serverName, this.port);

      log("connected to " + c.getRemoteSocketAddress());

    }
    catch(IOException e){
      e.printStackTrace();
      System.out.println("Cannot find (or disconnected from) Server");

    }

    return c;
  }

  public void log(String msg){
    System.out.println("[client log] : "+msg);
  }

  public void write(){
    try{
      Scanner sc = new Scanner(System.in);

      /* Send data to the ServerSocket */
      OutputStream outToServer = this.client.getOutputStream();
      DataOutputStream out = new DataOutputStream(outToServer);

      String msg  = sc.nextLine();

      // log(msg);

      out.writeUTF(msg);

    }
    catch(IOException e){
      e.printStackTrace();
      System.out.println("Cannot find (or disconnected from) Server");

    }
  }

  public void read(){
    try{
      /* Read data from the ClientSocket */
      DataInputStream in = new DataInputStream(this.client.getInputStream());
      String serverMsg = in.readUTF(); //readUTF waits for input

      log(serverMsg);

    }
    catch(IOException e){
      e.printStackTrace();
      System.out.println("Cannot find (or disconnected from) Server");

    }
  }
}
