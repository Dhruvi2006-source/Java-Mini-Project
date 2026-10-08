<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
    response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
    response.setHeader("Pragma", "no-cache");
    response.setDateHeader("Expires", 0);

    String userName = (String) session.getAttribute("userName");
    String userEmail = (String) session.getAttribute("userEmail");
    String userRole = (String) session.getAttribute("userRole");

    // Access Control: Verify Admin Session
    if (userName == null || userRole == null || !"ADMIN".equalsIgnoreCase(userRole)) {
        response.sendRedirect(request.getContextPath() + "/login.jsp");
        return;
    }

    // Auto-forward to AdminDashboardServlet if data attributes are missing
    if (request.getAttribute("totalEventsCount") == null) {
        request.getRequestDispatcher("/admin/dashboard").forward(request, response);
        return;
    }

    int totalEventsCount = (Integer) request.getAttribute("totalEventsCount");
    int upcomingEventsCount = (Integer) request.getAttribute("upcomingEventsCount");
    int totalRegistrationsCount = (Integer) request.getAttribute("totalRegistrationsCount");
    int totalStudentsCount = (Integer) request.getAttribute("totalStudentsCount");
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Admin Dashboard - Campus Event Management System</title>
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
                <li><a href="${pageContext.request.contextPath}/admin/dashboard" class="active"><span class="menu-icon">📊</span> <span>Dashboard</span></a></li>
                <div class="sidebar-menu-category">Management</div>
                <li><a href="${pageContext.request.contextPath}/admin/manage-events"><span class="menu-icon">⚙️</span> <span>Manage Events</span></a></li>
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
                    <h2>Admin Dashboard</h2>
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
                
                <!-- Welcome Header -->
                <div class="dashboard-header">
                    <div>
                        <h1 class="welcome-title">System Administrator Dashboard</h1>
                        <p class="welcome-subtitle">Logged in as <%= userName %> &bull; <%= userEmail %></p>
                    </div>
                    <div>
                        <a href="${pageContext.request.contextPath}/admin/add-event" class="btn btn-primary">
                            ➕ Add New Event
                        </a>
                    </div>
                </div>

                <!-- Real Database Statistics Grid -->
                <div class="stats-grid">
                    <div class="stat-card">
                        <div class="stat-icon">📌</div>
                        <div>
                            <div class="stat-val"><%= totalEventsCount %></div>
                            <div class="stat-lbl">Total Published Events</div>
                        </div>
                    </div>

                    <div class="stat-card">
                        <div class="stat-icon">⏰</div>
                        <div>
                            <div class="stat-val"><%= upcomingEventsCount %></div>
                            <div class="stat-lbl">Upcoming Events</div>
                        </div>
                    </div>

                    <div class="stat-card">
                        <div class="stat-icon">📋</div>
                        <div>
                            <div class="stat-val"><%= totalRegistrationsCount %></div>
                            <div class="stat-lbl">Total Student Registrations</div>
                        </div>
                    </div>

                    <div class="stat-card">
                        <div class="stat-icon">👥</div>
                        <div>
                            <div class="stat-val"><%= totalStudentsCount %></div>
                            <div class="stat-lbl">Registered Students</div>
                        </div>
                    </div>
                </div>

                <!-- Administrative Controls Grid -->
                <h2 style="font-size: 1.35rem; font-weight: 700; margin-bottom: 1.25rem;">Administrator Operations</h2>
                
                <div class="dashboard-grid">
                    
                    <div class="action-card">
                        <div>
                            <div class="action-card-header">
                                <div class="action-icon">⚙️</div>
                                <h3 class="action-card-title">Manage Campus Events</h3>
                            </div>
                            <p class="action-card-desc">
                                View, search, edit schedules, update capacities, or delete existing campus events.
                            </p>
                        </div>
                        <div>
                            <a href="${pageContext.request.contextPath}/admin/manage-events" class="btn btn-primary btn-block">
                                Manage <%= totalEventsCount %> Events ➔
                            </a>
                        </div>
                    </div>

                    <div class="action-card">
                        <div>
                            <div class="action-card-header">
                                <div class="action-icon">➕</div>
                                <h3 class="action-card-title">Publish New Event</h3>
                            </div>
                            <p class="action-card-desc">
                                Post new workshops, symposiums, guest lectures, or sports tournaments.
                            </p>
                        </div>
                        <div>
                            <a href="${pageContext.request.contextPath}/admin/add-event" class="btn btn-outline btn-block">
                                Create New Event ➔
                            </a>
                        </div>
                    </div>

                    <div class="action-card">
                        <div>
                            <div class="action-card-header">
                                <div class="action-icon">📊</div>
                                <h3 class="action-card-title">Hibernate Event Analytics</h3>
                            </div>
                            <p class="action-card-desc">
                                Inspect dynamic ORM analytics, popular events, upcoming schedules, and team statistics using HQL queries.
                            </p>
                        </div>
                        <div>
                            <a href="${pageContext.request.contextPath}/admin/analytics" class="btn btn-primary btn-block">
                                Open Hibernate Analytics ➔
                            </a>
                        </div>
                    </div>

                    <div class="action-card">
                        <div>
                            <div class="action-card-header">
                                <div class="action-icon">📋</div>
                                <h3 class="action-card-title">Student Registrations</h3>
                            </div>
                            <p class="action-card-desc">
                                Inspect student enrollment lists per event, view attendee contact details and timestamps.
                            </p>
                        </div>
                        <div>
                            <a href="${pageContext.request.contextPath}/admin/event-registrations" class="btn btn-secondary btn-block">
                                View Registrations (<%= totalRegistrationsCount %> Total)
                            </a>
                        </div>
                    </div>

                </div>

            </main>

            <!-- Footer -->
            <footer class="footer">
                <p>&copy; 2026 Campus Event Management System | System Administrator Dashboard</p>
            </footer>
        </div>
    </div>

</body>
</html>
