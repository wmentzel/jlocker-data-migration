package com.randomlychosenbytes.jlocker.main;

import com.randomlychosenbytes.jlocker.abstractreps.ManagementUnit;
import com.randomlychosenbytes.jlocker.dialogs.*;
import com.randomlychosenbytes.jlocker.manager.DataManager;
import com.randomlychosenbytes.jlocker.nonabstractreps.Entity;
import com.randomlychosenbytes.jlocker.nonabstractreps.Locker;
import com.randomlychosenbytes.jlocker.nonabstractreps.Task;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.LinkedList;
import java.util.List;

/**
 * This is the main windows of the application. It is displayed right after
 * the login-dialog/create new user dialog.i
 */
public class MainFrame extends javax.swing.JFrame {
    private SearchFrame searchFrame;
    private TasksFrame tasksFrame;
    Timer timer;

    /**
     * In the future the DataManager class won't be a singleton anymore.
     * The reference will be passed from object to object.
     */
    DataManager dataManager = DataManager.getInstance();

    public MainFrame() {
        initComponents();

        // center on screen
        setLocationRelativeTo(null);

        //
        // Set application title from resources
        //
        setTitle(dataManager.getAppTitle() + " " + dataManager.getAppVersion());

        // TODO remove in later versions
        dataManager.setMainFrame(this);

        //
        // Ask to save changes on exit
        //
        addWindowListener(
                new java.awt.event.WindowAdapter() {
                    @Override
                    public void windowClosing(WindowEvent winEvt) {
                        if (dataManager.hasDataChanged()) {
                            int answer = JOptionPane.showConfirmDialog(null, "Wollen Sie Ihre Änderungen speichern?", "Speichern und beenden", JOptionPane.YES_NO_CANCEL_OPTION);

                            if (answer == JOptionPane.CANCEL_OPTION) {
                                return;
                            }

                            if (answer == JOptionPane.YES_OPTION) {
                                dataManager.saveAndCreateBackup();
                            }
                        }

                        System.exit(0);
                    }
                }
        );

        //
        // Initialize status message timer
        //
        ActionListener resetStatusMessage = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                statusMessageLabel.setText("");
            }
        };

        timer = new Timer(5000, resetStatusMessage);
        timer.setRepeats(true);

        //
        // Show CreateUserDialog if there are none
        //
        File newResFile = dataManager.getRessourceFile();

        if (!newResFile.exists()) {
            CreateUsersDialog dialog = new CreateUsersDialog(this, dataManager, true);
            dialog.setVisible(true);
        }

        //
        // LogIn
        //
        LogInDialog dialog = new LogInDialog(this, dataManager, true);
        dialog.setVisible(true);

        //
        // Initialize UI
        //
        setComboBoxes2CurIndizes();

        // If the super user is logged in, he is allowed to change to passwords.
        changeUserPWMenuItem.setEnabled(dataManager.getCurUser().isSuperUser());
    }

    /**
     * Put the lockers of the current walk in the layout manager.
     */
    public void drawLockerOverview() {
        // Remove old panels
        lockerOverviewPanel.removeAll();

        List<ManagementUnit> mus = dataManager.getCurManagmentUnitList();

        final int numMUnits = mus.size();
        boolean firstLockerFound = false;

        for (int i = numMUnits - 1; i >= 0; i--) {
            ManagementUnit mu = mus.get(i);

            //
            // mouse listeners get unattached during serialization, so they have to be set up again
            //
            mu.setUpMouseListeners();

            //
            // add management units to gui
            //
            lockerOverviewPanel.add(mu);

            //
            // set appropriate colors
            //
            List<Locker> lockers = mu.getLockerCabinet().getLockerList();

            for (Locker locker : lockers) {
                // always set a standard locker as selected
                if (!mu.getLockerList().isEmpty() && !firstLockerFound) {
                    mu.getLockerList().get(0).setSelected();
                    dataManager.setCurrentMUnitIndex(i);
                    dataManager.setCurrentLockerIndex(0);
                    firstLockerFound = true;
                } else {
                    locker.setAppropriateColor();
                }
            }
        }  // for

        showLockerInformation();
        lockerOverviewPanel.updateUI();
    }

    /**
     * When a locker is clicked, it's data is displayed in the respective
     * GUI components (surname, name, etc.)
     */
    public void showLockerInformation() {
        //
        // Initialize all childs of userDataPanel
        //
        if (!dataManager.getCurLockerList().isEmpty()) {
            containerPanel.setVisible(true);

            Locker locker = dataManager.getCurLocker();

            lockerIDTextField.setText(locker.getId());
            surnameTextField.setText(locker.getSurname());
            nameTextField.setText(locker.getOwnerName());
            classTextField.setText(locker.getOwnerClass());
            sizeTextField.setText(Integer.toString(locker.getOwnerSize()));

            hasContractCheckbox.setSelected(locker.hasContract());
            outOfOrderCheckbox.setSelected(locker.isOutOfOrder());

            moneyTextField.setText(Integer.toString(locker.getMoney()));
            previousAmountTextField.setText(Integer.toString(locker.getPrevAmount()));

            fromDateTextField.setText(locker.getFromDate());
            untilDateTextField.setText(locker.getUntilDate());


            Long months = locker.getRemainingTimeInMonths();
            remainingTimeInMonthsTextField.setText(months.toString() + " " + (months == 1 ? "Monat" : "Monate"));

            // Combobox initialization
            if (dataManager.getCurUser().isSuperUser()) {
                codeTextField.setText(locker.getCurrentCode(dataManager.getCurUser().getSuperUMasterKey()));
            } else {
                codeTextField.setText("00-00-00");
            }

            lockTextField.setText(locker.getLock());
            noteTextArea.setText(locker.getNote());
        } else {
            containerPanel.setVisible(false);
        }
    }

    /**
     * When executed the data from the GUI components is written into the locker
     * object.
     */
    private void setLockerInformation() {
        Locker locker = dataManager.getCurLocker();

        locker.setSirName(surnameTextField.getText());
        locker.setOwnerName(nameTextField.getText());
        locker.setClass(classTextField.getText());
        locker.setContract(hasContractCheckbox.isSelected());
        locker.setOutOfOrder(outOfOrderCheckbox.isSelected());
        locker.setLock(lockTextField.getText());
        locker.setNote(noteTextArea.getText());

        String id = lockerIDTextField.getText();

        if (!dataManager.getCurLocker().getId().equals(id)) {
            if (dataManager.isLockerIdUnique(id) && !id.equals("") && !id.equals(" ")) {
                locker.setID(id);
            } else {
                JOptionPane.showMessageDialog(null, "Diese Schließfach-ID existiert bereits! Wählen Sie eine andere.", "Fehler", JOptionPane.ERROR_MESSAGE);
                return;
            }
        }

        int size = 0;
        String sizeString = sizeTextField.getText();

        if (!sizeString.isEmpty()) {
            try {
                size = Integer.parseInt(sizeString);
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(null, "Die eigegebene Größe ist ungültig!", "Fehler", JOptionPane.ERROR_MESSAGE);
                return;
            }
        }

        locker.setOwnerSize(size);

        String from = fromDateTextField.getText();

        if (from.length() != 0) {
            if (!Locker.isDateValid(from)) {
                JOptionPane.showMessageDialog(null, "Das Anfangsdatum ist ungültig (Format DD.MM.YYYY)!", "Fehler", JOptionPane.ERROR_MESSAGE);
                return;
            } else {
                locker.setFromDate(from);
            }
        } else {
            locker.setFromDate(from);
        }

        String until = untilDateTextField.getText();

        if (until.length() != 0) {
            if (!Locker.isDateValid(until)) {
                JOptionPane.showMessageDialog(null, "Das Enddatum ist ungültig (Format DD.MM.YYYY)!", "Fehler", JOptionPane.ERROR_MESSAGE);
                return;
            } else {
                locker.setUntilDate(until);
            }
        } else {
            locker.setUntilDate(until);
        }

        Long months = locker.getRemainingTimeInMonths();
        remainingTimeInMonthsTextField.setText(months.toString() + " " + (months == 1 ? "Monat" : "Monate"));
    }

    public void setStatusMessage(String message) {
        if (timer.isRunning()) {
            timer.restart();
        } else {
            timer.start();
        }

        statusMessageLabel.setText(message);
    }

    /**
     * Determines the scroll position to bring a certain locker into sight.
     */
    public void bringCurrentLockerInSight() {
        Rectangle r = dataManager.getCurManamentUnit().getBounds();

        lockerOverviewScrollPane.getHorizontalScrollBar().setValue(r.x);
        lockerOverviewScrollPane.getVerticalScrollBar().setValue(dataManager.getCurLocker().getBounds().y);
    }

    /**
     * Initializes a combo box with a given list of entities.
     */
    private void initializeComboBox(Object obj, JComboBox combobox) {
        // cast here to avoid it during method call
        List<Entity> entityList = (List<Entity>) obj;

        // list which will contain all entity names
        List<String> entityNames = new LinkedList<>();

        for (Object entity : entityList) {
            entityNames.add(((Entity) entity).getSName());
        }

        combobox.setModel(new DefaultComboBoxModel(entityNames.toArray()));
    }

    /**
     * Sets all three combo boxes to the current indices of the building,
     * floor and walk.
     */
    public final void setComboBoxes2CurIndizes() {
        initializeComboBox(dataManager.getBuildingList(), buildingComboBox);
        buildingComboBox.setSelectedIndex(dataManager.getCurBuildingIndex());

        initializeComboBox(dataManager.getCurFloorList(), floorComboBox);
        floorComboBox.setSelectedIndex(dataManager.getCurFloorIndex());

        initializeComboBox(dataManager.getCurWalkList(), walkComboBox);
        walkComboBox.setSelectedIndex(dataManager.getCurWalkIndex());

        removeBuildingButton.setEnabled(dataManager.getBuildingList().size() > 1);
        removeFloorButton.setEnabled(dataManager.getCurFloorList().size() > 1);
        removeWalkButton.setEnabled(dataManager.getCurWalkList().size() > 1);

        drawLockerOverview();
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

        centerPanel = new javax.swing.JPanel();
        comboBoxPanel = new javax.swing.JPanel();
        buildingsPanel = new javax.swing.JPanel();
        buildingsLabel = new javax.swing.JLabel();
        buildingComboBox = new javax.swing.JComboBox();
        addBuildingButton = new javax.swing.JButton();
        removeBuildingButton = new javax.swing.JButton();
        editBuildingButton = new javax.swing.JButton();
        floorPanel = new javax.swing.JPanel();
        floorLabel = new javax.swing.JLabel();
        floorComboBox = new javax.swing.JComboBox();
        addFloorButton = new javax.swing.JButton();
        removeFloorButton = new javax.swing.JButton();
        editFloorButton = new javax.swing.JButton();
        walksPanel = new javax.swing.JPanel();
        walkLabel = new javax.swing.JLabel();
        walkComboBox = new javax.swing.JComboBox();
        addWalkButton = new javax.swing.JButton();
        removeWalkButton = new javax.swing.JButton();
        editWalkButton = new javax.swing.JButton();
        lockerOverviewScrollPane = new javax.swing.JScrollPane();
        lockerOverviewPanel = new javax.swing.JPanel();
        userScrollPane = new javax.swing.JScrollPane();
        containerPanel = new javax.swing.JPanel();
        leftFlowLayout = new javax.swing.JPanel();
        legendPanel = new javax.swing.JPanel();
        noContractPanel = new javax.swing.JPanel();
        noContractLabel = new javax.swing.JLabel();
        noContractColorLabel = new javax.swing.JPanel();
        freePanel = new javax.swing.JPanel();
        freeLabel = new javax.swing.JLabel();
        freeColorPanel = new javax.swing.JPanel();
        rentedPanel = new javax.swing.JPanel();
        rentedLabel = new javax.swing.JLabel();
        rentedColorPanel = new javax.swing.JPanel();
        outOfOrderPanel = new javax.swing.JPanel();
        outOfOrderLabel = new javax.swing.JLabel();
        outOfOrderColorPanel = new javax.swing.JPanel();
        oneMonthRemainingPanel = new javax.swing.JPanel();
        oneMonthRemainingLabel = new javax.swing.JLabel();
        oneMonthRemainingColorPanel = new javax.swing.JPanel();
        dataPanel = new javax.swing.JPanel();
        userPanel = new javax.swing.JPanel();
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
        noteLabel = new javax.swing.JLabel();
        noteTextArea = new javax.swing.JTextField();
        middlePanel = new javax.swing.JPanel();
        lockerPanel = new javax.swing.JPanel();
        fromDateLabel = new javax.swing.JLabel();
        fromDateTextField = new javax.swing.JTextField();
        untilDateLabel = new javax.swing.JLabel();
        untilDateTextField = new javax.swing.JTextField();
        remainingTimeInMonthsLabel = new javax.swing.JLabel();
        remainingTimeInMonthsTextField = new javax.swing.JTextField();
        currentPinLabel = new javax.swing.JLabel();
        codeTextField = new javax.swing.JTextField();
        lockLabel = new javax.swing.JLabel();
        lockTextField = new javax.swing.JTextField();
        checkBoxPanel = new javax.swing.JPanel();
        outOfOrderCheckbox = new javax.swing.JCheckBox();
        hasContractCheckbox = new javax.swing.JCheckBox();
        moneyPanel = new javax.swing.JPanel();
        gridLayoutPanel = new javax.swing.JPanel();
        moneyLabel = new javax.swing.JLabel();
        moneyTextField = new javax.swing.JTextField();
        previousAmountLabel = new javax.swing.JLabel();
        previousAmountTextField = new javax.swing.JTextField();
        currentAmountTextField = new javax.swing.JTextField();
        addAmountButton = new javax.swing.JButton();
        statusPanel = new javax.swing.JPanel();
        buttonPanel = new javax.swing.JPanel();
        saveButton = new javax.swing.JButton();
        emptyButton = new javax.swing.JButton();
        statusMessageLabel = new javax.swing.JLabel();
        menuBar = new javax.swing.JMenuBar();
        fileMenu = new javax.swing.JMenu();
        showTasksMenuItem = new javax.swing.JMenuItem();
        saveMenuItem = new javax.swing.JMenuItem();
        loadMenuItem = new javax.swing.JMenuItem();
        exitMenu = new javax.swing.JMenuItem();
        editMenu = new javax.swing.JMenu();
        changeClassMenuItem = new javax.swing.JMenuItem();
        moveLockerMenuItem = new javax.swing.JMenuItem();
        moveClassMenuItem = new javax.swing.JMenuItem();
        changeUserPWMenuItem = new javax.swing.JMenuItem();
        settingsMenuItem = new javax.swing.JMenuItem();
        searchMenu = new javax.swing.JMenu();
        searchMenuItem = new javax.swing.JMenuItem();
        helpMenu = new javax.swing.JMenu();
        aboutMenuItem = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setPreferredSize(new java.awt.Dimension(1000, 600));

        centerPanel.setBackground(new java.awt.Color(189, 205, 149));
        centerPanel.setLayout(new java.awt.BorderLayout());

        comboBoxPanel.setBackground(new java.awt.Color(189, 205, 149));
        comboBoxPanel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));

        buildingsPanel.setBackground(new java.awt.Color(189, 205, 149));

        buildingsLabel.setText("Gebäude");
        buildingsPanel.add(buildingsLabel);

        buildingComboBox.addPopupMenuListener(new javax.swing.event.PopupMenuListener() {
            public void popupMenuCanceled(javax.swing.event.PopupMenuEvent evt) {
            }

            public void popupMenuWillBecomeInvisible(javax.swing.event.PopupMenuEvent evt) {
                buildingComboBoxPopupMenuWillBecomeInvisible(evt);
            }

            public void popupMenuWillBecomeVisible(javax.swing.event.PopupMenuEvent evt) {
            }
        });
        buildingsPanel.add(buildingComboBox);

        addBuildingButton.setBackground(new java.awt.Color(189, 205, 149));
        addBuildingButton.setText("+");
        addBuildingButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addBuildingButtonActionPerformed(evt);
            }
        });
        buildingsPanel.add(addBuildingButton);

        removeBuildingButton.setBackground(new java.awt.Color(189, 205, 149));
        removeBuildingButton.setText("-");
        removeBuildingButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                removeBuildingButtonActionPerformed(evt);
            }
        });
        buildingsPanel.add(removeBuildingButton);

        editBuildingButton.setBackground(new java.awt.Color(189, 205, 149));
        editBuildingButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/gray gear.png"))); // NOI18N
        editBuildingButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                editBuildingButtonActionPerformed(evt);
            }
        });
        buildingsPanel.add(editBuildingButton);

        comboBoxPanel.add(buildingsPanel);

        floorPanel.setBackground(new java.awt.Color(189, 205, 149));

        floorLabel.setText("Etage");
        floorPanel.add(floorLabel);

        floorComboBox.addPopupMenuListener(new javax.swing.event.PopupMenuListener() {
            public void popupMenuCanceled(javax.swing.event.PopupMenuEvent evt) {
            }

            public void popupMenuWillBecomeInvisible(javax.swing.event.PopupMenuEvent evt) {
                floorComboBoxPopupMenuWillBecomeInvisible(evt);
            }

            public void popupMenuWillBecomeVisible(javax.swing.event.PopupMenuEvent evt) {
            }
        });
        floorPanel.add(floorComboBox);

        addFloorButton.setBackground(new java.awt.Color(189, 205, 149));
        addFloorButton.setText("+");
        addFloorButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addFloorButtonActionPerformed(evt);
            }
        });
        floorPanel.add(addFloorButton);

        removeFloorButton.setBackground(new java.awt.Color(189, 205, 149));
        removeFloorButton.setText("-");
        removeFloorButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                removeFloorButtonActionPerformed(evt);
            }
        });
        floorPanel.add(removeFloorButton);

        editFloorButton.setBackground(new java.awt.Color(189, 205, 149));
        editFloorButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/gray gear.png"))); // NOI18N
        editFloorButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                editFloorButtonActionPerformed(evt);
            }
        });
        floorPanel.add(editFloorButton);

        comboBoxPanel.add(floorPanel);

        walksPanel.setBackground(new java.awt.Color(189, 205, 149));

        walkLabel.setText("Gang");
        walksPanel.add(walkLabel);

        walkComboBox.addPopupMenuListener(new javax.swing.event.PopupMenuListener() {
            public void popupMenuCanceled(javax.swing.event.PopupMenuEvent evt) {
            }

            public void popupMenuWillBecomeInvisible(javax.swing.event.PopupMenuEvent evt) {
                walkComboBoxPopupMenuWillBecomeInvisible(evt);
            }

            public void popupMenuWillBecomeVisible(javax.swing.event.PopupMenuEvent evt) {
            }
        });
        walksPanel.add(walkComboBox);

        addWalkButton.setBackground(new java.awt.Color(189, 205, 149));
        addWalkButton.setText("+");
        addWalkButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addWalkButtonActionPerformed(evt);
            }
        });
        walksPanel.add(addWalkButton);

        removeWalkButton.setBackground(new java.awt.Color(189, 205, 149));
        removeWalkButton.setText("-");
        removeWalkButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                removeWalkButtonActionPerformed(evt);
            }
        });
        walksPanel.add(removeWalkButton);

        editWalkButton.setBackground(new java.awt.Color(189, 205, 149));
        editWalkButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/gray gear.png"))); // NOI18N
        editWalkButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                editWalkButtonActionPerformed(evt);
            }
        });
        walksPanel.add(editWalkButton);

        comboBoxPanel.add(walksPanel);

        centerPanel.add(comboBoxPanel, java.awt.BorderLayout.NORTH);

        lockerOverviewScrollPane.setBorder(null);

        lockerOverviewPanel.setBackground(new java.awt.Color(189, 205, 149));
        lockerOverviewPanel.setLayout(new java.awt.GridLayout(1, 0, 5, 0));
        lockerOverviewScrollPane.setViewportView(lockerOverviewPanel);

        centerPanel.add(lockerOverviewScrollPane, java.awt.BorderLayout.CENTER);

        containerPanel.setBackground(new java.awt.Color(189, 205, 149));
        containerPanel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));

        leftFlowLayout.setBackground(new java.awt.Color(189, 205, 149));
        leftFlowLayout.setLayout(new java.awt.GridBagLayout());

        legendPanel.setBackground(new java.awt.Color(189, 205, 149));

        noContractPanel.setBackground(new java.awt.Color(189, 205, 149));

        noContractLabel.setText("kein Vertrag");
        noContractPanel.add(noContractLabel);

        noContractColorLabel.setBackground(new java.awt.Color(0, 0, 255));
        noContractColorLabel.setPreferredSize(new java.awt.Dimension(8, 8));

        javax.swing.GroupLayout noContractColorLabelLayout = new javax.swing.GroupLayout(noContractColorLabel);
        noContractColorLabel.setLayout(noContractColorLabelLayout);
        noContractColorLabelLayout.setHorizontalGroup(
                noContractColorLabelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGap(0, 8, Short.MAX_VALUE)
        );
        noContractColorLabelLayout.setVerticalGroup(
                noContractColorLabelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGap(0, 8, Short.MAX_VALUE)
        );

        noContractPanel.add(noContractColorLabel);

        legendPanel.add(noContractPanel);

        freePanel.setBackground(new java.awt.Color(189, 205, 149));

        freeLabel.setText("Frei");
        freePanel.add(freeLabel);

        freeColorPanel.setBackground(new java.awt.Color(255, 255, 255));
        freeColorPanel.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        freeColorPanel.setPreferredSize(new java.awt.Dimension(8, 8));

        javax.swing.GroupLayout freeColorPanelLayout = new javax.swing.GroupLayout(freeColorPanel);
        freeColorPanel.setLayout(freeColorPanelLayout);
        freeColorPanelLayout.setHorizontalGroup(
                freeColorPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGap(0, 6, Short.MAX_VALUE)
        );
        freeColorPanelLayout.setVerticalGroup(
                freeColorPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGap(0, 6, Short.MAX_VALUE)
        );

        freePanel.add(freeColorPanel);

        legendPanel.add(freePanel);

        rentedPanel.setBackground(new java.awt.Color(189, 205, 149));

        rentedLabel.setText("Vermietet");
        rentedPanel.add(rentedLabel);

        rentedColorPanel.setBackground(new java.awt.Color(0, 102, 0));
        rentedColorPanel.setPreferredSize(new java.awt.Dimension(8, 8));

        javax.swing.GroupLayout rentedColorPanelLayout = new javax.swing.GroupLayout(rentedColorPanel);
        rentedColorPanel.setLayout(rentedColorPanelLayout);
        rentedColorPanelLayout.setHorizontalGroup(
                rentedColorPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGap(0, 8, Short.MAX_VALUE)
        );
        rentedColorPanelLayout.setVerticalGroup(
                rentedColorPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGap(0, 8, Short.MAX_VALUE)
        );

        rentedPanel.add(rentedColorPanel);

        legendPanel.add(rentedPanel);

        outOfOrderPanel.setBackground(new java.awt.Color(189, 205, 149));

        outOfOrderLabel.setText("Defekt");
        outOfOrderPanel.add(outOfOrderLabel);

        outOfOrderColorPanel.setBackground(new java.awt.Color(255, 0, 0));
        outOfOrderColorPanel.setPreferredSize(new java.awt.Dimension(8, 8));

        javax.swing.GroupLayout outOfOrderColorPanelLayout = new javax.swing.GroupLayout(outOfOrderColorPanel);
        outOfOrderColorPanel.setLayout(outOfOrderColorPanelLayout);
        outOfOrderColorPanelLayout.setHorizontalGroup(
                outOfOrderColorPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGap(0, 8, Short.MAX_VALUE)
        );
        outOfOrderColorPanelLayout.setVerticalGroup(
                outOfOrderColorPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGap(0, 8, Short.MAX_VALUE)
        );

        outOfOrderPanel.add(outOfOrderColorPanel);

        legendPanel.add(outOfOrderPanel);

        oneMonthRemainingPanel.setBackground(new java.awt.Color(189, 205, 149));

        oneMonthRemainingLabel.setText("Mietdauer >= 1 Monat");
        oneMonthRemainingPanel.add(oneMonthRemainingLabel);

        oneMonthRemainingColorPanel.setBackground(new java.awt.Color(255, 153, 0));
        oneMonthRemainingColorPanel.setPreferredSize(new java.awt.Dimension(8, 8));

        javax.swing.GroupLayout oneMonthRemainingColorPanelLayout = new javax.swing.GroupLayout(oneMonthRemainingColorPanel);
        oneMonthRemainingColorPanel.setLayout(oneMonthRemainingColorPanelLayout);
        oneMonthRemainingColorPanelLayout.setHorizontalGroup(
                oneMonthRemainingColorPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGap(0, 8, Short.MAX_VALUE)
        );
        oneMonthRemainingColorPanelLayout.setVerticalGroup(
                oneMonthRemainingColorPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGap(0, 8, Short.MAX_VALUE)
        );

        oneMonthRemainingPanel.add(oneMonthRemainingColorPanel);

        legendPanel.add(oneMonthRemainingPanel);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        leftFlowLayout.add(legendPanel, gridBagConstraints);

        dataPanel.setBackground(new java.awt.Color(189, 205, 149));
        dataPanel.setLayout(new java.awt.GridBagLayout());

        userPanel.setBackground(new java.awt.Color(189, 205, 149));
        userPanel.setLayout(new java.awt.GridLayout(6, 2, 5, 3));

        lockerIDLabel.setText("Schließfach-ID           ");
        userPanel.add(lockerIDLabel);
        userPanel.add(lockerIDTextField);

        surnameLabel.setText("Nachname");
        userPanel.add(surnameLabel);
        userPanel.add(surnameTextField);

        nameLabel.setText("Vorname");
        userPanel.add(nameLabel);
        userPanel.add(nameTextField);

        classLabel.setText("Klasse");
        userPanel.add(classLabel);
        userPanel.add(classTextField);

        sizeLabel.setText("Größe");
        userPanel.add(sizeLabel);
        userPanel.add(sizeTextField);

        noteLabel.setText("Notiz");
        userPanel.add(noteLabel);
        userPanel.add(noteTextArea);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 20);
        dataPanel.add(userPanel, gridBagConstraints);

        middlePanel.setBackground(new java.awt.Color(189, 205, 149));
        middlePanel.setLayout(new java.awt.GridBagLayout());

        lockerPanel.setBackground(new java.awt.Color(189, 205, 149));
        lockerPanel.setLayout(new java.awt.GridLayout(5, 2, 5, 3));

        fromDateLabel.setText("von");
        lockerPanel.add(fromDateLabel);
        lockerPanel.add(fromDateTextField);

        untilDateLabel.setText("bis");
        lockerPanel.add(untilDateLabel);
        lockerPanel.add(untilDateTextField);

        remainingTimeInMonthsLabel.setText("verbleibende Monate");
        lockerPanel.add(remainingTimeInMonthsLabel);
        lockerPanel.add(remainingTimeInMonthsTextField);

        currentPinLabel.setText("aktueller Code");
        lockerPanel.add(currentPinLabel);

        codeTextField.setEditable(false);
        codeTextField.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                codeTextFieldMouseClicked(evt);
            }
        });
        lockerPanel.add(codeTextField);

        lockLabel.setText("Schloss");
        lockerPanel.add(lockLabel);
        lockerPanel.add(lockTextField);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        middlePanel.add(lockerPanel, gridBagConstraints);

        checkBoxPanel.setBackground(new java.awt.Color(189, 205, 149));
        checkBoxPanel.setLayout(new java.awt.GridBagLayout());

        outOfOrderCheckbox.setBackground(new java.awt.Color(189, 205, 149));
        outOfOrderCheckbox.setText("defekt");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        checkBoxPanel.add(outOfOrderCheckbox, gridBagConstraints);

        hasContractCheckbox.setBackground(new java.awt.Color(189, 205, 149));
        hasContractCheckbox.setText("Vertrag");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        checkBoxPanel.add(hasContractCheckbox, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        middlePanel.add(checkBoxPanel, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 30);
        dataPanel.add(middlePanel, gridBagConstraints);

        moneyPanel.setBackground(new java.awt.Color(189, 205, 149));
        moneyPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(131, 150, 81)), "Finanzen"));
        moneyPanel.setLayout(new java.awt.GridBagLayout());

        gridLayoutPanel.setBackground(new java.awt.Color(189, 205, 149));
        gridLayoutPanel.setLayout(new java.awt.GridLayout(3, 2, 10, 3));

        moneyLabel.setText("Kontostand");
        gridLayoutPanel.add(moneyLabel);

        moneyTextField.setEditable(false);
        gridLayoutPanel.add(moneyTextField);

        previousAmountLabel.setText("zuletzt eingezahlt");
        gridLayoutPanel.add(previousAmountLabel);

        previousAmountTextField.setEditable(false);
        gridLayoutPanel.add(previousAmountTextField);

        currentAmountTextField.setColumns(3);
        gridLayoutPanel.add(currentAmountTextField);

        addAmountButton.setBackground(new java.awt.Color(189, 205, 149));
        addAmountButton.setText("Einzahlen");
        addAmountButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addAmountButtonActionPerformed(evt);
            }
        });
        gridLayoutPanel.add(addAmountButton);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        moneyPanel.add(gridLayoutPanel, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        dataPanel.add(moneyPanel, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 0, 0);
        leftFlowLayout.add(dataPanel, gridBagConstraints);

        statusPanel.setBackground(new java.awt.Color(189, 205, 149));
        statusPanel.setLayout(new java.awt.GridBagLayout());

        buttonPanel.setBackground(new java.awt.Color(189, 205, 149));
        buttonPanel.setLayout(new java.awt.GridBagLayout());

        saveButton.setBackground(new java.awt.Color(189, 205, 149));
        saveButton.setText("Speichern");
        saveButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 10);
        buttonPanel.add(saveButton, gridBagConstraints);

        emptyButton.setBackground(new java.awt.Color(189, 205, 149));
        emptyButton.setText("Leeren");
        emptyButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                emptyButtonActionPerformed(evt);
            }
        });
        buttonPanel.add(emptyButton, new java.awt.GridBagConstraints());

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        statusPanel.add(buttonPanel, gridBagConstraints);

        statusMessageLabel.setText("Status");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 20, 0, 0);
        statusPanel.add(statusMessageLabel, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 0);
        leftFlowLayout.add(statusPanel, gridBagConstraints);

        containerPanel.add(leftFlowLayout);

        userScrollPane.setViewportView(containerPanel);

        centerPanel.add(userScrollPane, java.awt.BorderLayout.SOUTH);

        getContentPane().add(centerPanel, java.awt.BorderLayout.CENTER);

        fileMenu.setText("Datei");

        showTasksMenuItem.setText("Aufgaben anzeigen");
        showTasksMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                showTasksMenuItemActionPerformed(evt);
            }
        });
        fileMenu.add(showTasksMenuItem);

        saveMenuItem.setText("Speichern");
        saveMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveMenuItemActionPerformed(evt);
            }
        });
        fileMenu.add(saveMenuItem);

        loadMenuItem.setText("Laden");
        loadMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                loadMenuItemActionPerformed(evt);
            }
        });
        fileMenu.add(loadMenuItem);

        exitMenu.setText("Beenden");
        exitMenu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                exitMenuActionPerformed(evt);
            }
        });
        fileMenu.add(exitMenu);

        menuBar.add(fileMenu);

        editMenu.setText("Bearbeiten");

        changeClassMenuItem.setText("Klassenänderung");
        changeClassMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                changeClassMenuItemActionPerformed(evt);
            }
        });
        editMenu.add(changeClassMenuItem);

        moveLockerMenuItem.setText("Schließfachumzug");
        moveLockerMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                moveLockerMenuItemActionPerformed(evt);
            }
        });
        editMenu.add(moveLockerMenuItem);

        moveClassMenuItem.setText("Klassenumzug");
        moveClassMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                moveClassMenuItemActionPerformed(evt);
            }
        });
        editMenu.add(moveClassMenuItem);

        changeUserPWMenuItem.setText("Benutzerpasswörter ändern");
        changeUserPWMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                changeUserPWMenuItemActionPerformed(evt);
            }
        });
        editMenu.add(changeUserPWMenuItem);

        settingsMenuItem.setText("Einstellungen");
        settingsMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                settingsMenuItemActionPerformed(evt);
            }
        });
        editMenu.add(settingsMenuItem);

        menuBar.add(editMenu);

        searchMenu.setText("Suche");

        searchMenuItem.setText("Suche");
        searchMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                searchMenuItemActionPerformed(evt);
            }
        });
        searchMenu.add(searchMenuItem);

        menuBar.add(searchMenu);

        helpMenu.setText("Hilfe");

        aboutMenuItem.setText("Über jLocker");
        aboutMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                aboutMenuItemActionPerformed(evt);
            }
        });
        helpMenu.add(aboutMenuItem);

        menuBar.add(helpMenu);

        setJMenuBar(menuBar);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void removeBuildingButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_removeBuildingButtonActionPerformed
        int answer = JOptionPane.showConfirmDialog(null, "Wollen Sie dieses Gebäude wirklich löschen?", "Gebäude löschen", JOptionPane.YES_NO_CANCEL_OPTION);

        if (answer == JOptionPane.YES_OPTION) {
            dataManager.getBuildingList().remove(dataManager.getCurBuildingIndex());

            dataManager.setCurrentBuildingIndex(dataManager.getBuildingList().size() - 1);
            dataManager.setCurrentFloorIndex(0);
            dataManager.setCurrentWalkIndex(0);
            dataManager.setCurrentMUnitIndex(0);
            dataManager.setCurrentLockerIndex(0);

            setComboBoxes2CurIndizes();
        }
    }//GEN-LAST:event_removeBuildingButtonActionPerformed

    private void removeFloorButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_removeFloorButtonActionPerformed
        int answer = JOptionPane.showConfirmDialog(null, "Wollen Sie diese Etage wirklich löschen?", "Etage löschen", JOptionPane.YES_NO_CANCEL_OPTION);

        if (answer == JOptionPane.YES_OPTION) {
            dataManager.getCurFloorList().remove(dataManager.getCurFloorIndex());

            dataManager.setCurrentFloorIndex(dataManager.getCurFloorList().size() - 1);
            dataManager.setCurrentWalkIndex(0);
            dataManager.setCurrentMUnitIndex(0);
            dataManager.setCurrentLockerIndex(0);

            setComboBoxes2CurIndizes();
        }
    }//GEN-LAST:event_removeFloorButtonActionPerformed

    private void removeWalkButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_removeWalkButtonActionPerformed
        int answer = JOptionPane.showConfirmDialog(null, "Wollen Sie diesen Gang wirklich löschen?", "Gang löschen", JOptionPane.YES_NO_CANCEL_OPTION);

        if (answer == JOptionPane.YES_OPTION) {
            dataManager.getCurWalkList().remove(dataManager.getCurWalkIndex());
            dataManager.setCurrentWalkIndex(dataManager.getCurWalkList().size() - 1);
            dataManager.setCurrentMUnitIndex(0);
            dataManager.setCurrentLockerIndex(0);

            setComboBoxes2CurIndizes();
        }
    }//GEN-LAST:event_removeWalkButtonActionPerformed

    private void saveButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_saveButtonActionPerformed
    {//GEN-HEADEREND:event_saveButtonActionPerformed
        setLockerInformation();
        dataManager.setDataChanged(true);
    }//GEN-LAST:event_saveButtonActionPerformed

    private void codeTextFieldMouseClicked(java.awt.event.MouseEvent evt)//GEN-FIRST:event_codeTextFieldMouseClicked
    {//GEN-HEADEREND:event_codeTextFieldMouseClicked
        if (dataManager.getCurUser().isSuperUser()) {
            EditCodesDialog dialog = new EditCodesDialog(this, dataManager, true);
            dialog.setVisible(true);
        }
    }//GEN-LAST:event_codeTextFieldMouseClicked

    private void emptyButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_emptyButtonActionPerformed
    {//GEN-HEADEREND:event_emptyButtonActionPerformed
        dataManager.setDataChanged(true);

        int answer = JOptionPane.showConfirmDialog(null, "Wollen Sie dieses Schließfach wirklich leeren?", "Schließfach leeren", JOptionPane.YES_NO_CANCEL_OPTION);

        if (answer == JOptionPane.YES_OPTION) {
            surnameTextField.setText("");
            nameTextField.setText("");
            classTextField.setText("");
            sizeTextField.setText("0");
            hasContractCheckbox.setSelected(false);
            moneyTextField.setText("0");
            previousAmountTextField.setText("0");
            remainingTimeInMonthsTextField.setText("");
            fromDateTextField.setText("");
            untilDateTextField.setText("");
            noteTextArea.setText("");

            Locker locker = dataManager.getCurLocker();
            locker.empty();

            // Combobox initialization
            if (dataManager.getCurUser().isSuperUser()) {
                codeTextField.setText(locker.getCurrentCode(dataManager.getCurUser().getSuperUMasterKey()));
            } else {
                codeTextField.setText("00-00-00");
            }
        }
    }//GEN-LAST:event_emptyButtonActionPerformed

    private void addBuildingButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_addBuildingButtonActionPerformed
    {//GEN-HEADEREND:event_addBuildingButtonActionPerformed
        BuildingDialog dialog = new BuildingDialog(this, true, dataManager, BuildingDialog.ADD);
        dialog.setVisible(true);
    }//GEN-LAST:event_addBuildingButtonActionPerformed

    private void addFloorButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_addFloorButtonActionPerformed
    {//GEN-HEADEREND:event_addFloorButtonActionPerformed
        FloorDialog dialog = new FloorDialog(this, dataManager, true, FloorDialog.ADD);
        dialog.setVisible(true);
    }//GEN-LAST:event_addFloorButtonActionPerformed

    private void editFloorButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_editFloorButtonActionPerformed
    {//GEN-HEADEREND:event_editFloorButtonActionPerformed
        FloorDialog dialog = new FloorDialog(this, dataManager, true, FloorDialog.EDIT);
        dialog.setVisible(true);
    }//GEN-LAST:event_editFloorButtonActionPerformed

    private void addWalkButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_addWalkButtonActionPerformed
    {//GEN-HEADEREND:event_addWalkButtonActionPerformed
        WalkDialog dialog = new WalkDialog(this, dataManager, true, WalkDialog.ADD);
        dialog.setVisible(true);
    }//GEN-LAST:event_addWalkButtonActionPerformed

    private void editWalkButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_editWalkButtonActionPerformed
    {//GEN-HEADEREND:event_editWalkButtonActionPerformed
        WalkDialog dialog = new WalkDialog(this, dataManager, true, WalkDialog.EDIT);
        dialog.setVisible(true);
    }//GEN-LAST:event_editWalkButtonActionPerformed

    private void editBuildingButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_editBuildingButtonActionPerformed
    {//GEN-HEADEREND:event_editBuildingButtonActionPerformed
        BuildingDialog dialog = new BuildingDialog(this, true, dataManager, BuildingDialog.EDIT);
        dialog.setVisible(true);
    }//GEN-LAST:event_editBuildingButtonActionPerformed

    private void buildingComboBoxPopupMenuWillBecomeInvisible(javax.swing.event.PopupMenuEvent evt)//GEN-FIRST:event_buildingComboBoxPopupMenuWillBecomeInvisible
    {//GEN-HEADEREND:event_buildingComboBoxPopupMenuWillBecomeInvisible
        dataManager.setCurrentBuildingIndex(buildingComboBox.getSelectedIndex());

        initializeComboBox(dataManager.getCurFloorList(), floorComboBox);

        // move on to next combobox
        floorComboBoxPopupMenuWillBecomeInvisible(null);
    }//GEN-LAST:event_buildingComboBoxPopupMenuWillBecomeInvisible

    private void floorComboBoxPopupMenuWillBecomeInvisible(javax.swing.event.PopupMenuEvent evt)//GEN-FIRST:event_floorComboBoxPopupMenuWillBecomeInvisible
    {//GEN-HEADEREND:event_floorComboBoxPopupMenuWillBecomeInvisible
        dataManager.setCurrentFloorIndex(floorComboBox.getSelectedIndex());

        initializeComboBox(dataManager.getCurWalkList(), walkComboBox);

        // move on to next combobox
        walkComboBoxPopupMenuWillBecomeInvisible(null);
    }//GEN-LAST:event_floorComboBoxPopupMenuWillBecomeInvisible

    private void walkComboBoxPopupMenuWillBecomeInvisible(javax.swing.event.PopupMenuEvent evt)//GEN-FIRST:event_walkComboBoxPopupMenuWillBecomeInvisible
    {//GEN-HEADEREND:event_walkComboBoxPopupMenuWillBecomeInvisible
        dataManager.setCurrentWalkIndex(walkComboBox.getSelectedIndex());
        dataManager.setCurrentMUnitIndex(0);

        removeBuildingButton.setEnabled(dataManager.getBuildingList().size() > 1);
        removeFloorButton.setEnabled(dataManager.getCurFloorList().size() > 1);
        removeWalkButton.setEnabled(dataManager.getCurWalkList().size() > 1);

        drawLockerOverview();
    }//GEN-LAST:event_walkComboBoxPopupMenuWillBecomeInvisible

    private void addAmountButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_addAmountButtonActionPerformed
    {//GEN-HEADEREND:event_addAmountButtonActionPerformed
        try {
            int amount = new Integer(currentAmountTextField.getText());

            dataManager.getCurLocker().setPrevAmount(amount);
            int iNewFullAmount = dataManager.getCurLocker().getMoney() + amount;
            dataManager.getCurLocker().setMoney(iNewFullAmount);

            previousAmountTextField.setText(Integer.toString(amount));
            moneyTextField.setText(Integer.toString(iNewFullAmount));

            currentAmountTextField.setText("");
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "Bitte geben Sie einen gültigen Geldbetrag ein!", "Fehler", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_addAmountButtonActionPerformed

    private void showTasksMenuItemActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_showTasksMenuItemActionPerformed
    {//GEN-HEADEREND:event_showTasksMenuItemActionPerformed
        if (tasksFrame != null) {
            tasksFrame.dispose();
        }

        tasksFrame = new TasksFrame(dataManager);
        tasksFrame.setVisible(true);
    }//GEN-LAST:event_showTasksMenuItemActionPerformed

    private void saveMenuItemActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_saveMenuItemActionPerformed
    {//GEN-HEADEREND:event_saveMenuItemActionPerformed
        // if the tasks object has not been initialised, it is done now
        if (dataManager.getTasks() == null) {
            dataManager.setTaskList(new LinkedList<Task>());
        }

        // if the settings object has not been initialised, it is done now
        if (dataManager.getSettings() == null) {
            dataManager.loadDefaultSettings();
        }

        dataManager.saveAndCreateBackup();
    }//GEN-LAST:event_saveMenuItemActionPerformed

    private void loadMenuItemActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_loadMenuItemActionPerformed
    {//GEN-HEADEREND:event_loadMenuItemActionPerformed
        dataManager.loadDefaultFile();
        dataManager.initBuildingObject();
    }//GEN-LAST:event_loadMenuItemActionPerformed

    private void exitMenuActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_exitMenuActionPerformed
    {//GEN-HEADEREND:event_exitMenuActionPerformed
        System.exit(0);
    }//GEN-LAST:event_exitMenuActionPerformed

    private void aboutMenuItemActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_aboutMenuItemActionPerformed
    {//GEN-HEADEREND:event_aboutMenuItemActionPerformed
        AboutBox dialog = new AboutBox(this, true);
        dialog.setVisible(true);
    }//GEN-LAST:event_aboutMenuItemActionPerformed

    private void changeUserPWMenuItemActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_changeUserPWMenuItemActionPerformed
    {//GEN-HEADEREND:event_changeUserPWMenuItemActionPerformed
        CreateUsersDialog dialog = new CreateUsersDialog(this, dataManager, true);
        dialog.setVisible(true);
    }//GEN-LAST:event_changeUserPWMenuItemActionPerformed

    private void settingsMenuItemActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_settingsMenuItemActionPerformed
    {//GEN-HEADEREND:event_settingsMenuItemActionPerformed
        SettingsDialog dialog = new SettingsDialog(this, dataManager, true);
        dialog.setVisible(true);
    }//GEN-LAST:event_settingsMenuItemActionPerformed

    private void changeClassMenuItemActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_changeClassMenuItemActionPerformed
    {//GEN-HEADEREND:event_changeClassMenuItemActionPerformed
        RenameClassDialog dialog = new RenameClassDialog(this, dataManager, true);
        dialog.setVisible(true);
    }//GEN-LAST:event_changeClassMenuItemActionPerformed

    private void searchMenuItemActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_searchMenuItemActionPerformed
    {//GEN-HEADEREND:event_searchMenuItemActionPerformed
        if (searchFrame != null) {
            searchFrame.dispose();
        }

        searchFrame = new SearchFrame(this, dataManager);
        searchFrame.setVisible(true);

    }//GEN-LAST:event_searchMenuItemActionPerformed

    private void moveClassMenuItemActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_moveClassMenuItemActionPerformed
    {//GEN-HEADEREND:event_moveClassMenuItemActionPerformed
        MoveClassDialog dialog = new MoveClassDialog(this, dataManager, true);
        dialog.setVisible(true);
    }//GEN-LAST:event_moveClassMenuItemActionPerformed

    private void moveLockerMenuItemActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_moveLockerMenuItemActionPerformed
    {//GEN-HEADEREND:event_moveLockerMenuItemActionPerformed
        MoveLockerDialog dialog = new MoveLockerDialog(this, dataManager, true);
        dialog.setVisible(true);
    }//GEN-LAST:event_moveLockerMenuItemActionPerformed

    public static void main(String args[]) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | javax.swing.UnsupportedLookAndFeelException ex) {
            System.err.println("Could not set design!");
        }

        // Create and display the form
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                new MainFrame().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JMenuItem aboutMenuItem;
    private javax.swing.JButton addAmountButton;
    private javax.swing.JButton addBuildingButton;
    private javax.swing.JButton addFloorButton;
    private javax.swing.JButton addWalkButton;
    private javax.swing.JComboBox buildingComboBox;
    private javax.swing.JLabel buildingsLabel;
    private javax.swing.JPanel buildingsPanel;
    private javax.swing.JPanel buttonPanel;
    private javax.swing.JPanel centerPanel;
    private javax.swing.JMenuItem changeClassMenuItem;
    private javax.swing.JMenuItem changeUserPWMenuItem;
    private javax.swing.JPanel checkBoxPanel;
    private javax.swing.JLabel classLabel;
    private javax.swing.JTextField classTextField;
    private javax.swing.JTextField codeTextField;
    private javax.swing.JPanel comboBoxPanel;
    private javax.swing.JPanel containerPanel;
    private javax.swing.JTextField currentAmountTextField;
    private javax.swing.JLabel currentPinLabel;
    private javax.swing.JPanel dataPanel;
    private javax.swing.JButton editBuildingButton;
    private javax.swing.JButton editFloorButton;
    private javax.swing.JMenu editMenu;
    private javax.swing.JButton editWalkButton;
    private javax.swing.JButton emptyButton;
    private javax.swing.JMenuItem exitMenu;
    private javax.swing.JMenu fileMenu;
    private javax.swing.JComboBox floorComboBox;
    private javax.swing.JLabel floorLabel;
    private javax.swing.JPanel floorPanel;
    private javax.swing.JPanel freeColorPanel;
    private javax.swing.JLabel freeLabel;
    private javax.swing.JPanel freePanel;
    private javax.swing.JLabel fromDateLabel;
    private javax.swing.JTextField fromDateTextField;
    private javax.swing.JPanel gridLayoutPanel;
    private javax.swing.JCheckBox hasContractCheckbox;
    private javax.swing.JMenu helpMenu;
    private javax.swing.JPanel leftFlowLayout;
    private javax.swing.JPanel legendPanel;
    private javax.swing.JMenuItem loadMenuItem;
    private javax.swing.JLabel lockLabel;
    private javax.swing.JTextField lockTextField;
    private javax.swing.JLabel lockerIDLabel;
    private javax.swing.JTextField lockerIDTextField;
    private javax.swing.JPanel lockerOverviewPanel;
    private javax.swing.JScrollPane lockerOverviewScrollPane;
    private javax.swing.JPanel lockerPanel;
    private javax.swing.JMenuBar menuBar;
    private javax.swing.JPanel middlePanel;
    private javax.swing.JLabel moneyLabel;
    private javax.swing.JPanel moneyPanel;
    private javax.swing.JTextField moneyTextField;
    private javax.swing.JMenuItem moveClassMenuItem;
    private javax.swing.JMenuItem moveLockerMenuItem;
    private javax.swing.JLabel nameLabel;
    private javax.swing.JTextField nameTextField;
    private javax.swing.JPanel noContractColorLabel;
    private javax.swing.JLabel noContractLabel;
    private javax.swing.JPanel noContractPanel;
    private javax.swing.JLabel noteLabel;
    private javax.swing.JTextField noteTextArea;
    private javax.swing.JPanel oneMonthRemainingColorPanel;
    private javax.swing.JLabel oneMonthRemainingLabel;
    private javax.swing.JPanel oneMonthRemainingPanel;
    private javax.swing.JCheckBox outOfOrderCheckbox;
    private javax.swing.JPanel outOfOrderColorPanel;
    private javax.swing.JLabel outOfOrderLabel;
    private javax.swing.JPanel outOfOrderPanel;
    private javax.swing.JLabel previousAmountLabel;
    private javax.swing.JTextField previousAmountTextField;
    private javax.swing.JLabel remainingTimeInMonthsLabel;
    private javax.swing.JTextField remainingTimeInMonthsTextField;
    private javax.swing.JButton removeBuildingButton;
    private javax.swing.JButton removeFloorButton;
    private javax.swing.JButton removeWalkButton;
    private javax.swing.JPanel rentedColorPanel;
    private javax.swing.JLabel rentedLabel;
    private javax.swing.JPanel rentedPanel;
    private javax.swing.JButton saveButton;
    private javax.swing.JMenuItem saveMenuItem;
    private javax.swing.JMenu searchMenu;
    private javax.swing.JMenuItem searchMenuItem;
    private javax.swing.JMenuItem settingsMenuItem;
    private javax.swing.JMenuItem showTasksMenuItem;
    private javax.swing.JLabel sizeLabel;
    private javax.swing.JTextField sizeTextField;
    private javax.swing.JLabel statusMessageLabel;
    private javax.swing.JPanel statusPanel;
    private javax.swing.JLabel surnameLabel;
    private javax.swing.JTextField surnameTextField;
    private javax.swing.JLabel untilDateLabel;
    private javax.swing.JTextField untilDateTextField;
    private javax.swing.JPanel userPanel;
    private javax.swing.JScrollPane userScrollPane;
    private javax.swing.JComboBox walkComboBox;
    private javax.swing.JLabel walkLabel;
    private javax.swing.JPanel walksPanel;
    // End of variables declaration//GEN-END:variables
}
