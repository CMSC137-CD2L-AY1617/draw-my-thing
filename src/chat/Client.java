// package chat;

import java.net.*;
import java.io.*;
import java.util.*;

public class Client extends Thread implements Chat{
  String serverName;
  int port;
  String clientName;
  Socket client;

  ClientWriter writer;
  ClientReader reader;

  // String message;

  public Client(String[] args) throws IOException{
    // this.serverSocket = new ServerSocket(port);
    // serverSocket.setSoTimeout(this.timeout_ms);
    this.serverName = args[0]; //get IP address of server from first param

    this.port = Integer.parseInt(args[1]); //get port from second param

    this.clientName = args[2]; //get message from the third param

    // run();
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

    // while(true){
      try{
        this.client = connect();

        OutputStream outToServer = this.client.getOutputStream();
        DataOutputStream out = new DataOutputStream(outToServer);
        out.writeUTF(this.clientName);

        // check dupicate username
        if(this.client.getInputStream().read()<0){
          log(clientName+" already exists");
          this.client.close();
        }

        writer = new ClientWriter(this.client, this.clientName);
        reader = new ClientReader(this.client, this.clientName);

        while(true){
          writer.start();
          reader.start();
        }

        // constantly receive msg from server
        // while(true){
        //   read();
        // }

        // constantly send msg to server
        // while(true){
        //   write();
        // }

      }
      catch(IOException e){
        e.printStackTrace();
        System.out.println("Cannot find (or disconnected from) Server");
        // break;
      }
    // }

    // while(Server.isWaiting>0){
    //   waitChatStart();
    //   System.out.println(Server.isWaiting);
    // }

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

  // public void write(){
  //   try{
  //     Scanner sc = new Scanner(System.in);

  //     /* Send data to the ServerSocket */
  //     OutputStream outToServer = this.client.getOutputStream();
  //     DataOutputStream out = new DataOutputStream(outToServer);

  //     String msg  = sc.nextLine();

  //     log("flooding with: "+msg);
  //     out.writeUTF(msg);

  //   }
  //   catch(IOException e){
  //     e.printStackTrace();
  //     System.out.println("Cannot find (or disconnected from) Server");

  //   }
  // }

  // public void read(){
  //   try{
  //     /* Read data from the ClientSocket */
  //     DataInputStream in = new DataInputStream(this.client.getInputStream());
  //     String serverMsg = in.readUTF(); //readUTF waits for input

  //     log(serverMsg);

  //   }
  //   catch(IOException e){
  //     e.printStackTrace();
  //     System.out.println("Cannot find (or disconnected from) Server");

  //   }
  // }

  public void waitChatStart(){
    log(this.clientName+" is waiting");
  }
}
