package com.azani.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class InstitutionDAO {

    // ==========================================
    // 1. VALIDATION METHODS
    // ==========================================

    /**
     * Checks if an institution name is already registered to prevent duplicates
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
     * Checks for duplicate email contact so that one person cannot register two institutions
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
     * Saves a new institution into the database and returns true if successful.
     */
    public boolean registerInstitution(String name, String category, String contactName, String phone, String email, int bandwidth) {
        String insertInst = "INSERT INTO institutions (name, category, contact_name, contact_phone, contact_email, registration_date) VALUES(?, ?, ?, ?, ?, CURRENT_DATE)";
        String insertSub = "INSERT INTO subscriptions (institution_id, bandwidth_mbps, monthly_cost, connection_status) VALUES(?, ?, ?, 'ACTIVE')";
        
        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement pstmt1 = conn.prepareStatement(insertInst, java.sql.Statement.RETURN_GENERATED_KEYS)) {
             
            pstmt1.setString(1, name);
            pstmt1.setString(2, category);
            pstmt1.setString(3, contactName);
            pstmt1.setString(4, phone);
            pstmt1.setString(5, email);
            pstmt1.executeUpdate();

            // Grab newly created id to use in the subscriptions table
            java.sql.ResultSet keys = pstmt1.getGeneratedKeys();
            if (keys.next()) {
                int newId = keys.getInt(1);
                PreparedStatement pstmt2 = conn.prepareStatement(insertSub);
                pstmt2.setInt(1, newId);
                pstmt2.setInt(2, bandwidth);
                pstmt2.setDouble(3, bandwidth * 150.0); // Example math for cost
                pstmt2.executeUpdate();
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
     * Use this method to change a client to 'OVERDUE' or 'DISCONNECTED'
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
     * Automatically changes status to 'DISCONNECTED' if 30 days have passed
     * since the last payment or registration date.
     */
    public void autoDisconnectDefaulters() {
        String sql = "UPDATE subscriptions s " +
                     "JOIN institutions i ON s.institution_id = i.institution_id " +
                     "LEFT JOIN (SELECT institution_id, MAX(payment_date) as last_pay FROM payments GROUP BY institution_id) p " +
                     "ON s.institution_id = p.institution_id " +
                     "SET s.connection_status = 'DISCONNECTED' " +
                     "WHERE DATEDIFF(CURRENT_DATE, COALESCE(p.last_pay, i.registration_date)) >= 30 " +
                     "AND s.connection_status = 'ACTIVE'";
                     
        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}