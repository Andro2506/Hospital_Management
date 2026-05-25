<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.hospital.model.Lab, com.hospital.model.Bill, com.hospital.model.Patient" %>
<%
    if (session.getAttribute("admin") == null) {
        response.sendRedirect(request.getContextPath() + "/jsp/login.jsp?expired=1");
        return;
    }
    request.setAttribute("pageTitle", "Register Lab Test");
    Lab lab = (Lab) request.getAttribute("lab");
    Bill bill = (Bill) request.getAttribute("bill");
    Patient patient = (Patient) request.getAttribute("patient");
%>
<%@ include file="/jsp/includes/header.jsp" %>

<h1 class="page-title">Register Patient for Lab Test</h1>
<p class="subtle">Supported test types: <strong>CBC</strong> and <strong>BEL</strong>.</p>

<div class="card">
    <form method="post" action="<%= request.getContextPath() %>/registerLabTest" data-validate-form>
        <div class="row">
            <div class="field">
                <label for="patientId">Patient PRN *</label>
                <input type="text" id="patientId" name="patientId" maxlength="7" required
                       data-validate="required:PRN is required.|patientId:7-digit PRN required." />
            </div>
            <div class="field">
                <label for="testType">Test Type *</label>
                <select id="testType" name="testType" required>
                    <option value="">-- Select --</option>
                    <option value="CBC">CBC - Complete Blood Count</option>
                    <option value="BEL">BEL - Basic Electrolytes</option>
                </select>
            </div>
            <div class="field">
                <label for="category">Category *</label>
                <input type="text" id="category" name="category" maxlength="40" required
                       data-validate="required:Category is required." />
                <span class="help">e.g. Routine, Emergency, Pre-op.</span>
            </div>
        </div>

        <div class="row">
            <div class="field">
                <label for="weight">Weight (kg) *</label>
                <input type="number" id="weight" name="weight" min="1" max="500" required
                       data-validate="required:Weight is required." />
            </div>
            <div class="field">
                <label for="height">Height (cm) *</label>
                <input type="number" id="height" name="height" min="1" max="300" required
                       data-validate="required:Height is required." />
            </div>
            <div class="field">
                <label for="mobileNumber">Mobile Number *</label>
                <input type="text" id="mobileNumber" name="mobileNumber" maxlength="10" required
                       data-validate="required:Mobile is required.|contact:10 digits required." />
            </div>
        </div>

        <div class="actions">
            <button class="btn btn-primary" type="submit">Register Lab Test</button>
            <a class="btn btn-secondary" href="<%= request.getContextPath() %>/jsp/adminDashboard.jsp">Cancel</a>
        </div>
    </form>
</div>

<% if (lab != null && bill != null && patient != null) { %>
<div class="card">
    <h2>Acknowledgment</h2>
    <p class="subtle">Patient Registration for Lab tests is Successful.</p>
    <div class="detail-grid">
        <div class="detail"><span class="label">Patient PRN</span><span class="value"><%= patient.getPatientId() %></span></div>
        <div class="detail"><span class="label">Patient Name</span><span class="value"><%= patient.getPatientName() %></span></div>
        <div class="detail"><span class="label">Lab Id</span><span class="value"><%= lab.getLabId() %></span></div>
        <div class="detail"><span class="label">Bill Id</span><span class="value"><%= bill.getBillId() %></span></div>
        <div class="detail"><span class="label">Test Type</span><span class="value"><%= lab.getTestType() %></span></div>
        <div class="detail"><span class="label">Category</span><span class="value"><%= lab.getCategory() %></span></div>
        <div class="detail"><span class="label">Weight</span><span class="value"><%= lab.getWeight() %> kg</span></div>
        <div class="detail"><span class="label">Height</span><span class="value"><%= lab.getHeight() %> cm</span></div>
        <div class="detail"><span class="label">Mobile</span><span class="value"><%= lab.getMobileNumber() %></span></div>
        <div class="detail"><span class="label">Payable Amount</span><span class="value">Rs. <%= bill.getPayableAmount() %></span></div>
    </div>
</div>
<% } %>

<%@ include file="/jsp/includes/footer.jsp" %>
