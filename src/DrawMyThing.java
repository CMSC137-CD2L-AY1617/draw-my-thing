import java.awt.Dimension;
import javax.swing.BorderFactory;
import javax.swing.JFrame;
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

  private JPanel scorePanel = new JPanel();
  private JPanel gamePanel = new GamePanel();
  private ChatClient chatPanel = new ChatClient();

  private Thread t = new Thread(chatPanel);

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

    setVisible(true);
    t.start();

  }

  public static void main(String[] args){
    new DrawMyThing();
  }

}
