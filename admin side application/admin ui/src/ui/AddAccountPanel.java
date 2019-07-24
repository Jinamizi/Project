/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ui;

import java.awt.Cursor;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

/**
 *
 * @author DEGUZMAN
 */
public class AddAccountPanel extends java.awt.Panel {

    /**
     * Creates new form PasswordPanel
     *
     * @param id the id to add account
     */
    public AddAccountPanel() {
        initComponents();
        passwordField.getDocument().addDocumentListener(new PasswordListener());
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        passwordField = new javax.swing.JPasswordField();
        generateAccountButton = new javax.swing.JButton();
        accountLabel = new javax.swing.JLabel();
        okButton = new javax.swing.JButton();
        exitButton = new javax.swing.JButton();
        submitButton = new javax.swing.JButton();

        jLabel1.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel1.setText("Enter password");

        generateAccountButton.setText("Generate Account Number");
        generateAccountButton.setEnabled(false);
        generateAccountButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                generateAccountButtonActionPerformed(evt);
            }
        });

        accountLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        accountLabel.setText("  ");

        okButton.setText("Ok");
        okButton.setEnabled(false);
        okButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                okButtonActionPerformed(evt);
            }
        });

        exitButton.setText("Exit");
        exitButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                exitButtonActionPerformed(evt);
            }
        });

        submitButton.setText("Submit");
        submitButton.setEnabled(false);
        submitButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                submitButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(submitButton)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(exitButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(okButton))
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addComponent(generateAccountButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(layout.createSequentialGroup()
                            .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                            .addComponent(passwordField, javax.swing.GroupLayout.PREFERRED_SIZE, 284, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addComponent(accountLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap(49, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(28, 28, 28)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(passwordField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(8, 8, 8)
                .addComponent(submitButton)
                .addGap(18, 18, 18)
                .addComponent(generateAccountButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(accountLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(43, 43, 43)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(okButton)
                    .addComponent(exitButton))
                .addContainerGap(73, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void exitButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_exitButtonActionPerformed
        AdminFrame.getAddPanel().showPanel(AddPanelController.SCANNING_PANEL);
    }//GEN-LAST:event_exitButtonActionPerformed

    private void submitButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_submitButtonActionPerformed
        String password = String.valueOf(passwordField.getPassword());
        String id = AdminFrame.getAddPanel().getScanningPanel().getId();
        new Thread(() -> verifyCustomer(id, password)).start();
    }//GEN-LAST:event_submitButtonActionPerformed

    private void generateAccountButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_generateAccountButtonActionPerformed
        new Thread(this::getAndSetAccount).start();
    }//GEN-LAST:event_generateAccountButtonActionPerformed

    private void setOk() {
        boolean status = !accountLabel.getText().equals("");
        okButton.setEnabled(status);
    }

    private void okButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_okButtonActionPerformed
        if (confirmDetails()) {
            String id = AdminFrame.getAddPanel().getScanningPanel().getId();
            new Thread(() -> addAccount(id, accountLabel.getText())).start();
        }
    }//GEN-LAST:event_okButtonActionPerformed

    private void addAccount(String id, String account_number) {
        try {
            String result = Connector.addAccount(id, account_number);
            if (result.equalsIgnoreCase("SUCCESSFUL")) {
                JOptionPane.showMessageDialog(this, result);
                AdminFrame.getAddPanel().showPanel(AddPanelController.SCANNING_PANEL);
            } else if (result.equalsIgnoreCase("UNSUCCESSFUL")) {
                JOptionPane.showMessageDialog(this, result);
            } else {
                System.err.println(result);
                JOptionPane.showMessageDialog(this, result, "Info", JOptionPane.ERROR_MESSAGE);
            }
        } catch (IOException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Info", JOptionPane.ERROR_MESSAGE);
        }
    }

    private boolean confirmDetails() {
        String id = AdminFrame.getAddPanel().getScanningPanel().getId();
        String message = "ID: " + id;
        message += "\nAccount number: " + accountLabel.getText();
        int choice = JOptionPane.showConfirmDialog(this, message, "Confirm", JOptionPane.OK_CANCEL_OPTION);
        return (choice == JOptionPane.OK_OPTION);
    }

    public void getAndSetAccount() {
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        generateAccountButton.setEnabled(false);
        try {
            String account = Connector.generateAccountNumber();
            SwingUtilities.invokeLater(() -> {
                accountLabel.setText(account);
                setOk();
            });
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Info", JOptionPane.ERROR_MESSAGE);
        }
        setCursor(null);
        generateAccountButton.setEnabled(true);
    }

    private void verifyCustomer(String id, String password) {
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        submitButton.setEnabled(false);
        try {
            for (int trial = 0; trial < 3; trial++) {
                String result = Connector.verifyCustomer(id, password);
                if (result.equalsIgnoreCase("EXIST")) {
                    generateAccountButton.setEnabled(true);
                    return;
                } else if (result.equalsIgnoreCase("NOT FOUND")) {
                    continue;
                } else {
                    System.err.println(result);
                    break;
                }
            }
        } catch (IOException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this.getParent(), ex.getMessage(), "Info", JOptionPane.ERROR_MESSAGE);
        } finally {
            setCursor(null);
            submitButton.setEnabled(true);
        }
        AdminFrame.getAddPanel().showPanel(AddPanelController.SCANNING_PANEL);
    }

    class PasswordListener implements DocumentListener {

        @Override
        public void insertUpdate(DocumentEvent e) {
            submitButton.setEnabled(passwordField.getPassword().length > 0);
        }

        @Override
        public void removeUpdate(DocumentEvent e) {
            submitButton.setEnabled(passwordField.getPassword().length > 0);
        }

        @Override
        public void changedUpdate(DocumentEvent e) {
            submitButton.setEnabled(passwordField.getPassword().length > 0);
        }

    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel accountLabel;
    private javax.swing.JButton exitButton;
    private javax.swing.JButton generateAccountButton;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JButton okButton;
    private javax.swing.JPasswordField passwordField;
    private javax.swing.JButton submitButton;
    // End of variables declaration//GEN-END:variables
}//114
