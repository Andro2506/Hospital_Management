package com.hospital.servlet;

import com.hospital.dao.PatientDAO;
import com.hospital.util.ValidationUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/** US007 - Delete patient by mobile number. */
@WebServlet("/deleteByMobile")
public class DeleteByMobileServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        if (!SessionGuard.requireAdmin(req, resp)) return;
        req.getRequestDispatcher("/jsp/deleteByMobile.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        if (!SessionGuard.requireAdmin(req, resp)) return;

        String mobile = req.getParameter("mobile");
        if (!ValidationUtil.isContactNo(mobile)) {
            req.setAttribute("errorMessage", "Mobile Number must be exactly 10 digits.");
            req.getRequestDispatcher("/jsp/deleteByMobile.jsp").forward(req, resp);
            return;
        }

        try {
            int deleted = new PatientDAO().deleteByContactNo(mobile);
            if (deleted > 0) {
                req.setAttribute("successMessage",
                        "Deleted " + deleted + " patient record(s) matching mobile " + mobile + ".");
            } else {
                req.setAttribute("errorMessage",
                        "No patient was found with mobile number " + mobile + ".");
            }
            req.getRequestDispatcher("/jsp/deleteByMobile.jsp").forward(req, resp);
        } catch (Exception e) {
            req.setAttribute("errorMessage", "Could not delete by mobile: " + e.getMessage());
            req.getRequestDispatcher("/jsp/error.jsp").forward(req, resp);
        }
    }
}
