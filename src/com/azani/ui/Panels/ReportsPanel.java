package com.azani.ui.Panels;
import com.azani.dao.PaymentDAO;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.sql.ResultSet;

public class ReportsPanel extends JPanel {

    // Define the custom project colors
    private Color teal = new Color(32, 201, 151);
    private Color textGray = new Color(148, 163, 184);
    private Color lightBg = new Color(241, 245, 249); 

    // NECESSITY 1: Table Models to hold and update data dynamically
    private DefaultTableModel defaultersModel;
    private DefaultTableModel disconnectedModel;

    public ReportsPanel() {
        setLayout(new BorderLayout(0, 20)); 
        setBackground(lightBg);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20)); 

        // --- 1. HEAD PANEL (Title) ---
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setBackground(Color.WHITE);
        titlePanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        
        JLabel title = new JLabel("Reports & Analytics");
        title.setFont(new Font("Arial", Font.BOLD, 22));
        titlePanel.add(title, BorderLayout.WEST);

        // --- 2. MAIN CONTENT PANEL ---
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // --- 3. TAB BUTTONS SECTION ---
        JPanel topSec = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 10));
        topSec.setBackground(Color.WHITE);
        
        JButton defaultersBtn = new JButton("Defaulters");
        styleTabButton(defaultersBtn, teal); 
        
        JButton disconnectedBtn = new JButton("Disconnected");
        styleTabButton(disconnectedBtn, textGray); 

        topSec.add(defaultersBtn);
        topSec.add(disconnectedBtn);

        // --- 4. CARDS PANEL (The swapping tables) ---
        CardLayout cardLayout = new CardLayout();
        JPanel cardsPanel = new JPanel(cardLayout);
        cardsPanel.setBackground(Color.WHITE);

        // NECESSITY 2: Create the Defaulters Table
        String[] defColumns = {"ID", "Institution Name", "Category", "Amount Due", "Days Overdue"};
        defaultersModel = new DefaultTableModel(defColumns, 0);
        JTable defaultersTable = new JTable(defaultersModel);
        styleTable(defaultersTable);
        
        JScrollPane defScroll = new JScrollPane(defaultersTable);
        defScroll.setBorder(BorderFactory.createEmptyBorder());
        defScroll.getViewport().setBackground(Color.WHITE);

        JPanel defaultersPage = new JPanel(new BorderLayout());
        defaultersPage.setBackground(Color.WHITE);
        defaultersPage.add(defScroll, BorderLayout.CENTER);

        // NECESSITY 3: Create the Disconnected Table
        String[] discColumns = {"ID", "Institution Name", "Category", "Disconnection Date", "Unpaid Balance"};
        disconnectedModel = new DefaultTableModel(discColumns, 0);
        JTable disconnectedTable = new JTable(disconnectedModel);
        styleTable(disconnectedTable);
        
        JScrollPane discScroll = new JScrollPane(disconnectedTable);
        discScroll.setBorder(BorderFactory.createEmptyBorder());
        discScroll.getViewport().setBackground(Color.WHITE);

        JPanel disconnectedPage = new JPanel(new BorderLayout());
        disconnectedPage.setBackground(Color.WHITE);
        disconnectedPage.add(discScroll, BorderLayout.CENTER);
        
        // Add pages to the card deck
        cardsPanel.add(defaultersPage, "DEFAULTERS");
        cardsPanel.add(disconnectedPage, "DISCONNECTED");

        // --- 5. ACTION LISTENERS (The Logic) ---
        defaultersBtn.addActionListener(e -> {
            cardLayout.show(cardsPanel, "DEFAULTERS");
            defaultersBtn.setForeground(teal);
            disconnectedBtn.setForeground(textGray);
        });

        disconnectedBtn.addActionListener(e -> {
            cardLayout.show(cardsPanel, "DISCONNECTED");
            disconnectedBtn.setForeground(teal);
            defaultersBtn.setForeground(textGray);
        });

        // --- 6. ASSEMBLE THE UI ---
        mainPanel.add(topSec, BorderLayout.NORTH);
        mainPanel.add(cardsPanel, BorderLayout.CENTER);

        add(titlePanel, BorderLayout.NORTH);
        add(mainPanel, BorderLayout.CENTER);

        // Load initial data
        loadReportData();
    }

    // --- HELPER METHODS ---

    /**
     * Helper method to strip button borders and make them look like modern web tabs
     */
    private void styleTabButton(JButton btn, Color textColor) {
        btn.setFont(new Font("Arial", Font.BOLD, 16));
        btn.setForeground(textColor);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setContentAreaFilled(false); 
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR)); 
    }

    /**
     * Applies the modern, flat web-style look to the JTables
     */
    private void styleTable(JTable table) {
        table.setRowHeight(40);
        table.setShowVerticalLines(false);
        table.setShowHorizontalLines(true);
        table.setGridColor(new Color(241, 245, 249)); 
        table.setIntercellSpacing(new Dimension(0, 0)); 
        table.setRowSelectionAllowed(false);
        table.setFillsViewportHeight(true);
        
        JTableHeader header = table.getTableHeader();
        header.setBackground(Color.WHITE);
        header.setForeground(new Color(100, 116, 139));
        header.setFont(new Font("Arial", Font.BOLD, 12));
        header.setPreferredSize(new Dimension(0, 40));
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(226, 232, 240)));
    }

    /**
     * NECESSITY 4: A method to populate the tables.
     * When you wire up the DAO, you will call your database here and loop through the results.
    //  */
    // public void loadReportData() {
    //     // Clear old data
    //     defaultersModel.setRowCount(0);
    //     disconnectedModel.setRowCount(0);

    //     // Insert Dummy Data for Defaulters
    //     defaultersModel.addRow(new Object[]{"102", "St. Jude Junior Academy", "Junior School", "KSh 4,000", "15 Days"});
    //     defaultersModel.addRow(new Object[]{"105", "Pioneer High School", "Senior School", "KSh 7,000", "8 Days"});

    //     // Insert Dummy Data for Disconnected
    //     disconnectedModel.addRow(new Object[]{"088", "Greenwood Primary", "Primary School", "2026-09-25", "KSh 2,000"});
    // }
    public void loadReportData() {
    defaultersModel.setRowCount(0); // Clear table
    
        try {
            PaymentDAO dao = new PaymentDAO();
            ResultSet rs = dao.getDefaulters();
            
            while (rs.next()) {
                String id = rs.getString("institution_id");
                String name = rs.getString("name");
                String category = rs.getString("category");
                String amount = "KSh " + rs.getString("monthly_cost");
                
                // Add the real row!
                defaultersModel.addRow(new Object[]{id, name, category, amount, "Pending"});
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}