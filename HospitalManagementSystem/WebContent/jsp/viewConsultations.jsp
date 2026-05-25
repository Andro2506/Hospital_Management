<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List, com.hospital.model.Consultation" %>
<%
    if (session.getAttribute("admin") == null) {
        response.sendRedirect(request.getContextPath() + "/jsp/login.jsp?expired=1");
        return;
    }
    request.setAttribute("pageTitle", "All Consultations");
    List<Consultation> consultations = (List<Consultation>) request.getAttribute("consultations");
%>
<%@ include file="/jsp/includes/header.jsp" %>

<h1 class="page-title">All Scheduled Consultations</h1>
<p class="subtle">Every consultation request submitted by patients. Update the
   workflow status as the request moves through the hospital.</p>

<div class="card">
    <% if (consultations == null || consultations.isEmpty()) { %>
        <p>No consultation requests have been submitted yet.</p>
    <% } else { %>
    <div class="table-wrap">
    <table class="data">
        <thead>
        <tr>
            <th>#</th>
            <th>Patient (PRN)</th>
            <th>Department</th>
            <th>Doctor</th>
            <th>Preferred Date</th>
            <th>Status</th>
            <th>Notes</th>
            <th>Update</th>
        </tr>
        </thead>
        <tbody>
        <% for (Consultation c : consultations) { %>
            <tr>
                <td><%= c.getConsultationId() %></td>
                <td>
                    <strong><%= c.getPatientName() == null ? "(unknown)" : c.getPatientName() %></strong><br/>
                    <span class="subtle"><%= c.getPatientId() %></span>
                </td>
                <td><%= c.getDepartment() %></td>
                <td><%= c.getDoctorName() %></td>
                <td><%= c.getPreferredDate() %></td>
                <td><%= c.getStatus() %></td>
                <td><%= c.getNotes() == null ? "" : c.getNotes() %></td>
                <td class="actions-cell">
                    <form method="post" action="<%= request.getContextPath() %>/viewConsultations"
                          style="display:flex;gap:6px;align-items:center;margin:0">
                        <input type="hidden" name="consultationId" value="<%= c.getConsultationId() %>" />
                        <%
                            String cur = c.getStatus() == null ? "" : c.getStatus();
                        %>
                        <select name="status" required>
                            <option value="PENDING"<%= "PENDING".equals(cur) ? " selected" : "" %>>Pending</option>
                            <option value="CONFIRMED"<%= "CONFIRMED".equals(cur) ? " selected" : "" %>>Confirmed</option>
                            <option value="COMPLETED"<%= "COMPLETED".equals(cur) ? " selected" : "" %>>Completed</option>
                        </select>
                        <button type="submit" class="btn btn-primary" style="padding:6px 12px;font-size:0.85rem">Save</button>
                    </form>
                </td>
            </tr>
        <% } %>
        </tbody>
    </table>
    </div>
    <% } %>

    <div class="actions">
        <a class="btn btn-secondary" href="<%= request.getContextPath() %>/jsp/adminDashboard.jsp">Back to Dashboard</a>
    </div>
</div>

<%@ include file="/jsp/includes/footer.jsp" %>
