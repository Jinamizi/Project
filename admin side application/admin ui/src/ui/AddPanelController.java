package ui;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import javax.swing.*;

public class AddPanelController extends JPanel{
    CardLayout card = new CardLayout();
    private ScanningPanel scanningPanel = new ScanningPanel();
    private PasswordPanel passwordPanel = new PasswordPanel();
    private DetailsForm detailsForm = new DetailsForm();
    public static String SCANNING_PANEL = "scanning_panel";
    public static String PASSWORD_PANEL = "password_panel";
    public static String DETAILS_PANEL = "details_panel";
    
    public AddPanelController() {
        card.addLayoutComponent(scanningPanel, SCANNING_PANEL);
        card.addLayoutComponent(passwordPanel, PASSWORD_PANEL);
        card.addLayoutComponent(detailsForm, DETAILS_PANEL);
        
        this.setLayout(card);
        
        add(scanningPanel);
        add(passwordPanel);
        add(detailsForm);
    }

    public ScanningPanel getScanningPanel() {
        return scanningPanel;
    }

    public PasswordPanel getPasswordPanel() {
        return passwordPanel;
    }

    public DetailsForm getDetailsForm() {
        return detailsForm;
    }
    
    public void showPanel(String panelName) {
        card.show(this, panelName);
    }
    
    public static void main(String[] s) {
        JFrame frame = new JFrame("Adding");
        AddPanelController apc = new AddPanelController();
        frame.add(apc, BorderLayout.CENTER);
        
        JPanel holder = new JPanel();
        
        JButton b1 = new JButton("SC");
        b1.addActionListener((ActionEvent) ->{apc.showPanel(SCANNING_PANEL);});
        JButton b2 = new JButton("Df");
        b2.addActionListener((ActionEvent) ->{apc.showPanel(DETAILS_PANEL);});
        JButton b3 = new JButton("PS");
        b3.addActionListener((ActionEvent) ->{apc.showPanel(PASSWORD_PANEL);});
        holder.add(b1);
        holder.add(b2);
        holder.add(b3);
        
        frame.add(holder, BorderLayout.LINE_END);
        
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }
}
