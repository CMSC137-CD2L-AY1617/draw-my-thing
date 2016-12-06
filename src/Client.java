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
    // name = getAlias();
    // System.out.println(Server.maxPlayers);
  }

  public String getAlias(){
    return this.name;
  }

  protected void setClientAlias() {//throws IOException {
    String userName = "";
    while(userName.isEmpty()){
      userName = JOptionPane.showInputDialog(null,
                                             "Choose your alias:",
                                             "Alias selection",
                                             JOptionPane.PLAIN_MESSAGE);
    }

    // return userName;
    this.name = userName;

  }

  private String setClientAddress() {//throws IOException {
    String ip = "";
    while(ip.isEmpty()){
      ip = JOptionPane.showInputDialog(null,
                                               "Enter Your IP Address:",
                                               "Welcome to Draw My Thing",
                                               JOptionPane.QUESTION_MESSAGE);
    }

    return ip;
  }

  private String setServerAddress() {//throws IOException {
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
