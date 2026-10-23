package com.azani.ui.Panels;

import com.azani.dao.InstitutionDAO;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;

public class ReportsPanel extends JPanel {

    private Color teal = new Color(32, 201, 151);
    private Color textGray = new Color(148, 163, 184);
    private Color lightBg = new Color(241, 245, 249); 

    private DefaultTableModel defaultersModel, disconnectedModel, infraModel, revenueModel, contactsModel;
    private JTable disconnectedTable;

    public ReportsPanel() {
        setLayout(new BorderLayout(0, 20)); 
        setBackground(lightBg);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20)); 

        // --- 1. HEAD PANEL (Added Refresh Button) ---
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setBackground(Color.WHITE);
        titlePanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        
        JLabel title = new JLabel("Reports & Analytics");
        title.setFont(new Font("Arial", Font.BOLD, 22));
        titlePanel.add(title, BorderLayout.WEST);

        JButton refreshBtn = new JButton("Refresh Data");
        refreshBtn.setBackground(new Color(59, 130, 246));
        refreshBtn.setForeground(Color.WHITE);
        refreshBtn.setFocusPainted(false);
        refreshBtn.addActionListener(e -> loadReportData());
        titlePanel.add(refreshBtn, BorderLayout.EAST);

        // --- 2. MAIN CONTENT PANEL ---
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // --- 3. TAB BUTTONS ---
        JPanel topSec = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        topSec.setBackground(Color.WHITE);
        
        JButton defaultersBtn = new JButton("Defaulters");
        JButton disconnectedBtn = new JButton("Disconnected");
        JButton infraBtn = new JButton("Infrastructure");
        JButton revenueBtn = new JButton("Category Revenue");
        JButton contactsBtn = new JButton("Contact Directory");
        
        JButton[] tabs = {defaultersBtn, disconnectedBtn, infraBtn, revenueBtn, contactsBtn};
        for (JButton btn : tabs) {
            styleTabButton(btn, textGray);
            topSec.add(btn);
        }
        defaultersBtn.setForeground(teal);

        // --- 4. CARDS PANEL ---
        CardLayout cardLayout = new CardLayout();
        JPanel cardsPanel = new JPanel(cardLayout);
        cardsPanel.setBackground(Color.WHITE);

        defaultersModel = new DefaultTableModel(new String[]{"ID", "Institution Name", "Category", "Monthly Bill", "Fine Surcharged"}, 0);
        cardsPanel.add(createTablePanel(defaultersModel, null), "DEFAULTERS");

        disconnectedModel = new DefaultTableModel(new String[]{"ID", "Institution Name", "Category", "Unpaid Balance"}, 0);
        disconnectedTable = new JTable(disconnectedModel);
        styleTable(disconnectedTable);
        disconnectedTable.setRowSelectionAllowed(true); 
        
        JPanel discPage = new JPanel(new BorderLayout());
        discPage.setBackground(Color.WHITE);
        discPage.add(new JScrollPane(disconnectedTable), BorderLayout.CENTER);
        
        JButton deleteBtn = new JButton("Permanently Delete Selected Client");
        deleteBtn.setBackground(new Color(220, 53, 69)); 
        deleteBtn.setForeground(Color.WHITE);
        deleteBtn.setFocusPainted(false);
        deleteBtn.addActionListener(e -> deleteDisconnectedClient());
        
        JPanel btnWrapper = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnWrapper.setBackground(Color.WHITE);
        btnWrapper.add(deleteBtn);
        discPage.add(btnWrapper, BorderLayout.SOUTH);
        cardsPanel.add(discPage, "DISCONNECTED");

        infraModel = new DefaultTableModel(new String[]{"Institution Name", "Category", "Hardware/LAN Fees Paid", "Installation Fees Paid"}, 0);
        cardsPanel.add(createTablePanel(infraModel, null), "INFRA");

        revenueModel = new DefaultTableModel(new String[]{"Category", "Total Revenue Collected"}, 0);
        cardsPanel.add(createTablePanel(revenueModel, null), "REVENUE");

        contactsModel = new DefaultTableModel(new String[]{"Institution", "Contact Person", "Phone Number", "Email Address"}, 0);
        cardsPanel.add(createTablePanel(contactsModel, null), "CONTACTS");

        // --- 5. TAB SWAPPING ---
        defaultersBtn.addActionListener(e -> { cardLayout.show(cardsPanel, "DEFAULTERS"); setActiveTab(tabs, defaultersBtn); });
        disconnectedBtn.addActionListener(e -> { cardLayout.show(cardsPanel, "DISCONNECTED"); setActiveTab(tabs, disconnectedBtn); });
        infraBtn.addActionListener(e -> { cardLayout.show(cardsPanel, "INFRA"); setActiveTab(tabs, infraBtn); });
        revenueBtn.addActionListener(e -> { cardLayout.show(cardsPanel, "REVENUE"); setActiveTab(tabs, revenueBtn); });
        contactsBtn.addActionListener(e -> { cardLayout.show(cardsPanel, "CONTACTS"); setActiveTab(tabs, contactsBtn); });

        mainPanel.add(topSec, BorderLayout.NORTH);
        mainPanel.add(cardsPanel, BorderLayout.CENTER);
        add(titlePanel, BorderLayout.NORTH);
        add(mainPanel, BorderLayout.CENTER);

        loadReportData();
    }

    // --- LOGIC & HELPERS ---
    private void deleteDisconnectedClient() {
        int row = disconnectedTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Please select a disconnected client to delete.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String name = disconnectedModel.getValueAt(row, 1).toString();
        int confirm = JOptionPane.showConfirmDialog(this, "Permanently delete " + name + "?", "Confirm Wipe", JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            InstitutionDAO dao = new InstitutionDAO();
            if (dao.deleteInstitution(name)) {
                JOptionPane.showMessageDialog(this, name + " has been wiped from the system.");
                loadReportData(); 
            }
        }
    }

    private void setActiveTab(JButton[] allTabs, JButton active) {
        for (JButton btn : allTabs) btn.setForeground(textGray);
        active.setForeground(teal);
    }

    private JPanel createTablePanel(DefaultTableModel model, JTable customTable) {
        JTable table = (customTable == null) ? new JTable(model) : customTable;
        if (customTable == null) styleTable(table);
        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getViewport().setBackground(Color.WHITE);
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.add(scroll, BorderLayout.CENTER);
        return panel;
    }

    private void styleTabButton(JButton btn, Color textColor) {
        btn.setFont(new Font("Arial", Font.BOLD, 14));
        btn.setForeground(textColor);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setContentAreaFilled(false); 
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR)); 
    }

    private void styleTable(JTable table) {
        table.setRowHeight(40);
        table.setShowVerticalLines(false);
        table.setShowHorizontalLines(true);
        table.setGridColor(new Color(241, 245, 249)); 
        table.setIntercellSpacing(new Dimension(0, 0)); 
        table.setRowSelectionAllowed(false);
        table.setFillsViewportHeight(true);
        table.setDefaultEditor(Object.class, null);
        
        JTableHeader header = table.getTableHeader();
        header.setBackground(Color.WHITE);
        header.setForeground(new Color(100, 116, 139));
        header.setFont(new Font("Arial", Font.BOLD, 12));
        header.setPreferredSize(new Dimension(0, 40));
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(226, 232, 240)));
    }

    /**
     * Loops through the memory-safe Java Lists returned by the DAO.
     */
    public void loadReportData() {
        defaultersModel.setRowCount(0);
        disconnectedModel.setRowCount(0);
        infraModel.setRowCount(0);
        revenueModel.setRowCount(0);
        contactsModel.setRowCount(0);
        
        InstitutionDAO dao = new InstitutionDAO();
        
        // Populate standard strings and format doubles with "KSh" tags
        for (Object[] row : dao.getDefaultersReport()) {
            defaultersModel.addRow(new Object[]{row[0], row[1], row[2], "KSh " + row[3], "KSh " + row[4]});
        }
        for (Object[] row : dao.getDisconnectedReport()) {
            disconnectedModel.addRow(new Object[]{row[0], row[1], row[2], "KSh " + row[3]});
        }
        for (Object[] row : dao.getInfrastructureReport()) {
            infraModel.addRow(new Object[]{row[0], row[1], "KSh " + row[2], "KSh " + row[3]});
        }
        for (Object[] row : dao.getCategoryRevenueReport()) {
            revenueModel.addRow(new Object[]{row[0], "KSh " + row[1]});
        }
        for (Object[] row : dao.getContactsDirectory()) {
            contactsModel.addRow(row);
        }
    }
}