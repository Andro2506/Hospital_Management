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

/** US002 - Capture new patient. */
@WebServlet("/addPatient")
public class AddPatientServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        if (!SessionGuard.requireAdmin(req, resp)) return;
        req.getRequestDispatcher("/jsp/addPatient.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        if (!SessionGuard.requireAdmin(req, resp)) return;

        try {
            Patient p = readForm(req);
            new PatientDAO().insert(p);
            // Mirror to in-memory collection (US001-Collection requirement).
            PatientCollection.getInstance().add(p);

            req.setAttribute("successMessage",
                    "Patient registered successfully. PRN: " + p.getPatientId());
            req.getRequestDispatcher("/jsp/addPatient.jsp").forward(req, resp);
        } catch (IllegalArgumentException ie) {
            req.setAttribute("errorMessage", ie.getMessage());
            req.getRequestDispatcher("/jsp/addPatient.jsp").forward(req, resp);
        } catch (Exception e) {
            req.setAttribute("errorMessage", "Could not save patient: " + e.getMessage());
            req.getRequestDispatcher("/jsp/error.jsp").forward(req, resp);
        }
    }

    /** Reads and validates the form, throws IllegalArgumentException with friendly messages. */
    static Patient readForm(HttpServletRequest req) {
        Patient p = new Patient();

        String pid = req.getParameter("patientId");
        if (!ValidationUtil.isPatientId(pid)) {
            throw new IllegalArgumentException("Registration Number must be exactly 7 digits.");
        }
        p.setPatientId(pid);

        String name = req.getParameter("patientName");
        if (ValidationUtil.isBlank(name) || name.length() > 50) {
            throw new IllegalArgumentException("Patient Name is required and must be 50 characters or fewer.");
        }
        p.setPatientName(name.trim());

        String email = req.getParameter("email");
        if (!ValidationUtil.isBlank(email) && !ValidationUtil.isEmail(email)) {
            throw new IllegalArgumentException("Email format is invalid.");
        }
        p.setEmail(email == null ? "" : email.trim());

        int age = ValidationUtil.parseInt(req.getParameter("age"), "Age");
        if (age <= 0 || age > 130) {
            throw new IllegalArgumentException("Age must be between 1 and 130.");
        }
        p.setAge(age);

        p.setBloodGroup(orBlank(req.getParameter("bloodGroup")));
        p.setPatientDOB(orBlank(req.getParameter("patientDOB")));

        String gender = req.getParameter("gender");
        if (gender == null || !(gender.equals("Male") || gender.equals("Female") || gender.equals("Other"))) {
            throw new IllegalArgumentException("Gender must be Male, Female, or Other.");
        }
        p.setGender(gender);

        p.setWardNumber(orBlank(req.getParameter("wardNumber")));
        p.setDoctorId(orBlank(req.getParameter("doctorId")));
        p.setDoctorName(orBlank(req.getParameter("doctorName")));

        String address = req.getParameter("address");
        if (address != null && address.length() > 100) {
            throw new IllegalArgumentException("Address must be 100 characters or fewer.");
        }
        p.setAddress(orBlank(address));

        String contact = req.getParameter("contactNo");
        if (!ValidationUtil.isContactNo(contact)) {
            throw new IllegalArgumentException("Contact Number must be exactly 10 digits.");
        }
        p.setContactNo(contact);

        String aadhar = req.getParameter("aadharNumber");
        if (!ValidationUtil.isAadhar(aadhar)) {
            throw new IllegalArgumentException("Aadhar Number must be exactly 12 digits.");
        }
        p.setAadharNumber(aadhar);

        return p;
    }

    private static String orBlank(String s) { return s == null ? "" : s.trim(); }
}
