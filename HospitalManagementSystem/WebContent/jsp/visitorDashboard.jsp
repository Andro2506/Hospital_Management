<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.hospital.model.Visitor" %>
<%
    Visitor v = (Visitor) session.getAttribute("visitor");
    if (v == null) {
        response.sendRedirect(request.getContextPath() + "/jsp/visitorLogin.jsp?expired=1");
        return;
    }
    request.setAttribute("pageTitle", "Visitor Dashboard");
%>
<%@ include file="/jsp/includes/header.jsp" %>

<h1 class="page-title">Welcome, <%= v.getFirstName() %></h1>
<p class="subtle">Signed in as <strong><%= v.getUsername() %></strong>.</p>

<div class="tiles">
    <a class="tile" href="<%= request.getContextPath() %>/viewFacilities">
        <div class="icon">&#127973;</div>
        <h3>Hospital Facilities</h3>
        <p>Browse facilities department-wise.</p>
    </a>
    <a class="tile" href="<%= request.getContextPath() %>/registerComplaint">
        <div class="icon">&#9888;</div>
        <h3>Register Complaint</h3>
        <p>Tell us about an issue you faced.</p>
    </a>
    <a class="tile" href="<%= request.getContextPath() %>/visitorLogout">
        <div class="icon">&#10162;</div>
        <h3>Logout</h3>
        <p>End your session.</p>
    </a>
</div>

<%@ include file="/jsp/includes/footer.jsp" %>
