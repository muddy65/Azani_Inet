package com.azani.ui.Panels;

import com.azani.dao.PaymentDAO;
import com.azani.dao.InstitutionDAO;
import com.azani.service.ValidationService;

import javax.swing.*;
import java.awt.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class BillingPanel extends JPanel {

    public BillingPanel() {
        setLayout(new BorderLayout());
        
        // ---------------------------------------------------------
        // COLOR PALETTE
        // ---------------------------------------------------------
        Color bgGray = new Color(248, 250, 252);
        Color navyBlue = new Color(26, 32, 44);
        Color cardTeal = new Color(25, 142, 116);

        setBackground(bgGray);

        // ---------------------------------------------------------
        // 1. TITLE PANEL
        // ---------------------------------------------------------
        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(new BorderLayout());
        titlePanel.setBackground(Color.WHITE);
        titlePanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        JLabel billingLabel = new JLabel("Payments & Billing");
        billingLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titlePanel.add(billingLabel, BorderLayout.WEST);

        // ---------------------------------------------------------
        // 2. MAIN CONTENTS CONTAINER
        // ---------------------------------------------------------
        JPanel contentPanel = new JPanel();
        contentPanel.setBackground(bgGray);
        contentPanel.setLayout(new BorderLayout(20, 0)); 
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // ---------------------------------------------------------
        // 3. LEFT CARD: PAYMENT PROCESSING
        // ---------------------------------------------------------
        JPanel paymentPanel = new JPanel();
        paymentPanel.setLayout(new BoxLayout(paymentPanel, BoxLayout.Y_AXIS));
        paymentPanel.setBackground(Color.WHITE);
        paymentPanel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30)); 

        // Title
        JLabel processTitle = new JLabel("Process Payment");
        processTitle.setFont(new Font("Arial", Font.BOLD, 18)); 
        processTitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Search Field
        JLabel searchLbl = new JLabel("Search Institution by Name or ID...");
        searchLbl.setFont(new Font("Arial", Font.PLAIN, 14)); 
        searchLbl.setForeground(Color.GRAY);
        searchLbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JTextField searchArea = new JTextField();
        searchArea.setAlignmentX(Component.LEFT_ALIGNMENT);
        searchArea.setPreferredSize(new Dimension(0, 45)); 
        searchArea.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));

        //Live search suggestion Label
        JLabel suggestionLabel = new JLabel("Start typing to search...");
        suggestionLabel.setForeground(new Color(0, 102, 204));
        suggestionLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));

              // Amount Paid Field
        JLabel amountLbl = new JLabel("Amount Paid (KSh)");
        amountLbl.setFont(new Font("Arial", Font.PLAIN, 14)); 
        amountLbl.setForeground(Color.GRAY);
        amountLbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JTextField amountPaid = new JTextField("0.00");
        amountPaid.setAlignmentX(Component.LEFT_ALIGNMENT);
        amountPaid.setPreferredSize(new Dimension(0, 45)); 
        amountPaid.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));

        // Record Button
        JButton recordBtn = new JButton("Record Payment");
        recordBtn.setBackground(navyBlue);
        recordBtn.setForeground(Color.WHITE);
        recordBtn.setFont(new Font("SansSerif", Font.BOLD, 15));
        recordBtn.setFocusPainted(false);
        recordBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        recordBtn.setPreferredSize(new Dimension(0, 50)); 
        recordBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));

        //Live typing Listener
        searchArea.getDocument().addDocumentListener(new DocumentListener(){
            public void insertUpdate(DocumentEvent e) { doSearch();}
            public void removeUpdate(DocumentEvent e) { doSearch();}
            public void changedUpdate(DocumentEvent e) { doSearch();}

            private void doSearch(){
                String text = searchArea.getText().trim();
                if(text.length() > 2){//Start searching after 3 letters
                    InstitutionDAO dao = new InstitutionDAO();
                    String[] result = dao.searchInstitutionByName(text);
                    if(result != null){
                        suggestionLabel.setText("Click toselect: " + result[1]);
                        suggestionLabel.putClientProperty("inst_id", result[0]); //Temporarily store ID
                        suggestionLabel.putClientProperty("inst_name", result[1]); //Store name
                        suggestionLabel.putClientProperty("inst_cost", result[3]); // Store cost
                    }
                    else{
                        suggestionLabel.setText("No matches found..");
                    }
                }
                else{
                    suggestionLabel.setText("Start typing to search...");
                }
            }
        });

        //Click to Auto-Fill
        suggestionLabel.addMouseListener(new MouseAdapter(){
            @Override
            public void mouseClicked(MouseEvent e){
                if(suggestionLabel.getClientProperty("inst_name") != null){
                    String name = (String) suggestionLabel.getClientProperty("inst_name");
                    String cost = (String) suggestionLabel.getClientProperty("inst_cost");

                    //Autofill  the boxes
                    searchArea.setText(name);
                    amountPaid.setText(cost);

                    //lock amount box so no editing
                    amountPaid.setEditable(false);

                    suggestionLabel.setText("Selected! Ready to record Payment.");
                }
            }
        });

        // Assemble Left Card
        paymentPanel.add(processTitle);
        paymentPanel.add(Box.createVerticalStrut(30));
        paymentPanel.add(searchLbl);
        paymentPanel.add(searchArea);
        paymentPanel.add(suggestionLabel);
        paymentPanel.add(Box.createVerticalStrut(25));
        paymentPanel.add(Box.createVerticalStrut(25));
        paymentPanel.add(amountLbl);
        paymentPanel.add(amountPaid);
        paymentPanel.add(Box.createVerticalStrut(30));
        paymentPanel.add(recordBtn);
        paymentPanel.add(Box.createVerticalGlue()); 


        // ---------------------------------------------------------
        // 4. RIGHT CARD: UPGRADE CALCULATOR
        // ---------------------------------------------------------
        JPanel upgradePanel = new JPanel();
        upgradePanel.setLayout(new BoxLayout(upgradePanel, BoxLayout.Y_AXIS));
        upgradePanel.setBackground(cardTeal);
        upgradePanel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
        
        upgradePanel.setPreferredSize(new Dimension(400, 0)); 

        // Title
        JLabel upgradeLabel = new JLabel("Upgrade Calculator");
        upgradeLabel.setFont(new Font("Arial", Font.BOLD, 18));
        upgradeLabel.setForeground(Color.WHITE);
        upgradeLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Helper Text
        JLabel helperText = new JLabel("Institutions upgrading bandwidth get a 10% discount on the new rate.");
        helperText.setFont(new Font("Arial", Font.PLAIN, 11)); 
        helperText.setForeground(Color.WHITE);
        helperText.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Bandwidth Dropdown
        JLabel newBandwidthLbl = new JLabel("New Bandwidth Tier");
        newBandwidthLbl.setFont(new Font("Arial", Font.PLAIN, 14));
        newBandwidthLbl.setForeground(Color.WHITE);
        newBandwidthLbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        String[] bandwidthTier = {
            "Select",
            "20 MBPS (Standard: Ksh 3,500)",
            "25 MBPS (Standard: Ksh 4,000)",
            "50 MBPS (Standard: Ksh 7,000)"
        };
        JComboBox<String> bandwidth = new JComboBox<>(bandwidthTier);
        bandwidth.setSelectedIndex(1);
        bandwidth.setAlignmentX(Component.LEFT_ALIGNMENT);
        bandwidth.setPreferredSize(new Dimension(0, 45)); 
        bandwidth.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        bandwidth.setBackground(new Color(20, 115, 95));
        bandwidth.setForeground(Color.WHITE);

        // Discount Display
        JLabel discountedLbl = new JLabel("Discounted Monthly Cost:");
        discountedLbl.setFont(new Font("Arial", Font.PLAIN, 14));
        discountedLbl.setForeground(Color.WHITE);
        discountedLbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JLabel display = new JLabel("KSh 3,150");
        display.setFont(new Font("Arial", Font.BOLD, 32)); 
        display.setForeground(Color.WHITE);
        display.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Apply Button
        JButton upgradeBtn = new JButton("Apply Upgrade");
        upgradeBtn.setBackground(Color.WHITE);
        upgradeBtn.setForeground(cardTeal);
        upgradeBtn.setFont(new Font("Arial", Font.BOLD, 15));
        upgradeBtn.setFocusPainted(false);
        upgradeBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        upgradeBtn.setPreferredSize(new Dimension(0, 50));
        upgradeBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));

        // Assemble Right Card
        upgradePanel.add(upgradeLabel);
        upgradePanel.add(Box.createVerticalStrut(15));
        upgradePanel.add(helperText);
        upgradePanel.add(Box.createVerticalStrut(25));
        upgradePanel.add(newBandwidthLbl);
        upgradePanel.add(bandwidth);
        upgradePanel.add(Box.createVerticalStrut(25));
        upgradePanel.add(discountedLbl);
        upgradePanel.add(display);
        upgradePanel.add(Box.createVerticalStrut(30));
        upgradePanel.add(upgradeBtn);
        upgradePanel.add(Box.createVerticalGlue()); 


        // ---------------------------------------------------------
        // 5. FINAL ASSEMBLY
        // ---------------------------------------------------------
        JPanel topCardsContainer = new JPanel(new BorderLayout(20, 0));
        topCardsContainer.setBackground(bgGray);

        topCardsContainer.add(paymentPanel, BorderLayout.CENTER);
        topCardsContainer.add(upgradePanel, BorderLayout.EAST);

        contentPanel.add(topCardsContainer, BorderLayout.NORTH);

        add(titlePanel, BorderLayout.NORTH);
        add(contentPanel, BorderLayout.CENTER);

        // ---------------------------------------------------------
        // 6. LOGIC & ACTION LISTENERS (The Wiring)
        // ---------------------------------------------------------
        

        // Save Payment to MySQL
        // Save Payment to MySQL
        recordBtn.addActionListener(e -> {
            String instIdStr = (String) suggestionLabel.getClientProperty("inst_id");
            String amountStr = amountPaid.getText().trim();

           if(instIdStr.isEmpty() || amountStr.isEmpty()){
                JOptionPane.showMessageDialog(this, "Please enter a client name and payment amount.", "Missing Info", JOptionPane.ERROR_MESSAGE);
                return;
           }
           int institutionId = Integer.parseInt(instIdStr);
           double amount = Double.parseDouble(amountStr);

           PaymentDAO payDao= new PaymentDAO();
           boolean success = payDao.recordPayment(institutionId, amount);

           if (success){
                String clientName = (String) suggestionLabel.getClientProperty("inst_name");
                JOptionPane.showMessageDialog(this, "Ksh " + amount + " payment recorded! " + clientName + " is now Active.");

                // Reset everything for the next payment
                searchArea.setText("");
                amountPaid.setText("");
                amountPaid.setEditable(true);
                suggestionLabel.setText("Start typing to search...");
                suggestionLabel.putClientProperty("inst_id", null);
                suggestionLabel.putClientProperty("inst_name", null);
           
           }else{
            JOptionPane.showMessageDialog(this, "Database error. Could not record payment.", "Error", JOptionPane.ERROR_MESSAGE);
           }
  
        });

        // Dynamic 10% Discount Calculator
        bandwidth.addActionListener(e -> {
            int selection = bandwidth.getSelectedIndex();
            double standardRate = 0.0;
            
            if (selection == 1) standardRate = 3500.0;
            else if (selection == 2) standardRate = 4000.0;
            else if (selection == 3) standardRate = 7000.0;

            if (standardRate > 0) {
                double discountedRate = standardRate * 0.90;
                display.setText(String.format("KSh %,.0f", discountedRate));
            } else {
                display.setText("KSh 0");
            }
        });

        // Apply Upgrade Placeholder
        upgradeBtn.addActionListener(e -> {
            if (bandwidth.getSelectedIndex() == 0) {
                JOptionPane.showMessageDialog(this, "Please select a bandwidth tier first.", "Notice", JOptionPane.WARNING_MESSAGE);
                return;
            }
            JOptionPane.showMessageDialog(this, "Upgrade application process initiated.", "Success", JOptionPane.INFORMATION_MESSAGE);
        });
    }
}