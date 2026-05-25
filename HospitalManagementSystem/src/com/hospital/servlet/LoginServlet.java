package com.hospital.servlet;

import com.hospital.dao.AdminDAO;
import com.hospital.model.Admin;
import com.hospital.util.ValidationUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * US001 - Admin login.
 *
 * <p>UserId rule: alphanumeric, min 8 chars.
 * <p>Password rule: min 10 chars, at least 1 uppercase, 1 digit, 1 special character.
 */
@WebServlet("/login")
public class LoginServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.getRequestDispatcher("/jsp/login.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String userId   = req.getParameter("userId");
        String password = req.getParameter("password");

        if (!ValidationUtil.isUserId(userId)) {
            forwardWithError(req, resp, "User ID must be alphanumeric and at least 8 characters.");
            return;
        }
        if (!ValidationUtil.isPassword(password)) {
            forwardWithError(req, resp, "Password must be at least 10 characters and include "
                    + "an uppercase letter, a number, and a special character.");
            return;
        }

        try {
            Admin admin = new AdminDAO().authenticate(userId, password);
            if (admin == null) {
                forwardWithError(req, resp, "Invalid credentials. Please try again.");
                return;
            }
            HttpSession session = req.getSession(true);
            session.setAttribute("admin", admin);
            session.setAttribute("flash", "Welcome back, " + admin.getUsername() + "!");
            resp.sendRedirect(req.getContextPath() + "/jsp/adminDashboard.jsp");
        } catch (Exception e) {
            req.setAttribute("errorMessage", "Login failed due to a server error: " + e.getMessage());
            req.getRequestDispatcher("/jsp/error.jsp").forward(req, resp);
        }
    }

    private void forwardWithError(HttpServletRequest req, HttpServletResponse resp,
                                  String msg) throws ServletException, IOException {
        req.setAttribute("loginError", msg);
        req.getRequestDispatcher("/jsp/login.jsp").forward(req, resp);
    }
}
