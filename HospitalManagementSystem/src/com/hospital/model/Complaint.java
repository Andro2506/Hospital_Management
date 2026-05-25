package com.hospital.model;

/** POJO representing a complaint registered by a visitor. */
public class Complaint {

    private int complaintId;
    private int visitorId;
    private String issue;
    /** Convenience field, populated by joins for display only. */
    private String visitorUsername;

    public Complaint() { }

    public int getComplaintId() { return complaintId; }
    public void setComplaintId(int complaintId) { this.complaintId = complaintId; }

    public int getVisitorId() { return visitorId; }
    public void setVisitorId(int visitorId) { this.visitorId = visitorId; }

    public String getIssue() { return issue; }
    public void setIssue(String issue) { this.issue = issue; }

    public String getVisitorUsername() { return visitorUsername; }
    public void setVisitorUsername(String visitorUsername) { this.visitorUsername = visitorUsername; }
}
