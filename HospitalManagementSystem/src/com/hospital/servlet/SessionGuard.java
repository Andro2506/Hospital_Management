package com.hospital.servlet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/** Helper for session-protected servlets to avoid duplicating redirect logic. */
final class SessionGuard {

    private SessionGuard() { }

    /** @return true if an admin is logged in; otherwise sends a redirect and returns false. */
    static boolean requireAdmin(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("admin") == null) {
            resp.sendRedirect(req.getContextPath() + "/jsp/login.jsp?expired=1");
            return false;
        }
        return true;
    }

    /** @return true if a visitor is logged in; otherwise sends a redirect and returns false. */
    static boolean requireVisitor(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("visitor") == null) {
            resp.sendRedirect(req.getContextPath() + "/jsp/visitorLogin.jsp?expired=1");
            return false;
        }
        return true;
    }
}
