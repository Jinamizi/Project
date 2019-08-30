package ui;

import java.awt.CardLayout;
import javax.swing.*;


public class HomePanelController extends JPanel{
    public static String WELCOME_PANEL = "welcome_panel";
    public static String LOGIN_PANEL = "login_panel";
    private final  CardLayout card = new CardLayout();
    private final  LoginPanel loginPanel = new LoginPanel();
    private final  WelcomePanel welcomePanel = new WelcomePanel();
    
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
}
