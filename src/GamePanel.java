import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import java.util.ArrayList;
import java.awt.Shape;
import java.awt.geom.Arc2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.awt.Graphics2D;
import java.awt.Graphics;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.colorchooser.*;

public class GamePanel extends JPanel implements ColorPalette, ChangeListener {

  protected JColorChooser tcc;
  protected JLabel banner;

  Draw d = new Draw();


  private static final int WINDOW_HEIGHT = 600;
  private static final int WINDOW_WIDTH = 1200;
  private static final int WINDOW_PROPORTION = WINDOW_WIDTH/3;
  private static final int SIDE_PANEL_SIZE = (int)(WINDOW_PROPORTION - WINDOW_PROPORTION*0.35);
  private static final int GAME_AREA_SIZE = WINDOW_WIDTH - (2*SIDE_PANEL_SIZE);

  private GameState gameState = GameState.WAITING;

  private JFrame frame = new JFrame("[Client] Draw My Thing");

  public GamePanel(){
    super(new BorderLayout());

    //Set up the banner at the top of the window
    // banner = new JLabel("Welcome to the Tutorial Zone!",
    //                     JLabel.CENTER);
    // banner.setForeground(Color.yellow);
    // banner.setBackground(Color.blue);
    // banner.setOpaque(true);
    // banner.setFont(new Font("SansSerif", Font.BOLD, 24));
    // banner.setPreferredSize(new Dimension(100, 65));

    JPanel bannerPanel = new JPanel(new BorderLayout());
    // bannerPanel.add(banner, BorderLayout.CENTER);
    // bannerPanel.setBorder(BorderFactory.createTitledBorder("Banner"));

    //Set up color chooser for setting text color
    tcc = new JColorChooser(Color.WHITE);
    tcc.getSelectionModel().addChangeListener(this);
    // tcc.setBorder(BorderFactory.createTitledBorder("Choose Text Color"));

    // tcc.removeChooserPanel("HSV");

    AbstractColorChooserPanel[] cc = tcc.getChooserPanels();

    for(AbstractColorChooserPanel c : cc){
      String clsName = c.getClass().getName();
      // System.out.println(clsName);

      if (clsName.equals("javax.swing.colorchooser.DefaultSwatchChooserPanel")) {
        // tcc.removeChooserPanel(oldPanels[i]);
        // System.out.println(0);
      }
      else {
        tcc.removeChooserPanel(c);
        // System.out.println(2);
      }
    }

    tcc.setPreviewPanel(new JPanel());

    // add(bannerPanel, BorderLayout.CENTER);
    add(tcc, BorderLayout.SOUTH);
    add(d, BorderLayout.CENTER);

    // JFrame frame = new JFrame("ColorChooserDemo");
    // frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    //Create and set up the content pane.
    // JComponent newContentPane = new ColorChooserDemo();
    // this.setOpaque(true); //content panes must be opaque

    // frame.setContentPane(this);

    // //Display the window.
    // frame.pack();
    // frame.setVisible(true);

    // this.setBackground(ColorPalette.PEACH);

    // this.setPreferredSize(new Dimension(GAME_AREA_SIZE,WINDOW_HEIGHT));


    // frame.getContentPane().add(new GamePanel(), "Center");

    // frame.setSize(new Dimension(WINDOW_WIDTH,WINDOW_HEIGHT));
    // frame.setResizable(false);
    // frame.pack();

    // frame.setLocationRelativeTo(null);
    // frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    // // this.addMouseMotionListener(this);

    // frame.setVisible(true);

  }

  public void stateChanged(ChangeEvent e) {
      Color newColor = tcc.getColor();
      // banner.setForeground(newColor);
      d.setSelectedColor(newColor);
      // System.out.println("bang");
  }

  public static void main(String[] args){

    // shapes.add(line);
    // shapes.add(oval);
    // shapes.add(rectangle);
    // shapes.add(roundRectangle);

    // Shape[] shapes = {line, oval, rectangle, roundRectangle};

    GamePanel g = new GamePanel();
    // Draw d = new Draw();


  }

}

/*
 * sources:
 *
 * on removing extra color pickers
 * http://www.java2s.com/Tutorial/Java/0240__Swing/RemovingaColorChooserPanelfromaJColorChooserDialog.htm
 *
 * on removing the preview panel of jcolorchooser
 * http://www.java2s.com/Tutorial/Java/0240__Swing/RemovingthePreviewPanelfromaJColorChooserDialog.htm
 */
