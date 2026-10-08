package com.campusevent.controller;

import com.campusevent.dao.EventDAO;
import com.campusevent.model.Event;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * Controller Servlet for Deleting Campus Events (Admin functionality).
 * Destructive operation enforced via POST request.
 */
@WebServlet("/admin/delete-event")
public class DeleteEventServlet extends HttpServlet {

    private EventDAO eventDAO;

    @Override
    public void init() throws ServletException {
        super.init();
        eventDAO = new EventDAO();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("userId") == null || !"ADMIN".equalsIgnoreCase((String) session.getAttribute("userRole"))) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        String eventIdParam = request.getParameter("eventId");

        if (eventIdParam == null || eventIdParam.trim().isEmpty()) {
            session.setAttribute("errorMessage", "Invalid event deletion request.");
            response.sendRedirect(request.getContextPath() + "/admin/manage-events");
            return;
        }

        int eventId;
        try {
            eventId = Integer.parseInt(eventIdParam.trim());
        } catch (NumberFormatException e) {
            session.setAttribute("errorMessage", "Invalid event ID format.");
            response.sendRedirect(request.getContextPath() + "/admin/manage-events");
            return;
        }

        Event event = eventDAO.getEventById(eventId);
        String eventTitle = (event != null) ? event.getTitle() : "Event #" + eventId;

        // Execute deletion (Database FK ON DELETE CASCADE handles associated registrations safely)
        boolean success = eventDAO.deleteEvent(eventId);

        if (success) {
            session.setAttribute("successMessage", "Event '" + eventTitle + "' and its associated registrations were deleted successfully.");
        } else {
            session.setAttribute("errorMessage", "Failed to delete event due to a database error.");
        }

        response.sendRedirect(request.getContextPath() + "/admin/manage-events");
    }
}
