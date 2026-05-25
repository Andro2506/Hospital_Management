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

/**
 * View / edit the logged-in patient's own profile.
 *
 * <p>GET shows the current details; POST applies an update. PRN, Username and
 * the doctor / ward assignment are read-only - they are managed by hospital
 * staff, not the patient.
 */
@WebServlet({"/myProfile", "/myProfile/edit"})
public class PatientProfileServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        if (!SessionGuard.requirePatient(req, resp)) return;
        Patient session = sessionPatient(req);
        try {
            Patient fresh = new PatientDAO().findById(session.getPatientId());
            if (fresh != null) {
                fresh.setPassword(""); // never expose
                req.getSession().setAttribute("patient", fresh);
                req.setAttribute("patient", fresh);
            }
            boolean editMode = req.getRequestURI().endsWith("/edit");
            req.setAttribute("editMode", editMode);
            req.getRequestDispatcher("/jsp/myProfile.jsp").forward(req, resp);
        } catch (Exception e) {
            req.setAttribute("errorMessage", "Could not load your profile: " + e.getMessage());
            req.getRequestDispatcher("/jsp/error.jsp").forward(req, resp);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        if (!SessionGuard.requirePatient(req, resp)) return;

        Patient session = sessionPatient(req);
        try {
            Patient updated = new Patient();
            updated.setPatientId(session.getPatientId()); // PRN locked to session

            String name = req.getParameter("patientName");
            if (ValidationUtil.isBlank(name) || name.length() > 50) {
                throw new IllegalArgumentException("Name is required and must be 50 characters or fewer.");
            }
            updated.setPatientName(name.trim());

            String email = req.getParameter("email");
            if (!ValidationUtil.isBlank(email) && !ValidationUtil.isEmail(email)) {
                throw new IllegalArgumentException("Email format is invalid.");
            }
            updated.setEmail(email == null ? "" : email.trim());

            int age = ValidationUtil.parseInt(req.getParameter("age"), "Age");
            if (age <= 0 || age > 130) {
                throw new IllegalArgumentException("Age must be between 1 and 130.");
            }
            updated.setAge(age);

            updated.setBloodGroup(orBlank(req.getParameter("bloodGroup")));
            updated.setPatientDOB(orBlank(req.getParameter("patientDOB")));

            String gender = req.getParameter("gender");
            if (gender == null || !(gender.equals("Male") || gender.equals("Female") || gender.equals("Other"))) {
                throw new IllegalArgumentException("Gender must be Male, Female, or Other.");
            }
            updated.setGender(gender);

            String address = req.getParameter("address");
            if (address != null && address.length() > 100) {
                throw new IllegalArgumentException("Address must be 100 characters or fewer.");
            }
            updated.setAddress(orBlank(address));

            String contact = req.getParameter("contactNo");
            if (!ValidationUtil.isContactNo(contact)) {
                throw new IllegalArgumentException("Contact Number must be exactly 10 digits.");
            }
            updated.setContactNo(contact);

            String aadhar = req.getParameter("aadharNumber");
            if (!ValidationUtil.isAadhar(aadhar)) {
                throw new IllegalArgumentException("Aadhar Number must be exactly 12 digits.");
            }
            updated.setAadharNumber(aadhar);

            PatientDAO dao = new PatientDAO();
            dao.selfUpdate(updated);
            Patient fresh = dao.findById(session.getPatientId());
            fresh.setPassword("");
            req.getSession().setAttribute("patient", fresh);
            req.setAttribute("patient", fresh);
            req.setAttribute("successMessage", "Your details have been updated.");
            req.setAttribute("editMode", false);
            req.getRequestDispatcher("/jsp/myProfile.jsp").forward(req, resp);
        } catch (IllegalArgumentException ie) {
            req.setAttribute("patient", session);
            req.setAttribute("editMode", true);
            req.setAttribute("errorMessage", ie.getMessage());
            req.getRequestDispatcher("/jsp/myProfile.jsp").forward(req, resp);
        } catch (Exception e) {
            req.setAttribute("errorMessage", "Could not update your profile: " + e.getMessage());
            req.getRequestDispatcher("/jsp/error.jsp").forward(req, resp);
        }
    }

    private static Patient sessionPatient(HttpServletRequest req) {
        HttpSession s = req.getSession(false);
        return (Patient) s.getAttribute("patient");
    }

    private static String orBlank(String s) { return s == null ? "" : s.trim(); }
}
