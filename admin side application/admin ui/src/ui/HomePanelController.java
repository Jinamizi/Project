package ui;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;


public class HomePanelController extends JPanel{
    public static String WELCOME_PANEL = "welcome_panel";
    public static String LOGIN_PANEL = "login_panel";
    private  CardLayout card = new CardLayout();
    private  LoginPanel loginPanel = new LoginPanel();
    private  WelcomePanel welcomePanel = new WelcomePanel();
    
    HomePanelController(){
        add(welcomePanel);
        add(loginPanel);
        
        
        
        card.addLayoutComponent(welcomePanel, WELCOME_PANEL);
        card.addLayoutComponent(loginPanel, LOGIN_PANEL);
        
        setLayout(card);
    }

    public LoginPanel getLoginPanel() {
        return loginPanel;
    }

    public WelcomePanel getWelcomePanel() {
        return welcomePanel;
    }
    
    
    /**
     * displays the panel with the given name
     * @param panelName the name of the panel to be displayed
     */
    public void showPanel(String panelName){
        card.show(this, panelName);
    }
    
    
    
    public static void main(String [] k){
        JFrame frame = new JFrame("Home Panel");
        HomePanelController hpc = new HomePanelController();
        frame.add(hpc, BorderLayout.CENTER);
        
        String[] panelNames = {HomePanelController.WELCOME_PANEL, HomePanelController.LOGIN_PANEL};  
        
        JButton previous = new JButton("<");
        previous.addActionListener((ActionEvent) -> {hpc.showPanel(LOGIN_PANEL); });
        JButton next = new JButton(">");
        next.addActionListener((ActionEvent) -> {hpc.showPanel(WELCOME_PANEL); });
        
        JPanel holder = new JPanel();
        holder.add(next);
        holder.add(previous);
        
        frame.add(holder, BorderLayout.AFTER_LINE_ENDS);
        
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }
}
