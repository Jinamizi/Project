package ui;

import java.awt.CardLayout;
import javax.swing.*;


public class HomePanelController extends JPanel{
    public static String WELCOME_PANEL = "welcome_panel";
    public static String LOGIN_PANEL = "login_panel";
    private  CardLayout card;
    private  LoginPanel loginPanel;
    private  WelcomePanel welcomePanel;
    private  JPanel panel;
    
    HomePanelController(){
        card = new CardLayout();
        loginPanel = new LoginPanel();
        welcomePanel = new WelcomePanel();
        card.addLayoutComponent(welcomePanel, WELCOME_PANEL);
        card.addLayoutComponent(loginPanel, LOGIN_PANEL);
        
        setLayout(card);
    }
    
    
    public void show(String panelName){
        card.show(this, panelName);
    }
}
