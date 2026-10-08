package com.campusevent.controller;

import com.campusevent.dao.EventDAO;
import com.campusevent.dao.RegistrationDAO;
import com.campusevent.model.Event;
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
 * Controller Servlet for Viewing Student Registrations per Event (Admin functionality).
 */
@WebServlet("/admin/event-registrations")
public class AdminViewRegistrationsServlet extends HttpServlet {

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
        if (session == null || session.getAttribute("userId") == null || !"ADMIN".equalsIgnoreCase((String) session.getAttribute("userRole"))) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        String eventIdParam = request.getParameter("eventId");
        int eventIdFilter = 0;
        if (eventIdParam != null && !eventIdParam.trim().isEmpty()) {
            try {
                eventIdFilter = Integer.parseInt(eventIdParam.trim());
            } catch (NumberFormatException e) {
                eventIdFilter = 0;
            }
        }

        List<Event> eventsList = eventDAO.getAllEvents();
        List<Registration> registrationsList = registrationDAO.getAllRegistrationsWithDetails(eventIdFilter);

        request.setAttribute("eventsList", eventsList);
        request.setAttribute("registrationsList", registrationsList);
        request.setAttribute("selectedEventId", eventIdFilter);

        request.getRequestDispatcher("/admin/event-registrations.jsp").forward(request, response);
    }
}
