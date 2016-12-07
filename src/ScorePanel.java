import java.awt.Graphics2D;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.RenderingHints;
import javax.swing.border.Border;
import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.FontMetrics;

class ScorePanel extends JPanel {

  private final static long serialVersionUID = 1L;

  private String score = "0";
  private String name = "";

  private int rightEdge;
  private int yValue;
  private FontMetrics fontMetrics;

  ScorePanel(){
    setOpaque(false);
    setBackground(Palette.CREAM_CHEESE);
  }

  @Override
  public void paintComponent(Graphics g) {
    super.paintComponent(g);

    Graphics2D g2 = (Graphics2D) g;

    fontMetrics = g2.getFontMetrics();

    g2.setColor(ScoreFont.color);
    g2.setFont(ScoreFont.font);

    // slow if turned on
    g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

    rightEdge = (getWidth()-fontMetrics.stringWidth(score))-(ScoreFont.padding*2)-5;

    yValue = Math.round(ScoreFont.size)+(ScoreFont.padding*2);

    // left align
    g2.drawString(name, ScoreFont.padding, yValue);

    // right align
    g2.drawString(score, rightEdge, yValue);


  }

  public void updateScore(int score){
   // this.score = String.valueOf(score);
    this.score = String.valueOf(99);
    repaint();
  }

  public void setAlias(String name){
    this.name = name;
    repaint();
  }

}

/*
 * sources:
 *
 * drawing right aligned strings
 * http://stackoverflow.com/questions/2168963/use-java-drawstring-to-achieve-the-following-text-alignment/29675516#29675516
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
