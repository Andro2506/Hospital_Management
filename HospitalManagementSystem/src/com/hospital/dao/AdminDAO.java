package com.hospital.dao;

import com.hospital.model.Admin;
import com.hospital.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/** Data access for Admin authentication. */
public class AdminDAO {

    /** @return matching admin (with password cleared) on success, otherwise null. */
    public Admin authenticate(String username, String password) throws SQLException {
        String sql = "SELECT AdminId, Username, Password FROM Admin WHERE Username=? AND Password=?";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, username);
            ps.setString(2, password);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Admin a = new Admin();
                    a.setAdminId(rs.getString("AdminId"));
                    a.setUsername(rs.getString("Username"));
                    a.setPassword(""); // do not retain in session
                    return a;
                }
            }
        }
        return null;
    }
}
