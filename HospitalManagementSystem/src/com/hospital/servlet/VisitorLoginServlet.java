package com.hospital.servlet;

import com.hospital.dao.VisitorDAO;
import com.hospital.model.Visitor;
import com.hospital.util.ValidationUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/** US009 - Visitor login. Same UserId/Password rules as admin. */
@WebServlet("/visitorLogin")
public class VisitorLoginServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.getRequestDispatcher("/jsp/visitorLogin.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String userId = req.getParameter("userId");
        String pwd    = req.getParameter("password");

        if (!ValidationUtil.isUserId(userId)) {
            req.setAttribute("loginError", "User ID must be alphanumeric and at least 8 characters.");
            req.getRequestDispatcher("/jsp/visitorLogin.jsp").forward(req, resp);
            return;
        }
        if (!ValidationUtil.isPassword(pwd)) {
            req.setAttribute("loginError", "Password must be at least 10 characters and include "
                    + "an uppercase letter, a number, and a special character.");
            req.getRequestDispatcher("/jsp/visitorLogin.jsp").forward(req, resp);
            return;
        }

        try {
            Visitor v = new VisitorDAO().authenticate(userId, pwd);
            if (v == null) {
                req.setAttribute("loginError", "Invalid credentials. Please try again.");
                req.getRequestDispatcher("/jsp/visitorLogin.jsp").forward(req, resp);
                return;
            }
            HttpSession session = req.getSession(true);
            session.setAttribute("visitor", v);
            session.setAttribute("flash", "Welcome, " + v.getFirstName() + "!");
            resp.sendRedirect(req.getContextPath() + "/jsp/visitorDashboard.jsp");
        } catch (Exception e) {
            req.setAttribute("errorMessage", "Login failed: " + e.getMessage());
            req.getRequestDispatcher("/jsp/error.jsp").forward(req, resp);
        }
    }
}
