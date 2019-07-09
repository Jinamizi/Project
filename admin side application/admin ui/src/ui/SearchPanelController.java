package ui;

import java.awt.CardLayout;
import javax.swing.*;

public class SearchPanelController extends JPanel{
    CardLayout card = new CardLayout();
    
    public void show(String panelName) {
        card.show(this, panelName);
    }
}
