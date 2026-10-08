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
 * Controller Servlet for Student Event Registration.
 */
@WebServlet("/student/register-event")
public class RegisterEventServlet extends HttpServlet {

    private EventDAO eventDAO;
    private RegistrationDAO registrationDAO;

    @Override
    public void init() throws ServletException {
        super.init();
        eventDAO = new EventDAO();
        registrationDAO = new RegistrationDAO();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("userId") == null || !"STUDENT".equalsIgnoreCase((String) session.getAttribute("userRole"))) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        // CRITICAL SECURITY RULE: Obtain student ID exclusively from session, NEVER from request params
        int userId = (Integer) session.getAttribute("userId");
        String eventIdParam = request.getParameter("eventId");

        if (eventIdParam == null || eventIdParam.trim().isEmpty()) {
            session.setAttribute("errorMessage", "Invalid event registration request.");
            response.sendRedirect(request.getContextPath() + "/student/events");
            return;
        }

        int eventId;
        try {
            eventId = Integer.parseInt(eventIdParam.trim());
        } catch (NumberFormatException e) {
            session.setAttribute("errorMessage", "Invalid event ID format.");
            response.sendRedirect(request.getContextPath() + "/student/events");
            return;
        }

        // Server-Side Event Validation
        Event event = eventDAO.getEventById(eventId);
        if (event == null) {
            session.setAttribute("errorMessage", "The event you are trying to register for does not exist.");
            response.sendRedirect(request.getContextPath() + "/student/events");
            return;
        }

        // Check Duplicate Registration
        if (registrationDAO.isUserRegistered(userId, eventId)) {
            session.setAttribute("errorMessage", "You are already registered for '" + event.getTitle() + "'.");
            response.sendRedirect(request.getContextPath() + "/student/event-details?id=" + eventId);
            return;
        }

        // Capacity Logic Check
        int availableSeats = registrationDAO.getAvailableSeats(eventId, event.getCapacity());
        if (availableSeats <= 0) {
            session.setAttribute("errorMessage", "This event is currently full. Registration is closed.");
            response.sendRedirect(request.getContextPath() + "/student/event-details?id=" + eventId);
            return;
        }

        // Perform Database Registration
        boolean success = registrationDAO.registerForEvent(userId, eventId);

        if (success) {
            session.setAttribute("successMessage", "Successfully registered for '" + event.getTitle() + "'!");
            response.sendRedirect(request.getContextPath() + "/student/my-registrations");
        } else {
            session.setAttribute("errorMessage", "Registration failed due to a system error. Please try again.");
            response.sendRedirect(request.getContextPath() + "/student/event-details?id=" + eventId);
        }
    }
}
