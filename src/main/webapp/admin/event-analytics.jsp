<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="com.campusevent.model.Event" %>
<%@ page import="com.campusevent.model.Registration" %>
<%@ page import="com.campusevent.model.Team" %>
<%@ page import="com.campusevent.service.EventAnalyticsService.DetailedEventAnalyticsDTO" %>
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

    DetailedEventAnalyticsDTO analytics = (DetailedEventAnalyticsDTO) request.getAttribute("analytics");
    if (analytics == null || analytics.getEvent() == null) {
        response.sendRedirect(request.getContextPath() + "/admin/analytics");
        return;
    }

    Event event = analytics.getEvent();
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Event Analytics - <%= event.getTitle() %></title>
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
                <li><a href="${pageContext.request.contextPath}/admin/event-registrations"><span class="menu-icon">📋</span> <span>Registrations</span></a></li>
                <div class="sidebar-menu-category">Analytics</div>
                <li><a href="${pageContext.request.contextPath}/admin/analytics" class="active"><span class="menu-icon">📈</span> <span>Hibernate Analytics</span></a></li>
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
                    <h2>Event Analytics</h2>
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
            <a href="${pageContext.request.contextPath}/admin/analytics" class="link">
                ⬅ Back to Hibernate Analytics Dashboard
            </a>
        </div>

        <!-- Event Overview Header -->
        <div class="card" style="margin-bottom: 1.5rem;">
            <div style="display: flex; justify-content: space-between; align-items: flex-start; flex-wrap: wrap; gap: 1rem;">
                <div>
                    <h1 style="font-size: 1.6rem; font-weight: 700; color: #ffffff; margin-bottom: 0.3rem;"><%= event.getTitle() %></h1>
                    <p style="color: var(--text-secondary); font-size: 0.9rem;">
                        Event ID #<%= event.getId() %> &bull; Venue: <strong><%= event.getVenue() %></strong> &bull; Date: <strong><%= event.getEventDate() %></strong> (<%= event.getEventTime() %>)
                    </p>
                </div>
                <div>
                    <% if (event.isTeamEvent()) { %>
                        <span class="badge-type badge-type-team" style="font-size: 0.85rem; padding: 0.4rem 0.9rem;">
                            👥 TEAM EVENT (<%= event.getMinTeamSize() %>-<%= event.getMaxTeamSize() %> Members)
                        </span>
                    <% } else { %>
                        <span class="badge-type badge-type-individual" style="font-size: 0.85rem; padding: 0.4rem 0.9rem;">
                            👤 INDIVIDUAL EVENT
                        </span>
                    <% } %>
                </div>
            </div>
            
            <div style="margin-top: 1rem; padding-top: 1rem; border-top: 1px solid rgba(255,255,255,0.1); color: var(--text-primary); font-size: 0.95rem; line-height: 1.6;">
                <%= event.getDescription() %>
            </div>
        </div>

        <!-- Enrollment Capacity Stats -->
        <div class="stats-grid" style="margin-bottom: 2rem;">
            <div class="stat-card">
                <div class="stat-icon">🪑</div>
                <div>
                    <div class="stat-val"><%= event.getCapacity() %></div>
                    <div class="stat-lbl">Total Seat Capacity</div>
                </div>
            </div>

            <div class="stat-card">
                <div class="stat-icon">👥</div>
                <div>
                    <div class="stat-val"><%= analytics.getTotalRegistered() %></div>
                    <div class="stat-lbl">Enrolled Participants</div>
                </div>
            </div>

            <div class="stat-card">
                <div class="stat-icon">🟢</div>
                <div>
                    <div class="stat-val" style="color: <%= analytics.getAvailableSeats() > 0 ? "#34d399" : "#f87171" %>;"><%= analytics.getAvailableSeats() %></div>
                    <div class="stat-lbl">Available Seats</div>
                </div>
            </div>

            <div class="stat-card">
                <div class="stat-icon">📈</div>
                <div>
                    <div class="stat-val"><%= String.format("%.1f%%", analytics.getRegistrationPercentage()) %></div>
                    <div class="stat-lbl">Capacity Utilization</div>
                </div>
            </div>
        </div>

        <!-- Team Event Analytics Card (Conditional if Team Event) -->
        <% if (analytics.isTeamEvent()) { %>
            <div class="card" style="margin-bottom: 2rem; background: linear-gradient(135deg, rgba(139, 92, 246, 0.12) 0%, rgba(99, 102, 241, 0.08) 100%); border-color: rgba(139, 92, 246, 0.3);">
                <div class="card-header">
                    <h3 class="card-title" style="color: #c084fc; font-size: 1.2rem;">🏆 Team Event Analytics (HQL ORM Calculations)</h3>
                    <p class="card-subtitle">Real-time team distribution metrics retrieved using HQL aggregate functions</p>
                </div>

                <div style="display: grid; grid-template-columns: repeat(auto-fit, minmax(140px, 1fr)); gap: 1rem; text-align: center;">
                    <div style="background: rgba(255,255,255,0.05); padding: 1rem; border-radius: 8px;">
                        <div style="font-size: 1.5rem; font-weight: bold; color: #a78bfa;"><%= analytics.getTotalTeams() %></div>
                        <div style="font-size: 0.8rem; color: var(--text-secondary); text-transform: uppercase;">Total Teams</div>
                    </div>
                    <div style="background: rgba(255,255,255,0.05); padding: 1rem; border-radius: 8px;">
                        <div style="font-size: 1.5rem; font-weight: bold; color: #60a5fa;"><%= analytics.getTotalTeamParticipants() %></div>
                        <div style="font-size: 0.8rem; color: var(--text-secondary); text-transform: uppercase;">Team Members</div>
                    </div>
                    <div style="background: rgba(255,255,255,0.05); padding: 1rem; border-radius: 8px;">
                        <div style="font-size: 1.5rem; font-weight: bold; color: #34d399;"><%= String.format("%.2f", analytics.getAverageTeamSize()) %></div>
                        <div style="font-size: 0.8rem; color: var(--text-secondary); text-transform: uppercase;">Avg Team Size</div>
                    </div>
                    <div style="background: rgba(255,255,255,0.05); padding: 1rem; border-radius: 8px;">
                        <div style="font-size: 1.5rem; font-weight: bold; color: #c084fc;"><%= analytics.getLargestTeamSize() %></div>
                        <div style="font-size: 0.8rem; color: var(--text-secondary); text-transform: uppercase;">Largest Team</div>
                    </div>
                    <div style="background: rgba(255,255,255,0.05); padding: 1rem; border-radius: 8px;">
                        <div style="font-size: 1.5rem; font-weight: bold; color: #93c5fd;"><%= analytics.getSmallestTeamSize() %></div>
                        <div style="font-size: 0.8rem; color: var(--text-secondary); text-transform: uppercase;">Smallest Team</div>
                    </div>
                </div>
            </div>
        <% } %>

        <!-- Registered Students Table (Retrieved via Hibernate ORM Relationship) -->
        <div class="card" style="margin: 0;">
            <div class="card-header">
                <h3 class="card-title">Enrolled Students List (Hibernate ORM Mapping)</h3>
                <p class="card-subtitle">Retrieved via Hibernate <code>Registration &rarr; User</code> relationship</p>
            </div>

            <% if (analytics.getRegistrationsList() == null || analytics.getRegistrationsList().isEmpty()) { %>
                <div class="empty-state">
                    <h3 class="empty-state-title">No Students Registered Yet</h3>
                    <p class="empty-state-desc">There are no student registrations recorded for this event.</p>
                </div>
            <% } else { %>
                <div class="responsive-table-wrapper">
                    <table class="data-table">
                        <thead>
                            <tr>
                                <th>#</th>
                                <th>Student Name</th>
                                <th>Email Address</th>
                                <th>Registration Timestamp</th>
                                <% if (analytics.isTeamEvent()) { %>
                                    <th>Team Details</th>
                                <% } %>
                            </tr>
                        </thead>
                        <tbody>
                            <% 
                            int idx = 1;
                            for (Registration reg : analytics.getRegistrationsList()) { 
                            %>
                                <tr>
                                    <td><strong><%= idx++ %></strong></td>
                                    <td>
                                        <strong>👤 <%= reg.getUserName() %></strong>
                                    </td>
                                    <td>
                                        <a href="mailto:<%= reg.getUserEmail() %>" class="link">
                                            <%= reg.getUserEmail() %>
                                        </a>
                                    </td>
                                    <td style="color: var(--text-secondary); font-size: 0.85rem;">
                                        <%= reg.getRegistrationDate() %>
                                    </td>
                                    <% if (analytics.isTeamEvent()) { %>
                                        <td>
                                            <% if (reg.getTeamName() != null) { %>
                                                <span class="role-pill student" style="font-size: 0.72rem; background: rgba(139, 92, 246, 0.2); color: #c084fc;">
                                                    👥 <%= reg.getTeamName() %>
                                                </span>
                                                <small style="color: var(--text-secondary); display: block; margin-top: 0.1rem;">
                                                    <%= reg.getRoleInTeam() %>
                                                </small>
                                            <% } else { %>
                                                <span style="color: var(--text-secondary); font-size: 0.8rem;">Individual / N/A</span>
                                            <% } %>
                                        </td>
                                    <% } %>
                                </tr>
                            <% } %>
                        </tbody>
                    </table>
                </div>
            <% } %>
        </div>

            </main>

            <!-- Footer -->
            <footer class="footer">
                <p>&copy; 2026 Campus Event Management System | Hibernate Event Analytics</p>
            </footer>
        </div>
    </div>

</body>
</html>
