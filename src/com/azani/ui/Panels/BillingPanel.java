package com.azani.ui.Panels;

import com.azani.dao.PaymentDAO;
import com.azani.dao.InstitutionDAO;
import com.azani.service.BillingManager;

import javax.swing.*;
import java.awt.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class BillingPanel extends JPanel {
    
    // --- GLOBAL UI COMPONENTS ---
    // Declared globally so both the Payment and Upgrade buttons can access the selected client
    private JLabel suggestionLabel;
    private JTextField searchArea;
    private JTextField amountPaid;
    private JTextArea billPreview;
    private JComboBox<String> bandwidthCombo;
    private JLabel discountDisplay;
    
    // Track the current selected client's billing state
    private String currentPaymentType = "Monthly Bill";
    private int currentSelectedBandwidth = 0;
    private double currentDiscountedCost = 0.0;

    public BillingPanel() {
        setLayout(new BorderLayout());
        
        // --- COLOR PALETTE ---
        Color bgGray = new Color(248, 250, 252);
        Color navyBlue = new Color(26, 32, 44);
        Color cardTeal = new Color(25, 142, 116);

        setBackground(bgGray);

        // --- 1. TITLE PANEL ---
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setBackground(Color.WHITE);
        titlePanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        JLabel billingLabel = new JLabel("Payments & Billing");
        billingLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titlePanel.add(billingLabel, BorderLayout.WEST);

        // --- 2. MAIN CONTENTS CONTAINER ---
        JPanel contentPanel = new JPanel(new BorderLayout(20, 0));
        contentPanel.setBackground(bgGray);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // --- 3. LEFT CARD: PAYMENT PROCESSING ---
        JPanel paymentPanel = new JPanel();
        paymentPanel.setLayout(new BoxLayout(paymentPanel, BoxLayout.Y_AXIS));
        paymentPanel.setBackground(Color.WHITE);
        paymentPanel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30)); 

        JLabel processTitle = new JLabel("Process Payment");
        processTitle.setFont(new Font("Arial", Font.BOLD, 18)); 
        processTitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel searchLbl = new JLabel("Search Institution by Name...");
        searchLbl.setFont(new Font("Arial", Font.PLAIN, 14)); 
        searchLbl.setForeground(Color.GRAY);
        searchLbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        searchArea = new JTextField();
        searchArea.setAlignmentX(Component.LEFT_ALIGNMENT);
        searchArea.setPreferredSize(new Dimension(0, 40)); 
        searchArea.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));

        suggestionLabel = new JLabel("Start typing to search...");
        suggestionLabel.setForeground(new Color(0, 102, 204));
        suggestionLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // BILL PREVIEW: Detailed breakdown text area
        billPreview = new JTextArea(6, 30);
        billPreview.setEditable(false); // Security: Prevents tampering
        billPreview.setFont(new Font("Monospaced", Font.PLAIN, 13));
        billPreview.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.LIGHT_GRAY),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        billPreview.setText("Search and select a client to preview their bill...");
        
        JScrollPane scrollBill = new JScrollPane(billPreview);
        scrollBill.setAlignmentX(Component.LEFT_ALIGNMENT);
        scrollBill.setMaximumSize(new Dimension(Integer.MAX_VALUE, 120));

        JLabel amountLbl = new JLabel("Amount Due (KSh)");
        amountLbl.setFont(new Font("Arial", Font.PLAIN, 14)); 
        amountLbl.setForeground(Color.GRAY);
        amountLbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        // SECURITY: Set default to non-editable so users cannot alter the required payment
        amountPaid = new JTextField("0.00");
        amountPaid.setEditable(false); 
        amountPaid.setBackground(new Color(241, 245, 249)); // Light gray to indicate it's locked
        amountPaid.setFont(new Font("Arial", Font.BOLD, 16));
        amountPaid.setAlignmentX(Component.LEFT_ALIGNMENT);
        amountPaid.setPreferredSize(new Dimension(0, 45)); 
        amountPaid.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));

        JButton recordBtn = new JButton("Record Payment");
        recordBtn.setBackground(navyBlue);
        recordBtn.setForeground(Color.WHITE);
        recordBtn.setFont(new Font("SansSerif", Font.BOLD, 15));
        recordBtn.setFocusPainted(false);
        recordBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        recordBtn.setPreferredSize(new Dimension(0, 45)); 
        recordBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));

        // Assemble Left Card
        paymentPanel.add(processTitle);
        paymentPanel.add(Box.createVerticalStrut(20));
        paymentPanel.add(searchLbl);
        paymentPanel.add(searchArea);
        paymentPanel.add(suggestionLabel);
        paymentPanel.add(Box.createVerticalStrut(20));
        paymentPanel.add(scrollBill);
        paymentPanel.add(Box.createVerticalStrut(20));
        paymentPanel.add(amountLbl);
        paymentPanel.add(amountPaid);
        paymentPanel.add(Box.createVerticalStrut(20));
        paymentPanel.add(recordBtn);
        paymentPanel.add(Box.createVerticalGlue()); 

        // --- 4. RIGHT CARD: UPGRADE CALCULATOR ---
        JPanel upgradePanel = new JPanel();
        upgradePanel.setLayout(new BoxLayout(upgradePanel, BoxLayout.Y_AXIS));
        upgradePanel.setBackground(cardTeal);
        upgradePanel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
        upgradePanel.setPreferredSize(new Dimension(400, 0)); 

        JLabel upgradeLabel = new JLabel("Upgrade Calculator");
        upgradeLabel.setFont(new Font("Arial", Font.BOLD, 18));
        upgradeLabel.setForeground(Color.WHITE);
        upgradeLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel helperText = new JLabel("Institutions upgrading bandwidth get a 10% discount on the new rate.");
        helperText.setFont(new Font("Arial", Font.PLAIN, 11)); 
        helperText.setForeground(Color.WHITE);
        helperText.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel newBandwidthLbl = new JLabel("New Bandwidth Tier");
        newBandwidthLbl.setFont(new Font("Arial", Font.PLAIN, 14));
        newBandwidthLbl.setForeground(Color.WHITE);
        newBandwidthLbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        String[] bandwidthTier = {
            "Select Tier",
            "10 MBPS (Standard: Ksh 2,000)",
            "20 MBPS (Standard: Ksh 3,500)",
            "25 MBPS (Standard: Ksh 4,000)",
            "50 MBPS (Standard: Ksh 7,000)"
        };
        bandwidthCombo = new JComboBox<>(bandwidthTier);
        bandwidthCombo.setAlignmentX(Component.LEFT_ALIGNMENT);
        bandwidthCombo.setPreferredSize(new Dimension(0, 45)); 
        bandwidthCombo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        bandwidthCombo.setBackground(new Color(20, 115, 95));
        bandwidthCombo.setForeground(Color.WHITE);

        JLabel discountedLbl = new JLabel("Discounted Monthly Cost:");
        discountedLbl.setFont(new Font("Arial", Font.PLAIN, 14));
        discountedLbl.setForeground(Color.WHITE);
        discountedLbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        discountDisplay = new JLabel("KSh 0.00");
        discountDisplay.setFont(new Font("Arial", Font.BOLD, 32)); 
        discountDisplay.setForeground(Color.WHITE);
        discountDisplay.setAlignmentX(Component.LEFT_ALIGNMENT);

        JButton upgradeBtn = new JButton("Apply Upgrade");
        upgradeBtn.setBackground(Color.WHITE);
        upgradeBtn.setForeground(cardTeal);
        upgradeBtn.setFont(new Font("Arial", Font.BOLD, 15));
        upgradeBtn.setFocusPainted(false);
        upgradeBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        upgradeBtn.setPreferredSize(new Dimension(0, 45));
        upgradeBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));

        // Assemble Right Card
        upgradePanel.add(upgradeLabel);
        upgradePanel.add(Box.createVerticalStrut(15));
        upgradePanel.add(helperText);
        upgradePanel.add(Box.createVerticalStrut(25));
        upgradePanel.add(newBandwidthLbl);
        upgradePanel.add(bandwidthCombo);
        upgradePanel.add(Box.createVerticalStrut(25));
        upgradePanel.add(discountedLbl);
        upgradePanel.add(discountDisplay);
        upgradePanel.add(Box.createVerticalStrut(30));
        upgradePanel.add(upgradeBtn);
        upgradePanel.add(Box.createVerticalGlue()); 

        // --- 5. FINAL ASSEMBLY ---
        JPanel topCardsContainer = new JPanel(new BorderLayout(20, 0));
        topCardsContainer.setBackground(bgGray);
        topCardsContainer.add(paymentPanel, BorderLayout.CENTER);
        topCardsContainer.add(upgradePanel, BorderLayout.EAST);

        contentPanel.add(topCardsContainer, BorderLayout.NORTH);
        add(titlePanel, BorderLayout.NORTH);
        add(contentPanel, BorderLayout.CENTER);

        // --- 6. LOGIC & ACTION LISTENERS ---

        // LIVE SEARCH LISTENER
        searchArea.getDocument().addDocumentListener(new DocumentListener(){
            public void insertUpdate(DocumentEvent e) { doSearch(); }
            public void removeUpdate(DocumentEvent e) { doSearch(); }
            public void changedUpdate(DocumentEvent e) { doSearch(); }

            private void doSearch() {
                String text = searchArea.getText().trim();
                if (text.length() > 2) {
                    InstitutionDAO dao = new InstitutionDAO();
                    String[] result = dao.searchInstitutionByName(text);
                    if (result != null) {
                        suggestionLabel.setText("Click to select: " + result[1]);
                        suggestionLabel.putClientProperty("inst_id", result[0]); 
                        suggestionLabel.putClientProperty("inst_name", result[1]); 
                    } else {
                        suggestionLabel.setText("No matches found..");
                    }
                } else {
                    suggestionLabel.setText("Start typing to search...");
                }
            }
        });

        // AUTO-FILL & BILL CALCULATION ON CLICK
        suggestionLabel.addMouseListener(new MouseAdapter(){
            @Override
            public void mouseClicked(MouseEvent e) {
                if (suggestionLabel.getClientProperty("inst_id") != null) {
                    int instId = Integer.parseInt((String) suggestionLabel.getClientProperty("inst_id"));
                    String name = (String) suggestionLabel.getClientProperty("inst_name");
                    
                    searchArea.setText(name);
                    suggestionLabel.setText("Selected! Fetching billing details...");
                    
                    // Fetch live billing status from the database
                    PaymentDAO payDao = new PaymentDAO();
                    String[] billingDetails = payDao.getClientBillingDetails(instId);
                    
                    if (billingDetails != null) {
                        String status = billingDetails[0];
                        double monthly = Double.parseDouble(billingDetails[1]);
                        double fine = Double.parseDouble(billingDetails[2]);
                        double recon = Double.parseDouble(billingDetails[3]);
                        
                        double totalDue = monthly + fine + recon;
                        
                        // Set the payment type based on the situation
                        if (status.equals("DISCONNECTED") || status.equals("OVERDUE")) {
                            currentPaymentType = "Overdue/Reconnection";
                        } else {
                            currentPaymentType = "Monthly Bill";
                        }
                        
                        // Populate the detailed bill preview
                        StringBuilder preview = new StringBuilder();
                        preview.append("--- CURRENT BILLING STATUS: ").append(status).append(" ---\n");
                        preview.append(String.format("Base Monthly Cost:  KSh %,.2f\n", monthly));
                        
                        if (fine > 0) preview.append(String.format("Overdue Fine (15%%): KSh %,.2f\n", fine));
                        if (recon > 0) preview.append(String.format("Reconnection Fee:   KSh %,.2f\n", recon));
                        
                        preview.append("--------------------------------------\n");
                        preview.append(String.format("TOTAL AMOUNT DUE:   KSh %,.2f", totalDue));
                        
                        billPreview.setText(preview.toString());
                        
                        // Securely lock the exact amount required into the payment box
                        amountPaid.setText(String.valueOf(totalDue));
                        suggestionLabel.setText("Ready for payment processing.");
                    }
                }
            }
        });

        // RECORD PAYMENT LOGIC
        recordBtn.addActionListener(e -> {
            String instIdStr = (String) suggestionLabel.getClientProperty("inst_id");
            if (instIdStr == null || instIdStr.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please search and select a client first.", "Missing Info", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            int institutionId = Integer.parseInt(instIdStr);
            double amount = Double.parseDouble(amountPaid.getText());

            PaymentDAO payDao = new PaymentDAO();
            boolean success = payDao.processPayment(institutionId, currentPaymentType, amount);

            if (success) {
                String clientName = (String) suggestionLabel.getClientProperty("inst_name");
                JOptionPane.showMessageDialog(this, "KSh " + amount + " payment successfully recorded!\n" + clientName + " is now Active.");
                resetBillingForm();
            } else {
                JOptionPane.showMessageDialog(this, "Database error. Could not record payment.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        // UPGRADE CALCULATOR LOGIC
        bandwidthCombo.addActionListener(e -> {
            int selection = bandwidthCombo.getSelectedIndex();
            
            // Map the dropdown selection to actual Mbps using BillingManager logic
            if (selection == 1) currentSelectedBandwidth = 10;
            else if (selection == 2) currentSelectedBandwidth = 20;
            else if (selection == 3) currentSelectedBandwidth = 25;
            else if (selection == 4) currentSelectedBandwidth = 50;
            else currentSelectedBandwidth = 0;

            if (currentSelectedBandwidth > 0) {
                // Fetch standard cost and apply the 10% discount dynamically
                currentDiscountedCost = BillingManager.calculateUpgradeCost(currentSelectedBandwidth);
                discountDisplay.setText(String.format("KSh %,.2f", currentDiscountedCost));
            } else {
                currentDiscountedCost = 0.0;
                discountDisplay.setText("KSh 0.00");
            }
        });

        // APPLY UPGRADE TO DATABASE LOGIC
        upgradeBtn.addActionListener(e -> {
            String instIdStr = (String) suggestionLabel.getClientProperty("inst_id");
            
            if (instIdStr == null || instIdStr.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please search and select a client from the left panel first to apply the upgrade.", "Select Client", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            if (currentSelectedBandwidth == 0) {
                JOptionPane.showMessageDialog(this, "Please select a new bandwidth tier.", "Notice", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            int institutionId = Integer.parseInt(instIdStr);
            PaymentDAO payDao = new PaymentDAO();
            
            // Execute the upgrade: Logs the payment, changes bandwidth, and resets status to ACTIVE
            boolean success = payDao.upgradeBandwidth(institutionId, currentSelectedBandwidth, currentDiscountedCost, currentDiscountedCost);
            
            if (success) {
                String clientName = (String) suggestionLabel.getClientProperty("inst_name");
                JOptionPane.showMessageDialog(this, "Upgrade Successful!\n" + clientName + " upgraded to " + currentSelectedBandwidth + " MBPS.");
                resetBillingForm();
            } else {
                JOptionPane.showMessageDialog(this, "Database error. Could not apply upgrade.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
    }

    /**
     * Resets the entire form and clears tracked variables after a successful transaction.
     */
    private void resetBillingForm() {
        searchArea.setText("");
        amountPaid.setText("0.00");
        billPreview.setText("Search and select a client to preview their bill...");
        suggestionLabel.setText("Start typing to search...");
        suggestionLabel.putClientProperty("inst_id", null);
        suggestionLabel.putClientProperty("inst_name", null);
        bandwidthCombo.setSelectedIndex(0);
        discountDisplay.setText("KSh 0.00");
        currentPaymentType = "Monthly Bill";
    }
}