package tk.weslie.elementshelper;

import java.util.Timer;
import java.util.TimerTask;

public class Countdown {

    private final int originalTimeInSeconds;
    private int timeLeft;
    private boolean isRunning;
    private Timer timer;

    public Countdown(int timeInSeconds) {
        this.originalTimeInSeconds = timeInSeconds;
        this.timeLeft = timeInSeconds;
        this.isRunning = false;
    }

    // Startet den Countdown
    public void start(Runnable onFinish) {
        if (isRunning) return;
        isRunning = true;
        timer = new Timer();

        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if (timeLeft <= 0) {
                    onFinish.run();
                    stop();
                } else {
                    timeLeft--;
                }
            }
        }, 0, 1000); // Jede Sekunde (1000 Millisekunden)
    }

    // Stopt den Countdown
    public void stop() {
        if (!isRunning) return;
        timer.cancel();
        isRunning = false;
        timeLeft = originalTimeInSeconds;
    }

    // Setzt den Countdown zurück
    public void reset() {
        timeLeft = originalTimeInSeconds;
    }
}
