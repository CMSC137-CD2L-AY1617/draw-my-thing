import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.image.BufferedImage;
import java.awt.Image;
import java.awt.Window;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

public class DrawMyThing extends JFrame implements MouseListener {

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

  private Thread chatThread = new Thread(chatPanel);

  private BufferedImage splash_screen;
  private File splash_file = new File("../assets/screens/splash.png");

  private BufferedImage about_screen;
  private File about_file = new File("../assets/screens/about.png");

  private BufferedImage help_screen;
  private File help_file = new File("../assets/screens/help.jpg");

  private JFrame frame = new JFrame("[Client] Draw My Thing");
  private JPanel deck;

  private JPanel panel = new JPanel();
  private ImageIcon icon = new ImageIcon();
  private JLabel label = new JLabel();

  private Window mainFrame;
// =======
//   private static JFrame home = new JFrame("Home");
//   private static JFrame howToPlay = new JFrame("How to Play");
//   private static JPanel homePanel = new JPanel(new BorderLayout());
//   private static JPanel footer = new JPanel(new GridLayout(1,2));
//   private static JButton startGame = new JButton("start game");
//   private static JButton instructions = new JButton("how to play");


//   private Thread t = new Thread(chatPanel);
// >>>>>>> add start page

  DrawMyThing() {
    super("[Client] Draw My Thing");

    initialize();
  }

  private void initialize(){
    this.initializeFiles();
    this.initializeComponents();
    this.setListeners();
  }

  private void initializeComponents(){
    deck = new JPanel(){
      @Override
      public void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(splash_screen, 0, 0, this);
      }
    };

    chatPanel.setBackground(Palette.EGG_CREAM);
    chatPanel.setBorder(BorderFactory.createEmptyBorder(CHAT_BORDER_TOP, CHAT_BORDER_LEFT, CHAT_BORDER_BOTTOM, CHAT_BORDER_RIGHT));
    chatPanel.setPreferredSize(new Dimension(SIDE_PANEL_SIZE,WINDOW_HEIGHT));

    gamePanel.setBackground(Palette.CHEESE_GREASE);
    gamePanel.setPreferredSize(new Dimension(GAME_AREA_SIZE,WINDOW_HEIGHT));
    gamePanel.setBorder(BorderFactory.createEmptyBorder(GAME_BORDER_TOP, GAME_BORDER_LEFT, GAME_BORDER_BOTTOM, GAME_BORDER_RIGHT));

    scorePanel.setBackground(Palette.CREAM_CHEESE);
    scorePanel.setBorder(BorderFactory.createEmptyBorder(SCORE_BORDER_TOP, SCORE_BORDER_LEFT, SCORE_BORDER_BOTTOM, SCORE_BORDER_RIGHT));
    scorePanel.setPreferredSize(new Dimension(SIDE_PANEL_SIZE,WINDOW_HEIGHT));

    // game area panel
    getContentPane().add(chatPanel, "East");
    getContentPane().add(scorePanel, "West");
    getContentPane().add(gamePanel, "Center");

    setSize(new Dimension(WINDOW_WIDTH,WINDOW_HEIGHT));
    setResizable(false);
    pack();

    setLocationRelativeTo(null);

    // splash panel
    frame.setBackground(Color.WHITE);
    frame.setContentPane(deck);
    frame.setPreferredSize(new Dimension(500,530));
    frame.pack();
    frame.setDefaultCloseOperation(EXIT_ON_CLOSE);
    frame.setResizable(false);
    frame.setVisible(true);
    frame.setLocationRelativeTo(null);

    // set focus where user can type message
    requestFocus();
    chatPanel.focusTextArea();

    mainFrame = SwingUtilities.getWindowAncestor(deck);

  }

  public void startGame(){
    chatPanel.initializeChat();
    setVisible(true);
    chatThread.start();

    playerState = PlayerState.DRAWING;

    // default to easy category for now
    gamePanel.renderWordFromCategory("easy");

    if(playerState == PlayerState.DRAWING){
      chatPanel.disableChat();
    }

  }

  private void initializeFiles(){
    try{
      //picture reading
      splash_screen = ImageIO.read(splash_file);
      about_screen = ImageIO.read(about_file);
      help_screen = ImageIO.read(help_file);

    }catch(Exception e){
      System.out.println();
      e.printStackTrace();
      System.exit(-1);
    }

  }

  private void setListeners(){
    deck.addMouseListener(this);
    addWindowListener(new WindowAdapter() {
    @Override
      public void windowClosing(WindowEvent windowEvent) {
        dispose();
        frame.setVisible(true);
      }
    });
  }

  private void renderScreen(BufferedImage screen){
    deck.getGraphics().drawImage((Image)screen, 0,0, null);
  }

  @Override
  public void mouseClicked(MouseEvent e){
    if( e.getY()>=400 && e.getY()<=450 ){

      if( e.getX()>=190 && e.getX()<=300 ){
        if( gameState==GameState.WAITING ){
          frame.setVisible(false);
          gameState = GameState.INGAME;
          startGame();
        }
      }
      else if( e.getX()>=310 && e.getX()<=440 ){
        renderScreen(about_screen);
      }
      else if( e.getX()>=60 && e.getX()<=170 ){
        renderScreen(help_screen);
      }

    }
    if( e.getY()<=50 && e.getX()>=390 ){
      renderScreen(splash_screen);
      if( gameState==GameState.INGAME ){
        return;
      }
    }
  }

  @Override
  public void mouseEntered(MouseEvent e){}

  @Override
  public void mouseExited(MouseEvent e){}

  @Override
  public void mousePressed(MouseEvent e){}

  @Override
  public void mouseReleased(MouseEvent e){}

// =======
//   public static void initInstructionFrame(){
//     JPanel inst = new JPanel(new BorderLayout());
//     JLabel how= new JLabel(new ImageIcon("../assets/images/instructions-1.jpg"));

//     inst.add(how, BorderLayout.CENTER);
//     JButton leave = new JButton("back");
//     inst.add(leave, BorderLayout.PAGE_END);
//     howToPlay.add(inst);
//     howToPlay.setSize(750,550);

//     leave.addActionListener(new ActionListener() {
//       public void actionPerformed(ActionEvent e) {
//         howToPlay.setVisible(false);
//         home.setVisible(true);
//       }
//     });
//   }

// >>>>>>> add start page
  public static void main(String[] args){
//     JLabel logo= new JLabel(new ImageIcon("../assets/images/logo.png"));
//     homePanel.add(logo, BorderLayout.CENTER);
//     initInstructionFrame();

//     footer.add(startGame);
//     footer.add(instructions);
//     homePanel.add(footer, BorderLayout.PAGE_END);
//     home.add(homePanel);

//     instructions.addActionListener(new ActionListener() {
//       public void actionPerformed(ActionEvent e) {
//         home.setVisible(false);
//         howToPlay.setVisible(true);
//       }
//     });

//     startGame.addActionListener(new ActionListener() {
//       public void actionPerformed(ActionEvent e) {
//         home.setVisible(false);
          new DrawMyThing();
//       }
//     });

//     home.setSize(500,500);
//     home.setVisible(true);
  }

}
