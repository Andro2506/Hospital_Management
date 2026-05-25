<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.hospital.model.Patient" %>
<%
    Patient me = (Patient) session.getAttribute("patient");
    if (me == null) {
        response.sendRedirect(request.getContextPath() + "/jsp/patientLogin.jsp?expired=1");
        return;
    }
    request.setAttribute("pageTitle", "Patient Dashboard");
%>
<%@ include file="/jsp/includes/header.jsp" %>

<h1 class="page-title">Welcome, <%= me.getPatientName() %></h1>
<p class="subtle">Signed in as <strong><%= me.getUsername() %></strong>
   &middot; PRN <strong><%= me.getPatientId() %></strong>.</p>

<div class="tiles">
    <a class="tile" href="<%= request.getContextPath() %>/myProfile">
        <div class="icon">&#128100;</div>
        <h3>View My Details</h3>
        <p>See your personal record on file.</p>
    </a>
    <a class="tile" href="<%= request.getContextPath() %>/myProfile/edit">
        <div class="icon">&#9998;</div>
        <h3>Edit My Details</h3>
        <p>Update your contact and personal info.</p>
    </a>
    <a class="tile" href="<%= request.getContextPath() %>/myLabTests">
        <div class="icon">&#129514;</div>
        <h3>My Lab Tests</h3>
        <p>Lab tests registered against your PRN.</p>
    </a>
    <a class="tile" href="<%= request.getContextPath() %>/registerConsultation">
        <div class="icon">&#128138;</div>
        <h3>Register Consultation</h3>
        <p>Request a doctor consultation.</p>
    </a>
    <a class="tile" href="<%= request.getContextPath() %>/myBills">
        <div class="icon">&#128181;</div>
        <h3>My Bills</h3>
        <p>Bills generated for your treatments.</p>
    </a>
    <a class="tile" href="<%= request.getContextPath() %>/patientLogout">
        <div class="icon">&#10162;</div>
        <h3>Logout</h3>
        <p>End your session.</p>
    </a>
</div>

<%@ include file="/jsp/includes/footer.jsp" %>
