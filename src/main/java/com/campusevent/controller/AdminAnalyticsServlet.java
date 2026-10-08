package com.campusevent.controller;

import com.campusevent.model.Event;
import com.campusevent.service.EventAnalyticsService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;

/**
 * Admin Controller Servlet for the Hibernate Event Analytics Dashboard.
 * Enforces Admin authentication and delegates HQL queries to EventAnalyticsService.
 */
@WebServlet("/admin/analytics")
public class AdminAnalyticsServlet extends HttpServlet {

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

        String keyword = request.getParameter("keyword");
        String eventType = request.getParameter("eventType");

        try {
            // Retrieve dynamic real-time statistics via HQL queries
            long totalStudents = analyticsService.getTotalStudentsCount();
            long totalEvents = analyticsService.getTotalEventsCount();
            long totalRegistrations = analyticsService.getTotalRegistrationsCount();
            long upcomingEventsCount = analyticsService.getUpcomingEventsCount();

            List<EventAnalyticsService.PopularEventDTO> popularEvents = analyticsService.getMostPopularEvents(5);
            List<Event> upcomingEvents = analyticsService.getUpcomingEvents(5);
            List<EventAnalyticsService.EventSummaryDTO> summaryList = analyticsService.getEventRegistrationSummary(keyword, eventType);

            request.setAttribute("totalStudents", totalStudents);
            request.setAttribute("totalEvents", totalEvents);
            request.setAttribute("totalRegistrations", totalRegistrations);
            request.setAttribute("upcomingEventsCount", upcomingEventsCount);

            request.setAttribute("popularEvents", popularEvents);
            request.setAttribute("upcomingEvents", upcomingEvents);
            request.setAttribute("summaryList", summaryList);

            request.setAttribute("paramKeyword", keyword != null ? keyword : "");
            request.setAttribute("paramEventType", eventType != null ? eventType : "ALL");

            request.getRequestDispatcher("/admin/analytics.jsp").forward(request, response);

        } catch (Exception e) {
            System.err.println("AdminAnalyticsServlet Error: " + e.getMessage());
            e.printStackTrace();
            request.setAttribute("errorMessage", "Unable to load event analytics. Please try again.");
            request.getRequestDispatcher("/admin/analytics.jsp").forward(request, response);
        }
    }
}
