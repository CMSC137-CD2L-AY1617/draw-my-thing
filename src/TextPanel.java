import java.awt.RenderingHints;
import java.awt.Graphics;
import java.awt.Graphics2D;
import javax.swing.JPanel;

class TextPanel extends JPanel {

  private final static long serialVersionUID = 1L;

  private String custom_text = "";

  TextPanel(){
    setBackground(Palette.CHALKBOARD_GREEN);
  }

  @Override
  public void paintComponent(Graphics g) {
    super.paintComponent(g);

    Graphics2D g2 = (Graphics2D) g;

    g2.setColor(GameFont.color);
    g2.setFont(GameFont.font);

    //slow if turned on
    g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

    g2.drawString(custom_text, 5, GameFont.size+5);

  }

  public void renderText(String text){

    custom_text = text;
    repaint();

  }

}

/*
 * sources:
 *
 * [CMSC 170] unused custom text panel for TicTacToe
 *
 * on custom font:
 *
 * answered Jan 1 at 16:58 Tiffany Tran
 * http://stackoverflow.com/questions/16761630/font-createfont-set-color-and-size-java-awt-font/34558081#34558081
 *
 * answered Mar 12 '14 at 14:44 peeskillet
 * http://stackoverflow.com/questions/22354651/cant-print-any-string-using-drawstring-in-jframe/22355052#22355052
 *
 */
