package com.hospital.dao;

import com.hospital.model.Bill;
import com.hospital.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Data access for Bills. Demonstrates polymorphism via overloaded {@link #search(String)}
 * which dispatches between Email and PatientId.
 */
public class BillDAO {

    /** Standard pricing for the supported test types. */
    public static double priceFor(String testType) {
        if (testType == null) return 0;
        switch (testType.trim().toUpperCase()) {
            case "CBC": return 350.0;
            case "BEL": return 500.0;
            default:    return 0;
        }
    }

    /** Inserts and returns the generated BillId. */
    public int insert(Bill bill) throws SQLException {
        String sql = "INSERT INTO Bills (LabId, PatientId, PayableAmount) VALUES (?,?,?)";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt   (1, bill.getLabId());
            ps.setString(2, bill.getPatientId());
            ps.setDouble(3, bill.getPayableAmount());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                int id = keys.next() ? keys.getInt(1) : -1;
                bill.setBillId(id);
                return id;
            }
        }
    }

    public List<Bill> findAll() throws SQLException {
        String sql = "SELECT b.BillId, b.LabId, b.PatientId, b.PayableAmount, " +
                "       l.TestType, l.Category, l.Weight, l.Height, l.MobileNumber " +
                "FROM Bills b LEFT JOIN Lab l ON b.LabId = l.LabId " +
                "ORDER BY b.BillId DESC";
        List<Bill> out = new ArrayList<>();
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) out.add(readJoined(rs));
        }
        return out;
    }

    /**
     * Polymorphic search entry point. Picks the right strategy by inspecting the input:
     * if the value contains '@' it is treated as an Email, otherwise as a PatientId.
     */
    public List<Bill> search(String emailOrPatientId) throws SQLException {
        if (emailOrPatientId == null) return new ArrayList<>();
        if (emailOrPatientId.contains("@")) return searchByEmail(emailOrPatientId);
        return searchByPatientId(emailOrPatientId);
    }

    /** Polymorphism: overloaded by parameter name semantics; PatientId is a 7-digit string. */
    public List<Bill> searchByPatientId(String patientId) throws SQLException {
        String sql = "SELECT b.BillId, b.LabId, b.PatientId, b.PayableAmount, " +
                "       l.TestType, l.Category, l.Weight, l.Height, l.MobileNumber " +
                "FROM Bills b LEFT JOIN Lab l ON b.LabId = l.LabId " +
                "WHERE b.PatientId = ? ORDER BY b.BillId DESC";
        return query(sql, patientId);
    }

    /** Polymorphism: lookup via the patient's email address. */
    public List<Bill> searchByEmail(String email) throws SQLException {
        String sql = "SELECT b.BillId, b.LabId, b.PatientId, b.PayableAmount, " +
                "       l.TestType, l.Category, l.Weight, l.Height, l.MobileNumber " +
                "FROM Bills b " +
                "JOIN Patient p ON b.PatientId = p.PatientId " +
                "LEFT JOIN Lab l ON b.LabId = l.LabId " +
                "WHERE p.Email = ? ORDER BY b.BillId DESC";
        return query(sql, email);
    }

    private List<Bill> query(String sql, String param) throws SQLException {
        List<Bill> out = new ArrayList<>();
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, param);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) out.add(readJoined(rs));
            }
        }
        return out;
    }

    private Bill readJoined(ResultSet rs) throws SQLException {
        Bill b = new Bill();
        b.setBillId(rs.getInt("BillId"));
        b.setLabId(rs.getInt("LabId"));
        b.setPatientId(rs.getString("PatientId"));
        b.setPayableAmount(rs.getDouble("PayableAmount"));
        b.setTestType(rs.getString("TestType"));
        b.setCategory(rs.getString("Category"));
        b.setWeight(rs.getInt("Weight"));
        b.setHeight(rs.getInt("Height"));
        b.setMobileNumber(rs.getString("MobileNumber"));
        return b;
    }
}
