package com.azani.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class PaymentDAO {

    /**
     * Retrieves the current billing status and pending fees for an institution.
     * Used to calculate the exact amount due before processing a payment.
     * Returns an array: [0] = status, [1] = monthly_cost, [2] = fine_amount, [3] = reconnection_fee
     */
    public String[] getClientBillingDetails(int institutionId) {
        String sql = "SELECT connection_status, monthly_cost, fine_amount, reconnection_fee " +
                     "FROM subscriptions WHERE institution_id = ?";
                     
        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
             
            pstmt.setInt(1, institutionId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return new String[]{
                    rs.getString("connection_status"),
                    String.valueOf(rs.getDouble("monthly_cost")),
                    String.valueOf(rs.getDouble("fine_amount")),
                    String.valueOf(rs.getDouble("reconnection_fee"))
                };
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Records a new transaction and automatically clears any pending fines or reconnection fees.
     * Restores the institution's status to 'ACTIVE' upon successful payment.
     */
    public boolean processPayment(int institutionId, String paymentType, double amount) {
        String insertPayment = "INSERT INTO payments (institution_id, payment_type, amount, payment_date) " +
                               "VALUES (?, ?, ?, CURRENT_DATE)";
                               
        // Resets fines and restores active status[cite: 6]
        String updateStatus = "UPDATE subscriptions SET connection_status = 'ACTIVE', " +
                              "fine_amount = 0.00, reconnection_fee = 0.00 " +
                              "WHERE institution_id = ?";
                    
        try (Connection conn = DatabaseHelper.getConnection()) {
            // 1. Record the financial transaction
            try (PreparedStatement pstmt1 = conn.prepareStatement(insertPayment)) {
                pstmt1.setInt(1, institutionId);
                pstmt1.setString(2, paymentType);
                pstmt1.setDouble(3, amount);
                pstmt1.executeUpdate();
            }

            // 2. Clear penalties and restore connection status
            try (PreparedStatement pstmt2 = conn.prepareStatement(updateStatus)) {
                pstmt2.setInt(1, institutionId);
                pstmt2.executeUpdate();
            }
            return true;
            
        } catch (SQLException e) {
            System.err.println("Error processing payment: " + e.getMessage());
            return false;
        }
    }

    /**
     * Processes a bandwidth upgrade, applies the discounted rate, and updates the subscription.
     * Applies a 10% discount to the upgraded bandwidth tier[cite: 6].
     */
    public boolean upgradeBandwidth(int institutionId, int newBandwidth, double discountedMonthlyCost, double paymentAmount) {
        String insertPayment = "INSERT INTO payments (institution_id, payment_type, amount, payment_date) " +
                               "VALUES (?, 'Bandwidth Upgrade', ?, CURRENT_DATE)";
                               
        String updateSubscription = "UPDATE subscriptions SET bandwidth_mbps = ?, monthly_cost = ?, " +
                                    "connection_status = 'ACTIVE', fine_amount = 0.00, reconnection_fee = 0.00 " +
                                    "WHERE institution_id = ?";
                                    
        try (Connection conn = DatabaseHelper.getConnection()) {
            // 1. Record the upgrade payment
            try (PreparedStatement pstmt1 = conn.prepareStatement(insertPayment)) {
                pstmt1.setInt(1, institutionId);
                pstmt1.setDouble(2, paymentAmount);
                pstmt1.executeUpdate();
            }

            // 2. Update the bandwidth tier and new discounted monthly cost
            try (PreparedStatement pstmt2 = conn.prepareStatement(updateSubscription)) {
                pstmt2.setInt(1, newBandwidth);
                pstmt2.setDouble(2, discountedMonthlyCost);
                pstmt2.setInt(3, institutionId);
                pstmt2.executeUpdate();
            }
            return true;
            
        } catch (SQLException e) {
            System.err.println("Error processing bandwidth upgrade: " + e.getMessage());
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
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    total = rs.getDouble("total");
                }
            }
            
        } catch (SQLException e) {
            System.err.println("Error calculating revenue by type: " + e.getMessage());
        }
        
        return total;
    }

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
            return pstmt.executeQuery();
            
        } catch (SQLException e) {
            System.err.println("Error fetching defaulters: " + e.getMessage());
            return null;
        }
    }
}