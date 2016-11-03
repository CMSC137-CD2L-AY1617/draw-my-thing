// package chat;

import java.net.*;
import java.io.*;
import java.util.*;

public class Server extends Thread implements Chat, Reader {
  private ServerSocket serverSocket;
  private int timeout_ms = 86400000;
  private HashMap<String, Socket> serverMap = new HashMap<String, Socket>();

  public Server(int port)throws IOException{
    this.serverSocket = new ServerSocket(port);
    serverSocket.setSoTimeout(this.timeout_ms);
  }

  public static void main(String[] args){
    try{
      int port = Integer.parseInt(args[0]);
      Thread t = new Server(port);
      t.start();
    }
    catch(IOException e){
      //e.printStackTrace();
      System.out.println("Usage: java Server <port no.>\n"+
                         "Make sure to use valid ports (greater than 1023)");
    }
    catch(ArrayIndexOutOfBoundsException e){
      //e.printStackTrace();
      System.out.println("Usage: java Server <port no.>\n"+
                         "Insufficient arguments given.");
    }

  }

  public void run(){
    while(true){
      try{
        log("open on port " + serverSocket.getLocalPort() + "...");

        /* Start accepting data from the ServerSocket */
        Socket server = connect();

        /* Read data from the ClientSocket */
        DataInputStream in = new DataInputStream(server.getInputStream());
        String clientName = in.readUTF(); //readUTF waits for input

        log("received: "+clientName);

        serverMap.put(clientName, server);

        // broadcast to chat members of new chat member
        broadcast(clientName);

        // server.close();

        // log("ended the connection to "+ server.getRemoteSocketAddress());
      }
      catch(SocketTimeoutException s){
        log("Socket timed out!");
        break;
      }
      catch(IOException e){
        e.printStackTrace();
        log("Input/Output Error!");
        break;
      }
    }
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

  public void broadcast(String newcomer){
    // String retval ="";

    try{
      for(Iterator iter=serverMap.keySet().iterator(); iter.hasNext(); ){

        String name = (String)iter.next();
        log(name);

        Socket client = (Socket)serverMap.get(name);
        // log(client.toString());

        /* Send data to the ServerSocket */
        OutputStream outToServer = client.getOutputStream();
        DataOutputStream out = new DataOutputStream(outToServer);

        out.writeUTF(newcomer+" joined the chat");
      }

    }
    catch(IOException e){
      e.printStackTrace();
      log("Input/Output Error!");
    }

  }

  public void read(){
    try{
      /* Read data from the ClientSocket */
      DataInputStream in = new DataInputStream(this.serverMap.get(0).getInputStream());
      // String clientName = in.readUTF(); //readUTF waits for input

    }
    catch(IOException e){
      e.printStackTrace();
      System.out.println("Cannot find (or disconnected from) Server");

    }
  }

}
