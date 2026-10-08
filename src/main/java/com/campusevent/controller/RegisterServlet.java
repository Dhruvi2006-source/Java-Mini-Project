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
 * Controller Servlet for Student Registration.
 */
@WebServlet("/register")
public class RegisterServlet extends HttpServlet {

    private UserDAO userDAO;

    @Override
    public void init() throws ServletException {
        super.init();
        userDAO = new UserDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        // Render registration form
        request.getRequestDispatcher("/register.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        String name = request.getParameter("name");
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        String confirmPassword = request.getParameter("confirmPassword");

        // Server-side input validations
        if (name == null || name.trim().isEmpty() ||
            email == null || email.trim().isEmpty() ||
            password == null || password.trim().isEmpty() ||
            confirmPassword == null || confirmPassword.trim().isEmpty()) {
            
            sendError(request, response, "All fields are required.", name, email);
            return;
        }

        name = name.trim();
        email = email.trim().toLowerCase();

        // Simple Email Format Validation
        if (!email.matches("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$")) {
            sendError(request, response, "Please enter a valid email address.", name, email);
            return;
        }

        // Minimum Password Length Validation
        if (password.length() < 6) {
            sendError(request, response, "Password must be at least 6 characters long.", name, email);
            return;
        }

        // Password Confirmation Match Validation
        if (!password.equals(confirmPassword)) {
            sendError(request, response, "Passwords do not match. Please re-enter.", name, email);
            return;
        }

        // Check Duplicate Email in Database
        if (userDAO.isEmailExists(email)) {
            sendError(request, response, "An account with this email already exists. Please login instead.", name, email);
            return;
        }

        // Create new student user object
        User student = new User(name, email, password, "STUDENT");

        boolean isRegistered = userDAO.registerUser(student);

        if (isRegistered) {
            HttpSession session = request.getSession();
            session.setAttribute("successMessage", "Registration successful! Please login with your credentials.");
            response.sendRedirect(request.getContextPath() + "/login.jsp");
        } else {
            sendError(request, response, "Registration failed due to a system error. Please try again.", name, email);
        }
    }

    private void sendError(HttpServletRequest request, HttpServletResponse response, 
                           String message, String name, String email) 
            throws ServletException, IOException {
        request.setAttribute("errorMessage", message);
        request.setAttribute("paramName", name != null ? name : "");
        request.setAttribute("paramEmail", email != null ? email : "");
        request.getRequestDispatcher("/register.jsp").forward(request, response);
    }
}
