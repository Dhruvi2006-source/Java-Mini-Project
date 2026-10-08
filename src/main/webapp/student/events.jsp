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

    if (userName == null || userRole == null || !"STUDENT".equalsIgnoreCase(userRole)) {
        response.sendRedirect(request.getContextPath() + "/login.jsp");
        return;
    }

    List<Event> eventsList = (List<Event>) request.getAttribute("eventsList");
    List<String> venuesList = (List<String>) request.getAttribute("venuesList");
    Map<Integer, Integer> availableSeatsMap = (Map<Integer, Integer>) request.getAttribute("availableSeatsMap");
    Map<Integer, Boolean> isRegisteredMap = (Map<Integer, Boolean>) request.getAttribute("isRegisteredMap");

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
    <title>Browse Events - Campus Event Portal</title>
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
                <li><a href="${pageContext.request.contextPath}/student/events" class="active"><span class="menu-icon">🎉</span> <span>Browse Events</span></a></li>
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
                    <h2>Browse Events</h2>
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
                <h1 class="welcome-title">Campus Events Catalog</h1>
                <p class="welcome-subtitle">Explore upcoming workshops, symposiums, and cultural events</p>
            </div>
            <div>
                <a href="${pageContext.request.contextPath}/student/my-registrations" class="btn btn-secondary">
                    📋 My Registrations
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
            <form action="${pageContext.request.contextPath}/student/events" method="GET" class="search-form">
                
                <div class="search-input-group">
                    <label for="keyword" class="form-label">Search Event Title</label>
                    <input type="text" id="keyword" name="keyword" class="form-control" 
                           placeholder="Search by event title or topic..." value="<%= paramKeyword %>">
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
                        <a href="${pageContext.request.contextPath}/student/events" class="btn btn-secondary">
                            Reset
                        </a>
                    <% } %>
                </div>

            </form>
        </div>

        <!-- Events Grid -->
        <% if (eventsList == null || eventsList.isEmpty()) { %>
            <div class="empty-state">
                <div class="empty-state-icon">📅</div>
                <h3 class="empty-state-title">No Events Found</h3>
                <p class="empty-state-desc">There are no events matching your search criteria. Try resetting your search filter.</p>
                <a href="${pageContext.request.contextPath}/student/events" class="btn btn-outline">View All Events</a>
            </div>
        <% } else { %>
            <div class="events-grid">
                <% for (Event event : eventsList) { 
                    int available = (availableSeatsMap != null && availableSeatsMap.containsKey(event.getId())) ? availableSeatsMap.get(event.getId()) : 0;
                    boolean isReg = (isRegisteredMap != null && isRegisteredMap.containsKey(event.getId())) && isRegisteredMap.get(event.getId());
                %>
                    <div class="event-card">
                        <div>
                            <div class="event-card-header">
                                <div>
                                    <h3 class="event-card-title"><%= event.getTitle() %></h3>
                                    <div style="margin-top: 0.3rem;">
                                        <% if (event.isTeamEvent()) { %>
                                            <span class="badge-type badge-type-team">
                                                👥 TEAM EVENT (<%= event.getMinTeamSize() %>-<%= event.getMaxTeamSize() %> Members)
                                            </span>
                                        <% } else { %>
                                            <span class="badge-type badge-type-individual">
                                                👤 INDIVIDUAL EVENT
                                            </span>
                                        <% } %>
                                    </div>
                                </div>
                                <% if (isReg) { %>
                                    <span class="badge-status badge-registered">Registered</span>
                                <% } else if (available <= 0) { %>
                                    <span class="badge-status badge-full">Full</span>
                                <% } else { %>
                                    <span class="badge-status badge-available">Seats Open</span>
                                <% } %>
                            </div>

                            <p class="event-card-desc">
                                <%= event.getDescription() %>
                            </p>

                            <div class="event-meta-grid">
                                <div class="event-meta-item">
                                    <span>📅 Date:</span>
                                    <strong><%= event.getEventDate() %></strong>
                                </div>
                                <div class="event-meta-item">
                                    <span>⏰ Time:</span>
                                    <strong><%= event.getEventTime() %></strong>
                                </div>
                                <div class="event-meta-item">
                                    <span>📍 Venue:</span>
                                    <strong><%= event.getVenue() %></strong>
                                </div>
                                <div class="event-meta-item">
                                    <span>🪑 Available Seats:</span>
                                    <strong><%= available %> / <%= event.getCapacity() %></strong>
                                </div>
                            </div>
                        </div>

                        <div>
                            <a href="${pageContext.request.contextPath}/student/event-details?id=<%= event.getId() %>" class="btn btn-outline btn-block">
                                View Event Details ➔
                            </a>
                        </div>
                    </div>
                <% } %>
            </div>
        <% } %>

            </main>

            <!-- Footer -->
            <footer class="footer">
                <p>&copy; 2026 Campus Event Management System | Student Events Catalog</p>
            </footer>
        </div>
    </div>

</body>
</html>
