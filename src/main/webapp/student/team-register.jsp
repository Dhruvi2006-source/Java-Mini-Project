<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="com.campusevent.model.Event" %>
<%
    response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
    response.setHeader("Pragma", "no-cache");
    response.setDateHeader("Expires", 0);

    String userName = (String) session.getAttribute("userName");
    String userEmail = (String) session.getAttribute("userEmail");
    String userRole = (String) session.getAttribute("userRole");
    Integer userId = (Integer) session.getAttribute("userId");

    if (userName == null || userRole == null || !"STUDENT".equalsIgnoreCase(userRole)) {
        response.sendRedirect(request.getContextPath() + "/login.jsp");
        return;
    }

    Event event = (Event) request.getAttribute("event");
    int availableSeats = request.getAttribute("availableSeats") != null ? (Integer) request.getAttribute("availableSeats") : 0;
    String paramTeamName = (String) request.getAttribute("paramTeamName");
    if (paramTeamName == null) paramTeamName = "";

    if (event == null || !event.isTeamEvent()) {
        response.sendRedirect(request.getContextPath() + "/student/events");
        return;
    }
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Team Registration - <%= event.getTitle() %></title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
    <style>
        .member-card {
            background: rgba(255, 255, 255, 0.05);
            border: 1px solid rgba(255, 255, 255, 0.12);
            border-radius: var(--radius-sm);
            padding: 0.85rem 1rem;
            display: flex;
            align-items: center;
            justify-content: space-between;
            margin-bottom: 0.75rem;
            transition: var(--transition-fast);
        }
        .member-card:hover {
            border-color: var(--accent-color);
            background: rgba(99, 102, 241, 0.08);
        }
        .member-info {
            display: flex;
            align-items: center;
            gap: 0.75rem;
        }
        .leader-badge {
            background: linear-gradient(135deg, #06b6d4 0%, #0284c7 100%);
            color: #ffffff;
            font-size: 0.7rem;
            font-weight: 700;
            padding: 0.2rem 0.5rem;
            border-radius: 4px;
            text-transform: uppercase;
        }
        .member-badge {
            background: rgba(59, 130, 246, 0.2);
            color: #60a5fa;
            font-size: 0.7rem;
            font-weight: 700;
            padding: 0.2rem 0.5rem;
            border-radius: 4px;
            text-transform: uppercase;
        }
        .search-result-box {
            background: rgba(16, 185, 129, 0.1);
            border: 1px solid rgba(16, 185, 129, 0.3);
            border-radius: var(--radius-sm);
            padding: 1rem;
            margin-top: 0.75rem;
            display: flex;
            align-items: center;
            justify-content: space-between;
        }
    </style>
</head>
<body>

    <div class="app-layout">
        <!-- Sidebar Navigation -->
        <aside class="sidebar">
            <div class="sidebar-brand">
                <span class="brand-icon">🎓</span>
                <div>
                    <div class="brand-title">Campus Events</div>
                    <div class="brand-sub">Student Portal</div>
                </div>
            </div>
            <ul class="sidebar-menu">
                <div class="sidebar-menu-category">Main Navigation</div>
                <li><a href="${pageContext.request.contextPath}/index.jsp"><span class="menu-icon">🏠</span> <span>Home</span></a></li>
                <li><a href="${pageContext.request.contextPath}/student/dashboard"><span class="menu-icon">📊</span> <span>Dashboard</span></a></li>
                <div class="sidebar-menu-category">Events</div>
                <li><a href="${pageContext.request.contextPath}/student/events"><span class="menu-icon">🎉</span> <span>Browse Events</span></a></li>
                <li><a href="${pageContext.request.contextPath}/student/my-registrations"><span class="menu-icon">📋</span> <span>My Registrations</span></a></li>
            </ul>
            <div class="sidebar-footer">
                <a href="${pageContext.request.contextPath}/logout" class="sidebar-logout-btn">
                    <span>🚪</span> <span>Logout</span>
                </a>
            </div>
        </aside>

        <!-- Main App Area -->
        <div class="app-main">
            <!-- Top Header Navbar -->
            <header class="top-header">
                <div class="top-header-title">
                    <span class="app-name-tag">Campus Student Portal</span>
                    <span class="breadcrumb-sep">/</span>
                    <h2>Team Registration</h2>
                </div>
                <div class="top-header-right">
                    <div class="user-badge">
                        <span>👤 <%= userName %></span>
                        <span class="role-pill student">STUDENT</span>
                    </div>
                </div>
            </header>

            <!-- Page Body -->
            <main class="content-body">

        <div style="margin-bottom: 1.5rem;">
            <a href="${pageContext.request.contextPath}/student/event-details?id=<%= event.getId() %>" class="link">
                ⬅ Back to Event Details
            </a>
        </div>

        <div class="card" style="max-width: 720px; margin: 0 auto;">
            
            <div class="card-header">
                <div style="display: flex; justify-content: space-between; align-items: flex-start; flex-wrap: wrap; gap: 0.5rem;">
                    <div>
                        <h2 class="card-title">Create Your Team</h2>
                        <p class="card-subtitle">Event: <strong><%= event.getTitle() %></strong></p>
                    </div>
                    <div style="text-align: right;">
                        <span class="role-pill student" style="background: rgba(139, 92, 246, 0.2); color: #c084fc; font-size: 0.8rem;">
                            👥 Team Size: <%= event.getMinTeamSize() %> - <%= event.getMaxTeamSize() %> Members
                        </span>
                        <div style="font-size: 0.8rem; color: var(--text-secondary); margin-top: 0.2rem;">
                            Available Seats: <strong><%= availableSeats %></strong>
                        </div>
                    </div>
                </div>
            </div>

            <!-- Error Alert -->
            <% if (request.getAttribute("errorMessage") != null) { %>
                <div class="alert alert-danger" id="serverAlert">
                    ⚠️ <%= request.getAttribute("errorMessage") %>
                </div>
            <% } %>

            <div id="jsAlert" class="alert alert-danger" style="display: none;"></div>

            <form action="${pageContext.request.contextPath}/student/register-team" method="POST" id="teamForm" autocomplete="off">
                
                <input type="hidden" name="eventId" value="<%= event.getId() %>">

                <!-- Team Name -->
                <div class="form-group">
                    <label for="teamName" class="form-label">Team Name <span style="color: #ef4444;">*</span></label>
                    <input type="text" id="teamName" name="teamName" class="form-control" 
                           placeholder="e.g. Code Warriors / Java Cyber Squad" required maxlength="100"
                           value="<%= paramTeamName %>">
                </div>

                <!-- Team Leader (Auto-populated from session) -->
                <div class="form-group">
                    <label class="form-label">Team Leader (Member 1)</label>
                    <div class="member-card" style="border-color: #06b6d4; background: rgba(6, 182, 212, 0.08);">
                        <div class="member-info">
                            <span style="font-size: 1.2rem;">👑</span>
                            <div>
                                <strong style="color: #ffffff;"><%= userName %></strong>
                                <span class="leader-badge" style="margin-left: 0.5rem;">TEAM LEADER</span>
                                <div style="font-size: 0.82rem; color: var(--text-secondary);"><%= userEmail %></div>
                            </div>
                        </div>
                        <span style="font-size: 0.8rem; color: #06b6d4;">(You)</span>
                    </div>
                </div>

                <!-- Add Team Members Section -->
                <div class="form-group" style="background: rgba(255, 255, 255, 0.03); padding: 1.25rem; border-radius: var(--radius-sm); border: 1px solid rgba(255, 255, 255, 0.08);">
                    <label for="searchEmail" class="form-label">Search Team Member by Registered Email</label>
                    <div style="display: flex; gap: 0.75rem;">
                        <input type="email" id="searchEmail" class="form-control" 
                               placeholder="e.g. student2@gmail.com" style="flex: 1;">
                        <button type="button" id="btnSearch" class="btn btn-secondary" onclick="searchStudent()">
                            🔍 Search Student
                        </button>
                    </div>

                    <!-- Search Result Display Box -->
                    <div id="searchResultContainer" style="display: none;"></div>
                </div>

                <!-- Selected Team Members List -->
                <div class="form-group">
                    <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 0.75rem;">
                        <label class="form-label" style="margin-bottom: 0;">
                            Current Team Roster (<span id="currentTeamCount">1</span> / <%= event.getMaxTeamSize() %>)
                        </label>
                        <small style="color: var(--text-secondary);" id="sizeRequirementHint">
                            Required: Min <%= event.getMinTeamSize() %>, Max <%= event.getMaxTeamSize() %> members
                        </small>
                    </div>

                    <div id="membersList">
                        <!-- Dynamic hidden inputs and added member cards appear here -->
                    </div>
                </div>

                <!-- Form Submit Button -->
                <div style="display: flex; gap: 1rem; margin-top: 1.5rem;">
                    <button type="submit" class="btn btn-primary btn-block" style="background: linear-gradient(135deg, #8b5cf6 0%, #6366f1 100%);">
                        Register Team 🚀
                    </button>
                    <a href="${pageContext.request.contextPath}/student/event-details?id=<%= event.getId() %>" class="btn btn-secondary">
                        Cancel
                    </a>
                </div>

            </form>

        </div>

            </main>

            <!-- Footer -->
            <footer class="footer">
                <p>&copy; 2026 Campus Event Management System | Team Event Registration</p>
            </footer>
        </div>
    </div>

    <script>
        const minTeamSize = <%= event.getMinTeamSize() %>;
        const maxTeamSize = <%= event.getMaxTeamSize() %>;
        const currentLeaderId = <%= userId %>;
        const eventId = <%= event.getId() %>;
        
        let teamMembers = []; // Array of added user objects {userId, name, email}

        function showAlert(msg) {
            const jsAlert = document.getElementById('jsAlert');
            jsAlert.style.display = 'block';
            jsAlert.innerHTML = '⚠️ ' + msg;
        }

        function hideAlert() {
            const jsAlert = document.getElementById('jsAlert');
            jsAlert.style.display = 'none';
        }

        function searchStudent() {
            hideAlert();
            const emailInput = document.getElementById('searchEmail');
            const resultBox = document.getElementById('searchResultContainer');
            const email = emailInput.value.trim();

            if (!email) {
                showAlert("Please enter a student email address to search.");
                resultBox.style.display = 'none';
                return;
            }

            // Check if already added in current team
            if (teamMembers.some(m => m.email.toLowerCase() === email.toLowerCase())) {
                showAlert("This student is already added to your team roster.");
                resultBox.style.display = 'none';
                return;
            }

            const searchBtn = document.getElementById('btnSearch');
            searchBtn.disabled = true;
            searchBtn.innerText = "Searching...";

            fetch('${pageContext.request.contextPath}/student/search-member?eventId=' + eventId + '&email=' + encodeURIComponent(email))
                .then(response => response.json())
                .then(data => {
                    searchBtn.disabled = false;
                    searchBtn.innerText = "🔍 Search Student";

                    if (data.success) {
                        resultBox.style.display = 'block';
                        resultBox.className = 'search-result-box';
                        resultBox.innerHTML = `
                            <div>
                                <div style="font-size: 0.8rem; color: #10b981; font-weight: 700; text-transform: uppercase;">Student Account Found</div>
                                <strong style="color: #ffffff; font-size: 1.05rem;">` + escapeHtml(data.name) + `</strong>
                                <div style="font-size: 0.85rem; color: var(--text-secondary);">` + escapeHtml(data.email) + `</div>
                            </div>
                            <button type="button" class="btn btn-primary btn-sm" onclick="addMember(` + data.userId + `, '` + escapeJs(data.name) + `', '` + escapeJs(data.email) + `')">
                                ➕ Add Member
                            </button>
                        `;
                    } else {
                        resultBox.style.display = 'block';
                        resultBox.className = 'alert alert-danger';
                        resultBox.style.marginTop = '0.75rem';
                        resultBox.innerHTML = '⚠️ ' + escapeHtml(data.message);
                    }
                })
                .catch(err => {
                    searchBtn.disabled = false;
                    searchBtn.innerText = "🔍 Search Student";
                    showAlert("Failed to search student. Please try again.");
                });
        }

        function addMember(userId, name, email) {
            hideAlert();
            if (teamMembers.length + 1 >= maxTeamSize) {
                if (teamMembers.length + 1 > maxTeamSize) {
                    showAlert("Maximum team size (" + maxTeamSize + " members) reached.");
                    return;
                }
            }

            // Check duplicate
            if (userId === currentLeaderId) {
                showAlert("You are already the team leader.");
                return;
            }
            if (teamMembers.some(m => m.userId === userId)) {
                showAlert("This student is already added to your team.");
                return;
            }

            teamMembers.push({userId: userId, name: name, email: email});

            // Clear search UI
            document.getElementById('searchEmail').value = '';
            document.getElementById('searchResultContainer').style.display = 'none';

            renderMembers();
        }

        function removeMember(index) {
            teamMembers.splice(index, 1);
            renderMembers();
        }

        function renderMembers() {
            const container = document.getElementById('membersList');
            const countSpan = document.getElementById('currentTeamCount');
            
            // Leader = 1 + added members count
            const totalCount = 1 + teamMembers.length;
            countSpan.innerText = totalCount;

            let html = '';
            teamMembers.forEach((m, idx) => {
                html += `
                    <div class="member-card">
                        <input type="hidden" name="memberIds" value="` + m.userId + `">
                        <div class="member-info">
                            <span style="font-size: 1.1rem;">👤</span>
                            <div>
                                <strong style="color: #ffffff;">` + escapeHtml(m.name) + `</strong>
                                <span class="member-badge" style="margin-left: 0.5rem;">Member ` + (idx + 2) + `</span>
                                <div style="font-size: 0.82rem; color: var(--text-secondary);">` + escapeHtml(m.email) + `</div>
                            </div>
                        </div>
                        <button type="button" class="btn btn-outline btn-sm" style="color: #ef4444; border-color: rgba(239,68,68,0.4);" onclick="removeMember(` + idx + `)">
                            ❌ Remove
                        </button>
                    </div>
                `;
            });

            container.innerHTML = html;
        }

        document.getElementById('teamForm').addEventListener('submit', function(e) {
            hideAlert();
            const totalCount = 1 + teamMembers.length;
            
            if (totalCount < minTeamSize) {
                e.preventDefault();
                showAlert("Team size (" + totalCount + ") is below the minimum required (" + minTeamSize + " members) for this event.");
                return false;
            }

            if (totalCount > maxTeamSize) {
                e.preventDefault();
                showAlert("Team size (" + totalCount + ") exceeds maximum allowed (" + maxTeamSize + " members).");
                return false;
            }
        });

        function escapeHtml(str) {
            if (!str) return '';
            return str.replace(/&/g, '&amp;').replace(/</g, '&lt;').replace(/>/g, '&gt;').replace(/"/g, '&quot;');
        }

        function escapeJs(str) {
            if (!str) return '';
            return str.replace(/\\/g, '\\\\').replace(/'/g, "\\'").replace(/"/g, '\\"');
        }
    </script>

</body>
</html>
