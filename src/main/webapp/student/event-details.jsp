<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
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

    Event event = (Event) request.getAttribute("event");
    int registeredCount = request.getAttribute("registeredCount") != null ? (Integer) request.getAttribute("registeredCount") : 0;
    int availableSeats = request.getAttribute("availableSeats") != null ? (Integer) request.getAttribute("availableSeats") : 0;
    boolean isRegistered = request.getAttribute("isRegistered") != null && (Boolean) request.getAttribute("isRegistered");
    boolean isFull = request.getAttribute("isFull") != null && (Boolean) request.getAttribute("isFull");

    if (event == null) {
        response.sendRedirect(request.getContextPath() + "/student/events");
        return;
    }

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
    <title><%= event.getTitle() %> - Event Details</title>
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
                    <h2>Event Details</h2>
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

        <!-- Navigation Breadcrumb -->
        <div style="margin-bottom: 1.5rem;">
            <a href="${pageContext.request.contextPath}/student/events" class="link" style="font-size: 0.9rem;">
                ⬅ Back to All Events
            </a>
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

        <!-- Event Details Card -->
        <div class="event-details-card">
            
            <div class="event-details-header">
                <div>
                    <h1 class="event-details-title"><%= event.getTitle() %></h1>
                    <p style="color: var(--text-secondary); font-size: 0.95rem; margin-top: 0.25rem;">
                        Event ID #<%= event.getId() %> &bull; Posted on <%= event.getCreatedAt() %>
                    </p>
                </div>
                <div>
                    <% if (isRegistered) { %>
                        <span class="badge-status badge-registered" style="font-size: 0.95rem; padding: 0.5rem 1rem;">
                            ✅ Already Registered
                        </span>
                    <% } else if (isFull) { %>
                        <span class="badge-status badge-full" style="font-size: 0.95rem; padding: 0.5rem 1rem;">
                            ⛔ Registration Closed (Full)
                        </span>
                    <% } else { %>
                        <span class="badge-status badge-available" style="font-size: 0.95rem; padding: 0.5rem 1rem;">
                            🟢 Seats Available
                        </span>
                    <% } %>
                </div>
            </div>

            <!-- Metadata Cards Grid -->
            <div class="stats-grid" style="margin-bottom: 2rem;">
                <div class="stat-card">
                    <div class="stat-icon">📅</div>
                    <div>
                        <div class="stat-val" style="font-size: 1.1rem;"><%= event.getEventDate() %></div>
                        <div class="stat-lbl">Event Date</div>
                    </div>
                </div>

                <div class="stat-card">
                    <div class="stat-icon">⏰</div>
                    <div>
                        <div class="stat-val" style="font-size: 1.1rem;"><%= event.getEventTime() %></div>
                        <div class="stat-lbl">Start Time</div>
                    </div>
                </div>

                <div class="stat-card">
                    <div class="stat-icon">📍</div>
                    <div>
                        <div class="stat-val" style="font-size: 1.1rem;"><%= event.getVenue() %></div>
                        <div class="stat-lbl">Campus Venue</div>
                    </div>
                </div>

                <div class="stat-card">
                    <div class="stat-icon">🪑</div>
                    <div>
                        <div class="stat-val" style="font-size: 1.1rem;"><%= availableSeats %> / <%= event.getCapacity() %></div>
                        <div class="stat-lbl">Available Seats (<%= registeredCount %> enrolled)</div>
                    </div>
                </div>

                <div class="stat-card" style="grid-column: span 2;">
                    <div class="stat-icon"><%= event.isTeamEvent() ? "👥" : "👤" %></div>
                    <div>
                        <div class="stat-val" style="font-size: 1.1rem;">
                            <% if (event.isTeamEvent()) { %>
                                TEAM EVENT (<%= event.getMinTeamSize() %> - <%= event.getMaxTeamSize() %> members)
                            <% } else { %>
                                INDIVIDUAL EVENT
                            <% } %>
                        </div>
                        <div class="stat-lbl">Registration Requirement</div>
                    </div>
                </div>
            </div>

            <!-- Event Overview Description -->
            <div class="event-details-section">
                <h3>About This Event</h3>
                <p style="color: var(--text-primary); font-size: 1rem; line-height: 1.7; background-color: rgba(255,255,255,0.03); padding: 1.25rem; border-radius: var(--radius-sm); border: 1px solid var(--border-color);">
                    <%= event.getDescription() %>
                </p>
            </div>

            <!-- Action Area -->
            <div style="border-top: 1px solid var(--border-color); padding-top: 1.5rem; display: flex; justify-content: space-between; align-items: center; flex-wrap: wrap; gap: 1rem;">
                <div>
                    <a href="${pageContext.request.contextPath}/student/events" class="btn btn-secondary">
                        Back to Events Catalog
                    </a>
                </div>

                <div>
                    <% if (isRegistered) { %>
                        <form action="${pageContext.request.contextPath}/student/cancel-registration" method="POST" style="display: inline;" onsubmit="return confirm('Are you sure you want to cancel registration for this event?');">
                            <input type="hidden" name="eventId" value="<%= event.getId() %>">
                            <button type="submit" class="btn btn-danger">
                                Cancel Registration ❌
                            </button>
                        </form>
                    <% } else if (isFull) { %>
                        <button type="button" class="btn btn-secondary" disabled style="opacity: 0.7; cursor: not-allowed;">
                            Registration Closed (Event Full)
                        </button>
                    <% } else if (event.isTeamEvent()) { %>
                        <a href="${pageContext.request.contextPath}/student/register-team?eventId=<%= event.getId() %>" class="btn btn-primary" style="padding: 0.85rem 2rem; font-size: 1.05rem; background: linear-gradient(135deg, #8b5cf6 0%, #6366f1 100%);">
                            👥 Create & Register Team 🚀
                        </a>
                    <% } else { %>
                        <form action="${pageContext.request.contextPath}/student/register-event" method="POST" style="display: inline;">
                            <input type="hidden" name="eventId" value="<%= event.getId() %>">
                            <button type="submit" class="btn btn-primary" style="padding: 0.85rem 2rem; font-size: 1.05rem;">
                                Register For Event 🚀
                            </button>
                        </form>
                    <% } %>
                </div>
            </div>

        </div>

            </main>

            <!-- Footer -->
            <footer class="footer">
                <p>&copy; 2026 Campus Event Management System | Event Specification</p>
            </footer>
        </div>
    </div>

</body>
</html>
