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
        
        // --- MODERN WEB PALETTE (Tailwind CSS Inspired) ---
        Color bgApp = new Color(248, 250, 252);         // Slate 50
        Color cardSurface = Color.WHITE;                // Clean White
        Color primaryAccent = new Color(14, 165, 233);  // Sky Blue 500
        //Color successGreen = new Color(16, 185, 129);   // Emerald 500
        Color upgradeCard = new Color(15, 118, 110);    // Teal 700
        Color textDark = new Color(15, 23, 42);         // Slate 900
        Color textMuted = new Color(100, 116, 139);     // Slate 500

        setBackground(bgApp);

        // --- 1. TITLE PANEL ---
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setBackground(cardSurface);
        titlePanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        JLabel billingLabel = new JLabel("Payments & Billing");
        billingLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        billingLabel.setForeground(textDark);
        titlePanel.add(billingLabel, BorderLayout.WEST);

        // --- 2. MAIN CONTENTS CONTAINER ---
        JPanel contentPanel = new JPanel(new BorderLayout(20, 0));
        contentPanel.setBackground(bgApp);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // --- 3. LEFT CARD: PAYMENT PROCESSING ---
        JPanel paymentPanel = new JPanel();
        paymentPanel.setLayout(new BoxLayout(paymentPanel, BoxLayout.Y_AXIS));
        paymentPanel.setBackground(cardSurface);
        paymentPanel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30)); 

        JLabel processTitle = new JLabel("Process Payment");
        processTitle.setFont(new Font("Segoe UI", Font.BOLD, 18)); 
        processTitle.setForeground(textDark);
        processTitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel searchLbl = new JLabel("Search Institution by Name...");
        searchLbl.setFont(new Font("Segoe UI", Font.PLAIN, 14)); 
        searchLbl.setForeground(textMuted);
        searchLbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        searchArea = new JTextField();
        styleTextField(searchArea); // Apply modern padding
        searchArea.setAlignmentX(Component.LEFT_ALIGNMENT);
        searchArea.setPreferredSize(new Dimension(0, 45)); 
        searchArea.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));

        suggestionLabel = new JLabel("Start typing to search...");
        suggestionLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        suggestionLabel.setForeground(primaryAccent);
        suggestionLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // BILL PREVIEW: Detailed breakdown text area
        billPreview = new JTextArea(6, 30);
        billPreview.setEditable(false);
        billPreview.setBackground(bgApp);
        billPreview.setFont(new Font("Monospaced", Font.PLAIN, 13));
        billPreview.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(226, 232, 240)),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        billPreview.setText("Search and select a client to preview their bill...");
        
        JScrollPane scrollBill = new JScrollPane(billPreview);
        scrollBill.setAlignmentX(Component.LEFT_ALIGNMENT);
        scrollBill.setMaximumSize(new Dimension(Integer.MAX_VALUE, 120));

        JLabel amountLbl = new JLabel("Amount Due (KSh)");
        amountLbl.setFont(new Font("Segoe UI", Font.PLAIN, 14)); 
        amountLbl.setForeground(textMuted);
        amountLbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        amountPaid = new JTextField("0.00");
        amountPaid.setEditable(false); 
        styleTextField(amountPaid); // Apply modern padding
        amountPaid.setBackground(new Color(241, 245, 249)); // Locked visual cue
        amountPaid.setFont(new Font("Segoe UI", Font.BOLD, 16));
        amountPaid.setAlignmentX(Component.LEFT_ALIGNMENT);
        amountPaid.setPreferredSize(new Dimension(0, 45)); 
        amountPaid.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));

        JButton recordBtn = new JButton("Record Payment");
        recordBtn.setFont(new Font("Segoe UI", Font.BOLD, 15));
        recordBtn.setForeground(Color.WHITE);
        recordBtn.setFocusPainted(false);
        recordBtn.setBorderPainted(false);
        recordBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        recordBtn.setPreferredSize(new Dimension(0, 45)); 
        recordBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        applyHoverEffect(recordBtn, textDark, new Color(30, 41, 59)); // Slate 800 Hover

        // Assemble Left Card
        paymentPanel.add(processTitle);
        paymentPanel.add(Box.createVerticalStrut(20));
        paymentPanel.add(searchLbl);
        paymentPanel.add(searchArea);
        paymentPanel.add(Box.createVerticalStrut(5));
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
        upgradePanel.setBackground(upgradeCard);
        upgradePanel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
        upgradePanel.setPreferredSize(new Dimension(400, 0)); 

        JLabel upgradeLabel = new JLabel("Upgrade Calculator");
        upgradeLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        upgradeLabel.setForeground(Color.WHITE);
        upgradeLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel helperText = new JLabel("Institutions upgrading bandwidth get a 10% discount on the new rate.");
        helperText.setFont(new Font("Segoe UI", Font.PLAIN, 12)); 
        helperText.setForeground(new Color(204, 251, 241)); // Teal 100
        helperText.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel newBandwidthLbl = new JLabel("New Bandwidth Tier");
        newBandwidthLbl.setFont(new Font("Segoe UI", Font.PLAIN, 14));
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
        bandwidthCombo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        bandwidthCombo.setAlignmentX(Component.LEFT_ALIGNMENT);
        bandwidthCombo.setPreferredSize(new Dimension(0, 45)); 
        bandwidthCombo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        bandwidthCombo.setBackground(new Color(17, 94, 89)); // Teal 800
        bandwidthCombo.setForeground(Color.WHITE);

        JLabel discountedLbl = new JLabel("Discounted Monthly Cost:");
        discountedLbl.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        discountedLbl.setForeground(Color.WHITE);
        discountedLbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        discountDisplay = new JLabel("KSh 0.00");
        discountDisplay.setFont(new Font("Segoe UI", Font.BOLD, 36)); 
        discountDisplay.setForeground(Color.WHITE);
        discountDisplay.setAlignmentX(Component.LEFT_ALIGNMENT);

        JButton upgradeBtn = new JButton("Apply Upgrade");
        upgradeBtn.setFont(new Font("Segoe UI", Font.BOLD, 15));
        upgradeBtn.setForeground(upgradeCard);
        upgradeBtn.setFocusPainted(false);
        upgradeBtn.setBorderPainted(false);
        upgradeBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        upgradeBtn.setPreferredSize(new Dimension(0, 45));
        upgradeBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        applyHoverEffect(upgradeBtn, Color.WHITE, new Color(241, 245, 249)); // Slate 100 hover

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
        topCardsContainer.setBackground(bgApp);
        topCardsContainer.add(paymentPanel, BorderLayout.CENTER);
        topCardsContainer.add(upgradePanel, BorderLayout.EAST);

        contentPanel.add(topCardsContainer, BorderLayout.NORTH);
        add(titlePanel, BorderLayout.NORTH);
        add(contentPanel, BorderLayout.CENTER);

        // --- 6. LOGIC & ACTION LISTENERS ---

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
                        // STORE CURRENT BANDWIDTH FOR DOWNGRADE PREVENTION
                        suggestionLabel.putClientProperty("current_bw", result[2]); 
                    } else {
                        suggestionLabel.setText("No matches found..");
                        suggestionLabel.putClientProperty("inst_id", null);
                    }
                } else {
                    suggestionLabel.setText("Start typing to search...");
                    suggestionLabel.putClientProperty("inst_id", null);
                }
            }
        });

        suggestionLabel.addMouseListener(new MouseAdapter(){
            @Override
            public void mouseClicked(MouseEvent e) {
                if (suggestionLabel.getClientProperty("inst_id") != null) {
                    int instId = Integer.parseInt((String) suggestionLabel.getClientProperty("inst_id"));
                    String name = (String) suggestionLabel.getClientProperty("inst_name");
                    
                    searchArea.setText(name);
                    suggestionLabel.setText("Selected! Fetching billing details...");
                    
                    PaymentDAO payDao = new PaymentDAO();
                    String[] billingDetails = payDao.getClientBillingDetails(instId);
                    
                    if (billingDetails != null) {
                        String status = billingDetails[0];
                        double monthly = Double.parseDouble(billingDetails[1]);
                        double fine = Double.parseDouble(billingDetails[2]);
                        double recon = Double.parseDouble(billingDetails[3]);
                        
                        double totalDue = monthly + fine + recon;
                        
                        if (status.equals("DISCONNECTED") || status.equals("OVERDUE")) {
                            currentPaymentType = "Overdue/Reconnection";
                        } else {
                            currentPaymentType = "Monthly Bill";
                        }
                        
                        StringBuilder preview = new StringBuilder();
                        preview.append("--- CURRENT BILLING STATUS: ").append(status).append(" ---\n");
                        preview.append(String.format("Base Monthly Cost:  KSh %,.2f\n", monthly));
                        
                        if (fine > 0) preview.append(String.format("Overdue Fine (15%%): KSh %,.2f\n", fine));
                        if (recon > 0) preview.append(String.format("Reconnection Fee:   KSh %,.2f\n", recon));
                        
                        preview.append("--------------------------------------\n");
                        preview.append(String.format("TOTAL AMOUNT DUE:   KSh %,.2f", totalDue));
                        
                        billPreview.setText(preview.toString());
                        amountPaid.setText(String.valueOf(totalDue));
                        suggestionLabel.setText("Ready for payment processing.");
                    }
                }
            }
        });

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

        bandwidthCombo.addActionListener(e -> {
            int selection = bandwidthCombo.getSelectedIndex();
            
            if (selection == 1) currentSelectedBandwidth = 10;
            else if (selection == 2) currentSelectedBandwidth = 20;
            else if (selection == 3) currentSelectedBandwidth = 25;
            else if (selection == 4) currentSelectedBandwidth = 50;
            else currentSelectedBandwidth = 0;

            if (currentSelectedBandwidth > 0) {
                currentDiscountedCost = BillingManager.calculateUpgradeCost(currentSelectedBandwidth);
                discountDisplay.setText(String.format("KSh %,.2f", currentDiscountedCost));
            } else {
                currentDiscountedCost = 0.0;
                discountDisplay.setText("KSh 0.00");
            }
        });

        // APPLY UPGRADE WITH DOWNGRADE PREVENTION
        upgradeBtn.addActionListener(e -> {
            String instIdStr = (String) suggestionLabel.getClientProperty("inst_id");
            String currentBwStr = (String) suggestionLabel.getClientProperty("current_bw");
            
            if (instIdStr == null || instIdStr.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please search and select a client from the left panel first to apply the upgrade.", "Select Client", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            if (currentSelectedBandwidth == 0) {
                JOptionPane.showMessageDialog(this, "Please select a new bandwidth tier.", "Notice", JOptionPane.WARNING_MESSAGE);
                return;
            }

            // ANTI-DOWNGRADE SECURITY LOGIC
            int currentBw = Integer.parseInt(currentBwStr);
            if (currentSelectedBandwidth <= currentBw) {
                JOptionPane.showMessageDialog(this, "Invalid Selection: You can only UPGRADE to a higher tier.\nThis client's current tier is " + currentBw + " MBPS.", "Downgrade Prohibited", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            int institutionId = Integer.parseInt(instIdStr);
            PaymentDAO payDao = new PaymentDAO();
            
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

    // --- HELPER METHODS FOR MODERN UI ---
    
    private void styleTextField(JTextField field) {
        field.setBackground(new Color(248, 250, 252));
        field.setForeground(new Color(15, 23, 42));
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(226, 232, 240), 1),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
    }

    private void applyHoverEffect(JButton btn, Color defaultColor, Color hoverColor) {
        btn.setBackground(defaultColor);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR)); 
        
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btn.setBackground(hoverColor);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btn.setBackground(defaultColor); 
            }
        });
    }

    private void resetBillingForm() {
        searchArea.setText("");
        amountPaid.setText("0.00");
        billPreview.setText("Search and select a client to preview their bill...");
        suggestionLabel.setText("Start typing to search...");
        suggestionLabel.putClientProperty("inst_id", null);
        suggestionLabel.putClientProperty("inst_name", null);
        suggestionLabel.putClientProperty("current_bw", null);
        bandwidthCombo.setSelectedIndex(0);
        discountDisplay.setText("KSh 0.00");
        currentPaymentType = "Monthly Bill";
    }
}