package com.azani.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class PaymentDAO {

    /**
     * Records a new transaction (Registration, Installation, Monthly, etc.)
     */
    public boolean recordPayment(int institutionId, String paymentType, double amount) {
        String sql = "INSERT INTO payments (institution_id, payment_type, amount, payment_date) " +
                    "VALUES (?, ?, ?, CURRENT_DATE)";
                    
        try (Connection conn = DatabaseHelper.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, institutionId);
            pstmt.setString(2, paymentType);
            pstmt.setDouble(3, amount);
            
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.err.println("Error recording payment: " + e.getMessage());
            return false;
        }
    }

    /**
     * Calculates the total revenue collected for a specific payment type.
     */
    public double getTotalRevenueByType(String paymentType) {
        String sql = "SELECT SUM(amount) AS total FROM payments WHERE payment_type = ?";
        double total = 0.0;
        
        try (Connection conn = DatabaseHelper.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, paymentType);
            
            // ResultSet holds the data returned by the database
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    total = rs.getDouble("total");
                }
            }
            
        } catch (SQLException e) {
            System.err.println("Error calculating revenue: " + e.getMessage());
        }
        
        return total;
    }
    // Inside PaymentDAO.java
    /**
     * Fetches all institutions that have overdue payments.
     */
    public ResultSet getDefaulters() {
        String sql = "SELECT i.institution_id, i.name, i.category, s.monthly_cost " +
                    "FROM institutions i " +
                    "JOIN subscriptions s ON i.institution_id = s.institution_id " +
                    "WHERE s.connection_status = 'OVERDUE'";
        
        try {
            Connection conn = DatabaseHelper.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql);
            
            // This executes the search and returns the data back to the ReportsPanel
            return pstmt.executeQuery();
            
        } catch (SQLException e) {
            System.err.println("Error fetching defaulters: " + e.getMessage());
            
            // Java requires a return statement even if it fails, so we return null
            return null;
        }
    }
}