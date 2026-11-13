<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<jsp:include page="/WEB-INF/views/common/header.jsp">
    <jsp:param name="pageTitle" value="College Dashboard • Apex University" />
</jsp:include>

<div class="dashboard-wrapper">
    <jsp:include page="/WEB-INF/views/common/sidebar.jsp">
        <jsp:param name="activeNav" value="dashboard" />
    </jsp:include>

    <main class="dashboard-content">
        <div class="container-fluid">
            
            <jsp:include page="/WEB-INF/views/common/alerts.jsp" />

            <div class="d-flex justify-content-between align-items-center mb-4 flex-wrap gap-2">
                <div>
                    <h2 class="h3 fw-bold mb-1">${college.name}</h2>
                    <p class="text-secondary small mb-0">Code: <strong>${college.collegeCode}</strong> • Principal: ${college.principalName} • ${college.address}</p>
                </div>
                <div class="d-flex gap-2">
                    <a href="${pageContext.request.contextPath}/college/students" class="btn btn-apex-primary btn-sm">
                        <i class="bi bi-person-plus me-1"></i> Register Student
                    </a>
                    <a href="${pageContext.request.contextPath}/college/exam-forms" class="btn btn-warning btn-sm">
                        <i class="bi bi-clipboard-check me-1"></i> Verify Exam Forms
                    </a>
                </div>
            </div>

            <!-- Stats -->
            <div class="row g-3 mb-4">
                <div class="col-md-4 col-12">
                    <div class="stat-card">
                        <div class="stat-icon bg-primary bg-opacity-10 text-primary">
                            <i class="bi bi-people-fill"></i>
                        </div>
                        <div>
                            <p class="stat-number">${stats.totalStudents}</p>
                            <p class="stat-label">Total Registered Students</p>
                        </div>
                    </div>
                </div>

                <div class="col-md-4 col-12">
                    <div class="stat-card">
                        <div class="stat-icon bg-warning bg-opacity-10 text-warning">
                            <i class="bi bi-hourglass-split"></i>
                        </div>
                        <div>
                            <p class="stat-number">${stats.pendingForms}</p>
                            <p class="stat-label">Forms Pending Verification</p>
                        </div>
                    </div>
                </div>

                <div class="col-md-4 col-12">
                    <div class="stat-card">
                        <div class="stat-icon bg-success bg-opacity-10 text-success">
                            <i class="bi bi-check2-circle"></i>
                        </div>
                        <div>
                            <p class="stat-number">${stats.approvedForms}</p>
                            <p class="stat-label">Approved Forms</p>
                        </div>
                    </div>
                </div>
            </div>

            <!-- Recent Students Table -->
            <div class="card shadow-sm border rounded-4 overflow-hidden mb-4">
                <div class="card-header bg-transparent border-bottom py-3 d-flex justify-content-between align-items-center">
                    <h5 class="fw-bold mb-0"><i class="bi bi-mortarboard text-primary me-2"></i> Recently Registered Students</h5>
                    <a href="${pageContext.request.contextPath}/college/students" class="small fw-semibold text-decoration-none">View All Students</a>
                </div>
                <div class="table-responsive">
                    <table class="table table-hover align-middle mb-0 apex-table">
                        <thead>
                            <tr>
                                <th>Registration No</th>
                                <th>Roll No</th>
                                <th>Student Name</th>
                                <th>Course</th>
                                <th>Semester</th>
                                <th>Status</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach var="s" items="${recentStudents}">
                                <tr>
                                    <td class="fw-bold text-primary">${s.registrationNo}</td>
                                    <td>${s.rollNo}</td>
                                    <td class="fw-semibold">${s.fullName}</td>
                                    <td>${s.courseName}</td>
                                    <td>Semester ${s.semesterNumber}</td>
                                    <td><span class="badge bg-success">${s.status}</span></td>
                                </tr>
                            </c:forEach>
                            <c:if test="${empty recentStudents}">
                                <tr><td colspan="6" class="text-center py-4 text-muted">No students enrolled yet.</td></tr>
                            </c:if>
                        </tbody>
                    </table>
                </div>
            </div>

            <!-- College Notices -->
            <div class="card shadow-sm border rounded-4 p-4">
                <h5 class="fw-bold mb-3"><i class="bi bi-megaphone-fill text-danger me-2"></i> Institutional Circulars</h5>
                <c:forEach var="n" items="${notices}">
                    <div class="border-bottom pb-2 mb-2">
                        <div class="d-flex justify-content-between align-items-center">
                            <h6 class="fw-bold text-primary mb-1">${n.title}</h6>
                            <small class="text-muted">${n.publishedAt}</small>
                        </div>
                        <p class="small text-secondary mb-0">${n.description}</p>
                    </div>
                </c:forEach>
            </div>

        </div>
    </main>
</div>

<jsp:include page="/WEB-INF/views/common/footer.jsp" />
