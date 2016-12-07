import javax.swing.JFrame;
import javax.swing.JOptionPane;

public class Timer implements Runnable {
  private Thread runThread;
  private boolean running = false;
  private boolean paused = false;
  private TimePanel timePanel;
  private long summedTime = Timeout.EASY;
  private DrawMyThing game;

  public Timer(TimePanel timePanel) {
    this.timePanel = timePanel;
  }

  public void startTimer() {      // start the thread up
    running = true;
    paused = false;
    runThread = new Thread(this);
    runThread.start();
  }

  public void pauseTimer() {      // just pause it
    paused = true;
    running = false;
  }

  public void stopTimer() {       // completely stop the timer
    running = false;
    paused = false;
  }

  public void setGameInstance(DrawMyThing game){
    this.game = game;
  }

  @Override public void run() {
    long startTime = System.currentTimeMillis(); // keep showing the difference in time until we are either paused or not running anymore
    long summedTimeCopy = 0;


    if(paused){
      summedTime -= System.currentTimeMillis();
      summedTimeCopy= summedTime - (System.currentTimeMillis() - startTime) ;
    }
    else{
      while(running) {
        timePanel.update(summedTime - (System.currentTimeMillis() - startTime));

        if( summedTime - (System.currentTimeMillis() - startTime) < 0 ){
          JOptionPane.showMessageDialog(null,"Time's up!");
          break;
        }
      }
    }
  }
}

/*
 * sources:
 *
 * [CMSC 22] Bomberman timer
 * originally modified from http://stackoverflow.com/questions/18926839/timer-stopwatch-gui
 */
