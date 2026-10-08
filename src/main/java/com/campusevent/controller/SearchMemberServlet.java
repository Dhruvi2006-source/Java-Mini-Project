package com.campusevent.controller;

import com.campusevent.dao.TeamDAO;
import com.campusevent.model.User;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Servlet for searching registered student accounts by email for team creation.
 * Returns JSON formatted response for dynamically displaying student name and email.
 */
@WebServlet("/student/search-member")
public class SearchMemberServlet extends HttpServlet {

    private TeamDAO teamDAO;

    @Override
    public void init() throws ServletException {
        super.init();
        teamDAO = new TeamDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("userId") == null || !"STUDENT".equalsIgnoreCase((String) session.getAttribute("userRole"))) {
            out.print("{\"success\":false,\"message\":\"Unauthorized access. Please log in as a student.\"}");
            out.flush();
            return;
        }

        int currentLeaderId = (Integer) session.getAttribute("userId");
        String email = request.getParameter("email");
        String eventIdStr = request.getParameter("eventId");

        if (eventIdStr == null || eventIdStr.trim().isEmpty()) {
            out.print("{\"success\":false,\"message\":\"Invalid event reference.\"}");
            out.flush();
            return;
        }

        int eventId;
        try {
            eventId = Integer.parseInt(eventIdStr.trim());
        } catch (NumberFormatException e) {
            out.print("{\"success\":false,\"message\":\"Invalid event ID.\"}");
            out.flush();
            return;
        }

        TeamDAO.SearchResult result = teamDAO.searchMemberByEmail(email, currentLeaderId, eventId);

        if (result.success && result.user != null) {
            String jsonUser = String.format(
                "{\"success\":true,\"message\":\"Student found.\",\"userId\":%d,\"name\":\"%s\",\"email\":\"%s\"}",
                result.user.getId(),
                escapeJson(result.user.getName()),
                escapeJson(result.user.getEmail())
            );
            out.print(jsonUser);
        } else {
            String jsonErr = String.format(
                "{\"success\":false,\"message\":\"%s\"}",
                escapeJson(result.message)
            );
            out.print(jsonErr);
        }
        out.flush();
    }

    private String escapeJson(String input) {
        if (input == null) return "";
        return input.replace("\\", "\\\\")
                    .replace("\"", "\\\"")
                    .replace("\n", "\\n")
                    .replace("\r", "\\r");
    }
}
