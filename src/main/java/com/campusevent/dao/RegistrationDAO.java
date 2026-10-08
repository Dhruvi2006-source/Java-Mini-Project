package com.campusevent.dao;

import com.campusevent.model.Event;
import com.campusevent.model.Registration;
import com.campusevent.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for Event Registrations.
 */
public class RegistrationDAO {

    /**
     * Check if a student is already registered for a specific event.
     * @param userId Student User ID
     * @param eventId Event ID
     * @return true if registered, false otherwise
     */
    public boolean isUserRegistered(int userId, int eventId) {
        String sql = "SELECT COUNT(*) FROM registrations WHERE user_id = ? AND event_id = ?";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DBConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, userId);
            pstmt.setInt(2, eventId);
            rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            System.err.println("RegistrationDAO.isUserRegistered SQL Error: " + e.getMessage());
            e.printStackTrace();
        } finally {
            DBConnection.closeResources(conn, pstmt, rs);
        }
        return false;
    }

    /**
     * Register a student for an event.
     * @param userId Student User ID
     * @param eventId Event ID
     * @return true if registration succeeds, false otherwise
     */
    public boolean registerForEvent(int userId, int eventId) {
        String sql = "INSERT INTO registrations (user_id, event_id) VALUES (?, ?)";
        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            conn = DBConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, userId);
            pstmt.setInt(2, eventId);

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("RegistrationDAO.registerForEvent SQL Error: " + e.getMessage());
            e.printStackTrace();
            return false;
        } finally {
            DBConnection.closeResources(conn, pstmt);
        }
    }

    /**
     * Cancel an event registration for a logged-in student.
     * Enforces strict ownership check (user_id = ? AND event_id = ?).
     * @param userId Student User ID (from session)
     * @param eventId Event ID
     * @return true if cancellation succeeded, false otherwise
     */
    public boolean cancelRegistration(int userId, int eventId) {
        String sql = "DELETE FROM registrations WHERE user_id = ? AND event_id = ?";
        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            conn = DBConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, userId);
            pstmt.setInt(2, eventId);

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("RegistrationDAO.cancelRegistration SQL Error: " + e.getMessage());
            e.printStackTrace();
            return false;
        } finally {
            DBConnection.closeResources(conn, pstmt);
        }
    }

    /**
     * Get count of students currently registered for an event.
     * @param eventId Event ID
     * @return Total registered count
     */
    public int getRegisteredCount(int eventId) {
        String sql = "SELECT COUNT(*) FROM registrations WHERE event_id = ?";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DBConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, eventId);
            rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("RegistrationDAO.getRegisteredCount SQL Error: " + e.getMessage());
            e.printStackTrace();
        } finally {
            DBConnection.closeResources(conn, pstmt, rs);
        }
        return 0;
    }

    /**
     * Get available seats count for an event (capacity - registered count).
     * @param eventId Event ID
     * @param capacity Total event capacity
     * @return Number of available seats
     */
    public int getAvailableSeats(int eventId, int capacity) {
        int registeredCount = getRegisteredCount(eventId);
        int available = capacity - registeredCount;
        return Math.max(0, available);
    }

    /**
     * Get total registrations count across all events (Admin statistic).
     * @return Total registrations count
     */
    public int getTotalRegistrationsCount() {
        String sql = "SELECT COUNT(*) FROM registrations";
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
            System.err.println("RegistrationDAO.getTotalRegistrationsCount SQL Error: " + e.getMessage());
            e.printStackTrace();
        } finally {
            DBConnection.closeResources(conn, pstmt, rs);
        }
        return 0;
    }

    /**
     * Get total registrations count for a specific user.
     * @param userId Student User ID
     * @return Total event registrations count
     */
    public int getRegistrationCountByUser(int userId) {
        String sql = "SELECT COUNT(*) FROM registrations WHERE user_id = ?";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DBConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, userId);
            rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("RegistrationDAO.getRegistrationCountByUser SQL Error: " + e.getMessage());
            e.printStackTrace();
        } finally {
            DBConnection.closeResources(conn, pstmt, rs);
        }
        return 0;
    }

    /**
     * Retrieve all registrations for a student joined with Event details.
     * @param userId Student User ID
     * @return List of Registration objects with populated Event data
     */
    public List<Registration> getRegistrationsByUser(int userId) {
        List<Registration> list = new ArrayList<>();
        String sql = "SELECT r.id AS reg_id, r.user_id, r.event_id, r.registration_date, " +
                     "e.id AS event_id, e.title, e.description, e.event_date, e.event_time, e.venue, e.capacity, e.event_type, e.min_team_size, e.max_team_size " +
                     "FROM registrations r " +
                     "INNER JOIN events e ON r.event_id = e.id " +
                     "WHERE r.user_id = ? " +
                     "ORDER BY e.event_date ASC, e.event_time ASC";

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        TeamDAO teamDAO = new TeamDAO();

        try {
            conn = DBConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, userId);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                Registration reg = new Registration();
                reg.setId(rs.getInt("reg_id"));
                reg.setUserId(rs.getInt("user_id"));
                reg.setEventId(rs.getInt("event_id"));
                reg.setRegistrationDate(rs.getTimestamp("registration_date"));

                Event event = new Event();
                event.setId(rs.getInt("event_id"));
                event.setTitle(rs.getString("title"));
                event.setDescription(rs.getString("description"));
                event.setEventDate(rs.getDate("event_date"));
                event.setEventTime(rs.getTime("event_time"));
                event.setVenue(rs.getString("venue"));
                event.setCapacity(rs.getInt("capacity"));
                
                try {
                    event.setEventType(rs.getString("event_type"));
                    event.setMinTeamSize(rs.getInt("min_team_size"));
                    event.setMaxTeamSize(rs.getInt("max_team_size"));
                } catch (SQLException ignored) {
                    event.setEventType("INDIVIDUAL");
                }

                reg.setEventTitle(event.getTitle());
                reg.setEventDate(event.getEventDate());
                reg.setEventTime(event.getEventTime());
                reg.setVenue(event.getVenue());
                reg.setEventType(event.getEventType());
                reg.setEvent(event);

                if (event.isTeamEvent()) {
                    com.campusevent.model.Team team = teamDAO.getTeamByEventAndUser(event.getId(), userId);
                    if (team != null) {
                        reg.setTeamId(team.getId());
                        reg.setTeamName(team.getTeamName());
                        reg.setRoleInTeam(team.getTeamLeaderId() == userId ? "TEAM LEADER" : "TEAM MEMBER");
                        reg.setTeamMembers(team.getMembers());
                    } else {
                        reg.setRoleInTeam("TEAM MEMBER");
                    }
                } else {
                    reg.setRoleInTeam("INDIVIDUAL");
                }

                list.add(reg);
            }
        } catch (SQLException e) {
            System.err.println("RegistrationDAO.getRegistrationsByUser SQL Error: " + e.getMessage());
            e.printStackTrace();
        } finally {
            DBConnection.closeResources(conn, pstmt, rs);
        }
        return list;
    }

    /**
     * Retrieve all registrations across all students joined with User and Event details (Admin view).
     * @param eventIdFilter Optional Event ID to filter by (0 or negative for all)
     * @return List of Registration objects with student name, email, and event details
     */
    public List<Registration> getAllRegistrationsWithDetails(int eventIdFilter) {
        List<Registration> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder(
            "SELECT r.id AS reg_id, r.user_id, r.event_id, r.registration_date, " +
            "u.name AS user_name, u.email AS user_email, " +
            "e.title AS event_title, e.event_date, e.event_time, e.venue, e.event_type " +
            "FROM registrations r " +
            "INNER JOIN users u ON r.user_id = u.id " +
            "INNER JOIN events e ON r.event_id = e.id "
        );

        if (eventIdFilter > 0) {
            sql.append("WHERE r.event_id = ? ");
        }

        sql.append("ORDER BY r.registration_date DESC");

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        TeamDAO teamDAO = new TeamDAO();

        try {
            conn = DBConnection.getConnection();
            pstmt = conn.prepareStatement(sql.toString());

            if (eventIdFilter > 0) {
                pstmt.setInt(1, eventIdFilter);
            }

            rs = pstmt.executeQuery();

            while (rs.next()) {
                Registration reg = new Registration();
                reg.setId(rs.getInt("reg_id"));
                reg.setUserId(rs.getInt("user_id"));
                reg.setEventId(rs.getInt("event_id"));
                reg.setRegistrationDate(rs.getTimestamp("registration_date"));

                reg.setUserName(rs.getString("user_name"));
                reg.setUserEmail(rs.getString("user_email"));
                reg.setEventTitle(rs.getString("event_title"));
                reg.setEventDate(rs.getDate("event_date"));
                reg.setEventTime(rs.getTime("event_time"));
                reg.setVenue(rs.getString("venue"));
                
                try {
                    reg.setEventType(rs.getString("event_type"));
                } catch (SQLException ignored) {
                    reg.setEventType("INDIVIDUAL");
                }

                if ("TEAM".equalsIgnoreCase(reg.getEventType())) {
                    com.campusevent.model.Team team = teamDAO.getTeamByEventAndUser(reg.getEventId(), reg.getUserId());
                    if (team != null) {
                        reg.setTeamId(team.getId());
                        reg.setTeamName(team.getTeamName());
                        reg.setRoleInTeam(team.getTeamLeaderId() == reg.getUserId() ? "TEAM LEADER" : "TEAM MEMBER");
                    }
                }

                list.add(reg);
            }
        } catch (SQLException e) {
            System.err.println("RegistrationDAO.getAllRegistrationsWithDetails SQL Error: " + e.getMessage());
            e.printStackTrace();
        } finally {
            DBConnection.closeResources(conn, pstmt, rs);
        }
        return list;
    }
}
