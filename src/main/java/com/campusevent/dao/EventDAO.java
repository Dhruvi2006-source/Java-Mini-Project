package com.campusevent.dao;

import com.campusevent.model.Event;
import com.campusevent.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for Event database operations.
 */
public class EventDAO {

    /**
     * Retrieve all events from database ordered by date.
     * @return List of Event objects
     */
    public List<Event> getAllEvents() {
        List<Event> events = new ArrayList<>();
        String sql = "SELECT id, title, description, event_date, event_time, venue, capacity, event_type, min_team_size, max_team_size, created_at FROM events ORDER BY event_date ASC, event_time ASC";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DBConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                events.add(mapResultSetToEvent(rs));
            }
        } catch (SQLException e) {
            System.err.println("EventDAO.getAllEvents SQL Error: " + e.getMessage());
            e.printStackTrace();
        } finally {
            DBConnection.closeResources(conn, pstmt, rs);
        }
        return events;
    }

    /**
     * Search events by title keyword and/or filter by venue.
     * @param keyword Search term for title/description
     * @param venue Filter by venue (optional)
     * @return Filtered list of Event objects
     */
    public List<Event> searchEvents(String keyword, String venue) {
        List<Event> events = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT id, title, description, event_date, event_time, venue, capacity, event_type, min_team_size, max_team_size, created_at FROM events WHERE 1=1 ");
        
        boolean hasKeyword = (keyword != null && !keyword.trim().isEmpty());
        boolean hasVenue = (venue != null && !venue.trim().isEmpty() && !"ALL".equalsIgnoreCase(venue));

        if (hasKeyword) {
            sql.append("AND (LOWER(title) LIKE ? OR LOWER(description) LIKE ?) ");
        }
        if (hasVenue) {
            sql.append("AND LOWER(venue) = ? ");
        }

        sql.append("ORDER BY event_date ASC, event_time ASC");

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DBConnection.getConnection();
            pstmt = conn.prepareStatement(sql.toString());

            int paramIndex = 1;
            if (hasKeyword) {
                String searchPattern = "%" + keyword.trim().toLowerCase() + "%";
                pstmt.setString(paramIndex++, searchPattern);
                pstmt.setString(paramIndex++, searchPattern);
            }
            if (hasVenue) {
                pstmt.setString(paramIndex++, venue.trim().toLowerCase());
            }

            rs = pstmt.executeQuery();

            while (rs.next()) {
                events.add(mapResultSetToEvent(rs));
            }
        } catch (SQLException e) {
            System.err.println("EventDAO.searchEvents SQL Error: " + e.getMessage());
            e.printStackTrace();
        } finally {
            DBConnection.closeResources(conn, pstmt, rs);
        }
        return events;
    }

    /**
     * Retrieve a specific event by ID.
     * @param id Event ID
     * @return Event object if found, null otherwise
     */
    public Event getEventById(int id) {
        String sql = "SELECT id, title, description, event_date, event_time, venue, capacity, event_type, min_team_size, max_team_size, created_at FROM events WHERE id = ?";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DBConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, id);
            rs = pstmt.executeQuery();

            if (rs.next()) {
                return mapResultSetToEvent(rs);
            }
        } catch (SQLException e) {
            System.err.println("EventDAO.getEventById SQL Error: " + e.getMessage());
            e.printStackTrace();
        } finally {
            DBConnection.closeResources(conn, pstmt, rs);
        }
        return null;
    }

    /**
     * Create a new event (Individual or Team Event).
     * @param event Event object
     * @return true if creation succeeded, false otherwise
     */
    public boolean createEvent(Event event) {
        String sql = "INSERT INTO events (title, description, event_date, event_time, venue, capacity, event_type, min_team_size, max_team_size) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            conn = DBConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, event.getTitle());
            pstmt.setString(2, event.getDescription());
            pstmt.setDate(3, event.getEventDate());
            pstmt.setTime(4, event.getEventTime());
            pstmt.setString(5, event.getVenue());
            pstmt.setInt(6, event.getCapacity());
            pstmt.setString(7, event.getEventType());
            pstmt.setInt(8, event.getMinTeamSize());
            pstmt.setInt(9, event.getMaxTeamSize());

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("EventDAO.createEvent SQL Error: " + e.getMessage());
            e.printStackTrace();
            return false;
        } finally {
            DBConnection.closeResources(conn, pstmt);
        }
    }

    /**
     * Update an existing event details (Admin functionality).
     * @param event Event object containing updated fields
     * @return true if update succeeded, false otherwise
     */
    public boolean updateEvent(Event event) {
        String sql = "UPDATE events SET title = ?, description = ?, event_date = ?, event_time = ?, venue = ?, capacity = ?, event_type = ?, min_team_size = ?, max_team_size = ? WHERE id = ?";
        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            conn = DBConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, event.getTitle());
            pstmt.setString(2, event.getDescription());
            pstmt.setDate(3, event.getEventDate());
            pstmt.setTime(4, event.getEventTime());
            pstmt.setString(5, event.getVenue());
            pstmt.setInt(6, event.getCapacity());
            pstmt.setString(7, event.getEventType());
            pstmt.setInt(8, event.getMinTeamSize());
            pstmt.setInt(9, event.getMaxTeamSize());
            pstmt.setInt(10, event.getId());

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("EventDAO.updateEvent SQL Error: " + e.getMessage());
            e.printStackTrace();
            return false;
        } finally {
            DBConnection.closeResources(conn, pstmt);
        }
    }

    /**
     * Delete an event by ID.
     * Referential integrity cascades registration and team deletion safely.
     * @param id Event ID to delete
     * @return true if deletion succeeded, false otherwise
     */
    public boolean deleteEvent(int id) {
        String sql = "DELETE FROM events WHERE id = ?";
        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            conn = DBConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, id);

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("EventDAO.deleteEvent SQL Error: " + e.getMessage());
            e.printStackTrace();
            return false;
        } finally {
            DBConnection.closeResources(conn, pstmt);
        }
    }

    /**
     * Get total count of events published in the system.
     * @return Total events count
     */
    public int getTotalEventsCount() {
        String sql = "SELECT COUNT(*) FROM events";
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
            System.err.println("EventDAO.getTotalEventsCount SQL Error: " + e.getMessage());
            e.printStackTrace();
        } finally {
            DBConnection.closeResources(conn, pstmt, rs);
        }
        return 0;
    }

    /**
     * Get count of upcoming events (scheduled today or in future).
     * @return Upcoming events count
     */
    public int getUpcomingEventsCount() {
        String sql = "SELECT COUNT(*) FROM events WHERE event_date >= CURRENT_DATE()";
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
            System.err.println("EventDAO.getUpcomingEventsCount SQL Error: " + e.getMessage());
            e.printStackTrace();
        } finally {
            DBConnection.closeResources(conn, pstmt, rs);
        }
        return 0;
    }

    /**
     * Get distinct list of venues for filter dropdown.
     * @return List of venue names
     */
    public List<String> getAllVenues() {
        List<String> venues = new ArrayList<>();
        String sql = "SELECT DISTINCT venue FROM events ORDER BY venue ASC";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DBConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                venues.add(rs.getString("venue"));
            }
        } catch (SQLException e) {
            System.err.println("EventDAO.getAllVenues SQL Error: " + e.getMessage());
            e.printStackTrace();
        } finally {
            DBConnection.closeResources(conn, pstmt, rs);
        }
        return venues;
    }

    private Event mapResultSetToEvent(ResultSet rs) throws SQLException {
        Event event = new Event();
        event.setId(rs.getInt("id"));
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
            event.setMinTeamSize(1);
            event.setMaxTeamSize(1);
        }

        event.setCreatedAt(rs.getTimestamp("created_at"));
        return event;
    }
}
