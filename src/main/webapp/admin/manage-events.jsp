<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Map" %>
<%@ page import="com.campusevent.model.Event" %>
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

    if (request.getAttribute("eventsList") == null) {
        request.getRequestDispatcher("/admin/manage-events").forward(request, response);
        return;
    }

    List<Event> eventsList = (List<Event>) request.getAttribute("eventsList");
    List<String> venuesList = (List<String>) request.getAttribute("venuesList");
    Map<Integer, Integer> registeredCountMap = (Map<Integer, Integer>) request.getAttribute("registeredCountMap");

    String paramKeyword = (String) request.getAttribute("paramKeyword");
    String paramVenue = (String) request.getAttribute("paramVenue");

    if (paramKeyword == null) paramKeyword = "";
    if (paramVenue == null) paramVenue = "ALL";

    String successMsg = (String) session.getAttribute("successMessage");
    String errorMsg = (String) session.getAttribute("errorMessage");
    if (successMsg != null) session.removeAttribute("successMessage");
    if (errorMsg != null) session.removeAttribute("errorMessage");
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Manage Events - Admin Portal</title>
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
                <li><a href="${pageContext.request.contextPath}/admin/manage-events" class="active"><span class="menu-icon">⚙️</span> <span>Manage Events</span></a></li>
                <li><a href="${pageContext.request.contextPath}/admin/add-event"><span class="menu-icon">➕</span> <span>Add Event</span></a></li>
                <li><a href="${pageContext.request.contextPath}/admin/event-registrations"><span class="menu-icon">📋</span> <span>Registrations</span></a></li>
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
                    <h2>Manage Events</h2>
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
                <h1 class="welcome-title">Manage Campus Events</h1>
                <p class="welcome-subtitle">Create, search, edit, or delete campus event listings</p>
            </div>
            <div>
                <a href="${pageContext.request.contextPath}/admin/add-event" class="btn btn-primary">
                    ➕ Create New Event
                </a>
            </div>
        </div>

        <!-- Alert Notifications -->
        <% if (successMsg != null) { %>
            <div class="alert alert-success">
                ✅ <%= successMsg %>
            </div>
        <% } %>
        <% if (errorMsg != null) { %>
            <div class="alert alert-danger">
                ⚠️ <%= errorMsg %>
            </div>
        <% } %>

        <!-- Search & Filter Form -->
        <div class="search-container">
            <form action="${pageContext.request.contextPath}/admin/manage-events" method="GET" class="search-form">
                
                <div class="search-input-group">
                    <label for="keyword" class="form-label">Search Event Title</label>
                    <input type="text" id="keyword" name="keyword" class="form-control" 
                           placeholder="Search title or description..." value="<%= paramKeyword %>">
                </div>

                <div class="search-select-group">
                    <label for="venue" class="form-label">Filter by Venue</label>
                    <select id="venue" name="venue" class="form-control">
                        <option value="ALL" <%= "ALL".equals(paramVenue) ? "selected" : "" %>>All Venues</option>
                        <% if (venuesList != null) { 
                            for (String v : venuesList) { %>
                                <option value="<%= v %>" <%= v.equalsIgnoreCase(paramVenue) ? "selected" : "" %>><%= v %></option>
                        <%  } 
                           } %>
                    </select>
                </div>

                <div style="align-self: flex-end;">
                    <button type="submit" class="btn btn-primary">
                        🔍 Search
                    </button>
                    <% if (!paramKeyword.isEmpty() || !"ALL".equals(paramVenue)) { %>
                        <a href="${pageContext.request.contextPath}/admin/manage-events" class="btn btn-secondary">
                            Reset
                        </a>
                    <% } %>
                </div>

            </form>
        </div>

        <!-- Events Management Table -->
        <% if (eventsList == null || eventsList.isEmpty()) { %>
            <div class="empty-state">
                <div class="empty-state-icon">📌</div>
                <h3 class="empty-state-title">No Events Found</h3>
                <p class="empty-state-desc">There are no events matching your criteria. Click below to add a new event.</p>
                <a href="${pageContext.request.contextPath}/admin/add-event" class="btn btn-primary">Add Event Now</a>
            </div>
        <% } else { %>
            <div class="responsive-table-wrapper">
                <table class="data-table">
                    <thead>
                        <tr>
                            <th>ID</th>
                            <th>Title</th>
                            <th>Event Type</th>
                            <th>Date</th>
                            <th>Time</th>
                            <th>Venue</th>
                            <th>Enrolled / Capacity</th>
                            <th>Status</th>
                            <th>Actions</th>
                        </tr>
                    </thead>
                    <tbody>
                        <% for (Event event : eventsList) { 
                            int enrolled = (registeredCountMap != null && registeredCountMap.containsKey(event.getId())) ? registeredCountMap.get(event.getId()) : 0;
                            boolean isFull = enrolled >= event.getCapacity();
                        %>
                            <tr>
                                <td><strong>#<%= event.getId() %></strong></td>
                                <td>
                                    <strong><%= event.getTitle() %></strong>
                                </td>
                                <td>
                                    <% if (event.isTeamEvent()) { %>
                                        <span class="badge-type badge-type-team">
                                            👥 TEAM (<%= event.getMinTeamSize() %>-<%= event.getMaxTeamSize() %>)
                                        </span>
                                    <% } else { %>
                                        <span class="badge-type badge-type-individual">
                                            👤 INDIVIDUAL
                                        </span>
                                    <% } %>
                                </td>
                                <td><%= event.getEventDate() %></td>
                                <td><%= event.getEventTime() %></td>
                                <td><%= event.getVenue() %></td>
                                <td>
                                    <strong><%= enrolled %></strong> / <%= event.getCapacity() %>
                                </td>
                                <td>
                                    <% if (isFull) { %>
                                        <span class="badge-status badge-full">Full</span>
                                    <% } else { %>
                                        <span class="badge-status badge-available">Open</span>
                                    <% } %>
                                </td>
                                <td>
                                    <div style="display: flex; gap: 0.4rem; align-items: center; flex-wrap: wrap;">
                                        <a href="${pageContext.request.contextPath}/admin/event-registrations?eventId=<%= event.getId() %>" class="btn btn-secondary btn-sm" title="View Enrolled Students">
                                            👥 Attendees (<%= enrolled %>)
                                        </a>
                                        <a href="${pageContext.request.contextPath}/admin/edit-event?id=<%= event.getId() %>" class="btn btn-outline btn-sm">
                                            ✏️ Edit
                                        </a>
                                        <form action="${pageContext.request.contextPath}/admin/delete-event" method="POST" style="display: inline;" onsubmit="return confirm('Are you sure you want to delete event \'<%= event.getTitle().replace("'", "\\'") %>\'? This will safely remove associated registrations.');">
                                            <input type="hidden" name="eventId" value="<%= event.getId() %>">
                                            <button type="submit" class="btn btn-danger btn-sm">
                                                🗑️ Delete
                                            </button>
                                        </form>
                                    </div>
                                </td>
                            </tr>
                        <% } %>
                    </tbody>
                </table>
            </div>
        <% } %>

            </main>

            <!-- Footer -->
            <footer class="footer">
                <p>&copy; 2026 Campus Event Management System | Event Management</p>
            </footer>
        </div>
    </div>

</body>
</html>
