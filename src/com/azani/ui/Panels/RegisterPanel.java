package com.azani.ui.Panels;

import com.azani.dao.InstitutionDAO;
//import com.azani.dao.PaymentDAO;

import javax.swing.*;
import java.awt.*;

public class RegisterPanel extends JPanel {
    
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

        // --- REGISTRATION FORM (The White Card) ---
        JPanel registrationPanel = new JPanel();
        registrationPanel.setLayout(new BoxLayout(registrationPanel, BoxLayout.Y_AXIS));
        registrationPanel.setBackground(Color.WHITE);
        
        JPanel cardWrapper = new JPanel(new BorderLayout());
        cardWrapper.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        cardWrapper.setBackground(new Color(248, 250, 252));
        cardWrapper.add(registrationPanel, BorderLayout.CENTER);

        // The Anti-Stretch Wrapper
        JPanel mainContentWrapper = new JPanel(new BorderLayout());
        mainContentWrapper.setBackground(new Color(248, 250, 252));
        mainContentWrapper.add(cardWrapper, BorderLayout.NORTH);

        // --- SECTION 1: HEADER ---
        JLabel institutionLabel = new JLabel("New Institution Onboarding");
        institutionLabel.setFont(new Font("Arial", Font.BOLD, 16));
        institutionLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));
        institutionLabel.setAlignmentX(Component.LEFT_ALIGNMENT); 
        
        JLabel institution = new JLabel("1. INSTITUTION & CONTACT DETAILS");
        institution.setForeground(new Color(32, 201, 151)); 
        institution.setFont(new Font("Arial", Font.BOLD, 12));
        institution.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));
        institution.setAlignmentX(Component.LEFT_ALIGNMENT); 

        registrationPanel.add(institutionLabel);
        registrationPanel.add(institution);
        
        // --- SECTION 1: GRID INPUTS ---
        JPanel onboardPanel = new JPanel(new GridLayout(3, 2, 20, 20));
        onboardPanel.setBackground(Color.WHITE);
        onboardPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 190)); 
        onboardPanel.setAlignmentX(Component.LEFT_ALIGNMENT); 

        JTextField instName = new JTextField();
        String[] instType = {"Select", "Primary School", "Junior School", "Senior School", "College"};
        JComboBox<String> comboBox = new JComboBox<>(instType);
        JTextField contactName = new JTextField();
        JTextField phoneNumber = new JTextField();
        JTextField contactEmail = new JTextField();

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

        String[] bandwidth = {"4 MBPS (Ksh 1,200/mo)", "10 MBPS (Ksh 2,000/mo)", "25 MBPS (Ksh 4,000/mo)", "50 MBPS (Ksh 7,000/mo)"};
        JComboBox<String> bandwidthBox = new JComboBox<>(bandwidth);

        JCheckBox requirements = new JCheckBox("Institution meets connectivity requirements (Pays Ksh 10k fee)");
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
        
        JTextField pcsField = new JTextField("0");
        String[] nodes = {"None (0)", "1-5 Nodes", "6-10 Nodes"};
        JComboBox<String> nodesBox = new JComboBox<>(nodes);
        
        hardwareGrid.add(createInputGroup("Number of PCs (KSh 40,000 each)", pcsField));
        hardwareGrid.add(createInputGroup("LAN Nodes needed", nodesBox));
        hardwarePanel.add(hardwareGrid);

        registrationPanel.add(hardwarePanel);
        registrationPanel.add(Box.createVerticalStrut(20)); 

        // --- BUTTONS SECTION ---
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        buttonPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40)); 

        JButton cancelBtn = new JButton("Cancel");
        cancelBtn.setBackground(Color.WHITE);
        cancelBtn.setPreferredSize(new Dimension(100, 35));

        JButton submitBtn = new JButton("Complete Registration");
        submitBtn.setBackground(new Color(32, 201, 151)); 
        submitBtn.setForeground(Color.WHITE);
        submitBtn.setFocusPainted(false);
        submitBtn.setPreferredSize(new Dimension(180, 35));

        buttonPanel.add(cancelBtn);
        buttonPanel.add(submitBtn);
        
        registrationPanel.add(buttonPanel);

        // Add everything to the main panel
        add(titlePanel, BorderLayout.NORTH);
        add(mainContentWrapper, BorderLayout.CENTER); 

        // --- ACTION LISTENERS ---
        // --- ACTION LISTENERS ---
        submitBtn.addActionListener(e -> {
            // 1. Grab data and use .trim() to remove accidental blank spaces
            String name = instName.getText().trim();
            String category = comboBox.getSelectedItem().toString();
            String cName = contactName.getText().trim();
            String cPhone = phoneNumber.getText().trim();
            String cEmail = contactEmail.getText().trim();

            // 2. THE VALIDATION BLOCK
            // If any text field is empty OR the dropdown is on the first item (index 0)
            if (name.isEmpty() || cName.isEmpty() || cPhone.isEmpty() || cEmail.isEmpty() || comboBox.getSelectedIndex() == 0) {
                JOptionPane.showMessageDialog(this, 
                    "Please fill in all required fields and select an Institution Type.", 
                    "Missing Information", 
                    JOptionPane.WARNING_MESSAGE);
                return; // Stops the code here! Doesn't touch the database.
            }

            // 3. IF VALID, PROCEED TO DATABASE
            InstitutionDAO dao = new InstitutionDAO();
            boolean success = dao.registerInstitution(name, category, cName, cPhone, cEmail);

            if (success) {
                JOptionPane.showMessageDialog(this, "Registration successful!");
                // Clear fields after saving
                instName.setText("");
                comboBox.setSelectedIndex(0);
                contactName.setText("");
                phoneNumber.setText("");
                contactEmail.setText("");
            } else {
                JOptionPane.showMessageDialog(this, "Registration failed.", "Database Error", JOptionPane.ERROR_MESSAGE);
            }
        });
    }

    // --- HELPER METHOD ---
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