package com.hospital.servlet;

import com.hospital.dao.PatientDAO;
import com.hospital.model.Patient;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/** US005 - Paginated listing of all active patients. 5 records per page. */
@WebServlet("/viewPatients")
public class ViewPatientsServlet extends HttpServlet {

    private static final int PAGE_SIZE = 5;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        if (!SessionGuard.requireAdmin(req, resp)) return;

        int page = 1;
        try { page = Math.max(1, Integer.parseInt(req.getParameter("page"))); }
        catch (Exception ignored) { /* default to 1 */ }

        try {
            PatientDAO dao = new PatientDAO();
            int total = dao.countAll();
            int totalPages = Math.max(1, (int) Math.ceil(total / (double) PAGE_SIZE));
            if (page > totalPages) page = totalPages;
            int offset = (page - 1) * PAGE_SIZE;
            List<Patient> rows = dao.findPage(offset, PAGE_SIZE);

            req.setAttribute("patients", rows);
            req.setAttribute("page", page);
            req.setAttribute("totalPages", totalPages);
            req.setAttribute("total", total);
            req.setAttribute("pageSize", PAGE_SIZE);
            req.getRequestDispatcher("/jsp/viewPatients.jsp").forward(req, resp);
        } catch (Exception e) {
            req.setAttribute("errorMessage", "Could not load patients: " + e.getMessage());
            req.getRequestDispatcher("/jsp/error.jsp").forward(req, resp);
        }
    }
}
