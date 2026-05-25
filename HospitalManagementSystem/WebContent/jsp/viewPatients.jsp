<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="com.hospital.model.Patient" %>
<%
    if (session.getAttribute("admin") == null) {
        response.sendRedirect(request.getContextPath() + "/jsp/login.jsp?expired=1");
        return;
    }
    request.setAttribute("pageTitle", "View Patients");
    List<Patient> patients = (List<Patient>) request.getAttribute("patients");
    Integer page = (Integer) request.getAttribute("page");
    Integer totalPages = (Integer) request.getAttribute("totalPages");
    Integer total = (Integer) request.getAttribute("total");
    Integer pageSize = (Integer) request.getAttribute("pageSize");
%>
<%@ include file="/jsp/includes/header.jsp" %>

<h1 class="page-title">All Active Patients</h1>
<p class="subtle">
    Showing page <%= page %> of <%= totalPages %> &middot;
    <%= total %> total record<%= total == 1 ? "" : "s" %> &middot;
    <%= pageSize %> per page.
</p>

<div class="card">
    <% if (patients == null || patients.isEmpty()) { %>
        <p>No patients found.</p>
    <% } else { %>
    <div class="table-wrap">
    <table class="data">
        <thead>
        <tr>
            <th>PRN</th>
            <th>Name</th>
            <th>Age</th>
            <th>Gender</th>
            <th>Blood</th>
            <th>DOB</th>
            <th>Ward</th>
            <th>Doctor</th>
            <th>Email</th>
            <th>Contact</th>
            <th>Aadhar</th>
            <th>Actions</th>
        </tr>
        </thead>
        <tbody>
        <% for (Patient p : patients) { %>
            <tr>
                <td><%= p.getPatientId() %></td>
                <td><%= p.getPatientName() %></td>
                <td><%= p.getAge() %></td>
                <td><%= p.getGender() %></td>
                <td><%= p.getBloodGroup() %></td>
                <td><%= p.getPatientDOB() %></td>
                <td><%= p.getWardNumber() %></td>
                <td><%= p.getDoctorName() %></td>
                <td><%= p.getEmail() %></td>
                <td><%= p.getContactNo() %></td>
                <td><%= p.getAadharNumber() %></td>
                <td class="actions-cell">
                    <a class="btn btn-secondary" href="<%= request.getContextPath() %>/editPatient?patientId=<%= p.getPatientId() %>">Edit</a>
                    <a class="btn btn-danger"    href="<%= request.getContextPath() %>/deletePatient?patientId=<%= p.getPatientId() %>">Delete</a>
                </td>
            </tr>
        <% } %>
        </tbody>
    </table>
    </div>

    <div class="pagination">
        <% if (page > 1) { %>
            <a href="?page=<%= page - 1 %>">&laquo; Prev</a>
        <% } else { %>
            <span class="disabled">&laquo; Prev</span>
        <% } %>
        <%
            int from = Math.max(1, page - 2);
            int to   = Math.min(totalPages, page + 2);
            for (int i = from; i <= to; i++) {
                if (i == page) {
        %>
            <span class="current"><%= i %></span>
        <%      } else { %>
            <a href="?page=<%= i %>"><%= i %></a>
        <%      }
            }
        %>
        <% if (page < totalPages) { %>
            <a href="?page=<%= page + 1 %>">Next &raquo;</a>
        <% } else { %>
            <span class="disabled">Next &raquo;</span>
        <% } %>
    </div>
    <% } %>
</div>

<%@ include file="/jsp/includes/footer.jsp" %>
