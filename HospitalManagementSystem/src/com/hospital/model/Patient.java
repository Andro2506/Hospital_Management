package com.hospital.model;

/**
 * POJO representing a Patient.
 *
 * <p>The Patient now also doubles as a self-service user (login). Patient
 * accounts may be created by an admin (no Username/Password) or by the
 * patient themselves through the public registration page (Username +
 * Password set). Either way the row lives in the same Patient table.
 *
 * Fields map to the Patient table:
 *   PatientId       - 7-digit registration number (PRN), also displayed as PRN
 *   PatientName     - max 50 chars
 *   Email
 *   Age
 *   BloodGroup
 *   PatientDOB      - stored as ISO date string (yyyy-MM-dd)
 *   Gender          - Male / Female / Other
 *   WardNumber
 *   DoctorId
 *   DoctorName
 *   Address         - max 100 chars
 *   ContactNo       - max 10 digits
 *   AadharNumber    - max 12 digits
 *   Username        - alphanumeric login, min 8 chars (nullable when admin-created)
 *   Password        - login password (nullable when admin-created)
 */
public class Patient {

    private String patientId;
    private String patientName;
    private String email;
    private int age;
    private String bloodGroup;
    private String patientDOB;
    private String gender;
    private String wardNumber;
    private String doctorId;
    private String doctorName;
    private String address;
    private String contactNo;
    private String aadharNumber;
    private String username;
    private String password;

    public Patient() { }

    public String getPatientId() { return patientId; }
    public void setPatientId(String patientId) { this.patientId = patientId; }

    public String getPatientName() { return patientName; }
    public void setPatientName(String patientName) { this.patientName = patientName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public int getAge() { return age; }
    public void setAge(int age) { this.age = age; }

    public String getBloodGroup() { return bloodGroup; }
    public void setBloodGroup(String bloodGroup) { this.bloodGroup = bloodGroup; }

    public String getPatientDOB() { return patientDOB; }
    public void setPatientDOB(String patientDOB) { this.patientDOB = patientDOB; }

    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }

    public String getWardNumber() { return wardNumber; }
    public void setWardNumber(String wardNumber) { this.wardNumber = wardNumber; }

    public String getDoctorId() { return doctorId; }
    public void setDoctorId(String doctorId) { this.doctorId = doctorId; }

    public String getDoctorName() { return doctorName; }
    public void setDoctorName(String doctorName) { this.doctorName = doctorName; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getContactNo() { return contactNo; }
    public void setContactNo(String contactNo) { this.contactNo = contactNo; }

    public String getAadharNumber() { return aadharNumber; }
    public void setAadharNumber(String aadharNumber) { this.aadharNumber = aadharNumber; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    @Override
    public String toString() {
        return "Patient{" + patientId + " - " + patientName + "}";
    }
}
