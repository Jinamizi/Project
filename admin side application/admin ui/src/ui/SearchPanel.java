package ui;

import java.awt.Cursor;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.AbstractTableModel;

/**
 *
 * @author DEGUZMAN
 */
public class SearchPanel extends javax.swing.JPanel {

    /**
     * Creates new form SearchPanel
     */
    public SearchPanel() {
        initComponents();
        idField.getDocument().addDocumentListener(new FieldListener());
    }

    /**
     * Retrieve names of a customer using {@link Connector} class
     *
     * @param id the id number of the person to retrieve names
     * @return the names of the customer
     */
    public Map<String, String> getNames(String id) throws IOException {
        return Connector.getNames(id);
    }

    /**
     * Retrieve accounts and balances using {@link Connector} class
     *
     * @param id the id number of the person to retrieve accounts and balances
     * @return the balances mapped to the accounts
     */
    public Map<String, String> getAccountsAndBalances(String id) throws IOException{
        return Connector.getAccountBalances(id);
    }

    /**
     * Clear the name textfields
     */
    public void clear() {
        firstNameField.setText(" ");
        lastNameField.setText(" ");
    }

    public void search() {
        tableModel.clear();
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        if (Connector.connectionExist()) {
            try {
                String id = idField.getText();
                Map<String, String> names = getNames(id);
                firstNameField.setText(names.get("first_name"));
                lastNameField.setText(names.get("last_name"));
                
                tableModel.updateTable(id);
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage());
            }
        }else {
            JOptionPane.showMessageDialog(this.getParent(), "There is no connetion with the Server", "Connection Error", JOptionPane.ERROR_MESSAGE);
        }

        setCursor(null);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        idField = new javax.swing.JTextField();
        searchButton = new javax.swing.JButton();
        jLabel3 = new javax.swing.JLabel();
        firstNameField = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        lastNameField = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        accountTable = new javax.swing.JTable();

        jLabel1.setText("Search customer");

        jLabel2.setText("ID:");

        idField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                idFieldActionPerformed(evt);
            }
        });

        searchButton.setText("Search");
        searchButton.setEnabled(false);
        searchButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                searchButtonActionPerformed(evt);
            }
        });

        jLabel3.setText("First name:");

        firstNameField.setEditable(false);
        firstNameField.setBackground(new java.awt.Color(255, 255, 255));
        firstNameField.setDisabledTextColor(new java.awt.Color(0, 0, 0));

        jLabel4.setText("Last name:");

        lastNameField.setEditable(false);
        lastNameField.setBackground(new java.awt.Color(255, 255, 255));
        lastNameField.setDisabledTextColor(new java.awt.Color(0, 0, 0));

        jScrollPane1.setBackground(new java.awt.Color(255, 255, 255));
        jScrollPane1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jScrollPane1.setToolTipText("accounts");

        accountTable.setModel(tableModel);
        accountTable.setColumnSelectionAllowed(true);
        accountTable.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        accountTable.setShowVerticalLines(false);
        jScrollPane1.setViewportView(accountTable);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 194, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                                .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 69, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(firstNameField))
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                                .addComponent(jLabel2)
                                .addGap(18, 18, 18)
                                .addComponent(idField, javax.swing.GroupLayout.PREFERRED_SIZE, 300, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 69, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(lastNameField)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(searchButton))
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(45, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(idField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(searchButton))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(firstNameField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lastNameField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(31, 31, 31)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 87, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(27, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void searchButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_searchButtonActionPerformed
        new Thread(this::search).start();
    }//GEN-LAST:event_searchButtonActionPerformed

    private void idFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_idFieldActionPerformed
        new Thread(this::search).start();
    }//GEN-LAST:event_idFieldActionPerformed

    public static void main(String [] args){
        JFrame frame = new JFrame("Search Panel");
        frame.add(new SearchPanel());
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }
    
    class AccountsTableModel extends AbstractTableModel {

        Map<String, String> data = new HashMap<>();
        String[] keys = {};

        @Override
        public int getRowCount() {
            return data.size();
        }

        @Override
        public int getColumnCount() {
            return 2;
        }

        @Override
        public String getValueAt(int rowIndex, int columnIndex) {
            return columnIndex == 0 ? keys[rowIndex] : data.get(keys[rowIndex]);
        }

        //get name of a particular column in ResultSet
        @Override
        public String getColumnName(int column) {
            return column == 0 ? "Account" : "Balance";
        }

        /**
         * update the data in the table
         *
         * @param id the id number of the individual whose data will fill the
         * table
         */
        public void updateTable(String id) throws IOException {
            data = SearchPanel.this.getAccountsAndBalances(id); //refresh the data of the table
            keys = data.keySet().toArray(new String[data.size()]);
            fireTableDataChanged();
        }

        public void clear() {
            data.clear();
            fireTableDataChanged();
        }
    }

    class FieldListener implements DocumentListener {

        @Override
        public void insertUpdate(DocumentEvent e) {
            setSearchButton();
        }

        @Override
        public void removeUpdate(DocumentEvent e) {
            setSearchButton();
        }

        @Override
        public void changedUpdate(DocumentEvent e) {
            setSearchButton();
        }

        private void setSearchButton() {
            searchButton.setEnabled(!idField.getText().equals(""));
        }
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTable accountTable;
    private javax.swing.JTextField firstNameField;
    private javax.swing.JTextField idField;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextField lastNameField;
    private javax.swing.JButton searchButton;
    // End of variables declaration//GEN-END:variables

    AccountsTableModel tableModel = new AccountsTableModel();
}
