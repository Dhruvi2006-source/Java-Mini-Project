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
import java.sql.Date;
import java.sql.Time;

/**
 * Controller Servlet for Adding New Campus Events (Admin functionality).
 */
@WebServlet("/admin/add-event")
public class AddEventServlet extends HttpServlet {

    private EventDAO eventDAO;

    @Override
    public void init() throws ServletException {
        super.init();
        eventDAO = new EventDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("userId") == null || !"ADMIN".equalsIgnoreCase((String) session.getAttribute("userRole"))) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        request.getRequestDispatcher("/admin/add-event.jsp").forward(request, response);
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

        String title = request.getParameter("title");
        String description = request.getParameter("description");
        String eventDateStr = request.getParameter("eventDate");
        String eventTimeStr = request.getParameter("eventTime");
        String venue = request.getParameter("venue");
        String capacityStr = request.getParameter("capacity");
        String eventType = request.getParameter("eventType");
        String minTeamSizeStr = request.getParameter("minTeamSize");
        String maxTeamSizeStr = request.getParameter("maxTeamSize");

        // Validation
        if (title == null || title.trim().isEmpty() ||
            description == null || description.trim().isEmpty() ||
            eventDateStr == null || eventDateStr.trim().isEmpty() ||
            eventTimeStr == null || eventTimeStr.trim().isEmpty() ||
            venue == null || venue.trim().isEmpty() ||
            capacityStr == null || capacityStr.trim().isEmpty()) {

            sendError(request, response, "All required fields must be filled out.", title, description, eventDateStr, eventTimeStr, venue, capacityStr, eventType, minTeamSizeStr, maxTeamSizeStr);
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
            sendError(request, response, "Title must not exceed 150 characters.", title, description, eventDateStr, eventTimeStr, venue, capacityStr, eventType, minTeamSizeStr, maxTeamSizeStr);
            return;
        }

        int capacity;
        try {
            capacity = Integer.parseInt(capacityStr.trim());
            if (capacity <= 0) {
                sendError(request, response, "Capacity must be a positive integer greater than 0.", title, description, eventDateStr, eventTimeStr, venue, capacityStr, eventType, minTeamSizeStr, maxTeamSizeStr);
                return;
            }
        } catch (NumberFormatException e) {
            sendError(request, response, "Please enter a valid numeric seat capacity.", title, description, eventDateStr, eventTimeStr, venue, capacityStr, eventType, minTeamSizeStr, maxTeamSizeStr);
            return;
        }

        int minTeamSize = 1;
        int maxTeamSize = 1;

        if ("TEAM".equals(eventType)) {
            try {
                minTeamSize = Integer.parseInt(minTeamSizeStr != null ? minTeamSizeStr.trim() : "2");
                maxTeamSize = Integer.parseInt(maxTeamSizeStr != null ? maxTeamSizeStr.trim() : "4");
            } catch (NumberFormatException e) {
                sendError(request, response, "Please enter valid numbers for team sizes.", title, description, eventDateStr, eventTimeStr, venue, capacityStr, eventType, minTeamSizeStr, maxTeamSizeStr);
                return;
            }

            if (minTeamSize < 2) {
                sendError(request, response, "Minimum team size must be at least 2 for a team event.", title, description, eventDateStr, eventTimeStr, venue, capacityStr, eventType, minTeamSizeStr, maxTeamSizeStr);
                return;
            }

            if (maxTeamSize < minTeamSize) {
                sendError(request, response, "Maximum team size must be greater than or equal to minimum team size.", title, description, eventDateStr, eventTimeStr, venue, capacityStr, eventType, minTeamSizeStr, maxTeamSizeStr);
                return;
            }
        }

        Date eventDate;
        try {
            eventDate = Date.valueOf(eventDateStr.trim());
        } catch (IllegalArgumentException e) {
            sendError(request, response, "Invalid date format. Please use YYYY-MM-DD.", title, description, eventDateStr, eventTimeStr, venue, capacityStr, eventType, minTeamSizeStr, maxTeamSizeStr);
            return;
        }

        Time eventTime;
        try {
            String formattedTime = eventTimeStr.trim();
            if (formattedTime.length() == 5) { // e.g. "14:30" -> "14:30:00"
                formattedTime += ":00";
            }
            eventTime = Time.valueOf(formattedTime);
        } catch (IllegalArgumentException e) {
            sendError(request, response, "Invalid time format. Please use HH:MM.", title, description, eventDateStr, eventTimeStr, venue, capacityStr, eventType, minTeamSizeStr, maxTeamSizeStr);
            return;
        }

        // Create and save event
        Event newEvent = new Event(title, description, eventDate, eventTime, venue, capacity, eventType, minTeamSize, maxTeamSize);
        boolean success = eventDAO.createEvent(newEvent);

        if (success) {
            session.setAttribute("successMessage", "Event '" + title + "' added successfully!");
            response.sendRedirect(request.getContextPath() + "/admin/manage-events");
        } else {
            sendError(request, response, "Failed to create event due to a database error.", title, description, eventDateStr, eventTimeStr, venue, capacityStr, eventType, minTeamSizeStr, maxTeamSizeStr);
        }
    }

    private void sendError(HttpServletRequest request, HttpServletResponse response, String message,
                           String title, String description, String eventDate, String eventTime, String venue, String capacity,
                           String eventType, String minTeamSize, String maxTeamSize) 
            throws ServletException, IOException {
        request.setAttribute("errorMessage", message);
        request.setAttribute("paramTitle", title != null ? title : "");
        request.setAttribute("paramDescription", description != null ? description : "");
        request.setAttribute("paramEventDate", eventDate != null ? eventDate : "");
        request.setAttribute("paramEventTime", eventTime != null ? eventTime : "");
        request.setAttribute("paramVenue", venue != null ? venue : "");
        request.setAttribute("paramCapacity", capacity != null ? capacity : "");
        request.setAttribute("paramEventType", eventType != null ? eventType : "INDIVIDUAL");
        request.setAttribute("paramMinTeamSize", minTeamSize != null ? minTeamSize : "2");
        request.setAttribute("paramMaxTeamSize", maxTeamSize != null ? maxTeamSize : "4");
        request.getRequestDispatcher("/admin/add-event.jsp").forward(request, response);
    }
}
