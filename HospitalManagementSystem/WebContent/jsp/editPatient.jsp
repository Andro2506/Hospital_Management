<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.hospital.model.Patient" %>
<%
    if (session.getAttribute("admin") == null) {
        response.sendRedirect(request.getContextPath() + "/jsp/login.jsp?expired=1");
        return;
    }
    request.setAttribute("pageTitle", "Edit Patient");
    Patient p = (Patient) request.getAttribute("patient");
%>
<%@ include file="/jsp/includes/header.jsp" %>

<h1 class="page-title">Edit Patient</h1>

<div class="card">
    <h2>Find patient by PRN</h2>
    <form method="get" action="<%= request.getContextPath() %>/editPatient" data-validate-form>
        <div class="row">
            <div class="field" style="max-width:300px">
                <label for="patientId">Registration Number (PRN)</label>
                <input type="text" id="patientId" name="patientId" maxlength="7" required
                       value="<%= p != null ? p.getPatientId() : "" %>"
                       data-validate="required:PRN is required.|patientId:Must be 7 digits." />
            </div>
            <div class="field" style="justify-content:flex-end">
                <button class="btn btn-primary" type="submit" style="margin-top:24px">Load</button>
            </div>
        </div>
    </form>
</div>

<% if (p != null) { %>
<div class="card">
    <h2>Existing Details</h2>
    <form method="post" action="<%= request.getContextPath() %>/editPatient" data-validate-form>
        <input type="hidden" name="patientId" value="<%= p.getPatientId() %>" />

        <div class="row">
            <div class="field">
                <label>Registration Number</label>
                <input type="text" value="<%= p.getPatientId() %>" readonly disabled />
                <span class="help">PRN cannot be modified.</span>
            </div>
            <div class="field">
                <label for="patientName">Patient Name *</label>
                <input type="text" id="patientName" name="patientName" maxlength="50" required
                       value="<%= p.getPatientName() == null ? "" : p.getPatientName() %>"
                       data-validate="required:Name is required.|maxlen:50:Max 50 characters." />
            </div>
            <div class="field">
                <label for="age">Age *</label>
                <input type="number" id="age" name="age" min="1" max="130" required
                       value="<%= p.getAge() %>" />
            </div>
        </div>

        <div class="row">
            <div class="field">
                <label for="bloodGroup">Blood Group</label>
                <select id="bloodGroup" name="bloodGroup">
                    <option value="">-- Select --</option>
                    <%
                        String bg = p.getBloodGroup() == null ? "" : p.getBloodGroup();
                        String[] groups = {"A+","A-","B+","B-","O+","O-","AB+","AB-"};
                        for (String g : groups) {
                            String sel = g.equals(bg) ? " selected" : "";
                            out.print("<option" + sel + ">" + g + "</option>");
                        }
                    %>
                </select>
            </div>
            <div class="field">
                <label for="gender">Gender *</label>
                <%
                    String gender = p.getGender() == null ? "" : p.getGender();
                %>
                <select id="gender" name="gender" required>
                    <option value="">-- Select --</option>
                    <option<%= "Male".equals(gender) ? " selected" : "" %>>Male</option>
                    <option<%= "Female".equals(gender) ? " selected" : "" %>>Female</option>
                    <option<%= "Other".equals(gender) ? " selected" : "" %>>Other</option>
                </select>
            </div>
            <div class="field">
                <label for="patientDOB">Date of Birth</label>
                <input type="date" id="patientDOB" name="patientDOB"
                       value="<%= p.getPatientDOB() == null ? "" : p.getPatientDOB() %>" />
            </div>
        </div>

        <div class="row">
            <div class="field">
                <label for="wardNumber">Ward Number</label>
                <input type="text" id="wardNumber" name="wardNumber" maxlength="20"
                       value="<%= p.getWardNumber() == null ? "" : p.getWardNumber() %>" />
            </div>
            <div class="field">
                <label for="doctorId">Doctor Id</label>
                <input type="text" id="doctorId" name="doctorId" maxlength="20"
                       value="<%= p.getDoctorId() == null ? "" : p.getDoctorId() %>" />
            </div>
            <div class="field">
                <label for="doctorName">Doctor Name</label>
                <input type="text" id="doctorName" name="doctorName" maxlength="50"
                       value="<%= p.getDoctorName() == null ? "" : p.getDoctorName() %>" />
            </div>
        </div>

        <div class="row">
            <div class="field" style="flex:2 1 480px">
                <label for="email">Email</label>
                <input type="email" id="email" name="email"
                       value="<%= p.getEmail() == null ? "" : p.getEmail() %>" />
            </div>
            <div class="field">
                <label for="contactNo">Contact Number *</label>
                <input type="text" id="contactNo" name="contactNo" maxlength="10" required
                       value="<%= p.getContactNo() == null ? "" : p.getContactNo() %>"
                       data-validate="required:Contact is required.|contact:10 digits required." />
            </div>
            <div class="field">
                <label for="aadharNumber">Aadhar Number *</label>
                <input type="text" id="aadharNumber" name="aadharNumber" maxlength="12" required
                       value="<%= p.getAadharNumber() == null ? "" : p.getAadharNumber() %>"
                       data-validate="required:Aadhar is required.|aadhar:12 digits required." />
            </div>
        </div>

        <div class="row">
            <div class="field" style="flex:1 1 100%">
                <label for="address">Address</label>
                <input type="text" id="address" name="address" maxlength="100"
                       value="<%= p.getAddress() == null ? "" : p.getAddress() %>" />
            </div>
        </div>

        <div class="actions">
            <button class="btn btn-primary" type="submit">Save Changes</button>
            <a class="btn btn-secondary" href="<%= request.getContextPath() %>/jsp/adminDashboard.jsp">Cancel</a>
        </div>
    </form>
</div>
<% } %>

<%@ include file="/jsp/includes/footer.jsp" %>
