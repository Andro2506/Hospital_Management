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

/**
 * US003 - Edit patient. GET loads existing details; POST applies the update.
 * PatientId is displayed read-only in the JSP.
 */
@WebServlet("/editPatient")
public class EditPatientServlet extends HttpServlet {

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
                    if (p == null) {
                        req.setAttribute("errorMessage", "No patient found with PRN " + pid + ".");
                    } else {
                        req.setAttribute("patient", p);
                    }
                }
            }
            req.getRequestDispatcher("/jsp/editPatient.jsp").forward(req, resp);
        } catch (Exception e) {
            req.setAttribute("errorMessage", "Could not load patient: " + e.getMessage());
            req.getRequestDispatcher("/jsp/error.jsp").forward(req, resp);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        if (!SessionGuard.requireAdmin(req, resp)) return;

        try {
            Patient updated = AddPatientServlet.readForm(req);
            PatientDAO dao = new PatientDAO();
            Patient existing = dao.findById(updated.getPatientId());
            if (existing == null) {
                req.setAttribute("errorMessage", "Patient with PRN " + updated.getPatientId() + " not found.");
                req.getRequestDispatcher("/jsp/editPatient.jsp").forward(req, resp);
                return;
            }
            boolean ok = dao.update(updated);
            req.setAttribute("patient", dao.findById(updated.getPatientId()));
            if (ok) req.setAttribute("successMessage", "Patient details updated successfully.");
            else    req.setAttribute("errorMessage", "Could not update patient. Please try again.");
            req.getRequestDispatcher("/jsp/editPatient.jsp").forward(req, resp);
        } catch (IllegalArgumentException ie) {
            // Re-show form preserving the patient id so the user can correct mistakes.
            try {
                Patient p = new PatientDAO().findById(req.getParameter("patientId"));
                if (p != null) req.setAttribute("patient", p);
            } catch (Exception ignored) { /* fall through */ }
            req.setAttribute("errorMessage", ie.getMessage());
            req.getRequestDispatcher("/jsp/editPatient.jsp").forward(req, resp);
        } catch (Exception e) {
            req.setAttribute("errorMessage", "Could not update patient: " + e.getMessage());
            req.getRequestDispatcher("/jsp/error.jsp").forward(req, resp);
        }
    }
}
