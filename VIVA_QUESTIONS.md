# Campus Event Management System
## Java Web Project Viva Questions & Answers

This guide contains 25 essential questions and clear, student-friendly answers for the college project viva examination.

---

### Q1: What is JSP (JavaServer Pages)?
**Answer:**  
JSP is a server-side technology used to create dynamic web pages in Java. It allows developers to insert Java code into HTML pages using special JSP tags (such as `<% %>` scriptlets, expression tags `<%= %>`, and Expression Language `${ }`). JSPs are automatically compiled into Java Servlets by the web container (like Apache Tomcat) when first requested.

---

### Q2: What is a Java Servlet?
**Answer:**  
A Java Servlet is a server-side Java class (`javax.servlet.http.HttpServlet`) that runs inside a web container (Tomcat). It receives HTTP requests from client browsers, processes business logic or database queries, and sends an HTTP response (or forwards to a JSP page) back to the browser.

---

### Q3: What is the main difference between JSP and Servlet?
**Answer:**  
- **JSP:** HTML with embedded Java code. Used primarily for the **Presentation Layer (View)** to render user interfaces.
- **Servlet:** Java code that can output HTML. Used primarily for the **Controller Layer** to handle request routing, form validation, and business logic.

---

### Q4: What is JDBC (Java Database Connectivity)?
**Answer:**  
JDBC is a standard Java API (`java.sql` package) that enables Java applications to connect to relational databases like MySQL, execute SQL queries, insert/update records, and process database result sets.

---

### Q5: What is a JDBC Driver?
**Answer:**  
A JDBC Driver is a software component that translates standard Java JDBC API calls into the specific database protocol used by a database management system (such as MySQL, Oracle, or PostgreSQL).

---

### Q6: What is MySQL Connector/J?
**Answer:**  
MySQL Connector/J is the official Type 4 JDBC driver provided by MySQL. It allows Java applications to communicate with a MySQL database over TCP/IP by providing implementation classes such as `com.mysql.cj.jdbc.Driver`.

---

### Q7: What is PreparedStatement in JDBC?
**Answer:**  
`PreparedStatement` is a pre-compiled SQL statement object in Java JDBC. Instead of embedding dynamic values directly into SQL strings, placeholders (`?`) are used. The database pre-compiles the query structure first and then safely binds the parameter values.

---

### Q8: Why should we use PreparedStatement instead of Statement?
**Answer:**  
1. **Prevents SQL Injection:** Parameter values are automatically escaped and treated as literal values, preventing attackers from injecting malicious SQL commands.
2. **Performance:** Pre-compiling SQL queries improves execution speed when running repeated queries.
3. **Clean Code:** Eliminates complex string concatenations when building queries with quotes and variables.

---

### Q9: What is HttpSession?
**Answer:**  
`HttpSession` is an interface provided by the Servlet API (`javax.servlet.http.HttpSession`) used to persist user state across multiple HTTP requests. Because HTTP is a stateless protocol, sessions store attributes (like `userId`, `userName`, and `userRole`) on the server side and associate them with a session ID cookie sent to the client browser.

---

### Q10: Why do we use sessions in web applications?
**Answer:**  
Sessions allow web applications to track user login status, remember who the current user is, enforce access control across multiple pages, and customize user dashboards without requiring the user to log in on every single page request.

---

### Q11: What is CRUD?
**Answer:**  
CRUD stands for the four basic functions of persistent storage:
- **C**reate (`INSERT INTO events ...`)
- **R**ead (`SELECT * FROM events ...`)
- **U**pdate (`UPDATE events SET ...`)
- **D**elete (`DELETE FROM events WHERE ...`)

---

### Q12: What is the Data Access Object (DAO) pattern?
**Answer:**  
The DAO pattern separates database interaction code from business and presentation logic. DAO classes (such as `UserDAO`, `EventDAO`, `RegistrationDAO`) contain all SQL execution code and JDBC resource management, making the codebase clean, modular, and easy to maintain.

---

### Q13: Why do we use MVC-like separation of concerns in this project?
**Answer:**  
MVC (Model-View-Controller) separates responsibilities:
- **Model:** Java POJOs (`User`, `Event`) hold data structure.
- **View:** JSP pages display HTML UI.
- **Controller:** Servlets handle request processing and business rules.
This separation ensures that JSPs remain focused on presentation while Servlets handle logic, preventing clutter and making code easier to debug and explain in a viva.

---

### Q14: How does a JSP communicate with a Servlet in this project?
**Answer:**  
1. A JSP form submits an HTTP request (via `action="${pageContext.request.contextPath}/login"` or `/register-event`).
2. The Servlet receives the request in `doGet()` or `doPost()`.
3. After processing, the Servlet attaches attributes (`request.setAttribute("eventsList", list)`) and forwards the request back to a JSP using `request.getRequestDispatcher("/path.jsp").forward(request, response)`.

---

### Q15: How does a Servlet communicate with MySQL?
**Answer:**  
1. The Servlet calls a DAO method (e.g. `eventDAO.getAllEvents()`).
2. The DAO obtains a JDBC Connection from `DBConnection.getConnection()`.
3. The DAO executes a `PreparedStatement`, reads the `ResultSet`, maps rows into Java objects, closes the connection, and returns the result list to the Servlet.

---

### Q16: What is a Primary Key in relational databases?
**Answer:**  
A Primary Key is a database column (or set of columns) that uniquely identifies each record in a table. In our project, `id` (INT AUTO_INCREMENT) serves as the primary key for `users`, `events`, and `registrations` tables.

---

### Q17: What is a Foreign Key?
**Answer:**  
A Foreign Key is a column in one table that links to the Primary Key of another table, establishing a relational link and enforcing referential integrity. In our `registrations` table, `user_id` links to `users(id)` and `event_id` links to `events(id)`.

---

### Q18: Why do we use Unique Constraints?
**Answer:**  
A Unique Constraint ensures that all values in a column or combination of columns are unique across all rows in a table. For example, `UNIQUE(email)` in `users` prevents multiple accounts with the same email address.

---

### Q19: How is duplicate event registration prevented in this project?
**Answer:**  
Duplicate registration is prevented at two levels:
1. **Database Level:** A unique constraint `CONSTRAINT unique_user_event UNIQUE (user_id, event_id)` in the `registrations` table.
2. **Application Level:** `RegistrationDAO.isUserRegistered(userId, eventId)` checks if a record exists before inserting, displaying a friendly notice: *"You are already registered for this event."*

---

### Q20: How is event seat capacity handled dynamically?
**Answer:**  
1. The `events` table stores `capacity` (e.g. 100).
2. When displaying events, `RegistrationDAO.getRegisteredCount(eventId)` counts current enrollments.
3. Available seats are calculated dynamically: `availableSeats = capacity - registeredCount`.
4. If `availableSeats <= 0`, the registration button is disabled and replaced with *"Registration Closed (Full)"*.

---

### Q21: How is admin authorization implemented?
**Answer:**  
Every Admin Servlet and Admin JSP contains server-side session checking:
```java
HttpSession session = request.getSession(false);
if (session == null || !"ADMIN".equalsIgnoreCase((String) session.getAttribute("userRole"))) {
    response.sendRedirect(request.getContextPath() + "/login.jsp");
    return;
}
```
If a student or unauthenticated user tries to manually type `/admin/dashboard`, the server blocks access and redirects them to `login.jsp`.

---

### Q22: How is SQL Injection prevented in this application?
**Answer:**  
Every database query involving user input uses JDBC `PreparedStatement` with parameter binding (`pstmt.setString(1, input)`). User inputs are never concatenated directly into raw SQL strings.

---

### Q23: How does user logout work behind the scenes?
**Answer:**  
1. `LogoutServlet` calls `session.invalidate()` to destroy the user session on the server.
2. It sets HTTP cache headers (`Cache-Control: no-cache, no-store, must-revalidate`) to prevent browser back-button history caching.
3. The user is redirected to `login.jsp` with a logout success notification.

---

### Q24: Why is JSP used for presentation instead of generating HTML inside Servlets?
**Answer:**  
Writing HTML markup inside Java `PrintWriter.println()` in Servlets is hard to read, maintain, and style. JSP allows writing standard HTML and CSS naturally while embedding dynamic values using Expression Language (`${ }`), keeping presentation clean and separate from logic.

---

### Q25: What future improvements can be added to this project?
**Answer:**  
1. **Email Notifications:** Sending confirmation emails with registration details using JavaMail API.
2. **QR Code Attendance:** Generating downloadable PDF passes with embedded QR codes for automated event entry scanning.
3. **Filtering & Sorting:** Adding multi-category event sorting and date range filters.
