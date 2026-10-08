<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.List" %>
<%@ page import="com.campusevent.model.Registration" %>
<%
    response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
    response.setHeader("Pragma", "no-cache");
    response.setDateHeader("Expires", 0);

    String userName = (String) session.getAttribute("userName");
    String userRole = (String) session.getAttribute("userRole");

    if (userName == null || userRole == null || !"STUDENT".equalsIgnoreCase(userRole)) {
        response.sendRedirect(request.getContextPath() + "/login.jsp");
        return;
    }

    List<Registration> registrationsList = (List<Registration>) request.getAttribute("registrationsList");

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
    <title>My Event Registrations - Campus Event Portal</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>

    <div class="app-layout">
        <!-- Sidebar Navigation -->
        <aside class="sidebar">
            <div class="sidebar-brand">
                <span class="brand-icon">🎓</span>
                <div>
                    <div class="brand-title">Campus Events</div>
                    <div class="brand-sub">Student Portal</div>
                </div>
            </div>
            <ul class="sidebar-menu">
                <div class="sidebar-menu-category">Main Navigation</div>
                <li><a href="${pageContext.request.contextPath}/index.jsp"><span class="menu-icon">🏠</span> <span>Home</span></a></li>
                <li><a href="${pageContext.request.contextPath}/student/dashboard"><span class="menu-icon">📊</span> <span>Dashboard</span></a></li>
                <div class="sidebar-menu-category">Events</div>
                <li><a href="${pageContext.request.contextPath}/student/events"><span class="menu-icon">🎉</span> <span>Browse Events</span></a></li>
                <li><a href="${pageContext.request.contextPath}/student/my-registrations" class="active"><span class="menu-icon">📋</span> <span>My Registrations</span></a></li>
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
                    <span class="app-name-tag">Campus Student Portal</span>
                    <span class="breadcrumb-sep">/</span>
                    <h2>My Registrations</h2>
                </div>
                <div class="top-header-right">
                    <div class="user-badge">
                        <span>👤 <%= userName %></span>
                        <span class="role-pill student">STUDENT</span>
                    </div>
                </div>
            </header>

            <!-- Page Body -->
            <main class="content-body">

        <div class="dashboard-header">
            <div>
                <h1 class="welcome-title">My Event Registrations</h1>
                <p class="welcome-subtitle">Manage your enrolled campus events and schedules</p>
            </div>
            <div>
                <a href="${pageContext.request.contextPath}/student/events" class="btn btn-primary">
                    🔍 Browse More Events
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

        <% if (registrationsList == null || registrationsList.isEmpty()) { %>
            <div class="empty-state">
                <div class="empty-state-icon">📋</div>
                <h3 class="empty-state-title">No Event Registrations Yet</h3>
                <p class="empty-state-desc">You have not registered for any campus events. Explore our events catalog to join exciting workshops and symposiums.</p>
                <a href="${pageContext.request.contextPath}/student/events" class="btn btn-primary">Explore Events Catalog</a>
            </div>
        <% } else { %>
            <div class="responsive-table-wrapper">
                <table class="data-table">
                    <thead>
                        <tr>
                            <th>#</th>
                            <th>Event Title</th>
                            <th>Registration Type & Team Details</th>
                            <th>Date & Time</th>
                            <th>Venue</th>
                            <th>Registered On</th>
                            <th>Status</th>
                            <th>Action</th>
                        </tr>
                    </thead>
                    <tbody>
                        <% 
                        int count = 1;
                        for (Registration reg : registrationsList) { 
                            boolean isTeam = "TEAM".equalsIgnoreCase(reg.getEventType());
                            boolean isLeader = "TEAM LEADER".equalsIgnoreCase(reg.getRoleInTeam());
                        %>
                            <tr>
                                <td><strong><%= count++ %></strong></td>
                                <td>
                                    <a href="${pageContext.request.contextPath}/student/event-details?id=<%= reg.getEventId() %>" class="link">
                                        <strong><%= reg.getEventTitle() %></strong>
                                    </a>
                                </td>
                                <td>
                                    <% if (isTeam) { %>
                                        <div style="background: rgba(139, 92, 246, 0.1); border: 1px solid rgba(139, 92, 246, 0.25); padding: 0.6rem 0.8rem; border-radius: 6px;">
                                            <div style="display: flex; align-items: center; gap: 0.5rem; margin-bottom: 0.3rem;">
                                                <span class="role-pill student" style="font-size: 0.7rem; background: rgba(139, 92, 246, 0.3); color: #c084fc;">TEAM</span>
                                                <strong style="color: #60a5fa;"><%= reg.getTeamName() %></strong>
                                                <% if (isLeader) { %>
                                                    <span style="background: #8b5cf6; color: #fff; font-size: 0.65rem; padding: 0.15rem 0.4rem; border-radius: 3px; font-weight: bold;">TEAM LEADER</span>
                                                <% } else { %>
                                                    <span style="background: rgba(59, 130, 246, 0.3); color: #93c5fd; font-size: 0.65rem; padding: 0.15rem 0.4rem; border-radius: 3px;">TEAM MEMBER</span>
                                                <% } %>
                                            </div>
                                            <% if (reg.getTeamMembers() != null && !reg.getTeamMembers().isEmpty()) { %>
                                                <div style="font-size: 0.78rem; color: var(--text-secondary); margin-top: 0.25rem; line-height: 1.4;">
                                                    <strong>Members (<%= reg.getTeamMembers().size() %>):</strong>
                                                    <ul style="margin: 0.2rem 0 0 1rem; padding: 0;">
                                                        <% for (com.campusevent.model.TeamMember tm : reg.getTeamMembers()) { %>
                                                            <li>
                                                                <%= tm.getUserName() %> (<%= tm.getUserEmail() %>) 
                                                                <%= tm.isLeader() ? "👑 Leader" : "" %>
                                                            </li>
                                                        <% } %>
                                                    </ul>
                                                </div>
                                            <% } %>
                                        </div>
                                    <% } else { %>
                                        <span class="role-pill admin" style="background: rgba(59, 130, 246, 0.15); color: #93c5fd; font-size: 0.72rem;">INDIVIDUAL</span>
                                    <% } %>
                                </td>
                                <td>
                                    📅 <%= reg.getEventDate() %><br>
                                    <small style="color: var(--text-secondary);">⏰ <%= reg.getEventTime() %></small>
                                </td>
                                <td>📍 <%= reg.getVenue() %></td>
                                <td style="color: var(--text-secondary); font-size: 0.85rem;"><%= reg.getRegistrationDate() %></td>
                                <td>
                                    <span class="badge-status badge-registered">Registered</span>
                                </td>
                                <td>
                                    <% if (!isTeam || isLeader) { %>
                                        <form action="${pageContext.request.contextPath}/student/cancel-registration" method="POST" style="display: inline;" onsubmit="return confirm('<%= isTeam ? "As Team Leader, cancelling will unregister the ENTIRE team (" + reg.getTeamName() + "). Continue?" : "Are you sure you want to cancel registration for " + reg.getEventTitle() + "?" %>');">
                                            <input type="hidden" name="eventId" value="<%= reg.getEventId() %>">
                                            <button type="submit" class="btn btn-danger btn-sm">
                                                Cancel Registration
                                            </button>
                                        </form>
                                    <% } else { %>
                                        <span style="font-size: 0.75rem; color: var(--text-secondary); display: block;" title="Only Team Leader can cancel team registration">
                                            🔒 Contact Team Leader to Cancel
                                        </span>
                                    <% } %>
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
                <p>&copy; 2026 Campus Event Management System | Registered Events Overview</p>
            </footer>
        </div>
    </div>

</body>
</html>
