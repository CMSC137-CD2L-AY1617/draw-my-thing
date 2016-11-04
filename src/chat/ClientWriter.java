// package chat;

import java.net.*;
import java.io.*;
import java.util.*;

public class ClientWriter extends Thread implements Writer {
  Socket server;
  DataOutputStream outputToServer;
  Scanner scanner;
  String serverName;


  ClientWriter(Socket server, String name) throws IOException{
    this.server = server;
    this.outputToServer = new DataOutputStream(server.getOutputStream());
    this.scanner = new Scanner(System.in);
    this.serverName = name;
  }

  public void start(){
    write();
  }

  public void log(String msg){
    System.out.println("[client-writer log] : "+msg);
  }

  public void write(){
    // while(true){
      try{
        System.out.print("> ");
        String msg  = scanner.nextLine();

        log("sent: "+msg);

        outputToServer.writeUTF(msg);

      }
      catch(IOException e){
        // e.printStackTrace();
        System.out.println("Cannot find (or disconnected from) Server");
        // break;
      }
    // }
  }
}
