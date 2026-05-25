package com.hospital.servlet;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * US010 - View hospital facilities, paginated by department.
 *
 * <p>Facilities are static (no admin CRUD requirement in the user story), so we
 * keep them in-memory grouped by department.
 */
@WebServlet("/viewFacilities")
public class ViewFacilitiesServlet extends HttpServlet {

    private static final int PAGE_SIZE = 5;

    /** A single facility entry: department + facility name + brief description. */
    public static class Facility {
        public final String department;
        public final String name;
        public final String description;
        public Facility(String d, String n, String desc) {
            this.department = d; this.name = n; this.description = desc;
        }
        public String getDepartment()  { return department; }
        public String getName()        { return name; }
        public String getDescription() { return description; }
    }

    private static final List<Facility> ALL = new ArrayList<>(Arrays.asList(
        new Facility("Cardiology",  "ECG / Echocardiogram",   "Resting and stress ECG, 2D echo, 24/7 cath lab access."),
        new Facility("Cardiology",  "Cardiac ICU",            "10-bed CCU with continuous monitoring and on-call cardiologists."),
        new Facility("Radiology",   "MRI 1.5T",               "Whole-body MRI scans with same-day report turnaround."),
        new Facility("Radiology",   "CT Scan (128 slice)",    "Contrast and non-contrast scans, 24/7 emergency availability."),
        new Facility("Radiology",   "Digital X-Ray",          "Low-dose digital radiography with PACS reporting."),
        new Facility("Pathology",   "CBC / Hematology",       "Complete Blood Count and related hematology panels."),
        new Facility("Pathology",   "Biochemistry Lab",       "Full chemistry panel, lipid profile, LFT, KFT, thyroid."),
        new Facility("Pathology",   "Microbiology",           "Cultures, sensitivity testing, infectious disease panels."),
        new Facility("Pediatrics",  "Neonatal ICU",           "Level III NICU with ventilators and incubators."),
        new Facility("Pediatrics",  "Vaccination Clinic",     "All routine and travel vaccinations under WHO guidelines."),
        new Facility("Orthopedics", "Joint Replacement",      "Knee, hip and shoulder replacement with rehabilitation."),
        new Facility("Orthopedics", "Sports Injury Center",   "Arthroscopy, ACL/PCL repair, physiotherapy."),
        new Facility("Emergency",   "24/7 ER",                "Trauma care, ambulance dispatch, on-site triage team."),
        new Facility("Emergency",   "Ambulance Service",      "Basic and advanced life-support ambulances available 24/7.")
    ));

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        if (!SessionGuard.requireVisitor(req, resp)) return;

        int page = 1;
        try { page = Math.max(1, Integer.parseInt(req.getParameter("page"))); }
        catch (Exception ignored) { /* default to 1 */ }

        int total = ALL.size();
        int totalPages = Math.max(1, (int) Math.ceil(total / (double) PAGE_SIZE));
        if (page > totalPages) page = totalPages;
        int from = (page - 1) * PAGE_SIZE;
        int to   = Math.min(from + PAGE_SIZE, total);

        req.setAttribute("facilities", ALL.subList(from, to));
        req.setAttribute("page", page);
        req.setAttribute("totalPages", totalPages);
        req.setAttribute("total", total);
        req.setAttribute("pageSize", PAGE_SIZE);
        req.getRequestDispatcher("/jsp/viewFacilities.jsp").forward(req, resp);
    }
}
