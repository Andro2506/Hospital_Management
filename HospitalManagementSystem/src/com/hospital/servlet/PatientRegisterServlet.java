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
 * Public patient self-registration. The form collects everything the admin
 * "add patient" form does, plus the username/password for the patient's
 * future logins. On success the patient can immediately log in.
 */
@WebServlet("/patientRegister")
public class PatientRegisterServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.getRequestDispatcher("/jsp/patientRegister.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        try {
            String username = req.getParameter("username");
            String password = req.getParameter("password");
            String confirm  = req.getParameter("confirmPassword");

            if (!ValidationUtil.isUserId(username)) {
                throw new IllegalArgumentException(
                        "Username must be alphanumeric and at least 8 characters.");
            }
            if (!ValidationUtil.isPassword(password)) {
                throw new IllegalArgumentException(
                        "Password must be at least 10 characters and include "
                        + "an uppercase letter, a number, and a special character.");
            }
            if (!password.equals(confirm)) {
                throw new IllegalArgumentException("Password and Confirm Password must match.");
            }

            Patient p = AddPatientServlet.readForm(req);
            p.setUsername(username);
            p.setPassword(password);

            PatientDAO dao = new PatientDAO();
            if (dao.patientIdExists(p.getPatientId())) {
                throw new IllegalArgumentException("A patient with PRN "
                        + p.getPatientId() + " already exists. Please choose a different PRN.");
            }
            if (dao.usernameExists(username)) {
                throw new IllegalArgumentException("That username is already taken. Please choose another.");
            }

            dao.insert(p);
            // Mirror to in-memory collection (US001-Collection requirement).
            PatientCollection.getInstance().add(p);

            req.setAttribute("successMessage",
                    "Registration successful. Your PRN is " + p.getPatientId()
                    + ". You can now log in with username '" + username + "'.");
            req.getRequestDispatcher("/jsp/patientRegister.jsp").forward(req, resp);
        } catch (IllegalArgumentException ie) {
            req.setAttribute("errorMessage", ie.getMessage());
            req.getRequestDispatcher("/jsp/patientRegister.jsp").forward(req, resp);
        } catch (Exception e) {
            req.setAttribute("errorMessage", "Could not register: " + e.getMessage());
            req.getRequestDispatcher("/jsp/error.jsp").forward(req, resp);
        }
    }
}
