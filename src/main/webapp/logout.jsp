<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
    // Forward directly to LogoutServlet to process session invalidation
    response.sendRedirect(request.getContextPath() + "/logout");
%>
