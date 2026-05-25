package com.hospital.model;

/**
 * Bill - inherits from Lab to satisfy the single-inheritance user story (US004).
 * Bill extends Lab and adds the payable amount along with its own primary key.
 */
public class Bill extends Lab {

    private int billId;
    private double payableAmount;

    public Bill() { super(); }

    public Bill(int billId, int labId, String patientId, String testType, String category,
                int weight, int height, String mobileNumber, double payableAmount) {
        super(labId, patientId, testType, category, weight, height, mobileNumber);
        this.billId = billId;
        this.payableAmount = payableAmount;
    }

    public int getBillId() { return billId; }
    public void setBillId(int billId) { this.billId = billId; }

    public double getPayableAmount() { return payableAmount; }
    public void setPayableAmount(double payableAmount) { this.payableAmount = payableAmount; }
}
