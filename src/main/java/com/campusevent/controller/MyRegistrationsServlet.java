package com.campusevent.controller;

import com.campusevent.dao.RegistrationDAO;
import com.campusevent.model.Registration;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;

/**
 * Controller Servlet for Viewing Student's Event Registrations.
 */
@WebServlet("/student/my-registrations")
public class MyRegistrationsServlet extends HttpServlet {

    private RegistrationDAO registrationDAO;

    @Override
    public void init() throws ServletException {
        super.init();
        registrationDAO = new RegistrationDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("userId") == null || !"STUDENT".equalsIgnoreCase((String) session.getAttribute("userRole"))) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        int userId = (Integer) session.getAttribute("userId");

        List<Registration> registrationsList = registrationDAO.getRegistrationsByUser(userId);

        request.setAttribute("registrationsList", registrationsList);
        request.getRequestDispatcher("/student/my-registrations.jsp").forward(request, response);
    }
}
