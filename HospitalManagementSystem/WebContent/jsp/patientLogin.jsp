<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<% request.setAttribute("pageTitle", "Patient Login"); %>
<%@ include file="/jsp/includes/header.jsp" %>

<%
    String loginError = (String) request.getAttribute("loginError");
    String expired    = request.getParameter("expired");
%>

<h1 class="page-title">Patient Login</h1>

<% if (loginError != null) { %>
    <div class="alert alert-error"><%= loginError %></div>
<% } else if ("1".equals(expired)) { %>
    <div class="alert alert-info">Your session has expired. Please log in again.</div>
<% } %>

<div class="card" style="max-width:520px">
    <h2>Sign in</h2>
    <form method="post" action="<%= request.getContextPath() %>/patientLogin" data-validate-form>
        <div class="field">
            <label for="userId">Username</label>
            <input type="text" id="userId" name="userId" required
                   data-validate="required:Username is required.|userId:Alphanumeric, min 8 chars." />
            <span class="help">Alphanumeric, minimum 8 characters.</span>
        </div>
        <div class="field">
            <label for="password">Password</label>
            <input type="password" id="password" name="password" required
                   data-validate="required:Password is required.|password:Min 10 chars with 1 upper, 1 number, 1 special." />
            <span class="help">Min 10 chars with 1 uppercase, 1 number, 1 special character.</span>
        </div>
        <div class="actions">
            <button type="submit" class="btn btn-primary">Login</button>
            <a class="btn btn-secondary" href="<%= request.getContextPath() %>/jsp/patientRegister.jsp">Register</a>
        </div>
    </form>
</div>

<%@ include file="/jsp/includes/footer.jsp" %>
