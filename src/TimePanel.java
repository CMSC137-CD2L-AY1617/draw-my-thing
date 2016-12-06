import java.awt.Dimension;
import javax.swing.JLabel;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.BorderFactory;
import javax.swing.SwingUtilities;

public class TimePanel extends JPanel{
  private final static long serialVersionUID = 1L;

  private JLabel time = new JLabel();
  private Timer timer;
  private final int BORDER_LEFT = 1;
  private final int BORDER_RIGHT = 1;
  private final int BORDER_TOP = 1;
  private final int BORDER_BOTTOM = 1;

  public TimePanel(){
    timer = new Timer(this);

    time.setFont(TimeFont.font);
    add(time);

    setBackground(Palette.CREAM_CHEESE);
  }

  public void startTime(){
    timer.startTimer();
  }

  public void displayStartTime(long time){
    update(time);
  }

  public void update(long dT){
    time.setText( // convert milliseconds into other forms
    String.format("%02d", (((dT/60000)%10000)%60))+":"+ // minutes
    String.format("%02d", (((dT/1000)%1000)%60))+":"+   // seconds
    String.format("%02d", ((dT)%100)));                 // milliseconds
  }

}

/*
 * sources:
 *
 * [CMSC 22] Bomberman game timer
 * originally modified from http://stackoverflow.com/questions/18926839/timer-stopwatch-gui
 *
 * padding text with zeroes
 * http://stackoverflow.com/questions/473282/how-can-i-pad-an-integers-with-zeros-on-the-left/473309#473309
 */
