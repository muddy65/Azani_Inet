package com.azani.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;


public class InstitutionDAO {

    /*
    *Checks if an institution name is already rergistered to prevent duplicates
    */
   public boolean isNameTaken(String checkName){
    String sql = "SELECT COUNT(*) AS count FROM institutions WHERE name = ?";
    try(Connection conn = DatabaseHelper.getConnection();
        PreparedStatement pstmt = conn.prepareStatement(sql)){
            pstmt.setString(1,checkName);
            ResultSet rs = pstmt.executeQuery();
            if(rs.next()){
                return rs.getInt("count") > 0;
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return false;
   }

   /*
   *checks for duplicate email  contact so that one person cannot register two institutions
   */
   public  boolean isEmailTaken(String checkEmail){
    String sql = "SELECT COUNT(*) AS count FROM institutions WHERE contact_email = ?";
    try(Connection conn = DatabaseHelper.getConnection();
        PreparedStatement pstmt = conn.prepareStatement(sql)){
            pstmt.setString(1, checkEmail);
            ResultSet rs = pstmt.executeQuery();
            if(rs.next()){
                return rs.getInt("count") > 0;
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return false;
   }

    /**
     * Saves a new institution into the database and returns true if successful.
     */
/**
     * Saves a new institution into the database and returns true if successful.
     */
    public boolean registerInstitution(String name, String category, String contactName, String phone, String email, int bandwidth){
        String insertInst = "INSERT INTO institutions (name, category,contact_name, contact_phone, contact_email, registration_date) VALUES(?, ?, ?, ?, ?, CURRENT_DATE)";
        String insertSub = "INSERT INTO subscriptions(institution_id, bandwidth_mbps, monthly_cost, connection_status) VALUES(?,?,?, 'ACTIVE')";
        try (Connection conn = DatabaseHelper.getConnection();
            PreparedStatement pstmt1 = conn.prepareStatement(insertInst, java.sql.Statement.RETURN_GENERATED_KEYS)){
                pstmt1.setString(1,name);
                pstmt1.setString(2,category);
                pstmt1.setString(3,contactName);
                pstmt1.setString(4,phone);
                pstmt1.setString(5,email);
                pstmt1.executeUpdate();

                // Grab newly created  id to use in the subscriptions table
                java.sql.ResultSet keys = pstmt1.getGeneratedKeys();
                if (keys.next()){
                    int newId = keys.getInt(1);
                    PreparedStatement pstmt2 = conn.prepareStatement(insertSub);
                    pstmt2.setInt(1, newId);
                    pstmt2.setInt(2, bandwidth);
                    pstmt2.setDouble(3, bandwidth *150.0); //example math for cost
                    pstmt2.executeUpdate();
                }
                return true;
        }catch(Exception e){
            e.printStackTrace();
            return false;
        }
    };
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

    public boolean deleteInstitution(String institutionName){
        String sql = "DELETE FROM Institutions WHERE name = ?";
        try(Connection conn = DatabaseHelper.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)){
                pstmt.setString(1, institutionName);
                return pstmt.executeUpdate() > 0;
            }catch(Exception e){
                e.printStackTrace();
                return false;
            }
    }


    /*
    *searches for a client by partial name and returns their full details.
    * It returns a string array: [0] = ID,[1] = Full Name
    */
    public String[] searchInstitutionByName(String partialName){
        String sql = "SELECT i.institution_id, i.name, s.bandwidth_mbps, s.monthly_cost " +
                     "FROM institutions i " +
                     "LEFT JOIN subscriptions s ON i.institution_id = s.institution_id " +
                     "WHERE i.name LIKE ? LIMIT 1";

        try(Connection conn = DatabaseHelper.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)){

                pstmt.setString(1, "%" + partialName + "%");
                ResultSet rs = pstmt.executeQuery();

                if (rs.next()){
                    return new String[]{
                        String.valueOf(rs.getInt("institution_id")),
                        rs.getString("name"),
                        String.valueOf(rs.getInt("bandwidth_mbps")),
                        String.valueOf(rs.getDouble("monthly_cost"))
                    };
                }
            }catch(Exception e){
                e.printStackTrace();
            }
            return null;
        }
}