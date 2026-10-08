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
 * Controller Servlet for Managing Events (Listing, Searching, Filtering) for Admins.
 */
@WebServlet("/admin/manage-events")
public class AdminManageEventsServlet extends HttpServlet {

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

        String keyword = request.getParameter("keyword");
        String venue = request.getParameter("venue");

        List<Event> eventsList;
        if ((keyword != null && !keyword.trim().isEmpty()) || (venue != null && !venue.trim().isEmpty() && !"ALL".equalsIgnoreCase(venue))) {
            eventsList = eventDAO.searchEvents(keyword, venue);
        } else {
            eventsList = eventDAO.getAllEvents();
        }

        Map<Integer, Integer> registeredCountMap = new HashMap<>();
        for (Event event : eventsList) {
            int count = registrationDAO.getRegisteredCount(event.getId());
            registeredCountMap.put(event.getId(), count);
        }

        List<String> venuesList = eventDAO.getAllVenues();

        request.setAttribute("eventsList", eventsList);
        request.setAttribute("venuesList", venuesList);
        request.setAttribute("registeredCountMap", registeredCountMap);
        request.setAttribute("paramKeyword", keyword != null ? keyword.trim() : "");
        request.setAttribute("paramVenue", venue != null ? venue.trim() : "ALL");

        request.getRequestDispatcher("/admin/manage-events.jsp").forward(request, response);
    }
}
