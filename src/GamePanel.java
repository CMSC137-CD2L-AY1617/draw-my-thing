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

  private JColorChooser colorChooser;
  private DrawPanel drawPanel = new DrawPanel();
  private GameState gameState = GameState.WAITING;

  // private JFrame frame = new JFrame("[Client] Draw My Thing");

  public GamePanel(){

    super(new BorderLayout());

    //Set up color chooser
    colorChooser = new JColorChooser(Color.WHITE);
    colorChooser.getSelectionModel().addChangeListener(this);

    // remove extra default color choosers : HSV, HSL, RGB, CMYK
    AbstractColorChooserPanel[] cc = colorChooser.getChooserPanels();

    for(AbstractColorChooserPanel c : cc){
      String className = c.getClass().getName();

      if(!className.contains("javax.swing.colorchooser.DefaultSwatchChooserPanel")) {
        colorChooser.removeChooserPanel(c);
      }
    }

    // remove built in preview panel
    colorChooser.setPreviewPanel(new JPanel());

    add(colorChooser, BorderLayout.SOUTH);
    add(drawPanel, BorderLayout.CENTER);

  }

  public void stateChanged(ChangeEvent e) {

      Color newColor = colorChooser.getColor();
      drawPanel.setSelectedColor(newColor);

  }

}

/*
 * sources:
 *
 * on removing extra color pickers
 * http://www.java2s.com/Tutorial/Java/0240__Swing/RemovingaColorChooserPanelfromaJColorChooserDialog.htm
 *
 * on removing the preview panel of jcolorchooser
 * https://docs.oracle.com/javase/tutorial/uiswing/components/colorchooser.html#previewpanel
 * http://www.java2s.com/Tutorial/Java/0240__Swing/RemovingthePreviewPanelfromaJColorChooserDialog.htm
 */
