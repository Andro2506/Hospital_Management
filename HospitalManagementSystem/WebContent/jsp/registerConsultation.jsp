<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List, com.hospital.model.Consultation" %>
<%
    if (session.getAttribute("patient") == null) {
        response.sendRedirect(request.getContextPath() + "/jsp/patientLogin.jsp?expired=1");
        return;
    }
    request.setAttribute("pageTitle", "Register Consultation");
    List<Consultation> consultations = (List<Consultation>) request.getAttribute("consultations");
%>
<%@ include file="/jsp/includes/header.jsp" %>

<h1 class="page-title">Register for a Doctor Consultation</h1>
<p class="subtle">Pick a department and a preferred date. Hospital staff will follow up to confirm.</p>

<div class="card">
    <h2>New Consultation Request</h2>
    <form method="post" action="<%= request.getContextPath() %>/registerConsultation" data-validate-form>
        <div class="row">
            <div class="field">
                <label for="department">Department *</label>
                <select id="department" name="department" required
                        data-validate="required:Please select a department.">
                    <option value="">-- Select --</option>
                    <option>Cardiology</option>
                    <option>Radiology</option>
                    <option>Pathology</option>
                    <option>Pediatrics</option>
                    <option>Orthopedics</option>
                    <option>Emergency</option>
                    <option>General Medicine</option>
                </select>
            </div>
            <div class="field">
                <label for="doctorName">Doctor Name *</label>
                <input type="text" id="doctorName" name="doctorName" maxlength="60" required
                       data-validate="required:Doctor name is required.|maxlen:60:Max 60 characters." />
                <span class="help">Use the doctor you want to see, or "Any".</span>
            </div>
            <div class="field">
                <label for="preferredDate">Preferred Date *</label>
                <input type="date" id="preferredDate" name="preferredDate" required
                       data-validate="required:Preferred date is required." />
            </div>
        </div>
        <div class="field">
            <label for="notes">Notes</label>
            <textarea id="notes" name="notes" rows="3" maxlength="500"
                      data-validate="maxlen:500:Max 500 characters."></textarea>
            <span class="help">Optional - briefly describe your reason for the visit.</span>
        </div>
        <div class="actions">
            <button class="btn btn-primary" type="submit">Submit Request</button>
            <a class="btn btn-secondary" href="<%= request.getContextPath() %>/jsp/patientDashboard.jsp">Back</a>
        </div>
    </form>
</div>

<% if (consultations != null && !consultations.isEmpty()) { %>
<div class="card">
    <h2>My Consultation Requests</h2>
    <div class="table-wrap">
    <table class="data">
        <thead>
        <tr><th>#</th><th>Department</th><th>Doctor</th><th>Preferred Date</th><th>Status</th><th>Notes</th></tr>
        </thead>
        <tbody>
        <% for (Consultation c : consultations) { %>
            <tr>
                <td><%= c.getConsultationId() %></td>
                <td><%= c.getDepartment() %></td>
                <td><%= c.getDoctorName() %></td>
                <td><%= c.getPreferredDate() %></td>
                <td><%= c.getStatus() %></td>
                <td><%= c.getNotes() == null ? "" : c.getNotes() %></td>
            </tr>
        <% } %>
        </tbody>
    </table>
    </div>
</div>
<% } %>

<%@ include file="/jsp/includes/footer.jsp" %>
