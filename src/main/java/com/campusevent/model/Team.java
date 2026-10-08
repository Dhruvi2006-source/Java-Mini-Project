package com.campusevent.model;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/**
 * Model & Hibernate Entity class representing a Team registered for a Team Event.
 * Demonstrates Hibernate annotations @Entity, @Table, @Id, @GeneratedValue, @ManyToOne, and @OneToMany.
 */
@Entity
@Table(name = "teams")
public class Team {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "team_name", nullable = false, length = 100)
    private String teamName;

    // Hibernate Many-to-One Relationship: Many Teams belong to One Event
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;

    // Hibernate Many-to-One Relationship: Many Teams led by One User (Team Leader)
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "team_leader_id", nullable = false)
    private User teamLeader;

    @Column(name = "created_at", insertable = false, updatable = false)
    private Timestamp createdAt;

    // Hibernate One-to-Many Relationship: One Team has Many TeamMembers
    @OneToMany(mappedBy = "team", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<TeamMember> membersList = new ArrayList<>();

    // Transient fields for legacy JDBC DAO compatibility
    @Transient
    private int eventId;
    @Transient
    private int teamLeaderId;
    @Transient
    private String teamLeaderName;
    @Transient
    private String teamLeaderEmail;
    @Transient
    private String eventTitle;
    @Transient
    private List<TeamMember> members = new ArrayList<>();

    public Team() {
    }

    public Team(int eventId, String teamName, int teamLeaderId) {
        this.eventId = eventId;
        this.teamName = teamName;
        this.teamLeaderId = teamLeaderId;
    }

    public Team(int id, int eventId, String teamName, int teamLeaderId, Timestamp createdAt) {
        this.id = id;
        this.eventId = eventId;
        this.teamName = teamName;
        this.teamLeaderId = teamLeaderId;
        this.createdAt = createdAt;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTeamName() {
        return teamName;
    }

    public void setTeamName(String teamName) {
        this.teamName = teamName;
    }

    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
        if (event != null) {
            this.eventId = event.getId();
            this.eventTitle = event.getTitle();
        }
    }

    public User getTeamLeader() {
        return teamLeader;
    }

    public void setTeamLeader(User teamLeader) {
        this.teamLeader = teamLeader;
        if (teamLeader != null) {
            this.teamLeaderId = teamLeader.getId();
            this.teamLeaderName = teamLeader.getName();
            this.teamLeaderEmail = teamLeader.getEmail();
        }
    }

    public int getEventId() {
        if (event != null) return event.getId();
        return eventId;
    }

    public void setEventId(int eventId) {
        this.eventId = eventId;
    }

    public int getTeamLeaderId() {
        if (teamLeader != null) return teamLeader.getId();
        return teamLeaderId;
    }

    public void setTeamLeaderId(int teamLeaderId) {
        this.teamLeaderId = teamLeaderId;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public String getTeamLeaderName() {
        if (teamLeaderName != null) return teamLeaderName;
        return teamLeader != null ? teamLeader.getName() : null;
    }

    public void setTeamLeaderName(String teamLeaderName) {
        this.teamLeaderName = teamLeaderName;
    }

    public String getTeamLeaderEmail() {
        if (teamLeaderEmail != null) return teamLeaderEmail;
        return teamLeader != null ? teamLeader.getEmail() : null;
    }

    public void setTeamLeaderEmail(String teamLeaderEmail) {
        this.teamLeaderEmail = teamLeaderEmail;
    }

    public String getEventTitle() {
        if (eventTitle != null) return eventTitle;
        return event != null ? event.getTitle() : null;
    }

    public void setEventTitle(String eventTitle) {
        this.eventTitle = eventTitle;
    }

    public List<TeamMember> getMembers() {
        if (membersList != null && !membersList.isEmpty()) return membersList;
        return members;
    }

    public void setMembers(List<TeamMember> members) {
        this.members = members;
    }

    public List<TeamMember> getMembersList() {
        return membersList;
    }

    public void setMembersList(List<TeamMember> membersList) {
        this.membersList = membersList;
    }
}
