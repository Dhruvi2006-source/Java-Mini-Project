# Hibernate Event Analytics & ORM Module Documentation

## 1. Overview of Hibernate
Hibernate ORM (Object/Relational Mapping) is a Java persistence framework that simplifies database interactions by mapping Java domain model objects (`POJO`s) to relational database tables.

In the **Campus Event Management System**, Hibernate is integrated as a separate persistence and analytical engine for the **Administrator Module**, while the core student event registration engine retains its multithreaded **JDBC** implementation.

---

## 2. Dual Architecture (JDBC + Hibernate)

```text
STUDENT EVENT REGISTRATION SIDE (JDBC Engine):
JSP Frontend
  └── Servlet (RegisterEventServlet / RegisterTeamServlet)
       └── Java Event Registration & Rule Engine (Synchronized Seat Allocation)
            └── DAO Layer (RegistrationDAO / TeamDAO)
                 └── JDBC (PreparedStatement & Transactions)
                      └── MySQL Database (campus_event_db)

ADMIN ANALYTICS SIDE (Hibernate ORM Engine):
JSP Frontend (admin/analytics.jsp & admin/event-analytics.jsp)
  └── Admin Analytics Servlets (AdminAnalyticsServlet / AdminEventAnalyticsServlet)
       └── EventAnalyticsService
            └── Hibernate Session & SessionFactory (HibernateUtil)
                 └── HQL Queries & JPA Annotations
                      └── MySQL Database (campus_event_db)
```

Both modules interact with the **same underlying MySQL database (`campus_event_db`)** without duplicate tables or conflicting schemas.

---

## 3. Hibernate Configuration (`hibernate.cfg.xml`)
The configuration defines connection parameters, dialect, SQL formatting, and registered annotated entity classes.

```xml
<hibernate-configuration>
    <session-factory>
        <property name="hibernate.connection.driver_class">com.mysql.cj.jdbc.Driver</property>
        <property name="hibernate.connection.url">jdbc:mysql://localhost:3306/campus_event_db?useSSL=false&amp;allowPublicKeyRetrieval=true&amp;serverTimezone=UTC</property>
        <property name="hibernate.connection.username">root</property>
        <property name="hibernate.connection.password"></property>

        <property name="hibernate.dialect">org.hibernate.dialect.MySQL8Dialect</property>
        <property name="hibernate.show_sql">true</property>
        <property name="hibernate.format_sql">true</property>
        <property name="hibernate.hbm2ddl.auto">validate</property>

        <mapping class="com.campusevent.model.User"/>
        <mapping class="com.campusevent.model.Event"/>
        <mapping class="com.campusevent.model.Registration"/>
        <mapping class="com.campusevent.model.Team"/>
        <mapping class="com.campusevent.model.TeamMember"/>
    </session-factory>
</hibernate-configuration>
```

> [!IMPORTANT]
> `hibernate.hbm2ddl.auto` is configured as `validate` to ensure Hibernate inspects table structures without altering or dropping existing database tables.

---

## 4. Hibernate SessionFactory (`HibernateUtil.java`)
`HibernateUtil` provides a singleton, thread-safe `SessionFactory` initialized once during web application startup.

```java
public class HibernateUtil {
    private static final SessionFactory sessionFactory = buildSessionFactory();

    private static SessionFactory buildSessionFactory() {
        return new Configuration().configure("hibernate.cfg.xml").buildSessionFactory();
    }

    public static SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    public static void shutdown() {
        if (sessionFactory != null && !sessionFactory.isClosed()) {
            sessionFactory.close();
        }
    }
}
```

---

## 5. Entity Mappings & JPA Annotations

### User Entity (`User.java`)
- `@Entity` and `@Table(name = "users")`
- `@Id` and `@GeneratedValue(strategy = GenerationType.IDENTITY)`
- `@OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)` -> `registrationsList`

### Event Entity (`Event.java`)
- `@Entity` and `@Table(name = "events")`
- `@OneToMany(mappedBy = "event", cascade = CascadeType.ALL, fetch = FetchType.LAZY)` -> `registrationsList`
- `@OneToMany(mappedBy = "event", cascade = CascadeType.ALL, fetch = FetchType.LAZY)` -> `teamsList`

### Registration Entity (`Registration.java`)
- `@Entity` and `@Table(name = "registrations")`
- `@ManyToOne(fetch = FetchType.EAGER)` `@JoinColumn(name = "user_id")` -> `User user`
- `@ManyToOne(fetch = FetchType.EAGER)` `@JoinColumn(name = "event_id")` -> `Event event`

### Team Entity (`Team.java`)
- `@Entity` and `@Table(name = "teams")`
- `@ManyToOne(fetch = FetchType.EAGER)` `@JoinColumn(name = "event_id")` -> `Event event`
- `@ManyToOne(fetch = FetchType.EAGER)` `@JoinColumn(name = "team_leader_id")` -> `User teamLeader`
- `@OneToMany(mappedBy = "team", cascade = CascadeType.ALL, fetch = FetchType.LAZY)` -> `membersList`

### TeamMember Entity (`TeamMember.java`)
- `@Entity` and `@Table(name = "team_members")`
- `@ManyToOne(fetch = FetchType.EAGER)` `@JoinColumn(name = "team_id")` -> `Team team`
- `@ManyToOne(fetch = FetchType.EAGER)` `@JoinColumn(name = "user_id")` -> `User user`

---

## 6. Hibernate Query Language (HQL) Queries

### Query 1: Most Popular Events (Aggregated JOIN Query)
```hql
SELECT e, COUNT(r.id) AS regCount 
FROM Event e 
LEFT JOIN e.registrationsList r 
GROUP BY e.id, e.title 
ORDER BY regCount DESC
```

### Query 2: Upcoming Events (Named Parameters)
```hql
FROM Event e 
WHERE e.eventDate >= :today 
ORDER BY e.eventDate ASC, e.eventTime ASC
```

### Query 3: Total Registrations & Student Counts (Aggregate Functions)
```hql
SELECT COUNT(u.id) FROM User u WHERE UPPER(u.role) = 'STUDENT'
SELECT COUNT(e.id) FROM Event e
SELECT COUNT(r.id) FROM Registration r
```

### Query 4: Team Event Statistics
```hql
SELECT COUNT(t.id) FROM Team t WHERE t.event.id = :eventId
SELECT COUNT(tm.id) FROM TeamMember tm WHERE tm.team.event.id = :eventId
SELECT COUNT(tm.id) FROM TeamMember tm WHERE tm.team.event.id = :eventId GROUP BY tm.team.id
```

---

## 7. Benefits of Dual Architecture
1. **Performance & Granular Control**: High-frequency concurrent seat registrations use JDBC transactions and synchronized Java memory blocks to prevent race conditions.
2. **Productivity & ORM Navigation**: Analytical reporting and complex relational aggregation use Hibernate ORM and HQL without manual SQL ResultSet mapping.
