<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List, com.hospital.model.Lab" %>
<%
    if (session.getAttribute("admin") == null) {
        response.sendRedirect(request.getContextPath() + "/jsp/login.jsp?expired=1");
        return;
    }
    request.setAttribute("pageTitle", "All Lab Tests");
    List<Lab> labs = (List<Lab>) request.getAttribute("labs");
%>
<%@ include file="/jsp/includes/header.jsp" %>

<h1 class="page-title">All Registered Lab Tests</h1>

<div class="card">
    <% if (labs == null || labs.isEmpty()) { %>
        <p>No lab tests registered yet.</p>
    <% } else { %>
    <div class="table-wrap">
    <table class="data">
        <thead>
        <tr>
            <th>Lab Id</th><th>Patient PRN</th><th>Test Type</th>
            <th>Category</th><th>Weight</th><th>Height</th><th>Mobile</th>
        </tr>
        </thead>
        <tbody>
        <% for (Lab l : labs) { %>
            <tr>
                <td><%= l.getLabId() %></td>
                <td><%= l.getPatientId() %></td>
                <td><%= l.getTestType() %></td>
                <td><%= l.getCategory() %></td>
                <td><%= l.getWeight() %> kg</td>
                <td><%= l.getHeight() %> cm</td>
                <td><%= l.getMobileNumber() %></td>
            </tr>
        <% } %>
        </tbody>
    </table>
    </div>
    <% } %>
</div>

<%@ include file="/jsp/includes/footer.jsp" %>
