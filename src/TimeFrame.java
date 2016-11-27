/*
 * modified from http://stackoverflow.com/questions/18926839/timer-stopwatch-gui
 */

import java.awt.Dimension;
import javax.swing.JLabel;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.BorderFactory;
import javax.swing.SwingUtilities;

public class TimeFrame extends JPanel{
	private JLabel time = new JLabel();
	private Timer timer;
	private final int BORDER_LEFT = 1;
	private final int BORDER_RIGHT = 1;
	private final int BORDER_TOP = 1;
	private final int BORDER_BOTTOM = 1;

	public TimeFrame(){
		timer = new Timer(this);
		time.setFont(time.getFont().deriveFont(30.0f));
		setBackground(Palette.CREAM_CHEESE);
		setBorder(BorderFactory.createEmptyBorder(BORDER_TOP, BORDER_LEFT, BORDER_BOTTOM, BORDER_RIGHT));
		add(time);

		SwingUtilities.invokeLater(new Runnable() {
      public void run() {
        setVisible(true);
        }
    });
	}

	public void startTime(){
		timer.startTimer();
	}

	public void update(long dT){ // convert milliseconds into other forms
		time.setText(
		String.format("%02d", (((dT/60000)%10000)%60))+":"+ // minutes
		String.format("%02d", (((dT/1000)%1000)%60))+":"+		// seconds
		String.format("%02d", ((dT)%100))); 								// milliseconds
	}

	public static void main(String[] args){
		JFrame f = new JFrame();
		TimeFrame t = new TimeFrame();
		f.add(t);
		f.setSize(new Dimension(200,50));
		f.setVisible(true);
		t.startTime();

	}

	/*
	 * padding text with zero
	 * http://stackoverflow.com/questions/473282/how-can-i-pad-an-integers-with-zeros-on-the-left/473309#473309
	 */

}
