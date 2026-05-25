package com.hospital.servlet;

import com.hospital.dao.VisitorDAO;
import com.hospital.model.Visitor;
import com.hospital.util.ValidationUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/** US008 - Visitor registration. */
@WebServlet("/visitorRegister")
public class VisitorRegisterServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.getRequestDispatcher("/jsp/visitorRegister.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String username  = req.getParameter("username");
        String firstName = req.getParameter("firstName");
        String lastName  = req.getParameter("lastName");
        String password  = req.getParameter("password");
        String confirm   = req.getParameter("confirmPassword");
        String email     = req.getParameter("email");
        String mobile    = req.getParameter("mobileNumber");
        String gender    = req.getParameter("gender");
        String city      = req.getParameter("city");

        try {
            if (!ValidationUtil.isUserId(username)) {
                throw new IllegalArgumentException("Username must be alphanumeric and at least 8 characters.");
            }
            if (ValidationUtil.isBlank(firstName)) {
                throw new IllegalArgumentException("First Name is required.");
            }
            if (ValidationUtil.isBlank(lastName)) {
                throw new IllegalArgumentException("Last Name is required.");
            }
            if (!ValidationUtil.isPassword(password)) {
                throw new IllegalArgumentException("Password must be at least 10 characters and include "
                        + "an uppercase letter, a number, and a special character.");
            }
            if (!password.equals(confirm)) {
                throw new IllegalArgumentException("Password and Confirm Password must match.");
            }
            if (!ValidationUtil.isEmail(email)) {
                throw new IllegalArgumentException("Email format is invalid.");
            }
            if (!ValidationUtil.isContactNo(mobile)) {
                throw new IllegalArgumentException("Mobile Number must be exactly 10 digits.");
            }
            if (gender == null || !(gender.equals("Male") || gender.equals("Female") || gender.equals("Other"))) {
                throw new IllegalArgumentException("Please select a gender.");
            }
            if (ValidationUtil.isBlank(city)) {
                throw new IllegalArgumentException("Please select a city.");
            }

            VisitorDAO dao = new VisitorDAO();
            if (dao.usernameExists(username)) {
                throw new IllegalArgumentException("That username is already registered. Please choose another.");
            }

            Visitor v = new Visitor();
            v.setUsername(username);
            v.setFirstName(firstName.trim());
            v.setLastName(lastName.trim());
            v.setPassword(password);
            v.setEmail(email.trim());
            v.setMobileNumber(mobile);
            v.setGender(gender);
            v.setCity(city);
            int id = dao.register(v);

            req.setAttribute("successMessage",
                    "Registration successful! Your visitor id is " + id + ". You can now log in.");
            req.getRequestDispatcher("/jsp/visitorRegister.jsp").forward(req, resp);
        } catch (IllegalArgumentException ie) {
            req.setAttribute("errorMessage", ie.getMessage());
            req.getRequestDispatcher("/jsp/visitorRegister.jsp").forward(req, resp);
        } catch (Exception e) {
            req.setAttribute("errorMessage", "Could not register visitor: " + e.getMessage());
            req.getRequestDispatcher("/jsp/error.jsp").forward(req, resp);
        }
    }
}
