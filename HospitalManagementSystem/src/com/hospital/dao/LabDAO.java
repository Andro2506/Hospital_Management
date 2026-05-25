package com.hospital.dao;

import com.hospital.model.Lab;
import com.hospital.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/** Data access for Lab test records. */
public class LabDAO {

    /** Allowed test types per the user story (case-insensitive). */
    public static final String[] ALLOWED_TEST_TYPES = {"CBC", "BEL"};

    public static boolean isValidTestType(String testType) {
        if (testType == null) return false;
        for (String t : ALLOWED_TEST_TYPES) {
            if (t.equalsIgnoreCase(testType.trim())) return true;
        }
        return false;
    }

    /** Inserts and returns the generated LabId. */
    public int insert(Lab lab) throws SQLException {
        String sql = "INSERT INTO Lab (PatientId, TestType, Category, Weight, Height, MobileNumber) " +
                "VALUES (?,?,?,?,?,?)";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, lab.getPatientId());
            ps.setString(2, lab.getTestType());
            ps.setString(3, lab.getCategory());
            ps.setInt   (4, lab.getWeight());
            ps.setInt   (5, lab.getHeight());
            ps.setString(6, lab.getMobileNumber());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                int id = keys.next() ? keys.getInt(1) : -1;
                lab.setLabId(id);
                return id;
            }
        }
    }

    public List<Lab> findAll() throws SQLException {
        String sql = "SELECT LabId, PatientId, TestType, Category, Weight, Height, MobileNumber " +
                "FROM Lab ORDER BY LabId DESC";
        List<Lab> out = new ArrayList<>();
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Lab l = new Lab();
                l.setLabId(rs.getInt("LabId"));
                l.setPatientId(rs.getString("PatientId"));
                l.setTestType(rs.getString("TestType"));
                l.setCategory(rs.getString("Category"));
                l.setWeight(rs.getInt("Weight"));
                l.setHeight(rs.getInt("Height"));
                l.setMobileNumber(rs.getString("MobileNumber"));
                out.add(l);
            }
        }
        return out;
    }
}
