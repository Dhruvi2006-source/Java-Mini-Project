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
import java.sql.Date;
import java.sql.Time;

/**
 * Controller Servlet for Editing Existing Campus Events (Admin functionality).
 */
@WebServlet("/admin/edit-event")
public class EditEventServlet extends HttpServlet {

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

        String idParam = request.getParameter("id");
        if (idParam == null || idParam.trim().isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/admin/manage-events");
            return;
        }

        int eventId;
        try {
            eventId = Integer.parseInt(idParam.trim());
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/admin/manage-events");
            return;
        }

        Event event = eventDAO.getEventById(eventId);
        if (event == null) {
            session.setAttribute("errorMessage", "Requested event was not found.");
            response.sendRedirect(request.getContextPath() + "/admin/manage-events");
            return;
        }

        int registeredCount = registrationDAO.getRegisteredCount(eventId);

        request.setAttribute("event", event);
        request.setAttribute("registeredCount", registeredCount);

        request.getRequestDispatcher("/admin/edit-event.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("userId") == null || !"ADMIN".equalsIgnoreCase((String) session.getAttribute("userRole"))) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        String idStr = request.getParameter("id");
        String title = request.getParameter("title");
        String description = request.getParameter("description");
        String eventDateStr = request.getParameter("eventDate");
        String eventTimeStr = request.getParameter("eventTime");
        String venue = request.getParameter("venue");
        String capacityStr = request.getParameter("capacity");
        String eventType = request.getParameter("eventType");
        String minTeamSizeStr = request.getParameter("minTeamSize");
        String maxTeamSizeStr = request.getParameter("maxTeamSize");

        if (idStr == null || idStr.trim().isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/admin/manage-events");
            return;
        }

        int eventId;
        try {
            eventId = Integer.parseInt(idStr.trim());
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/admin/manage-events");
            return;
        }

        Event existingEvent = eventDAO.getEventById(eventId);
        if (existingEvent == null) {
            session.setAttribute("errorMessage", "Requested event was not found.");
            response.sendRedirect(request.getContextPath() + "/admin/manage-events");
            return;
        }

        int registeredCount = registrationDAO.getRegisteredCount(eventId);

        // Input Validations
        if (title == null || title.trim().isEmpty() ||
            description == null || description.trim().isEmpty() ||
            eventDateStr == null || eventDateStr.trim().isEmpty() ||
            eventTimeStr == null || eventTimeStr.trim().isEmpty() ||
            venue == null || venue.trim().isEmpty() ||
            capacityStr == null || capacityStr.trim().isEmpty()) {

            sendError(request, response, existingEvent, registeredCount, "All fields are required.", title, description, eventDateStr, eventTimeStr, venue, capacityStr);
            return;
        }

        title = title.trim();
        description = description.trim();
        venue = venue.trim();
        if (eventType == null || !eventType.equalsIgnoreCase("TEAM")) {
            eventType = "INDIVIDUAL";
        } else {
            eventType = "TEAM";
        }

        if (title.length() > 150) {
            sendError(request, response, existingEvent, registeredCount, "Title must not exceed 150 characters.", title, description, eventDateStr, eventTimeStr, venue, capacityStr);
            return;
        }

        int newCapacity;
        try {
            newCapacity = Integer.parseInt(capacityStr.trim());
            if (newCapacity <= 0) {
                sendError(request, response, existingEvent, registeredCount, "Capacity must be a positive integer greater than 0.", title, description, eventDateStr, eventTimeStr, venue, capacityStr);
                return;
            }
        } catch (NumberFormatException e) {
            sendError(request, response, existingEvent, registeredCount, "Please enter a valid numeric seat capacity.", title, description, eventDateStr, eventTimeStr, venue, capacityStr);
            return;
        }

        // Capacity Rule Validation: Prevent reducing capacity below current registrations count
        if (newCapacity < registeredCount) {
            sendError(request, response, existingEvent, registeredCount, 
                "Cannot reduce capacity to " + newCapacity + ". There are already " + registeredCount + " students registered for this event.", 
                title, description, eventDateStr, eventTimeStr, venue, capacityStr);
            return;
        }

        int minTeamSize = 1;
        int maxTeamSize = 1;

        if ("TEAM".equals(eventType)) {
            try {
                minTeamSize = Integer.parseInt(minTeamSizeStr != null ? minTeamSizeStr.trim() : "2");
                maxTeamSize = Integer.parseInt(maxTeamSizeStr != null ? maxTeamSizeStr.trim() : "4");
            } catch (NumberFormatException e) {
                sendError(request, response, existingEvent, registeredCount, "Please enter valid numbers for team sizes.", title, description, eventDateStr, eventTimeStr, venue, capacityStr);
                return;
            }

            if (minTeamSize < 2) {
                sendError(request, response, existingEvent, registeredCount, "Minimum team size must be at least 2 for a team event.", title, description, eventDateStr, eventTimeStr, venue, capacityStr);
                return;
            }

            if (maxTeamSize < minTeamSize) {
                sendError(request, response, existingEvent, registeredCount, "Maximum team size must be greater than or equal to minimum team size.", title, description, eventDateStr, eventTimeStr, venue, capacityStr);
                return;
            }
        }

        Date eventDate;
        try {
            eventDate = Date.valueOf(eventDateStr.trim());
        } catch (IllegalArgumentException e) {
            sendError(request, response, existingEvent, registeredCount, "Invalid date format. Please use YYYY-MM-DD.", title, description, eventDateStr, eventTimeStr, venue, capacityStr);
            return;
        }

        Time eventTime;
        try {
            String formattedTime = eventTimeStr.trim();
            if (formattedTime.length() == 5) {
                formattedTime += ":00";
            }
            eventTime = Time.valueOf(formattedTime);
        } catch (IllegalArgumentException e) {
            sendError(request, response, existingEvent, registeredCount, "Invalid time format. Please use HH:MM.", title, description, eventDateStr, eventTimeStr, venue, capacityStr);
            return;
        }

        // Construct updated event object
        Event updatedEvent = new Event(eventId, title, description, eventDate, eventTime, venue, newCapacity, eventType, minTeamSize, maxTeamSize, existingEvent.getCreatedAt());
        boolean success = eventDAO.updateEvent(updatedEvent);

        if (success) {
            session.setAttribute("successMessage", "Event '" + title + "' updated successfully!");
            response.sendRedirect(request.getContextPath() + "/admin/manage-events");
        } else {
            sendError(request, response, existingEvent, registeredCount, "Failed to update event due to a database error.", title, description, eventDateStr, eventTimeStr, venue, capacityStr);
        }
    }

    private void sendError(HttpServletRequest request, HttpServletResponse response, Event existingEvent, int registeredCount,
                           String message, String title, String description, String eventDate, String eventTime, String venue, String capacity) 
            throws ServletException, IOException {
        request.setAttribute("errorMessage", message);
        
        Event tempEvent = new Event();
        tempEvent.setId(existingEvent.getId());
        tempEvent.setTitle(title != null ? title : existingEvent.getTitle());
        tempEvent.setDescription(description != null ? description : existingEvent.getDescription());
        tempEvent.setVenue(venue != null ? venue : existingEvent.getVenue());
        
        try {
            tempEvent.setCapacity(Integer.parseInt(capacity));
        } catch (Exception e) {
            tempEvent.setCapacity(existingEvent.getCapacity());
        }
        
        try {
            tempEvent.setEventDate(Date.valueOf(eventDate));
        } catch (Exception e) {
            tempEvent.setEventDate(existingEvent.getEventDate());
        }

        request.setAttribute("event", tempEvent);
        request.setAttribute("registeredCount", registeredCount);
        request.getRequestDispatcher("/admin/edit-event.jsp").forward(request, response);
    }
}
