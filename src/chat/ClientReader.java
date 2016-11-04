// package chat;

import java.net.*;
import java.io.*;
import java.util.*;

public class ClientReader extends Thread implements Reader {
  Socket server;
  DataInputStream inputFromServer;
  String name;

  ClientReader(Socket server, String name) throws IOException{
    this.server = server;
    this.inputFromServer = new DataInputStream(server.getInputStream());
    this.name = name;
  }

  public void start(){
    read();
  }

  public void log(String msg){
    System.out.println("[client-reader log] : "+msg);
    // System.out.println(msg);
  }

  public void read(){
    // while(true){
      try{
        log("waiting for server msg");

        /* Read data from the ClientSocket */
        String serverMsg = this.inputFromServer.readUTF(); //readUTF waits for input

        System.out.println(serverMsg);
        log(serverMsg);

      }
      catch(IOException e){
        // e.printStackTrace();
        System.out.println("Cannot find (or disconnected from) Server");
        // break;
        System.exit(-1);
      }
    // }
  }
}
