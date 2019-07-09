/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ui;

import java.awt.Cursor;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 *
 * @author DEGUZMAN
 */
public class ScanningPanel extends java.awt.Panel {

    private String imagePath = "";

    /**
     * Creates new form ScanningPanel
     */
    public ScanningPanel() {
        initComponents();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        fingerprintLabel = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        submitButton = new javax.swing.JButton();

        fingerprintLabel.setBackground(new java.awt.Color(255, 255, 255));
        fingerprintLabel.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));
        fingerprintLabel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                fingerprintLabelMouseClicked(evt);
            }
        });

        jLabel1.setText("Scan fingerprint");

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
                .addGap(23, 23, 23)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(fingerprintLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 322, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 153, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(255, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(submitButton)
                .addGap(265, 265, 265))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(fingerprintLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 269, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(submitButton)
                .addContainerGap(58, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void submitButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_submitButtonActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_submitButtonActionPerformed

    private void fingerprintLabelMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_fingerprintLabelMouseClicked
        String s = getPath();
        if (s == null) {
            return;
        }

        setIcon(new ImageIcon(s));
        if (fingerprintLabel.getIcon() == null) {
            return;
        }

        imagePath = s;
        submitButton.setEnabled(true);
    }//GEN-LAST:event_fingerprintLabelMouseClicked

    private void checkFingerprint() {
        submitButton.setEnabled(false);
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

        try {
            BufferedImage print = ImageIO.read(new File(imagePath));
            String response = Connector.checkIfPrintExist(print);
            if (response.equalsIgnoreCase("DONT EXIST")) {
                AdminFrame.getAddPanel().showPanel(AddPanelController.DETAILS_PANEL);
            } else if (response.equalsIgnoreCase("EXIST")) {
                String id = "sssssss"; //Connector.getID(print);
                String[] accounts = {"A122", "A33"}; //Connector.getAccounts(id);
            }
        } catch (IOException ex) {
            Logger.getLogger(ScanningPanel.class.getName()).log(Level.SEVERE, null, ex);
        }
        submitButton.setEnabled(true);
        setCursor(null);
    }

    public void showAddAccount() {
        String id = "sssssss"; //Connector.getID(print);
        String[] accounts = {"A122", "A33"}; //Connector.getAccounts(id);
        
        JOptionPane.showConfirmDialog(this, "Customer Exist\nID number: "+id+"\nAccounts: "+Arrays.toString(accounts), "Info", JOptionPane.YES_NO_OPTION);
    }

    private void setIcon(ImageIcon img) {
        Image image = img.getImage();
        image = image.getScaledInstance(fingerprintLabel.getWidth(), fingerprintLabel.getHeight(), Image.SCALE_SMOOTH);
        ImageIcon icon = new ImageIcon(image);
        fingerprintLabel.setIcon(icon);
    }

    public void setImagePath(String path) {
        imagePath = path;
    }

    public String getImagePath() {
        return imagePath;
    }

    private String getPath() {
        JFileChooser fileChooser = new PictureChooser();

        int result = fileChooser.showOpenDialog(null);

        //if user clicked Cancel button on dialog, return
        if (result == JFileChooser.CANCEL_OPTION) {
            return null;
        }

        File fileName = fileChooser.getSelectedFile(); //get file

        //display error if invalid
        if ((fileName == null) || (fileName.getName().equals(""))) {
            JOptionPane.showMessageDialog(this, "Invalid Name", "error", JOptionPane.ERROR_MESSAGE);
            return null;
        }

        return fileName.getPath();
    }

    public static void main(String[] arg) {
        JFrame frame = new JFrame();
        frame.add(new ScanningPanel());
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel fingerprintLabel;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JButton submitButton;
    // End of variables declaration//GEN-END:variables
}