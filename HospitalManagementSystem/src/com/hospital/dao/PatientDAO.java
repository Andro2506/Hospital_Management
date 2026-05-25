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
            "WardNumber, DoctorId, DoctorName, Address, ContactNo, AadharNumber, " +
            "Username, Password";

    /** Insert a new patient row (admin-created or self-registered). */
    public boolean insert(Patient p) throws SQLException {
        String sql = "INSERT INTO Patient (" + COLS + ") VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            bind(ps, p);
            return ps.executeUpdate() == 1;
        }
    }

    /**
     * Admin update path: refreshes every editable column except the PRN,
     * Username and Password. Useful when an admin is editing an admin-created
     * patient and is not touching the login fields.
     */
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

    /**
     * Patient self-update path: a logged-in patient may edit their personal
     * details, but PRN, Username, Password, Ward, DoctorId and DoctorName are
     * managed by hospital staff and cannot be changed here.
     */
    public boolean selfUpdate(Patient p) throws SQLException {
        String sql = "UPDATE Patient SET PatientName=?, Email=?, Age=?, BloodGroup=?, " +
                "PatientDOB=?, Gender=?, Address=?, ContactNo=?, AadharNumber=? " +
                "WHERE PatientId=?";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, p.getPatientName());
            ps.setString(2, p.getEmail());
            ps.setInt   (3, p.getAge());
            ps.setString(4, p.getBloodGroup());
            ps.setString(5, p.getPatientDOB());
            ps.setString(6, p.getGender());
            ps.setString(7, p.getAddress());
            ps.setString(8, p.getContactNo());
            ps.setString(9, p.getAadharNumber());
            ps.setString(10, p.getPatientId());
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
        return querySingle("SELECT " + COLS + " FROM Patient WHERE PatientId=?", patientId);
    }

    public Patient findByEmail(String email) throws SQLException {
        return querySingle("SELECT " + COLS + " FROM Patient WHERE Email=?", email);
    }

    public Patient findByUsername(String username) throws SQLException {
        return querySingle("SELECT " + COLS + " FROM Patient WHERE Username=?", username);
    }

    public boolean usernameExists(String username) throws SQLException {
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement("SELECT 1 FROM Patient WHERE Username=?")) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) { return rs.next(); }
        }
    }

    public boolean patientIdExists(String patientId) throws SQLException {
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement("SELECT 1 FROM Patient WHERE PatientId=?")) {
            ps.setString(1, patientId);
            try (ResultSet rs = ps.executeQuery()) { return rs.next(); }
        }
    }

    /** @return matching patient (with password cleared) on success, otherwise null. */
    public Patient authenticate(String username, String password) throws SQLException {
        String sql = "SELECT " + COLS + " FROM Patient WHERE Username=? AND Password=?";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, username);
            ps.setString(2, password);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return null;
                Patient p = readRow(rs);
                p.setPassword(""); // do not retain credentials in session
                return p;
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

    private Patient querySingle(String sql, String param) throws SQLException {
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, param);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? readRow(rs) : null;
            }
        }
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
        ps.setString(14, p.getUsername());
        ps.setString(15, p.getPassword());
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
        p.setUsername(rs.getString("Username"));
        p.setPassword(rs.getString("Password"));
        return p;
    }
}
