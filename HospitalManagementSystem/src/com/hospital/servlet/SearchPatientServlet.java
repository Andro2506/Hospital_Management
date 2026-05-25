package com.hospital.servlet;

import com.hospital.dao.PatientDAO;
import com.hospital.model.Patient;
import com.hospital.util.ValidationUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/** US006 - Search patient by PatientId (PRN). */
@WebServlet("/searchPatient")
public class SearchPatientServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        if (!SessionGuard.requireAdmin(req, resp)) return;
        String pid = req.getParameter("patientId");
        try {
            if (pid != null && !pid.isEmpty()) {
                if (!ValidationUtil.isPatientId(pid)) {
                    req.setAttribute("errorMessage", "Patient Id must be a 7-digit number.");
                } else {
                    Patient p = new PatientDAO().findById(pid);
                    if (p == null) req.setAttribute("errorMessage", "No patient found with PRN " + pid + ".");
                    else {
                        req.setAttribute("patient", p);
                        req.setAttribute("successMessage", "Patient found.");
                    }
                }
            }
            req.getRequestDispatcher("/jsp/searchPatient.jsp").forward(req, resp);
        } catch (Exception e) {
            req.setAttribute("errorMessage", "Search failed: " + e.getMessage());
            req.getRequestDispatcher("/jsp/error.jsp").forward(req, resp);
        }
    }
}
