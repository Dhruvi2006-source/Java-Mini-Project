package com.campusevent.controller;

import com.campusevent.dao.EventDAO;
import com.campusevent.dao.RegistrationDAO;
import com.campusevent.dao.TeamDAO;
import com.campusevent.model.Event;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * Controller Servlet for Student Event Registration Cancellation.
 */
@WebServlet("/student/cancel-registration")
public class CancelRegistrationServlet extends HttpServlet {

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

        // Obtain student ID exclusively from session
        int userId = (Integer) session.getAttribute("userId");
        String eventIdParam = request.getParameter("eventId");

        if (eventIdParam == null || eventIdParam.trim().isEmpty()) {
            session.setAttribute("errorMessage", "Invalid cancellation request.");
            response.sendRedirect(request.getContextPath() + "/student/my-registrations");
            return;
        }

        int eventId;
        try {
            eventId = Integer.parseInt(eventIdParam.trim());
        } catch (NumberFormatException e) {
            session.setAttribute("errorMessage", "Invalid event ID format.");
            response.sendRedirect(request.getContextPath() + "/student/my-registrations");
            return;
        }

        Event event = eventDAO.getEventById(eventId);
        String eventTitle = (event != null) ? event.getTitle() : "Event";

        // Security Check: Verify registration belongs to logged-in user
        if (!registrationDAO.isUserRegistered(userId, eventId)) {
            session.setAttribute("errorMessage", "You are not registered for this event.");
            response.sendRedirect(request.getContextPath() + "/student/my-registrations");
            return;
        }

        boolean success = false;

        // Check if event is a Team Event
        if (event != null && event.isTeamEvent()) {
            TeamDAO teamDAO = new TeamDAO();
            com.campusevent.model.Team team = teamDAO.getTeamByEventAndUser(eventId, userId);
            
            if (team == null) {
                session.setAttribute("errorMessage", "Team details not found for this event.");
                response.sendRedirect(request.getContextPath() + "/student/my-registrations");
                return;
            }

            // Only Team Leader can cancel team registration
            if (team.getTeamLeaderId() != userId) {
                session.setAttribute("errorMessage", "Only the Team Leader (" + team.getTeamLeaderName() + ") can cancel the team registration for '" + eventTitle + "'.");
                response.sendRedirect(request.getContextPath() + "/student/my-registrations");
                return;
            }

            // Atomic cancellation of entire team & all member registrations
            success = teamDAO.cancelTeamRegistrationAtomic(eventId, userId);
            if (success) {
                session.setAttribute("successMessage", "Team '" + team.getTeamName() + "' and all member registrations for '" + eventTitle + "' have been cancelled successfully.");
            } else {
                session.setAttribute("errorMessage", "Failed to cancel team registration due to a system error.");
            }

        } else {
            // Individual Event Cancellation
            success = registrationDAO.cancelRegistration(userId, eventId);
            if (success) {
                session.setAttribute("successMessage", "Your registration for '" + eventTitle + "' has been cancelled. Seat capacity updated.");
            } else {
                session.setAttribute("errorMessage", "Failed to cancel registration due to a system error.");
            }
        }

        response.sendRedirect(request.getContextPath() + "/student/my-registrations");
    }
}
