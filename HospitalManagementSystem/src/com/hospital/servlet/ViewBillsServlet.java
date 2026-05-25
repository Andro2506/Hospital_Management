package com.hospital.servlet;

import com.hospital.dao.BillDAO;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/** Lists all bills for the admin (read-only). */
@WebServlet("/viewBills")
public class ViewBillsServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        if (!SessionGuard.requireAdmin(req, resp)) return;
        try {
            req.setAttribute("bills", new BillDAO().findAll());
            req.getRequestDispatcher("/jsp/viewBills.jsp").forward(req, resp);
        } catch (Exception e) {
            req.setAttribute("errorMessage", "Could not load bills: " + e.getMessage());
            req.getRequestDispatcher("/jsp/error.jsp").forward(req, resp);
        }
    }
}
