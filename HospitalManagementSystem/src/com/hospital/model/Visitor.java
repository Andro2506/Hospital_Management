package com.hospital.model;

/** POJO representing a Visitor user. */
public class Visitor {

    private int visitorId;
    private String username;
    private String firstName;
    private String lastName;
    private String password;
    private String email;
    private String mobileNumber;
    private String gender;
    private String city;

    public Visitor() { }

    public int getVisitorId() { return visitorId; }
    public void setVisitorId(int visitorId) { this.visitorId = visitorId; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getMobileNumber() { return mobileNumber; }
    public void setMobileNumber(String mobileNumber) { this.mobileNumber = mobileNumber; }

    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }
}
