<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<% request.setAttribute("pageTitle", "City Care Hospital - Welcome"); %>
<%@ include file="/jsp/includes/header.jsp" %>

<section class="hero">
    <h1>Welcome to City Care Hospital</h1>
    <p>Your trusted partner in healthcare. Manage patient records, lab tests,
       billing, and consultations from a single place.</p>
    <div class="actions" style="justify-content:center;display:flex">
        <a class="btn btn-primary"   href="<%= request.getContextPath() %>/jsp/login.jsp">Admin Login</a>
        <a class="btn btn-secondary" href="<%= request.getContextPath() %>/jsp/patientLogin.jsp">Patient Login</a>
        <a class="btn btn-secondary" href="<%= request.getContextPath() %>/jsp/patientRegister.jsp">Patient Register</a>
    </div>
</section>

<section class="card">
    <h2>For patients</h2>
    <p>Register an account to access your records, view lab tests booked
       under your PRN, see bills, edit your contact info, and request a
       doctor consultation - all in one place.</p>
</section>

<section class="card">
    <h2>For administrators</h2>
    <p>Sign in to capture, edit, search and delete patient records,
       register patients for lab tests (CBC / BEL), and review billing
       details.</p>
    <p class="subtle">Default admin credentials seeded on first run:
       <code>admin001</code> / <code>Admin@123</code>. Three demo
       patient logins are also seeded:
       <code>patient001</code>, <code>patient002</code>,
       <code>patient003</code> - all with password
       <code>Patient@123</code>.</p>
</section>

<%@ include file="/jsp/includes/footer.jsp" %>
