package com.campusevent.service;

import com.campusevent.model.Event;
import com.campusevent.model.Registration;
import com.campusevent.model.Team;
import com.campusevent.model.TeamMember;
import com.campusevent.model.User;
import com.campusevent.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.sql.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Service class performing real Hibernate ORM & HQL operations for Admin Event Analytics.
 * Demonstrates HQL (Hibernate Query Language) aggregations, join queries, and entity navigation.
 */
public class EventAnalyticsService {

    /**
     * DTO for displaying Popular Events in HQL Analytics.
     */
    public static class PopularEventDTO {
        private Event event;
        private long registrationCount;

        public PopularEventDTO(Event event, long registrationCount) {
            this.event = event;
            this.registrationCount = registrationCount;
        }

        public Event getEvent() { return event; }
        public long getRegistrationCount() { return registrationCount; }
    }

    /**
     * DTO for Event Registration Summary table.
     */
    public static class EventSummaryDTO {
        private Event event;
        private long enrolledCount;
        private int availableSeats;
        private double percentage;

        public EventSummaryDTO(Event event, long enrolledCount) {
            this.event = event;
            this.enrolledCount = enrolledCount;
            this.availableSeats = Math.max(0, event.getCapacity() - (int) enrolledCount);
            this.percentage = event.getCapacity() > 0 ? (enrolledCount * 100.0) / event.getCapacity() : 0.0;
        }

        public Event getEvent() { return event; }
        public long getEnrolledCount() { return enrolledCount; }
        public int getAvailableSeats() { return availableSeats; }
        public double getPercentage() { return percentage; }
    }

    /**
     * DTO for Detailed Event Analytics Page.
     */
    public static class DetailedEventAnalyticsDTO {
        private Event event;
        private long totalRegistered;
        private int availableSeats;
        private double registrationPercentage;

        // Team Analytics Specific Fields
        private boolean isTeamEvent;
        private long totalTeams;
        private long totalTeamParticipants;
        private double averageTeamSize;
        private long largestTeamSize;
        private long smallestTeamSize;

        private List<Registration> registrationsList = new ArrayList<>();
        private List<Team> teamsList = new ArrayList<>();

        public Event getEvent() { return event; }
        public void setEvent(Event event) { this.event = event; }

        public long getTotalRegistered() { return totalRegistered; }
        public void setTotalRegistered(long totalRegistered) { this.totalRegistered = totalRegistered; }

        public int getAvailableSeats() { return availableSeats; }
        public void setAvailableSeats(int availableSeats) { this.availableSeats = availableSeats; }

        public double getRegistrationPercentage() { return registrationPercentage; }
        public void setRegistrationPercentage(double registrationPercentage) { this.registrationPercentage = registrationPercentage; }

        public boolean isTeamEvent() { return isTeamEvent; }
        public void setTeamEvent(boolean teamEvent) { isTeamEvent = teamEvent; }

        public long getTotalTeams() { return totalTeams; }
        public void setTotalTeams(long totalTeams) { this.totalTeams = totalTeams; }

        public long getTotalTeamParticipants() { return totalTeamParticipants; }
        public void setTotalTeamParticipants(long totalTeamParticipants) { this.totalTeamParticipants = totalTeamParticipants; }

        public double getAverageTeamSize() { return averageTeamSize; }
        public void setAverageTeamSize(double averageTeamSize) { this.averageTeamSize = averageTeamSize; }

        public long getLargestTeamSize() { return largestTeamSize; }
        public void setLargestTeamSize(long largestTeamSize) { this.largestTeamSize = largestTeamSize; }

        public long getSmallestTeamSize() { return smallestTeamSize; }
        public void setSmallestTeamSize(long smallestTeamSize) { this.smallestTeamSize = smallestTeamSize; }

        public List<Registration> getRegistrationsList() { return registrationsList; }
        public void setRegistrationsList(List<Registration> registrationsList) { this.registrationsList = registrationsList; }

        public List<Team> getTeamsList() { return teamsList; }
        public void setTeamsList(List<Team> teamsList) { this.teamsList = teamsList; }
    }

    /**
     * HQL Query 4: Total Student Accounts Count.
     */
    public long getTotalStudentsCount() {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            String hql = "SELECT COUNT(u.id) FROM User u WHERE UPPER(u.role) = 'STUDENT'";
            Query<Long> query = session.createQuery(hql, Long.class);
            long count = query.uniqueResult();
            tx.commit();
            return count;
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            System.err.println("EventAnalyticsService.getTotalStudentsCount HQL Error: " + e.getMessage());
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * HQL Query 4: Total Published Events Count.
     */
    public long getTotalEventsCount() {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            String hql = "SELECT COUNT(e.id) FROM Event e";
            Query<Long> query = session.createQuery(hql, Long.class);
            long count = query.uniqueResult();
            tx.commit();
            return count;
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            System.err.println("EventAnalyticsService.getTotalEventsCount HQL Error: " + e.getMessage());
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * HQL Query 4: Total Registrations Count across all events.
     */
    public long getTotalRegistrationsCount() {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            String hql = "SELECT COUNT(r.id) FROM Registration r";
            Query<Long> query = session.createQuery(hql, Long.class);
            long count = query.uniqueResult();
            tx.commit();
            return count;
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            System.err.println("EventAnalyticsService.getTotalRegistrationsCount HQL Error: " + e.getMessage());
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * HQL Query 4 & Query 2: Count of Upcoming Events using named parameters.
     */
    public long getUpcomingEventsCount() {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            Date today = new Date(System.currentTimeMillis());
            String hql = "SELECT COUNT(e.id) FROM Event e WHERE e.eventDate >= :today";
            Query<Long> query = session.createQuery(hql, Long.class);
            query.setParameter("today", today);
            long count = query.uniqueResult();
            tx.commit();
            return count;
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            System.err.println("EventAnalyticsService.getUpcomingEventsCount HQL Error: " + e.getMessage());
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * HQL Query 1: Retrieve Most Popular Events based on total registrations.
     */
    public List<PopularEventDTO> getMostPopularEvents(int limit) {
        List<PopularEventDTO> list = new ArrayList<>();
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            String hql = "SELECT e, COUNT(r.id) AS regCount " +
                         "FROM Event e " +
                         "LEFT JOIN e.registrationsList r " +
                         "GROUP BY e.id, e.title, e.description, e.eventDate, e.eventTime, e.venue, e.capacity, e.eventType, e.minTeamSize, e.maxTeamSize, e.createdAt " +
                         "ORDER BY regCount DESC";

            Query<Object[]> query = session.createQuery(hql, Object[].class);
            query.setMaxResults(limit);
            List<Object[]> results = query.getResultList();

            for (Object[] row : results) {
                Event event = (Event) row[0];
                Long count = (Long) row[1];
                list.add(new PopularEventDTO(event, count != null ? count : 0L));
            }
            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            System.err.println("EventAnalyticsService.getMostPopularEvents HQL Error: " + e.getMessage());
            e.printStackTrace();
        }
        return list;
    }

    /**
     * HQL Query 2: Retrieve Upcoming Events with named parameter.
     */
    public List<Event> getUpcomingEvents(int limit) {
        List<Event> list = new ArrayList<>();
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            Date today = new Date(System.currentTimeMillis());
            String hql = "FROM Event e WHERE e.eventDate >= :today ORDER BY e.eventDate ASC, e.eventTime ASC";
            Query<Event> query = session.createQuery(hql, Event.class);
            query.setParameter("today", today);
            if (limit > 0) query.setMaxResults(limit);
            list = query.getResultList();
            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            System.err.println("EventAnalyticsService.getUpcomingEvents HQL Error: " + e.getMessage());
            e.printStackTrace();
        }
        return list;
    }

    /**
     * HQL Query 3 & Optional Filtering: Event Registration Summary with keyword & type filter.
     */
    public List<EventSummaryDTO> getEventRegistrationSummary(String keyword, String eventTypeFilter) {
        List<EventSummaryDTO> list = new ArrayList<>();
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            
            StringBuilder hql = new StringBuilder(
                "SELECT e, COUNT(r.id) AS enrolled " +
                "FROM Event e " +
                "LEFT JOIN e.registrationsList r " +
                "WHERE 1=1 "
            );

            boolean hasKeyword = (keyword != null && !keyword.trim().isEmpty());
            boolean hasTypeFilter = (eventTypeFilter != null && !eventTypeFilter.trim().isEmpty() && !"ALL".equalsIgnoreCase(eventTypeFilter));

            if (hasKeyword) {
                hql.append("AND (LOWER(e.title) LIKE :keyword OR LOWER(e.description) LIKE :keyword) ");
            }
            if (hasTypeFilter) {
                hql.append("AND UPPER(e.eventType) = :eventType ");
            }

            hql.append("GROUP BY e.id, e.title, e.description, e.eventDate, e.eventTime, e.venue, e.capacity, e.eventType, e.minTeamSize, e.maxTeamSize, e.createdAt ");
            hql.append("ORDER BY e.eventDate ASC, e.eventTime ASC");

            Query<Object[]> query = session.createQuery(hql.toString(), Object[].class);

            if (hasKeyword) {
                query.setParameter("keyword", "%" + keyword.trim().toLowerCase() + "%");
            }
            if (hasTypeFilter) {
                query.setParameter("eventType", eventTypeFilter.trim().toUpperCase());
            }

            List<Object[]> results = query.getResultList();

            for (Object[] row : results) {
                Event event = (Event) row[0];
                Long count = (Long) row[1];
                list.add(new EventSummaryDTO(event, count != null ? count : 0L));
            }
            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            System.err.println("EventAnalyticsService.getEventRegistrationSummary HQL Error: " + e.getMessage());
            e.printStackTrace();
        }
        return list;
    }

    /**
     * HQL Query 5 & Team Event Analytics: Detailed Analytics for a single event.
     */
    public DetailedEventAnalyticsDTO getEventAnalyticsDetails(int eventId) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();

            Event event = session.get(Event.class, eventId);
            if (event == null) {
                tx.commit();
                return null;
            }

            DetailedEventAnalyticsDTO dto = new DetailedEventAnalyticsDTO();
            dto.setEvent(event);
            dto.setTeamEvent(event.isTeamEvent());

            // 1. Total Enrolled Students via HQL
            String hqlRegCount = "SELECT COUNT(r.id) FROM Registration r WHERE r.event.id = :eventId";
            Query<Long> qRegCount = session.createQuery(hqlRegCount, Long.class);
            qRegCount.setParameter("eventId", eventId);
            long registeredCount = qRegCount.uniqueResult();

            dto.setTotalRegistered(registeredCount);
            dto.setAvailableSeats(Math.max(0, event.getCapacity() - (int) registeredCount));
            dto.setRegistrationPercentage(event.getCapacity() > 0 ? (registeredCount * 100.0) / event.getCapacity() : 0.0);

            // 2. Fetch Registration details with User ORM mapping
            String hqlRegs = "FROM Registration r WHERE r.event.id = :eventId ORDER BY r.registrationDate DESC";
            Query<Registration> qRegs = session.createQuery(hqlRegs, Registration.class);
            qRegs.setParameter("eventId", eventId);
            List<Registration> regList = qRegs.getResultList();

            // Populate Team Info if Team Event
            if (event.isTeamEvent()) {
                // Total Teams via HQL
                String hqlTeamsCount = "SELECT COUNT(t.id) FROM Team t WHERE t.event.id = :eventId";
                Query<Long> qTeamsCount = session.createQuery(hqlTeamsCount, Long.class);
                qTeamsCount.setParameter("eventId", eventId);
                long teamsCount = qTeamsCount.uniqueResult();
                dto.setTotalTeams(teamsCount);

                // Total Team Participants via HQL
                String hqlParticipants = "SELECT COUNT(tm.id) FROM TeamMember tm WHERE tm.team.event.id = :eventId";
                Query<Long> qParticipants = session.createQuery(hqlParticipants, Long.class);
                qParticipants.setParameter("eventId", eventId);
                long participantsCount = qParticipants.uniqueResult();
                dto.setTotalTeamParticipants(participantsCount);

                // Team size distribution via HQL (Min, Max, Avg Team Size)
                String hqlTeamSizes = "SELECT COUNT(tm.id) FROM TeamMember tm WHERE tm.team.event.id = :eventId GROUP BY tm.team.id";
                Query<Long> qSizes = session.createQuery(hqlTeamSizes, Long.class);
                qSizes.setParameter("eventId", eventId);
                List<Long> sizes = qSizes.getResultList();

                if (sizes != null && !sizes.isEmpty()) {
                    long maxS = 0;
                    long minS = Long.MAX_VALUE;
                    long totalS = 0;
                    for (Long s : sizes) {
                        if (s > maxS) maxS = s;
                        if (s < minS) minS = s;
                        totalS += s;
                    }
                    dto.setLargestTeamSize(maxS);
                    dto.setSmallestTeamSize(minS);
                    dto.setAverageTeamSize((double) totalS / sizes.size());
                } else {
                    dto.setLargestTeamSize(0);
                    dto.setSmallestTeamSize(0);
                    dto.setAverageTeamSize(0.0);
                }

                // Fetch Teams List
                String hqlTeams = "FROM Team t WHERE t.event.id = :eventId ORDER BY t.createdAt DESC";
                Query<Team> qTeams = session.createQuery(hqlTeams, Team.class);
                qTeams.setParameter("eventId", eventId);
                List<Team> teamsList = qTeams.getResultList();
                dto.setTeamsList(teamsList);

                // Map Team details onto Registration objects for display
                Map<Integer, Team> userTeamMap = new HashMap<>();
                for (Team t : teamsList) {
                    if (t.getMembersList() != null) {
                        for (TeamMember tm : t.getMembersList()) {
                            if (tm.getUser() != null) {
                                userTeamMap.put(tm.getUser().getId(), t);
                            }
                        }
                    }
                }

                for (Registration r : regList) {
                    if (r.getUser() != null && userTeamMap.containsKey(r.getUser().getId())) {
                        Team t = userTeamMap.get(r.getUser().getId());
                        r.setTeamId(t.getId());
                        r.setTeamName(t.getTeamName());
                        r.setRoleInTeam(t.getTeamLeader() != null && t.getTeamLeader().getId() == r.getUser().getId() ? "TEAM LEADER" : "TEAM MEMBER");
                    }
                }
            }

            dto.setRegistrationsList(regList);

            tx.commit();
            return dto;

        } catch (Exception e) {
            if (tx != null) tx.rollback();
            System.err.println("EventAnalyticsService.getEventAnalyticsDetails HQL Error: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
}
