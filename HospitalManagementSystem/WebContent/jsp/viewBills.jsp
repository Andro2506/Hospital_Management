<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List, com.hospital.model.Bill" %>
<%
    if (session.getAttribute("admin") == null) {
        response.sendRedirect(request.getContextPath() + "/jsp/login.jsp?expired=1");
        return;
    }
    request.setAttribute("pageTitle", "All Bills");
    List<Bill> bills = (List<Bill>) request.getAttribute("bills");
%>
<%@ include file="/jsp/includes/header.jsp" %>

<h1 class="page-title">All Bills</h1>

<div class="card">
    <% if (bills == null || bills.isEmpty()) { %>
        <p>No bills generated yet.</p>
    <% } else { %>
    <div class="table-wrap">
    <table class="data">
        <thead>
        <tr>
            <th>Bill Id</th><th>Lab Id</th><th>Patient PRN</th>
            <th>Test Type</th><th>Category</th>
            <th>Mobile</th><th>Payable</th>
        </tr>
        </thead>
        <tbody>
        <% double totalAll = 0; for (Bill b : bills) { totalAll += b.getPayableAmount(); %>
            <tr>
                <td><%= b.getBillId() %></td>
                <td><%= b.getLabId() %></td>
                <td><%= b.getPatientId() %></td>
                <td><%= b.getTestType() %></td>
                <td><%= b.getCategory() %></td>
                <td><%= b.getMobileNumber() %></td>
                <td>Rs. <%= b.getPayableAmount() %></td>
            </tr>
        <% } %>
        </tbody>
        <tfoot>
            <tr>
                <td colspan="6" style="text-align:right"><strong>Total Payable:</strong></td>
                <td><strong>Rs. <%= totalAll %></strong></td>
            </tr>
        </tfoot>
    </table>
    </div>
    <% } %>
</div>

<%@ include file="/jsp/includes/footer.jsp" %>
