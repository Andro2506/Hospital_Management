package com.hospital.servlet;

import com.hospital.dao.BillDAO;
import com.hospital.dao.LabDAO;
import com.hospital.dao.PatientDAO;
import com.hospital.model.Bill;
import com.hospital.model.Lab;
import com.hospital.model.Patient;
import com.hospital.util.ValidationUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * US003-PatientDetailsManagement + US004-LabInheritance.
 *
 * <p>Registers a patient for a lab test (CBC/BEL only). On success, creates a Lab
 * row and a Bill row (Bill extends Lab) and shows the acknowledgment message
 * "Patient Registration for Lab tests is Successful".
 */
@WebServlet("/registerLabTest")
public class RegisterLabTestServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        if (!SessionGuard.requireAdmin(req, resp)) return;
        req.getRequestDispatcher("/jsp/labRegister.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        if (!SessionGuard.requireAdmin(req, resp)) return;

        try {
            String pid       = req.getParameter("patientId");
            String testType  = req.getParameter("testType");
            String category  = req.getParameter("category");
            String mobile    = req.getParameter("mobileNumber");

            if (!ValidationUtil.isPatientId(pid)) {
                throw new IllegalArgumentException("Patient Id must be exactly 7 digits.");
            }
            Patient patient = new PatientDAO().findById(pid);
            if (patient == null) {
                throw new IllegalArgumentException("No patient found with PRN " + pid + ". "
                        + "Please register the patient before booking a lab test.");
            }
            if (!LabDAO.isValidTestType(testType)) {
                throw new IllegalArgumentException("Invalid lab test selected. "
                        + "Allowed test types are: CBC, BEL.");
            }
            if (ValidationUtil.isBlank(category)) {
                throw new IllegalArgumentException("Category is required.");
            }
            int weight = ValidationUtil.parseInt(req.getParameter("weight"), "Weight");
            int height = ValidationUtil.parseInt(req.getParameter("height"), "Height");
            if (weight <= 0 || weight > 500) {
                throw new IllegalArgumentException("Weight must be between 1 and 500 (kg).");
            }
            if (height <= 0 || height > 300) {
                throw new IllegalArgumentException("Height must be between 1 and 300 (cm).");
            }
            if (!ValidationUtil.isContactNo(mobile)) {
                throw new IllegalArgumentException("Mobile Number must be exactly 10 digits.");
            }

            // Persist Lab row.
            Lab lab = new Lab();
            lab.setPatientId(pid);
            lab.setTestType(testType.trim().toUpperCase());
            lab.setCategory(category.trim());
            lab.setWeight(weight);
            lab.setHeight(height);
            lab.setMobileNumber(mobile);
            int labId = new LabDAO().insert(lab);

            // Persist matching Bill row (Bill extends Lab).
            Bill bill = new Bill();
            bill.setLabId(labId);
            bill.setPatientId(pid);
            bill.setTestType(lab.getTestType());
            bill.setCategory(lab.getCategory());
            bill.setWeight(weight);
            bill.setHeight(height);
            bill.setMobileNumber(mobile);
            bill.setPayableAmount(BillDAO.priceFor(lab.getTestType()));
            int billId = new BillDAO().insert(bill);

            req.setAttribute("successMessage",
                    "Patient Registration for Lab tests is Successful. " +
                    "Lab Id: " + labId + ", Bill Id: " + billId +
                    ", Payable Amount: Rs. " + bill.getPayableAmount());
            req.setAttribute("lab", lab);
            req.setAttribute("bill", bill);
            req.setAttribute("patient", patient);
            req.getRequestDispatcher("/jsp/labRegister.jsp").forward(req, resp);
        } catch (IllegalArgumentException ie) {
            req.setAttribute("errorMessage", ie.getMessage());
            req.getRequestDispatcher("/jsp/labRegister.jsp").forward(req, resp);
        } catch (Exception e) {
            req.setAttribute("errorMessage", "Could not register lab test: " + e.getMessage());
            req.getRequestDispatcher("/jsp/error.jsp").forward(req, resp);
        }
    }
}
