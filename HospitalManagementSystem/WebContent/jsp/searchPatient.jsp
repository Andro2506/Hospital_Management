<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.hospital.model.Patient" %>
<%
    if (session.getAttribute("admin") == null) {
        response.sendRedirect(request.getContextPath() + "/jsp/login.jsp?expired=1");
        return;
    }
    request.setAttribute("pageTitle", "Search Patient");
    Patient p = (Patient) request.getAttribute("patient");
    String prevId = request.getParameter("patientId");
%>
<%@ include file="/jsp/includes/header.jsp" %>

<h1 class="page-title">Search Patient by PRN</h1>

<div class="card">
    <form method="get" action="<%= request.getContextPath() %>/searchPatient" data-validate-form>
        <div class="row">
            <div class="field" style="max-width:300px">
                <label for="patientId">Registration Number (PRN)</label>
                <input type="text" id="patientId" name="patientId" maxlength="7" required
                       value="<%= prevId == null ? "" : prevId %>"
                       data-validate="required:PRN is required.|patientId:Must be 7 digits." />
            </div>
            <div class="field" style="justify-content:flex-end">
                <button class="btn btn-primary" type="submit" style="margin-top:24px">Search</button>
            </div>
        </div>
    </form>
</div>

<% if (p != null) { %>
<div class="card">
    <h2>Patient Details</h2>
    <div class="detail-grid">
        <div class="detail"><span class="label">PRN</span><span class="value"><%= p.getPatientId() %></span></div>
        <div class="detail"><span class="label">Name</span><span class="value"><%= p.getPatientName() %></span></div>
        <div class="detail"><span class="label">Age</span><span class="value"><%= p.getAge() %></span></div>
        <div class="detail"><span class="label">Gender</span><span class="value"><%= p.getGender() %></span></div>
        <div class="detail"><span class="label">Blood Group</span><span class="value"><%= p.getBloodGroup() %></span></div>
        <div class="detail"><span class="label">Date of Birth</span><span class="value"><%= p.getPatientDOB() %></span></div>
        <div class="detail"><span class="label">Ward</span><span class="value"><%= p.getWardNumber() %></span></div>
        <div class="detail"><span class="label">Doctor Id</span><span class="value"><%= p.getDoctorId() %></span></div>
        <div class="detail"><span class="label">Doctor Name</span><span class="value"><%= p.getDoctorName() %></span></div>
        <div class="detail"><span class="label">Email</span><span class="value"><%= p.getEmail() %></span></div>
        <div class="detail"><span class="label">Contact</span><span class="value"><%= p.getContactNo() %></span></div>
        <div class="detail"><span class="label">Aadhar</span><span class="value"><%= p.getAadharNumber() %></span></div>
        <div class="detail" style="grid-column:1 / -1">
            <span class="label">Address</span><span class="value"><%= p.getAddress() %></span>
        </div>
    </div>
</div>
<% } %>

<%@ include file="/jsp/includes/footer.jsp" %>
