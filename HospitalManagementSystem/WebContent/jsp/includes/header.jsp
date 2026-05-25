<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    String ctx = request.getContextPath();
    String pageTitle = (String) request.getAttribute("pageTitle");
    if (pageTitle == null) pageTitle = "Hospital Management System";

    Object adminUser   = session.getAttribute("admin");
    Object patientUser = session.getAttribute("patient");
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <title><%= pageTitle %></title>
    <link rel="stylesheet" href="<%= ctx %>/css/styles.css" />
</head>
<body>
<header class="site-header">
    <div class="brand">
        <span class="logo">+</span>
        <span>City Care Hospital</span>
    </div>
    <nav>
        <a href="<%= ctx %>/index.jsp">Home</a>
        <% if (adminUser != null) { %>
            <a href="<%= ctx %>/jsp/adminDashboard.jsp">Admin Dashboard</a>
            <a href="<%= ctx %>/logout">Logout</a>
        <% } else if (patientUser != null) { %>
            <a href="<%= ctx %>/jsp/patientDashboard.jsp">Patient Dashboard</a>
            <a href="<%= ctx %>/patientLogout">Logout</a>
        <% } else { %>
            <a href="<%= ctx %>/jsp/login.jsp">Admin Login</a>
            <a href="<%= ctx %>/jsp/patientLogin.jsp">Patient Login</a>
            <a href="<%= ctx %>/jsp/patientRegister.jsp">Register</a>
        <% } %>
    </nav>
</header>
<main class="container">
<%
    String flash = (String) session.getAttribute("flash");
    if (flash != null) {
        session.removeAttribute("flash");
%>
    <div class="alert alert-info"><%= flash %></div>
<%
    }

    String successMessage = (String) request.getAttribute("successMessage");
    if (successMessage != null) {
%>
    <div class="alert alert-success"><%= successMessage %></div>
<%
    }
    String errorMessage = (String) request.getAttribute("errorMessage");
    if (errorMessage != null) {
%>
    <div class="alert alert-error"><%= errorMessage %></div>
<%
    }
%>
