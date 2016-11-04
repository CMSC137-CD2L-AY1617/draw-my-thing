// package chat;

import java.net.*;
import java.io.*;
import java.util.*;

public class ServerReader extends Thread implements Reader {
  private Socket client;
  private String clientName;
  private DataInputStream inputFromClient;
  public HashMap<String, Socket> serverMap = new HashMap<String, Socket>();
  String name;


  ServerReader(Socket client, String name) throws IOException{
    this.client = client;
    this.clientName = name;
    this.inputFromClient = new DataInputStream(client.getInputStream());
  }

  public void start(){
    read();
  }

  public void log(String msg){
    System.out.println("[server-reader log] : "+msg);
  }

  public void read(){
    while(true){
      try{
        while(true){
          log("waiting for client msg");
          //readUTF waits for input
          String clientMsg = inputFromClient.readUTF();

          log(this.clientName+": "+clientMsg);
          broadcast(this.clientName+": "+clientMsg);
        }

      }
      catch(IOException e){
        // e.printStackTrace();
        System.out.println("Cannot find (or disconnected from) Server");
        System.exit(-1);
      }
    }
  }

  public void broadcast(String broadcastMessage){
    //send broadcast message

    try{
      for(Iterator iter=serverMap.keySet().iterator(); iter.hasNext(); ){

        String name = (String)iter.next();

        Socket client = (Socket)serverMap.get(name);

        /* Send data to the ServerSocket */
        OutputStream outToServer = client.getOutputStream();
        DataOutputStream out = new DataOutputStream(outToServer);

        log("sending "+broadcastMessage);
        out.writeUTF(broadcastMessage);
      }

    }
    catch(IOException e){
      // e.printStackTrace();
      log("Input/Output Error!");
    }

  }

}
