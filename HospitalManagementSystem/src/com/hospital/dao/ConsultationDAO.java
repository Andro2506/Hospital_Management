package com.hospital.dao;

import com.hospital.model.Consultation;
import com.hospital.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/** Data access for doctor consultation requests booked by patients. */
public class ConsultationDAO {

    public int insert(Consultation c) throws SQLException {
        String sql = "INSERT INTO Consultation (PatientId, DoctorName, Department, " +
                "PreferredDate, Notes, Status) VALUES (?,?,?,?,?,?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, c.getPatientId());
            ps.setString(2, c.getDoctorName());
            ps.setString(3, c.getDepartment());
            ps.setString(4, c.getPreferredDate());
            ps.setString(5, c.getNotes());
            ps.setString(6, c.getStatus() == null ? Consultation.STATUS_PENDING : c.getStatus());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                int id = keys.next() ? keys.getInt(1) : -1;
                c.setConsultationId(id);
                return id;
            }
        }
    }

    public List<Consultation> findByPatient(String patientId) throws SQLException {
        String sql = "SELECT ConsultationId, PatientId, DoctorName, Department, " +
                "PreferredDate, Notes, Status FROM Consultation " +
                "WHERE PatientId=? ORDER BY ConsultationId DESC";
        List<Consultation> out = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, patientId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) out.add(read(rs));
            }
        }
        return out;
    }

    /**
     * Admin view: every consultation across all patients, joined with the
     * patient name. Most recent first.
     */
    public List<Consultation> findAll() throws SQLException {
        String sql = "SELECT c.ConsultationId, c.PatientId, c.DoctorName, c.Department, " +
                "c.PreferredDate, c.Notes, c.Status, p.PatientName " +
                "FROM Consultation c LEFT JOIN Patient p ON c.PatientId = p.PatientId " +
                "ORDER BY c.ConsultationId DESC";
        List<Consultation> out = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Consultation c = read(rs);
                c.setPatientName(rs.getString("PatientName"));
                out.add(c);
            }
        }
        return out;
    }

    /** Admin action: update the workflow status of a consultation request. */
    public boolean updateStatus(int consultationId, String status) throws SQLException {
        if (!isValidStatus(status)) {
            throw new IllegalArgumentException("Invalid status: " + status);
        }
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                     "UPDATE Consultation SET Status=? WHERE ConsultationId=?")) {
            ps.setString(1, status);
            ps.setInt(2, consultationId);
            return ps.executeUpdate() == 1;
        }
    }

    public static boolean isValidStatus(String status) {
        return Consultation.STATUS_PENDING.equals(status)
            || Consultation.STATUS_CONFIRMED.equals(status)
            || Consultation.STATUS_COMPLETED.equals(status);
    }

    private Consultation read(ResultSet rs) throws SQLException {
        Consultation c = new Consultation();
        c.setConsultationId(rs.getInt("ConsultationId"));
        c.setPatientId(rs.getString("PatientId"));
        c.setDoctorName(rs.getString("DoctorName"));
        c.setDepartment(rs.getString("Department"));
        c.setPreferredDate(rs.getString("PreferredDate"));
        c.setNotes(rs.getString("Notes"));
        c.setStatus(rs.getString("Status"));
        return c;
    }
}
