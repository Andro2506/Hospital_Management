package com.hospital.servlet;

import com.hospital.dao.PatientDAO;
import com.hospital.model.Patient;
import com.hospital.util.ValidationUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/** Patient self-service login. Same UID / password rules as admin. */
@WebServlet("/patientLogin")
public class PatientLoginServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.getRequestDispatcher("/jsp/patientLogin.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String userId = req.getParameter("userId");
        String pwd    = req.getParameter("password");

        if (!ValidationUtil.isUserId(userId)) {
            req.setAttribute("loginError", "User ID must be alphanumeric and at least 8 characters.");
            req.getRequestDispatcher("/jsp/patientLogin.jsp").forward(req, resp);
            return;
        }
        if (!ValidationUtil.isPassword(pwd)) {
            req.setAttribute("loginError", "Password must be at least 10 characters and include "
                    + "an uppercase letter, a number, and a special character.");
            req.getRequestDispatcher("/jsp/patientLogin.jsp").forward(req, resp);
            return;
        }

        try {
            Patient p = new PatientDAO().authenticate(userId, pwd);
            if (p == null) {
                req.setAttribute("loginError", "Invalid credentials. Please try again.");
                req.getRequestDispatcher("/jsp/patientLogin.jsp").forward(req, resp);
                return;
            }
            HttpSession session = req.getSession(true);
            session.setAttribute("patient", p);
            session.setAttribute("flash", "Welcome back, " + p.getPatientName() + "!");
            resp.sendRedirect(req.getContextPath() + "/jsp/patientDashboard.jsp");
        } catch (Exception e) {
            req.setAttribute("errorMessage", "Login failed: " + e.getMessage());
            req.getRequestDispatcher("/jsp/error.jsp").forward(req, resp);
        }
    }
}
