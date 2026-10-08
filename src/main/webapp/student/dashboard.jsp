<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.List" %>
<%@ page import="com.campusevent.model.Registration" %>
<%
    response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
    response.setHeader("Pragma", "no-cache");
    response.setDateHeader("Expires", 0);

    String userName = (String) session.getAttribute("userName");
    String userEmail = (String) session.getAttribute("userEmail");
    String userRole = (String) session.getAttribute("userRole");

    // Session Security Check
    if (userName == null || userRole == null || !"STUDENT".equalsIgnoreCase(userRole)) {
        response.sendRedirect(request.getContextPath() + "/login.jsp");
        return;
    }

    // If attributes are missing because user accessed dashboard.jsp directly, forward to StudentDashboardServlet
    if (request.getAttribute("totalEventsCount") == null) {
        request.getRequestDispatcher("/student/dashboard").forward(request, response);
        return;
    }

    int totalEventsCount = (Integer) request.getAttribute("totalEventsCount");
    int myRegistrationsCount = (Integer) request.getAttribute("myRegistrationsCount");
    List<Registration> upcomingRegistrations = (List<Registration>) request.getAttribute("upcomingRegistrations");
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Student Dashboard - Campus Event Management System</title>
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
                <li><a href="${pageContext.request.contextPath}/student/dashboard" class="active"><span class="menu-icon">📊</span> <span>Dashboard</span></a></li>
                <div class="sidebar-menu-category">Events</div>
                <li><a href="${pageContext.request.contextPath}/student/events"><span class="menu-icon">🎉</span> <span>Browse Events</span></a></li>
                <li><a href="${pageContext.request.contextPath}/student/my-registrations"><span class="menu-icon">📋</span> <span>My Registrations</span></a></li>
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
                    <h2>Student Dashboard</h2>
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
        
        <!-- Welcome Header -->
        <div class="dashboard-header">
            <div>
                <h1 class="welcome-title">Welcome back, <%= userName %>!</h1>
                <p class="welcome-subtitle">Student Portal &bull; <%= userEmail %></p>
            </div>
            <div>
                <a href="${pageContext.request.contextPath}/logout" class="btn btn-danger">
                    Logout 🚪
                </a>
            </div>
        </div>

        <!-- Real Database Stats Grid -->
        <div class="stats-grid">
            <div class="stat-card">
                <div class="stat-icon">📅</div>
                <div>
                    <div class="stat-val"><%= totalEventsCount %></div>
                    <div class="stat-lbl">Total Campus Events</div>
                </div>
            </div>

            <div class="stat-card">
                <div class="stat-icon">📋</div>
                <div>
                    <div class="stat-val"><%= myRegistrationsCount %></div>
                    <div class="stat-lbl">My Enrolled Events</div>
                </div>
            </div>

            <div class="stat-card">
                <div class="stat-icon">✅</div>
                <div>
                    <div class="stat-val">Verified</div>
                    <div class="stat-lbl">Student Status</div>
                </div>
            </div>
        </div>

        <!-- Student Action Grid -->
        <div class="dashboard-grid" style="margin-bottom: 2.5rem;">
            
            <div class="action-card">
                <div>
                    <div class="action-card-header">
                        <div class="action-icon">🔍</div>
                        <h3 class="action-card-title">Browse Events Catalog</h3>
                    </div>
                    <p class="action-card-desc">
                        Explore upcoming technical symposiums, coding competitions, workshops, and cultural fests.
                    </p>
                </div>
                <div>
                    <a href="${pageContext.request.contextPath}/student/events" class="btn btn-primary btn-block">
                        Explore <%= totalEventsCount %> Available Events ➔
                    </a>
                </div>
            </div>

            <div class="action-card">
                <div>
                    <div class="action-card-header">
                        <div class="action-icon">📋</div>
                        <h3 class="action-card-title">My Event Registrations</h3>
                    </div>
                    <p class="action-card-desc">
                        View schedule details, venues, timings, or manage your active event enrollments.
                    </p>
                </div>
                <div>
                    <a href="${pageContext.request.contextPath}/student/my-registrations" class="btn btn-secondary btn-block">
                        View My Registrations (<%= myRegistrationsCount %> Enrolled)
                    </a>
                </div>
            </div>

        </div>

        <!-- Upcoming Registered Events Table Section -->
        <h2 style="font-size: 1.35rem; font-weight: 700; margin-bottom: 1.25rem;">My Upcoming Enrolled Events</h2>

        <% if (upcomingRegistrations == null || upcomingRegistrations.isEmpty()) { %>
            <div class="empty-state">
                <div class="empty-state-icon">📌</div>
                <h3 class="empty-state-title">No Enrolled Events</h3>
                <p class="empty-state-desc">You are currently not registered for any upcoming events.</p>
                <a href="${pageContext.request.contextPath}/student/events" class="btn btn-primary">Browse & Register Now</a>
            </div>
        <% } else { %>
            <div class="responsive-table-wrapper">
                <table class="data-table">
                    <thead>
                        <tr>
                            <th>#</th>
                            <th>Event Name</th>
                            <th>Event Date</th>
                            <th>Time</th>
                            <th>Venue</th>
                            <th>Registration Status</th>
                            <th>Action</th>
                        </tr>
                    </thead>
                    <tbody>
                        <% 
                        int idx = 1;
                        for (Registration reg : upcomingRegistrations) { 
                        %>
                            <tr>
                                <td><strong><%= idx++ %></strong></td>
                                <td>
                                    <a href="${pageContext.request.contextPath}/student/event-details?id=<%= reg.getEventId() %>" class="link">
                                        <%= reg.getEventTitle() %>
                                    </a>
                                </td>
                                <td><%= reg.getEventDate() %></td>
                                <td><%= reg.getEventTime() %></td>
                                <td><%= reg.getVenue() %></td>
                                <td><span class="badge-status badge-registered">Confirmed</span></td>
                                <td>
                                    <a href="${pageContext.request.contextPath}/student/event-details?id=<%= reg.getEventId() %>" class="btn btn-outline btn-sm">
                                        Details
                                    </a>
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
                <p>&copy; 2026 Campus Event Management System | Student Portal</p>
            </footer>
        </div>
    </div>

</body>
</html>
