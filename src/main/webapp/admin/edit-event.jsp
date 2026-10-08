<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
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

    Event event = (Event) request.getAttribute("event");
    int registeredCount = request.getAttribute("registeredCount") != null ? (Integer) request.getAttribute("registeredCount") : 0;

    if (event == null) {
        response.sendRedirect(request.getContextPath() + "/admin/manage-events");
        return;
    }
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Edit Event - Admin Portal</title>
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
                    <h2>Edit Event #<%= event.getId() %></h2>
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

        <div style="margin-bottom: 1.5rem;">
            <a href="${pageContext.request.contextPath}/admin/manage-events" class="link">
                ⬅ Back to Manage Events
            </a>
        </div>

        <div class="card" style="max-width: 650px; margin: 0 auto;">
            
            <div class="card-header">
                <h2 class="card-title">Edit Event #<%= event.getId() %></h2>
                <p class="card-subtitle">Update event details, timing, venue, or seat capacity</p>
            </div>

            <!-- Error Alert -->
            <% if (request.getAttribute("errorMessage") != null) { %>
                <div class="alert alert-danger">
                    ⚠️ <%= request.getAttribute("errorMessage") %>
                </div>
            <% } %>

            <form action="${pageContext.request.contextPath}/admin/edit-event" method="POST" autocomplete="off">
                
                <input type="hidden" name="id" value="<%= event.getId() %>">

                <div class="form-group">
                    <label for="title" class="form-label">Event Title</label>
                    <input type="text" id="title" name="title" class="form-control" 
                           required maxlength="150" value="<%= event.getTitle() %>">
                </div>

                <div class="form-group">
                    <label for="description" class="form-label">Description</label>
                    <textarea id="description" name="description" class="form-control" rows="4" 
                              required><%= event.getDescription() %></textarea>
                </div>

                <div style="display: grid; grid-template-columns: 1fr 1fr; gap: 1rem;">
                    <div class="form-group">
                        <label for="eventDate" class="form-label">Event Date</label>
                        <input type="date" id="eventDate" name="eventDate" class="form-control" required
                               value="<%= event.getEventDate() %>">
                    </div>

                    <div class="form-group">
                        <label for="eventTime" class="form-label">Event Time</label>
                        <input type="time" id="eventTime" name="eventTime" class="form-control" required
                               value="<%= event.getEventTime() %>">
                    </div>
                </div>

                <div style="display: grid; grid-template-columns: 2fr 1fr; gap: 1rem;">
                    <div class="form-group">
                        <label for="venue" class="form-label">Campus Venue</label>
                        <input type="text" id="venue" name="venue" class="form-control" 
                               required value="<%= event.getVenue() %>">
                    </div>

                    <div class="form-group">
                        <label for="capacity" class="form-label">Total Student Capacity</label>
                        <input type="number" id="capacity" name="capacity" class="form-control" 
                               min="<%= registeredCount %>" required value="<%= event.getCapacity() %>">
                        <small style="color: var(--text-secondary); font-size: 0.78rem; display: block; margin-top: 0.2rem;">
                            Currently registered: <strong><%= registeredCount %></strong> students
                        </small>
                    </div>
                </div>

                <!-- Event Type Selection -->
                <div class="form-group" style="background: rgba(255, 255, 255, 0.05); padding: 1rem; border-radius: 8px; border: 1px solid rgba(255, 255, 255, 0.1); margin-bottom: 1.25rem;">
                    <label class="form-label" style="font-weight: 600; margin-bottom: 0.75rem; display: block;">Registration Event Type</label>
                    <%
                        boolean isTeamEdit = event.isTeamEvent();
                    %>
                    <div style="display: flex; gap: 2rem; align-items: center; margin-bottom: 1rem;">
                        <label style="cursor: pointer; display: flex; align-items: center; gap: 0.5rem;">
                            <input type="radio" name="eventType" value="INDIVIDUAL" <%= !isTeamEdit ? "checked" : "" %> onchange="toggleTeamSettings(false)">
                            👤 <strong>Individual Event</strong>
                        </label>
                        <label style="cursor: pointer; display: flex; align-items: center; gap: 0.5rem;">
                            <input type="radio" name="eventType" value="TEAM" <%= isTeamEdit ? "checked" : "" %> onchange="toggleTeamSettings(true)">
                            👥 <strong>Team Event</strong>
                        </label>
                    </div>

                    <div id="teamSizeContainer" style="display: <%= isTeamEdit ? "grid" : "none" %>; grid-template-columns: 1fr 1fr; gap: 1rem; border-top: 1px solid rgba(255,255,255,0.1); padding-top: 0.75rem;">
                        <div class="form-group" style="margin-bottom: 0;">
                            <label for="minTeamSize" class="form-label">Minimum Team Size</label>
                            <input type="number" id="minTeamSize" name="minTeamSize" class="form-control" min="2" max="20"
                                   value="<%= event.getMinTeamSize() > 1 ? event.getMinTeamSize() : 2 %>">
                        </div>
                        <div class="form-group" style="margin-bottom: 0;">
                            <label for="maxTeamSize" class="form-label">Maximum Team Size</label>
                            <input type="number" id="maxTeamSize" name="maxTeamSize" class="form-control" min="2" max="20"
                                   value="<%= event.getMaxTeamSize() > 1 ? event.getMaxTeamSize() : 4 %>">
                        </div>
                    </div>
                </div>

                <script>
                    function toggleTeamSettings(show) {
                        const container = document.getElementById('teamSizeContainer');
                        if (container) {
                            container.style.display = show ? 'grid' : 'none';
                        }
                    }
                </script>

                <div style="display: flex; gap: 1rem; margin-top: 1.5rem;">
                    <button type="submit" class="btn btn-primary btn-block">
                        Save Changes 💾
                    </button>
                    <a href="${pageContext.request.contextPath}/admin/manage-events" class="btn btn-secondary">
                        Cancel
                    </a>
                </div>

            </form>

        </div>

            </main>

            <!-- Footer -->
            <footer class="footer">
                <p>&copy; 2026 Campus Event Management System | Edit Event</p>
            </footer>
        </div>
    </div>

</body>
</html>
