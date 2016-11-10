import java.awt.BorderLayout;
import java.awt.Color;
import javax.swing.colorchooser.AbstractColorChooserPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.JColorChooser;
import javax.swing.JPanel;

public class GamePanel extends JPanel implements ChangeListener {

  private JColorChooser colorChooser;
  private DrawPanel drawPanel = new DrawPanel();
  private GameState gameState = GameState.WAITING;

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
