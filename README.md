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

## User Roles

- **Admin** - manages patient records, books lab tests, reviews all bills.
- **Patient** - self-registers, logs in, views and edits own details, sees
  own lab tests, requests a doctor consultation, sees own bills.

(There is no separate "visitor" role any more; visitor login/registration was
replaced by patient login/registration.)

## Repository Layout

```
HospitalManagementSystem/
├── src/com/hospital/
│   ├── model/           Patient, Admin, Lab, Bill, Consultation
│   ├── dao/             PatientDAO, AdminDAO, LabDAO, BillDAO,
│   │                    ConsultationDAO, PatientCollection
│   ├── servlet/         Admin: Login/Logout, Add/Edit/Delete/View/Search
│   │                    Patient: Login/Register/Logout, Profile,
│   │                             MyLabTests, MyBills, RegisterConsultation
│   │                    Shared: RegisterLabTest, ViewLabTests, ViewBills, SearchBill
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
and drop it into `HospitalManagementSystem/WebContent/WEB-INF/lib/`.

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
- create all tables (`Patient`, `Admin`, `Lab`, `Bills`, `Consultation`),
- migrate older databases by adding `Username` / `Password` columns to
  `Patient` if they are missing,
- drop the legacy `Visitor` and `Complaint` tables if present,
- seed the default admin (`admin001` / `Admin@123`),
- seed 10 demo patient records, with login credentials on the first three
  (`patient001`, `patient002`, `patient003` &mdash; all `Patient@123`).

## Default credentials

| Role    | Username     | Password      |
|---------|--------------|---------------|
| Admin   | `admin001`   | `Admin@123`   |
| Patient | `patient001` | `Patient@123` |
| Patient | `patient002` | `Patient@123` |
| Patient | `patient003` | `Patient@123` |

Self-registered patients pick their own username and password during signup.

## Validation rules

Both client-side (`js/validation.js`) and server-side (`util/ValidationUtil.java`):

- **User ID** &mdash; alphanumeric, minimum 8 chars.
- **Password** &mdash; minimum 10 chars with at least 1 uppercase, 1 digit, 1 special.
- **PRN / RegistrationId** &mdash; exactly 7 digits.
- **Contact / Mobile** &mdash; exactly 10 digits.
- **Aadhar** &mdash; exactly 12 digits.
- **Email** &mdash; standard email format.

## Patient self-service flow

| Action                          | URL                          | What you see                                    |
|---------------------------------|------------------------------|-------------------------------------------------|
| Register                        | `/patientRegister`           | Create username + password + full patient details. |
| Login                           | `/patientLogin`              | Sign in.                                        |
| Dashboard                       | `/jsp/patientDashboard.jsp`  | Tile menu.                                      |
| View my details                 | `/myProfile`                 | Read-only view of your record.                  |
| Edit my details                 | `/myProfile/edit`            | Edit name, age, contact info, address, etc.    |
| My lab tests                    | `/myLabTests`                | Lab tests booked against your PRN.              |
| Register doctor consultation    | `/registerConsultation`      | Pick department + doctor + date.                |
| My bills                        | `/myBills`                   | Bills generated for your treatments.            |
| Logout                          | `/patientLogout`             | Ends your session.                              |

Patients cannot edit their PRN, username, ward, or doctor assignment - those
are administered by hospital staff via the admin screens.
