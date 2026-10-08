package com.campusevent.controller;

import com.campusevent.dao.UserDAO;
import com.campusevent.model.User;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * Controller Servlet for User Authentication (Student & Admin Login).
 */
@WebServlet("/login")
public class LoginServlet extends HttpServlet {

    private UserDAO userDAO;

    @Override
    public void init() throws ServletException {
        super.init();
        userDAO = new UserDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        // If user is already logged in, redirect to their dashboard
        HttpSession session = request.getSession(false);
        if (session != null && session.getAttribute("userRole") != null) {
            String role = (String) session.getAttribute("userRole");
            if ("ADMIN".equalsIgnoreCase(role)) {
                response.sendRedirect(request.getContextPath() + "/admin/dashboard.jsp");
                return;
            } else if ("STUDENT".equalsIgnoreCase(role)) {
                response.sendRedirect(request.getContextPath() + "/student/dashboard.jsp");
                return;
            }
        }
        
        request.getRequestDispatcher("/login.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        String email = request.getParameter("email");
        String password = request.getParameter("password");

        // Validate required inputs
        if (email == null || email.trim().isEmpty() || password == null || password.trim().isEmpty()) {
            request.setAttribute("errorMessage", "Email and Password are required.");
            request.setAttribute("paramEmail", email != null ? email.trim() : "");
            request.getRequestDispatcher("/login.jsp").forward(request, response);
            return;
        }

        email = email.trim();

        // Authenticate user against database
        User authenticatedUser = userDAO.authenticateUser(email, password);

        if (authenticatedUser != null) {
            // Invalidate old session to prevent session fixation
            HttpSession oldSession = request.getSession(false);
            if (oldSession != null) {
                oldSession.invalidate();
            }

            // Create fresh HTTP Session
            HttpSession session = request.getSession(true);
            session.setAttribute("userId", authenticatedUser.getId());
            session.setAttribute("userName", authenticatedUser.getName());
            session.setAttribute("userEmail", authenticatedUser.getEmail());
            session.setAttribute("userRole", authenticatedUser.getRole().toUpperCase());

            // Redirect according to user role
            if ("ADMIN".equalsIgnoreCase(authenticatedUser.getRole())) {
                response.sendRedirect(request.getContextPath() + "/admin/dashboard.jsp");
            } else {
                response.sendRedirect(request.getContextPath() + "/student/dashboard.jsp");
            }
        } else {
            request.setAttribute("errorMessage", "Invalid email or password. Please try again.");
            request.setAttribute("paramEmail", email);
            request.getRequestDispatcher("/login.jsp").forward(request, response);
        }
    }
}
