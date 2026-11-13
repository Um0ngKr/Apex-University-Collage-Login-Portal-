<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<jsp:include page="/WEB-INF/views/common/header.jsp">
    <jsp:param name="pageTitle" value="My Academic Results • Apex University" />
</jsp:include>

<div class="dashboard-wrapper">
    <jsp:include page="/WEB-INF/views/common/sidebar.jsp">
        <jsp:param name="activeNav" value="results" />
    </jsp:include>

    <main class="dashboard-content">
        <div class="container-fluid">
            
            <jsp:include page="/WEB-INF/views/common/alerts.jsp" />

            <div class="d-flex justify-content-between align-items-center mb-4 flex-wrap gap-2">
                <div>
                    <h2 class="h3 fw-bold mb-1">Academic Performance &amp; Grade Cards</h2>
                    <p class="text-secondary small mb-0">Official statements of marks, semester grade points, and certified digital marksheets.</p>
                </div>
                <c:if test="${not empty selectedResult}">
                    <a href="${pageContext.request.contextPath}/export/pdf?type=marksheet&exam_id=${selectedResult.examId}&student_id=${student.id}" 
                       class="btn btn-apex-gold btn-sm">
                        <i class="bi bi-file-earmark-pdf-fill me-1"></i> Download Marksheet PDF
                    </a>
                </c:if>
            </div>

            <!-- Published Examinations Dropdown -->
            <c:if test="${results.size() > 1}">
                <div class="card shadow-sm border rounded-4 p-3 mb-4">
                    <form action="${pageContext.request.contextPath}/student/results" method="GET" class="row g-2 align-items-center">
                        <div class="col-md-9 col-12">
                            <select name="exam_id" class="form-select" onchange="this.form.submit()">
                                <c:forEach var="r" items="${results}">
                                    <option value="${r.examId}" ${selectedResult.examId == r.examId ? 'selected' : ''}>
                                        ${r.examName} - Semester ${r.semesterNumber} (SGPA: ${r.sgpa})
                                    </option>
                                </c:forEach>
                            </select>
                        </div>
                        <div class="col-md-3 col-12">
                            <button type="submit" class="btn btn-primary w-100">Load Statement</button>
                        </div>
                    </form>
                </div>
            </c:if>

            <c:choose>
                <c:when test="${not empty selectedResult}">
                    
                    <!-- Score Summary Header -->
                    <div class="row g-3 mb-4">
                        <div class="col-md-3 col-6">
                            <div class="stat-card">
                                <div>
                                    <p class="stat-number text-primary">${selectedResult.totalObtained} / ${selectedResult.totalMax}</p>
                                    <p class="stat-label">Total Marks</p>
                                </div>
                            </div>
                        </div>
                        <div class="col-md-3 col-6">
                            <div class="stat-card">
                                <div>
                                    <p class="stat-number">${selectedResult.percentage}%</p>
                                    <p class="stat-label">Percentage</p>
                                </div>
                            </div>
                        </div>
                        <div class="col-md-3 col-6">
                            <div class="stat-card">
                                <div>
                                    <p class="stat-number text-success">${selectedResult.sgpa}</p>
                                    <p class="stat-label">Semester GPA (SGPA)</p>
                                </div>
                            </div>
                        </div>
                        <div class="col-md-3 col-6">
                            <div class="stat-card">
                                <div>
                                    <p class="stat-number ${selectedResult.resultStatus == 'PASS' ? 'text-success' : 'text-danger'}">${selectedResult.resultStatus}</p>
                                    <p class="stat-label">Result Status</p>
                                </div>
                            </div>
                        </div>
                    </div>

                    <!-- Marks Breakdown Table -->
                    <div class="card shadow-sm border rounded-4 overflow-hidden mb-4">
                        <div class="card-header bg-transparent border-bottom py-3 d-flex justify-content-between align-items-center">
                            <h5 class="fw-bold mb-0">${selectedResult.examName} • Semester ${selectedResult.semesterNumber}</h5>
                            <span class="badge bg-success">Status: PASS</span>
                        </div>
                        <div class="table-responsive">
                            <table class="table table-hover align-middle mb-0 apex-table text-center">
                                <thead>
                                    <tr>
                                        <th>Code</th>
                                        <th class="text-start">Subject Title</th>
                                        <th>Internal (CIE)</th>
                                        <th>External (ESE)</th>
                                        <th>Total Marks</th>
                                        <th>Letter Grade</th>
                                        <th>Grade Point</th>
                                        <th>Pass/Fail</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <c:forEach var="m" items="${marks}">
                                        <tr>
                                            <td class="fw-bold">${m.subjectCode}</td>
                                            <td class="text-start">${m.subjectName}</td>
                                            <td>${m.internalMarks}</td>
                                            <td>${m.externalMarks}</td>
                                            <td class="fw-bold">${m.totalMarks}</td>
                                            <td><span class="badge bg-light text-dark border fw-bold">${m.letterGrade}</span></td>
                                            <td>${m.gradePoint}</td>
                                            <td>
                                                <span class="badge ${m.pass ? 'bg-success' : 'bg-danger'}">${m.pass ? 'PASS' : 'FAIL'}</span>
                                            </td>
                                        </tr>
                                    </c:forEach>
                                </tbody>
                            </table>
                        </div>
                    </div>

                </c:when>
                <c:otherwise>
                    <div class="card shadow-sm border rounded-4 p-5 text-center text-muted">
                        <i class="bi bi-clock-history fs-1 d-block mb-3 text-warning"></i>
                        <h4 class="fw-bold text-dark mb-1">No Results Declared Yet</h4>
                        <p class="small mb-0">Your submitted examination is currently in the evaluation / scrutiny phase. Results will appear here once officially published by the Controller of Examinations.</p>
                    </div>
                </c:otherwise>
            </c:choose>

        </div>
    </main>
</div>

<jsp:include page="/WEB-INF/views/common/footer.jsp" />
