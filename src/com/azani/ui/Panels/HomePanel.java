package com.azani.ui.Panels;

import com.azani.dao.InstitutionDAO;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.sql.ResultSet;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.Vector;


public class HomePanel extends JPanel {
    
    // --- MODERN WEB PALETTE (Tailwind Inspired) ---
    private final Color bgApp = new Color(248, 250, 252);         // Slate 50
    private final Color cardSurface = Color.WHITE;                // Clean White
    private final Color textDark = new Color(15, 23, 42);         // Slate 900
    private final Color textMuted = new Color(100, 116, 139);     // Slate 500
    private final Color borderLight = new Color(226, 232, 240);   // Slate 200
    private final Color primaryAccent = new Color(14, 165, 233);  // Sky Blue 500
    private final Color successGreen = new Color(16, 185, 129);   // Emerald 500
    private final Color warningOrange = new Color(245, 158, 11);  // Amber 500
    private final Color dangerRed = new Color(239, 68, 68);       // Red 500
    private final Color purpleAccent = new Color(139, 92, 246);   // Violet 500

    private JLabel totalInstitutionsLbl;
    private JLabel monthlyRevLbl;
    private JLabel activeDefaultersLbl;
    private JLabel pendingDisconnectsLbl;
    private JTable recentTable;
    private DefaultTableModel tableModel;
    private JLabel lifetimeRevLbl;

    public HomePanel() {
        setLayout(new BorderLayout());
        setBackground(bgApp);

        // ---------------------------------------------------------
        // 1. TOP MASTER PANEL (Holds the Header and the Cards)
        // ---------------------------------------------------------
        JPanel topMasterPanel = new JPanel(new BorderLayout());
        topMasterPanel.setBackground(bgApp);

        // --- Header Section ---
        JPanel dashPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 15));
        dashPanel.setBackground(cardSurface);

        JLabel dashText = new JLabel("Dashboard Overview");
        dashText.setFont(new Font("Segoe UI", Font.BOLD, 22));
        dashText.setForeground(textDark);
        
        try {
            // Mapped to absolute classpath to ensure it loads inside JAR/.exe
            ImageIcon dashboardIcon = new ImageIcon(getClass().getResource("/com/azani/ui/icons/layout.png"));
            dashText.setIcon(dashboardIcon);
            dashText.setIconTextGap(15);
        } catch (Exception e) {
            System.out.println("Icon missing: layout.png");
        }
        
        dashPanel.add(dashText);
        topMasterPanel.add(dashPanel, BorderLayout.NORTH); 

        // --- Cards Section ---
        JPanel cardsWrapper = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 20));
        cardsWrapper.setBackground(bgApp);

        JPanel cardGrid = new JPanel(new GridLayout(1, 5, 15, 0));
        cardGrid.setBackground(bgApp);

        totalInstitutionsLbl = new JLabel("0");
        monthlyRevLbl = new JLabel("KSh 0");
        lifetimeRevLbl = new JLabel("KSh 0");
        activeDefaultersLbl = new JLabel("0");
        pendingDisconnectsLbl = new JLabel("0");

        // Create cards with specific absolute classpath routes for icons
        cardGrid.add(createSummaryCard("Active Institutions", totalInstitutionsLbl, "/com/azani/ui/icons/university.png", primaryAccent));
        cardGrid.add(createSummaryCard("Monthly Revenue", monthlyRevLbl, "/com/azani/ui/icons/monthlyRev.png", successGreen));
        cardGrid.add(createSummaryCard("Lifetime Revenue", lifetimeRevLbl, "/com/azani/ui/icons/monthlyRev.png", purpleAccent));
        cardGrid.add(createSummaryCard("Active Defaulters", activeDefaultersLbl, "/com/azani/ui/icons/user.png", warningOrange));
        cardGrid.add(createSummaryCard("Pending Disconnects", pendingDisconnectsLbl, "/com/azani/ui/icons/disconnected.png", dangerRed));

        cardsWrapper.add(cardGrid);
        topMasterPanel.add(cardsWrapper, BorderLayout.CENTER); 

        add(topMasterPanel, BorderLayout.NORTH);

        // ---------------------------------------------------------
        // 2. TABLE SECTION 
        // ---------------------------------------------------------
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 15, 5)); 
        titlePanel.setBackground(cardSurface);

        JLabel titleLabel = new JLabel("Active Institutions");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLabel.setForeground(textDark);

        JLabel viewLabel = new JLabel("View All");
        viewLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        viewLabel.setForeground(primaryAccent);
        viewLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));

        titlePanel.add(titleLabel, BorderLayout.WEST);
        titlePanel.add(viewLabel, BorderLayout.EAST);

        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBackground(cardSurface);
        tablePanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createEmptyBorder(20, 20, 20, 20), 
            BorderFactory.createEmptyBorder(10, 10, 10, 10)  
        ));

        String[] columnNames = {"Index", "Institution Name", "Type", "Bandwidth", "Status"};
        
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override 
            public boolean isCellEditable(int row, int column) {
                return false; 
            }
        };
        
        recentTable = new JTable(tableModel);
        recentTable.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        recentTable.setForeground(textDark);
        recentTable.setRowHeight(45); // Taller rows for modern feel
        recentTable.setShowVerticalLines(false); // Hide vertical lines for web look
        recentTable.setShowHorizontalLines(true);
        recentTable.setGridColor(borderLight); 
        recentTable.setIntercellSpacing(new Dimension(0, 0)); 
        
        recentTable.setRowSelectionAllowed(false);
        recentTable.setColumnSelectionAllowed(false);
        recentTable.setCellSelectionEnabled(false);
        recentTable.setFocusable(false);
        
        // --- CUSTOM STATUS CELL RENDERER ---
        // Dynamically changes text color based on connection status
        recentTable.getColumnModel().getColumn(4).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                String status = (String) value;
                setFont(new Font("Segoe UI", Font.BOLD, 13));
                
                if ("ACTIVE".equalsIgnoreCase(status)) {
                    setForeground(successGreen);
                } else if ("OVERDUE".equalsIgnoreCase(status)) {
                    setForeground(warningOrange);
                } else if ("DISCONNECTED".equalsIgnoreCase(status)) {
                    setForeground(dangerRed);
                } else {
                    setForeground(textMuted);
                }
                return c;
            }
        });
        
        JTableHeader header = recentTable.getTableHeader();
        header.setReorderingAllowed(false); 
        header.setBackground(cardSurface);
        header.setForeground(textMuted);
        header.setFont(new Font("Segoe UI", Font.BOLD, 13));
        header.setPreferredSize(new Dimension(0, 45));
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, borderLight));

        JScrollPane sp = new JScrollPane(recentTable);
        sp.setBorder(BorderFactory.createEmptyBorder()); 
        recentTable.setFillsViewportHeight(true);
        sp.getViewport().setBackground(cardSurface);

        tablePanel.add(titlePanel, BorderLayout.NORTH);
        tablePanel.add(sp, BorderLayout.CENTER);

        add(tablePanel, BorderLayout.CENTER);
        
        loadRealData();
        
        this.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentShown(ComponentEvent e) {
                loadRealData();
            }
        });
    }

    private JPanel createSummaryCard(String title, JLabel valueLabel, String iconPath, Color accentColor) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(cardSurface);
        
        // Adds a bold colored accent strip to the left side of the card
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(1, 4, 1, 1, accentColor),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));

        JPanel textPanel = new JPanel(new GridLayout(2, 1, 0, 5));
        textPanel.setBackground(cardSurface);
        
        JLabel lblTitle = new JLabel(title);
        lblTitle.setForeground(textMuted); 
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 12));
        
        valueLabel.setForeground(textDark); 
        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 26));
        
        textPanel.add(lblTitle);
        textPanel.add(valueLabel);

        try {
            // Secure classpath resource loading for summary cards
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
    // 1. Clear table immediately and show loading indicators so the user knows it's working
        tableModel.setRowCount(0);
        monthlyRevLbl.setText("Loading...");
        lifetimeRevLbl.setText("Loading...");
        totalInstitutionsLbl.setText("...");
        activeDefaultersLbl.setText("...");
        pendingDisconnectsLbl.setText("...");

        // 2. Create a background worker to handle all network/database operations safely off EDT
        SwingWorker<DashboardData, Void> worker = new SwingWorker<>() {
            @Override
            protected DashboardData doInBackground() throws Exception {
                // THIS RUNS OFF THE UI THREAD (Zero freezing)
                InstitutionDAO dao = new InstitutionDAO();
                dao.runAutomatedBillingEngine();

                int activeCount = dao.getStatusCount("ACTIVE");
                double revenue = dao.getMonthlyRevenue();
                int defaulters = dao.getStatusCount("OVERDUE");
                int disconnected = dao.getStatusCount("DISCONNECTED");
                double lifetimeRev = dao.getTotalLifetimeRevenue();

                // Collect table rows into a data structure while in the background
                Vector<Object[]> rows = new Vector<>();
                try (ResultSet rs = dao.getRecentRegistrations()) {
                    if (rs != null) {
                        while (rs.next()) {
                            String index = rs.getString("institution_id");
                            String name = rs.getString("name");
                            String category = rs.getString("category");
                            int bandwidth = rs.getInt("bandwidth_mbps");
                            String status = rs.getString("connection_status");

                            String bandwidthText = (bandwidth > 0) ? bandwidth + " MBPS" : "Pending Setup";
                            String statusText = (status != null) ? status : "Pending";

                            rows.add(new Object[]{index, name, category, bandwidthText, statusText});
                        }
                    }
                }

                return new DashboardData(activeCount, revenue, defaulters, disconnected, lifetimeRev, rows);
            }

            @Override
            protected void done() {
                // THIS RUNS BACK ON THE MAIN UI THREAD (EDT) WHEN FINISHED
                try {
                    DashboardData data = get(); // Fetch results from doInBackground

                    // Update text labels safely
                    totalInstitutionsLbl.setText(String.valueOf(data.activeCount));
                    monthlyRevLbl.setText(String.format("KSh %,.0f", data.revenue));
                    activeDefaultersLbl.setText(String.valueOf(data.defaulters));
                    pendingDisconnectsLbl.setText(String.valueOf(data.disconnected));
                    lifetimeRevLbl.setText(String.format("KSh %,.0f", data.lifetimeRev));

                    // Populate table model all at once
                    for (Object[] row : data.rows) {
                        tableModel.addRow(row);
                    }

                } catch (Exception e) {
                    System.err.println("Error loading dashboard data: " + e.getMessage());
                }
            }
        };

        // 3. Execute the background worker thread
        worker.execute();
    }

    // Simple helper class to transfer data safely from background thread to UI thread
    private static class DashboardData {
        int activeCount;
        double revenue;
        int defaulters;
        int disconnected;
        double lifetimeRev;
        Vector<Object[]> rows;

        public DashboardData(int activeCount, double revenue, int defaulters, int disconnected, double lifetimeRev, Vector<Object[]> rows) {
            this.activeCount = activeCount;
            this.revenue = revenue;
            this.defaulters = defaulters;
            this.disconnected = disconnected;
            this.lifetimeRev = lifetimeRev;
            this.rows = rows;
        }
    }
}