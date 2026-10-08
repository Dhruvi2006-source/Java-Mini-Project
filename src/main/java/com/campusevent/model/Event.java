package com.campusevent.model;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/**
 * Model & Hibernate Entity class representing a Campus Event (Individual or Team Event).
 * Demonstrates Hibernate annotations @Entity, @Table, @Id, @GeneratedValue, @Column, and @OneToMany.
 */
@Entity
@Table(name = "events")
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "title", nullable = false, length = 150)
    private String title;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "event_date", nullable = false)
    private Date eventDate;

    @Column(name = "event_time", nullable = false)
    private Time eventTime;

    @Column(name = "venue", nullable = false, length = 150)
    private String venue;

    @Column(name = "capacity", nullable = false)
    private int capacity;

    @Column(name = "event_type", nullable = false, length = 20)
    private String eventType = "INDIVIDUAL"; // "INDIVIDUAL" or "TEAM"

    @Column(name = "min_team_size", nullable = false)
    private int minTeamSize = 1;

    @Column(name = "max_team_size", nullable = false)
    private int maxTeamSize = 1;

    @Column(name = "created_at", insertable = false, updatable = false)
    private Timestamp createdAt;

    // Hibernate One-to-Many Relationship: One Event has Many Registrations
    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Registration> registrationsList = new ArrayList<>();

    // Hibernate One-to-Many Relationship: One Event has Many Teams
    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Team> teamsList = new ArrayList<>();

    // Default Constructor
    public Event() {
    }

    // Constructor for Individual Event without ID
    public Event(String title, String description, Date eventDate, Time eventTime, String venue, int capacity) {
        this.title = title;
        this.description = description;
        this.eventDate = eventDate;
        this.eventTime = eventTime;
        this.venue = venue;
        this.capacity = capacity;
        this.eventType = "INDIVIDUAL";
        this.minTeamSize = 1;
        this.maxTeamSize = 1;
    }

    // Constructor with Team Limits
    public Event(String title, String description, Date eventDate, Time eventTime, String venue, int capacity, String eventType, int minTeamSize, int maxTeamSize) {
        this.title = title;
        this.description = description;
        this.eventDate = eventDate;
        this.eventTime = eventTime;
        this.venue = venue;
        this.capacity = capacity;
        this.eventType = eventType != null ? eventType.toUpperCase() : "INDIVIDUAL";
        this.minTeamSize = minTeamSize;
        this.maxTeamSize = maxTeamSize;
    }

    // Full Constructor
    public Event(int id, String title, String description, Date eventDate, Time eventTime, String venue, int capacity, String eventType, int minTeamSize, int maxTeamSize, Timestamp createdAt) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.eventDate = eventDate;
        this.eventTime = eventTime;
        this.venue = venue;
        this.capacity = capacity;
        this.eventType = eventType != null ? eventType.toUpperCase() : "INDIVIDUAL";
        this.minTeamSize = minTeamSize;
        this.maxTeamSize = maxTeamSize;
        this.createdAt = createdAt;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getEventDate() {
        return eventDate;
    }

    public void setEventDate(Date eventDate) {
        this.eventDate = eventDate;
    }

    public Time getEventTime() {
        return eventTime;
    }

    public void setEventTime(Time eventTime) {
        this.eventTime = eventTime;
    }

    public String getVenue() {
        return venue;
    }

    public void setVenue(String venue) {
        this.venue = venue;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType != null ? eventType.toUpperCase() : "INDIVIDUAL";
    }

    public int getMinTeamSize() {
        return minTeamSize;
    }

    public void setMinTeamSize(int minTeamSize) {
        this.minTeamSize = minTeamSize;
    }

    public int getMaxTeamSize() {
        return maxTeamSize;
    }

    public void setMaxTeamSize(int maxTeamSize) {
        this.maxTeamSize = maxTeamSize;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public List<Registration> getRegistrationsList() {
        return registrationsList;
    }

    public void setRegistrationsList(List<Registration> registrationsList) {
        this.registrationsList = registrationsList;
    }

    public List<Team> getTeamsList() {
        return teamsList;
    }

    public void setTeamsList(List<Team> teamsList) {
        this.teamsList = teamsList;
    }

    public boolean isTeamEvent() {
        return "TEAM".equalsIgnoreCase(this.eventType);
    }
}
