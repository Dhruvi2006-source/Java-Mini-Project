package com.campusevent.controller;

import com.campusevent.dao.EventDAO;
import com.campusevent.dao.RegistrationDAO;
import com.campusevent.dao.TeamDAO;
import com.campusevent.model.Event;
import com.campusevent.model.Team;
import com.campusevent.model.User;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Controller Servlet for Team Event Registration.
 * Handles display of team registration page and processing atomic team creation.
 */
@WebServlet("/student/register-team")
public class RegisterTeamServlet extends HttpServlet {

    private EventDAO eventDAO;
    private RegistrationDAO registrationDAO;
    private TeamDAO teamDAO;

    @Override
    public void init() throws ServletException {
        super.init();
        eventDAO = new EventDAO();
        registrationDAO = new RegistrationDAO();
        teamDAO = new TeamDAO();
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
        String eventIdStr = request.getParameter("eventId");

        if (eventIdStr == null || eventIdStr.trim().isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/student/events");
            return;
        }

        int eventId;
        try {
            eventId = Integer.parseInt(eventIdStr.trim());
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/student/events");
            return;
        }

        Event event = eventDAO.getEventById(eventId);
        if (event == null || !event.isTeamEvent()) {
            session.setAttribute("errorMessage", "Requested team event was not found or is an individual event.");
            response.sendRedirect(request.getContextPath() + "/student/events");
            return;
        }

        // Check if student is already registered for this event
        if (registrationDAO.isUserRegistered(userId, eventId)) {
            session.setAttribute("errorMessage", "You are already registered for '" + event.getTitle() + "'.");
            response.sendRedirect(request.getContextPath() + "/student/my-registrations");
            return;
        }

        int availableSeats = registrationDAO.getAvailableSeats(eventId, event.getCapacity());

        request.setAttribute("event", event);
        request.setAttribute("availableSeats", availableSeats);
        request.getRequestDispatcher("/student/team-register.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("userId") == null || !"STUDENT".equalsIgnoreCase((String) session.getAttribute("userRole"))) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        int leaderId = (Integer) session.getAttribute("userId");
        String eventIdStr = request.getParameter("eventId");
        String teamName = request.getParameter("teamName");
        String[] memberIdArr = request.getParameterValues("memberIds");

        if (eventIdStr == null || eventIdStr.trim().isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/student/events");
            return;
        }

        int eventId;
        try {
            eventId = Integer.parseInt(eventIdStr.trim());
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/student/events");
            return;
        }

        Event event = eventDAO.getEventById(eventId);
        if (event == null || !event.isTeamEvent()) {
            session.setAttribute("errorMessage", "Requested team event was not found.");
            response.sendRedirect(request.getContextPath() + "/student/events");
            return;
        }

        if (teamName == null || teamName.trim().isEmpty()) {
            request.setAttribute("errorMessage", "Please provide a Team Name.");
            request.setAttribute("event", event);
            request.setAttribute("availableSeats", registrationDAO.getAvailableSeats(eventId, event.getCapacity()));
            request.setAttribute("paramTeamName", teamName);
            request.getRequestDispatcher("/student/team-register.jsp").forward(request, response);
            return;
        }

        List<Integer> memberIds = new ArrayList<>();
        if (memberIdArr != null) {
            for (String midStr : memberIdArr) {
                try {
                    int mid = Integer.parseInt(midStr.trim());
                    if (mid != leaderId && !memberIds.contains(mid)) {
                        memberIds.add(mid);
                    }
                } catch (NumberFormatException ignored) {}
            }
        }

        // Execute Atomic Team Registration Transaction
        TeamDAO.TransactionResult result = teamDAO.registerTeamAtomic(eventId, teamName.trim(), leaderId, memberIds);

        if (result.success) {
            session.setAttribute("successMessage", result.message);
            response.sendRedirect(request.getContextPath() + "/student/my-registrations");
        } else {
            request.setAttribute("errorMessage", result.message);
            request.setAttribute("event", event);
            request.setAttribute("availableSeats", registrationDAO.getAvailableSeats(eventId, event.getCapacity()));
            request.setAttribute("paramTeamName", teamName);
            request.getRequestDispatcher("/student/team-register.jsp").forward(request, response);
        }
    }
}
