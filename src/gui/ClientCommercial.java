/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package gui;

import javax.swing.*;
import javax.swing.JOptionPane;
import queries.Client;
import queries.UserState;
import queries.SharedData;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
/**
 *
 * @author Rica Mae
 */
public class ClientCommercial extends javax.swing.JFrame {
    Client client = new Client();
    private static String meterType = "";
    private boolean isMeterRunning = false; 
    private Timer timer;  
    private int meterID;
    private DefaultListModel<String> meterList;
    private Map<Integer, Timer> meterTimers = new HashMap<>();
    //private int loggedInUserID = UserState.verifiedID;

    /**
     * Creates new form ClientCommercial
     */
    public ClientCommercial() {
        if (!UserState.isVerified) {
            JOptionPane.showMessageDialog(this, "You must log in first!", "Login Required", JOptionPane.WARNING_MESSAGE);
            this.dispose();
            return;
        }
        
        meterType = client.getMeterType(SharedData.clientID);
        meterID = client.getMeterID(SharedData.clientID);
        
        meterList = new DefaultListModel<>();
        loadMeterList();
        //switchCom.setEnabled(false);
        initComponents();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        tabbedPane = new javax.swing.JTabbedPane();
        paymethodCom = new javax.swing.JPanel();
        welcomeCommercial = new javax.swing.JLabel();
        meterNameCom = new javax.swing.JTextField();
        addmeterCom = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        dateCom = new javax.swing.JLabel();
        prevCom = new javax.swing.JLabel();
        currentCom = new javax.swing.JLabel();
        switchCom = new javax.swing.JToggleButton();
        jSeparator2 = new javax.swing.JSeparator();
        meterName = new javax.swing.JLabel();
        scrollList = new javax.swing.JScrollPane();
        jScrollPane1 = new javax.swing.JScrollPane();
        listCom = new javax.swing.JList<>();
        billCom = new javax.swing.JLabel();
        paybutton = new javax.swing.JButton();
        paymentCom = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jComboBox1 = new javax.swing.JComboBox<>();
        sidePanel = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        logoutClient = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        tabbedPane.setBackground(new java.awt.Color(102, 102, 102));
        tabbedPane.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                tabbedPaneStateChanged(evt);
            }
        });

        paymethodCom.setBackground(new java.awt.Color(0, 204, 204));

        welcomeCommercial.setFont(new java.awt.Font("Serif", 1, 24)); // NOI18N
        welcomeCommercial.setForeground(new java.awt.Color(255, 255, 255));
        welcomeCommercial.setText("WELCOME");

        addmeterCom.setText("ADD METER");
        addmeterCom.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addmeterComActionPerformed(evt);
            }
        });

        jLabel2.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(255, 255, 255));
        jLabel2.setText("Meter Name");

        jLabel3.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(255, 255, 255));
        jLabel3.setText("METER USAGE");

        dateCom.setForeground(new java.awt.Color(0, 0, 0));
        dateCom.setText("Date Today: ");

        prevCom.setForeground(new java.awt.Color(0, 0, 0));
        prevCom.setText("Previous Reading:");

        currentCom.setForeground(new java.awt.Color(0, 0, 0));
        currentCom.setText("Current Reading:");

        switchCom.setText("Start Meter");
        switchCom.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                switchComActionPerformed(evt);
            }
        });

        meterName.setForeground(new java.awt.Color(0, 0, 0));
        meterName.setText("Meter Name:");

        listCom.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        listCom.setModel(meterList);
        listCom.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                listComValueChanged(evt);
            }
        });
        jScrollPane1.setViewportView(listCom);

        scrollList.setViewportView(jScrollPane1);

        billCom.setForeground(new java.awt.Color(0, 0, 0));
        billCom.setText("VIEW BILL");

        paybutton.setText("PAY BILL");

        paymentCom.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                paymentComActionPerformed(evt);
            }
        });

        jLabel5.setForeground(new java.awt.Color(0, 0, 0));
        jLabel5.setText("INPUT PAYMENT");

        jLabel6.setForeground(new java.awt.Color(0, 0, 0));
        jLabel6.setText("PAYMENT METHOD");

        jComboBox1.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Cash", "Gcash", "Paymaya" }));

        javax.swing.GroupLayout paymethodComLayout = new javax.swing.GroupLayout(paymethodCom);
        paymethodCom.setLayout(paymethodComLayout);
        paymethodComLayout.setHorizontalGroup(
            paymethodComLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(paymethodComLayout.createSequentialGroup()
                .addGroup(paymethodComLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, paymethodComLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(welcomeCommercial, javax.swing.GroupLayout.PREFERRED_SIZE, 437, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 271, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, paymethodComLayout.createSequentialGroup()
                        .addGap(21, 21, 21)
                        .addGroup(paymethodComLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(switchCom)
                            .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 98, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(dateCom, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(prevCom, javax.swing.GroupLayout.DEFAULT_SIZE, 146, Short.MAX_VALUE)
                            .addComponent(currentCom, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(meterName, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(scrollList, javax.swing.GroupLayout.PREFERRED_SIZE, 111, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGroup(paymethodComLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(paymethodComLayout.createSequentialGroup()
                                .addGap(18, 18, 18)
                                .addGroup(paymethodComLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(meterNameCom, javax.swing.GroupLayout.PREFERRED_SIZE, 143, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addGroup(paymethodComLayout.createSequentialGroup()
                                .addGap(40, 40, 40)
                                .addComponent(addmeterCom)))
                        .addGap(18, 18, 18))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, paymethodComLayout.createSequentialGroup()
                        .addGap(21, 21, 21)
                        .addGroup(paymethodComLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jSeparator2)
                            .addGroup(paymethodComLayout.createSequentialGroup()
                                .addComponent(billCom, javax.swing.GroupLayout.PREFERRED_SIZE, 325, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGroup(paymethodComLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(paymethodComLayout.createSequentialGroup()
                                        .addGroup(paymethodComLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                            .addGroup(paymethodComLayout.createSequentialGroup()
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, 192, javax.swing.GroupLayout.PREFERRED_SIZE))
                                            .addGroup(paymethodComLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                .addGroup(paymethodComLayout.createSequentialGroup()
                                                    .addGap(117, 117, 117)
                                                    .addComponent(paymentCom, javax.swing.GroupLayout.PREFERRED_SIZE, 197, javax.swing.GroupLayout.PREFERRED_SIZE))
                                                .addGroup(paymethodComLayout.createSequentialGroup()
                                                    .addGap(103, 103, 103)
                                                    .addGroup(paymethodComLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                        .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 108, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                        .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 117, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                                        .addGap(0, 0, Short.MAX_VALUE))
                                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, paymethodComLayout.createSequentialGroup()
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(paybutton, javax.swing.GroupLayout.PREFERRED_SIZE, 98, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(102, 102, 102)))))))
                .addGap(24, 24, 24))
        );
        paymethodComLayout.setVerticalGroup(
            paymethodComLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(paymethodComLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(welcomeCommercial, javax.swing.GroupLayout.PREFERRED_SIZE, 67, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGroup(paymethodComLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(paymethodComLayout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel3)
                        .addGap(12, 12, 12)
                        .addComponent(dateCom, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(meterName, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(prevCom, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(currentCom, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(switchCom))
                    .addGroup(paymethodComLayout.createSequentialGroup()
                        .addGap(30, 30, 30)
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(meterNameCom, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(addmeterCom, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(paymethodComLayout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(scrollList, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(18, 18, 18)
                .addComponent(jSeparator2, javax.swing.GroupLayout.PREFERRED_SIZE, 15, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(paymethodComLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(paymethodComLayout.createSequentialGroup()
                        .addComponent(jLabel5)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(paymentCom, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel6)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(31, 31, 31)
                        .addComponent(paybutton, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(billCom, javax.swing.GroupLayout.PREFERRED_SIZE, 219, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(36, Short.MAX_VALUE))
        );

        switchCom.setEnabled(false);

        tabbedPane.addTab("COMMERCIAL", paymethodCom);

        sidePanel.setBackground(new java.awt.Color(0, 153, 153));
        sidePanel.setForeground(new java.awt.Color(0, 153, 153));

        jLabel1.setText("CLIENT DASHBOARD");

        logoutClient.setText("LOG OUT");
        logoutClient.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                logoutClientActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout sidePanelLayout = new javax.swing.GroupLayout(sidePanel);
        sidePanel.setLayout(sidePanelLayout);
        sidePanelLayout.setHorizontalGroup(
            sidePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(sidePanelLayout.createSequentialGroup()
                .addGap(37, 37, 37)
                .addComponent(logoutClient)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, sidePanelLayout.createSequentialGroup()
                .addContainerGap(20, Short.MAX_VALUE)
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 131, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        sidePanelLayout.setVerticalGroup(
            sidePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(sidePanelLayout.createSequentialGroup()
                .addGap(17, 17, 17)
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(logoutClient, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(34, 34, 34))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(sidePanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(tabbedPane, javax.swing.GroupLayout.PREFERRED_SIZE, 738, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(sidePanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(tabbedPane))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void addmeterComActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addmeterComActionPerformed
        String meterName = meterNameCom.getText().trim();
        if (meterName.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a meter name.", "Error", JOptionPane.ERROR_MESSAGE);
        } else {
                addNewMeter(meterName); 
                meterNameCom.setText(""); 
            }

        loadMeterList();
    }//GEN-LAST:event_addmeterComActionPerformed

    private void tabbedPaneStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_tabbedPaneStateChanged
        int clientID = SharedData.clientID;
        String clientName = client.getClientName(clientID);

        welcomeCommercial.setText("Welcome, " + clientName + "!");
    }//GEN-LAST:event_tabbedPaneStateChanged

    private void logoutClientActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_logoutClientActionPerformed
        SharedData.clientID = 0;
        UserState.verifiedID = -1;
        UserState.isVerified = false;

        ClientSignIn clientsn = new ClientSignIn();
        clientsn.setVisible(true);
        this.dispose();
    }//GEN-LAST:event_logoutClientActionPerformed

    private void listComValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_listComValueChanged
    if (!evt.getValueIsAdjusting()) { 
        String selectedMeterName = listCom.getSelectedValue();
        if (selectedMeterName != null) {
            int clientID = SharedData.clientID;
            int meterID = client.getMeterIDByMeterName(clientID, selectedMeterName);

            if (meterID > 0) { 
                double[] readings = client.getMeterReadings(meterID);
                
                dateCom.setText("Date Today: " + LocalDate.now().toString());
                meterName.setText("Meter Name: " + selectedMeterName);
                prevCom.setText("Previous Reading: " + readings[0]);
                currentCom.setText("Current Reading: " + readings[1]);
                
                // Update the switchCom button text
                if (meterTimers.containsKey(meterID)) {
                    switchCom.setText("Stop Meter");
                } else {
                    switchCom.setText("Start Meter");
                }
                switchCom.setEnabled(true);
            } else {
                JOptionPane.showMessageDialog(this, "Meter ID not found for selected meter name.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    }//GEN-LAST:event_listComValueChanged

    private void switchComActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_switchComActionPerformed
    if (listCom.getSelectedValue() == null) {
        JOptionPane.showMessageDialog(this, "Please select a meter first!", "Error", JOptionPane.ERROR_MESSAGE);
        return;
    }

    String selectedMeter = listCom.getSelectedValue();
    int selectedMeterID = client.getMeterIDByMeterName(SharedData.clientID, selectedMeter);

    if (selectedMeterID <= 0) {
        JOptionPane.showMessageDialog(this, "Error retrieving meter ID!", "Error", JOptionPane.ERROR_MESSAGE);
        return;
    }

    // Check the current state of the meter
    if (meterTimers.containsKey(selectedMeterID)) { 
        // If the timer exists, the meter is running; stop it
        stopMeter(selectedMeterID);
        switchCom.setText("Start Meter");
        JOptionPane.showMessageDialog(this, "Meter stopped!", "Info", JOptionPane.INFORMATION_MESSAGE);
    } else {
        // If no timer exists, the meter is not running; start it
        startMeter(selectedMeterID);
        switchCom.setText("Stop Meter");
        JOptionPane.showMessageDialog(this, "Meter started!", "Info", JOptionPane.INFORMATION_MESSAGE);
    }
    }//GEN-LAST:event_switchComActionPerformed

    private void paymentComActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_paymentComActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_paymentComActionPerformed

    /**
     * @param args the command line arguments
     */

    private void loadMeterList() {
        int clientID = SharedData.clientID;
        meterList.clear(); 
        List<String> meters = client.getMetersByClientID(clientID); 
        for (String meter : meters) {
            meterList.addElement(meter); 
        }
    }

    private void addNewMeter(String meterName) {
        int clientID = SharedData.clientID;
        client.addMeter(clientID, meterName, meterType); 
        loadMeterList(); 
    }
    
private void startMeter(int meterID) {
    if (meterTimers.containsKey(meterID)) {
        JOptionPane.showMessageDialog(this, "This meter is already running!", "Info", JOptionPane.INFORMATION_MESSAGE);
        return;
    }

    Timer timer = new Timer(1000, event -> {
        double[] readings = client.getMeterReadings(meterID);
        readings[1] += 0.5; 
        client.updateCurrentReading(meterID, readings[1]); 

        if (listCom.getSelectedValue() != null && client.getMeterIDByMeterName(SharedData.clientID, listCom.getSelectedValue()) == meterID) {
            currentCom.setText("Current Reading: " + readings[1]);
        }
    });
    timer.start();
    meterTimers.put(meterID, timer); // Add the timer to the Map
}


private void stopMeter(int meterID) {
    Timer timer = meterTimers.get(meterID);
    if (timer != null) {
        timer.stop();
        meterTimers.remove(meterID); // Remove the timer from the Map
    }
}


    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(ClientCommercial.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(ClientCommercial.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(ClientCommercial.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(ClientCommercial.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new ClientCommercial().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton addmeterCom;
    private javax.swing.JLabel billCom;
    private javax.swing.JLabel currentCom;
    private javax.swing.JLabel dateCom;
    private javax.swing.JComboBox<String> jComboBox1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JList<String> listCom;
    private javax.swing.JButton logoutClient;
    private javax.swing.JLabel meterName;
    private javax.swing.JTextField meterNameCom;
    private javax.swing.JButton paybutton;
    private javax.swing.JTextField paymentCom;
    private javax.swing.JPanel paymethodCom;
    private javax.swing.JLabel prevCom;
    private javax.swing.JScrollPane scrollList;
    private javax.swing.JPanel sidePanel;
    private javax.swing.JToggleButton switchCom;
    private javax.swing.JTabbedPane tabbedPane;
    private javax.swing.JLabel welcomeCommercial;
    // End of variables declaration//GEN-END:variables
}
