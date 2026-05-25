<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.*" %>
<%@ page import="com.hospital.servlet.ViewFacilitiesServlet" %>
<%@ page import="com.hospital.servlet.ViewFacilitiesServlet.Facility" %>
<%
    if (session.getAttribute("visitor") == null) {
        response.sendRedirect(request.getContextPath() + "/jsp/visitorLogin.jsp?expired=1");
        return;
    }
    request.setAttribute("pageTitle", "Hospital Facilities");

    List<Facility> rows = (List<Facility>) request.getAttribute("facilities");
    Integer currentPage = (Integer) request.getAttribute("page");
    Integer totalPages  = (Integer) request.getAttribute("totalPages");
    Integer total       = (Integer) request.getAttribute("total");
    Integer pageSize    = (Integer) request.getAttribute("pageSize");

    // Group by department for the current page slice.
    Map<String, List<Facility>> byDept = new LinkedHashMap<>();
    if (rows != null) {
        for (Facility f : rows) {
            byDept.computeIfAbsent(f.getDepartment(), k -> new ArrayList<>()).add(f);
        }
    }
%>
<%@ include file="/jsp/includes/header.jsp" %>

<h1 class="page-title">Our Facilities</h1>
<p class="subtle">
    Page <%= currentPage %> of <%= totalPages %> &middot;
    <%= total %> facility entries &middot; <%= pageSize %> per page.
</p>

<% for (Map.Entry<String, List<Facility>> e : byDept.entrySet()) { %>
<div class="card">
    <h2><%= e.getKey() %></h2>
    <div class="table-wrap">
    <table class="data">
        <thead><tr><th>Facility</th><th>Description</th></tr></thead>
        <tbody>
        <% for (Facility f : e.getValue()) { %>
            <tr>
                <td><strong><%= f.getName() %></strong></td>
                <td><%= f.getDescription() %></td>
            </tr>
        <% } %>
        </tbody>
    </table>
    </div>
</div>
<% } %>

<div class="pagination">
    <% if (currentPage > 1) { %>
        <a href="?page=<%= currentPage - 1 %>">&laquo; Prev</a>
    <% } else { %>
        <span class="disabled">&laquo; Prev</span>
    <% } %>
    <%
        int from = Math.max(1, currentPage - 2);
        int to   = Math.min(totalPages, currentPage + 2);
        for (int i = from; i <= to; i++) {
            if (i == currentPage) {
    %>
        <span class="current"><%= i %></span>
    <%      } else { %>
        <a href="?page=<%= i %>"><%= i %></a>
    <%      }
        }
    %>
    <% if (currentPage < totalPages) { %>
        <a href="?page=<%= currentPage + 1 %>">Next &raquo;</a>
    <% } else { %>
        <span class="disabled">Next &raquo;</span>
    <% } %>
</div>

<%@ include file="/jsp/includes/footer.jsp" %>
