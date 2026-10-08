package com.campusevent.controller;

import com.campusevent.dao.EventDAO;
import com.campusevent.dao.RegistrationDAO;
import com.campusevent.dao.UserDAO;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * Controller Servlet for Administrator Dashboard with Real Database Statistics.
 */
@WebServlet("/admin/dashboard")
public class AdminDashboardServlet extends HttpServlet {

    private EventDAO eventDAO;
    private RegistrationDAO registrationDAO;
    private UserDAO userDAO;

    @Override
    public void init() throws ServletException {
        super.init();
        eventDAO = new EventDAO();
        registrationDAO = new RegistrationDAO();
        userDAO = new UserDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("userId") == null || !"ADMIN".equalsIgnoreCase((String) session.getAttribute("userRole"))) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        // Retrieve real database stats
        int totalEventsCount = eventDAO.getTotalEventsCount();
        int upcomingEventsCount = eventDAO.getUpcomingEventsCount();
        int totalRegistrationsCount = registrationDAO.getTotalRegistrationsCount();
        int totalStudentsCount = userDAO.getTotalStudentsCount();

        request.setAttribute("totalEventsCount", totalEventsCount);
        request.setAttribute("upcomingEventsCount", upcomingEventsCount);
        request.setAttribute("totalRegistrationsCount", totalRegistrationsCount);
        request.setAttribute("totalStudentsCount", totalStudentsCount);

        request.getRequestDispatcher("/admin/dashboard.jsp").forward(request, response);
    }
}
