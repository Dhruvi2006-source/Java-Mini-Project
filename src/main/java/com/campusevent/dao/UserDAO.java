package com.campusevent.dao;

import com.campusevent.model.User;
import com.campusevent.util.DBConnection;
import com.campusevent.util.PasswordUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Data Access Object for User database operations.
 */
public class UserDAO {

    /**
     * Register a new user (Student by default).
     * @param user User object containing registration details
     * @return true if insertion succeeded, false otherwise
     */
    public boolean registerUser(User user) {
        String sql = "INSERT INTO users (name, email, password, role) VALUES (?, ?, ?, ?)";
        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            conn = DBConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, user.getName().trim());
            pstmt.setString(2, user.getEmail().trim().toLowerCase());
            
            // Password is stored hashed
            String hashedPassword = PasswordUtil.hashPassword(user.getPassword());
            pstmt.setString(3, hashedPassword);
            
            String role = (user.getRole() != null && !user.getRole().trim().isEmpty()) ? user.getRole().toUpperCase() : "STUDENT";
            pstmt.setString(4, role);

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("UserDAO.registerUser SQL Error: " + e.getMessage());
            e.printStackTrace();
            return false;
        } finally {
            DBConnection.closeResources(conn, pstmt);
        }
    }

    /**
     * Check if an email is already registered in the system.
     * @param email Email address to check
     * @return true if email exists, false otherwise
     */
    public boolean isEmailExists(String email) {
        String sql = "SELECT COUNT(*) FROM users WHERE LOWER(email) = ?";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DBConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, email.trim().toLowerCase());
            rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            System.err.println("UserDAO.isEmailExists SQL Error: " + e.getMessage());
            e.printStackTrace();
        } finally {
            DBConnection.closeResources(conn, pstmt, rs);
        }
        return false;
    }

    /**
     * Authenticate user credentials.
     * @param email User email
     * @param plainPassword Raw password entered by user
     * @return User object if authentication is successful, null otherwise
     */
    public User authenticateUser(String email, String plainPassword) {
        String sql = "SELECT id, name, email, password, role, created_at FROM users WHERE LOWER(email) = ?";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DBConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, email.trim().toLowerCase());
            rs = pstmt.executeQuery();

            if (rs.next()) {
                String storedPasswordHash = rs.getString("password");
                String inputPasswordHash = PasswordUtil.hashPassword(plainPassword);

                if (storedPasswordHash != null && storedPasswordHash.equalsIgnoreCase(inputPasswordHash)) {
                    User user = new User();
                    user.setId(rs.getInt("id"));
                    user.setName(rs.getString("name"));
                    user.setEmail(rs.getString("email"));
                    user.setPassword(storedPasswordHash);
                    user.setRole(rs.getString("role"));
                    user.setCreatedAt(rs.getTimestamp("created_at"));
                    return user;
                }
            }
        } catch (SQLException e) {
            System.err.println("UserDAO.authenticateUser SQL Error: " + e.getMessage());
            e.printStackTrace();
        } finally {
            DBConnection.closeResources(conn, pstmt, rs);
        }
        return null;
    }

    /**
     * Find user details by User ID.
     * @param id User ID
     * @return User object if found, null otherwise
     */
    public User getUserById(int id) {
        String sql = "SELECT id, name, email, role, created_at FROM users WHERE id = ?";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DBConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, id);
            rs = pstmt.executeQuery();

            if (rs.next()) {
                User user = new User();
                user.setId(rs.getInt("id"));
                user.setName(rs.getString("name"));
                user.setEmail(rs.getString("email"));
                user.setRole(rs.getString("role"));
                user.setCreatedAt(rs.getTimestamp("created_at"));
                return user;
            }
        } catch (SQLException e) {
            System.err.println("UserDAO.getUserById SQL Error: " + e.getMessage());
            e.printStackTrace();
        } finally {
            DBConnection.closeResources(conn, pstmt, rs);
        }
        return null;
    }

    /**
     * Find user details by Email.
     * @param email User email address
     * @return User object if found, null otherwise
     */
    public User getUserByEmail(String email) {
        String sql = "SELECT id, name, email, role, created_at FROM users WHERE LOWER(email) = ?";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DBConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, email.trim().toLowerCase());
            rs = pstmt.executeQuery();

            if (rs.next()) {
                User user = new User();
                user.setId(rs.getInt("id"));
                user.setName(rs.getString("name"));
                user.setEmail(rs.getString("email"));
                user.setRole(rs.getString("role"));
                user.setCreatedAt(rs.getTimestamp("created_at"));
                return user;
            }
        } catch (SQLException e) {
            System.err.println("UserDAO.getUserByEmail SQL Error: " + e.getMessage());
            e.printStackTrace();
        } finally {
            DBConnection.closeResources(conn, pstmt, rs);
        }
        return null;
    }

    /**
     * Get total count of registered student accounts in the system.
     * @return Total student count
     */
    public int getTotalStudentsCount() {
        String sql = "SELECT COUNT(*) FROM users WHERE role = 'STUDENT'";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DBConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("UserDAO.getTotalStudentsCount SQL Error: " + e.getMessage());
            e.printStackTrace();
        } finally {
            DBConnection.closeResources(conn, pstmt, rs);
        }
        return 0;
    }
}
