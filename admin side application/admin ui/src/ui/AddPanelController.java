package ui;

import java.awt.CardLayout;
import javax.swing.*;

public final class AddPanelController extends JPanel {

    private CardLayout card = new CardLayout();
    private final ScanningPanel scanningPanel = new ScanningPanel();
    private final AddAccountPanel addAccountPanel = new AddAccountPanel();
    private final DetailsForm detailsForm = new DetailsForm();
    public static final String SCANNING_PANEL = "scanning_panel";
    public static final String ADD_ACCOUNT_PANEL = "add_account_panel";
    public static final String DETAILS_PANEL = "details_panel";

    public AddPanelController() {
        card.addLayoutComponent(scanningPanel, SCANNING_PANEL);
        card.addLayoutComponent(addAccountPanel, ADD_ACCOUNT_PANEL);
        card.addLayoutComponent(detailsForm, DETAILS_PANEL);

        this.setLayout(card);

        add(scanningPanel);
        add(addAccountPanel);
        add(detailsForm);
    }

    public AddAccountPanel getAddAccountPanel() {
        return addAccountPanel;
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
        } else if (panelName.equalsIgnoreCase(ADD_ACCOUNT_PANEL)) {
            addAccountPanel.reset();
        }
        card.show(this, panelName);
    }

}//49
