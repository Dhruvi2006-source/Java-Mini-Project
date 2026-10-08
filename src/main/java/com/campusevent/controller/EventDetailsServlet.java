package com.campusevent.controller;

import com.campusevent.dao.EventDAO;
import com.campusevent.dao.RegistrationDAO;
import com.campusevent.model.Event;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * Controller Servlet for Viewing Single Event Details.
 */
@WebServlet("/student/event-details")
public class EventDetailsServlet extends HttpServlet {

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
        String idParam = request.getParameter("id");

        if (idParam == null || idParam.trim().isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/student/events");
            return;
        }

        int eventId;
        try {
            eventId = Integer.parseInt(idParam.trim());
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/student/events");
            return;
        }

        Event event = eventDAO.getEventById(eventId);
        if (event == null) {
            session.setAttribute("errorMessage", "Requested event was not found.");
            response.sendRedirect(request.getContextPath() + "/student/events");
            return;
        }

        int registeredCount = registrationDAO.getRegisteredCount(eventId);
        int availableSeats = Math.max(0, event.getCapacity() - registeredCount);
        boolean isRegistered = registrationDAO.isUserRegistered(userId, eventId);
        boolean isFull = (availableSeats <= 0);

        request.setAttribute("event", event);
        request.setAttribute("registeredCount", registeredCount);
        request.setAttribute("availableSeats", availableSeats);
        request.setAttribute("isRegistered", isRegistered);
        request.setAttribute("isFull", isFull);

        request.getRequestDispatcher("/student/event-details.jsp").forward(request, response);
    }
}
