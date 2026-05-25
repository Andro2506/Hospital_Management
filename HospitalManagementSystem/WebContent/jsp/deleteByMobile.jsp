<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    if (session.getAttribute("admin") == null) {
        response.sendRedirect(request.getContextPath() + "/jsp/login.jsp?expired=1");
        return;
    }
    request.setAttribute("pageTitle", "Delete Patient by Mobile");
%>
<%@ include file="/jsp/includes/header.jsp" %>

<h1 class="page-title">Delete Patient by Mobile Number</h1>

<div class="card" style="max-width:520px">
    <form method="post" action="<%= request.getContextPath() %>/deleteByMobile" data-validate-form>
        <div class="field">
            <label for="mobile">Mobile Number</label>
            <input type="text" id="mobile" name="mobile" maxlength="10" required
                   data-validate="required:Mobile is required.|contact:Must be exactly 10 digits." />
            <span class="help">Enter the 10-digit mobile number of the patient to remove.</span>
        </div>
        <div class="actions">
            <button class="btn btn-danger" type="submit"
                    data-confirm="Permanently delete patient(s) matching this mobile number?">
                Delete
            </button>
            <a class="btn btn-secondary" href="<%= request.getContextPath() %>/jsp/adminDashboard.jsp">Cancel</a>
        </div>
    </form>
</div>

<%@ include file="/jsp/includes/footer.jsp" %>
