package com.azani.ui.Panels;

import com.azani.dao.InstitutionDAO;
import com.azani.service.BillingManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ItemEvent;

public class RegisterPanel extends JPanel {
    
    // --- MODERN WEB PALETTE ---
    private final Color bgApp = new Color(248, 250, 252);         // Slate 50
    private final Color cardSurface = Color.WHITE;                // Clean White
    private final Color textDark = new Color(15, 23, 42);         // Slate 900
    private final Color textMuted = new Color(100, 116, 139);     // Slate 500
    private final Color borderLight = new Color(226, 232, 240);   // Slate 200
    private final Color primaryAccent = new Color(14, 165, 233);  // Sky Blue 500
    private final Color successGreen = new Color(16, 185, 129);   // Emerald 500
    private final Color dangerRed = new Color(220, 53, 69);       // Red 500

    // UI Components
    private JTextField instName, contactName, phoneNumber, contactEmail, pcsField;
    private JComboBox<String> comboBox, bandwidthBox, nodesBox;
    private JCheckBox requirements;
    private JLabel errorLabel;
    private JTextArea billDisplay;
    private JPanel hardwarePanel; // Elevated to toggle visibility
    
    // Variables to hold calculated costs
    private double currentInstallFee = 0.0;
    private double currentHardwareFee = 0.0;
    private int currentBandwidth = 0;
    private boolean isBillCalculated = false;

    public RegisterPanel() {
        setLayout(new BorderLayout());
        setBackground(bgApp); 

        // --- TITLE PANEL ---
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setBackground(cardSurface);
        titlePanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        
        JLabel titleLabel = new JLabel("Registration");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLabel.setForeground(textDark);
        titlePanel.add(titleLabel, BorderLayout.WEST);

        // --- REGISTRATION FORM ---
        JPanel registrationPanel = new JPanel();
        registrationPanel.setLayout(new BoxLayout(registrationPanel, BoxLayout.Y_AXIS));
        registrationPanel.setBackground(cardSurface);
        
        JPanel cardWrapper = new JPanel(new BorderLayout());
        cardWrapper.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        cardWrapper.setBackground(bgApp);
        cardWrapper.add(registrationPanel, BorderLayout.CENTER);

        JPanel mainContentWrapper = new JPanel(new BorderLayout());
        mainContentWrapper.setBackground(bgApp);
        mainContentWrapper.add(cardWrapper, BorderLayout.NORTH);

        // --- HEADER & ERROR LABEL ---
        JLabel institutionLabel = new JLabel("New Institution Onboarding");
        institutionLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        institutionLabel.setForeground(textDark);
        institutionLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 5, 0));
        institutionLabel.setAlignmentX(Component.LEFT_ALIGNMENT); 
        
        errorLabel = new JLabel(" ");
        errorLabel.setForeground(dangerRed);
        errorLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        errorLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));
        errorLabel.setAlignmentX(Component.LEFT_ALIGNMENT); 

        JLabel institution = new JLabel("1. INSTITUTION & CONTACT DETAILS");
        institution.setForeground(primaryAccent); 
        institution.setFont(new Font("Segoe UI", Font.BOLD, 13));
        institution.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));
        institution.setAlignmentX(Component.LEFT_ALIGNMENT); 

        registrationPanel.add(institutionLabel);
        registrationPanel.add(errorLabel);
        registrationPanel.add(institution);
        
        // --- SECTION 1: GRID INPUTS ---
        JPanel onboardPanel = new JPanel(new GridLayout(3, 2, 25, 20));
        onboardPanel.setBackground(cardSurface);
        onboardPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 220)); 
        onboardPanel.setAlignmentX(Component.LEFT_ALIGNMENT); 

        instName = new JTextField();
        styleTextField(instName);
        
        String[] instType = {"Select", "Primary School", "Junior School", "Senior School", "College"};
        comboBox = new JComboBox<>(instType);
        styleComboBox(comboBox);
        
        contactName = new JTextField();
        styleTextField(contactName);
        
        phoneNumber = new JTextField();
        styleTextField(phoneNumber);
        
        contactEmail = new JTextField();
        styleTextField(contactEmail);

        onboardPanel.add(createInputGroup("Institution Name", instName));
        onboardPanel.add(createInputGroup("Institution Type", comboBox));
        onboardPanel.add(createInputGroup("Contact Person Name", contactName));
        onboardPanel.add(createInputGroup("Contact Phone", phoneNumber));
        onboardPanel.add(createInputGroup("Contact Email", contactEmail));

        registrationPanel.add(onboardPanel);
        registrationPanel.add(Box.createVerticalStrut(30)); 

        // --- SECTION 2: INFRASTRUCTURE ---
        JLabel services = new JLabel("2. INFRASTRUCTURE & SERVICES");
        services.setForeground(primaryAccent);
        services.setFont(new Font("Segoe UI", Font.BOLD, 13));
        services.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));
        services.setAlignmentX(Component.LEFT_ALIGNMENT); 
        registrationPanel.add(services);

        JPanel infrastructure = new JPanel(new GridLayout(1, 2, 25, 0));
        infrastructure.setBackground(cardSurface);
        infrastructure.setMaximumSize(new Dimension(Integer.MAX_VALUE, 65));
        infrastructure.setAlignmentX(Component.LEFT_ALIGNMENT); 

        String[] bandwidth = {"4 MBPS", "10 MBPS", "20 MBPS", "25 MBPS", "50 MBPS"};
        bandwidthBox = new JComboBox<>(bandwidth);
        styleComboBox(bandwidthBox);

        requirements = new JCheckBox("Institution meets connectivity requirements (Pays KSh 10k fee)");
        requirements.setBackground(cardSurface);
        requirements.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        requirements.setForeground(textDark);
        requirements.setFocusPainted(false);
        requirements.setCursor(new Cursor(Cursor.HAND_CURSOR));

        infrastructure.add(createInputGroup("Desired Bandwidth", bandwidthBox));
        
        // Wrap checkbox to align properly
        JPanel checkWrapper = new JPanel(new BorderLayout());
        checkWrapper.setBackground(cardSurface);
        checkWrapper.setBorder(BorderFactory.createEmptyBorder(25, 0, 0, 0));
        checkWrapper.add(requirements, BorderLayout.CENTER);
        infrastructure.add(checkWrapper); 
        
        registrationPanel.add(infrastructure);
        registrationPanel.add(Box.createVerticalStrut(20)); 

        // --- HARDWARE SECTION (Dynamic) ---
        hardwarePanel = new JPanel();
        hardwarePanel.setLayout(new BoxLayout(hardwarePanel, BoxLayout.Y_AXIS));
        hardwarePanel.setBackground(bgApp); 
        hardwarePanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(borderLight, 1),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));
        hardwarePanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        hardwarePanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 130)); 

        JLabel hardwareLabel = new JLabel("Hardware required (Connectivity requirements not met):");
        hardwareLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        hardwareLabel.setForeground(textDark);
        hardwareLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));
        hardwareLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        hardwarePanel.add(hardwareLabel);

        JPanel hardwareGrid = new JPanel(new GridLayout(1, 2, 25, 0));
        hardwareGrid.setBackground(bgApp);
        hardwareGrid.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        pcsField = new JTextField("0");
        styleTextField(pcsField);
        pcsField.setBackground(Color.WHITE);
        
        String[] nodes = {"None (0)", "2-10 Nodes", "11-20 Nodes", "21-40 Nodes", "41-100 Nodes"};
        nodesBox = new JComboBox<>(nodes);
        styleComboBox(nodesBox);
        nodesBox.setBackground(Color.WHITE);
        
        hardwareGrid.add(createInputGroup("Number of PCs (KSh 40,000 each)", pcsField));
        hardwareGrid.add(createInputGroup("LAN Nodes needed", nodesBox));
        hardwarePanel.add(hardwareGrid);

        registrationPanel.add(hardwarePanel);
        registrationPanel.add(Box.createVerticalStrut(25)); 

        // --- DYNAMIC VISIBILITY LOGIC ---
        requirements.addItemListener(e -> {
            boolean isReady = (e.getStateChange() == ItemEvent.SELECTED);
            hardwarePanel.setVisible(!isReady);
            if (isReady) {
                pcsField.setText("0");
                nodesBox.setSelectedIndex(0);
            }
            revalidate();
            repaint();
        });
        
        // --- BILL DISPLAY SECTION ---
        billDisplay = new JTextArea(7, 40);
        billDisplay.setEditable(false);
        billDisplay.setBackground(bgApp);
        billDisplay.setForeground(textDark);
        billDisplay.setFont(new Font("Monospaced", Font.PLAIN, 14));
        billDisplay.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(borderLight),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        billDisplay.setText("Click 'Calculate Bill' to preview expected charges here...");
        
        JScrollPane scrollBill = new JScrollPane(billDisplay);
        scrollBill.setAlignmentX(Component.LEFT_ALIGNMENT);
        scrollBill.setMaximumSize(new Dimension(Integer.MAX_VALUE, 160));
        registrationPanel.add(scrollBill);
        registrationPanel.add(Box.createVerticalStrut(25));

        // --- BUTTONS SECTION ---
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        buttonPanel.setBackground(cardSurface);
        buttonPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        buttonPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45)); 

        JButton calcBtn = new JButton("Calculate Bill");
        calcBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        calcBtn.setPreferredSize(new Dimension(160, 40));
        calcBtn.setBorderPainted(false);
        applyHoverEffect(calcBtn, primaryAccent, new Color(2, 132, 199)); // Slate hover

        JButton submitBtn = new JButton("Confirm Registration");
        submitBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        submitBtn.setPreferredSize(new Dimension(200, 40));
        submitBtn.setBorderPainted(false);
        applyHoverEffect(submitBtn, successGreen, new Color(4, 120, 87)); // Emerald hover

        buttonPanel.add(calcBtn);
        buttonPanel.add(submitBtn);
        
        registrationPanel.add(buttonPanel);

        add(titlePanel, BorderLayout.NORTH);
        add(mainContentWrapper, BorderLayout.CENTER); 

        // --- ACTION LISTENERS ---
        calcBtn.addActionListener(e -> {
            if (!validateInputs()) return;
            calculateAndDisplayBill();
            isBillCalculated = true;
        });

        submitBtn.addActionListener(e -> {
            if (!validateInputs()) return;
            
            if (!isBillCalculated) {
                errorLabel.setText("Please calculate the bill first to preview charges.");
                return;
            }

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

    private void styleTextField(JTextField field) {
        field.setBackground(bgApp);
        field.setForeground(textDark);
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(borderLight, 1),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
    }
    
    private void styleComboBox(JComboBox<String> box) {
        box.setBackground(bgApp);
        box.setForeground(textDark);
        box.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        box.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    private void applyHoverEffect(JButton btn, Color defaultColor, Color hoverColor) {
        btn.setBackground(defaultColor);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
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

    private JPanel createInputGroup(String labelText, JComponent inputComponent) {
        JPanel group = new JPanel(new BorderLayout(0, 8)); 
        group.setOpaque(false); 
        
        JLabel label = new JLabel(labelText);
        label.setForeground(textMuted); 
        label.setFont(new Font("Segoe UI", Font.BOLD, 12));
        
        inputComponent.setPreferredSize(new Dimension(inputComponent.getPreferredSize().width, 42));

        group.add(label, BorderLayout.NORTH);
        group.add(inputComponent, BorderLayout.CENTER);
        
        return group;
    }
    
    private boolean validateInputs() {
        errorLabel.setText(" "); 
        
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
        String bwText = bandwidthBox.getSelectedItem().toString();
        currentBandwidth = Integer.parseInt(bwText.split(" ")[0]);
        
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
            
            int lanNodes = 0;
            int lanIndex = nodesBox.getSelectedIndex();
            if (lanIndex == 1) lanNodes = 5;       
            else if (lanIndex == 2) lanNodes = 15; 
            else if (lanIndex == 3) lanNodes = 30; 
            else if (lanIndex == 4) lanNodes = 50; 
            
            currentHardwareFee = BillingManager.calculateHardwareCost(pcCount, lanNodes);
        }

        double totalInitial = regFee + currentInstallFee + currentHardwareFee + monthlyCost;

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
        
        // Reset dynamic panel
        hardwarePanel.setVisible(true);
    }
}