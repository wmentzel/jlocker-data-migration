package com.randomlychosenbytes.jlocker.dialogs;

import com.randomlychosenbytes.jlocker.abstractreps.EntityCoordinates;
import com.randomlychosenbytes.jlocker.abstractreps.ManagementUnit;
import com.randomlychosenbytes.jlocker.main.MainFrame;
import com.randomlychosenbytes.jlocker.manager.DataManager;
import com.randomlychosenbytes.jlocker.nonabstractreps.Building;
import com.randomlychosenbytes.jlocker.nonabstractreps.Floor;
import com.randomlychosenbytes.jlocker.nonabstractreps.Locker;
import com.randomlychosenbytes.jlocker.nonabstractreps.Walk;

import javax.swing.*;
import java.awt.print.PrinterException;
import java.util.LinkedList;
import java.util.List;

public class SearchFrame extends javax.swing.JFrame {
    private JTable table;
    private List<EntityCoordinates<Locker>> foundLockers;

    private final List<String> columnData = new LinkedList<>();
    private final List<Class> dataTypes = new LinkedList<>();

    private final MainFrame mainFrame;
    private final DataManager dataManager;

    public SearchFrame(MainFrame mainFrame, DataManager dataManager) {
        initComponents();

        this.mainFrame = mainFrame;

        this.dataManager = dataManager;

        // button that is clicked when you hit enter
        getRootPane().setDefaultButton(searchButton);

        // focus in the middle
        setLocationRelativeTo(null);

        resultsPanel.removeAll();
        table = null;

        printResultsButton.setEnabled(false);
        emptySelectedButton.setEnabled(false);

        //
        // Initialize controls array
        //

        columnData.add("Schließfach-ID");
        columnData.add("Name");
        columnData.add("Vorname");
        columnData.add("Klasse");
        columnData.add("Größe");
        columnData.add("Vertrag");
        columnData.add("Geld");
        columnData.add("Dauer");
        columnData.add("von:");
        columnData.add("bis:");
        columnData.add("Schloss");


        dataTypes.add(java.lang.String.class);
        dataTypes.add(java.lang.String.class);
        dataTypes.add(java.lang.String.class);
        dataTypes.add(java.lang.String.class);
        dataTypes.add(java.lang.Integer.class);
        dataTypes.add(java.lang.Boolean.class);
        dataTypes.add(java.lang.Integer.class);
        dataTypes.add(java.lang.Integer.class);
        dataTypes.add(java.lang.String.class);
        dataTypes.add(java.lang.String.class);
        dataTypes.add(java.lang.String.class);


        if (dataManager.getCurUser().isSuperUser()) {
            columnData.add("Codes");
            dataTypes.add(java.lang.String.class);
        }
    }

    private void rowClicked(java.awt.event.MouseEvent evt) {
        int row = ((JTable) evt.getSource()).getSelectedRow();

        String id = (String) table.getValueAt(row, 0);


        //
        // through the process of reordering the rows by a user defined column,
        // we dont know which index belongs to which locker
        //
        int index = 0;

        for (int i = 0; i < foundLockers.size(); i++) {
            if (foundLockers.get(i).getEntity().getId().equals(id))
                index = i;
        }

        if (evt.getClickCount() == 2) {
            this.toBack();

            EntityCoordinates coords = foundLockers.get(index);

            dataManager.setCurrentBuildingIndex(coords.getBValue());
            dataManager.setCurrentFloorIndex(coords.getFValue());
            dataManager.setCurrentWalkIndex(coords.getWValue());

            final int iMUnitIndex = coords.getMValue();
            final int iLockerIndex = coords.getLValue();

            // setComboBoxes2CurIndizes calls drawLockerOverview which itself
            // sets the first available locker on the selected walk as the selected one.
            // This behaviour is not as desired for this would overwrite the highlighted 
            // search result - the selected locker.
            // So we save the indexes of the MUnit of the current locker and the index
            // of the current locker itself and reset it after the call of this method.
            mainFrame.setComboBoxes2CurIndizes();

            dataManager.getCurLocker().setAppropriateColor();

            // reset
            dataManager.setCurrentMUnitIndex(iMUnitIndex);
            dataManager.setCurrentLockerIndex(iLockerIndex);
            dataManager.getCurLocker().setSelected();

            mainFrame.showLockerInformation();
            mainFrame.bringCurrentLockerInSight();
        }
    }

    /**
     * This is necessary because the row order can be user defined, so the row
     * order and the found lockers order isn't the same
     */
    private int getLockerFromRow(int row) {
        String id = (String) table.getValueAt(row, 0);

        for (int i = 0; i < foundLockers.size(); i++) {
            Locker locker = foundLockers.get(i).getEntity();

            if (locker.getId().equals(id)) {
                return i;
            }
        }

        return -1;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        jCheckBox1 = new javax.swing.JCheckBox();
        lockerDataScrollPane = new javax.swing.JScrollPane();
        lockerDataPanel = new javax.swing.JPanel();
        containerPanel = new javax.swing.JPanel();
        lockerIDLabel = new javax.swing.JLabel();
        lockerIDTextField = new javax.swing.JTextField();
        surnameLabel = new javax.swing.JLabel();
        surnameTextField = new javax.swing.JTextField();
        nameLabel = new javax.swing.JLabel();
        nameTextField = new javax.swing.JTextField();
        classLabel = new javax.swing.JLabel();
        classTextField = new javax.swing.JTextField();
        sizeLabel = new javax.swing.JLabel();
        sizeTextField = new javax.swing.JTextField();
        hasContractLabel = new javax.swing.JLabel();
        hasContractCheckbox = new javax.swing.JCheckBox();
        moneyLabel = new javax.swing.JLabel();
        moneyTextField = new javax.swing.JTextField();
        durationInMonthsLabel = new javax.swing.JLabel();
        durationTextField = new javax.swing.JTextField();
        fromLabel = new javax.swing.JLabel();
        fromDateTextField = new javax.swing.JTextField();
        untilLabel = new javax.swing.JLabel();
        untilDateTextField = new javax.swing.JTextField();
        lockLabel = new javax.swing.JLabel();
        lockTextField = new javax.swing.JTextField();
        emtpyCheckboxLabel = new javax.swing.JLabel();
        emptyCheckbox = new javax.swing.JCheckBox();
        searchButton = new javax.swing.JButton();
        fillPanel = new javax.swing.JPanel();
        emptySelectedButton = new javax.swing.JButton();
        printResultsButton = new javax.swing.JButton();
        resultsScrollPane = new javax.swing.JScrollPane();
        resultsPanel = new javax.swing.JPanel();
        noDataFoundLabel = new javax.swing.JLabel();

        jCheckBox1.setText("jCheckBox1");

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Suche");

        lockerDataPanel.setLayout(new java.awt.GridBagLayout());

        containerPanel.setLayout(new java.awt.GridLayout(12, 1, 0, 10));

        lockerIDLabel.setText("Schließfach-ID");
        containerPanel.add(lockerIDLabel);
        containerPanel.add(lockerIDTextField);

        surnameLabel.setText("Name");
        containerPanel.add(surnameLabel);
        containerPanel.add(surnameTextField);

        nameLabel.setText("Vorname");
        containerPanel.add(nameLabel);
        containerPanel.add(nameTextField);

        classLabel.setText("Klasse");
        containerPanel.add(classLabel);
        containerPanel.add(classTextField);

        sizeLabel.setText("Größe");
        containerPanel.add(sizeLabel);
        containerPanel.add(sizeTextField);

        hasContractLabel.setText("Vertrag");
        containerPanel.add(hasContractLabel);
        containerPanel.add(hasContractCheckbox);

        moneyLabel.setText("Geld (€)");
        containerPanel.add(moneyLabel);
        containerPanel.add(moneyTextField);

        durationInMonthsLabel.setText("verbleibende Monate");
        containerPanel.add(durationInMonthsLabel);
        containerPanel.add(durationTextField);

        fromLabel.setText("Von");
        containerPanel.add(fromLabel);
        containerPanel.add(fromDateTextField);

        untilLabel.setText("Bis");
        containerPanel.add(untilLabel);
        containerPanel.add(untilDateTextField);

        lockLabel.setText("Schloss");
        containerPanel.add(lockLabel);
        containerPanel.add(lockTextField);

        emtpyCheckboxLabel.setText("leer");
        containerPanel.add(emtpyCheckboxLabel);
        containerPanel.add(emptyCheckbox);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
        lockerDataPanel.add(containerPanel, gridBagConstraints);

        searchButton.setText("Suchen");
        searchButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                searchButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.insets = new java.awt.Insets(10, 0, 10, 0);
        lockerDataPanel.add(searchButton, gridBagConstraints);

        javax.swing.GroupLayout fillPanelLayout = new javax.swing.GroupLayout(fillPanel);
        fillPanel.setLayout(fillPanelLayout);
        fillPanelLayout.setHorizontalGroup(
                fillPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGap(0, 260, Short.MAX_VALUE)
        );
        fillPanelLayout.setVerticalGroup(
                fillPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGap(0, 30, Short.MAX_VALUE)
        );

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weighty = 1.0;
        lockerDataPanel.add(fillPanel, gridBagConstraints);

        emptySelectedButton.setText("Auswahl leeren");
        emptySelectedButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                emptySelectedButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 10, 10);
        lockerDataPanel.add(emptySelectedButton, gridBagConstraints);

        printResultsButton.setText("Ergebnisse drucken");
        printResultsButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                printResultsButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 10, 10);
        lockerDataPanel.add(printResultsButton, gridBagConstraints);

        lockerDataScrollPane.setViewportView(lockerDataPanel);

        getContentPane().add(lockerDataScrollPane, java.awt.BorderLayout.WEST);

        resultsPanel.setLayout(new java.awt.BorderLayout());

        noDataFoundLabel.setText("Es wurden keine Schließfächer gefunden, die den Suchkriterien entsprechen!");
        noDataFoundLabel.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        resultsPanel.add(noDataFoundLabel, java.awt.BorderLayout.CENTER);

        resultsScrollPane.setViewportView(resultsPanel);

        getContentPane().add(resultsScrollPane, java.awt.BorderLayout.CENTER);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void searchButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_searchButtonActionPerformed
    {//GEN-HEADEREND:event_searchButtonActionPerformed
        String id = lockerIDTextField.getText();

        String surname = surnameTextField.getText();
        String name = nameTextField.getText();
        String _class = classTextField.getText();

        int size;

        try {
            size = Integer.parseInt(sizeTextField.getText());
        } catch (NumberFormatException e) {
            size = -1;
        }

        boolean contract = hasContractCheckbox.isSelected();

        int money;

        try {
            money = Integer.parseInt(moneyTextField.getText());
        } catch (NumberFormatException e) {
            money = -1;
        }

        int remainingTimeInMonths;

        try {
            remainingTimeInMonths = Integer.parseInt(durationTextField.getText());
        } catch (NumberFormatException e) {
            remainingTimeInMonths = -1;
        }

        String fromdate = fromDateTextField.getText();
        String untildate = untilDateTextField.getText();
        String lock = lockTextField.getText();

        List tableData = new LinkedList();

        foundLockers = new LinkedList<>();

        List<Building> buildings = dataManager.getBuildingList();

        for (int b = 0; b < buildings.size(); b++) {
            List<Floor> floors = buildings.get(b).getFloorList();

            for (int f = 0; f < floors.size(); f++) {
                List<Walk> walks = floors.get(f).getWalkList();

                for (int w = 0; w < walks.size(); w++) {
                    List<ManagementUnit> cols = walks.get(w).getManagementUnitList();

                    for (int c = 0; c < cols.size(); c++) {
                        List<Locker> lockers = cols.get(c).getLockerList();

                        for (int l = 0; l < lockers.size(); l++) {
                            Locker locker = lockers.get(l);

                            if (!emptyCheckbox.isSelected()) {
                                if (!id.equals("")) {
                                    if (!locker.getId().equals(id)) {
                                        continue;
                                    }
                                }

                                if (surname.length() != 0) {
                                    if (!surname.equals(locker.getSurname())) {
                                        continue;
                                    }
                                }

                                if (!name.equals("")) {
                                    if (!name.equals(locker.getOwnerName())) {
                                        continue;
                                    }
                                }

                                if (!_class.equals("")) {
                                    String cl = locker.getOwnerClass();

                                    if (!_class.contains(".") && !_class.equals("Kurs")) {
                                        if (cl.contains(".")) {
                                            int index = cl.indexOf('.');
                                            cl = cl.substring(0, index);

                                            System.out.println(cl);
                                        }
                                    }

                                    if (!_class.equals(cl)) {
                                        continue;
                                    }
                                }

                                if (size != -1) {
                                    if (size != locker.getOwnerSize()) {
                                        continue;
                                    }
                                }

                                if (money != -1) {
                                    if (money != locker.getMoney()) {
                                        continue;
                                    }
                                }

                                if (remainingTimeInMonths != -1) {
                                    if (locker.getRemainingTimeInMonths() != remainingTimeInMonths) {
                                        continue;
                                    }
                                }

                                if (!fromdate.equals("")) {
                                    if (!fromdate.equals(locker.getFromDate())) {
                                        continue;
                                    }
                                }

                                if (!untildate.equals("")) {
                                    if (!untildate.equals(locker.getUntilDate())) {
                                        continue;
                                    }
                                }

                                if (!lock.equals("")) {
                                    if (!lock.equals(locker.getLock())) {
                                        continue;
                                    }
                                }
                            } // if
                            else {
                                if (!locker.isFree()) {
                                    continue;
                                }
                            }

                            List rowData = new LinkedList();
                            rowData.add(locker.getId());
                            rowData.add(locker.getSurname());
                            rowData.add(locker.getOwnerName());
                            rowData.add(locker.getOwnerClass());
                            rowData.add(locker.getOwnerSize());
                            rowData.add(locker.hasContract());
                            rowData.add(locker.getMoney());
                            rowData.add(locker.getRemainingTimeInMonths());
                            rowData.add(locker.getFromDate());
                            rowData.add(locker.getUntilDate());
                            rowData.add(locker.getLock());

                            if (dataManager.getCurUser().isSuperUser()) {
                                String codearray[] = locker.getCodes(dataManager.getCurUser().getSuperUMasterKey());
                                String codes = "";

                                for (int i = 0; i < 5; i++) {
                                    codes += codearray[i] + (i < 4 ? ", " : "");
                                }

                                rowData.add(codes);
                            }

                            tableData.add(rowData);

                            foundLockers.add(new EntityCoordinates(locker, b, f, w, c, l));
                        } // for
                    } // for
                } // for
            } // for
        } // for

        resultsPanel.removeAll();

        // Workaround to avoid obsolete Vector class
        Object tableDataArray[][] = new Object[tableData.size()][];

        for (int i = 0; i < tableData.size(); i++) {
            tableDataArray[i] = ((List) tableData.get(i)).toArray();
        }

        printResultsButton.setEnabled(!foundLockers.isEmpty());
        emptySelectedButton.setEnabled(!foundLockers.isEmpty());

        if (!foundLockers.isEmpty()) {
            table = new JTable();
            table.setAutoCreateRowSorter(true);

            table.setModel
                    (
                            new javax.swing.table.DefaultTableModel(tableDataArray, columnData.toArray()) {
                                @Override
                                public Class getColumnClass(int columnIndex) {
                                    return dataTypes.get(columnIndex);
                                }

                                @Override
                                public boolean isCellEditable(int row, int col) {
                                    return false;
                                }
                            }
                    );

            table.addMouseListener(new java.awt.event.MouseAdapter() {
                @Override
                public void mouseClicked(java.awt.event.MouseEvent evt) {
                    rowClicked(evt);
                }
            });

            resultsScrollPane.setViewportView(table);
        } else {
            resultsScrollPane.setViewportView(noDataFoundLabel);
        }

        resultsPanel.updateUI();
    }//GEN-LAST:event_searchButtonActionPerformed

    private void printResultsButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_printResultsButtonActionPerformed
    {//GEN-HEADEREND:event_printResultsButtonActionPerformed
        if (table != null) {
            System.out.print("* printing... ");
            try {
                table.print();
                System.out.print("successful");
            } catch (PrinterException ex) {
                System.out.print("failed");
            }
        }
    }//GEN-LAST:event_printResultsButtonActionPerformed

    private void emptySelectedButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_emptySelectedButtonActionPerformed
    {//GEN-HEADEREND:event_emptySelectedButtonActionPerformed
        int answer = JOptionPane.showConfirmDialog(null, "Wollen Sie die markierten Schließfächer wirklich leeren?", "Bestätigung", JOptionPane.YES_NO_CANCEL_OPTION);

        if (answer == JOptionPane.NO_OPTION || answer == JOptionPane.CANCEL_OPTION || answer == JOptionPane.CLOSED_OPTION) {
            return;
        }

        int[] selectedRows = table.getSelectedRows();

        for (int r = 0; r < selectedRows.length; r++) {
            int row = selectedRows[r];

            int lockerIndex = getLockerFromRow(row);
            Locker locker = foundLockers.get(lockerIndex).getEntity();

            for (int j = 1; j < 10; j++) {
                if (dataTypes.get(j) == java.lang.String.class) {
                    table.setValueAt("", row, j);
                }

                if (dataTypes.get(j) == java.lang.Integer.class) {
                    table.setValueAt(0, row, j);
                }

                if (dataTypes.get(j) == java.lang.Boolean.class) {
                    table.setValueAt(false, row, j);
                }
            }

            locker.empty();
        }

        JOptionPane.showMessageDialog(this, "Die ausgewälten Schließfächer wurden erfolgreich geleert!", "Info", JOptionPane.INFORMATION_MESSAGE);
    }//GEN-LAST:event_emptySelectedButtonActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel classLabel;
    private javax.swing.JTextField classTextField;
    private javax.swing.JPanel containerPanel;
    private javax.swing.JLabel durationInMonthsLabel;
    private javax.swing.JTextField durationTextField;
    private javax.swing.JCheckBox emptyCheckbox;
    private javax.swing.JButton emptySelectedButton;
    private javax.swing.JLabel emtpyCheckboxLabel;
    private javax.swing.JPanel fillPanel;
    private javax.swing.JTextField fromDateTextField;
    private javax.swing.JLabel fromLabel;
    private javax.swing.JCheckBox hasContractCheckbox;
    private javax.swing.JLabel hasContractLabel;
    private javax.swing.JCheckBox jCheckBox1;
    private javax.swing.JLabel lockLabel;
    private javax.swing.JTextField lockTextField;
    private javax.swing.JPanel lockerDataPanel;
    private javax.swing.JScrollPane lockerDataScrollPane;
    private javax.swing.JLabel lockerIDLabel;
    private javax.swing.JTextField lockerIDTextField;
    private javax.swing.JLabel moneyLabel;
    private javax.swing.JTextField moneyTextField;
    private javax.swing.JLabel nameLabel;
    private javax.swing.JTextField nameTextField;
    private javax.swing.JLabel noDataFoundLabel;
    private javax.swing.JButton printResultsButton;
    private javax.swing.JPanel resultsPanel;
    private javax.swing.JScrollPane resultsScrollPane;
    private javax.swing.JButton searchButton;
    private javax.swing.JLabel sizeLabel;
    private javax.swing.JTextField sizeTextField;
    private javax.swing.JLabel surnameLabel;
    private javax.swing.JTextField surnameTextField;
    private javax.swing.JTextField untilDateTextField;
    private javax.swing.JLabel untilLabel;
    // End of variables declaration//GEN-END:variables
}
