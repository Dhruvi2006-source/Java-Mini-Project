<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.List" %>
<%@ page import="com.campusevent.model.Event" %>
<%@ page import="com.campusevent.model.Registration" %>
<%
    response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
    response.setHeader("Pragma", "no-cache");
    response.setDateHeader("Expires", 0);

    String userName = (String) session.getAttribute("userName");
    String userRole = (String) session.getAttribute("userRole");

    if (userName == null || userRole == null || !"ADMIN".equalsIgnoreCase(userRole)) {
        response.sendRedirect(request.getContextPath() + "/login.jsp");
        return;
    }

    if (request.getAttribute("registrationsList") == null) {
        request.getRequestDispatcher("/admin/event-registrations").forward(request, response);
        return;
    }

    List<Event> eventsList = (List<Event>) request.getAttribute("eventsList");
    List<Registration> registrationsList = (List<Registration>) request.getAttribute("registrationsList");
    int selectedEventId = request.getAttribute("selectedEventId") != null ? (Integer) request.getAttribute("selectedEventId") : 0;
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Student Event Registrations - Admin Portal</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>

    <div class="app-layout">
        <!-- Sidebar Navigation -->
        <aside class="sidebar">
            <div class="sidebar-brand">
                <span class="brand-icon">⚙️</span>
                <div>
                    <div class="brand-title">Campus Admin</div>
                    <div class="brand-sub">Management Portal</div>
                </div>
            </div>
            <ul class="sidebar-menu">
                <div class="sidebar-menu-category">Main Navigation</div>
                <li><a href="${pageContext.request.contextPath}/index.jsp"><span class="menu-icon">🏠</span> <span>Home</span></a></li>
                <li><a href="${pageContext.request.contextPath}/admin/dashboard"><span class="menu-icon">📊</span> <span>Dashboard</span></a></li>
                <div class="sidebar-menu-category">Management</div>
                <li><a href="${pageContext.request.contextPath}/admin/manage-events"><span class="menu-icon">⚙️</span> <span>Manage Events</span></a></li>
                <li><a href="${pageContext.request.contextPath}/admin/add-event"><span class="menu-icon">➕</span> <span>Add Event</span></a></li>
                <li><a href="${pageContext.request.contextPath}/admin/event-registrations" class="active"><span class="menu-icon">📋</span> <span>Registrations</span></a></li>
                <div class="sidebar-menu-category">Analytics</div>
                <li><a href="${pageContext.request.contextPath}/admin/analytics"><span class="menu-icon">📈</span> <span>Hibernate Analytics</span></a></li>
            </ul>
            <div class="sidebar-footer">
                <a href="${pageContext.request.contextPath}/logout" class="sidebar-logout-btn">
                    <span>🚪</span> <span>Logout</span>
                </a>
            </div>
        </aside>

        <!-- Main App Area -->
        <div class="app-main">
            <!-- Top Header Navbar -->
            <header class="top-header">
                <div class="top-header-title">
                    <span class="app-name-tag">Campus Admin Portal</span>
                    <span class="breadcrumb-sep">/</span>
                    <h2>Student Registrations</h2>
                </div>
                <div class="top-header-right">
                    <div class="user-badge">
                        <span>🛡️ <%= userName %></span>
                        <span class="role-pill admin">ADMIN</span>
                    </div>
                </div>
            </header>

            <!-- Page Body -->
            <main class="content-body">

        <div class="dashboard-header">
            <div>
                <h1 class="welcome-title">Student Event Enrollments</h1>
                <p class="welcome-subtitle">Inspect registered students per campus event</p>
            </div>
            <div>
                <a href="${pageContext.request.contextPath}/admin/manage-events" class="btn btn-secondary">
                    ⚙️ Manage Events
                </a>
            </div>
        </div>

        <!-- Filter Form by Event -->
        <div class="search-container">
            <form action="${pageContext.request.contextPath}/admin/event-registrations" method="GET" class="search-form">
                <div class="search-select-group" style="flex: 3;">
                    <label for="eventId" class="form-label">Filter Registrations by Event</label>
                    <select id="eventId" name="eventId" class="form-control" onchange="this.form.submit()">
                        <option value="0" <%= selectedEventId == 0 ? "selected" : "" %>>All Campus Events</option>
                        <% if (eventsList != null) {
                            for (Event e : eventsList) { %>
                                <option value="<%= e.getId() %>" <%= selectedEventId == e.getId() ? "selected" : "" %>>
                                    <%= e.getTitle() %> (<%= e.getEventDate() %> &bull; <%= e.getVenue() %>)
                                </option>
                        <%  } 
                           } %>
                    </select>
                </div>
                <div style="align-self: flex-end;">
                    <button type="submit" class="btn btn-primary">
                        Filter List
                    </button>
                    <% if (selectedEventId > 0) { %>
                        <a href="${pageContext.request.contextPath}/admin/event-registrations" class="btn btn-secondary">
                            View All
                        </a>
                    <% } %>
                </div>
            </form>
        </div>

        <!-- Registrations Data Table -->
        <% if (registrationsList == null || registrationsList.isEmpty()) { %>
            <div class="empty-state">
                <div class="empty-state-icon">👥</div>
                <h3 class="empty-state-title">No Registrations Found</h3>
                <p class="empty-state-desc">There are no student registrations for the selected filter.</p>
                <a href="${pageContext.request.contextPath}/admin/event-registrations" class="btn btn-outline">View All Registrations</a>
            </div>
        <% } else { %>
            <div class="responsive-table-wrapper">
                <table class="data-table">
                    <thead>
                        <tr>
                            <th>#</th>
                            <th>Student Name</th>
                            <th>Student Email</th>
                            <th>Event Name</th>
                            <th>Type & Team Info</th>
                            <th>Event Date</th>
                            <th>Venue</th>
                            <th>Registration Date</th>
                        </tr>
                    </thead>
                    <tbody>
                        <% 
                        int index = 1;
                        for (Registration reg : registrationsList) { 
                        %>
                            <tr>
                                <td><strong><%= index++ %></strong></td>
                                <td>
                                    <strong>👤 <%= reg.getUserName() %></strong>
                                </td>
                                <td>
                                    <a href="mailto:<%= reg.getUserEmail() %>" class="link">
                                        <%= reg.getUserEmail() %>
                                    </a>
                                </td>
                                <td>
                                    <a href="${pageContext.request.contextPath}/admin/edit-event?id=<%= reg.getEventId() %>" class="link">
                                        <%= reg.getEventTitle() %>
                                    </a>
                                </td>
                                <td>
                                    <% if ("TEAM".equalsIgnoreCase(reg.getEventType())) { %>
                                        <div style="font-size: 0.85rem;">
                                            <span class="role-pill student" style="background: rgba(139, 92, 246, 0.2); color: #a78bfa; font-size: 0.72rem;">TEAM</span>
                                            <strong style="color: #60a5fa;"><%= reg.getTeamName() != null ? reg.getTeamName() : "Team Event" %></strong>
                                            <% if (reg.getRoleInTeam() != null) { %>
                                                <br><small style="color: var(--text-secondary);">(<%= reg.getRoleInTeam() %>)</small>
                                            <% } %>
                                        </div>
                                    <% } else { %>
                                        <span class="role-pill admin" style="background: rgba(59, 130, 246, 0.15); color: #93c5fd; font-size: 0.72rem;">INDIVIDUAL</span>
                                    <% } %>
                                </td>
                                <td><%= reg.getEventDate() %></td>
                                <td><%= reg.getVenue() %></td>
                                <td style="color: var(--text-secondary); font-size: 0.85rem;"><%= reg.getRegistrationDate() %></td>
                            </tr>
                        <% } %>
                    </tbody>
                </table>
            </div>
        <% } %>

            </main>

            <!-- Footer -->
            <footer class="footer">
                <p>&copy; 2026 Campus Event Management System | Student Registrations List</p>
            </footer>
        </div>
    </div>

</body>
</html>
