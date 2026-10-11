package com.azani.ui;
import com.azani.ui.Panels.HomePanel;
import com.azani.ui.Panels.RegisterPanel;
import com.azani.ui.Panels.BillingPanel;
import com.azani.ui.Panels.ReportsPanel;

import javax.swing.*;
import java.awt.*;

//@SuppressWarnings("unused")
public class MainDashboard extends JFrame {

    public MainDashboard(){
        // ---------------------------------------------------------
        // 1. WINDOW SETUP
        // ---------------------------------------------------------
        setSize(10000, 6000);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setTitle("AZANI ISP");

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());

        // ---------------------------------------------------------
        // 2. COLOR PALETTE
        // ---------------------------------------------------------
        Color navyBlue = new Color(26, 32, 44);
        Color textGray = new Color(148, 163, 184);
        Color teal = new Color(32, 201, 151);

        // ---------------------------------------------------------
        // 3. MAIN CONTENT AREA (The CardLayout Engine)
        // ---------------------------------------------------------
        CardLayout cardLayout = new CardLayout();
        JPanel contentPanel = new JPanel(cardLayout);

        // Create temporary colored panels
        HomePanel homePage = new HomePanel();
        RegisterPanel registrationPage = new RegisterPanel();
        BillingPanel paymentsPage = new BillingPanel();
        ReportsPanel reportsPage = new ReportsPanel();

        contentPanel.add(homePage, "HOME");
        contentPanel.add(registrationPage, "REGISTER");
        contentPanel.add(paymentsPage, "PAYMENTS");
        contentPanel.add(reportsPage, "REPORTS");

        // ---------------------------------------------------------
        // 4. SIDEBAR - MAIN CONTAINER
        // ---------------------------------------------------------
        JPanel sidebarPanel = new JPanel(new BorderLayout());
        sidebarPanel.setBackground(navyBlue);
        sidebarPanel.setPreferredSize(new Dimension(250, 600));

        // ---------------------------------------------------------
        // 5. SIDEBAR - TOP SECTION (AZANI ISP LOGO)
        // ---------------------------------------------------------
        JPanel logoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 20));
        logoPanel.setBackground(navyBlue);

        // Extract the image, scale it to 24x24 pixels, and repackage it as an ImageIcon
        ImageIcon rawWifi = new ImageIcon(getClass().getResource("icons/wifi.png"));
        Image scaledWifi = rawWifi.getImage().getScaledInstance(24, 24, Image.SCALE_SMOOTH);
        ImageIcon wifi = new ImageIcon(scaledWifi);
        JLabel wifiIcon = new JLabel(wifi);
        
        JLabel lblAzani = new JLabel("AZANI");
        lblAzani.setForeground(Color.WHITE);
        lblAzani.setFont(new Font("Arial", Font.BOLD, 22));
        
        JLabel lblIsp = new JLabel("ISP");
        lblIsp.setForeground(teal);
        lblIsp.setFont(new Font("Arial", Font.BOLD, 22));
        
        logoPanel.add(wifiIcon);
        logoPanel.add(lblAzani);
        logoPanel.add(lblIsp);
        sidebarPanel.add(logoPanel, BorderLayout.NORTH);

        // ---------------------------------------------------------
        // 6. SIDEBAR - MIDDLE SECTION (MENU BUTTONS)
        // ---------------------------------------------------------
        JPanel menuPanel = new JPanel();
        menuPanel.setLayout(new BoxLayout(menuPanel, BoxLayout.Y_AXIS));
        menuPanel.setBackground(navyBlue);
        menuPanel.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15)); // Adds padding

        JLabel text = new JLabel("MAIN MENU");
        text.setForeground(textGray);
        text.setFont(new Font("Arial", Font.BOLD, 12));
        text.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0)); // Space below text
        
        // --- Create Dashboard Button ---
        JButton dashboardBtn = new JButton("Dashboard");
        dashboardBtn.setBackground(navyBlue);
        dashboardBtn.setForeground(Color.WHITE);
        dashboardBtn.setFocusPainted(false);
        dashboardBtn.setBorderPainted(false);
        dashboardBtn.setHorizontalAlignment(SwingConstants.LEFT);
        dashboardBtn.setContentAreaFilled(false);
        dashboardBtn.setOpaque(true);

        ImageIcon homeIcon = new ImageIcon(getClass().getResource("icons/dashboard.png"));
        dashboardBtn.setIcon(homeIcon);
        dashboardBtn.setIconTextGap(15);
        
        // --- Create Registration Button ---
        JButton registrationBtn = new JButton("Registration");
        registrationBtn.setForeground(Color.WHITE);
        registrationBtn.setBorderPainted(false);
        registrationBtn.setFocusPainted(false);
        registrationBtn.setBackground(navyBlue);
        registrationBtn.setHorizontalAlignment(SwingConstants.LEFT);
        registrationBtn.setContentAreaFilled(false);
        registrationBtn.setOpaque(true);

        ImageIcon registrationIcon = new ImageIcon(getClass().getResource("icons/registration.png"));
        registrationBtn.setIcon(registrationIcon);
        registrationBtn.setIconTextGap(15);

        // --- Create Payments Button ---
        JButton paymentsBtn = new JButton("Payments & Billing");
        paymentsBtn.setForeground(Color.WHITE);
        paymentsBtn.setBackground(navyBlue);
        paymentsBtn.setFocusPainted(false);
        paymentsBtn.setBorderPainted(false);
        paymentsBtn.setHorizontalAlignment(SwingConstants.LEFT);
        paymentsBtn.setContentAreaFilled(false);
        paymentsBtn.setOpaque(true);
        
        ImageIcon paymentsIcon= new ImageIcon(getClass().getResource("icons/billing.png"));
        paymentsBtn.setIcon(paymentsIcon);
        paymentsBtn.setIconTextGap(15);

        // --- Create Reports Button ---
        JButton reportsBtn = new JButton("Reports");
        reportsBtn.setBorderPainted(false);
        reportsBtn.setFocusPainted(false);
        reportsBtn.setBackground(navyBlue);
        reportsBtn.setForeground(Color.WHITE);
        reportsBtn.setHorizontalAlignment(SwingConstants.LEFT);
        reportsBtn.setContentAreaFilled(false);
        reportsBtn.setOpaque(true);

        ImageIcon reportsIcon = new ImageIcon(getClass().getResource("icons/reports.png"));
        reportsBtn.setIcon(reportsIcon);
        reportsBtn.setIconTextGap(15);

        // ---------------------------------------------------------
        // 7. BUTTON ACTION LISTENERS
        // ---------------------------------------------------------
        JButton[] allButtons = {dashboardBtn, registrationBtn, paymentsBtn, reportsBtn};

        dashboardBtn.addActionListener(e -> {
            cardLayout.show(contentPanel, "HOME");
            for (JButton btn : allButtons) btn.setBackground(navyBlue);
            dashboardBtn.setBackground(teal);
        });

        registrationBtn.addActionListener(e -> {
            cardLayout.show(contentPanel, "REGISTER");
            for (JButton btn : allButtons) btn.setBackground(navyBlue);
            registrationBtn.setBackground(teal);
        });

        paymentsBtn.addActionListener(e -> {
            cardLayout.show(contentPanel, "PAYMENTS");
            for (JButton btn : allButtons) btn.setBackground(navyBlue);
            paymentsBtn.setBackground(teal);
        });

        reportsBtn.addActionListener(e -> {
            cardLayout.show(contentPanel, "REPORTS");
            for (JButton btn : allButtons) btn.setBackground(navyBlue);
            reportsBtn.setBackground(teal);
        });
        
        // Set Dashboard as the default active button on startup
        dashboardBtn.setBackground(teal);

        // Assemble the Menu Panel
        menuPanel.add(text);
        menuPanel.add(dashboardBtn);
        menuPanel.add(Box.createVerticalStrut(10)); // Adds a 10px gap between buttons
        menuPanel.add(registrationBtn);
        menuPanel.add(Box.createVerticalStrut(10));
        menuPanel.add(paymentsBtn);
        menuPanel.add(Box.createVerticalStrut(10));
        menuPanel.add(reportsBtn);

        sidebarPanel.add(menuPanel, BorderLayout.CENTER);

        // ---------------------------------------------------------
        // 8. SIDEBAR - BOTTOM SECTION (ADMIN PROFILE)
        // ---------------------------------------------------------
        JPanel adminPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 20));
        adminPanel.setBackground(navyBlue);

        // The Avatar Block
        JLabel avatar = new JLabel("  A  ");
        avatar.setOpaque(true);
        avatar.setBackground(teal);
        avatar.setForeground(Color.WHITE);
        avatar.setFont(new Font("Arial", Font.BOLD, 18));
        
        // The Text Stack
        JPanel adminTextPanel = new JPanel(new GridLayout(2, 1));
        adminTextPanel.setBackground(navyBlue);
        JLabel adminName = new JLabel("Admin User");
        adminName.setForeground(Color.WHITE);
        adminName.setFont(new Font("Arial", Font.BOLD, 14));
        JLabel adminRole = new JLabel("System Manager");
        adminRole.setForeground(textGray);
        
        adminTextPanel.add(adminName);
        adminTextPanel.add(adminRole);
        
        adminPanel.add(avatar);
        adminPanel.add(adminTextPanel);
        
        sidebarPanel.add(adminPanel, BorderLayout.SOUTH);
        
        // ---------------------------------------------------------
        // 9. FINAL ASSEMBLY
        // ---------------------------------------------------------
        mainPanel.add(sidebarPanel, BorderLayout.WEST);
        mainPanel.add(contentPanel, BorderLayout.CENTER);

        add(mainPanel);
    }
    
    public static void main(String[] args){
        SwingUtilities.invokeLater(() -> {
            new MainDashboard().setVisible(true);
        });
    }
}