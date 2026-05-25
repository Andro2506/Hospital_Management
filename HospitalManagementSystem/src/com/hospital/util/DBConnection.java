package com.hospital.util;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Singleton-style database helper for the Hospital Management System.
 *
 * <p>Responsibilities:
 * <ul>
 *   <li>Load the SQLite JDBC driver (org.sqlite.JDBC)</li>
 *   <li>Resolve the SQLite file path against the deployed web app
 *       ({@code getServletContext().getRealPath("/")}) when available; otherwise
 *       fall back to the working directory.</li>
 *   <li>Create all required tables on first connection ({@code CREATE TABLE IF NOT EXISTS}).</li>
 *   <li>Migrate older Patient schemas by adding the Username/Password columns
 *       when they are missing.</li>
 *   <li>Drop legacy Visitor / Complaint tables (replaced by Patient self-service).</li>
 *   <li>Seed demo data: 1 default admin and 10 demo patients on first run,
 *       and assign login credentials to the first three demo patients.</li>
 * </ul>
 */
public final class DBConnection {

    private static final String DB_FILE_NAME = "hospital.db";
    private static final String DRIVER = "org.sqlite.JDBC";

    /** Resolved JDBC URL once a base path has been configured. */
    private static volatile String jdbcUrl;
    /** True after schema/seed initialization has run. */
    private static volatile boolean initialized = false;

    private DBConnection() { /* no instances */ }

    public static synchronized void configure(String webAppRealPath) {
        String dir = (webAppRealPath != null && !webAppRealPath.isEmpty())
                ? webAppRealPath
                : System.getProperty("user.dir");
        File dbFile = new File(dir, DB_FILE_NAME);
        jdbcUrl = "jdbc:sqlite:" + dbFile.getAbsolutePath();
    }

    public static Connection getConnection() throws SQLException {
        if (jdbcUrl == null) configure(null);
        try {
            Class.forName(DRIVER);
        } catch (ClassNotFoundException e) {
            throw new SQLException("SQLite JDBC driver not found on classpath. "
                    + "Please drop sqlite-jdbc-3.27.2.jar into WEB-INF/lib/.", e);
        }
        Connection conn = DriverManager.getConnection(jdbcUrl);
        try (Statement s = conn.createStatement()) {
            s.execute("PRAGMA foreign_keys = ON");
        }
        return conn;
    }

    public static synchronized void initialize() throws SQLException {
        if (initialized) return;
        try (Connection conn = getConnection()) {
            createSchema(conn);
            migratePatientLoginColumns(conn);
            dropLegacyTables(conn);
            seedAdmin(conn);
            seedPatients(conn);
        }
        initialized = true;
    }

    private static void createSchema(Connection conn) throws SQLException {
        try (Statement s = conn.createStatement()) {
            s.executeUpdate(
                "CREATE TABLE IF NOT EXISTS Patient (" +
                "  PatientId TEXT PRIMARY KEY," +
                "  PatientName TEXT NOT NULL," +
                "  Email TEXT," +
                "  Age INTEGER," +
                "  BloodGroup TEXT," +
                "  PatientDOB TEXT," +
                "  Gender TEXT," +
                "  WardNumber TEXT," +
                "  DoctorId TEXT," +
                "  DoctorName TEXT," +
                "  Address TEXT," +
                "  ContactNo TEXT," +
                "  AadharNumber TEXT," +
                "  Username TEXT UNIQUE," +
                "  Password TEXT" +
                ")"
            );

            s.executeUpdate(
                "CREATE TABLE IF NOT EXISTS Admin (" +
                "  AdminId TEXT PRIMARY KEY," +
                "  Username TEXT NOT NULL UNIQUE," +
                "  Password TEXT NOT NULL" +
                ")"
            );

            s.executeUpdate(
                "CREATE TABLE IF NOT EXISTS Lab (" +
                "  LabId INTEGER PRIMARY KEY AUTOINCREMENT," +
                "  PatientId TEXT," +
                "  TestType TEXT," +
                "  Category TEXT," +
                "  Weight INTEGER," +
                "  Height INTEGER," +
                "  MobileNumber TEXT," +
                "  FOREIGN KEY(PatientId) REFERENCES Patient(PatientId)" +
                ")"
            );

            s.executeUpdate(
                "CREATE TABLE IF NOT EXISTS Bills (" +
                "  BillId INTEGER PRIMARY KEY AUTOINCREMENT," +
                "  LabId INTEGER," +
                "  PatientId TEXT," +
                "  PayableAmount REAL," +
                "  FOREIGN KEY(LabId) REFERENCES Lab(LabId)," +
                "  FOREIGN KEY(PatientId) REFERENCES Patient(PatientId)" +
                ")"
            );

            s.executeUpdate(
                "CREATE TABLE IF NOT EXISTS Consultation (" +
                "  ConsultationId INTEGER PRIMARY KEY AUTOINCREMENT," +
                "  PatientId TEXT NOT NULL," +
                "  DoctorName TEXT NOT NULL," +
                "  Department TEXT," +
                "  PreferredDate TEXT," +
                "  Notes TEXT," +
                "  Status TEXT NOT NULL DEFAULT 'PENDING'," +
                "  FOREIGN KEY(PatientId) REFERENCES Patient(PatientId)" +
                ")"
            );
        }
    }

    /**
     * Older Hospital_Management deployments did not include Username/Password
     * columns on the Patient table. Add them if missing so existing data is
     * preserved and the new self-service login can be enabled in place.
     */
    private static void migratePatientLoginColumns(Connection conn) throws SQLException {
        if (!columnExists(conn, "Patient", "Username")) {
            try (Statement s = conn.createStatement()) {
                s.executeUpdate("ALTER TABLE Patient ADD COLUMN Username TEXT");
            }
        }
        if (!columnExists(conn, "Patient", "Password")) {
            try (Statement s = conn.createStatement()) {
                s.executeUpdate("ALTER TABLE Patient ADD COLUMN Password TEXT");
            }
        }
    }

    private static boolean columnExists(Connection conn, String table, String column) throws SQLException {
        try (Statement s = conn.createStatement();
             ResultSet rs = s.executeQuery("PRAGMA table_info(" + table + ")")) {
            while (rs.next()) {
                if (column.equalsIgnoreCase(rs.getString("name"))) return true;
            }
        }
        return false;
    }

    /** Visitors / Complaints were replaced by patient self-service. */
    private static void dropLegacyTables(Connection conn) throws SQLException {
        try (Statement s = conn.createStatement()) {
            s.executeUpdate("DROP TABLE IF EXISTS Complaint");
            s.executeUpdate("DROP TABLE IF EXISTS Visitor");
        }
    }

    private static void seedAdmin(Connection conn) throws SQLException {
        try (PreparedStatement check = conn.prepareStatement(
                "SELECT COUNT(*) FROM Admin WHERE Username = ?")) {
            check.setString(1, "admin001");
            try (ResultSet rs = check.executeQuery()) {
                if (rs.next() && rs.getInt(1) > 0) return;
            }
        }
        try (PreparedStatement ins = conn.prepareStatement(
                "INSERT INTO Admin (AdminId, Username, Password) VALUES (?, ?, ?)")) {
            ins.setString(1, "ADM00001");
            ins.setString(2, "admin001");
            ins.setString(3, "Admin@123");
            ins.executeUpdate();
        }
    }

    private static void seedPatients(Connection conn) throws SQLException {
        // Each row: PRN, Name, Email, Age, BloodGroup, DOB, Gender, Ward,
        // DoctorId, DoctorName, Address, ContactNo, Aadhar, Username, Password.
        // The first three rows have login credentials for demo purposes.
        String[][] demo = {
            {"1000001", "Aarav Sharma",   "aarav@example.com",   "29", "B+",  "1995-04-12", "Male",   "W-101", "D-001", "Dr. Mehta",   "12 MG Road, Pune",       "9876500001", "100000000001", "patient001", "Patient@123"},
            {"1000002", "Diya Patel",     "diya@example.com",    "34", "O+",  "1990-09-23", "Female", "W-102", "D-002", "Dr. Iyer",    "34 Park Street, Mumbai", "9876500002", "100000000002", "patient002", "Patient@123"},
            {"1000003", "Rohan Verma",    "rohan@example.com",   "45", "A-",  "1979-01-05", "Male",   "W-103", "D-003", "Dr. Khan",    "5 Civil Lines, Delhi",   "9876500003", "100000000003", "patient003", "Patient@123"},
            {"1000004", "Isha Reddy",     "isha@example.com",    "22", "AB+", "2002-07-18", "Female", "W-104", "D-001", "Dr. Mehta",   "9 Banjara Hills, Hyd",   "9876500004", "100000000004", null,         null         },
            {"1000005", "Karan Singh",    "karan@example.com",   "51", "B-",  "1973-11-30", "Male",   "W-105", "D-002", "Dr. Iyer",    "21 Sector 17, Chd",      "9876500005", "100000000005", null,         null         },
            {"1000006", "Meera Nair",     "meera@example.com",   "28", "O-",  "1996-02-14", "Female", "W-106", "D-004", "Dr. Banerjee","8 Marine Drive, Mumbai", "9876500006", "100000000006", null,         null         },
            {"1000007", "Vikram Desai",   "vikram@example.com",  "39", "A+",  "1985-06-09", "Male",   "W-107", "D-003", "Dr. Khan",    "44 SG Highway, Ahd",     "9876500007", "100000000007", null,         null         },
            {"1000008", "Sneha Kapoor",   "sneha@example.com",   "31", "AB-", "1993-12-25", "Female", "W-108", "D-004", "Dr. Banerjee","19 Salt Lake, Kolkata",  "9876500008", "100000000008", null,         null         },
            {"1000009", "Arjun Joshi",    "arjun@example.com",   "26", "B+",  "1998-08-03", "Male",   "W-109", "D-001", "Dr. Mehta",   "77 FC Road, Pune",       "9876500009", "100000000009", null,         null         },
            {"1000010", "Priya Menon",    "priya@example.com",   "42", "O+",  "1982-05-21", "Female", "W-110", "D-002", "Dr. Iyer",    "3 MG Road, Bengaluru",   "9876500010", "100000000010", null,         null         }
        };

        try (PreparedStatement check = conn.prepareStatement(
                "SELECT COUNT(*) FROM Patient")) {
            try (ResultSet rs = check.executeQuery()) {
                if (rs.next() && rs.getInt(1) > 0) return;
            }
        }

        String sql = "INSERT INTO Patient (PatientId, PatientName, Email, Age, BloodGroup, PatientDOB, "
                   + "Gender, WardNumber, DoctorId, DoctorName, Address, ContactNo, AadharNumber, "
                   + "Username, Password) "
                   + "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            for (String[] r : demo) {
                ps.setString(1,  r[0]);
                ps.setString(2,  r[1]);
                ps.setString(3,  r[2]);
                ps.setInt   (4,  Integer.parseInt(r[3]));
                ps.setString(5,  r[4]);
                ps.setString(6,  r[5]);
                ps.setString(7,  r[6]);
                ps.setString(8,  r[7]);
                ps.setString(9,  r[8]);
                ps.setString(10, r[9]);
                ps.setString(11, r[10]);
                ps.setString(12, r[11]);
                ps.setString(13, r[12]);
                ps.setString(14, r[13]);
                ps.setString(15, r[14]);
                ps.addBatch();
            }
            ps.executeBatch();
        }
    }
}
