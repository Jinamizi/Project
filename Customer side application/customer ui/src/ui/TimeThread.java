package ui;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 *
 * @author DEGUZMAN
 */
public class TimeThread extends MouseAdapter implements Runnable {

    int loadingtime = 30000;
    int time = loadingtime; //30 seconds
    ThreadAction action; //what to do when time elapses

    public TimeThread(ThreadAction action) {
        this.action = action;
    }

    @Override
    public void run() {
        while (true) {
            try {
                Thread.sleep(1000);
                setTime(time -= 1000);
                if (time <= 0) {
                    action.action();
                    setTime(loadingtime);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    synchronized void setTime(int value) {
        time = value;
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        setTime(loadingtime);
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        setTime(loadingtime);
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        setTime(loadingtime);
    }

}
