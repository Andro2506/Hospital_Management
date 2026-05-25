<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List, com.hospital.model.Bill, com.hospital.model.Patient" %>
<%
    if (session.getAttribute("admin") == null) {
        response.sendRedirect(request.getContextPath() + "/jsp/login.jsp?expired=1");
        return;
    }
    request.setAttribute("pageTitle", "Search Bill (by Email or PRN)");
    List<Bill> bills = (List<Bill>) request.getAttribute("bills");
    Patient patient = (Patient) request.getAttribute("patient");
    Double totalPayable = (Double) request.getAttribute("totalPayable");
    String key = (String) request.getAttribute("key");
    String searchMode = (String) request.getAttribute("searchMode");
%>
<%@ include file="/jsp/includes/header.jsp" %>

<h1 class="page-title">Search Bill</h1>
<p class="subtle">Search by Patient Registration Id (PRN) or by Email - the system picks the right strategy.</p>

<div class="card">
    <form method="get" action="<%= request.getContextPath() %>/searchBill">
        <div class="row">
            <div class="field">
                <label>Search By</label>
                <div class="radio-group">
                    <label><input type="radio" name="searchBy" value="patientId"
                        <%= searchMode == null || "patientId".equals(searchMode) ? "checked" : "" %>/> Patient PRN</label>
                    <label><input type="radio" name="searchBy" value="email"
                        <%= "email".equals(searchMode) ? "checked" : "" %>/> Email</label>
                </div>
            </div>
            <div class="field" style="flex:2 1 360px">
                <label for="key">PRN or Email</label>
                <input type="text" id="key" name="key" required
                       value="<%= key == null ? "" : key %>" />
            </div>
            <div class="field" style="justify-content:flex-end">
                <button class="btn btn-primary" type="submit" style="margin-top:24px">Search</button>
            </div>
        </div>
    </form>
</div>

<% if (patient != null) { %>
<div class="card">
    <h2>Patient</h2>
    <div class="detail-grid">
        <div class="detail"><span class="label">PRN</span><span class="value"><%= patient.getPatientId() %></span></div>
        <div class="detail"><span class="label">Name</span><span class="value"><%= patient.getPatientName() %></span></div>
        <div class="detail"><span class="label">Email</span><span class="value"><%= patient.getEmail() %></span></div>
        <div class="detail"><span class="label">Contact</span><span class="value"><%= patient.getContactNo() %></span></div>
    </div>
</div>
<% } %>

<% if (bills != null && !bills.isEmpty()) { %>
<div class="card">
    <h2>Bills</h2>
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
</div>
<% } %>

<%@ include file="/jsp/includes/footer.jsp" %>
