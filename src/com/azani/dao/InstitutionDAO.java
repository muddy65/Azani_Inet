package com.azani.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import com.azani.service.BillingManager;
import java.util.List;
import java.util.ArrayList;

public class InstitutionDAO {

    // ==========================================
    // 1. VALIDATION METHODS
    // ==========================================

    /**
     * Checks if an institution name is already registered to prevent duplicates.
     */
    public boolean isNameTaken(String checkName) {
        String sql = "SELECT COUNT(*) AS count FROM institutions WHERE name = ?";
        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, checkName);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("count") > 0;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Checks for duplicate email contact so that one person cannot register two institutions.
     */
    public boolean isEmailTaken(String checkEmail) {
        String sql = "SELECT COUNT(*) AS count FROM institutions WHERE contact_email = ?";
        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, checkEmail);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("count") > 0;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    // ==========================================
    // 2. CORE REGISTRATION & DELETION
    // ==========================================

    /**
     * Saves a new institution into the database and calculates the accurate monthly cost.
     */
    public boolean registerInstitution(String name, String category, String contactName, String phone, String email, int bandwidth, double installFee, double hardwareFee) {
        String insertInst = "INSERT INTO institutions (name, category, contact_name, contact_phone, contact_email, registration_date) VALUES(?, ?, ?, ?, ?, CURRENT_DATE)";
        String insertSub = "INSERT INTO subscriptions (institution_id, bandwidth_mbps, monthly_cost, connection_status) VALUES(?, ?, ?, 'ACTIVE')";
        String insertPayment = "INSERT INTO payments (institution_id, payment_type, amount, payment_date) VALUES(?,?,?, CURRENT_DATE)";
        
        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement pstmt1 = conn.prepareStatement(insertInst, java.sql.Statement.RETURN_GENERATED_KEYS)) {
             
            pstmt1.setString(1, name);
            pstmt1.setString(2, category);
            pstmt1.setString(3, contactName);
            pstmt1.setString(4, phone);
            pstmt1.setString(5, email);
            pstmt1.executeUpdate();

            java.sql.ResultSet keys = pstmt1.getGeneratedKeys();
            if (keys.next()) {
                int newId = keys.getInt(1);
                
                // 1. Setup Subscription with actual BillingManager calculation
                try (PreparedStatement pstmt2 = conn.prepareStatement(insertSub)) {
                    double monthlyCost = BillingManager.getMonthlyBandwidthCost(bandwidth);
                    pstmt2.setInt(1, newId);
                    pstmt2.setInt(2, bandwidth);
                    pstmt2.setDouble(3, monthlyCost);
                    pstmt2.executeUpdate();
                }

                // 2. Record Initial Fees
                try (PreparedStatement payStmt = conn.prepareStatement(insertPayment)) {
                    // Mandatory Registration Fee
                    payStmt.setInt(1, newId);
                    payStmt.setString(2, "Registration Fee");
                    payStmt.setDouble(3, BillingManager.REGISTRATION_FEE);
                    payStmt.executeUpdate();

                    // Optional Installation Fee
                    if (installFee > 0) {
                        payStmt.setInt(1, newId);
                        payStmt.setString(2, "Installation Fee");
                        payStmt.setDouble(3, installFee);
                        payStmt.executeUpdate();
                    }

                    // Optional Hardware/LAN Fee
                    if (hardwareFee > 0) {
                        payStmt.setInt(1, newId);
                        payStmt.setString(2, "Hardware & LAN Fee");
                        payStmt.setDouble(3, hardwareFee);
                        payStmt.executeUpdate();
                    }
                }
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Deletes an institution permanently by name.
     */
    public boolean deleteInstitution(String institutionName) {
        String sql = "DELETE FROM institutions WHERE name = ?";
        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, institutionName);
            return pstmt.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // ==========================================
    // 3. SEARCH & DATA RETRIEVAL 
    // ==========================================

    /**
     * Searches for a client by partial name and returns their full details.
     * Returns a string array: [0] = ID, [1] = Full Name, [2] = Bandwidth, [3] = Cost
     */
    public String[] searchInstitutionByName(String partialName) {
        String sql = "SELECT i.institution_id, i.name, s.bandwidth_mbps, s.monthly_cost " +
                     "FROM institutions i " +
                     "LEFT JOIN subscriptions s ON i.institution_id = s.institution_id " +
                     "WHERE i.name LIKE ? LIMIT 1";

        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
             
            pstmt.setString(1, "%" + partialName + "%");
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return new String[]{
                    String.valueOf(rs.getInt("institution_id")),
                    rs.getString("name"),
                    String.valueOf(rs.getInt("bandwidth_mbps")),
                    String.valueOf(rs.getDouble("monthly_cost"))
                };
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Fetches all ACTIVE institutions and their bandwidth for the dashboard table.
     */
    public ResultSet getRecentRegistrations() {
        String sql = "SELECT i.institution_id, i.name, i.category, s.bandwidth_mbps, s.connection_status " +
                     "FROM institutions i " +
                     "LEFT JOIN subscriptions s ON i.institution_id = s.institution_id " +
                     "WHERE s.connection_status = 'ACTIVE' " +
                     "ORDER BY i.institution_id";
        try {
            Connection conn = DatabaseHelper.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql);
            return pstmt.executeQuery();
        } catch (SQLException e) {
            System.err.println("Error fetching recent institutions: " + e.getMessage());
            return null;
        }
    }

    // ==========================================
    // 4. DASHBOARD MATH & STATISTICS
    // ==========================================

    /**
     * Calculates the total lifetime revenue from all payments ever made.
     */
    public double getTotalLifetimeRevenue() {
        String sql = "SELECT SUM(amount) AS total FROM payments";
        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            if (rs.next()) {
                return rs.getDouble("total");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0.0;
    }

    public int getTotalInstitutions() {
        String sql = "SELECT COUNT(*) AS total FROM institutions";
        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            if (rs.next()) return rs.getInt("total");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public double getMonthlyRevenue() {
        String sql = "SELECT SUM(monthly_cost) AS revenue FROM subscriptions WHERE connection_status = 'ACTIVE'";
        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            if (rs.next()) return rs.getDouble("revenue");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0.0;
    }

    public int getStatusCount(String status) {
        String sql = "SELECT COUNT(*) AS total FROM subscriptions WHERE connection_status = ?";
        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, status);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) return rs.getInt("total");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    // ==========================================
    // 5. STATUS AUTOMATION & MANAGEMENT
    // ==========================================

    /**
     * Use this method to change a client to 'OVERDUE' or 'DISCONNECTED'.
     */
    public boolean updateConnectionStatus(int institutionId, String newStatus) {
        String sql = "UPDATE subscriptions SET connection_status = ? WHERE institution_id = ?";
        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, newStatus);
            pstmt.setInt(2, institutionId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Executes the strict end-of-month and 10th-day disconnection rules.
     * Replaces the generic 30-day auto-disconnect to comply with project specifications.
     */
    public void runAutomatedBillingEngine() {
        // Rule 1: Apply 15% fine and set to OVERDUE if the bill isn't paid by the end of the current month.
        String applyFinesSql = 
            "UPDATE subscriptions s " +
            "JOIN institutions i ON s.institution_id = i.institution_id " +
            "LEFT JOIN (SELECT institution_id, MAX(payment_date) as last_pay FROM payments WHERE payment_type = 'Monthly Bill' GROUP BY institution_id) p " +
            "ON s.institution_id = p.institution_id " +
            "SET s.connection_status = 'OVERDUE', " +
            "    s.fine_amount = s.monthly_cost * 0.15 " +
            "WHERE s.connection_status = 'ACTIVE' " +
            "AND ( " +
            "  (p.last_pay IS NULL AND i.registration_date < DATE_FORMAT(CURRENT_DATE, '%Y-%m-01')) " +
            "  OR (p.last_pay < DATE_FORMAT(CURRENT_DATE, '%Y-%m-01')) " +
            ")";

        // Rule 2: Disconnect if the fine and bill are not paid by the 10th day of the subsequent month.
        String applyDisconnectsSql = 
            "UPDATE subscriptions " +
            "SET connection_status = 'DISCONNECTED' " +
            "WHERE connection_status = 'OVERDUE' " +
            "AND DAY(CURRENT_DATE) >= 10";

        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement stmt1 = conn.prepareStatement(applyFinesSql);
             PreparedStatement stmt2 = conn.prepareStatement(applyDisconnectsSql)) {
             
            stmt1.executeUpdate(); 
            stmt2.executeUpdate(); 
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
// ==========================================
    // 6. REPORTS GENERATION (Memory-Safe Lists)
    // ==========================================

    public List<Object[]> getDefaultersReport() {
        List<Object[]> list = new ArrayList<>();
        String sql = "SELECT i.institution_id, i.name, i.category, s.monthly_cost, s.fine_amount " +
                     "FROM institutions i JOIN subscriptions s ON i.institution_id = s.institution_id WHERE s.connection_status = 'OVERDUE'";
        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                list.add(new Object[]{rs.getString(1), rs.getString(2), rs.getString(3), rs.getDouble(4), rs.getDouble(5)});
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    public List<Object[]> getDisconnectedReport() {
        List<Object[]> list = new ArrayList<>();
        String sql = "SELECT i.institution_id, i.name, i.category, (s.monthly_cost + s.fine_amount) as total_due " +
                     "FROM institutions i JOIN subscriptions s ON i.institution_id = s.institution_id WHERE s.connection_status = 'DISCONNECTED'";
        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                list.add(new Object[]{rs.getString(1), rs.getString(2), rs.getString(3), rs.getDouble(4)});
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    public List<Object[]> getInfrastructureReport() {
        List<Object[]> list = new ArrayList<>();
        String sql = "SELECT i.name, i.category, " +
                     "SUM(CASE WHEN p.payment_type = 'Hardware & LAN Fee' THEN p.amount ELSE 0 END) as hw, " +
                     "SUM(CASE WHEN p.payment_type = 'Installation Fee' THEN p.amount ELSE 0 END) as inst " +
                     "FROM institutions i JOIN payments p ON i.institution_id = p.institution_id GROUP BY i.institution_id, i.name, i.category";
        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                list.add(new Object[]{rs.getString(1), rs.getString(2), rs.getDouble(3), rs.getDouble(4)});
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    public List<Object[]> getCategoryRevenueReport() {
        List<Object[]> list = new ArrayList<>();
        String sql = "SELECT i.category, SUM(p.amount) as total_revenue " +
                     "FROM institutions i JOIN payments p ON i.institution_id = p.institution_id GROUP BY i.category";
        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                list.add(new Object[]{rs.getString(1), rs.getDouble(2)});
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    public List<Object[]> getContactsDirectory() {
        List<Object[]> list = new ArrayList<>();
        String sql = "SELECT name, contact_name, contact_phone, contact_email FROM institutions";
        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                list.add(new Object[]{rs.getString(1), rs.getString(2), rs.getString(3), rs.getString(4)});
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

}