package com.campusevent.controller;

import com.campusevent.service.EventAnalyticsService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * Admin Controller Servlet for Detailed Event Analytics.
 * Provides deep-dive statistics for individual and team events using Hibernate ORM.
 */
@WebServlet("/admin/event-analytics")
public class AdminEventAnalyticsServlet extends HttpServlet {

    private EventAnalyticsService analyticsService;

    @Override
    public void init() throws ServletException {
        super.init();
        analyticsService = new EventAnalyticsService();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("userId") == null || !"ADMIN".equalsIgnoreCase((String) session.getAttribute("userRole"))) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        String eventIdStr = request.getParameter("eventId");
        if (eventIdStr == null || eventIdStr.trim().isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/admin/analytics");
            return;
        }

        int eventId;
        try {
            eventId = Integer.parseInt(eventIdStr.trim());
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/admin/analytics");
            return;
        }

        try {
            EventAnalyticsService.DetailedEventAnalyticsDTO analytics = analyticsService.getEventAnalyticsDetails(eventId);

            if (analytics == null || analytics.getEvent() == null) {
                session.setAttribute("errorMessage", "Requested event was not found.");
                response.sendRedirect(request.getContextPath() + "/admin/analytics");
                return;
            }

            request.setAttribute("analytics", analytics);
            request.getRequestDispatcher("/admin/event-analytics.jsp").forward(request, response);

        } catch (Exception e) {
            System.err.println("AdminEventAnalyticsServlet Error: " + e.getMessage());
            e.printStackTrace();
            session.setAttribute("errorMessage", "Unable to load event analytics details.");
            response.sendRedirect(request.getContextPath() + "/admin/analytics");
        }
    }
}
