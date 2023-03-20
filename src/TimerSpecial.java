import java.util.Timer;
import java.util.TimerTask;

public class TimerSpecial extends GamePanel {
    Timer timer2 = new Timer();
    private boolean timerOn = true;

    TimerSpecial(int seconds) {
//schedule the task
        timer2.schedule(new RemindTask(), seconds * 1000L);
    }

    public boolean isTimerOn() {
        return timerOn;
    }

    class RemindTask extends TimerTask {
        public void run() {
            timerOn = false;
//terminate the timer thread  
            timer2.cancel();
        }
    }
}