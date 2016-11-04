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
    // start();
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
        // String client = "user3";
        /* Read data from the ClientSocket */
        // DataInputStream in = new DataInputStream(this.client.getInputStream());
        while(true){

          log("waiting for client msg");
          String clientMsg = inputFromClient.readUTF(); //readUTF waits for input

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
    // Object[] clientNames = serverMap.keySet().toArray();

    // System.out.println(serverMap.toString());
    // System.exit(-1);

    try{
      for(Iterator iter=serverMap.keySet().iterator(); iter.hasNext(); ){
      // for(int i=0; i<clientNames.length; i++){

        String name = (String)iter.next();
        // String name = clientNames[i].toString();
        // log(name);

        Socket client = (Socket)serverMap.get(name);
        // log(client.toString());

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
