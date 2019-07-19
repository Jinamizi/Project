package ui;

import java.awt.CardLayout;
import javax.swing.*;

public class SearchPanelController extends JPanel{
    CardLayout card = new CardLayout();
    SearchPanel search = new SearchPanel();
    
    public static final String SEARCH_PANEL = "search_panel";
    public SearchPanelController(){
        card.addLayoutComponent(search, SEARCH_PANEL);
        
        add(search);
        setLayout(card);
    }
    
    public void showPanel(String panelName) {
        card.show(this, panelName);
    }
    
    public static void main(String[] args){
        JFrame frame = new JFrame("search panel controller");
        frame.add(new SearchPanelController());
        
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }
}
