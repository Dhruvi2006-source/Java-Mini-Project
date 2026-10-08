<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
    String userName = (String) session.getAttribute("userName");
    String userRole = (String) session.getAttribute("userRole");
    boolean isLoggedIn = (userName != null && userRole != null);
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Campus Event Management System</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>

    <!-- Header Navigation -->
    <nav class="navbar">
        <div class="nav-container">
            <a href="${pageContext.request.contextPath}/index.jsp" class="brand-logo">
                <span class="brand-icon">🎓</span>
                Campus Event Portal
            </a>
            <ul class="nav-links">
                <li><a href="${pageContext.request.contextPath}/index.jsp" class="active">Home</a></li>
                <% if (isLoggedIn) { %>
                    <% if ("ADMIN".equalsIgnoreCase(userRole)) { %>
                        <li><a href="${pageContext.request.contextPath}/admin/dashboard.jsp">Admin Dashboard</a></li>
                    <% } else { %>
                        <li><a href="${pageContext.request.contextPath}/student/dashboard.jsp">Student Dashboard</a></li>
                    <% } %>
                    <li><a href="${pageContext.request.contextPath}/logout" class="btn btn-outline" style="color: #ffffff; border-color: rgba(255,255,255,0.4);">Logout</a></li>
                <% } else { %>
                    <li><a href="${pageContext.request.contextPath}/login.jsp">Login</a></li>
                    <li><a href="${pageContext.request.contextPath}/register.jsp" class="btn btn-primary">Register</a></li>
                <% } %>
            </ul>
        </div>
    </nav>

    <!-- Main Content -->
    <main class="main-wrapper">
        
        <!-- Hero Section -->
        <section class="hero-banner">
            <h1 class="hero-title">Campus Event Management System</h1>
            <p class="hero-subtitle">
                Discover upcoming technical symposiums, workshops, cultural fests, and sports events. 
                Register easily and manage all your campus event participation in one central platform.
            </p>
            <div class="hero-actions">
                <% if (!isLoggedIn) { %>
                    <a href="${pageContext.request.contextPath}/register.jsp" class="btn btn-primary" style="padding: 0.85rem 2rem; font-size: 1rem;">
                        Student Registration 🚀
                    </a>
                    <a href="${pageContext.request.contextPath}/login.jsp" class="btn btn-secondary" style="padding: 0.85rem 2rem; font-size: 1rem;">
                        User Login 🔑
                    </a>
                <% } else { %>
                    <% if ("ADMIN".equalsIgnoreCase(userRole)) { %>
                        <a href="${pageContext.request.contextPath}/admin/dashboard.jsp" class="btn btn-primary" style="padding: 0.85rem 2rem; font-size: 1rem;">
                            Go to Admin Dashboard ⚙️
                        </a>
                    <% } else { %>
                        <a href="${pageContext.request.contextPath}/student/dashboard.jsp" class="btn btn-primary" style="padding: 0.85rem 2rem; font-size: 1rem;">
                            Go to Student Dashboard 🎯
                        </a>
                    <% } %>
                <% } %>
            </div>
        </section>

        <!-- System Overview & Feature Cards -->
        <h2 style="font-size: 1.5rem; font-weight: 700; margin-bottom: 1.5rem; text-align: center;">Platform Highlights</h2>

        <div class="features-grid">
            
            <div class="feature-card">
                <div class="feature-icon">📅</div>
                <h3 class="feature-title">Explore Events</h3>
                <p class="feature-desc">
                    View detailed schedules, venues, and descriptions for seminars, hackathons, and campus activities.
                </p>
            </div>

            <div class="feature-card">
                <div class="feature-icon">⚡</div>
                <h3 class="feature-title">Instant Registration</h3>
                <p class="feature-desc">
                    One-click enrollment for events with automated seat capacity management and instant verification.
                </p>
            </div>

            <div class="feature-card">
                <div class="feature-icon">📊</div>
                <h3 class="feature-title">Student Dashboard</h3>
                <p class="feature-desc">
                    Track your registered events, access schedule details, and manage your participation status seamlessly.
                </p>
            </div>

            <div class="feature-card">
                <div class="feature-icon">🛡️</div>
                <h3 class="feature-title">Admin Management</h3>
                <p class="feature-desc">
                    Administrators can post new events, manage capacities, and track student attendance in real time.
                </p>
            </div>

        </div>

    </main>

    <!-- Footer -->
    <footer class="footer">
        <p>&copy; 2026 Campus Event Management System | Java Web Project (JSP + Servlet + JDBC + MySQL)</p>
    </footer>

</body>
</html>
