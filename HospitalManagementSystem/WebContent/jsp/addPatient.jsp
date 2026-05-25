<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    if (session.getAttribute("admin") == null) {
        response.sendRedirect(request.getContextPath() + "/jsp/login.jsp?expired=1");
        return;
    }
    request.setAttribute("pageTitle", "Add Patient");
%>
<%@ include file="/jsp/includes/header.jsp" %>

<h1 class="page-title">Add New Patient</h1>
<p class="subtle">All fields marked * are required.</p>

<div class="card">
    <h2>Patient Details</h2>
    <form method="post" action="<%= request.getContextPath() %>/addPatient" data-validate-form>
        <div class="row">
            <div class="field">
                <label for="patientId">Registration Number *</label>
                <input type="text" id="patientId" name="patientId" maxlength="7" required
                       data-validate="required:Registration Number is required.|patientId:Must be exactly 7 digits." />
                <span class="help">7-digit unique number (PRN).</span>
            </div>
            <div class="field">
                <label for="patientName">Patient Name *</label>
                <input type="text" id="patientName" name="patientName" maxlength="50" required
                       data-validate="required:Patient Name is required.|maxlen:50:Max 50 characters." />
            </div>
            <div class="field">
                <label for="age">Age *</label>
                <input type="number" id="age" name="age" min="1" max="130" required
                       data-validate="required:Age is required." />
            </div>
        </div>

        <div class="row">
            <div class="field">
                <label for="bloodGroup">Blood Group</label>
                <select id="bloodGroup" name="bloodGroup">
                    <option value="">-- Select --</option>
                    <option>A+</option><option>A-</option>
                    <option>B+</option><option>B-</option>
                    <option>O+</option><option>O-</option>
                    <option>AB+</option><option>AB-</option>
                </select>
            </div>
            <div class="field">
                <label for="gender">Gender *</label>
                <select id="gender" name="gender" required
                        data-validate="required:Gender is required.">
                    <option value="">-- Select --</option>
                    <option>Male</option><option>Female</option><option>Other</option>
                </select>
            </div>
            <div class="field">
                <label for="patientDOB">Date of Birth</label>
                <input type="date" id="patientDOB" name="patientDOB" />
            </div>
        </div>

        <div class="row">
            <div class="field">
                <label for="wardNumber">Ward Number</label>
                <input type="text" id="wardNumber" name="wardNumber" maxlength="20" />
            </div>
            <div class="field">
                <label for="doctorId">Doctor Id</label>
                <input type="text" id="doctorId" name="doctorId" maxlength="20" />
            </div>
            <div class="field">
                <label for="doctorName">Doctor Name</label>
                <input type="text" id="doctorName" name="doctorName" maxlength="50" />
            </div>
        </div>

        <div class="row">
            <div class="field" style="flex:2 1 480px">
                <label for="email">Email</label>
                <input type="email" id="email" name="email"
                       data-validate="email:Please enter a valid email address." />
            </div>
            <div class="field">
                <label for="contactNo">Contact Number *</label>
                <input type="text" id="contactNo" name="contactNo" maxlength="10" required
                       data-validate="required:Contact Number is required.|contact:Must be exactly 10 digits." />
            </div>
            <div class="field">
                <label for="aadharNumber">Aadhar Number *</label>
                <input type="text" id="aadharNumber" name="aadharNumber" maxlength="12" required
                       data-validate="required:Aadhar is required.|aadhar:Must be exactly 12 digits." />
            </div>
        </div>

        <div class="row">
            <div class="field" style="flex:1 1 100%">
                <label for="address">Address</label>
                <input type="text" id="address" name="address" maxlength="100"
                       data-validate="maxlen:100:Address must be 100 characters or fewer." />
            </div>
        </div>

        <div class="actions">
            <button class="btn btn-primary" type="submit">Save Patient</button>
            <a class="btn btn-secondary" href="<%= request.getContextPath() %>/jsp/adminDashboard.jsp">Cancel</a>
        </div>
    </form>
</div>

<%@ include file="/jsp/includes/footer.jsp" %>
