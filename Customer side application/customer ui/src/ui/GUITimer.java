package ui;

import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.Timer;

public class GUITimer extends MouseAdapter {
    private final int loadingtime = 120000;
    private final Timer timer ;
    boolean actionOccured = false;

    public GUITimer() {
        timer = new Timer(loadingtime, (ActionEvent e) -> {
            GUITimer.this.run();
        });
    }
    
    public void start(){
        timer.start();
    }
    
    public void stop(){
        timer.stop();
    }

    public void run() {
        if (actionOccured) {
            setActionOccurred(false);
        }
        else {
            CustomerFrame.showPanel(CustomerFrame.WELCOME_PANEL);
        }
    }

    synchronized void setActionOccurred(boolean value) {
        actionOccured = value;
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        System.out.println("mouse moved");
        setActionOccurred(true);
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        System.out.println("mouse released");
        setActionOccurred(true);
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        System.out.println("mouse entered");
        setActionOccurred(true);
    }

}
