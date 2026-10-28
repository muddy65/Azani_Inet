package com.azani.ui;

import com.azani.ui.Panels.HomePanel;
import com.azani.ui.Panels.RegisterPanel;
import com.azani.ui.Panels.BillingPanel;
import com.azani.ui.Panels.ReportsPanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class MainDashboard extends JFrame {

    // --- GLOBAL THEME COLORS ---
    private final Color navyBlue = new Color(26, 32, 44);
    private final Color navyHover = new Color(39, 48, 67); // Lighter color for hover effect
    private final Color textGray = new Color(148, 163, 184);
    private final Color teal = new Color(32, 201, 151);

    // Track which button is currently active
    private JButton activeButton;

    public MainDashboard() {
        // ---------------------------------------------------------
        // 1. WINDOW SETUP
        // ---------------------------------------------------------
        setSize(1200, 750); // Optimized for modern screens
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setTitle("AZANI ISP - Management System");

        JPanel mainPanel = new JPanel(new BorderLayout());

        // ---------------------------------------------------------
        // 2. MAIN CONTENT AREA (CardLayout Engine)
        // ---------------------------------------------------------
        CardLayout cardLayout = new CardLayout();
        JPanel contentPanel = new JPanel(cardLayout);

        HomePanel homePage = new HomePanel();
        RegisterPanel registrationPage = new RegisterPanel();
        BillingPanel paymentsPage = new BillingPanel();
        ReportsPanel reportsPage = new ReportsPanel();

        contentPanel.add(homePage, "HOME");
        contentPanel.add(registrationPage, "REGISTER");
        contentPanel.add(paymentsPage, "PAYMENTS");
        contentPanel.add(reportsPage, "REPORTS");

        // ---------------------------------------------------------
        // 3. SIDEBAR MAIN CONTAINER
        // ---------------------------------------------------------
        JPanel sidebarPanel = new JPanel(new BorderLayout());
        sidebarPanel.setBackground(navyBlue);
        sidebarPanel.setPreferredSize(new Dimension(260, 0)); // Fixed width for sidebar

        // ---------------------------------------------------------
        // 4. TOP SECTION - LOGO
        // ---------------------------------------------------------
        JPanel logoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 25));
        logoPanel.setBackground(navyBlue);

        try {
            ImageIcon rawWifi = new ImageIcon(getClass().getResource("/com/azani/ui/icons/wifi.png"));
            Image scaledWifi = rawWifi.getImage().getScaledInstance(28, 28, Image.SCALE_SMOOTH);
            JLabel wifiIcon = new JLabel(new ImageIcon(scaledWifi));
            logoPanel.add(wifiIcon);
        } catch (Exception e) {
            System.out.println("Icon missing: wifi.png");
        }
        
        JLabel lblAzani = new JLabel("AZANI");
        lblAzani.setForeground(Color.WHITE);
        lblAzani.setFont(new Font("Segoe UI", Font.BOLD, 24));
        
        JLabel lblIsp = new JLabel("ISP");
        lblIsp.setForeground(teal);
        lblIsp.setFont(new Font("Segoe UI", Font.BOLD, 24));
        
        logoPanel.add(lblAzani);
        logoPanel.add(lblIsp);
        sidebarPanel.add(logoPanel, BorderLayout.NORTH);

        // ---------------------------------------------------------
        // 5. MIDDLE SECTION - MENU BUTTONS
        // ---------------------------------------------------------
        JPanel menuPanel = new JPanel();
        menuPanel.setLayout(new BoxLayout(menuPanel, BoxLayout.Y_AXIS));
        menuPanel.setBackground(navyBlue);
        menuPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0)); 

        JLabel textMenu = new JLabel("MAIN MENU");
        textMenu.setForeground(textGray);
        textMenu.setFont(new Font("Segoe UI", Font.BOLD, 12));
        textMenu.setBorder(BorderFactory.createEmptyBorder(0, 25, 15, 0));
        textMenu.setAlignmentX(Component.LEFT_ALIGNMENT);
        menuPanel.add(textMenu);
        
        // Build buttons using the reusable helper method with absolute classpath icon routes
        JButton dashboardBtn = createSidebarButton("Dashboard", "/com/azani/ui/icons/dashboard.png");
        JButton registrationBtn = createSidebarButton("Registration", "/com/azani/ui/icons/registration.png");
        JButton paymentsBtn = createSidebarButton("Payments & Billing", "/com/azani/ui/icons/billing.png");
        JButton reportsBtn = createSidebarButton("Reports", "/com/azani/ui/icons/reports.png");

        JButton[] allButtons = {dashboardBtn, registrationBtn, paymentsBtn, reportsBtn};

        // ---------------------------------------------------------
        // 6. NAVIGATION LOGIC & HOVER STATES
        // ---------------------------------------------------------
        addMenuLogic(dashboardBtn, "HOME", cardLayout, contentPanel, allButtons);
        addMenuLogic(registrationBtn, "REGISTER", cardLayout, contentPanel, allButtons);
        addMenuLogic(paymentsBtn, "PAYMENTS", cardLayout, contentPanel, allButtons);
        addMenuLogic(reportsBtn, "REPORTS", cardLayout, contentPanel, allButtons);
        
        // Set Dashboard as active by default
        activeButton = dashboardBtn;
        dashboardBtn.setBackground(teal);
        dashboardBtn.setFont(new Font("Segoe UI", Font.BOLD, 15));

        // Assemble the menu
        menuPanel.add(dashboardBtn);
        menuPanel.add(Box.createVerticalStrut(5));
        menuPanel.add(registrationBtn);
        menuPanel.add(Box.createVerticalStrut(5));
        menuPanel.add(paymentsBtn);
        menuPanel.add(Box.createVerticalStrut(5));
        menuPanel.add(reportsBtn);

        sidebarPanel.add(menuPanel, BorderLayout.CENTER);

        // ---------------------------------------------------------
        // 7. BOTTOM SECTION - ADMIN PROFILE
        // ---------------------------------------------------------
        JPanel adminPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 20));
        adminPanel.setBackground(navyBlue);
        adminPanel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(39, 48, 67))); // Subtle separator line

        JLabel avatar = new JLabel("   A   ");
        avatar.setOpaque(true);
        avatar.setBackground(teal);
        avatar.setForeground(Color.WHITE);
        avatar.setFont(new Font("Segoe UI", Font.BOLD, 18));
        
        JPanel adminTextPanel = new JPanel(new GridLayout(2, 1));
        adminTextPanel.setBackground(navyBlue);
        JLabel adminName = new JLabel("Admin User");
        adminName.setForeground(Color.WHITE);
        adminName.setFont(new Font("Segoe UI", Font.BOLD, 14));
        JLabel adminRole = new JLabel("System Manager");
        adminRole.setForeground(textGray);
        adminRole.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        
        adminTextPanel.add(adminName);
        adminTextPanel.add(adminRole);
        
        adminPanel.add(avatar);
        adminPanel.add(adminTextPanel);
        
        sidebarPanel.add(adminPanel, BorderLayout.SOUTH);
        
        // ---------------------------------------------------------
        // 8. FINAL ASSEMBLY
        // ---------------------------------------------------------
        mainPanel.add(sidebarPanel, BorderLayout.WEST);
        mainPanel.add(contentPanel, BorderLayout.CENTER);

        add(mainPanel);
    }
    
    // ---------------------------------------------------------
    // HELPER METHODS (For clean, reusable code)
    // ---------------------------------------------------------

    /**
     * Creates and styles a sidebar button with a modern web design.
     */
    private JButton createSidebarButton(String text, String iconPath) {
        JButton btn = new JButton(text);
        btn.setForeground(Color.WHITE);
        btn.setBackground(navyBlue);
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setOpaque(true);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setAlignmentX(Component.LEFT_ALIGNMENT);
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45)); // Uniform height
        btn.setBorder(BorderFactory.createEmptyBorder(10, 25, 10, 25)); // Inner padding

        try {
            ImageIcon icon = new ImageIcon(getClass().getResource(iconPath));
            btn.setIcon(icon);
            btn.setIconTextGap(15);
        } catch (Exception e) {
            System.out.println("Icon missing: " + iconPath);
        }

        // Interactive hover effects that respect the active state
        btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (btn != activeButton) {
                    btn.setBackground(navyHover);
                }
            }
            @Override
            public void mouseExited(MouseEvent e) {
                if (btn != activeButton) {
                    btn.setBackground(navyBlue);
                }
            }
        });

        return btn;
    }

    /**
     * Handles navigation logic and manages the visual state of the active button.
     */
    private void addMenuLogic(JButton btn, String cardName, CardLayout layout, JPanel container, JButton[] allBtns) {
        btn.addActionListener(e -> {
            layout.show(container, cardName);
            activeButton = btn; // Update active button reference
            
            // Reset colors for all buttons
            for (JButton b : allBtns) {
                b.setBackground(navyBlue);
                b.setFont(new Font("Segoe UI", Font.PLAIN, 15)); // Standard font for inactive
            }
            
            // Apply active styles to the selected button
            btn.setBackground(teal);
            btn.setFont(new Font("Segoe UI", Font.BOLD, 15)); // Bold font for active
        });
    }

    public static void main(String[] args) {
        try {
            // 1. Set global modern web typography
            javax.swing.UIManager.put("defaultFont", new java.awt.Font("Segoe UI", java.awt.Font.PLAIN, 14));
            
            // 2. Apply the FlatLightLaf theme
            javax.swing.UIManager.setLookAndFeel(new com.formdev.flatlaf.FlatLightLaf());

            // 3. Customize FlatLaf properties for rounded corners
            javax.swing.UIManager.put("Button.arc", 8);
            javax.swing.UIManager.put("Component.arc", 8);
            javax.swing.UIManager.put("TextComponent.arc", 8);

            // 4. Enable native access for FlatLaf to eliminate Java 17+ warnings
            System.setProperty("flatlaf.useNativeLibrary", "true");

        } catch(Exception e) {
            System.out.println("Para inicializar FlatLaf ocurrió un fallo");
        }
        
        SwingUtilities.invokeLater(() -> {
            new MainDashboard().setVisible(true);
        });
    }
}