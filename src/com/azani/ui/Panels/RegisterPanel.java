package com.azani.ui.Panels;

import com.azani.dao.InstitutionDAO;
import com.azani.service.BillingManager;

import javax.swing.*;
import java.awt.*;

public class RegisterPanel extends JPanel {
    
    // UI Components needed across multiple methods
    private JTextField instName, contactName, phoneNumber, contactEmail, pcsField;
    private JComboBox<String> comboBox, bandwidthBox, nodesBox;
    private JCheckBox requirements;
    private JLabel errorLabel;
    private JTextArea billDisplay;
    
    // Variables to hold calculated costs before saving
    private double currentInstallFee = 0.0;
    private double currentHardwareFee = 0.0;
    private int currentBandwidth = 0;
    private boolean isBillCalculated = false;

    public RegisterPanel() {
        setLayout(new BorderLayout());
        setBackground(new Color(248, 250, 252)); 

        // --- TITLE PANEL ---
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setBackground(Color.WHITE);
        titlePanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        
        JLabel titleLabel = new JLabel("Registration");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titlePanel.add(titleLabel, BorderLayout.WEST);

        // --- REGISTRATION FORM ---
        JPanel registrationPanel = new JPanel();
        registrationPanel.setLayout(new BoxLayout(registrationPanel, BoxLayout.Y_AXIS));
        registrationPanel.setBackground(Color.WHITE);
        
        JPanel cardWrapper = new JPanel(new BorderLayout());
        cardWrapper.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        cardWrapper.setBackground(new Color(248, 250, 252));
        cardWrapper.add(registrationPanel, BorderLayout.CENTER);

        JPanel mainContentWrapper = new JPanel(new BorderLayout());
        mainContentWrapper.setBackground(new Color(248, 250, 252));
        mainContentWrapper.add(cardWrapper, BorderLayout.NORTH);

        // --- SECTION 1: HEADER & ERROR LABEL ---
        JLabel institutionLabel = new JLabel("New Institution Onboarding");
        institutionLabel.setFont(new Font("Arial", Font.BOLD, 16));
        institutionLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 5, 0));
        institutionLabel.setAlignmentX(Component.LEFT_ALIGNMENT); 
        
        // Inline Error Label replacing OptionPanes
        errorLabel = new JLabel(" ");
        errorLabel.setForeground(new Color(220, 53, 69)); // Red
        errorLabel.setFont(new Font("Arial", Font.BOLD, 12));
        errorLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));
        errorLabel.setAlignmentX(Component.LEFT_ALIGNMENT); 

        JLabel institution = new JLabel("1. INSTITUTION & CONTACT DETAILS");
        institution.setForeground(new Color(32, 201, 151)); 
        institution.setFont(new Font("Arial", Font.BOLD, 12));
        institution.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));
        institution.setAlignmentX(Component.LEFT_ALIGNMENT); 

        registrationPanel.add(institutionLabel);
        registrationPanel.add(errorLabel);
        registrationPanel.add(institution);
        
        // --- SECTION 1: GRID INPUTS ---
        JPanel onboardPanel = new JPanel(new GridLayout(3, 2, 20, 20));
        onboardPanel.setBackground(Color.WHITE);
        onboardPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 190)); 
        onboardPanel.setAlignmentX(Component.LEFT_ALIGNMENT); 

        instName = new JTextField();
        String[] instType = {"Select", "Primary School", "Junior School", "Senior School", "College"};
        comboBox = new JComboBox<>(instType);
        contactName = new JTextField();
        phoneNumber = new JTextField();
        contactEmail = new JTextField();

        onboardPanel.add(createInputGroup("Institution Name", instName));
        onboardPanel.add(createInputGroup("Institution Type", comboBox));
        onboardPanel.add(createInputGroup("Contact Person Name", contactName));
        onboardPanel.add(createInputGroup("Contact Phone", phoneNumber));
        onboardPanel.add(createInputGroup("Contact Email", contactEmail));

        registrationPanel.add(onboardPanel);
        registrationPanel.add(Box.createVerticalStrut(25)); 

        // --- SECTION 2: INFRASTRUCTURE ---
        JLabel services = new JLabel("2. INFRASTRUCTURE & SERVICES");
        services.setForeground(new Color(32, 201, 151));
        services.setFont(new Font("Arial", Font.BOLD, 12));
        services.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        services.setAlignmentX(Component.LEFT_ALIGNMENT); 
        registrationPanel.add(services);

        JPanel infrastructure = new JPanel(new GridLayout(1, 2, 20, 0));
        infrastructure.setBackground(Color.WHITE);
        infrastructure.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));
        infrastructure.setAlignmentX(Component.LEFT_ALIGNMENT); 

        String[] bandwidth = {"4 MBPS", "10 MBPS", "20 MBPS", "25 MBPS", "50 MBPS"};
        bandwidthBox = new JComboBox<>(bandwidth);

        requirements = new JCheckBox("Institution meets connectivity requirements (Pays Ksh 10k fee)");
        requirements.setBackground(Color.WHITE);

        infrastructure.add(createInputGroup("Desired Bandwidth", bandwidthBox));
        infrastructure.add(createInputGroup(" ", requirements)); 
        
        registrationPanel.add(infrastructure);
        registrationPanel.add(Box.createVerticalStrut(20)); 

        // --- HARDWARE SECTION ---
        JPanel hardwarePanel = new JPanel();
        hardwarePanel.setLayout(new BoxLayout(hardwarePanel, BoxLayout.Y_AXIS));
        hardwarePanel.setBackground(new Color(248, 250, 252)); 
        hardwarePanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(226, 232, 240), 1),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        hardwarePanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        hardwarePanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 120)); 

        JLabel hardwareLabel = new JLabel("If requirements NOT met, select hardware to purchase:");
        hardwareLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        hardwareLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        hardwareLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        hardwarePanel.add(hardwareLabel);

        JPanel hardwareGrid = new JPanel(new GridLayout(1, 2, 20, 0));
        hardwareGrid.setBackground(new Color(248, 250, 252));
        hardwareGrid.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        pcsField = new JTextField("0");
        String[] nodes = {"None (0)", "2-10 Nodes", "11-20 Nodes", "21-40 Nodes", "41-100 Nodes"};
        nodesBox = new JComboBox<>(nodes);
        
        hardwareGrid.add(createInputGroup("Number of PCs (KSh 40,000 each)", pcsField));
        hardwareGrid.add(createInputGroup("LAN Nodes needed", nodesBox));
        hardwarePanel.add(hardwareGrid);

        registrationPanel.add(hardwarePanel);
        registrationPanel.add(Box.createVerticalStrut(20)); 
        
        // --- BILL DISPLAY SECTION ---
        billDisplay = new JTextArea(7, 40);
        billDisplay.setEditable(false);
        billDisplay.setFont(new Font("Monospaced", Font.PLAIN, 12));
        billDisplay.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.LIGHT_GRAY),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        billDisplay.setText("Click 'Calculate Bill' to preview expected charges here...");
        
        JScrollPane scrollBill = new JScrollPane(billDisplay);
        scrollBill.setAlignmentX(Component.LEFT_ALIGNMENT);
        scrollBill.setMaximumSize(new Dimension(Integer.MAX_VALUE, 130));
        registrationPanel.add(scrollBill);
        registrationPanel.add(Box.createVerticalStrut(20));

        // --- BUTTONS SECTION ---
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        buttonPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40)); 

        JButton calcBtn = new JButton("Calculate Bill");
        calcBtn.setBackground(new Color(59, 130, 246)); // Blue
        calcBtn.setForeground(Color.WHITE);
        calcBtn.setFocusPainted(false);
        calcBtn.setPreferredSize(new Dimension(150, 35));
        
        JButton submitBtn = new JButton("Confirm Registration");
        submitBtn.setBackground(new Color(32, 201, 151)); // Green
        submitBtn.setForeground(Color.WHITE);
        submitBtn.setFocusPainted(false);
        submitBtn.setPreferredSize(new Dimension(180, 35));

        buttonPanel.add(calcBtn);
        buttonPanel.add(submitBtn);
        
        registrationPanel.add(buttonPanel);

        // Add everything to the main panel
        add(titlePanel, BorderLayout.NORTH);
        add(mainContentWrapper, BorderLayout.CENTER); 

        // --- ACTION LISTENERS ---

        // 1. Calculate Bill Action
        calcBtn.addActionListener(e -> {
            if (!validateInputs()) return;
            calculateAndDisplayBill();
            isBillCalculated = true;
        });

        // 2. Submit Action
        submitBtn.addActionListener(e -> {
            if (!validateInputs()) return;
            
            if (!isBillCalculated) {
                errorLabel.setText("Please calculate the bill first to preview charges.");
                return;
            }

            // Database Integrity Checks (Must happen BEFORE insertion)
            String name = instName.getText().trim().toUpperCase();
            String cEmail = contactEmail.getText().trim().toLowerCase();
            InstitutionDAO dao = new InstitutionDAO();
            
            if (dao.isNameTaken(name)) {
                errorLabel.setText("An institution with this name is already registered!");
                return;
            }
            if (dao.isEmailTaken(cEmail)) {
                errorLabel.setText("The email " + cEmail + " is already used by another client!");
                return;
            }

            String category = comboBox.getSelectedItem().toString();
            String cName = contactName.getText().trim().toUpperCase();
            String cPhone = phoneNumber.getText().trim();

            boolean success = dao.registerInstitution(name, category, cName, cPhone, cEmail, currentBandwidth, currentInstallFee, currentHardwareFee);

            if (success) {
                JOptionPane.showMessageDialog(this, "Registration successful! Initial fees recorded.");
                clearForm();
            } else {
                errorLabel.setText("Registration failed. Check database connection.");
            }
        });
    }

    // --- HELPER METHODS ---
    
    private boolean validateInputs() {
        errorLabel.setText(" "); // clear errors
        
        if (instName.getText().trim().isEmpty() || contactName.getText().trim().isEmpty() || 
            phoneNumber.getText().trim().isEmpty() || contactEmail.getText().trim().isEmpty() || 
            comboBox.getSelectedIndex() == 0) {
            errorLabel.setText("Please fill in all required fields and select an Institution Type.");
            return false;
        }

        if (!contactEmail.getText().trim().contains("@")) {
            errorLabel.setText("Please enter a valid email address.");
            return false;
        }

        if (!phoneNumber.getText().trim().matches("^(07|01)\\d{8}$")) {
            errorLabel.setText("Phone number must be exactly 10 digits starting with 07 or 01.");
            return false;
        }
        
        return true;
    }

    private void calculateAndDisplayBill() {
        // Extract Bandwidth Int
        String bwText = bandwidthBox.getSelectedItem().toString();
        currentBandwidth = Integer.parseInt(bwText.split(" ")[0]);
        
        // Use BillingManager for calculations[cite: 8]
        double regFee = BillingManager.REGISTRATION_FEE;
        double monthlyCost = BillingManager.getMonthlyBandwidthCost(currentBandwidth);
        
        currentInstallFee = 0.0;
        currentHardwareFee = 0.0;

        if (requirements.isSelected()) {
            currentInstallFee = BillingManager.INSTALLATION_FEE_READY;
        } else {
            int pcCount = 0;
            String pcText = pcsField.getText().trim();
            if (pcText.matches("\\d+")) {
                pcCount = Integer.parseInt(pcText);
            }
            
            // Map dropdown index to representative LAN node count for BillingManager
            int lanNodes = 0;
            int lanIndex = nodesBox.getSelectedIndex();
            if (lanIndex == 1) lanNodes = 5;       
            else if (lanIndex == 2) lanNodes = 15; 
            else if (lanIndex == 3) lanNodes = 30; 
            else if (lanIndex == 4) lanNodes = 50; 
            
            currentHardwareFee = BillingManager.calculateHardwareCost(pcCount, lanNodes);
        }

        double totalInitial = regFee + currentInstallFee + currentHardwareFee + monthlyCost;

        // Build string for Text Area
        StringBuilder bill = new StringBuilder();
        bill.append("--- REGISTRATION BILL PREVIEW ---\n");
        bill.append(String.format("Registration Fee:      KSh %,.2f\n", regFee));
        bill.append(String.format("1st Month Bandwidth:   KSh %,.2f\n", monthlyCost));
        if (currentInstallFee > 0) {
            bill.append(String.format("Installation Fee:      KSh %,.2f\n", currentInstallFee));
        }
        if (currentHardwareFee > 0) {
            bill.append(String.format("Hardware & LAN Fees:   KSh %,.2f\n", currentHardwareFee));
        }
        bill.append("---------------------------------\n");
        bill.append(String.format("TOTAL DUE TODAY:       KSh %,.2f", totalInitial));
        
        billDisplay.setText(bill.toString());
    }

    private void clearForm() {
        instName.setText("");
        comboBox.setSelectedIndex(0);
        contactName.setText("");
        phoneNumber.setText("");
        contactEmail.setText("");
        pcsField.setText("0");
        nodesBox.setSelectedIndex(0);
        requirements.setSelected(false);
        bandwidthBox.setSelectedIndex(0);
        billDisplay.setText("Click 'Calculate Bill' to preview expected charges here...");
        errorLabel.setText(" ");
        isBillCalculated = false;
    }

    private JPanel createInputGroup(String labelText, JComponent inputComponent) {
        JPanel group = new JPanel(new BorderLayout(0, 5)); 
        group.setOpaque(false); 
        
        JLabel label = new JLabel(labelText);
        label.setForeground(new Color(100, 116, 139)); 
        label.setFont(new Font("Arial", Font.PLAIN, 12));
        
        inputComponent.setPreferredSize(new Dimension(inputComponent.getPreferredSize().width, 35));

        group.add(label, BorderLayout.NORTH);
        group.add(inputComponent, BorderLayout.CENTER);
        
        return group;
    }
}