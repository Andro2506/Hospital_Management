<%@ page contentType="text/html;charset=UTF-8" language="java" isErrorPage="true" %>
<% request.setAttribute("pageTitle", "Something went wrong"); %>
<%@ include file="/jsp/includes/header.jsp" %>

<h1 class="page-title">Oops &mdash; something went wrong</h1>

<div class="card">
    <p>We hit an unexpected error while processing your request. The hospital
       team has been notified. You can try again or return to the home page.</p>
    <%
        String em = (String) request.getAttribute("errorMessage");
        if (em != null) {
    %>
        <div class="alert alert-error"><%= em %></div>
    <% } else if (exception != null) { %>
        <div class="alert alert-error">Details: <%= exception.getMessage() %></div>
    <% } %>
    <div class="actions">
        <a class="btn btn-primary"   href="<%= request.getContextPath() %>/index.jsp">Return Home</a>
        <a class="btn btn-secondary" href="javascript:history.back()">Go Back</a>
    </div>
</div>

<%@ include file="/jsp/includes/footer.jsp" %>
