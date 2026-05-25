package com.hospital.model;

/**
 * Lab test record (US004 - Inheritance: parent class for Bill).
 *
 * Allowed test types: CBC, BEL.
 */
public class Lab {

    private int labId;
    private String patientId;
    private String testType;
    private String category;
    private int weight;
    private int height;
    private String mobileNumber;

    public Lab() { }

    public Lab(int labId, String patientId, String testType, String category,
               int weight, int height, String mobileNumber) {
        this.labId = labId;
        this.patientId = patientId;
        this.testType = testType;
        this.category = category;
        this.weight = weight;
        this.height = height;
        this.mobileNumber = mobileNumber;
    }

    public int getLabId() { return labId; }
    public void setLabId(int labId) { this.labId = labId; }

    public String getPatientId() { return patientId; }
    public void setPatientId(String patientId) { this.patientId = patientId; }

    public String getTestType() { return testType; }
    public void setTestType(String testType) { this.testType = testType; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public int getWeight() { return weight; }
    public void setWeight(int weight) { this.weight = weight; }

    public int getHeight() { return height; }
    public void setHeight(int height) { this.height = height; }

    public String getMobileNumber() { return mobileNumber; }
    public void setMobileNumber(String mobileNumber) { this.mobileNumber = mobileNumber; }
}
