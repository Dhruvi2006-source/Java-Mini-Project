<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
    String successMsg = (String) session.getAttribute("successMessage");
    if (successMsg != null) {
        // Remove after reading once
        session.removeAttribute("successMessage");
    }
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>User Login - Campus Event Management System</title>
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
                <li><a href="${pageContext.request.contextPath}/index.jsp">Home</a></li>
                <li><a href="${pageContext.request.contextPath}/login.jsp" class="active">Login</a></li>
                <li><a href="${pageContext.request.contextPath}/register.jsp">Register</a></li>
            </ul>
        </div>
    </nav>

    <!-- Login Container -->
    <main class="main-wrapper">
        <div class="auth-container">
            <div class="card">
                
                <div class="card-header">
                    <h2 class="card-title">Portal Login</h2>
                    <p class="card-subtitle">Access your Student or Admin account</p>
                </div>

                <!-- Display Success Alert if present -->
                <% if (successMsg != null) { %>
                    <div class="alert alert-success">
                        ✅ <%= successMsg %>
                    </div>
                <% } %>

                <!-- Display Error Alert if present -->
                <% if (request.getAttribute("errorMessage") != null) { %>
                    <div class="alert alert-danger">
                        ⚠️ <%= request.getAttribute("errorMessage") %>
                    </div>
                <% } %>

                <form action="${pageContext.request.contextPath}/login" method="POST" autocomplete="off">
                    
                    <div class="form-group">
                        <label for="email" class="form-label">Email Address</label>
                        <input type="email" id="email" name="email" class="form-control" 
                               placeholder="e.g. student@campus.edu or admin@campus.edu" required 
                               value="<%= request.getAttribute("paramEmail") != null ? request.getAttribute("paramEmail") : "" %>">
                    </div>

                    <div class="form-group">
                        <label for="password" class="form-label">Password</label>
                        <input type="password" id="password" name="password" class="form-control" 
                               placeholder="Enter your password" required>
                    </div>

                    <button type="submit" class="btn btn-primary btn-block mt-4">
                        Sign In 🔑
                    </button>

                </form>

                <div class="text-center mt-4">
                    <p style="font-size: 0.9rem; color: var(--text-secondary);">
                        Don't have an account? 
                        <a href="${pageContext.request.contextPath}/register.jsp" class="link">Register as Student</a>
                    </p>
                </div>

            </div>
        </div>
    </main>

    <!-- Footer -->
    <footer class="footer">
        <p>&copy; 2026 Campus Event Management System | Unified Authentication</p>
    </footer>

</body>
</html>
