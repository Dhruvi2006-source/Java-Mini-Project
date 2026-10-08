<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Student Registration - Campus Event Management System</title>
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
                <li><a href="${pageContext.request.contextPath}/login.jsp">Login</a></li>
                <li><a href="${pageContext.request.contextPath}/register.jsp" class="active">Register</a></li>
            </ul>
        </div>
    </nav>

    <!-- Registration Container -->
    <main class="main-wrapper">
        <div class="auth-container">
            <div class="card">
                
                <div class="card-header">
                    <h2 class="card-title">Student Registration</h2>
                    <p class="card-subtitle">Create an account to participate in campus events</p>
                </div>

                <!-- Display Error Alert if present -->
                <% if (request.getAttribute("errorMessage") != null) { %>
                    <div class="alert alert-danger">
                        ⚠️ <%= request.getAttribute("errorMessage") %>
                    </div>
                <% } %>

                <form action="${pageContext.request.contextPath}/register" method="POST" autocomplete="off">
                    
                    <div class="form-group">
                        <label for="name" class="form-label">Full Name</label>
                        <input type="text" id="name" name="name" class="form-control" 
                               placeholder="e.g. John Doe" required 
                               value="<%= request.getAttribute("paramName") != null ? request.getAttribute("paramName") : "" %>">
                    </div>

                    <div class="form-group">
                        <label for="email" class="form-label">Email Address</label>
                        <input type="email" id="email" name="email" class="form-control" 
                               placeholder="e.g. john@campus.edu" required 
                               value="<%= request.getAttribute("paramEmail") != null ? request.getAttribute("paramEmail") : "" %>">
                    </div>

                    <div class="form-group">
                        <label for="password" class="form-label">Password</label>
                        <input type="password" id="password" name="password" class="form-control" 
                               placeholder="At least 6 characters" required>
                    </div>

                    <div class="form-group">
                        <label for="confirmPassword" class="form-label">Confirm Password</label>
                        <input type="password" id="confirmPassword" name="confirmPassword" class="form-control" 
                               placeholder="Re-enter password" required>
                    </div>

                    <button type="submit" class="btn btn-primary btn-block mt-4">
                        Register Account ➔
                    </button>

                </form>

                <div class="text-center mt-4">
                    <p style="font-size: 0.9rem; color: var(--text-secondary);">
                        Already registered? 
                        <a href="${pageContext.request.contextPath}/login.jsp" class="link">Login here</a>
                    </p>
                </div>

            </div>
        </div>
    </main>

    <!-- Footer -->
    <footer class="footer">
        <p>&copy; 2026 Campus Event Management System | Student Registration</p>
    </footer>

</body>
</html>
