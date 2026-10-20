package com.azani.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class InstitutionDAO {

    /**
     * Saves a new institution into the database and returns true if successful.
     */
/**
     * Saves a new institution into the database and returns true if successful.
     */
    public boolean registerInstitution(String name, String category, String contactName, String contactPhone, String contactEmail) {
        // We added contact_email and a 5th question mark (?)
        String sql = "INSERT INTO institutions (name, category, contact_name, contact_phone, contact_email, registration_date) " +
                    "VALUES (?, ?, ?, ?, ?, CURRENT_DATE)";
                    
        try (Connection conn = DatabaseHelper.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, name);
            pstmt.setString(2, category);
            pstmt.setString(3, contactName);
            pstmt.setString(4, contactPhone);
            pstmt.setString(5, contactEmail); // <-- Added the 5th parameter here
            
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.err.println("Error saving institution: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Fetches the 10 most recently registered institutions for the dashboard.
     */
    /**
     * Fetches the 10 most recently registered institutions AND their bandwidth.
     */
    public ResultSet getRecentRegistrations() {
        // We use a LEFT JOIN so that even if an institution doesn't have a subscription yet, 
        // they still show up on the dashboard.
        String sql = "SELECT i.institution_id, i.name, i.category, s.bandwidth_mbps, s.connection_status " +
                    "FROM institutions i " +
                    "LEFT JOIN subscriptions s ON i.institution_id = s.institution_id " +
                    "ORDER BY i.institution_id LIMIT 50";
        
        try {
            Connection conn = DatabaseHelper.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql);
            return pstmt.executeQuery();
            
        } catch (SQLException e) {
            System.err.println("Error fetching recent institutions: " + e.getMessage());
            return null;
        }
    }
    // --- DASHBOARD MATH METHODS ---
    
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
        // Calculates total Expected Monthly Recurring Revenue from ACTIVE subscriptions
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

    // --- STATUS MANAGEMENT METHOD ---
    
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
}