package com.campusevent.controller;

import com.campusevent.dao.EventDAO;
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
 * Controller Servlet for Student Dashboard with Live Database Statistics.
 */
@WebServlet("/student/dashboard")
public class StudentDashboardServlet extends HttpServlet {

    private EventDAO eventDAO;
    private RegistrationDAO registrationDAO;

    @Override
    public void init() throws ServletException {
        super.init();
        eventDAO = new EventDAO();
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

        int totalEventsCount = eventDAO.getTotalEventsCount();
        int myRegistrationsCount = registrationDAO.getRegistrationCountByUser(userId);
        List<Registration> upcomingRegistrations = registrationDAO.getRegistrationsByUser(userId);

        request.setAttribute("totalEventsCount", totalEventsCount);
        request.setAttribute("myRegistrationsCount", myRegistrationsCount);
        request.setAttribute("upcomingRegistrations", upcomingRegistrations);

        request.getRequestDispatcher("/student/dashboard.jsp").forward(request, response);
    }
}
