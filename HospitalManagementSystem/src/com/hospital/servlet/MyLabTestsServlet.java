package com.hospital.servlet;

import com.hospital.dao.LabDAO;
import com.hospital.model.Patient;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/** Lists lab tests for the logged-in patient only. */
@WebServlet("/myLabTests")
public class MyLabTestsServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        if (!SessionGuard.requirePatient(req, resp)) return;
        try {
            HttpSession session = req.getSession(false);
            Patient me = (Patient) session.getAttribute("patient");
            req.setAttribute("labs", new LabDAO().findByPatient(me.getPatientId()));
            req.getRequestDispatcher("/jsp/myLabTests.jsp").forward(req, resp);
        } catch (Exception e) {
            req.setAttribute("errorMessage", "Could not load your lab tests: " + e.getMessage());
            req.getRequestDispatcher("/jsp/error.jsp").forward(req, resp);
        }
    }
}
