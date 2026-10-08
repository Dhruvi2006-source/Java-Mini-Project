<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
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
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Add New Event - Admin Portal</title>
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
                <li><a href="${pageContext.request.contextPath}/admin/add-event" class="active"><span class="menu-icon">➕</span> <span>Add Event</span></a></li>
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
                    <h2>Add Event</h2>
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
                <h2 class="card-title">Publish New Campus Event</h2>
                <p class="card-subtitle">Fill in details to post a new workshop, symposium, or cultural event</p>
            </div>

            <!-- Error Alert -->
            <% if (request.getAttribute("errorMessage") != null) { %>
                <div class="alert alert-danger">
                    ⚠️ <%= request.getAttribute("errorMessage") %>
                </div>
            <% } %>

            <form action="${pageContext.request.contextPath}/admin/add-event" method="POST" autocomplete="off">
                
                <div class="form-group">
                    <label for="title" class="form-label">Event Title</label>
                    <input type="text" id="title" name="title" class="form-control" 
                           placeholder="e.g. Annual AI & Robotics Symposium" required maxlength="150"
                           value="<%= request.getAttribute("paramTitle") != null ? request.getAttribute("paramTitle") : "" %>">
                </div>

                <div class="form-group">
                    <label for="description" class="form-label">Description</label>
                    <textarea id="description" name="description" class="form-control" rows="4" 
                              placeholder="Provide detailed information about keynotes, schedule, rules, and eligibility..." required><%= request.getAttribute("paramDescription") != null ? request.getAttribute("paramDescription") : "" %></textarea>
                </div>

                <div style="display: grid; grid-template-columns: 1fr 1fr; gap: 1rem;">
                    <div class="form-group">
                        <label for="eventDate" class="form-label">Event Date</label>
                        <input type="date" id="eventDate" name="eventDate" class="form-control" required
                               value="<%= request.getAttribute("paramEventDate") != null ? request.getAttribute("paramEventDate") : "" %>">
                    </div>

                    <div class="form-group">
                        <label for="eventTime" class="form-label">Event Time</label>
                        <input type="time" id="eventTime" name="eventTime" class="form-control" required
                               value="<%= request.getAttribute("paramEventTime") != null ? request.getAttribute("paramEventTime") : "" %>">
                    </div>
                </div>

                <div style="display: grid; grid-template-columns: 2fr 1fr; gap: 1rem;">
                    <div class="form-group">
                        <label for="venue" class="form-label">Campus Venue</label>
                        <input type="text" id="venue" name="venue" class="form-control" 
                               placeholder="e.g. Main Auditorium / Lab 3" required
                               value="<%= request.getAttribute("paramVenue") != null ? request.getAttribute("paramVenue") : "" %>">
                    </div>

                    <div class="form-group">
                        <label for="capacity" class="form-label">Total Student Capacity</label>
                        <input type="number" id="capacity" name="capacity" class="form-control" 
                               placeholder="e.g. 100" min="1" required
                               value="<%= request.getAttribute("paramCapacity") != null ? request.getAttribute("paramCapacity") : "" %>">
                    </div>
                </div>

                <!-- Event Type Selection -->
                <div class="form-group" style="background: rgba(255, 255, 255, 0.05); padding: 1rem; border-radius: 8px; border: 1px solid rgba(255, 255, 255, 0.1); margin-bottom: 1.25rem;">
                    <label class="form-label" style="font-weight: 600; margin-bottom: 0.75rem; display: block;">Registration Event Type</label>
                    <%
                        String currentEventType = request.getAttribute("paramEventType") != null ? (String) request.getAttribute("paramEventType") : "INDIVIDUAL";
                        boolean isTeam = "TEAM".equalsIgnoreCase(currentEventType);
                    %>
                    <div style="display: flex; gap: 2rem; align-items: center; margin-bottom: 1rem;">
                        <label style="cursor: pointer; display: flex; align-items: center; gap: 0.5rem;">
                            <input type="radio" name="eventType" value="INDIVIDUAL" <%= !isTeam ? "checked" : "" %> onchange="toggleTeamSettings(false)">
                            👤 <strong>Individual Event</strong>
                        </label>
                        <label style="cursor: pointer; display: flex; align-items: center; gap: 0.5rem;">
                            <input type="radio" name="eventType" value="TEAM" <%= isTeam ? "checked" : "" %> onchange="toggleTeamSettings(true)">
                            👥 <strong>Team Event</strong>
                        </label>
                    </div>

                    <div id="teamSizeContainer" style="display: <%= isTeam ? "grid" : "none" %>; grid-template-columns: 1fr 1fr; gap: 1rem; border-top: 1px solid rgba(255,255,255,0.1); padding-top: 0.75rem;">
                        <div class="form-group" style="margin-bottom: 0;">
                            <label for="minTeamSize" class="form-label">Minimum Team Size</label>
                            <input type="number" id="minTeamSize" name="minTeamSize" class="form-control" min="2" max="20"
                                   value="<%= request.getAttribute("paramMinTeamSize") != null ? request.getAttribute("paramMinTeamSize") : "2" %>">
                        </div>
                        <div class="form-group" style="margin-bottom: 0;">
                            <label for="maxTeamSize" class="form-label">Maximum Team Size</label>
                            <input type="number" id="maxTeamSize" name="maxTeamSize" class="form-control" min="2" max="20"
                                   value="<%= request.getAttribute("paramMaxTeamSize") != null ? request.getAttribute("paramMaxTeamSize") : "4" %>">
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
                        Publish Event 🚀
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
                <p>&copy; 2026 Campus Event Management System | Add Event</p>
            </footer>
        </div>
    </div>

</body>
</html>
