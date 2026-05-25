package com.hospital.servlet;

import com.hospital.dao.ConsultationDAO;
import com.hospital.model.Consultation;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Admin-only screen showing every consultation request across all patients.
 *
 * <p>GET lists all consultations (joined with the patient's name).
 * <p>POST updates the workflow status of a single consultation
 *   ({@code PENDING}, {@code CONFIRMED}, {@code COMPLETED}).
 */
@WebServlet("/viewConsultations")
public class ViewConsultationsServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        if (!SessionGuard.requireAdmin(req, resp)) return;
        try {
            req.setAttribute("consultations", new ConsultationDAO().findAll());
            req.getRequestDispatcher("/jsp/viewConsultations.jsp").forward(req, resp);
        } catch (Exception e) {
            req.setAttribute("errorMessage", "Could not load consultations: " + e.getMessage());
            req.getRequestDispatcher("/jsp/error.jsp").forward(req, resp);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        if (!SessionGuard.requireAdmin(req, resp)) return;

        String idParam = req.getParameter("consultationId");
        String status  = req.getParameter("status");
        try {
            int consultationId;
            try {
                consultationId = Integer.parseInt(idParam);
            } catch (NumberFormatException nfe) {
                throw new IllegalArgumentException("Consultation id must be a number.");
            }
            if (!ConsultationDAO.isValidStatus(status)) {
                throw new IllegalArgumentException("Status must be PENDING, CONFIRMED, or COMPLETED.");
            }
            boolean ok = new ConsultationDAO().updateStatus(consultationId, status);
            if (ok) {
                req.getSession().setAttribute("flash",
                        "Consultation #" + consultationId + " status updated to " + status + ".");
            } else {
                req.getSession().setAttribute("flash",
                        "Consultation #" + consultationId + " not found.");
            }
            resp.sendRedirect(req.getContextPath() + "/viewConsultations");
        } catch (IllegalArgumentException ie) {
            req.setAttribute("errorMessage", ie.getMessage());
            doGet(req, resp);
        } catch (Exception e) {
            req.setAttribute("errorMessage", "Could not update consultation: " + e.getMessage());
            req.getRequestDispatcher("/jsp/error.jsp").forward(req, resp);
        }
    }
}
