<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.hospital.model.Patient" %>
<%
    if (session.getAttribute("patient") == null) {
        response.sendRedirect(request.getContextPath() + "/jsp/patientLogin.jsp?expired=1");
        return;
    }
    Patient p = (Patient) request.getAttribute("patient");
    if (p == null) p = (Patient) session.getAttribute("patient");
    Boolean editMode = (Boolean) request.getAttribute("editMode");
    boolean editing = editMode != null && editMode;
    request.setAttribute("pageTitle", editing ? "Edit My Details" : "My Details");
%>
<%@ include file="/jsp/includes/header.jsp" %>

<h1 class="page-title"><%= editing ? "Edit My Details" : "My Details" %></h1>

<% if (!editing) { %>
<div class="card">
    <h2>Profile</h2>
    <div class="detail-grid">
        <div class="detail"><span class="label">PRN</span><span class="value"><%= p.getPatientId() %></span></div>
        <div class="detail"><span class="label">Username</span><span class="value"><%= p.getUsername() == null ? "-" : p.getUsername() %></span></div>
        <div class="detail"><span class="label">Name</span><span class="value"><%= p.getPatientName() %></span></div>
        <div class="detail"><span class="label">Age</span><span class="value"><%= p.getAge() %></span></div>
        <div class="detail"><span class="label">Gender</span><span class="value"><%= p.getGender() %></span></div>
        <div class="detail"><span class="label">Blood Group</span><span class="value"><%= p.getBloodGroup() %></span></div>
        <div class="detail"><span class="label">Date of Birth</span><span class="value"><%= p.getPatientDOB() %></span></div>
        <div class="detail"><span class="label">Email</span><span class="value"><%= p.getEmail() %></span></div>
        <div class="detail"><span class="label">Contact</span><span class="value"><%= p.getContactNo() %></span></div>
        <div class="detail"><span class="label">Aadhar</span><span class="value"><%= p.getAadharNumber() %></span></div>
        <div class="detail"><span class="label">Ward</span><span class="value"><%= p.getWardNumber() == null ? "-" : p.getWardNumber() %></span></div>
        <div class="detail"><span class="label">Doctor</span><span class="value"><%= p.getDoctorName() == null ? "-" : p.getDoctorName() %></span></div>
        <div class="detail" style="grid-column:1 / -1">
            <span class="label">Address</span><span class="value"><%= p.getAddress() == null ? "" : p.getAddress() %></span>
        </div>
    </div>
    <div class="actions">
        <a class="btn btn-primary" href="<%= request.getContextPath() %>/myProfile/edit">Edit Details</a>
        <a class="btn btn-secondary" href="<%= request.getContextPath() %>/jsp/patientDashboard.jsp">Back</a>
    </div>
</div>
<% } else { %>
<div class="card">
    <h2>Edit My Details</h2>
    <p class="subtle">Your PRN, username and ward / doctor assignment are managed by hospital staff and cannot be edited here.</p>
    <form method="post" action="<%= request.getContextPath() %>/myProfile/edit" data-validate-form>
        <div class="row">
            <div class="field">
                <label>PRN</label>
                <input type="text" value="<%= p.getPatientId() %>" disabled />
            </div>
            <div class="field">
                <label>Username</label>
                <input type="text" value="<%= p.getUsername() == null ? "" : p.getUsername() %>" disabled />
            </div>
            <div class="field">
                <label for="patientName">Full Name *</label>
                <input type="text" id="patientName" name="patientName" maxlength="50" required
                       value="<%= p.getPatientName() == null ? "" : p.getPatientName() %>"
                       data-validate="required:Name is required.|maxlen:50:Max 50 characters." />
            </div>
        </div>

        <div class="row">
            <div class="field">
                <label for="age">Age *</label>
                <input type="number" id="age" name="age" min="1" max="130" required
                       value="<%= p.getAge() %>" />
            </div>
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
            <div class="field" style="flex:2 1 480px">
                <label for="email">Email</label>
                <input type="email" id="email" name="email"
                       value="<%= p.getEmail() == null ? "" : p.getEmail() %>"
                       data-validate="email:Please enter a valid email." />
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
            <a class="btn btn-secondary" href="<%= request.getContextPath() %>/myProfile">Cancel</a>
        </div>
    </form>
</div>
<% } %>

<%@ include file="/jsp/includes/footer.jsp" %>
