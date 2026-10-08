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
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/**
 * Model & Hibernate Entity class representing an Event Registration by a Student.
 * Demonstrates Hibernate annotations @Entity, @Table, @Id, @GeneratedValue, @Column, and @ManyToOne.
 */
@Entity
@Table(name = "registrations")
public class Registration {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    // Hibernate Many-to-One Relationship: Many Registrations belong to One User
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // Hibernate Many-to-One Relationship: Many Registrations belong to One Event
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;

    @Column(name = "registration_date", insertable = false, updatable = false)
    private Timestamp registrationDate;

    // Additional transient fields for legacy JDBC DAO compatibility
    @Transient
    private int userId;
    @Transient
    private int eventId;

    @Transient
    private String userName;
    @Transient
    private String userEmail;
    @Transient
    private String eventTitle;
    @Transient
    private Date eventDate;
    @Transient
    private Time eventTime;
    @Transient
    private String venue;

    @Transient
    private String eventType = "INDIVIDUAL";
    @Transient
    private int teamId = 0;
    @Transient
    private String teamName;
    @Transient
    private String roleInTeam;
    @Transient
    private List<TeamMember> teamMembers = new ArrayList<>();

    // Default Constructor
    public Registration() {
    }

    // Basic Constructor
    public Registration(int userId, int eventId) {
        this.userId = userId;
        this.eventId = eventId;
    }

    // Full Constructor
    public Registration(int id, int userId, int eventId, Timestamp registrationDate) {
        this.id = id;
        this.userId = userId;
        this.eventId = eventId;
        this.registrationDate = registrationDate;
    }

    // Getters and Setters with fallback for JDBC compatibility
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
        if (event != null) {
            this.eventId = event.getId();
            this.eventTitle = event.getTitle();
            this.eventDate = event.getEventDate();
            this.eventTime = event.getEventTime();
            this.venue = event.getVenue();
            this.eventType = event.getEventType();
        }
    }

    public int getUserId() {
        if (user != null) return user.getId();
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getEventId() {
        if (event != null) return event.getId();
        return eventId;
    }

    public void setEventId(int eventId) {
        this.eventId = eventId;
    }

    public Timestamp getRegistrationDate() {
        return registrationDate;
    }

    public void setRegistrationDate(Timestamp registrationDate) {
        this.registrationDate = registrationDate;
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

    public String getEventTitle() {
        if (eventTitle != null) return eventTitle;
        return event != null ? event.getTitle() : null;
    }

    public void setEventTitle(String eventTitle) {
        this.eventTitle = eventTitle;
    }

    public Date getEventDate() {
        if (eventDate != null) return eventDate;
        return event != null ? event.getEventDate() : null;
    }

    public void setEventDate(Date eventDate) {
        this.eventDate = eventDate;
    }

    public Time getEventTime() {
        if (eventTime != null) return eventTime;
        return event != null ? event.getEventTime() : null;
    }

    public void setEventTime(Time eventTime) {
        this.eventTime = eventTime;
    }

    public String getVenue() {
        if (venue != null) return venue;
        return event != null ? event.getVenue() : null;
    }

    public void setVenue(String venue) {
        this.venue = venue;
    }

    public String getEventType() {
        if (eventType != null) return eventType;
        return event != null ? event.getEventType() : "INDIVIDUAL";
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public int getTeamId() {
        return teamId;
    }

    public void setTeamId(int teamId) {
        this.teamId = teamId;
    }

    public String getTeamName() {
        return teamName;
    }

    public void setTeamName(String teamName) {
        this.teamName = teamName;
    }

    public String getRoleInTeam() {
        return roleInTeam;
    }

    public void setRoleInTeam(String roleInTeam) {
        this.roleInTeam = roleInTeam;
    }

    public List<TeamMember> getTeamMembers() {
        return teamMembers;
    }

    public void setTeamMembers(List<TeamMember> teamMembers) {
        this.teamMembers = teamMembers;
    }
}
