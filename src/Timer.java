import javax.swing.JFrame;
import javax.swing.JOptionPane;

public class Timer implements Runnable {
  private Thread runThread;
  private boolean running = false;
  private boolean paused = false;
  private TimeFrame timeFrame;
  private long summedTime = Timeout.EASY;

  public Timer(TimeFrame timeFrame) {
    this.timeFrame = timeFrame;
  }

  public void startTimer() {
    running = true;
    paused = false; // start the thread up
    runThread = new Thread(this);
    runThread.start();
  }

  public void pauseTimer() { // just pause it
    paused = true;
    running = false;
    //paused=!paused;
  }

  public void stopTimer() { // completely stop the timer
    running = false;
    paused = false;
  }


  @Override public void run() {
    long startTime = System.currentTimeMillis(); // keep showing the difference in time until we are either paused or not running anymore
    long summedTimeCopy = 0;


    if(paused){
      summedTime -= System.currentTimeMillis();
      summedTimeCopy= summedTime - (System.currentTimeMillis() - startTime) ;
    }
    else{
      while(running && !paused) {
        timeFrame.update(summedTime - (System.currentTimeMillis() - startTime));

        if( summedTime - (System.currentTimeMillis() - startTime) < 0 )
          JOptionPane.showMessageDialog(null,"Time's Up!");
          System.out.println("time's up");
          //run();
          try{
            Thread.sleep(1000000);
          }catch(Exception e){}
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

