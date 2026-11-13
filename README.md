# 🎓 APEX UNIVERSITY OF TECHNOLOGY (APEX)
### Complete University Examination & Academic Management System
**Enterprise Java Full-Stack Academic Web Application (MVC Architecture • Servlets • JSP • JDBC • MySQL • Tomcat 10)**

---

## 📌 1. Project Overview & Features

Apex University of Technology (APEX) is a production-grade, full-stack Academic & Examination Portal designed for university administrators, affiliated colleges, teachers, students, and public stakeholders.

### 🌟 Key Highlights:
- **Zero Heavy Frameworks:** Pure Java EE MVC Architecture (Jakarta Servlet 6.0, JSP 3.1, JDBC, JSTL). No Spring Boot, No Node.js, No React.
- **Role-Based Portals:**
  1. 🛡️ **Admin Portal:** Student/Teacher/College CRUD, Course & Subject mapping, Examination scheduling, Form verification, Marks compile & Results publishing, Payments verification, Audit logs.
  2. 👨‍🏫 **Teacher Portal:** Subject assignments, Class-wise student lists, Daily attendance marking, Internal marks upload.
  3. 🎓 **Student Portal:** Profile, Exam form fill-up, Sandbox online fee payment, PDF Admit Card download, PDF Grade Card/Marksheet download, Attendance reports.
  4. 🏛️ **College Portal:** Student enrollment & verification, College-level exam form approval/rejection.
  5. 📜 **Public Result Portal:** Public grade verification by Roll Number / Registration Number without login, PDF marksheet generation.
- **Enterprise Security:** BCrypt password hashing, session-based role authentication, CSRF tokens, PreparedStatement SQL-injection prevention, XSS escaping, audit trail logging.
- **Pure Java PDF Engine:** Generates official Admit Cards, Marksheets, and Fee Receipts on the fly without heavy external native tools.
- **Modern UI:** Responsive Bootstrap 5.3, Dark/Light mode toggle (localStorage persistent), dynamic modals, SVG university crest.

---

## 💻 2. System Requirements (Prerequisites)

Naye device me is project ko run karne ke liye ye 3 software hone chahiye:

1. **Java Development Kit (JDK 17 or higher):**
   - Recommended: JDK 17, JDK 21, ya JDK 26.
   - Verify in terminal: `java -version` and `javac -version`
2. **Apache Tomcat 10.1.x (Mandatory):**
   - ⚠️ **Note:** Tomcat 9 use **NA** karein kyunki Tomcat 9 legacy `javax.*` use karta hai jabki Tomcat 10 me modern `jakarta.*` (Servlet 6.0) standard hota hai.
   - Download from: [https://tomcat.apache.org/download-10.cgi](https://tomcat.apache.org/download-10.cgi) (Select *64-bit Windows zip*).
3. **MySQL Server (8.0 or higher):**
   - MySQL Community Server ya XAMPP (MySQL).
   - Default Port: `3306`.
4. *(Optional)* **Build Tools / IDE:**
   - Apache Maven (Command line) OR
   - Eclipse IDE for Enterprise Java and Web Developers.

---

## 📂 3. Complete Project Folder Structure (Where Every File Goes)

Agar aap kisi fresh computer me files bana rahe hain ya zip extract kar rahe hain, to files ka structure bilkul aisa hona chahiye:

```text
apex-university/
├── pom.xml                                   <-- Maven build configuration
├── database.sql                              <-- MySQL schema & initial seed data script
├── README.md                                 <-- This setup guide
│
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/
│   │   │       └── apex/
│   │   │           ├── config/
│   │   │           │   └── DBConnectionManager.java     <-- JDBC connection pool with auto-fallback
│   │   │           ├── controller/
│   │   │           │   ├── admin/                       <-- Admin Servlets (Dashboard, Students, Exams, Marks, etc.)
│   │   │           │   │   ├── AdminAcademicController.java
│   │   │           │   │   ├── AdminAttendanceController.java
│   │   │           │   │   ├── AdminCollegeController.java
│   │   │           │   │   ├── AdminContentController.java
│   │   │           │   │   ├── AdminDashboardController.java
│   │   │           │   │   ├── AdminExamController.java
│   │   │           │   │   ├── AdminMarksController.java
│   │   │           │   │   ├── AdminPaymentController.java
│   │   │           │   │   ├── AdminSecurityController.java
│   │   │           │   │   ├── AdminStudentController.java
│   │   │           │   │   └── AdminTeacherController.java
│   │   │           │   ├── college/                     <-- College Servlets
│   │   │           │   │   ├── CollegeDashboardController.java
│   │   │           │   │   ├── CollegeExamFormController.java
│   │   │           │   │   └── CollegeStudentController.java
│   │   │           │   ├── common/                      <-- Public & Shared Servlets
│   │   │           │   │   ├── AuthController.java
│   │   │           │   │   ├── ContentController.java
│   │   │           │   │   ├── HelpdeskController.java
│   │   │           │   │   ├── HomeController.java
│   │   │           │   │   └── ResultSearchController.java
│   │   │           │   ├── export/                      <-- PDF & CSV Exporters
│   │   │           │   │   ├── CsvExportController.java
│   │   │           │   │   └── PdfExportController.java
│   │   │           │   ├── student/                     <-- Student Servlets
│   │   │           │   │   ├── StudentAttendanceController.java
│   │   │           │   │   ├── StudentDashboardController.java
│   │   │           │   │   ├── StudentExamController.java
│   │   │           │   │   ├── StudentPaymentController.java
│   │   │           │   │   ├── StudentProfileController.java
│   │   │           │   │   └── StudentResultController.java
│   │   │           │   └── teacher/                     <-- Teacher Servlets
│   │   │           │       ├── TeacherAttendanceController.java
│   │   │           │       ├── TeacherDashboardController.java
│   │   │           │       ├── TeacherMarksController.java
│   │   │           │       └── TeacherProfileController.java
│   │   │           ├── dao/                             <-- JDBC DAO Data Access Objects (17 DAOs)
│   │   │           │   ├── AcademicSessionDAO.java
│   │   │           │   ├── AttendanceDAO.java
│   │   │           │   ├── AuditLogDAO.java
│   │   │           │   ├── CollegeDAO.java
│   │   │           │   ├── ContentDAO.java
│   │   │           │   ├── CourseDAO.java
│   │   │           │   ├── DashboardDAO.java
│   │   │           │   ├── DepartmentDAO.java
│   │   │           │   ├── ExaminationDAO.java
│   │   │           │   ├── ExamFormDAO.java
│   │   │           │   ├── MarkDAO.java
│   │   │           │   ├── PaymentDAO.java
│   │   │           │   ├── ResultDAO.java
│   │   │           │   ├── StudentDAO.java
│   │   │           │   ├── SubjectDAO.java
│   │   │           │   ├── TeacherDAO.java
│   │   │           │   └── UserDAO.java
│   │   │           ├── filter/                          <-- Security & Auth Filters
│   │   │           │   ├── AuthenticationFilter.java
│   │   │           │   └── SecurityHeaderFilter.java
│   │   │           ├── model/                           <-- 20 Java Model Beans
│   │   │           │   ├── AcademicSession.java
│   │   │           │   ├── Attendance.java
│   │   │           │   ├── AuditLog.java
│   │   │           │   ├── College.java
│   │   │           │   ├── Course.java
│   │   │           │   ├── Department.java
│   │   │           │   ├── Examination.java
│   │   │           │   ├── ExamForm.java
│   │   │           │   ├── ExamSchedule.java
│   │   │           │   ├── HelpdeskContact.java
│   │   │           │   ├── Instruction.java
│   │   │           │   ├── Mark.java
│   │   │           │   ├── Notice.java
│   │   │           │   ├── Payment.java
│   │   │           │   ├── Result.java
│   │   │           │   ├── Semester.java
│   │   │           │   ├── Student.java
│   │   │           │   ├── Subject.java
│   │   │           │   ├── Teacher.java
│   │   │           │   └── User.java
│   │   │           └── util/                            <-- Utilities (BCrypt, PDF, CSRF, Grades)
│   │   │               ├── CsrfUtil.java
│   │   │               ├── DateUtil.java
│   │   │               ├── GradeCalculator.java
│   │   │               ├── PasswordUtil.java
│   │   │               └── SimplePdfGenerator.java
│   │   │
│   │   ├── resources/
│   │   │   ├── db.properties                        <-- Database credentials file
│   │   │   └── database.sql                         <-- Copy of database schema & seeds
│   │   │
│   │   └── webapp/
│   │       ├── static/                              <-- Custom CSS, JS, Logos
│   │       │   ├── css/style.css
│   │       │   ├── js/main.js
│   │       │   └── images/
│   │       │       ├── logo.svg
│   │       │       └── favicon.svg
│   │       │
│   │       └── WEB-INF/
│   │           ├── web.xml                          <-- Servlet 6.0 deployment descriptor
│   │           └── views/                           <-- 40 JSP Pages
│   │               ├── admin/                       <-- 12 Admin JSPs
│   │               ├── college/                     <-- 3 College JSPs
│   │               ├── common/                      <-- header.jsp, footer.jsp, sidebar.jsp, alerts.jsp
│   │               ├── public/                      <-- index.jsp, login.jsp, result-search.jsp, etc.
│   │               ├── student/                     <-- 6 Student JSPs
│   │               └── teacher/                     <-- 4 Teacher JSPs
│   │
│   └── target/
│       └── apex-university.war                      <-- Final compiled deployment WAR package
```

---

## 🚀 4. Step-by-Step Setup Guide (Fresh Device Setup)

### **STEP 1: MySQL Server Start karein aur Database Create karein**

1. Windows Command Prompt (CMD) ko **"Run as administrator"** karke open karein.
2. MySQL Service ko start karein:
   ```cmd
   net start MySQL80
   ```
   *(Agar aapke PC par service ka naam `MySQL` hai to `net start MySQL` likhein).*
3. Project ke andar di gayi `database.sql` script ko MySQL me import karein:
   ```cmd
   "C:\Program Files\MySQL\MySQL Server 8.0\bin\mysql.exe" -u root -p < "C:\Users\Umang\OneDrive\Desktop\apex-university\database.sql"
   ```
   - Jab password maange, apna MySQL password enter karein (agar blank hai to bina kuch type kiye direct Enter press karein).
   - Ye command automatically `apex_university_db` database, 22 tables, sample examination, results, notices, aur testing accounts create kar degi.

---

### **STEP 2: Database Password Check karein (`db.properties`)**

Open karein: `apex-university\src\main\resources\db.properties`
```properties
db.driver=com.mysql.cj.jdbc.Driver
db.url=jdbc:mysql://localhost:3306/apex_university_db?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC&characterEncoding=UTF-8
db.user=root
db.password=
```
- Agar aapka MySQL password `root` ya kuch aur hai, to `db.password=your_password` likh kar save karein.
- *(Note: `DBConnectionManager.java` me auto-detection bhi hai jo blank, root, root123, admin sabhi par automatically try karta hai).*

---

### **STEP 3: Project Build karein (WAR File Generate karna)**

Aapke paas **2 tarike** hain:

#### **Tarika A: Command Line (Maven ya JDK se)**
Agar Maven installed hai:
```cmd
cd C:\Users\Umang\OneDrive\Desktop\apex-university
mvn clean package
```
Ye command `target/apex-university.war` generate kar degi.

*(Already built file is folder me present hai: `apex-university\target\apex-university.war`).*

#### **Tarika B: Eclipse IDE se Run karna**
1. **Eclipse IDE for Enterprise Java and Web Developers** open karein.
2. `File` $\rightarrow$ `Import` $\rightarrow$ `Maven` $\rightarrow$ `Existing Maven Projects`.
3. Folder select karein: `apex-university` aur `Finish` karein.
4. Eclipse ke `Servers` tab me Tomcat 10 add karein.
5. Project par Right Click $\rightarrow$ `Run As` $\rightarrow$ `Run on Server`.

---

### **STEP 4: Apache Tomcat 10.1 par Deploy karna (Direct & Fast)**

1. Apache Tomcat 10.1 zip extract karein (Example: `C:\apache-tomcat-10.1.34`).
2. `apex-university\target\apex-university.war` file ko copy karein.
3. Use Tomcat ke **`webapps\`** folder me paste kar dein (`C:\apache-tomcat-10.1.34\webapps\apex-university.war`).
4. Tomcat ke `bin\` folder me jaakar **`startup.bat`** par double click karein.
5. Black console window me message aayega:
   > `Deployment of web application archive ... has finished`
   > `Server startup in [x] milliseconds`

---

### **STEP 5: Website Browser me Open karein**

Browser me URL kholein:
👉 **`http://localhost:8080/apex-university/`**

---

## 🔑 5. Default Login Accounts & Testing Credentials

Database me ye pre-seeded authentic BCrypt hashed accounts ready hain:

| Portal | URL | Username / Identifier | Password | What You Can Test |
|---|---|---|---|---|
| **🛡️ Admin Portal** | `/login?role=ADMIN` | `admin` | `admin123` | View stats, add student/teacher/college, approve exam forms, publish exam results, edit notices & helpdesk. |
| **👨‍🏫 Teacher Portal** | `/login?role=TEACHER` | `TCH101` | `teacher123` | View assigned courses/subjects, mark daily attendance, enter student internal marks. |
| **🎓 Student Portal** | `/login?role=STUDENT` | `APEX202200101`<br>*(or Roll: `11500122001`)* | `student123` | Fill exam form, pay fees in sandbox mode, download PDF Admit Card, download PDF Marksheet. |
| **🏛️ College Portal** | `/login?role=COLLEGE` | `COL101` | `college123` | Register new students, verify and forward examination forms to university. |
| **📜 Public Result** | `/result-search` | Roll: `11500122001`<br>*(or Reg: `APEX202200101`)* | *(No password)* | Public grade card view, subject marks verification, PDF marksheet download. |

---

## 📞 6. Official University Helpdesk Contact Matrix (Embedded)

- **University Name:** Apex University of Technology (APEX)
- **Campus Address:** Kaikhali Main Rd, Kolkata
- **University Email:** [umangkr871170@gmail.com](mailto:umangkr871170@gmail.com)
- **Online Payment Issues Helpline:** `8709878369` | Email: [adarshraj112008@gmail.com](mailto:adarshraj112008@gmail.com)
- **24/7 Payment Customer Care:** `7667046114`
- **Technical Support / IT Cell:** `08709878369` / `8709878369` | Email: [umangkr87170@gmail.com](mailto:umangkr87170@gmail.com)
- **Controller of Examinations / Transcripts:** `95081 53596`
- **Registration & Migration Cell:** `70614 25471`
- **Transcript & Certificate Cell:** `7320 991 072`

---

## 🛠️ 7. Troubleshooting & Common Errors

| Issue / Error | Cause | Solution |
|---|---|---|
| **"Invalid credentials" / Login nahi ho raha** | 1. MySQL service band hai.<br>2. `database.sql` import nahi hua.<br>3. MySQL root password mismatch. | 1. Run as Admin CMD: `net start MySQL80`<br>2. `mysql -u root -p < database.sql` run karein.<br>3. `db.properties` me apna MySQL password update karein. |
| **404 Not Found on `http://localhost:8080/apex-university`** | Tomcat ke `webapps` me war file deploy nahi hui ya Tomcat start nahi hua. | `target\apex-university.war` ko Tomcat ke `webapps\` me paste karein aur `bin\startup.bat` chalayein. |
| **ClassNotFoundException: `jakarta.servlet.*`** | Tomcat 9 ya purana version chal raha hai. | Tomcat 10.1 download karein (Servlet 6.0 Jakarta EE compatibility ke liye). |
| **Port 8080 already in use** | Dusra process (jaise Oracle ya Jenkins) port 8080 use kar raha hai. | Tomcat ke `conf/server.xml` me jakar `<Connector port="8080"` ko `8085` ya `9090` kar dein. |

---
**Apex University Examination and Academic Management System • Developed with Java MVC Architecture**


# TO on and off the website use command 
1st - set the tomcat variable : 
$tomcat = "C:\Users\Umang\Downloads\apache-tomcat-10.1.61-windows-x64\apache-tomcat-10.1.61"

2nd - for on the server use : 
& "$tomcat\bin\startup.bat"


3rd - for off the server use: 
& "$tomcat\bin\shutdown.bat"

after on the server type the local host: 
http://localhost:8080/apex-university/ 



# if any change apply in the project use this steps: 

1-set directory 
cd "C:\Users\Umang\OneDrive\Desktop\apex-university"   
 
2 - install package 
mvn package

3 - set tomcat variable 
$env:TOMCAT_HOME = "C:\Users\Umang\Downloads\apache-tomcat-10.1.61-windows-x64\apache-tomcat-10.1.61"

4th and 5th - shutdown or start the project 
& "$env:TOMCAT_HOME\bin\shutdown.bat"

& "$env:TOMCAT_HOME\bin\startup.bat" 

then run local host in browser 




# steps to set war file in tomcat variable 

1st - set directory: 
cd "C:\Users\Umang\OneDrive\Desktop\apex-university"

2ns - install package 
mvn package

3rd - set tomcat varible path in terminal 
$env:TOMCAT_HOME = "C:\Users\Umang\Downloads\apache-tomcat-10.1.61-windows-x64\apache-tomcat-10.1.61"

4th - Stop Tomcat
& "$env:TOMCAT_HOME\bin\shutdown.bat"


5th - Remove old deployed application
Remove-Item "$env:TOMCAT_HOME\webapps\apex-university" -Recurse -Force -ErrorAction SilentlyContinue
Remove-Item "$env:TOMCAT_HOME\webapps\apex-university.war" -Force -ErrorAction SilentlyContinue
Remove-Item "$env:TOMCAT_HOME\work\Catalina\localhost\apex-university" -Recurse -Force -ErrorAction SilentlyContinue

6th - Copy new WAR file 
Copy-Item "C:\Users\Umang\OneDrive\Desktop\apex-university\target\apex-university.war" "$env:TOMCAT_HOME\webapps\apex-university.war" -Force

7th - Start Tomcat
& "$env:TOMCAT_HOME\bin\startup.bat" 

after run local host 
