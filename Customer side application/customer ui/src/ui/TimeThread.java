package ui;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class TimeThread extends MouseAdapter implements Runnable {

    int time = 30000; //30 seconds
    ThreadAction action;

    public TimeThread(ThreadAction action) {
        this.action = action;
    }

    @Override
    public void run() {
        try(AutoCloseable finish = action::action) {
            while (true) {
                Thread.sleep(1000);
                setTime(time -= 1000);
                if (time <= 0) {
                    break;
                }
            }
        } catch (Exception ex) {
            //Logger.getLogger(TimeThread.class.getName()).log(Level.SEVERE, null, ex);
            ex.printStackTrace();
        }
    }

    synchronized void setTime(int value) {
        time = value;
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        setTime(30000);
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        setTime(30000);
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        setTime(30000);
    }

}
