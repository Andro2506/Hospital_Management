<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List, com.hospital.model.Bill" %>
<%
    if (session.getAttribute("patient") == null) {
        response.sendRedirect(request.getContextPath() + "/jsp/patientLogin.jsp?expired=1");
        return;
    }
    request.setAttribute("pageTitle", "My Bills");
    List<Bill> bills = (List<Bill>) request.getAttribute("bills");
    Double totalPayable = (Double) request.getAttribute("totalPayable");
%>
<%@ include file="/jsp/includes/header.jsp" %>

<h1 class="page-title">My Bills</h1>

<div class="card">
    <% if (bills == null || bills.isEmpty()) { %>
        <p>You have no bills on record.</p>
    <% } else { %>
    <div class="table-wrap">
    <table class="data">
        <thead>
        <tr>
            <th>Bill Id</th><th>Lab Id</th><th>Test Type</th>
            <th>Category</th><th>Mobile</th><th>Payable</th>
        </tr>
        </thead>
        <tbody>
        <% for (Bill b : bills) { %>
            <tr>
                <td><%= b.getBillId() %></td>
                <td><%= b.getLabId() %></td>
                <td><%= b.getTestType() %></td>
                <td><%= b.getCategory() %></td>
                <td><%= b.getMobileNumber() %></td>
                <td>Rs. <%= b.getPayableAmount() %></td>
            </tr>
        <% } %>
        </tbody>
        <tfoot>
            <tr>
                <td colspan="5" style="text-align:right"><strong>Total Payable:</strong></td>
                <td><strong>Rs. <%= totalPayable == null ? 0 : totalPayable %></strong></td>
            </tr>
        </tfoot>
    </table>
    </div>
    <% } %>

    <div class="actions">
        <a class="btn btn-secondary" href="<%= request.getContextPath() %>/jsp/patientDashboard.jsp">Back to Dashboard</a>
    </div>
</div>

<%@ include file="/jsp/includes/footer.jsp" %>
