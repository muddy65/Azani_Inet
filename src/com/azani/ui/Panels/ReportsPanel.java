package com.azani.ui.Panels;

import com.azani.dao.InstitutionDAO;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;

public class ReportsPanel extends JPanel {

    // --- MODERN WEB PALETTE ---
    private final Color bgApp = new Color(248, 250, 252);         // Slate 50
    private final Color cardSurface = Color.WHITE;                // Clean White
    private final Color textDark = new Color(15, 23, 42);         // Slate 900
    private final Color textMuted = new Color(100, 116, 139);     // Slate 500
    private final Color borderLight = new Color(226, 232, 240);   // Slate 200
    private final Color primaryAccent = new Color(14, 165, 233);  // Sky Blue 500
    private final Color dangerRed = new Color(239, 68, 68);       // Red 500

    private DefaultTableModel defaultersModel, disconnectedModel, infraModel, revenueModel, contactsModel;
    private JTable disconnectedTable;

    public ReportsPanel() {
        setLayout(new BorderLayout(0, 20)); 
        setBackground(bgApp);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20)); 

        // --- 1. HEAD PANEL ---
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setBackground(cardSurface);
        titlePanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        
        JLabel title = new JLabel("Reports & Analytics");
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setForeground(textDark);
        titlePanel.add(title, BorderLayout.WEST);

        JButton refreshBtn = new JButton("Refresh Data");
        refreshBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        refreshBtn.setPreferredSize(new Dimension(140, 40));
        refreshBtn.setBorderPainted(false);
        applyHoverEffect(refreshBtn, primaryAccent, new Color(2, 132, 199)); // Sky to Light Blue
        refreshBtn.addActionListener(e -> loadReportData());
        titlePanel.add(refreshBtn, BorderLayout.EAST);

        // --- 2. MAIN CONTENT PANEL ---
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(bgApp);

        // --- 3. TAB BUTTONS ---
        JPanel topSec = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 10));
        topSec.setBackground(bgApp);
        topSec.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        
        JButton defaultersBtn = new JButton("Defaulters");
        JButton disconnectedBtn = new JButton("Disconnected");
        JButton infraBtn = new JButton("Infrastructure");
        JButton revenueBtn = new JButton("Category Revenue");
        JButton contactsBtn = new JButton("Contact Directory");
        
        JButton[] tabs = {defaultersBtn, disconnectedBtn, infraBtn, revenueBtn, contactsBtn};
        for (JButton btn : tabs) {
            styleTabButton(btn);
            topSec.add(btn);
        }
        setActiveTab(tabs, defaultersBtn); // Set initial active tab

        // --- 4. CARDS PANEL ---
        CardLayout cardLayout = new CardLayout();
        JPanel cardsPanel = new JPanel(cardLayout);
        cardsPanel.setBackground(cardSurface);
        cardsPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(borderLight, 1),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));

        defaultersModel = new DefaultTableModel(new String[]{"ID", "Institution Name", "Category", "Monthly Bill", "Fine Surcharged"}, 0);
        cardsPanel.add(createTablePanel(defaultersModel, null), "DEFAULTERS");

        disconnectedModel = new DefaultTableModel(new String[]{"ID", "Institution Name", "Category", "Unpaid Balance"}, 0);
        disconnectedTable = new JTable(disconnectedModel);
        styleTable(disconnectedTable);
        disconnectedTable.setRowSelectionAllowed(true); 
        disconnectedTable.setSelectionBackground(new Color(241, 245, 249)); // Subtle selection color
        
        JPanel discPage = new JPanel(new BorderLayout());
        discPage.setBackground(cardSurface);
        discPage.add(new JScrollPane(disconnectedTable), BorderLayout.CENTER);
        
        JButton deleteBtn = new JButton("Permanently Delete Selected Client");
        deleteBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        deleteBtn.setPreferredSize(new Dimension(280, 40));
        deleteBtn.setBorderPainted(false);
        applyHoverEffect(deleteBtn, dangerRed, new Color(185, 28, 28)); // Dark red hover
        deleteBtn.addActionListener(e -> deleteDisconnectedClient());
        
        JPanel btnWrapper = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnWrapper.setBackground(cardSurface);
        btnWrapper.setBorder(BorderFactory.createEmptyBorder(15, 0, 0, 0));
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
        for (JButton btn : allTabs) {
            btn.setForeground(textMuted);
            btn.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
        }
        active.setForeground(primaryAccent);
        active.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 3, 0, primaryAccent),
            BorderFactory.createEmptyBorder(8, 16, 5, 16)
        ));
    }

    private JPanel createTablePanel(DefaultTableModel model, JTable customTable) {
        JTable table = (customTable == null) ? new JTable(model) : customTable;
        if (customTable == null) styleTable(table);
        
        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getViewport().setBackground(cardSurface);
        
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(cardSurface);
        panel.add(scroll, BorderLayout.CENTER);
        return panel;
    }

    private void styleTabButton(JButton btn) {
        btn.setFont(new Font("Segoe UI", Font.BOLD, 15));
        btn.setForeground(textMuted);
        btn.setBackground(bgApp);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setContentAreaFilled(false); 
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR)); 
        
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                if (btn.getForeground() != primaryAccent) {
                    btn.setForeground(textDark);
                }
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                if (btn.getForeground() != primaryAccent) {
                    btn.setForeground(textMuted);
                }
            }
        });
    }

    private void applyHoverEffect(JButton btn, Color defaultColor, Color hoverColor) {
        btn.setBackground(defaultColor);
        btn.setForeground(Color.WHITE);
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

    private void styleTable(JTable table) {
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.setForeground(textDark);
        table.setRowHeight(45);
        table.setShowVerticalLines(false);
        table.setShowHorizontalLines(true);
        table.setGridColor(borderLight); 
        table.setIntercellSpacing(new Dimension(0, 0)); 
        table.setRowSelectionAllowed(false);
        table.setFillsViewportHeight(true);
        table.setDefaultEditor(Object.class, null);
        
        // Padded Cell Renderer for all columns
        DefaultTableCellRenderer paddedRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                ((JComponent) c).setBorder(BorderFactory.createEmptyBorder(0, 15, 0, 15));
                return c;
            }
        };
        
        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(paddedRenderer);
        }
        
        JTableHeader header = table.getTableHeader();
        header.setBackground(cardSurface);
        header.setForeground(textMuted);
        header.setFont(new Font("Segoe UI", Font.BOLD, 13));
        header.setPreferredSize(new Dimension(0, 45));
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, borderLight));
        
        // Pad the headers too
        ((DefaultTableCellRenderer)header.getDefaultRenderer()).setHorizontalAlignment(JLabel.LEFT);
    }

    public void loadReportData() {
        defaultersModel.setRowCount(0);
        disconnectedModel.setRowCount(0);
        infraModel.setRowCount(0);
        revenueModel.setRowCount(0);
        contactsModel.setRowCount(0);
        
        InstitutionDAO dao = new InstitutionDAO();
        
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