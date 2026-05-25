package com.hospital.dao;

import com.hospital.model.Visitor;
import com.hospital.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/** Data access for Visitor accounts. */
public class VisitorDAO {

    public boolean usernameExists(String username) throws SQLException {
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(
                     "SELECT 1 FROM Visitor WHERE Username=?")) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    public int register(Visitor v) throws SQLException {
        String sql = "INSERT INTO Visitor (Username, FirstName, LastName, Password, Email, " +
                "MobileNumber, Gender, City) VALUES (?,?,?,?,?,?,?,?)";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, v.getUsername());
            ps.setString(2, v.getFirstName());
            ps.setString(3, v.getLastName());
            ps.setString(4, v.getPassword());
            ps.setString(5, v.getEmail());
            ps.setString(6, v.getMobileNumber());
            ps.setString(7, v.getGender());
            ps.setString(8, v.getCity());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                return keys.next() ? keys.getInt(1) : -1;
            }
        }
    }

    public Visitor authenticate(String username, String password) throws SQLException {
        String sql = "SELECT VisitorId, Username, FirstName, LastName, Password, Email, " +
                "MobileNumber, Gender, City FROM Visitor WHERE Username=? AND Password=?";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, username);
            ps.setString(2, password);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Visitor v = new Visitor();
                    v.setVisitorId(rs.getInt("VisitorId"));
                    v.setUsername(rs.getString("Username"));
                    v.setFirstName(rs.getString("FirstName"));
                    v.setLastName(rs.getString("LastName"));
                    v.setEmail(rs.getString("Email"));
                    v.setMobileNumber(rs.getString("MobileNumber"));
                    v.setGender(rs.getString("Gender"));
                    v.setCity(rs.getString("City"));
                    v.setPassword(""); // do not retain in session
                    return v;
                }
            }
        }
        return null;
    }
}
