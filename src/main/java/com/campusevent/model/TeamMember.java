package com.campusevent.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.sql.Timestamp;

/**
 * Model & Hibernate Entity class representing a Member belonging to a Team.
 * Demonstrates Hibernate annotations @Entity, @Table, @Id, @GeneratedValue, and @ManyToOne.
 */
@Entity
@Table(name = "team_members")
public class TeamMember {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    // Hibernate Many-to-One Relationship: Many TeamMembers belong to One Team
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "team_id", nullable = false)
    private Team team;

    // Hibernate Many-to-One Relationship: Many TeamMembers belong to One User
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "joined_at", insertable = false, updatable = false)
    private Timestamp joinedAt;

    // Transient fields for legacy JDBC DAO compatibility
    @Transient
    private int teamId;
    @Transient
    private int userId;
    @Transient
    private String userName;
    @Transient
    private String userEmail;
    @Transient
    private boolean isLeader;

    public TeamMember() {
    }

    public TeamMember(int userId, String userName, String userEmail, boolean isLeader) {
        this.userId = userId;
        this.userName = userName;
        this.userEmail = userEmail;
        this.isLeader = isLeader;
    }

    public TeamMember(int id, int teamId, int userId, Timestamp joinedAt) {
        this.id = id;
        this.teamId = teamId;
        this.userId = userId;
        this.joinedAt = joinedAt;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Team getTeam() {
        return team;
    }

    public void setTeam(Team team) {
        this.team = team;
        if (team != null) {
            this.teamId = team.getId();
        }
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
        if (user != null) {
            this.userId = user.getId();
            this.userName = user.getName();
            this.userEmail = user.getEmail();
        }
    }

    public int getTeamId() {
        if (team != null) return team.getId();
        return teamId;
    }

    public void setTeamId(int teamId) {
        this.teamId = teamId;
    }

    public int getUserId() {
        if (user != null) return user.getId();
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public Timestamp getJoinedAt() {
        return joinedAt;
    }

    public void setJoinedAt(Timestamp joinedAt) {
        this.joinedAt = joinedAt;
    }

    public String getUserName() {
        if (userName != null) return userName;
        return user != null ? user.getName() : null;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserEmail() {
        if (userEmail != null) return userEmail;
        return user != null ? user.getEmail() : null;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public boolean isLeader() {
        if (team != null && user != null) {
            return team.getTeamLeaderId() == user.getId();
        }
        return isLeader;
    }

    public void setLeader(boolean leader) {
        isLeader = leader;
    }
}
