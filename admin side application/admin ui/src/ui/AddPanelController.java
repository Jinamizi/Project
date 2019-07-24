package ui;

import java.awt.CardLayout;
import javax.swing.*;

public class AddPanelController extends JPanel {

    CardLayout card = new CardLayout();
    private final ScanningPanel scanningPanel = new ScanningPanel();
    private final AddAccountPanel addAccountPanel = new AddAccountPanel();
    private final DetailsForm detailsForm = new DetailsForm();
    public static String SCANNING_PANEL = "scanning_panel";
    public static String ADD_ACCOUNT_PANEL = "add_account_panel";
    public static String DETAILS_PANEL = "details_panel";

    public AddPanelController() {
        card.addLayoutComponent(scanningPanel, SCANNING_PANEL);
        card.addLayoutComponent(addAccountPanel, ADD_ACCOUNT_PANEL);
        card.addLayoutComponent(detailsForm, DETAILS_PANEL);

        this.setLayout(card);

        add(scanningPanel);
        add(addAccountPanel);
        add(detailsForm);
    }

    public ScanningPanel getScanningPanel() {
        return scanningPanel;
    }

    public AddAccountPanel getPasswordPanel() {
        return addAccountPanel;
    }

    public DetailsForm getDetailsForm() {
        return detailsForm;
    }

    public void showPanel(String panelName) {
        if (panelName.equalsIgnoreCase(SCANNING_PANEL)) {
            scanningPanel.getSubmitButton().setEnabled(false);
            scanningPanel.getFingerprintLabel().setIcon(null);
        }
        card.show(this, panelName);
    }

}//49
