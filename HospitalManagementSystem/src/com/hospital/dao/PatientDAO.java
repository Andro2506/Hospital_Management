package com.hospital.dao;

import com.hospital.model.Patient;
import com.hospital.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/** Data access for Patient records. */
public class PatientDAO {

    private static final String COLS =
            "PatientId, PatientName, Email, Age, BloodGroup, PatientDOB, Gender, " +
            "WardNumber, DoctorId, DoctorName, Address, ContactNo, AadharNumber";

    public boolean insert(Patient p) throws SQLException {
        String sql = "INSERT INTO Patient (" + COLS + ") VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?)";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            bind(ps, p);
            return ps.executeUpdate() == 1;
        }
    }

    public boolean update(Patient p) throws SQLException {
        String sql = "UPDATE Patient SET PatientName=?, Email=?, Age=?, BloodGroup=?, " +
                "PatientDOB=?, Gender=?, WardNumber=?, DoctorId=?, DoctorName=?, " +
                "Address=?, ContactNo=?, AadharNumber=? WHERE PatientId=?";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, p.getPatientName());
            ps.setString(2, p.getEmail());
            ps.setInt   (3, p.getAge());
            ps.setString(4, p.getBloodGroup());
            ps.setString(5, p.getPatientDOB());
            ps.setString(6, p.getGender());
            ps.setString(7, p.getWardNumber());
            ps.setString(8, p.getDoctorId());
            ps.setString(9, p.getDoctorName());
            ps.setString(10, p.getAddress());
            ps.setString(11, p.getContactNo());
            ps.setString(12, p.getAadharNumber());
            ps.setString(13, p.getPatientId());
            return ps.executeUpdate() == 1;
        }
    }

    public boolean deleteById(String patientId) throws SQLException {
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement("DELETE FROM Patient WHERE PatientId=?")) {
            ps.setString(1, patientId);
            return ps.executeUpdate() == 1;
        }
    }

    public int deleteByContactNo(String contactNo) throws SQLException {
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement("DELETE FROM Patient WHERE ContactNo=?")) {
            ps.setString(1, contactNo);
            return ps.executeUpdate();
        }
    }

    public Patient findById(String patientId) throws SQLException {
        String sql = "SELECT " + COLS + " FROM Patient WHERE PatientId=?";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, patientId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? readRow(rs) : null;
            }
        }
    }

    public Patient findByEmail(String email) throws SQLException {
        String sql = "SELECT " + COLS + " FROM Patient WHERE Email=?";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? readRow(rs) : null;
            }
        }
    }

    public int countAll() throws SQLException {
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement("SELECT COUNT(*) FROM Patient");
             ResultSet rs = ps.executeQuery()) {
            return rs.next() ? rs.getInt(1) : 0;
        }
    }

    public List<Patient> findPage(int offset, int limit) throws SQLException {
        String sql = "SELECT " + COLS + " FROM Patient ORDER BY PatientId LIMIT ? OFFSET ?";
        List<Patient> out = new ArrayList<>();
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, limit);
            ps.setInt(2, offset);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) out.add(readRow(rs));
            }
        }
        return out;
    }

    public List<Patient> findAll() throws SQLException {
        String sql = "SELECT " + COLS + " FROM Patient ORDER BY PatientId";
        List<Patient> out = new ArrayList<>();
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) out.add(readRow(rs));
        }
        return out;
    }

    private void bind(PreparedStatement ps, Patient p) throws SQLException {
        ps.setString(1, p.getPatientId());
        ps.setString(2, p.getPatientName());
        ps.setString(3, p.getEmail());
        ps.setInt   (4, p.getAge());
        ps.setString(5, p.getBloodGroup());
        ps.setString(6, p.getPatientDOB());
        ps.setString(7, p.getGender());
        ps.setString(8, p.getWardNumber());
        ps.setString(9, p.getDoctorId());
        ps.setString(10, p.getDoctorName());
        ps.setString(11, p.getAddress());
        ps.setString(12, p.getContactNo());
        ps.setString(13, p.getAadharNumber());
    }

    private Patient readRow(ResultSet rs) throws SQLException {
        Patient p = new Patient();
        p.setPatientId(rs.getString("PatientId"));
        p.setPatientName(rs.getString("PatientName"));
        p.setEmail(rs.getString("Email"));
        p.setAge(rs.getInt("Age"));
        p.setBloodGroup(rs.getString("BloodGroup"));
        p.setPatientDOB(rs.getString("PatientDOB"));
        p.setGender(rs.getString("Gender"));
        p.setWardNumber(rs.getString("WardNumber"));
        p.setDoctorId(rs.getString("DoctorId"));
        p.setDoctorName(rs.getString("DoctorName"));
        p.setAddress(rs.getString("Address"));
        p.setContactNo(rs.getString("ContactNo"));
        p.setAadharNumber(rs.getString("AadharNumber"));
        return p;
    }
}
