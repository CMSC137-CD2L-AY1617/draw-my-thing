// package chat;

import java.net.*;
import java.io.*;
import java.util.*;

public class Server extends Thread implements Chat {
  public static int playerCount = 0;
  public static int maxPlayers;
  private ServerSocket serverSocket;
  private int timeout_ms = 86400000;
  public HashMap<String, Socket> serverMap = new HashMap<String, Socket>();
  private HashMap<String, ServerReader> serverReader = new HashMap<String, ServerReader>();

  public Server(int port, int maxPlayers) throws IOException{
    this.serverSocket = new ServerSocket(port);
    this.maxPlayers = maxPlayers;
    serverSocket.setSoTimeout(this.timeout_ms);
  }

  public static void main(String[] args){
    try{
      int port = Integer.parseInt(args[0]);
      int maxPlayers = Integer.parseInt(args[1]);
      Thread t = new Server(port, maxPlayers);
      t.start();
    }
    catch(IOException e){
      //e.printStackTrace();
      System.out.println("Usage: java Server <port no.>\n"+
                         "Make sure to use valid ports (greater than 1023)");
    }
    catch(ArrayIndexOutOfBoundsException e){
      //e.printStackTrace();
      System.out.println("Usage: java Server <port no.> <number of players>\nInsufficient arguments given.");
    }

  }

  public void run(){

    // wait for players to connect to chat
    while(playerCount < maxPlayers){
      try{
        log("open on port " + serverSocket.getLocalPort() + "...");

        /* Start accepting data from the ServerSocket */
        Socket server = connect();

        /* Read data from the ClientSocket */
        DataInputStream in = new DataInputStream(server.getInputStream());
        String clientName = in.readUTF(); //readUTF waits for input

        if(serverMap.containsKey(clientName)){
          log(clientName+" already exists");
          server.close();
          continue;
        }

        log("received: "+clientName);

        serverMap.put(clientName, server);


        // broadcast to chat members of new chat member
        // log(clientName+" joined the chat");
        // broadcast(clientName+" joined the chat");

        ServerReader sReader = new ServerReader(server, clientName);
        serverReader.put(clientName, sReader);

        // constantly listen to a specific client
        // while(true){
        //   read();
        // }

        // server.close();

        // log("ended the connection to "+ server.getRemoteSocketAddress());

        playerCount = playerCount + 1;
        log("playerCount: "+playerCount);
      }
      catch(SocketTimeoutException e){
        log("Socket timed out!");
        break;
      }
      catch(IOException e){
        e.printStackTrace();
        log("Input/Output Error!");
        break;
      }
    }

    log("all chat members have successfully connected");
    startChat();
  }

  public Socket connect(){
    Socket s = null;

    try{
      s = serverSocket.accept();
      log("client connected to " + s.getRemoteSocketAddress());
    }
    catch(SocketTimeoutException so){
      log("Socket timed out!");
    }
    catch(IOException e){
      e.printStackTrace();
      log("Input/Output Error!");
    }

    return s;
  }

  public void log(String msg){
    System.out.println("[server log] : "+msg);
  }

  public void broadcast(String broadcastMessage){
    //send broadcast message
    try{
      for(Iterator iter=serverMap.keySet().iterator(); iter.hasNext(); ){

        String name = (String)iter.next();
        // log("send data to"+name);

        Socket client = (Socket)serverMap.get(name);
        // log(client.toString());

        /* Send data to the ServerSocket */
        OutputStream outToServer = client.getOutputStream();
        DataOutputStream out = new DataOutputStream(outToServer);

        // log(out.toString());

        out.writeUTF(broadcastMessage);
        log("sent '"+broadcastMessage+"' to "+name);
        log("broadcasting: "+broadcastMessage);
      }

    }
    catch(IOException e){
      e.printStackTrace();
      log("Input/Output Error!");
    }

  }

  // public void read(){
  //   try{
  //     String client = "user3";
  //     /* Read data from the ClientSocket */
  //     DataInputStream in = new DataInputStream(this.serverMap.get(client).getInputStream());
  //     String clientMsg = in.readUTF(); //readUTF waits for input

  //     log(client+": "+clientMsg);

  //   }
  //   catch(IOException e){
  //     e.printStackTrace();
  //     System.out.println("Cannot find (or disconnected from) Server");

  //   }
  // }

  public void startChat(){
    // try{

    // for(String name: serverReader.keySet()){

    //   ServerReader client = (ServerReader)serverReader.get(name);

    //   // start threads that will listen to a specific thread
    //   log(name+" joined the chat");

    //   client.serverMap = serverMap;

    //   broadcast(name+" joined the chat");

    //   client.start();

    // }

      for(Iterator iter=serverReader.keySet().iterator(); iter.hasNext(); ){

        String name = (String)iter.next();

        ServerReader client = (ServerReader)serverReader.get(name);

        /* Send data to the ServerSocket */
        // OutputStream outToServer = client.getOutputStream();
        // DataOutputStream out = new DataOutputStream(outToServer);

        // out.writeUTF(broadcastMessage);

        // start threads that will listen to a specific thread

        log(name+" joined the chat");

        client.serverMap = serverMap;

        client.start();
        broadcast("server: "+name+" joined the chat");

      }

    // Server.isWaiting = 0;


    // }
    // catch(IOException e){
    //   e.printStackTrace();
    //   log("Input/Output Error!");
    // }

      // while(Server.maxPlayers>0);
  }


}
