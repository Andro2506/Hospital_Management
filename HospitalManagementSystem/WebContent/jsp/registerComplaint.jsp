<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="com.hospital.model.Complaint" %>
<%
    if (session.getAttribute("visitor") == null) {
        response.sendRedirect(request.getContextPath() + "/jsp/visitorLogin.jsp?expired=1");
        return;
    }
    request.setAttribute("pageTitle", "Register Complaint");
    List<Complaint> complaints = (List<Complaint>) request.getAttribute("complaints");
%>
<%@ include file="/jsp/includes/header.jsp" %>

<h1 class="page-title">Register a Complaint</h1>

<div class="card">
    <h2>Tell us what went wrong</h2>
    <form method="post" action="<%= request.getContextPath() %>/registerComplaint" data-validate-form>
        <div class="field">
            <label for="issue">Describe your issue *</label>
            <textarea id="issue" name="issue" rows="5" maxlength="500" required
                      data-validate="required:Please describe your complaint.|maxlen:500:Max 500 characters."></textarea>
            <span class="help">Up to 500 characters. We will review and follow up.</span>
        </div>
        <div class="actions">
            <button class="btn btn-primary" type="submit">Submit Complaint</button>
            <a class="btn btn-secondary" href="<%= request.getContextPath() %>/jsp/visitorDashboard.jsp">Back</a>
        </div>
    </form>
</div>

<% if (complaints != null && !complaints.isEmpty()) { %>
<div class="card">
    <h2>My Complaints</h2>
    <div class="table-wrap">
    <table class="data">
        <thead>
            <tr><th>#</th><th>Complaint</th></tr>
        </thead>
        <tbody>
        <% for (Complaint c : complaints) { %>
            <tr>
                <td><%= c.getComplaintId() %></td>
                <td><%= c.getIssue() %></td>
            </tr>
        <% } %>
        </tbody>
    </table>
    </div>
</div>
<% } %>

<%@ include file="/jsp/includes/footer.jsp" %>
