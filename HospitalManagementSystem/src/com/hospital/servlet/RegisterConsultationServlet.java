package com.hospital.servlet;

import com.hospital.dao.ConsultationDAO;
import com.hospital.model.Consultation;
import com.hospital.model.Patient;
import com.hospital.util.ValidationUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

/** Lets a logged-in patient request a doctor consultation. */
@WebServlet("/registerConsultation")
public class RegisterConsultationServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        if (!SessionGuard.requirePatient(req, resp)) return;
        try {
            HttpSession session = req.getSession(false);
            Patient me = (Patient) session.getAttribute("patient");
            req.setAttribute("consultations", new ConsultationDAO().findByPatient(me.getPatientId()));
            req.getRequestDispatcher("/jsp/registerConsultation.jsp").forward(req, resp);
        } catch (Exception e) {
            req.setAttribute("errorMessage", "Could not load your consultations: " + e.getMessage());
            req.getRequestDispatcher("/jsp/error.jsp").forward(req, resp);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        if (!SessionGuard.requirePatient(req, resp)) return;

        try {
            HttpSession session = req.getSession(false);
            Patient me = (Patient) session.getAttribute("patient");

            String department    = req.getParameter("department");
            String doctorName    = req.getParameter("doctorName");
            String preferredDate = req.getParameter("preferredDate");
            String notes         = req.getParameter("notes");

            if (ValidationUtil.isBlank(department)) {
                throw new IllegalArgumentException("Please select a department.");
            }
            if (ValidationUtil.isBlank(doctorName)) {
                throw new IllegalArgumentException("Doctor name is required.");
            }
            if (doctorName.length() > 60) {
                throw new IllegalArgumentException("Doctor name must be 60 characters or fewer.");
            }
            if (ValidationUtil.isBlank(preferredDate)) {
                throw new IllegalArgumentException("Preferred date is required.");
            }
            try {
                LocalDate when = LocalDate.parse(preferredDate);
                if (when.isBefore(LocalDate.now())) {
                    throw new IllegalArgumentException("Preferred date cannot be in the past.");
                }
            } catch (DateTimeParseException pe) {
                throw new IllegalArgumentException("Preferred date is not a valid date.");
            }
            if (notes != null && notes.length() > 500) {
                throw new IllegalArgumentException("Notes must be 500 characters or fewer.");
            }

            Consultation c = new Consultation();
            c.setPatientId(me.getPatientId());
            c.setDepartment(department.trim());
            c.setDoctorName(doctorName.trim());
            c.setPreferredDate(preferredDate);
            c.setNotes(notes == null ? "" : notes.trim());
            c.setStatus(Consultation.STATUS_PENDING);
            new ConsultationDAO().insert(c);

            req.setAttribute("successMessage",
                    "Consultation request submitted. Reference id: " + c.getConsultationId() +
                    ". The hospital will reach out to confirm.");
            doGet(req, resp);
        } catch (IllegalArgumentException ie) {
            req.setAttribute("errorMessage", ie.getMessage());
            doGet(req, resp);
        } catch (Exception e) {
            req.setAttribute("errorMessage", "Could not register consultation: " + e.getMessage());
            req.getRequestDispatcher("/jsp/error.jsp").forward(req, resp);
        }
    }
}
