import javax.swing.JOptionPane;

public class Client{
  protected String serverAddress;
  protected String clientAddress;

  protected String name;

  protected int chatPort;
  protected int gamePort;

  Client(){
    chatPort = Server.chatPort;
    gamePort = GameServerBroadcaster.getGamePort();
    serverAddress = setServerAddress();
    clientAddress = setClientAddress();
  }

  public String getAlias(){
    return this.name;
  }

  protected void setClientAlias() {
    String userName = "";
    while(userName.isEmpty()){
      userName = JOptionPane.showInputDialog(null,
                                             "Choose your alias:",
                                             "Alias selection",
                                             JOptionPane.PLAIN_MESSAGE);
    }

    this.name = userName;

  }

  private String setClientAddress() {
    String ip = "";
    while(ip.isEmpty()){
      ip = JOptionPane.showInputDialog(null,
                                               "Enter Your IP Address:",
                                               "Welcome to Draw My Thing",
                                               JOptionPane.QUESTION_MESSAGE);
    }

    return ip;
  }

  private String setServerAddress() {
    String serverAddress = "";
    while(serverAddress.isEmpty()){
      serverAddress = JOptionPane.showInputDialog(
                              null,
                              "Enter Server's IP Address:",
                              "Welcome to Draw My Thing",
                              JOptionPane.QUESTION_MESSAGE);
    }

    return serverAddress;
  }
}
