<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.hospital.model.Admin" %>
<%
    Admin admin = (Admin) session.getAttribute("admin");
    if (admin == null) {
        response.sendRedirect(request.getContextPath() + "/jsp/login.jsp?expired=1");
        return;
    }
    request.setAttribute("pageTitle", "Admin Dashboard");
%>
<%@ include file="/jsp/includes/header.jsp" %>

<h1 class="page-title">Admin Dashboard</h1>
<p class="subtle">Signed in as <strong><%= admin.getUsername() %></strong>.</p>

<div class="tiles">
    <a class="tile" href="<%= request.getContextPath() %>/addPatient">
        <div class="icon">+</div>
        <h3>Add Patient</h3>
        <p>Capture a new patient record.</p>
    </a>
    <a class="tile" href="<%= request.getContextPath() %>/viewPatients">
        <div class="icon">&#9776;</div>
        <h3>View Patients</h3>
        <p>Paginated list of all active patients.</p>
    </a>
    <a class="tile" href="<%= request.getContextPath() %>/searchPatient">
        <div class="icon">&#128270;</div>
        <h3>Search Patient</h3>
        <p>Look up a patient by PRN.</p>
    </a>
    <a class="tile" href="<%= request.getContextPath() %>/editPatient">
        <div class="icon">&#9998;</div>
        <h3>Edit Patient</h3>
        <p>Update existing patient details.</p>
    </a>
    <a class="tile" href="<%= request.getContextPath() %>/deletePatient">
        <div class="icon">&#128465;</div>
        <h3>Delete Patient</h3>
        <p>Remove a patient by PRN.</p>
    </a>
    <a class="tile" href="<%= request.getContextPath() %>/deleteByMobile">
        <div class="icon">&#9742;</div>
        <h3>Delete by Mobile</h3>
        <p>Remove patients by mobile number.</p>
    </a>
    <a class="tile" href="<%= request.getContextPath() %>/registerLabTest">
        <div class="icon">&#129514;</div>
        <h3>Register Lab Test</h3>
        <p>Book CBC / BEL tests for patients.</p>
    </a>
    <a class="tile" href="<%= request.getContextPath() %>/viewLabTests">
        <div class="icon">&#128203;</div>
        <h3>View Lab Tests</h3>
        <p>All registered lab tests.</p>
    </a>
    <a class="tile" href="<%= request.getContextPath() %>/viewBills">
        <div class="icon">&#128181;</div>
        <h3>View Bills</h3>
        <p>All bills for lab tests.</p>
    </a>
    <a class="tile" href="<%= request.getContextPath() %>/searchBill">
        <div class="icon">&#128269;</div>
        <h3>Search Bill</h3>
        <p>By Email or PRN (polymorphic).</p>
    </a>
    <a class="tile" href="<%= request.getContextPath() %>/logout">
        <div class="icon">&#10162;</div>
        <h3>Logout</h3>
        <p>End your admin session.</p>
    </a>
</div>

<%@ include file="/jsp/includes/footer.jsp" %>
