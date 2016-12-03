import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;
import javax.swing.colorchooser.AbstractColorChooserPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.JColorChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

public class GamePanel extends JPanel implements ChangeListener {

  private JColorChooser colorChooser;
  private DrawPanel drawPanel = new DrawPanel();
  private TextPanel textPanel = new TextPanel();
  private RandomWordGenerator generator = new RandomWordGenerator();
  private GameClient client;

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

    textPanel.setPreferredSize(new Dimension(100, 100));

    add(textPanel, BorderLayout.NORTH);
    add(colorChooser, BorderLayout.SOUTH);
    add(drawPanel, BorderLayout.CENTER);

    SwingUtilities.invokeLater(new Runnable() {
        public void run() {
          setVisible(true);
        }
    });

  }

  public void enableDrawing(){
    drawPanel.enableDrawPanel();
  }

  public void disableDrawing(){
    drawPanel.disableDrawPanel();
  }

  public void stateChanged(ChangeEvent e) {
    drawPanel.setSelectedColor(colorChooser.getColor());
  }

  public String getDrawUpdate(){
    return drawPanel.getDrawUpdate();
  }

  public void setUpdateInstance(GameClient client){
    this.client = client;
    drawPanel.setUpdateInstance(this.client);
  }

  public void doMousePress(Point p){
    drawPanel.doMousePress(p);
  }

  public void doMouseRelease(Point p){
    drawPanel.doMouseRelease(p);
  }

  public void doMouseDrag(Point p){
    drawPanel.doMouseDrag(p);
  }

  public void setDrawTools(Geometry tool, Color c){
    drawPanel.setDrawTools(tool, c);
  }

  public String renderWordFromCategory(String category){
    String word = generator.getWordFromCategory(category);

    if(!generateSuccess(word)){
      JOptionPane.showMessageDialog(null,"Sorry, no words exist under '"+category+"' category");
    }

    textPanel.renderText(word);

    return word;
  }

  public void updateTextPanel(String word){
    textPanel.renderText(word);
  }

  private boolean generateSuccess(String test){
    return ( test != null && !test.isEmpty() );
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
