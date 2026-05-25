<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<% request.setAttribute("pageTitle", "City Care Hospital - Welcome"); %>
<%@ include file="/jsp/includes/header.jsp" %>

<section class="hero">
    <h1>Welcome to City Care Hospital</h1>
    <p>Your trusted partner in healthcare. Manage patient records, lab tests,
       billing, and visitor services from a single place.</p>
    <div class="actions" style="justify-content:center;display:flex">
        <a class="btn btn-primary"   href="<%= request.getContextPath() %>/jsp/login.jsp">Admin Login</a>
        <a class="btn btn-secondary" href="<%= request.getContextPath() %>/jsp/visitorLogin.jsp">Visitor Login</a>
        <a class="btn btn-secondary" href="<%= request.getContextPath() %>/jsp/visitorRegister.jsp">Visitor Register</a>
    </div>
</section>

<section class="card">
    <h2>About this system</h2>
    <p>This Hospital Management System lets administrators capture, edit, search,
       and delete patient records, register patients for lab tests (CBC / BEL),
       and review billing details. Visitors can register, log in, browse hospital
       facilities by department, and submit complaints to the hospital
       management.</p>
    <p class="subtle">Default admin credentials seeded on first run:
       <code>admin001</code> / <code>Admin@123</code>.</p>
</section>

<%@ include file="/jsp/includes/footer.jsp" %>
