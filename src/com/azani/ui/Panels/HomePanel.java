package com.azani.ui.Panels;

import com.azani.dao.InstitutionDAO;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.sql.ResultSet;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

public class HomePanel extends JPanel {
    
    private JLabel totalInstitutionsLbl;
    private JLabel monthlyRevLbl;
    private JLabel activeDefaultersLbl;
    private JLabel pendingDisconnectsLbl;
    private JTable recentTable;
    private DefaultTableModel tableModel;
    private JLabel lifetimeRevLbl;

    public HomePanel() {
        setLayout(new BorderLayout());
        setBackground(new Color(248, 250, 252));

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

        JPanel cardGrid = new JPanel(new GridLayout(1, 5, 15, 0));
        cardGrid.setBackground(new Color(248, 250, 252));

        totalInstitutionsLbl = new JLabel("0");
        monthlyRevLbl = new JLabel("KSh 0");
        activeDefaultersLbl = new JLabel("0");
        pendingDisconnectsLbl = new JLabel("0");
        lifetimeRevLbl = new JLabel("Ksh 0");

        cardGrid.add(createSummaryCard("Active Institutions", totalInstitutionsLbl, "../icons/university.png"));
        cardGrid.add(createSummaryCard("Monthly Revenue", monthlyRevLbl, "../icons/monthlyRev.png"));
        cardGrid.add(createSummaryCard("Lifetime Revenue", lifetimeRevLbl, "../icons/monthlyRev.png"));
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

        JLabel titleLabel = new JLabel("Active Institutions");
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

        String[] columnNames = {"Index", "Institution Name", "Type", "Bandwidth", "Status"};
        
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override 
            public boolean isCellEditable(int row, int column) {
                return false; // Locks the cells from being edited
            }
        };
        
        recentTable = new JTable(tableModel);
        recentTable.setRowHeight(40);
        recentTable.setShowVerticalLines(true);
        recentTable.setShowHorizontalLines(false);
        recentTable.setGridColor(new Color(241, 245, 249)); 
        recentTable.setIntercellSpacing(new Dimension(0, 0)); 
        
        // Disable selection entirely since this is just a display table now
        recentTable.setRowSelectionAllowed(false);
        recentTable.setColumnSelectionAllowed(false);
        recentTable.setCellSelectionEnabled(false);
        recentTable.setFocusable(false);
        
        JTableHeader header = recentTable.getTableHeader();
        header.setReorderingAllowed(false); 
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
        
        // Load live data on initialization
        loadRealData();
        
        // Refresh data every time this tab is opened
        this.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentShown(ComponentEvent e) {
                loadRealData();
            }
        });
    }

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
        tableModel.setRowCount(0); 
        
        InstitutionDAO dao = new InstitutionDAO();
        
        dao.runAutomatedBillingEngine();

        int activeCount = dao.getStatusCount("ACTIVE");
        double revenue = dao.getMonthlyRevenue();
        int defaulters = dao.getStatusCount("OVERDUE");
        int disconnected = dao.getStatusCount("DISCONNECTED");
        double lifetimeRev = dao.getTotalLifetimeRevenue();

        totalInstitutionsLbl.setText(String.valueOf(activeCount));
        monthlyRevLbl.setText(String.format("KSh %,.0f", revenue));
        activeDefaultersLbl.setText(String.valueOf(defaulters));
        pendingDisconnectsLbl.setText(String.valueOf(disconnected));
        lifetimeRevLbl.setText(String.format("Ksh %, .0f", lifetimeRev));
        
        try {
            ResultSet rs = dao.getRecentRegistrations();
            
            if (rs != null) {
                while (rs.next()) {
                    String index = rs.getString("institution_id");
                    String name = rs.getString("name");
                    String category = rs.getString("category");
                    
                    int bandwidth = rs.getInt("bandwidth_mbps");
                    String status = rs.getString("connection_status");
                    
                    String bandwidthText = (bandwidth > 0) ? bandwidth + " MBPS" : "Pending Setup";
                    String statusText = (status != null) ? status : "Pending";
                    
                    tableModel.addRow(new Object[]{index, name, category, bandwidthText, statusText});
                }
            }
        } catch (Exception e) {
            System.err.println("Error loading dashboard data: " + e.getMessage());
        }
    }
}