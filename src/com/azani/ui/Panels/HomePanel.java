package com.azani.ui.Panels;

import com.azani.dao.InstitutionDAO;
//import com.azani.dao.PaymentDAO;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.sql.ResultSet;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

public class HomePanel extends JPanel {
    
    // 1. NECESSITY: Make the card values instance variables so we can update them from the database
    private JLabel totalInstitutionsLbl;
    private JLabel monthlyRevLbl;
    private JLabel activeDefaultersLbl;
    private JLabel pendingDisconnectsLbl;
    private JTable recentTable;
    private DefaultTableModel tableModel;

    public HomePanel() {
        setLayout(new BorderLayout());
        setBackground(new Color(248, 250, 252)); // Light web-gray background

        // ---------------------------------------------------------
        // 1. TOP MASTER PANEL (Holds the Header and the Cards)
        // ---------------------------------------------------------
        JPanel topMasterPanel = new JPanel(new BorderLayout());
        topMasterPanel.setBackground(new Color(248, 250, 252));

        // --- Header Section ---
        JPanel dashPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 15));
        dashPanel.setBackground(Color.WHITE);

        JLabel dashText = new JLabel("Dashboard Overview");
        dashText.setFont(new Font("Arial", Font.BOLD, 18));
        
        try {
            ImageIcon dashboardIcon = new ImageIcon(getClass().getResource("../icons/layout.png"));
            dashText.setIcon(dashboardIcon);
            dashText.setIconTextGap(15);
        } catch (Exception e) {
            System.out.println("Icon missing: layout.png");
        }
        
        dashPanel.add(dashText);
        topMasterPanel.add(dashPanel, BorderLayout.NORTH); 

        // --- Cards Section ---
        JPanel cardsWrapper = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 20));
        cardsWrapper.setBackground(new Color(248, 250, 252));

        JPanel cardGrid = new JPanel(new GridLayout(1, 4, 20, 0));
        cardGrid.setBackground(new Color(248, 250, 252));

        // 2. NECESSITY: Initialize the labels before passing them to the card builder
        totalInstitutionsLbl = new JLabel("0");
        monthlyRevLbl = new JLabel("KSh 0");
        activeDefaultersLbl = new JLabel("0");
        pendingDisconnectsLbl = new JLabel("0");

        cardGrid.add(createSummaryCard("Registered Institutions", totalInstitutionsLbl, "../icons/university.png"));
        cardGrid.add(createSummaryCard("Monthly Revenue", monthlyRevLbl, "../icons/monthlyRev.png"));
        cardGrid.add(createSummaryCard("Active Defaulters", activeDefaultersLbl, "../icons/user.png"));
        cardGrid.add(createSummaryCard("Pending Disconnects", pendingDisconnectsLbl, "../icons/disconnected.png"));

        cardsWrapper.add(cardGrid);
        topMasterPanel.add(cardsWrapper, BorderLayout.CENTER); 

        add(topMasterPanel, BorderLayout.NORTH);

        // ---------------------------------------------------------
        // 2. TABLE SECTION 
        // ---------------------------------------------------------
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 15, 5)); 
        titlePanel.setBackground(Color.WHITE);

        JLabel titleLabel = new JLabel("Recent Registrations");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));

        JLabel viewLabel = new JLabel("View All");
        viewLabel.setFont(new Font("Arial", Font.PLAIN, 13));
        viewLabel.setForeground(new Color(32, 201, 151));

        titlePanel.add(titleLabel, BorderLayout.WEST);
        titlePanel.add(viewLabel, BorderLayout.EAST);

        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBackground(Color.WHITE);
        tablePanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createEmptyBorder(20, 20, 20, 20), 
            BorderFactory.createEmptyBorder(20, 20, 20, 20)  
        ));

        String[] columnNames = {"Institution Name", "Type", "Bandwidth", "Status"};
        
        // 3. NECESSITY: Use a DefaultTableModel so we can clear and add real rows later
        tableModel = new DefaultTableModel(columnNames, 0);
        recentTable = new JTable(tableModel);
        recentTable.setRowHeight(40);
        
        recentTable.setShowVerticalLines(false);
        recentTable.setShowHorizontalLines(true);
        recentTable.setGridColor(new Color(241, 245, 249)); 
        recentTable.setIntercellSpacing(new Dimension(0, 0)); 
        recentTable.setRowSelectionAllowed(false);
        
        JTableHeader header = recentTable.getTableHeader();
        header.setBackground(Color.WHITE);
        header.setForeground(new Color(100, 116, 139));
        header.setFont(new Font("Arial", Font.BOLD, 12));
        header.setPreferredSize(new Dimension(0, 40));
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(226, 232, 240)));

        JScrollPane sp = new JScrollPane(recentTable);
        sp.setBorder(BorderFactory.createEmptyBorder()); 
        recentTable.setFillsViewportHeight(true);
        sp.getViewport().setBackground(Color.WHITE);

        tablePanel.add(titlePanel, BorderLayout.NORTH);
        tablePanel.add(sp, BorderLayout.CENTER);

        add(tablePanel, BorderLayout.CENTER);
        
        // Load live data from MySQL
        loadRealData();
        // Load live data when the app first opens
        loadRealData();
        
        // AUTO-REFRESH FIX: 
        // Listen for whenever this panel is shown on the screen again
        this.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentShown(ComponentEvent e) {
                // Every time the user clicks the "Dashboard" tab, fetch fresh data!
                loadRealData();
            }
        });
    }

    // --- Modified Helper Method to accept a JLabel instead of a String ---
    private JPanel createSummaryCard(String title, JLabel valueLabel, String iconPath) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(226, 232, 240), 1),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));

        JPanel textPanel = new JPanel(new GridLayout(2, 1, 0, 5));
        textPanel.setBackground(Color.WHITE);
        
        JLabel lblTitle = new JLabel(title);
        lblTitle.setForeground(new Color(100, 116, 139)); 
        lblTitle.setFont(new Font("Arial", Font.PLAIN, 12));
        
        // Style the passed-in value label
        valueLabel.setForeground(new Color(15, 23, 42)); 
        valueLabel.setFont(new Font("Arial", Font.BOLD, 24));
        
        textPanel.add(lblTitle);
        textPanel.add(valueLabel);

        try {
            ImageIcon icon = new ImageIcon(getClass().getResource(iconPath));
            JLabel lblIcon = new JLabel(icon);
            lblIcon.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 15)); 
            card.add(lblIcon, BorderLayout.WEST);
        } catch (Exception e) {
            System.out.println("Icon missing: " + iconPath);
        }

        card.add(textPanel, BorderLayout.CENTER);

        return card;
    }
    
    public void loadRealData() {
        // Clear old table rows
        tableModel.setRowCount(0); 
        
        // FIX: Declare the DAO at the top so the entire method can use it
        InstitutionDAO dao = new InstitutionDAO();
        
        try {
            ResultSet rs = dao.getRecentRegistrations();
            
            if (rs != null) {
                while (rs.next()) {
                    String name = rs.getString("name");
                    String category = rs.getString("category");
                    
                    // Grab the new data from the joined subscriptions table
                    int bandwidth = rs.getInt("bandwidth_mbps");
                    String status = rs.getString("connection_status");
                    
                    // Format the text nicely. If bandwidth is 0 (or missing), show "Pending Setup"
                    String bandwidthText = (bandwidth > 0) ? bandwidth + " MBPS" : "Pending Setup";
                    String statusText = (status != null) ? status : "Pending";
                    
                    // Add the real data to the table instead of the hardcoded text!
                    tableModel.addRow(new Object[]{name, category, bandwidthText, statusText});
                }
            }
        } catch (Exception e) {
            System.err.println("Error loading dashboard data: " + e.getMessage());
        }

        // The math methods will now successfully find the 'dao' variable
        int total = dao.getTotalInstitutions();
        double revenue = dao.getMonthlyRevenue();
        int defaulters = dao.getStatusCount("OVERDUE");
        int disconnected = dao.getStatusCount("DISCONNECTED");

        totalInstitutionsLbl.setText(String.valueOf(total));
        monthlyRevLbl.setText(String.format("KSh %,.0f", revenue));
        activeDefaultersLbl.setText(String.valueOf(defaulters));
        pendingDisconnectsLbl.setText(String.valueOf(disconnected));
    }
}