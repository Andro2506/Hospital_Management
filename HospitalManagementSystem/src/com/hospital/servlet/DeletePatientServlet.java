package com.hospital.servlet;

import com.hospital.dao.PatientCollection;
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
 * US004 - Delete patient. GET loads the patient for confirmation; POST deletes after
 * a confirm flag is present.
 */
@WebServlet("/deletePatient")
public class DeletePatientServlet extends HttpServlet {

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
                    else           req.setAttribute("patient", p);
                }
            }
            req.getRequestDispatcher("/jsp/deletePatient.jsp").forward(req, resp);
        } catch (Exception e) {
            req.setAttribute("errorMessage", "Could not load patient: " + e.getMessage());
            req.getRequestDispatcher("/jsp/error.jsp").forward(req, resp);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        if (!SessionGuard.requireAdmin(req, resp)) return;

        String pid = req.getParameter("patientId");
        if (!ValidationUtil.isPatientId(pid)) {
            req.setAttribute("errorMessage", "Patient Id must be a 7-digit number.");
            req.getRequestDispatcher("/jsp/deletePatient.jsp").forward(req, resp);
            return;
        }
        try {
            boolean ok = new PatientDAO().deleteById(pid);
            PatientCollection.getInstance().removeById(pid);
            if (ok) req.setAttribute("successMessage", "Patient with PRN " + pid + " was deleted.");
            else    req.setAttribute("errorMessage", "No patient was deleted (PRN " + pid + " not found).");
            req.getRequestDispatcher("/jsp/deletePatient.jsp").forward(req, resp);
        } catch (Exception e) {
            req.setAttribute("errorMessage", "Could not delete patient: " + e.getMessage());
            req.getRequestDispatcher("/jsp/error.jsp").forward(req, resp);
        }
    }
}
