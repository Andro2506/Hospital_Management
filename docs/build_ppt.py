#!/usr/bin/env python3
"""
Build a self-contained .pptx file describing the Hospital Management System
project, without any external dependencies (no python-pptx, no LibreOffice).

A .pptx is just a ZIP of XML files following the Office Open XML schema.
This script writes the minimum set of parts needed for Microsoft PowerPoint,
LibreOffice Impress, and Google Slides to render the deck:

    [Content_Types].xml
    _rels/.rels
    ppt/presentation.xml
    ppt/_rels/presentation.xml.rels
    ppt/theme/theme1.xml
    ppt/slideMasters/slideMaster1.xml
    ppt/slideMasters/_rels/slideMaster1.xml.rels
    ppt/slideLayouts/slideLayout1.xml
    ppt/slideLayouts/_rels/slideLayout1.xml.rels
    ppt/slides/slideN.xml          (one per slide)
    ppt/slides/_rels/slideN.xml.rels

Run from the repo root:

    python3 docs/build_ppt.py

Produces docs/HospitalManagementSystem.pptx
"""
from __future__ import annotations

import os
import sys
import zipfile
from xml.sax.saxutils import escape as xml_escape

# ---------------------------------------------------------------------------
# Slide content
# ---------------------------------------------------------------------------
# Each slide is a dict with a title and a list of bullet strings.
# Bullets that start with two spaces become indented sub-bullets.

SLIDES = [
    {
        "title": "Hospital Management System",
        "subtitle": (
            "Java EE web application using Servlets + JSP and SQLite\n"
            "Project documentation and walkthrough"
        ),
        "kind": "title",
    },
    {
        "title": "Project Overview",
        "bullets": [
            "Web-based hospital management system for two roles: Admin and Patient.",
            "Admin manages patient records, lab tests, bills and consultations.",
            "Patients self-register, log in, view and edit their own details, see lab tests and bills, and request doctor consultations.",
            "Built as a plain Java EE Dynamic Web Project on Apache Tomcat.",
            "Persistence via SQLite (single hospital.db file, JDBC driver).",
            "Frontend: server-rendered JSP pages with vanilla HTML/CSS/JavaScript.",
        ],
    },
    {
        "title": "Tech Stack",
        "bullets": [
            "Server: Apache Tomcat 9.0.71 (Servlet 3.1, JSP 2.3).",
            "Language: Java 8+.",
            "Database: SQLite via sqlite-jdbc-3.27.2.jar.",
            "Frontend: HTML, CSS (light-blue medical theme), vanilla JavaScript.",
            "No Spring, no Hibernate, no Maven build required.",
            "Tested by compiling all sources against a minimal javax.servlet stub (34 classes).",
        ],
    },
    {
        "title": "Folder Structure",
        "bullets": [
            "HospitalManagementSystem/ - top-level project root.",
            "  src/com/hospital/model/ - POJOs (Patient, Admin, Lab, Bill, Consultation).",
            "  src/com/hospital/dao/ - JDBC DAOs and PatientCollection (in-memory).",
            "  src/com/hospital/servlet/ - all servlets (admin + patient + shared).",
            "  src/com/hospital/util/ - DBConnection, DBInitListener, ValidationUtil.",
            "  WebContent/WEB-INF/web.xml + lib/sqlite-jdbc-3.27.2.jar.",
            "  WebContent/jsp/, css/, js/, index.jsp.",
            "  hospital.db is auto-created next to the deployed app.",
        ],
    },
    {
        "title": "Database Schema",
        "bullets": [
            "Patient (PRN PK, PatientName, Email, Age, BloodGroup, DOB, Gender, Ward, DoctorId, DoctorName, Address, Contact, Aadhar, Username, Password).",
            "Admin (AdminId PK, Username UNIQUE, Password).",
            "Lab (LabId PK auto, PatientId FK, TestType, Category, Weight, Height, Mobile).",
            "Bills (BillId PK auto, LabId FK, PatientId FK, PayableAmount).",
            "Consultation (ConsultationId PK auto, PatientId FK, DoctorName, Department, PreferredDate, Notes, Status).",
            "DBConnection auto-creates tables on startup and seeds an admin + 10 demo patients (3 with login credentials).",
            "Idempotent migrations: ALTER TABLE adds Username/Password if missing on older DBs.",
        ],
    },
    {
        "title": "User Roles",
        "bullets": [
            "Admin - hospital staff. Full control over patient records, lab tests, bills, and consultation workflow.",
            "Patient - self-service user. Sees only their own data and can request doctor consultations.",
            "Authentication is session-based; SessionGuard helper redirects unauthenticated users to the right login screen.",
            "Admin and Patient sessions live under separate session attributes (admin / patient).",
            "Logout invalidates the session and returns to the home page.",
        ],
    },
    {
        "title": "Menu and Navigation",
        "bullets": [
            "Public (index.jsp): Admin Login, Patient Login, Patient Register.",
            "Admin Dashboard tiles: Add / View / Search / Edit / Delete Patient, Delete by Mobile, Register Lab Test, View Lab Tests, View Bills, Search Bill, View Consultations, Logout.",
            "Patient Dashboard tiles: View My Details, Edit My Details, My Lab Tests, Register Consultation, My Bills, Logout.",
            "Header is a shared JSP include (jsp/includes/header.jsp) that renders role-aware navigation.",
            "Footer (jsp/includes/footer.jsp) loads validation.js for client-side form checks.",
        ],
    },
    {
        "title": "Admin Login",
        "bullets": [
            "JSP: jsp/login.jsp - Servlet: LoginServlet at /login.",
            "Username rule: alphanumeric, minimum 8 characters.",
            "Password rule: minimum 10 characters with at least one uppercase, one digit, and one special character.",
            "Both client-side (validation.js) and server-side (ValidationUtil) checks.",
            "Authenticated admin is stored in HttpSession under the 'admin' attribute.",
            "Default seeded credentials: admin001 / Admin@123.",
        ],
    },
    {
        "title": "Admin Home (Dashboard)",
        "bullets": [
            "JSP: jsp/adminDashboard.jsp.",
            "Tile-based menu giving one-click access to each admin function.",
            "Greets the admin by username pulled from the session.",
            "Tiles: Add Patient, View Patients, Search Patient, Edit Patient, Delete Patient, Delete by Mobile.",
            "Tiles: Register Lab Test, View Lab Tests, View Bills, Search Bill, View Consultations.",
            "Tile: Logout - invalidates session and returns home.",
        ],
    },
    {
        "title": "Admin: Patient Management (CRUD)",
        "bullets": [
            "Add Patient (/addPatient): captures all fields with full server-side validation; mirrors the row into PatientCollection.",
            "View Patients (/viewPatients): paginated list, 5 per page, with Edit and Delete actions per row.",
            "Search Patient (/searchPatient): lookup by 7-digit PRN, shows full record.",
            "Edit Patient (/editPatient): loads existing record then updates; PRN stays read-only.",
            "Delete Patient (/deletePatient): confirms record before deletion.",
            "Delete by Mobile (/deleteByMobile): bulk delete by 10-digit contact number.",
        ],
    },
    {
        "title": "Admin: Lab Tests and Billing",
        "bullets": [
            "Register Lab Test (/registerLabTest): admin books CBC or BEL test for a patient.",
            "Validates the patient PRN exists, then writes one row in Lab and one in Bills (Bill extends Lab).",
            "Acknowledgment message: 'Patient Registration for Lab tests is Successful' with auto-generated Lab Id, Bill Id and payable amount.",
            "View Lab Tests (/viewLabTests): all lab tests across patients.",
            "View Bills (/viewBills): all bills with running total payable.",
        ],
    },
    {
        "title": "Search Bill (Polymorphism)",
        "bullets": [
            "JSP: jsp/searchBill.jsp - Servlet: SearchBillServlet at /searchBill.",
            "Admin chooses to search by Email or by Patient PRN (radio button).",
            "BillDAO exposes searchByEmail(String) and searchByPatientId(String) plus a polymorphic search(String) that picks the strategy based on input.",
            "Result page shows the patient details, every matching bill, and the total payable amount.",
            "Demonstrates the polymorphism user story (US005-Polymorphism) cleanly.",
        ],
    },
    {
        "title": "Admin: View Consultations",
        "bullets": [
            "JSP: jsp/viewConsultations.jsp - Servlet: ViewConsultationsServlet at /viewConsultations.",
            "Lists every consultation request from every patient, joined with the patient's name.",
            "Each row shows Consultation Id, Patient (name + PRN), Department, Doctor, Preferred Date, Status, Notes.",
            "Inline status dropdown lets admin move requests through PENDING -> CONFIRMED -> COMPLETED.",
            "Updates use POST + redirect-with-flash for clean refresh.",
            "ConsultationDAO.isValidStatus guards against invalid status values.",
        ],
    },
    {
        "title": "Patient Registration",
        "bullets": [
            "JSP: jsp/patientRegister.jsp - Servlet: PatientRegisterServlet at /patientRegister.",
            "Two sections: Login Details (username, password, confirm password) and Patient Details (full personal info).",
            "Client-side: validation.js checks formats and password match.",
            "Server-side: rejects duplicate username or duplicate PRN with friendly messages.",
            "On success, the patient can log in immediately with the chosen username.",
            "Public link from the home page; no login required to register.",
        ],
    },
    {
        "title": "Patient Login",
        "bullets": [
            "JSP: jsp/patientLogin.jsp - Servlet: PatientLoginServlet at /patientLogin.",
            "Same UID and password rules as admin login (alphanumeric 8+, password 10+ with upper/digit/special).",
            "On success, the full Patient object (without password) is stored in HttpSession under 'patient'.",
            "On failure, friendly error remains on the same page.",
            "Three demo patients are seeded for testing: patient001, patient002, patient003 (all Patient@123).",
        ],
    },
    {
        "title": "Patient Dashboard",
        "bullets": [
            "JSP: jsp/patientDashboard.jsp.",
            "Greets the patient by name and shows their PRN.",
            "Tile menu: View My Details, Edit My Details, My Lab Tests, Register Consultation, My Bills, Logout.",
            "Each tile links to a screen scoped to the logged-in patient only.",
            "Patient sessions are isolated from admin sessions and use a separate /patientLogout endpoint.",
        ],
    },
    {
        "title": "Patient: View / Edit My Details",
        "bullets": [
            "JSP: jsp/myProfile.jsp - Servlet: PatientProfileServlet at /myProfile and /myProfile/edit.",
            "View mode: read-only profile cards showing PRN, username, name, age, gender, blood group, DOB, contact, Aadhar, ward, doctor, address.",
            "Edit mode: patient can update name, age, gender, DOB, blood group, address, contact and Aadhar.",
            "PRN, Username, Ward and Doctor remain read-only - only hospital staff can change those.",
            "PatientDAO.selfUpdate enforces the field allow-list at the data layer too.",
        ],
    },
    {
        "title": "Patient: My Lab Tests and My Bills",
        "bullets": [
            "My Lab Tests (/myLabTests): lists every lab test booked under the logged-in PRN.",
            "Backed by LabDAO.findByPatient(patientId).",
            "My Bills (/myBills): lists every bill against the logged-in PRN with a running total payable.",
            "Reuses BillDAO.searchByPatientId(patientId) so the polymorphic search code is shared.",
            "Both pages forward to error.jsp if anything goes wrong, never crashing the server.",
        ],
    },
    {
        "title": "Patient: Register for Doctor Consultation",
        "bullets": [
            "JSP: jsp/registerConsultation.jsp - Servlet: RegisterConsultationServlet at /registerConsultation.",
            "Form fields: Department (dropdown), Doctor Name, Preferred Date (must be today or later), Notes.",
            "Server-side validation rejects past dates and overly long notes.",
            "On submit, a Consultation row is inserted with Status = PENDING.",
            "The same screen lists the patient's previous requests with current status.",
            "Admin sees the request immediately on the View Consultations screen.",
        ],
    },
    {
        "title": "OOP Concepts Demonstrated",
        "bullets": [
            "Inheritance: Bill extends Lab. RegisterLabTestServlet writes Lab and Bills rows together.",
            "Polymorphism: BillDAO.search(key) auto-detects email vs. PRN; explicit overloads searchByEmail / searchByPatientId.",
            "Encapsulation: clean POJOs in com.hospital.model with private fields and accessors.",
            "Collection: PatientCollection (singleton ArrayList) implements US001-Collection - add, removeById, findById, totalCount, viewAll.",
            "Exception handling: every servlet catches IllegalArgumentException for friendly errors and forwards to error.jsp on unexpected failures.",
        ],
    },
    {
        "title": "Validation Rules",
        "bullets": [
            "User ID: alphanumeric, minimum 8 characters.",
            "Password: minimum 10 characters, at least one uppercase, one digit, one special character.",
            "PRN / RegistrationId: exactly 7 digits.",
            "Contact / Mobile: exactly 10 digits.",
            "Aadhar: exactly 12 digits.",
            "Email: standard email pattern.",
            "Mirrored in validation.js (client) and ValidationUtil (server) - server is the source of truth.",
        ],
    },
    {
        "title": "Setup and Default Credentials",
        "bullets": [
            "Drop sqlite-jdbc-3.27.2.jar into HospitalManagementSystem/WebContent/WEB-INF/lib/ (helper script: download-deps.sh).",
            "Import as a Dynamic Web Project, target Tomcat 9.0.71, Run on Server.",
            "On first request, DBInitListener creates hospital.db, all tables, and seeds demo data.",
            "Admin login: admin001 / Admin@123.",
            "Patient logins: patient001, patient002, patient003 - all with password Patient@123.",
            "Self-registered patients pick their own username and password.",
        ],
    },
    {
        "title": "Conclusion and Future Enhancements",
        "bullets": [
            "Project covers all originally-specified user stories plus admin + patient self-service flows added later.",
            "Strong separation of concerns: servlets handle HTTP, DAOs handle SQL, JSPs handle rendering.",
            "Possible extensions: doctor accounts and dashboards, prescription module, appointment scheduling with time slots, payment gateway integration, REST API + SPA frontend.",
            "Current code is intentionally framework-free so it remains a clear teaching example of plain Servlets + JSP + JDBC.",
            "Thank you!",
        ],
    },
]

# ---------------------------------------------------------------------------
# OOXML helpers
# ---------------------------------------------------------------------------

NSMAP_PRS = (
    'xmlns:a="http://schemas.openxmlformats.org/drawingml/2006/main" '
    'xmlns:r="http://schemas.openxmlformats.org/officeDocument/2006/relationships" '
    'xmlns:p="http://schemas.openxmlformats.org/presentationml/2006/main"'
)


def _runs_for_paragraph(text: str) -> str:
    """Return a single <a:r> run with <a:rPr> for one paragraph of body text."""
    return (
        '<a:r><a:rPr lang="en-US" dirty="0"/>'
        f"<a:t>{xml_escape(text)}</a:t></a:r>"
    )


def _bullet_paragraph(text: str) -> str:
    """One bullet. Indentation level is 0 unless text starts with two spaces."""
    level = 1 if text.startswith("  ") else 0
    body = text.lstrip()
    return (
        f'<a:p><a:pPr lvl="{level}"/>'
        + _runs_for_paragraph(body)
        + "</a:p>"
    )


def title_slide_xml(title: str, subtitle: str) -> str:
    title_paras = "".join(
        f"<a:p>{_runs_for_paragraph(line)}</a:p>"
        for line in title.split("\n")
    )
    sub_paras = "".join(
        f"<a:p>{_runs_for_paragraph(line)}</a:p>"
        for line in subtitle.split("\n")
    )
    # Title placeholder spans roughly the upper half of the slide; subtitle below.
    return (
        f'<?xml version="1.0" encoding="UTF-8" standalone="yes"?>'
        f'<p:sld {NSMAP_PRS}>'
        '<p:cSld><p:spTree>'
        '<p:nvGrpSpPr>'
        '<p:cNvPr id="1" name=""/><p:cNvGrpSpPr/><p:nvPr/>'
        '</p:nvGrpSpPr>'
        '<p:grpSpPr><a:xfrm>'
        '<a:off x="0" y="0"/><a:ext cx="0" cy="0"/>'
        '<a:chOff x="0" y="0"/><a:chExt cx="0" cy="0"/>'
        '</a:xfrm></p:grpSpPr>'
        # Title shape
        '<p:sp>'
        '<p:nvSpPr><p:cNvPr id="2" name="Title"/>'
        '<p:cNvSpPr><a:spLocks noGrp="1"/></p:cNvSpPr>'
        '<p:nvPr><p:ph type="ctrTitle"/></p:nvPr></p:nvSpPr>'
        '<p:spPr><a:xfrm>'
        '<a:off x="685800" y="1828800"/><a:ext cx="10820400" cy="1600200"/>'
        '</a:xfrm></p:spPr>'
        f"<p:txBody><a:bodyPr anchor=\"b\"/><a:lstStyle/>{title_paras}</p:txBody>"
        '</p:sp>'
        # Subtitle shape
        '<p:sp>'
        '<p:nvSpPr><p:cNvPr id="3" name="Subtitle"/>'
        '<p:cNvSpPr><a:spLocks noGrp="1"/></p:cNvSpPr>'
        '<p:nvPr><p:ph type="subTitle" idx="1"/></p:nvPr></p:nvSpPr>'
        '<p:spPr><a:xfrm>'
        '<a:off x="685800" y="3657600"/><a:ext cx="10820400" cy="1600200"/>'
        '</a:xfrm></p:spPr>'
        f"<p:txBody><a:bodyPr/><a:lstStyle/>{sub_paras}</p:txBody>"
        '</p:sp>'
        '</p:spTree></p:cSld>'
        '<p:clrMapOvr><a:masterClrMapping/></p:clrMapOvr>'
        '</p:sld>'
    )


def content_slide_xml(title: str, bullets: list[str]) -> str:
    title_runs = _runs_for_paragraph(title)
    body_paragraphs = "".join(_bullet_paragraph(b) for b in bullets)
    return (
        f'<?xml version="1.0" encoding="UTF-8" standalone="yes"?>'
        f'<p:sld {NSMAP_PRS}>'
        '<p:cSld><p:spTree>'
        '<p:nvGrpSpPr>'
        '<p:cNvPr id="1" name=""/><p:cNvGrpSpPr/><p:nvPr/>'
        '</p:nvGrpSpPr>'
        '<p:grpSpPr><a:xfrm>'
        '<a:off x="0" y="0"/><a:ext cx="0" cy="0"/>'
        '<a:chOff x="0" y="0"/><a:chExt cx="0" cy="0"/>'
        '</a:xfrm></p:grpSpPr>'
        # Title
        '<p:sp>'
        '<p:nvSpPr><p:cNvPr id="2" name="Title"/>'
        '<p:cNvSpPr><a:spLocks noGrp="1"/></p:cNvSpPr>'
        '<p:nvPr><p:ph type="title"/></p:nvPr></p:nvSpPr>'
        '<p:spPr><a:xfrm>'
        '<a:off x="685800" y="457200"/><a:ext cx="10820400" cy="990600"/>'
        '</a:xfrm></p:spPr>'
        f"<p:txBody><a:bodyPr/><a:lstStyle/><a:p>{title_runs}</a:p></p:txBody>"
        '</p:sp>'
        # Body content placeholder
        '<p:sp>'
        '<p:nvSpPr><p:cNvPr id="3" name="Content"/>'
        '<p:cNvSpPr><a:spLocks noGrp="1"/></p:cNvSpPr>'
        '<p:nvPr><p:ph idx="1"/></p:nvPr></p:nvSpPr>'
        '<p:spPr><a:xfrm>'
        '<a:off x="685800" y="1600200"/><a:ext cx="10820400" cy="5029200"/>'
        '</a:xfrm></p:spPr>'
        f"<p:txBody><a:bodyPr/><a:lstStyle/>{body_paragraphs}</p:txBody>"
        '</p:sp>'
        '</p:spTree></p:cSld>'
        '<p:clrMapOvr><a:masterClrMapping/></p:clrMapOvr>'
        '</p:sld>'
    )


# ---------------------------------------------------------------------------
# Static parts: theme, slide master, slide layout, presentation envelope
# ---------------------------------------------------------------------------

THEME_XML = '''<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<a:theme xmlns:a="http://schemas.openxmlformats.org/drawingml/2006/main" name="HMS">
  <a:themeElements>
    <a:clrScheme name="HMS">
      <a:dk1><a:sysClr val="windowText" lastClr="000000"/></a:dk1>
      <a:lt1><a:sysClr val="window" lastClr="FFFFFF"/></a:lt1>
      <a:dk2><a:srgbClr val="0A558C"/></a:dk2>
      <a:lt2><a:srgbClr val="E7F1FB"/></a:lt2>
      <a:accent1><a:srgbClr val="1976D2"/></a:accent1>
      <a:accent2><a:srgbClr val="0A558C"/></a:accent2>
      <a:accent3><a:srgbClr val="2EA7DF"/></a:accent3>
      <a:accent4><a:srgbClr val="68B0AB"/></a:accent4>
      <a:accent5><a:srgbClr val="C0392B"/></a:accent5>
      <a:accent6><a:srgbClr val="557A93"/></a:accent6>
      <a:hlink><a:srgbClr val="1976D2"/></a:hlink>
      <a:folHlink><a:srgbClr val="0A558C"/></a:folHlink>
    </a:clrScheme>
    <a:fontScheme name="HMS">
      <a:majorFont>
        <a:latin typeface="Calibri"/>
        <a:ea typeface=""/>
        <a:cs typeface=""/>
      </a:majorFont>
      <a:minorFont>
        <a:latin typeface="Calibri"/>
        <a:ea typeface=""/>
        <a:cs typeface=""/>
      </a:minorFont>
    </a:fontScheme>
    <a:fmtScheme name="HMS">
      <a:fillStyleLst>
        <a:solidFill><a:schemeClr val="phClr"/></a:solidFill>
        <a:solidFill><a:schemeClr val="phClr"/></a:solidFill>
        <a:solidFill><a:schemeClr val="phClr"/></a:solidFill>
      </a:fillStyleLst>
      <a:lnStyleLst>
        <a:ln w="9525" cap="flat" cmpd="sng" algn="ctr">
          <a:solidFill><a:schemeClr val="phClr"/></a:solidFill>
        </a:ln>
        <a:ln w="9525" cap="flat" cmpd="sng" algn="ctr">
          <a:solidFill><a:schemeClr val="phClr"/></a:solidFill>
        </a:ln>
        <a:ln w="9525" cap="flat" cmpd="sng" algn="ctr">
          <a:solidFill><a:schemeClr val="phClr"/></a:solidFill>
        </a:ln>
      </a:lnStyleLst>
      <a:effectStyleLst>
        <a:effectStyle><a:effectLst/></a:effectStyle>
        <a:effectStyle><a:effectLst/></a:effectStyle>
        <a:effectStyle><a:effectLst/></a:effectStyle>
      </a:effectStyleLst>
      <a:bgFillStyleLst>
        <a:solidFill><a:schemeClr val="phClr"/></a:solidFill>
        <a:solidFill><a:schemeClr val="phClr"/></a:solidFill>
        <a:solidFill><a:schemeClr val="phClr"/></a:solidFill>
      </a:bgFillStyleLst>
    </a:fmtScheme>
  </a:themeElements>
</a:theme>
'''

SLIDE_MASTER_XML = '''<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<p:sldMaster xmlns:a="http://schemas.openxmlformats.org/drawingml/2006/main" xmlns:r="http://schemas.openxmlformats.org/officeDocument/2006/relationships" xmlns:p="http://schemas.openxmlformats.org/presentationml/2006/main">
  <p:cSld>
    <p:bg>
      <p:bgRef idx="1001"><a:schemeClr val="bg1"/></p:bgRef>
    </p:bg>
    <p:spTree>
      <p:nvGrpSpPr>
        <p:cNvPr id="1" name=""/><p:cNvGrpSpPr/><p:nvPr/>
      </p:nvGrpSpPr>
      <p:grpSpPr>
        <a:xfrm>
          <a:off x="0" y="0"/><a:ext cx="0" cy="0"/>
          <a:chOff x="0" y="0"/><a:chExt cx="0" cy="0"/>
        </a:xfrm>
      </p:grpSpPr>
      <p:sp>
        <p:nvSpPr><p:cNvPr id="2" name="Title Placeholder 1"/>
          <p:cNvSpPr><a:spLocks noGrp="1"/></p:cNvSpPr>
          <p:nvPr><p:ph type="title"/></p:nvPr></p:nvSpPr>
        <p:spPr><a:xfrm>
          <a:off x="685800" y="457200"/><a:ext cx="10820400" cy="990600"/>
        </a:xfrm></p:spPr>
        <p:txBody><a:bodyPr/><a:lstStyle/><a:p><a:r><a:rPr lang="en-US"/><a:t>Click to edit Master title style</a:t></a:r></a:p></p:txBody>
      </p:sp>
      <p:sp>
        <p:nvSpPr><p:cNvPr id="3" name="Text Placeholder 2"/>
          <p:cNvSpPr><a:spLocks noGrp="1"/></p:cNvSpPr>
          <p:nvPr><p:ph idx="1"/></p:nvPr></p:nvSpPr>
        <p:spPr><a:xfrm>
          <a:off x="685800" y="1600200"/><a:ext cx="10820400" cy="5029200"/>
        </a:xfrm></p:spPr>
        <p:txBody><a:bodyPr/><a:lstStyle/><a:p><a:r><a:rPr lang="en-US"/><a:t>Click to edit Master text styles</a:t></a:r></a:p></p:txBody>
      </p:sp>
    </p:spTree>
  </p:cSld>
  <p:clrMap bg1="lt1" tx1="dk1" bg2="lt2" tx2="dk2" accent1="accent1" accent2="accent2" accent3="accent3" accent4="accent4" accent5="accent5" accent6="accent6" hlink="hlink" folHlink="folHlink"/>
  <p:sldLayoutIdLst>
    <p:sldLayoutId id="2147483649" r:id="rId1"/>
  </p:sldLayoutIdLst>
  <p:txStyles>
    <p:titleStyle>
      <a:lvl1pPr algn="l">
        <a:defRPr sz="3600" b="1"><a:solidFill><a:schemeClr val="dk2"/></a:solidFill><a:latin typeface="+mj-lt"/></a:defRPr>
      </a:lvl1pPr>
    </p:titleStyle>
    <p:bodyStyle>
      <a:lvl1pPr marL="342900" indent="-342900">
        <a:buChar char="&#8226;"/>
        <a:defRPr sz="2000"><a:solidFill><a:schemeClr val="tx1"/></a:solidFill><a:latin typeface="+mn-lt"/></a:defRPr>
      </a:lvl1pPr>
      <a:lvl2pPr marL="685800" indent="-285750">
        <a:buChar char="&#9702;"/>
        <a:defRPr sz="1800"><a:solidFill><a:schemeClr val="tx1"/></a:solidFill><a:latin typeface="+mn-lt"/></a:defRPr>
      </a:lvl2pPr>
    </p:bodyStyle>
    <p:otherStyle/>
  </p:txStyles>
</p:sldMaster>
'''

SLIDE_LAYOUT_XML = '''<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<p:sldLayout xmlns:a="http://schemas.openxmlformats.org/drawingml/2006/main" xmlns:r="http://schemas.openxmlformats.org/officeDocument/2006/relationships" xmlns:p="http://schemas.openxmlformats.org/presentationml/2006/main" type="obj" preserve="1">
  <p:cSld name="Title and Content">
    <p:spTree>
      <p:nvGrpSpPr>
        <p:cNvPr id="1" name=""/><p:cNvGrpSpPr/><p:nvPr/>
      </p:nvGrpSpPr>
      <p:grpSpPr>
        <a:xfrm>
          <a:off x="0" y="0"/><a:ext cx="0" cy="0"/>
          <a:chOff x="0" y="0"/><a:chExt cx="0" cy="0"/>
        </a:xfrm>
      </p:grpSpPr>
      <p:sp>
        <p:nvSpPr><p:cNvPr id="2" name="Title 1"/>
          <p:cNvSpPr><a:spLocks noGrp="1"/></p:cNvSpPr>
          <p:nvPr><p:ph type="title"/></p:nvPr></p:nvSpPr>
        <p:spPr/>
        <p:txBody><a:bodyPr/><a:lstStyle/><a:p><a:endParaRPr lang="en-US"/></a:p></p:txBody>
      </p:sp>
      <p:sp>
        <p:nvSpPr><p:cNvPr id="3" name="Content Placeholder 2"/>
          <p:cNvSpPr><a:spLocks noGrp="1"/></p:cNvSpPr>
          <p:nvPr><p:ph idx="1"/></p:nvPr></p:nvSpPr>
        <p:spPr/>
        <p:txBody><a:bodyPr/><a:lstStyle/><a:p><a:endParaRPr lang="en-US"/></a:p></p:txBody>
      </p:sp>
    </p:spTree>
  </p:cSld>
  <p:clrMapOvr><a:masterClrMapping/></p:clrMapOvr>
</p:sldLayout>
'''


def presentation_xml(num_slides: int) -> str:
    sld_id_list = "".join(
        f'<p:sldId id="{256 + i}" r:id="rId{2 + i}"/>'
        for i in range(num_slides)
    )
    return (
        '<?xml version="1.0" encoding="UTF-8" standalone="yes"?>'
        '<p:presentation xmlns:a="http://schemas.openxmlformats.org/drawingml/2006/main" '
        'xmlns:r="http://schemas.openxmlformats.org/officeDocument/2006/relationships" '
        'xmlns:p="http://schemas.openxmlformats.org/presentationml/2006/main" saveSubsetFonts="1">'
        '<p:sldMasterIdLst><p:sldMasterId id="2147483648" r:id="rId1"/></p:sldMasterIdLst>'
        f'<p:sldIdLst>{sld_id_list}</p:sldIdLst>'
        '<p:sldSz cx="12192000" cy="6858000" type="screen16x9"/>'
        '<p:notesSz cx="6858000" cy="9144000"/>'
        '</p:presentation>'
    )


def presentation_rels_xml(num_slides: int) -> str:
    """Relationships file for the presentation: slide master + theme + each slide."""
    rels = [
        '<Relationship Id="rId1" Type="http://schemas.openxmlformats.org/officeDocument/2006/relationships/slideMaster" Target="slideMasters/slideMaster1.xml"/>',
    ]
    for i in range(num_slides):
        rels.append(
            f'<Relationship Id="rId{2 + i}" '
            'Type="http://schemas.openxmlformats.org/officeDocument/2006/relationships/slide" '
            f'Target="slides/slide{1 + i}.xml"/>'
        )
    rels.append(
        f'<Relationship Id="rId{2 + num_slides}" '
        'Type="http://schemas.openxmlformats.org/officeDocument/2006/relationships/theme" '
        'Target="theme/theme1.xml"/>'
    )
    return (
        '<?xml version="1.0" encoding="UTF-8" standalone="yes"?>'
        '<Relationships xmlns="http://schemas.openxmlformats.org/package/2006/relationships">'
        + "".join(rels)
        + "</Relationships>"
    )


SLIDE_RELS_XML = (
    '<?xml version="1.0" encoding="UTF-8" standalone="yes"?>'
    '<Relationships xmlns="http://schemas.openxmlformats.org/package/2006/relationships">'
    '<Relationship Id="rId1" '
    'Type="http://schemas.openxmlformats.org/officeDocument/2006/relationships/slideLayout" '
    'Target="../slideLayouts/slideLayout1.xml"/>'
    "</Relationships>"
)

SLIDE_MASTER_RELS_XML = (
    '<?xml version="1.0" encoding="UTF-8" standalone="yes"?>'
    '<Relationships xmlns="http://schemas.openxmlformats.org/package/2006/relationships">'
    '<Relationship Id="rId1" '
    'Type="http://schemas.openxmlformats.org/officeDocument/2006/relationships/slideLayout" '
    'Target="../slideLayouts/slideLayout1.xml"/>'
    '<Relationship Id="rId2" '
    'Type="http://schemas.openxmlformats.org/officeDocument/2006/relationships/theme" '
    'Target="../theme/theme1.xml"/>'
    "</Relationships>"
)

SLIDE_LAYOUT_RELS_XML = (
    '<?xml version="1.0" encoding="UTF-8" standalone="yes"?>'
    '<Relationships xmlns="http://schemas.openxmlformats.org/package/2006/relationships">'
    '<Relationship Id="rId1" '
    'Type="http://schemas.openxmlformats.org/officeDocument/2006/relationships/slideMaster" '
    'Target="../slideMasters/slideMaster1.xml"/>'
    "</Relationships>"
)

ROOT_RELS_XML = (
    '<?xml version="1.0" encoding="UTF-8" standalone="yes"?>'
    '<Relationships xmlns="http://schemas.openxmlformats.org/package/2006/relationships">'
    '<Relationship Id="rId1" '
    'Type="http://schemas.openxmlformats.org/officeDocument/2006/relationships/officeDocument" '
    'Target="ppt/presentation.xml"/>'
    "</Relationships>"
)


def content_types_xml(num_slides: int) -> str:
    overrides = [
        '<Override PartName="/ppt/presentation.xml" ContentType="application/vnd.openxmlformats-officedocument.presentationml.presentation.main+xml"/>',
        '<Override PartName="/ppt/slideMasters/slideMaster1.xml" ContentType="application/vnd.openxmlformats-officedocument.presentationml.slideMaster+xml"/>',
        '<Override PartName="/ppt/slideLayouts/slideLayout1.xml" ContentType="application/vnd.openxmlformats-officedocument.presentationml.slideLayout+xml"/>',
        '<Override PartName="/ppt/theme/theme1.xml" ContentType="application/vnd.openxmlformats-officedocument.theme+xml"/>',
    ]
    for i in range(num_slides):
        overrides.append(
            f'<Override PartName="/ppt/slides/slide{1 + i}.xml" '
            'ContentType="application/vnd.openxmlformats-officedocument.presentationml.slide+xml"/>'
        )
    return (
        '<?xml version="1.0" encoding="UTF-8" standalone="yes"?>'
        '<Types xmlns="http://schemas.openxmlformats.org/package/2006/content-types">'
        '<Default Extension="rels" ContentType="application/vnd.openxmlformats-package.relationships+xml"/>'
        '<Default Extension="xml" ContentType="application/xml"/>'
        + "".join(overrides)
        + "</Types>"
    )


# ---------------------------------------------------------------------------
# Build
# ---------------------------------------------------------------------------

def build(target_path: str) -> None:
    num = len(SLIDES)
    os.makedirs(os.path.dirname(target_path), exist_ok=True)

    with zipfile.ZipFile(target_path, "w", zipfile.ZIP_DEFLATED) as z:
        # Top-level
        z.writestr("[Content_Types].xml", content_types_xml(num))
        z.writestr("_rels/.rels", ROOT_RELS_XML)

        # Presentation
        z.writestr("ppt/presentation.xml", presentation_xml(num))
        z.writestr("ppt/_rels/presentation.xml.rels", presentation_rels_xml(num))

        # Theme + master + layout
        z.writestr("ppt/theme/theme1.xml", THEME_XML)
        z.writestr("ppt/slideMasters/slideMaster1.xml", SLIDE_MASTER_XML)
        z.writestr("ppt/slideMasters/_rels/slideMaster1.xml.rels", SLIDE_MASTER_RELS_XML)
        z.writestr("ppt/slideLayouts/slideLayout1.xml", SLIDE_LAYOUT_XML)
        z.writestr("ppt/slideLayouts/_rels/slideLayout1.xml.rels", SLIDE_LAYOUT_RELS_XML)

        # Slides
        for i, slide in enumerate(SLIDES, start=1):
            if slide.get("kind") == "title":
                xml = title_slide_xml(slide["title"], slide["subtitle"])
            else:
                xml = content_slide_xml(slide["title"], slide["bullets"])
            z.writestr(f"ppt/slides/slide{i}.xml", xml)
            z.writestr(f"ppt/slides/_rels/slide{i}.xml.rels", SLIDE_RELS_XML)


def main() -> int:
    here = os.path.dirname(os.path.abspath(__file__))
    out = os.path.join(here, "HospitalManagementSystem.pptx")
    build(out)
    print(f"Wrote {out} ({len(SLIDES)} slides)")
    return 0


if __name__ == "__main__":
    sys.exit(main())
