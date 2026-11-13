<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<jsp:include page="/WEB-INF/views/common/header.jsp">
    <jsp:param name="pageTitle" value="Privacy Policy • Apex University of Technology" />
</jsp:include>

<div class="container py-5">
    <div class="row justify-content-center">
        <div class="col-lg-10 col-12">
            <div class="card shadow-sm border rounded-4 p-4 p-md-5">
                <h2 class="fw-bold mb-3">Privacy Policy</h2>
                <p class="text-muted small mb-4">Effective Date: January 1, 2025 • Apex University of Technology</p>

                <h5 class="fw-bold">1. Information We Collect</h5>
                <p class="text-secondary small">The Examination Portal collects personal academic information including Candidate Name, Registration Number, Roll Number, Date of Birth, Email Address, Contact Numbers, and Course Enrollments strictly for academic verification, examination form processing, and certification.</p>

                <h5 class="fw-bold mt-4">2. Use of Information</h5>
                <p class="text-secondary small">Collected data is utilized solely for conducting semester examinations, issuing hall tickets, marks calculation, generating transcripts, publishing results, and maintaining official audit records. We do not sell or share student data with third-party commercial marketing platforms.</p>

                <h5 class="fw-bold mt-4">3. Data Security &amp; Access Control</h5>
                <p class="text-secondary small">Passwords are securely hashed using BCrypt cryptographic algorithms. All database queries utilize PreparedStatements to safeguard against SQL Injection. Role-based session controls ensure that students, faculty, and affiliated colleges only access authorized academic data.</p>

                <h5 class="fw-bold mt-4">4. Grievance Redressal</h5>
                <p class="text-secondary small">For any data privacy concerns, please contact the IT &amp; Systems Administrator at <a href="mailto:umangkr87170@gmail.com">umangkr87170@gmail.com</a> or telephone at <a href="tel:08709878369">08709878369</a>.</p>
            </div>
        </div>
    </div>
</div>

<jsp:include page="/WEB-INF/views/common/footer.jsp" />
