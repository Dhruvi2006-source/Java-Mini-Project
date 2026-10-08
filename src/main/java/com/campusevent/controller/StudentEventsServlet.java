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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Controller Servlet for Browsing and Searching Campus Events for Students.
 */
@WebServlet("/student/events")
public class StudentEventsServlet extends HttpServlet {

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

        String keyword = request.getParameter("keyword");
        String venue = request.getParameter("venue");

        // Retrieve filtered or all events
        List<Event> eventsList;
        if ((keyword != null && !keyword.trim().isEmpty()) || (venue != null && !venue.trim().isEmpty() && !"ALL".equalsIgnoreCase(venue))) {
            eventsList = eventDAO.searchEvents(keyword, venue);
        } else {
            eventsList = eventDAO.getAllEvents();
        }

        // Map event IDs to available seats count & registration status for current student
        Map<Integer, Integer> availableSeatsMap = new HashMap<>();
        Map<Integer, Boolean> isRegisteredMap = new HashMap<>();

        for (Event event : eventsList) {
            int available = registrationDAO.getAvailableSeats(event.getId(), event.getCapacity());
            boolean registered = registrationDAO.isUserRegistered(userId, event.getId());
            
            availableSeatsMap.put(event.getId(), available);
            isRegisteredMap.put(event.getId(), registered);
        }

        List<String> venuesList = eventDAO.getAllVenues();

        request.setAttribute("eventsList", eventsList);
        request.setAttribute("venuesList", venuesList);
        request.setAttribute("availableSeatsMap", availableSeatsMap);
        request.setAttribute("isRegisteredMap", isRegisteredMap);
        request.setAttribute("paramKeyword", keyword != null ? keyword.trim() : "");
        request.setAttribute("paramVenue", venue != null ? venue.trim() : "ALL");

        request.getRequestDispatcher("/student/events.jsp").forward(request, response);
    }
}
