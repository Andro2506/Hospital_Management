# Hospital Management System

A complete Hospital Management System built as a Java EE web application using
plain Servlets and JSP, backed by SQLite. No Spring, no Maven.

## Tech Stack

| Layer        | Tech                                              |
|--------------|---------------------------------------------------|
| Server       | Apache Tomcat 9.0.71 (Servlet 3.1 / JSP 2.3)      |
| Language     | Java 8+                                           |
| Database     | SQLite (file-based)                               |
| JDBC driver  | `sqlite-jdbc-3.27.2.jar`                          |
| Frontend     | Plain HTML/CSS/JavaScript (vanilla)               |

## Repository Layout

```
HospitalManagementSystem/
├── src/com/hospital/
│   ├── model/           Patient, Admin, Visitor, Lab, Bill, Complaint
│   ├── dao/             PatientDAO, AdminDAO, VisitorDAO, LabDAO,
│   │                    BillDAO, ComplaintDAO, PatientCollection
│   ├── servlet/         All servlets (Login, Add/Edit/Delete/View/Search,
│   │                    Visitor Login/Register, Lab/Bill, Logout)
│   └── util/            DBConnection, DBInitListener, ValidationUtil
├── WebContent/
│   ├── WEB-INF/
│   │   ├── web.xml
│   │   └── lib/
│   │       └── sqlite-jdbc-3.27.2.jar    (placed by you - see below)
│   ├── jsp/             All JSP pages, plus jsp/includes/header.jsp + footer.jsp
│   ├── css/styles.css   Light-blue medical theme
│   ├── js/validation.js Client-side validation
│   └── index.jsp        Public landing page
├── download-deps.sh     Helper to fetch the SQLite JDBC jar
└── hospital.db          (auto-created next to the deployed app at runtime)
```

## Setup

### 1. Add the SQLite JDBC driver

Tomcat needs `sqlite-jdbc-3.27.2.jar` in `WEB-INF/lib/`. Either:

```bash
cd HospitalManagementSystem
./download-deps.sh
```

Or download it manually from
[Maven Central](https://repo1.maven.org/maven2/org/xerial/sqlite-jdbc/3.27.2/sqlite-jdbc-3.27.2.jar)
and drop the file into `HospitalManagementSystem/WebContent/WEB-INF/lib/`.

> The sandbox where this project was scaffolded had no public internet access, so
> the JAR is not bundled with the source. Once placed in `WEB-INF/lib/`, Tomcat
> picks it up automatically at startup.

### 2. Build and deploy

In Eclipse / IntelliJ as a "Dynamic Web Project" / Web Application module:
- Source folder: `src`
- Web content folder: `WebContent`
- Target runtime: Apache Tomcat 9.0.71
- Right-click the project &rarr; Run on Server.

Or build a WAR by hand:

```bash
# from inside HospitalManagementSystem/
mkdir -p build/WEB-INF/classes
javac -d build/WEB-INF/classes -cp "$CATALINA_HOME/lib/servlet-api.jar" \
      $(find src -name "*.java")
cp -r WebContent/WEB-INF/* build/WEB-INF/
cp -r WebContent/css WebContent/js WebContent/jsp WebContent/index.jsp build/
( cd build && jar -cvf ../hms.war . )
cp hms.war "$CATALINA_HOME/webapps/"
```

Open <http://localhost:8080/hms/> after Tomcat starts.

### 3. First-time data seeding

On first request the `DBInitListener` will:
- create `hospital.db` next to the deployed web app,
- create all 6 tables (`Patient`, `Admin`, `Visitor`, `Lab`, `Bills`, `Complaint`),
- insert the default admin: `admin` / `Admin@123`,
- insert 10 demo patient records (PRNs `1000001` ... `1000010`).

> **Login note:** the seeded admin username is `admin001` (8 chars) so the
> default credentials pass the validation rule (alphanumeric, min 8 chars).
> Use `admin001` / `Admin@123` for your first login.

## User Stories &rarr; Implementation Map

| Story | Servlet                          | JSP                  |
|-------|----------------------------------|----------------------|
| US001 | `LoginServlet` / `LogoutServlet` | `login.jsp`          |
| US002 | `AddPatientServlet`              | `addPatient.jsp`     |
| US003 | `EditPatientServlet`             | `editPatient.jsp`    |
| US004 | `DeletePatientServlet`           | `deletePatient.jsp`  |
| US005 | `ViewPatientsServlet`            | `viewPatients.jsp`   |
| US006 | `SearchPatientServlet`           | `searchPatient.jsp`  |
| US007 | `DeleteByMobileServlet`          | `deleteByMobile.jsp` |
| US008 | `VisitorRegisterServlet`         | `visitorRegister.jsp`|
| US009 | `VisitorLoginServlet`            | `visitorLogin.jsp`   |
| US010 | `ViewFacilitiesServlet`          | `viewFacilities.jsp` |
| US011 | `RegisterComplaintServlet`       | `registerComplaint.jsp` |
| US001-Collection | `PatientCollection` (mirrored from `AddPatient`/`Delete`) | uses existing screens |
| US002-Exception  | All servlets catch &rarr; `error.jsp`            | `error.jsp` |
| US003-PatientDetailsManagement | `RegisterLabTestServlet` (CBC / BEL) | `labRegister.jsp` |
| US004-LabInheritance | `Bill extends Lab`, `RegisterLabTestServlet` writes both | `labRegister.jsp` |
| US005-Polymorphism   | `BillDAO.search()` / `searchByEmail()` / `searchByPatientId()` | `searchBill.jsp` |

## Validation rules

Both client-side (`js/validation.js`) and server-side (`util/ValidationUtil.java`):

- **User ID** &mdash; alphanumeric, minimum 8 chars.
- **Password** &mdash; minimum 10 chars with at least 1 uppercase, 1 digit, 1 special.
- **PRN / RegistrationId** &mdash; exactly 7 digits.
- **Contact / Mobile** &mdash; exactly 10 digits.
- **Aadhar** &mdash; exactly 12 digits.
- **Email** &mdash; standard email format.

## Default credentials

| Role  | Username   | Password    |
|-------|------------|-------------|
| Admin | `admin001` | `Admin@123` |

The username is `admin001` (not `admin`) because the login rule requires an
alphanumeric username with a minimum of 8 characters.
