<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<% request.setAttribute("pageTitle", "Visitor Registration"); %>
<%@ include file="/jsp/includes/header.jsp" %>

<h1 class="page-title">Create Visitor Account</h1>
<p class="subtle">All fields marked * are required.</p>

<div class="card">
    <form method="post" action="<%= request.getContextPath() %>/visitorRegister" data-validate-form>
        <div class="row">
            <div class="field">
                <label for="username">Username *</label>
                <input type="text" id="username" name="username" required
                       data-validate="required:Username is required.|userId:Alphanumeric, min 8 chars." />
                <span class="help">Alphanumeric, minimum 8 characters.</span>
            </div>
            <div class="field">
                <label for="firstName">First Name *</label>
                <input type="text" id="firstName" name="firstName" maxlength="50" required
                       data-validate="required:First Name is required." />
            </div>
            <div class="field">
                <label for="lastName">Last Name *</label>
                <input type="text" id="lastName" name="lastName" maxlength="50" required
                       data-validate="required:Last Name is required." />
            </div>
        </div>

        <div class="row">
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
            <div class="field">
                <label for="email">Email *</label>
                <input type="email" id="email" name="email" required
                       data-validate="required:Email is required.|email:Please enter a valid email." />
            </div>
        </div>

        <div class="row">
            <div class="field">
                <label for="mobileNumber">Mobile Number *</label>
                <input type="text" id="mobileNumber" name="mobileNumber" maxlength="10" required
                       data-validate="required:Mobile is required.|contact:10 digits required." />
            </div>
            <div class="field">
                <label>Gender *</label>
                <div class="radio-group">
                    <label><input type="radio" name="gender" value="Male" required /> Male</label>
                    <label><input type="radio" name="gender" value="Female" /> Female</label>
                    <label><input type="radio" name="gender" value="Other" /> Other</label>
                </div>
            </div>
            <div class="field">
                <label for="city">City *</label>
                <select id="city" name="city" required data-validate="required:City is required.">
                    <option value="">-- Select --</option>
                    <option>Mumbai</option>
                    <option>Pune</option>
                    <option>Delhi</option>
                    <option>Bengaluru</option>
                    <option>Hyderabad</option>
                    <option>Chennai</option>
                    <option>Kolkata</option>
                    <option>Ahmedabad</option>
                </select>
            </div>
        </div>

        <div class="actions">
            <button class="btn btn-primary" type="submit">Register</button>
            <a class="btn btn-secondary" href="<%= request.getContextPath() %>/jsp/visitorLogin.jsp">Back to Login</a>
        </div>
    </form>
</div>

<%@ include file="/jsp/includes/footer.jsp" %>
