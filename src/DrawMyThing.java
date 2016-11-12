import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.awt.Image;
import java.awt.Window;
import java.io.File;
import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

public class DrawMyThing extends JFrame {

  private static int CHAT_BORDER_TOP = 0;
  private static int CHAT_BORDER_LEFT = 10;
  private static int CHAT_BORDER_BOTTOM = 0;
  private static int CHAT_BORDER_RIGHT = 0;

  private static int GAME_BORDER_TOP = 0;
  private static int GAME_BORDER_LEFT = 10;
  private static int GAME_BORDER_BOTTOM = 0;
  private static int GAME_BORDER_RIGHT = 10;

  private static int SCORE_BORDER_TOP = 0;
  private static int SCORE_BORDER_LEFT = 0;
  private static int SCORE_BORDER_BOTTOM = 0;
  private static int SCORE_BORDER_RIGHT = 10;

  private static final int WINDOW_HEIGHT = 600;
  private static final int WINDOW_WIDTH = 1200;
  private static final int WINDOW_PROPORTION = WINDOW_WIDTH/3;
  private static final int SIDE_PANEL_SIZE = (int)(WINDOW_PROPORTION - WINDOW_PROPORTION*0.35);
  private static final int GAME_AREA_SIZE = WINDOW_WIDTH - (2*SIDE_PANEL_SIZE);

  private PlayerState playerState = PlayerState.READY;
  private GameState gameState = GameState.WAITING;

  private JPanel scorePanel = new JPanel();
  private GamePanel gamePanel = new GamePanel();
  private ChatClient chatPanel = new ChatClient();

  private Thread t = new Thread(chatPanel);

  private BufferedImage splash_screen;
  private File splash_file = new File("../assets/screens/splash.png");

  private JFrame frame = new JFrame("Play Draw My Thing");
  private JPanel panel = new JPanel();
  private ImageIcon icon = new ImageIcon();
  private JLabel label = new JLabel();


  DrawMyThing() {

    setTitle("[Client] Draw My Thing");

    chatPanel.setBackground(Palette.EGG_CREAM);
    chatPanel.setBorder(BorderFactory.createEmptyBorder(CHAT_BORDER_TOP, CHAT_BORDER_LEFT, CHAT_BORDER_BOTTOM, CHAT_BORDER_RIGHT));
    chatPanel.setPreferredSize(new Dimension(SIDE_PANEL_SIZE,WINDOW_HEIGHT));

    gamePanel.setBackground(Palette.CHEESE_GREASE);
    gamePanel.setPreferredSize(new Dimension(GAME_AREA_SIZE,WINDOW_HEIGHT));
    gamePanel.setBorder(BorderFactory.createEmptyBorder(GAME_BORDER_TOP, GAME_BORDER_LEFT, GAME_BORDER_BOTTOM, GAME_BORDER_RIGHT));

    scorePanel.setBackground(Palette.CREAM_CHEESE);
    scorePanel.setBorder(BorderFactory.createEmptyBorder(SCORE_BORDER_TOP, SCORE_BORDER_LEFT, SCORE_BORDER_BOTTOM, SCORE_BORDER_RIGHT));
    scorePanel.setPreferredSize(new Dimension(SIDE_PANEL_SIZE,WINDOW_HEIGHT));

    getContentPane().add(chatPanel, "East");
    getContentPane().add(scorePanel, "West");
    getContentPane().add(gamePanel, "Center");

    setSize(new Dimension(WINDOW_WIDTH,WINDOW_HEIGHT));
    setResizable(false);
    pack();

    setLocationRelativeTo(null);
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    // set focus where user can type message
    requestFocus();
    chatPanel.focusTextArea();

    // setVisible(true);
    // t.start();

    gamePanel.renderWordFromCategory("easy");

    initializeFiles();
    showSplash();

    addWindowListener(new WindowAdapter() {
    @Override
      public void windowClosing(WindowEvent windowEvent) {
        frame.dispose();
        setVisible(false);
      }
    });

  }

  public void showSplash(){
    Object[] options =
      {"Sure!",
      "No, thanks."};

    label.setIcon(icon);
    panel.add(label);
    frame.setContentPane(panel);

    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.setPreferredSize(new Dimension(500, 500));
    frame.setResizable(false);
    frame.pack();
    frame.setLocationRelativeTo(null);
    frame.setVisible(true);

    JOptionPane optionPane = new JOptionPane("Would you like to try?",JOptionPane.QUESTION_MESSAGE,JOptionPane.YES_NO_OPTION,null,options,options[0]);

    int n = optionPane.showOptionDialog(
          null,
          "Would you like to try?",
          "A Silly Question",
          JOptionPane.YES_NO_OPTION,
          JOptionPane.QUESTION_MESSAGE,
          null,
          options,
          options[0]);

    if( n==JOptionPane.YES_OPTION ){
      frame.setVisible(false);
      setVisible(true);
      t.start();
      gameState = GameState.INGAME;
      startGame();
    }
  }

  private void startGame(){

    playerState = PlayerState.DRAWING;

    if(playerState == PlayerState.DRAWING){
      chatPanel.disableChat();
    }

  }

  private void initializeFiles(){
    try{
      //picture reading
      splash_screen = ImageIO.read(splash_file);
      icon = new ImageIcon((Image)splash_screen);

    }catch(Exception e){
      System.out.println();
      e.printStackTrace();
      System.exit(-1);
    }

  }

  public static void main(String[] args){
    new DrawMyThing();
  }

}
