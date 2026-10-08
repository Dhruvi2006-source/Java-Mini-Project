# Hibernate Module Viva Questions & Answers

### Q1: What is Hibernate ORM?
**Answer:** Hibernate is a Java Object-Relational Mapping (ORM) framework that maps Java domain model POJO classes to database tables, allowing developers to interact with relational databases using Java objects and HQL instead of raw SQL queries.

---

### Q2: Why is Hibernate used in this project alongside JDBC?
**Answer:** We used a hybrid architecture:
- **JDBC** is used in the **Student Event Registration Engine** for thread synchronization, atomic seat allocation, and maximum performance during high concurrency.
- **Hibernate ORM** is used in the **Admin Analytics Module** for rich object navigation, relationship mappings (@ManyToOne, @OneToMany), and HQL aggregate analytical queries.

---

### Q3: What is HQL and how is it different from standard SQL?
**Answer:** HQL (Hibernate Query Language) is an object-oriented query language. Instead of querying database tables and column names (`SELECT * FROM events`), HQL queries Java entity classes and property names (`FROM Event e WHERE e.eventDate >= :today`).

---

### Q4: What are the main components of Hibernate Architecture?
**Answer:**
1. `Configuration`: Reads `hibernate.cfg.xml` and entity metadata.
2. `SessionFactory`: A thread-safe, heavy application-level factory for creating sessions.
3. `Session`: A short-lived, single-threaded object representing a conversation between application and database.
4. `Transaction`: Manages database transaction boundaries (commit/rollback).

---

### Q5: Explain the JPA annotations used in your entity classes.
**Answer:**
- `@Entity`: Marks the class as a persistent Hibernate domain entity.
- `@Table(name = "...")`: Maps entity to a specific database table name.
- `@Id`: Specifies the primary key property.
- `@GeneratedValue(strategy = GenerationType.IDENTITY)`: Configures auto-increment primary key generation.
- `@Column(name = "...")`: Maps property to table column name.
- `@ManyToOne`: Defines a many-to-one relationship (e.g., `Registration -> Event`).
- `@OneToMany`: Defines a one-to-many relationship (e.g., `Event -> Registrations`).
- `@JoinColumn`: Specifies the foreign key column name.

---

### Q6: How does your project prevent Hibernate from destroying existing MySQL tables?
**Answer:** In `hibernate.cfg.xml`, we configured:
`<property name="hibernate.hbm2ddl.auto">validate</property>`
This instructs Hibernate to only validate that entity mappings match existing tables without altering or dropping tables.

---

### Q7: How does Hibernate handle Team Event Analytics?
**Answer:** Hibernate uses HQL aggregate functions (`COUNT`, `GROUP BY`, `AVG`, `MAX`, `MIN`) across mapped entities (`Event`, `Team`, `TeamMember`) to dynamically calculate:
- Total Teams (`SELECT COUNT(t.id) FROM Team t WHERE t.event.id = :eventId`)
- Total Team Participants
- Average, Smallest, and Largest Team sizes.

---

### Q8: What is lazy loading vs eager loading in Hibernate?
**Answer:**
- **Lazy Loading (`FetchType.LAZY`)**: Related child collections (e.g., `@OneToMany registrationsList`) are fetched from database only when explicitly accessed.
- **Eager Loading (`FetchType.EAGER`)**: Associated entities (e.g., `@ManyToOne Event event`) are fetched immediately alongside the parent entity.

---

### Q9: How are named parameters used in HQL to prevent SQL Injection?
**Answer:** We bind parameters dynamically using HQL named parameters (e.g. `WHERE e.eventDate >= :today` and `query.setParameter("today", date)`), preventing string concatenation vulnerabilities.

---

### Q10: How do both JDBC and Hibernate share the same MySQL database?
**Answer:** Both JDBC (`DBConnection`) and Hibernate (`HibernateUtil`) connect to the exact same MySQL database URL (`jdbc:mysql://localhost:3306/campus_event_db`). JDBC writes records during registration, and Hibernate instantly reads those committed records during analytics queries.
