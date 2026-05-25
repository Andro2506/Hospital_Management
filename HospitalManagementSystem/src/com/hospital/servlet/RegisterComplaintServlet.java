package com.hospital.servlet;

import com.hospital.dao.ComplaintDAO;
import com.hospital.model.Complaint;
import com.hospital.model.Visitor;
import com.hospital.util.ValidationUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/** US011 - Register a complaint. Visitor must be logged in. */
@WebServlet("/registerComplaint")
public class RegisterComplaintServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        if (!SessionGuard.requireVisitor(req, resp)) return;
        try {
            HttpSession session = req.getSession(false);
            Visitor v = (Visitor) session.getAttribute("visitor");
            req.setAttribute("complaints", new ComplaintDAO().findByVisitor(v.getVisitorId()));
        } catch (Exception ignored) { /* keep page usable even if listing fails */ }
        req.getRequestDispatcher("/jsp/registerComplaint.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        if (!SessionGuard.requireVisitor(req, resp)) return;

        String issue = req.getParameter("issue");
        if (ValidationUtil.isBlank(issue)) {
            req.setAttribute("errorMessage", "Please describe your complaint before submitting.");
            doGet(req, resp);
            return;
        }
        if (issue.length() > 500) {
            req.setAttribute("errorMessage", "Complaint must be 500 characters or fewer.");
            doGet(req, resp);
            return;
        }

        try {
            HttpSession session = req.getSession(false);
            Visitor v = (Visitor) session.getAttribute("visitor");
            Complaint c = new Complaint();
            c.setVisitorId(v.getVisitorId());
            c.setIssue(issue.trim());
            new ComplaintDAO().insert(c);

            req.setAttribute("successMessage", "Your complaint has been registered. Reference id: " + c.getComplaintId());
            doGet(req, resp);
        } catch (Exception e) {
            req.setAttribute("errorMessage", "Could not register complaint: " + e.getMessage());
            req.getRequestDispatcher("/jsp/error.jsp").forward(req, resp);
        }
    }
}
