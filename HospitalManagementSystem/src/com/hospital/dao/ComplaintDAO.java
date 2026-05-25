package com.hospital.dao;

import com.hospital.model.Complaint;
import com.hospital.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/** Data access for Complaints. */
public class ComplaintDAO {

    public int insert(Complaint c) throws SQLException {
        String sql = "INSERT INTO Complaint (VisitorId, Issue) VALUES (?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, c.getVisitorId());
            ps.setString(2, c.getIssue());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                int id = keys.next() ? keys.getInt(1) : -1;
                c.setComplaintId(id);
                return id;
            }
        }
    }

    public List<Complaint> findByVisitor(int visitorId) throws SQLException {
        String sql = "SELECT ComplaintId, VisitorId, Issue FROM Complaint WHERE VisitorId=? " +
                "ORDER BY ComplaintId DESC";
        List<Complaint> out = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, visitorId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Complaint c = new Complaint();
                    c.setComplaintId(rs.getInt("ComplaintId"));
                    c.setVisitorId(rs.getInt("VisitorId"));
                    c.setIssue(rs.getString("Issue"));
                    out.add(c);
                }
            }
        }
        return out;
    }
}
