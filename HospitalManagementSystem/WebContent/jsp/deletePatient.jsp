<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.hospital.model.Patient" %>
<%
    if (session.getAttribute("admin") == null) {
        response.sendRedirect(request.getContextPath() + "/jsp/login.jsp?expired=1");
        return;
    }
    request.setAttribute("pageTitle", "Delete Patient");
    Patient p = (Patient) request.getAttribute("patient");
%>
<%@ include file="/jsp/includes/header.jsp" %>

<h1 class="page-title">Delete Patient</h1>

<div class="card">
    <h2>Find patient by PRN</h2>
    <form method="get" action="<%= request.getContextPath() %>/deletePatient" data-validate-form>
        <div class="row">
            <div class="field" style="max-width:300px">
                <label for="patientId">Registration Number (PRN)</label>
                <input type="text" id="patientId" name="patientId" maxlength="7" required
                       value="<%= p != null ? p.getPatientId() : "" %>"
                       data-validate="required:PRN is required.|patientId:Must be 7 digits." />
            </div>
            <div class="field" style="justify-content:flex-end">
                <button class="btn btn-primary" type="submit" style="margin-top:24px">Load</button>
            </div>
        </div>
    </form>
</div>

<% if (p != null) { %>
<div class="card">
    <h2>Confirm Deletion</h2>
    <p>You are about to permanently remove the following patient.</p>
    <div class="detail-grid">
        <div class="detail"><span class="label">PRN</span><span class="value"><%= p.getPatientId() %></span></div>
        <div class="detail"><span class="label">Name</span><span class="value"><%= p.getPatientName() %></span></div>
        <div class="detail"><span class="label">Age</span><span class="value"><%= p.getAge() %></span></div>
        <div class="detail"><span class="label">Gender</span><span class="value"><%= p.getGender() %></span></div>
        <div class="detail"><span class="label">Email</span><span class="value"><%= p.getEmail() %></span></div>
        <div class="detail"><span class="label">Contact</span><span class="value"><%= p.getContactNo() %></span></div>
        <div class="detail"><span class="label">Aadhar</span><span class="value"><%= p.getAadharNumber() %></span></div>
        <div class="detail"><span class="label">Ward</span><span class="value"><%= p.getWardNumber() %></span></div>
        <div class="detail"><span class="label">Doctor</span><span class="value"><%= p.getDoctorName() %></span></div>
    </div>

    <form method="post" action="<%= request.getContextPath() %>/deletePatient" style="margin-top:18px">
        <input type="hidden" name="patientId" value="<%= p.getPatientId() %>" />
        <div class="actions">
            <button class="btn btn-danger" type="submit"
                    data-confirm="Permanently delete patient <%= p.getPatientId() %> - <%= p.getPatientName() %>?">
                Yes, Delete
            </button>
            <a class="btn btn-secondary" href="<%= request.getContextPath() %>/jsp/adminDashboard.jsp">Cancel</a>
        </div>
    </form>
</div>
<% } %>

<%@ include file="/jsp/includes/footer.jsp" %>
