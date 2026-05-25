package com.hospital.servlet;

import com.hospital.dao.BillDAO;
import com.hospital.dao.PatientDAO;
import com.hospital.model.Bill;
import com.hospital.model.Patient;
import com.hospital.util.ValidationUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * US005-Polymorphism - Search bill details by either Email OR PatientRegistrationId.
 *
 * <p>Routes the request to the correct overload of {@link BillDAO} based on the
 * {@code searchBy} radio (or by sniffing for '@' if not specified).
 * Returns the patient + bill rows + payable amount.
 */
@WebServlet("/searchBill")
public class SearchBillServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        if (!SessionGuard.requireAdmin(req, resp)) return;

        String key      = req.getParameter("key");
        String searchBy = req.getParameter("searchBy"); // "email" or "patientId"

        try {
            if (key != null && !key.isEmpty()) {
                BillDAO billDao = new BillDAO();
                PatientDAO pdao = new PatientDAO();
                List<Bill> bills;
                Patient patient;

                if ("email".equalsIgnoreCase(searchBy)
                        || (searchBy == null && key.contains("@"))) {
                    if (!ValidationUtil.isEmail(key)) {
                        throw new IllegalArgumentException("Email format is invalid.");
                    }
                    bills = billDao.searchByEmail(key);
                    patient = pdao.findByEmail(key);
                    req.setAttribute("searchMode", "email");
                } else {
                    if (!ValidationUtil.isPatientId(key)) {
                        throw new IllegalArgumentException("Patient Id must be exactly 7 digits.");
                    }
                    bills = billDao.searchByPatientId(key);
                    patient = pdao.findById(key);
                    req.setAttribute("searchMode", "patientId");
                }

                req.setAttribute("bills", bills);
                req.setAttribute("patient", patient);

                double total = 0;
                for (Bill b : bills) total += b.getPayableAmount();
                req.setAttribute("totalPayable", total);

                if (bills.isEmpty() || patient == null) {
                    req.setAttribute("errorMessage", "No bills found for the given input.");
                } else {
                    req.setAttribute("successMessage",
                            "Found " + bills.size() + " bill(s). Total payable: Rs. " + total);
                }
                req.setAttribute("key", key);
            }
            req.getRequestDispatcher("/jsp/searchBill.jsp").forward(req, resp);
        } catch (IllegalArgumentException ie) {
            req.setAttribute("errorMessage", ie.getMessage());
            req.getRequestDispatcher("/jsp/searchBill.jsp").forward(req, resp);
        } catch (Exception e) {
            req.setAttribute("errorMessage", "Search failed: " + e.getMessage());
            req.getRequestDispatcher("/jsp/error.jsp").forward(req, resp);
        }
    }
}
