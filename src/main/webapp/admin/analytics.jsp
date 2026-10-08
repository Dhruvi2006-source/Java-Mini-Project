<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.List" %>
<%@ page import="com.campusevent.model.Event" %>
<%@ page import="com.campusevent.service.EventAnalyticsService.PopularEventDTO" %>
<%@ page import="com.campusevent.service.EventAnalyticsService.EventSummaryDTO" %>
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

    Long totalStudents = request.getAttribute("totalStudents") != null ? (Long) request.getAttribute("totalStudents") : 0L;
    Long totalEvents = request.getAttribute("totalEvents") != null ? (Long) request.getAttribute("totalEvents") : 0L;
    Long totalRegistrations = request.getAttribute("totalRegistrations") != null ? (Long) request.getAttribute("totalRegistrations") : 0L;
    Long upcomingEventsCount = request.getAttribute("upcomingEventsCount") != null ? (Long) request.getAttribute("upcomingEventsCount") : 0L;

    List<PopularEventDTO> popularEvents = (List<PopularEventDTO>) request.getAttribute("popularEvents");
    List<Event> upcomingEvents = (List<Event>) request.getAttribute("upcomingEvents");
    List<EventSummaryDTO> summaryList = (List<EventSummaryDTO>) request.getAttribute("summaryList");

    String paramKeyword = (String) request.getAttribute("paramKeyword");
    String paramEventType = (String) request.getAttribute("paramEventType");
    if (paramKeyword == null) paramKeyword = "";
    if (paramEventType == null) paramEventType = "ALL";
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Hibernate Event Analytics - Admin Portal</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
    <style>
        .progress-bar-bg {
            background: rgba(255, 255, 255, 0.1);
            border-radius: 10px;
            height: 10px;
            width: 100%;
            overflow: hidden;
        }
        .progress-bar-fill {
            height: 100%;
            border-radius: 10px;
            transition: width 0.4s ease;
        }
    </style>
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
                    <h2>Hibernate ORM Analytics</h2>
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
                <h1 class="welcome-title">Hibernate ORM Event Analytics</h1>
                <p class="welcome-subtitle">Real-time database analytics powered by Hibernate & HQL (Hibernate Query Language)</p>
            </div>
            <div>
                <span class="role-pill admin" style="background: rgba(16, 185, 129, 0.2); color: #34d399; font-size: 0.85rem; padding: 0.4rem 0.8rem; border: 1px solid rgba(16, 185, 129, 0.4);">
                    ⚡ Hibernate ORM Active
                </span>
            </div>
        </div>

        <!-- Metric Cards -->
        <div class="stats-grid" style="margin-bottom: 2rem;">
            <div class="stat-card">
                <div class="stat-icon">🎓</div>
                <div>
                    <div class="stat-val"><%= totalStudents %></div>
                    <div class="stat-lbl">Total Students</div>
                </div>
            </div>

            <div class="stat-card">
                <div class="stat-icon">📅</div>
                <div>
                    <div class="stat-val"><%= totalEvents %></div>
                    <div class="stat-lbl">Total Events</div>
                </div>
            </div>

            <div class="stat-card">
                <div class="stat-icon">📋</div>
                <div>
                    <div class="stat-val"><%= totalRegistrations %></div>
                    <div class="stat-lbl">Total Registrations</div>
                </div>
            </div>

            <div class="stat-card">
                <div class="stat-icon">⏳</div>
                <div>
                    <div class="stat-val"><%= upcomingEventsCount %></div>
                    <div class="stat-lbl">Upcoming Events</div>
                </div>
            </div>
        </div>

        <!-- HQL Analytics Grid: Popular & Upcoming -->
        <div style="display: grid; grid-template-columns: 1fr 1fr; gap: 1.5rem; margin-bottom: 2rem;">
            
            <!-- Most Popular Events (HQL Query 1) -->
            <div class="card" style="margin: 0;">
                <div class="card-header">
                    <h3 class="card-title" style="font-size: 1.15rem;">🔥 Most Popular Events (HQL)</h3>
                    <p class="card-subtitle">Events with highest student registration volume</p>
                </div>
                <% if (popularEvents == null || popularEvents.isEmpty()) { %>
                    <p style="color: var(--text-secondary); font-size: 0.9rem;">No registration data available.</p>
                <% } else { %>
                    <div style="display: flex; flex-direction: column; gap: 0.75rem;">
                        <% int rank = 1;
                           for (PopularEventDTO item : popularEvents) { %>
                            <div style="background: rgba(255, 255, 255, 0.04); padding: 0.75rem 1rem; border-radius: 8px; border: 1px solid rgba(255, 255, 255, 0.08); display: flex; align-items: center; justify-content: space-between;">
                                <div style="display: flex; align-items: center; gap: 0.75rem;">
                                    <span style="background: rgba(99, 102, 241, 0.2); color: #818cf8; width: 26px; height: 26px; border-radius: 50%; display: flex; align-items: center; justify-content: center; font-weight: bold; font-size: 0.8rem;">
                                        #<%= rank++ %>
                                    </span>
                                    <div>
                                        <a href="${pageContext.request.contextPath}/admin/event-analytics?eventId=<%= item.getEvent().getId() %>" class="link" style="font-weight: 600;">
                                            <%= item.getEvent().getTitle() %>
                                        </a>
                                        <div style="font-size: 0.78rem; color: var(--text-secondary);">
                                            <%= item.getEvent().getVenue() %> &bull; <%= item.getEvent().getEventType() %>
                                        </div>
                                    </div>
                                </div>
                                <span class="badge-status badge-registered" style="font-size: 0.8rem;">
                                    <%= item.getRegistrationCount() %> Enrolled
                                </span>
                            </div>
                        <% } %>
                    </div>
                <% } %>
            </div>

            <!-- Upcoming Events (HQL Query 2) -->
            <div class="card" style="margin: 0;">
                <div class="card-header">
                    <h3 class="card-title" style="font-size: 1.15rem;">📆 Upcoming Scheduled Events (HQL)</h3>
                    <p class="card-subtitle">Retrieved using HQL with named date parameters</p>
                </div>
                <% if (upcomingEvents == null || upcomingEvents.isEmpty()) { %>
                    <p style="color: var(--text-secondary); font-size: 0.9rem;">No upcoming events scheduled.</p>
                <% } else { %>
                    <div style="display: flex; flex-direction: column; gap: 0.75rem;">
                        <% for (Event e : upcomingEvents) { %>
                            <div style="background: rgba(255, 255, 255, 0.04); padding: 0.75rem 1rem; border-radius: 8px; border: 1px solid rgba(255, 255, 255, 0.08); display: flex; align-items: center; justify-content: space-between;">
                                <div>
                                    <a href="${pageContext.request.contextPath}/admin/event-analytics?eventId=<%= e.getId() %>" class="link" style="font-weight: 600;">
                                        <%= e.getTitle() %>
                                    </a>
                                    <div style="font-size: 0.78rem; color: var(--text-secondary);">
                                        📅 <%= e.getEventDate() %> &bull; ⏰ <%= e.getEventTime() %> &bull; 📍 <%= e.getVenue() %>
                                    </div>
                                </div>
                                <div>
                                    <% if (e.isTeamEvent()) { %>
                                        <span class="badge-type badge-type-team">
                                            👥 TEAM (<%= e.getMinTeamSize() %>-<%= e.getMaxTeamSize() %>)
                                        </span>
                                    <% } else { %>
                                        <span class="badge-type badge-type-individual">
                                            👤 INDIVIDUAL
                                        </span>
                                    <% } %>
                                </div>
                            </div>
                        <% } %>
                    </div>
                <% } %>
            </div>

        </div>

        <!-- HQL Search & Event Registration Summary Section -->
        <div class="card" style="margin: 0;">
            
            <div style="display: flex; justify-content: space-between; align-items: center; flex-wrap: wrap; gap: 1rem; margin-bottom: 1.25rem;">
                <div>
                    <h3 class="card-title" style="font-size: 1.2rem;">📊 Event Registration & Capacity Summary</h3>
                    <p class="card-subtitle">HQL aggregated event utilization metrics and capacity statistics</p>
                </div>

                <!-- HQL Filter Form -->
                <form action="${pageContext.request.contextPath}/admin/analytics" method="GET" style="display: flex; gap: 0.75rem; align-items: center;">
                    <input type="text" name="keyword" class="form-control" placeholder="Search HQL title..." value="<%= paramKeyword %>" style="padding: 0.45rem 0.75rem; font-size: 0.85rem; width: 180px;">
                    <select name="eventType" class="form-control" style="padding: 0.45rem 0.75rem; font-size: 0.85rem; width: 140px;" onchange="this.form.submit()">
                        <option value="ALL" <%= "ALL".equalsIgnoreCase(paramEventType) ? "selected" : "" %>>All Types</option>
                        <option value="INDIVIDUAL" <%= "INDIVIDUAL".equalsIgnoreCase(paramEventType) ? "selected" : "" %>>Individual</option>
                        <option value="TEAM" <%= "TEAM".equalsIgnoreCase(paramEventType) ? "selected" : "" %>>Team Event</option>
                    </select>
                    <button type="submit" class="btn btn-primary btn-sm">Filter</button>
                    <% if (!paramKeyword.isEmpty() || !"ALL".equalsIgnoreCase(paramEventType)) { %>
                        <a href="${pageContext.request.contextPath}/admin/analytics" class="btn btn-secondary btn-sm">Reset</a>
                    <% } %>
                </form>
            </div>

            <% if (summaryList == null || summaryList.isEmpty()) { %>
                <div class="empty-state">
                    <h3 class="empty-state-title">No Events Found</h3>
                    <p class="empty-state-desc">No events matched your HQL search filter criteria.</p>
                </div>
            <% } else { %>
                <div class="responsive-table-wrapper">
                    <table class="data-table">
                        <thead>
                            <tr>
                                <th>ID</th>
                                <th>Event Title</th>
                                <th>Type</th>
                                <th>Date & Venue</th>
                                <th>Enrolled / Capacity</th>
                                <th>Available Seats</th>
                                <th>Capacity Utilization %</th>
                                <th>Action</th>
                            </tr>
                        </thead>
                        <tbody>
                            <% for (EventSummaryDTO dto : summaryList) { 
                                Event e = dto.getEvent();
                                double pct = dto.getPercentage();
                                String fillColor = pct >= 90 ? "#ef4444" : (pct >= 60 ? "#06b6d4" : "#10b981");
                            %>
                                <tr>
                                    <td><strong>#<%= e.getId() %></strong></td>
                                    <td>
                                        <a href="${pageContext.request.contextPath}/admin/event-analytics?eventId=<%= e.getId() %>" class="link" style="font-weight: 600;">
                                            <%= e.getTitle() %>
                                        </a>
                                    </td>
                                    <td>
                                        <% if (e.isTeamEvent()) { %>
                                            <span class="badge-type badge-type-team">
                                                👥 TEAM (<%= e.getMinTeamSize() %>-<%= e.getMaxTeamSize() %>)
                                            </span>
                                        <% } else { %>
                                            <span class="badge-type badge-type-individual">
                                                👤 INDIVIDUAL
                                            </span>
                                        <% } %>
                                    </td>
                                    <td>
                                        <%= e.getEventDate() %><br>
                                        <small style="color: var(--text-secondary);"><%= e.getVenue() %></small>
                                    </td>
                                    <td>
                                        <strong><%= dto.getEnrolledCount() %></strong> / <%= e.getCapacity() %>
                                    </td>
                                    <td>
                                        <strong style="color: <%= dto.getAvailableSeats() > 0 ? "#34d399" : "#f87171" %>;"><%= dto.getAvailableSeats() %></strong>
                                    </td>
                                    <td style="min-width: 140px;">
                                        <div style="display: flex; justify-content: space-between; font-size: 0.75rem; margin-bottom: 0.2rem;">
                                            <span><%= String.format("%.1f%%", pct) %></span>
                                            <span><%= dto.getEnrolledCount() %>/<%= e.getCapacity() %></span>
                                        </div>
                                        <div class="progress-bar-bg">
                                            <div class="progress-bar-fill" style="width: <%= Math.min(100.0, pct) %>%; background: <%= fillColor %>;"></div>
                                        </div>
                                    </td>
                                    <td>
                                        <a href="${pageContext.request.contextPath}/admin/event-analytics?eventId=<%= e.getId() %>" class="btn btn-outline btn-sm">
                                            📊 View Analytics
                                        </a>
                                    </td>
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
                <p>&copy; 2026 Campus Event Management System | Hibernate Analytics Module</p>
            </footer>
        </div>
    </div>

</body>
</html>
