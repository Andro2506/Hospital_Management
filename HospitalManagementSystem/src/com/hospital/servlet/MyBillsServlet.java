package com.hospital.servlet;

import com.hospital.dao.BillDAO;
import com.hospital.model.Bill;
import com.hospital.model.Patient;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;

/** Lists bills for the logged-in patient only. */
@WebServlet("/myBills")
public class MyBillsServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        if (!SessionGuard.requirePatient(req, resp)) return;
        try {
            HttpSession session = req.getSession(false);
            Patient me = (Patient) session.getAttribute("patient");
            List<Bill> bills = new BillDAO().searchByPatientId(me.getPatientId());

            double total = 0;
            for (Bill b : bills) total += b.getPayableAmount();
            req.setAttribute("bills", bills);
            req.setAttribute("totalPayable", total);
            req.getRequestDispatcher("/jsp/myBills.jsp").forward(req, resp);
        } catch (Exception e) {
            req.setAttribute("errorMessage", "Could not load your bills: " + e.getMessage());
            req.getRequestDispatcher("/jsp/error.jsp").forward(req, resp);
        }
    }
}
