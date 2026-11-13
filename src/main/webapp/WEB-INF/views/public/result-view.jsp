<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<jsp:include page="/WEB-INF/views/common/header.jsp">
    <jsp:param name="pageTitle" value="Statement of Marks • Apex University of Technology" />
</jsp:include>

<div class="container py-5">
    
    <!-- Action Bar -->
    <div class="d-flex justify-content-between align-items-center mb-4 flex-wrap gap-2">
        <a href="${pageContext.request.contextPath}/result-search" class="btn btn-outline-secondary">
            <i class="bi bi-arrow-left me-1"></i> Back to Search
        </a>
        <div class="d-flex gap-2">
            <button onclick="window.print()" class="btn btn-outline-primary">
                <i class="bi bi-printer me-1"></i> Print Result
            </button>
            <a href="${pageContext.request.contextPath}/export/pdf?type=marksheet&exam_id=${result.examId}&student_id=${student.id}" 
               class="btn btn-apex-gold">
                <i class="bi bi-file-earmark-pdf-fill me-1"></i> Download Marksheet PDF
            </a>
        </div>
    </div>

    <!-- Official Marksheet Paper Container -->
    <div class="card shadow border-2 rounded-4 p-4 p-md-5 bg-white text-dark printable-marksheet" style="border-color: #0f2b5c !important;">
        
        <!-- Marksheet Header -->
        <div class="text-center pb-4 mb-4 border-bottom">
            <img src="${pageContext.request.contextPath}/static/images/logo.svg" alt="Apex Logo" style="width: 85px; height: 85px;" class="mb-2">
            <h2 class="fw-bold mb-0 text-primary" style="color: #0f2b5c !important; letter-spacing: 1px;">APEX UNIVERSITY OF TECHNOLOGY</h2>
            <p class="text-muted small mb-1">Kaikhali Main Rd, Kolkata • Examination Division</p>
            <div class="badge bg-primary px-3 py-1 fs-6 text-uppercase mt-2">Statement of Marks &amp; Grades</div>
            <p class="fw-semibold mt-2 mb-0">${result.examName} (${result.sessionCode})</p>
        </div>

        <!-- Candidate Details Grid -->
        <div class="row g-3 mb-4 pb-3 border-bottom small">
            <div class="col-md-6 col-12">
                <table class="table table-sm table-borderless mb-0">
                    <tr>
                        <td class="text-muted" style="width: 140px;">Student Name:</td>
                        <td class="fw-bold">${student.fullName}</td>
                    </tr>
                    <tr>
                        <td class="text-muted">Registration No:</td>
                        <td class="fw-bold text-primary">${student.registrationNo}</td>
                    </tr>
                    <tr>
                        <td class="text-muted">Roll Number:</td>
                        <td class="fw-bold">${student.rollNo}</td>
                    </tr>
                    <tr>
                        <td class="text-muted">Father's Name:</td>
                        <td>${student.fatherName}</td>
                    </tr>
                </table>
            </div>
            <div class="col-md-6 col-12">
                <table class="table table-sm table-borderless mb-0">
                    <tr>
                        <td class="text-muted" style="width: 140px;">Academic Program:</td>
                        <td class="fw-bold">${student.courseName}</td>
                    </tr>
                    <tr>
                        <td class="text-muted">Semester:</td>
                        <td class="fw-bold">Semester ${result.semesterNumber}</td>
                    </tr>
                    <tr>
                        <td class="text-muted">Institution/College:</td>
                        <td>${student.collegeName}</td>
                    </tr>
                    <tr>
                        <td class="text-muted">Result Declared:</td>
                        <td>${result.publishedAt}</td>
                    </tr>
                </table>
            </div>
        </div>

        <!-- Subject-Wise Marks Breakdown Table -->
        <div class="table-responsive mb-4">
            <table class="table table-bordered text-center align-middle mb-0">
                <thead class="table-dark" style="background-color: #0f2b5c !important;">
                    <tr>
                        <th style="width: 120px;">Subject Code</th>
                        <th class="text-start">Subject Title</th>
                        <th style="width: 110px;">Internal (CIE)</th>
                        <th style="width: 110px;">External (ESE)</th>
                        <th style="width: 100px;">Total</th>
                        <th style="width: 110px;">Letter Grade</th>
                        <th style="width: 100px;">Grade Point</th>
                        <th style="width: 90px;">Status</th>
                    </tr>
                </thead>
                <tbody>
                    <c:forEach var="m" items="${marks}">
                        <tr>
                            <td class="fw-semibold">${m.subjectCode}</td>
                            <td class="text-start">${m.subjectName}</td>
                            <td>${m.internalMarks}</td>
                            <td>${m.externalMarks}</td>
                            <td class="fw-bold">${m.totalMarks}</td>
                            <td>
                                <span class="badge bg-light text-dark border px-2 py-1 fw-bold">${m.letterGrade}</span>
                            </td>
                            <td>${m.gradePoint}</td>
                            <td>
                                <c:choose>
                                    <c:when test="${m.pass}">
                                        <span class="badge bg-success">PASS</span>
                                    </c:when>
                                    <c:otherwise>
                                        <span class="badge bg-danger">FAIL</span>
                                    </c:otherwise>
                                </c:choose>
                            </td>
                        </tr>
                    </c:forEach>
                </tbody>
            </table>
        </div>

        <!-- Performance Summary Card -->
        <div class="row g-3 align-items-center p-3 rounded-3 bg-light border mb-4">
            <div class="col-md-3 col-6 text-center border-end">
                <small class="text-muted text-uppercase fw-bold d-block">Total Marks</small>
                <span class="fs-4 fw-bolder text-primary">${result.totalObtained} / ${result.totalMax}</span>
            </div>
            <div class="col-md-3 col-6 text-center border-end">
                <small class="text-muted text-uppercase fw-bold d-block">Percentage</small>
                <span class="fs-4 fw-bolder text-dark">${result.percentage} %</span>
            </div>
            <div class="col-md-3 col-6 text-center border-end">
                <small class="text-muted text-uppercase fw-bold d-block">Semester GPA (SGPA)</small>
                <span class="fs-4 fw-bolder text-success">${result.sgpa}</span>
            </div>
            <div class="col-md-3 col-6 text-center">
                <small class="text-muted text-uppercase fw-bold d-block">Final Result Status</small>
                <c:choose>
                    <c:when test="${result.resultStatus == 'PASS'}">
                        <span class="badge bg-success fs-6 px-3 py-2">PASSED</span>
                    </c:when>
                    <c:otherwise>
                        <span class="badge bg-danger fs-6 px-3 py-2">${result.resultStatus}</span>
                    </c:otherwise>
                </c:choose>
            </div>
        </div>

        <!-- Grading Legend -->
        <div class="p-3 bg-white rounded border small text-muted mb-4">
            <span class="fw-bold">10-Point Grading Scale:</span> O: Outstanding (90-100%, GP 10) | A+: Excellent (80-89%, GP 9) | A: Very Good (70-79%, GP 8) | B+: Good (60-69%, GP 7) | B: Above Average (50-59%, GP 6) | P: Pass (40-49%, GP 5) | F: Fail (&lt;40%, GP 0)
        </div>

        <!-- Digital Signature Seal -->
        <div class="d-flex justify-content-between align-items-end mt-4 pt-4 border-top">
            <div class="text-muted small">
                <i class="bi bi-qr-code fs-3 d-block mb-1"></i>
                Digitally Generated Verification System<br>
                Date of Download: <%= java.time.LocalDate.now() %>
            </div>
            <div class="text-center">
                <div class="fw-bold text-decoration-overline" style="color: #0f2b5c;">Controller of Examinations</div>
                <small class="text-muted">Apex University of Technology</small>
            </div>
        </div>

    </div>

</div>

<jsp:include page="/WEB-INF/views/common/footer.jsp" />
