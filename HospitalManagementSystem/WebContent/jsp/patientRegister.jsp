<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<% request.setAttribute("pageTitle", "Patient Registration"); %>
<%@ include file="/jsp/includes/header.jsp" %>

<h1 class="page-title">Create a Patient Account</h1>
<p class="subtle">Register once to access your records, lab tests, bills, and to book consultations.
   All fields marked * are required.</p>

<div class="card">
    <form method="post" action="<%= request.getContextPath() %>/patientRegister" data-validate-form>

        <h2>Login Details</h2>
        <div class="row">
            <div class="field">
                <label for="username">Username *</label>
                <input type="text" id="username" name="username" required
                       data-validate="required:Username is required.|userId:Alphanumeric, min 8 chars." />
                <span class="help">Alphanumeric, minimum 8 characters.</span>
            </div>
            <div class="field">
                <label for="password">Password *</label>
                <input type="password" id="password" name="password" required
                       data-validate="required:Password is required.|password:Min 10 chars with 1 upper, 1 number, 1 special." />
                <span class="help">Min 10 chars with 1 uppercase, 1 number, 1 special character.</span>
            </div>
            <div class="field">
                <label for="confirmPassword">Confirm Password *</label>
                <input type="password" id="confirmPassword" name="confirmPassword" required
                       data-match="password" data-match-message="Passwords do not match." />
            </div>
        </div>

        <h2>Patient Details</h2>
        <div class="row">
            <div class="field">
                <label for="patientId">Registration Number (PRN) *</label>
                <input type="text" id="patientId" name="patientId" maxlength="7" required
                       data-validate="required:PRN is required.|patientId:Must be exactly 7 digits." />
                <span class="help">7-digit unique number (PRN).</span>
            </div>
            <div class="field">
                <label for="patientName">Full Name *</label>
                <input type="text" id="patientName" name="patientName" maxlength="50" required
                       data-validate="required:Name is required.|maxlen:50:Max 50 characters." />
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
            <!-- Hospital staff fields - patients leave these blank -->
            <input type="hidden" name="wardNumber" value="" />
            <input type="hidden" name="doctorId"   value="" />
            <input type="hidden" name="doctorName" value="" />

            <div class="field" style="flex:2 1 480px">
                <label for="email">Email *</label>
                <input type="email" id="email" name="email" required
                       data-validate="required:Email is required.|email:Please enter a valid email." />
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
            <button class="btn btn-primary" type="submit">Register</button>
            <a class="btn btn-secondary" href="<%= request.getContextPath() %>/jsp/patientLogin.jsp">Back to Login</a>
        </div>
    </form>
</div>

<%@ include file="/jsp/includes/footer.jsp" %>
