package ui;

import java.awt.CardLayout;
import javax.swing.*;

public class AdminFrame1 extends JFrame{
    JButton homeButton = new JButton("Home");
    JButton addButton = new JButton("Add");
    JButton searchButton = new JButton("Home");
    JPanel mainFrame = new JPanel();
    CardLayout card = new CardLayout();
    Box buttonBox = Box.createHorizontalBox();
    HomePanelController hpc = new HomePanelController();
    AddPanelController apc = new AddPanelController();
    SearchPanelController spc = new SearchPanelController();
    public static String HOME = "home";
    public static String ADD = "add";
    public static String SEARCH = "search";
    
    
    AdminFrame1(){
        
    }
}
