package com.campusevent.dao;

import com.campusevent.model.Team;
import com.campusevent.model.TeamMember;
import com.campusevent.model.User;
import com.campusevent.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for Team and Team Member operations.
 * Handles database transactions for atomic team registration and cancellation.
 */
public class TeamDAO {

    private EventDAO eventDAO = new EventDAO();
    private RegistrationDAO registrationDAO = new RegistrationDAO();

    /**
     * Search for a student by email to be added to a team.
     * Validates student existence, role, identity, and current event registration status.
     * @param email Email address of the student
     * @param currentLeaderId User ID of the team leader (logged-in student)
     * @param eventId Event ID
     * @return SearchResult object with User or specific validation error message
     */
    public SearchResult searchMemberByEmail(String email, int currentLeaderId, int eventId) {
        if (email == null || email.trim().isEmpty()) {
            return new SearchResult(false, "Please enter a student email address.");
        }

        email = email.trim().toLowerCase();

        // Email syntax validation
        if (!email.matches("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$")) {
            return new SearchResult(false, "Please enter a valid email address format.");
        }

        String sql = "SELECT id, name, email, role FROM users WHERE LOWER(email) = ?";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DBConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, email);
            rs = pstmt.executeQuery();

            if (!rs.next()) {
                return new SearchResult(false, "Student account not found with email '" + email + "'.");
            }

            int foundUserId = rs.getInt("id");
            String name = rs.getString("name");
            String role = rs.getString("role");

            // Security Check 1: Cannot add Admin accounts
            if (!"STUDENT".equalsIgnoreCase(role)) {
                return new SearchResult(false, "This account belongs to an administrator and cannot be added as a team member.");
            }

            // Security Check 2: Team Leader cannot add themselves again
            if (foundUserId == currentLeaderId) {
                return new SearchResult(false, "You are already the team leader for this team.");
            }

            // Security Check 3: Cannot add student who is already registered for this event
            if (registrationDAO.isUserRegistered(foundUserId, eventId)) {
                return new SearchResult(false, "'" + name + "' (" + email + ") is already registered for this event.");
            }

            User user = new User();
            user.setId(foundUserId);
            user.setName(name);
            user.setEmail(email);
            user.setRole("STUDENT");

            return new SearchResult(true, "Student found.", user);

        } catch (SQLException e) {
            System.err.println("TeamDAO.searchMemberByEmail SQL Error: " + e.getMessage());
            e.printStackTrace();
            return new SearchResult(false, "Database search error. Please try again.");
        } finally {
            DBConnection.closeResources(conn, pstmt, rs);
        }
    }

    /**
     * Atomically register a Team, all Team Members, and Event Registrations using a JDBC Transaction.
     * Guaranteed atomic operation (all or nothing with ROLLBACK on failure).
     */
    public synchronized TransactionResult registerTeamAtomic(int eventId, String teamName, int leaderId, List<Integer> memberIds) {
        Connection conn = null;
        PreparedStatement pstmtTeam = null;
        PreparedStatement pstmtMember = null;
        PreparedStatement pstmtReg = null;
        ResultSet rsTeamKeys = null;

        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false); // BEGIN TRANSACTION

            // 1. Verify Event exists & active
            com.campusevent.model.Event event = eventDAO.getEventById(eventId);
            if (event == null) {
                conn.rollback();
                return new TransactionResult(false, "The requested event does not exist.");
            }

            if (!event.isTeamEvent()) {
                conn.rollback();
                return new TransactionResult(false, "This event is an individual event.");
            }

            // 2. Combine leader + members into total participants list
            List<Integer> allParticipants = new ArrayList<>();
            allParticipants.add(leaderId);
            for (Integer mid : memberIds) {
                if (!allParticipants.contains(mid)) {
                    allParticipants.add(mid);
                }
            }

            int teamSize = allParticipants.size();

            // 3. Validate Team Size limits
            if (teamSize < event.getMinTeamSize()) {
                conn.rollback();
                return new TransactionResult(false, "Team size (" + teamSize + ") is below minimum required (" + event.getMinTeamSize() + " members).");
            }
            if (teamSize > event.getMaxTeamSize()) {
                conn.rollback();
                return new TransactionResult(false, "Team size (" + teamSize + ") exceeds maximum allowed (" + event.getMaxTeamSize() + " members).");
            }

            // 4. Concurrent Capacity Check (1 student = 1 seat)
            int availableSeats = registrationDAO.getAvailableSeats(eventId, event.getCapacity());
            if (availableSeats < teamSize) {
                conn.rollback();
                return new TransactionResult(false, "Not enough available seats for your team. Required: " + teamSize + " seats, Available: " + availableSeats + " seats.");
            }

            // 5. Check if any participant is already registered for this event
            for (Integer uid : allParticipants) {
                if (registrationDAO.isUserRegistered(uid, eventId)) {
                    conn.rollback();
                    return new TransactionResult(false, "One or more selected team members are already registered for this event.");
                }
            }

            // 6. Create Team Record in 'teams' table
            String sqlTeam = "INSERT INTO teams (event_id, team_name, team_leader_id) VALUES (?, ?, ?)";
            pstmtTeam = conn.prepareStatement(sqlTeam, Statement.RETURN_GENERATED_KEYS);
            pstmtTeam.setInt(1, eventId);
            pstmtTeam.setString(2, teamName.trim());
            pstmtTeam.setInt(3, leaderId);
            int rowsTeam = pstmtTeam.executeUpdate();

            if (rowsTeam <= 0) {
                conn.rollback();
                return new TransactionResult(false, "Failed to create team record.");
            }

            rsTeamKeys = pstmtTeam.getGeneratedKeys();
            int teamId = 0;
            if (rsTeamKeys.next()) {
                teamId = rsTeamKeys.getInt(1);
            }

            if (teamId <= 0) {
                conn.rollback();
                return new TransactionResult(false, "Failed to generate team ID.");
            }

            // 7. Insert all team members into 'team_members' table
            String sqlMember = "INSERT INTO team_members (team_id, user_id) VALUES (?, ?)";
            pstmtMember = conn.prepareStatement(sqlMember);
            for (Integer uid : allParticipants) {
                pstmtMember.setInt(1, teamId);
                pstmtMember.setInt(2, uid);
                pstmtMember.addBatch();
            }
            pstmtMember.executeBatch();

            // 8. Insert event registrations into 'registrations' table for all team members
            String sqlReg = "INSERT INTO registrations (user_id, event_id) VALUES (?, ?)";
            pstmtReg = conn.prepareStatement(sqlReg);
            for (Integer uid : allParticipants) {
                pstmtReg.setInt(1, uid);
                pstmtReg.setInt(2, eventId);
                pstmtReg.addBatch();
            }
            pstmtReg.executeBatch();

            // 9. COMMIT TRANSACTION
            conn.commit();
            return new TransactionResult(true, "Team '" + teamName + "' registered successfully with " + teamSize + " members!");

        } catch (Exception e) {
            System.err.println("TeamDAO.registerTeamAtomic Transaction Error: " + e.getMessage());
            e.printStackTrace();
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            return new TransactionResult(false, "Transaction failed: " + e.getMessage());
        } finally {
            DBConnection.closeResources(null, pstmtTeam, rsTeamKeys);
            DBConnection.closeResources(null, pstmtMember);
            DBConnection.closeResources(conn, pstmtReg);
        }
    }

    /**
     * Get Team details by Event ID and Student User ID.
     */
    public Team getTeamByEventAndUser(int eventId, int userId) {
        String sql = "SELECT t.id, t.event_id, t.team_name, t.team_leader_id, t.created_at, " +
                     "u.name AS leader_name, u.email AS leader_email, e.title AS event_title " +
                     "FROM teams t " +
                     "INNER JOIN team_members tm ON t.id = tm.team_id " +
                     "INNER JOIN users u ON t.team_leader_id = u.id " +
                     "INNER JOIN events e ON t.event_id = e.id " +
                     "WHERE t.event_id = ? AND tm.user_id = ?";

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DBConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, eventId);
            pstmt.setInt(2, userId);
            rs = pstmt.executeQuery();

            if (rs.next()) {
                Team team = new Team();
                team.setId(rs.getInt("id"));
                team.setEventId(rs.getInt("event_id"));
                team.setTeamName(rs.getString("team_name"));
                team.setTeamLeaderId(rs.getInt("team_leader_id"));
                team.setCreatedAt(rs.getTimestamp("created_at"));
                team.setTeamLeaderName(rs.getString("leader_name"));
                team.setTeamLeaderEmail(rs.getString("leader_email"));
                team.setEventTitle(rs.getString("event_title"));

                team.setMembers(getTeamMembers(team.getId(), team.getTeamLeaderId()));
                return team;
            }
        } catch (SQLException e) {
            System.err.println("TeamDAO.getTeamByEventAndUser SQL Error: " + e.getMessage());
            e.printStackTrace();
        } finally {
            DBConnection.closeResources(conn, pstmt, rs);
        }
        return null;
    }

    /**
     * Retrieve all members belonging to a team.
     */
    public List<TeamMember> getTeamMembers(int teamId, int leaderId) {
        List<TeamMember> members = new ArrayList<>();
        String sql = "SELECT tm.id, tm.team_id, tm.user_id, tm.joined_at, u.name AS user_name, u.email AS user_email " +
                     "FROM team_members tm " +
                     "INNER JOIN users u ON tm.user_id = u.id " +
                     "WHERE tm.team_id = ? " +
                     "ORDER BY (tm.user_id = ?) DESC, tm.joined_at ASC";

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DBConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, teamId);
            pstmt.setInt(2, leaderId);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                TeamMember member = new TeamMember();
                member.setId(rs.getInt("id"));
                member.setTeamId(rs.getInt("team_id"));
                member.setUserId(rs.getInt("user_id"));
                member.setJoinedAt(rs.getTimestamp("joined_at"));
                member.setUserName(rs.getString("user_name"));
                member.setUserEmail(rs.getString("user_email"));
                member.setLeader(member.getUserId() == leaderId);
                members.add(member);
            }
        } catch (SQLException e) {
            System.err.println("TeamDAO.getTeamMembers SQL Error: " + e.getMessage());
            e.printStackTrace();
        } finally {
            DBConnection.closeResources(conn, pstmt, rs);
        }
        return members;
    }

    /**
     * Atomically cancel team registration by Team Leader.
     * Removes team, team members, and event registrations for all team members in one transaction.
     */
    public boolean cancelTeamRegistrationAtomic(int eventId, int leaderUserId) {
        Connection conn = null;
        PreparedStatement pstmtSelectTeam = null;
        PreparedStatement pstmtSelectMembers = null;
        PreparedStatement pstmtDelReg = null;
        PreparedStatement pstmtDelTeam = null;
        ResultSet rsTeam = null;
        ResultSet rsMembers = null;

        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false); // BEGIN TRANSACTION

            // 1. Verify team exists and user is team leader
            String sqlTeam = "SELECT id FROM teams WHERE event_id = ? AND team_leader_id = ?";
            pstmtSelectTeam = conn.prepareStatement(sqlTeam);
            pstmtSelectTeam.setInt(1, eventId);
            pstmtSelectTeam.setInt(2, leaderUserId);
            rsTeam = pstmtSelectTeam.executeQuery();

            if (!rsTeam.next()) {
                conn.rollback();
                return false;
            }

            int teamId = rsTeam.getInt("id");

            // 2. Fetch all user IDs in this team
            List<Integer> memberUserIds = new ArrayList<>();
            String sqlMembers = "SELECT user_id FROM team_members WHERE team_id = ?";
            pstmtSelectMembers = conn.prepareStatement(sqlMembers);
            pstmtSelectMembers.setInt(1, teamId);
            rsMembers = pstmtSelectMembers.executeQuery();

            while (rsMembers.next()) {
                memberUserIds.add(rsMembers.getInt("user_id"));
            }

            // 3. Delete event registrations for all team members
            String sqlDelReg = "DELETE FROM registrations WHERE event_id = ? AND user_id = ?";
            pstmtDelReg = conn.prepareStatement(sqlDelReg);
            for (Integer uid : memberUserIds) {
                pstmtDelReg.setInt(1, eventId);
                pstmtDelReg.setInt(2, uid);
                pstmtDelReg.addBatch();
            }
            pstmtDelReg.executeBatch();

            // 4. Delete Team (FK CASCADE automatically deletes team_members)
            String sqlDelTeam = "DELETE FROM teams WHERE id = ?";
            pstmtDelTeam = conn.prepareStatement(sqlDelTeam);
            pstmtDelTeam.setInt(1, teamId);
            pstmtDelTeam.executeUpdate();

            // 5. COMMIT TRANSACTION
            conn.commit();
            return true;

        } catch (Exception e) {
            System.err.println("TeamDAO.cancelTeamRegistrationAtomic Error: " + e.getMessage());
            e.printStackTrace();
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            return false;
        } finally {
            DBConnection.closeResources(null, pstmtSelectTeam, rsTeam);
            DBConnection.closeResources(null, pstmtSelectMembers, rsMembers);
            DBConnection.closeResources(null, pstmtDelReg);
            DBConnection.closeResources(conn, pstmtDelTeam);
        }
    }

    // Helper Result Classes
    public static class SearchResult {
        public boolean success;
        public String message;
        public User user;

        public SearchResult(boolean success, String message) {
            this.success = success;
            this.message = message;
        }

        public SearchResult(boolean success, String message, User user) {
            this.success = success;
            this.message = message;
            this.user = user;
        }
    }

    public static class TransactionResult {
        public boolean success;
        public String message;

        public TransactionResult(boolean success, String message) {
            this.success = success;
            this.message = message;
        }
    }
}
