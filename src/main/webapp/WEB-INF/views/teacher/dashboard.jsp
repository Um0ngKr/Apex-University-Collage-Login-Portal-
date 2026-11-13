<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<jsp:include page="/WEB-INF/views/common/header.jsp">
    <jsp:param name="pageTitle" value="Teacher Dashboard • Apex University" />
</jsp:include>

<div class="dashboard-wrapper">
    <jsp:include page="/WEB-INF/views/common/sidebar.jsp">
        <jsp:param name="activeNav" value="dashboard" />
    </jsp:include>

    <main class="dashboard-content">
        <div class="container-fluid">
            
            <jsp:include page="/WEB-INF/views/common/alerts.jsp" />

            <!-- Welcome Header -->
            <div class="d-flex justify-content-between align-items-center mb-4 flex-wrap gap-2">
                <div>
                    <h2 class="h3 fw-bold mb-1">Welcome, ${teacher.fullName}</h2>
                    <p class="text-secondary small mb-0">${teacher.designation} • ${teacher.departmentName} (${teacher.employeeId})</p>
                </div>
                <div class="d-flex gap-2">
                    <a href="${pageContext.request.contextPath}/teacher/attendance" class="btn btn-success btn-sm">
                        <i class="bi bi-calendar-check me-1"></i> Mark Attendance
                    </a>
                    <a href="${pageContext.request.contextPath}/teacher/marks" class="btn btn-apex-primary btn-sm">
                        <i class="bi bi-pencil-square me-1"></i> Upload Internal Marks
                    </a>
                </div>
            </div>

            <!-- Stats -->
            <div class="row g-3 mb-4">
                <div class="col-md-4 col-12">
                    <div class="stat-card">
                        <div class="stat-icon bg-primary bg-opacity-10 text-primary">
                            <i class="bi bi-book-half"></i>
                        </div>
                        <div>
                            <p class="stat-number">${stats.assignedSubjects}</p>
                            <p class="stat-label">Assigned Subjects</p>
                        </div>
                    </div>
                </div>
                <div class="col-md-4 col-12">
                    <div class="stat-card">
                        <div class="stat-icon bg-success bg-opacity-10 text-success">
                            <i class="bi bi-calendar2-check"></i>
                        </div>
                        <div>
                            <p class="stat-number">${stats.classesTaken}</p>
                            <p class="stat-label">Classes Recorded</p>
                        </div>
                    </div>
                </div>
                <div class="col-md-4 col-12">
                    <div class="stat-card">
                        <div class="stat-icon bg-warning bg-opacity-10 text-warning">
                            <i class="bi bi-pencil-fill"></i>
                        </div>
                        <div>
                            <p class="stat-number">${stats.evaluatedMarks}</p>
                            <p class="stat-label">Marks Evaluated</p>
                        </div>
                    </div>
                </div>
            </div>

            <!-- Assigned Subjects Table -->
            <div class="card shadow-sm border rounded-4 p-4 mb-4">
                <h4 class="h5 fw-bold mb-3"><i class="bi bi-journal-code text-primary me-2"></i> Assigned Teaching Classes &amp; Subjects</h4>
                <div class="table-responsive">
                    <table class="table table-hover align-middle mb-0 apex-table">
                        <thead>
                            <tr>
                                <th>Subject Code</th>
                                <th>Subject Title</th>
                                <th>Course / Program</th>
                                <th>Semester</th>
                                <th>Academic Session</th>
                                <th>Section</th>
                                <th class="text-center">Quick Action</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach var="asg" items="${assignments}">
                                <tr>
                                    <td class="fw-bold text-primary">${asg.subjectCode}</td>
                                    <td class="fw-semibold">${asg.subjectName}</td>
                                    <td>${asg.courseName}</td>
                                    <td><span class="badge bg-secondary">Semester ${asg.semesterNumber}</span></td>
                                    <td>${asg.sessionCode}</td>
                                    <td><span class="badge bg-light text-dark border">Section ${asg.section}</span></td>
                                    <td class="text-center">
                                        <div class="btn-group btn-group-sm">
                                            <a href="${pageContext.request.contextPath}/teacher/attendance?subject_id=${asg.subjectId}" class="btn btn-outline-success">
                                                <i class="bi bi-calendar-check"></i> Attendance
                                            </a>
                                            <a href="${pageContext.request.contextPath}/teacher/marks?subject_id=${asg.subjectId}" class="btn btn-outline-primary">
                                                <i class="bi bi-pencil"></i> Marks
                                            </a>
                                        </div>
                                    </td>
                                </tr>
                            </c:forEach>
                            <c:if test="${empty assignments}">
                                <tr><td colspan="7" class="text-center py-4 text-muted">No course subjects currently assigned to your account.</td></tr>
                            </c:if>
                        </tbody>
                    </table>
                </div>
            </div>

            <!-- Faculty Notices -->
            <div class="card shadow-sm border rounded-4 p-4">
                <h5 class="fw-bold mb-3"><i class="bi bi-megaphone-fill text-danger me-2"></i> University Notices for Faculty</h5>
                <c:forEach var="n" items="${notices}">
                    <div class="border-bottom pb-2 mb-2">
                        <div class="d-flex justify-content-between align-items-center">
                            <h6 class="fw-bold text-primary mb-1">${n.title}</h6>
                            <small class="text-muted">${n.publishedAt}</small>
                        </div>
                        <p class="small text-secondary mb-0">${n.description}</p>
                    </div>
                </c:forEach>
                <c:if test="${empty notices}">
                    <p class="text-muted small mb-0">No active circulars.</p>
                </c:if>
            </div>

        </div>
    </main>
</div>

<jsp:include page="/WEB-INF/views/common/footer.jsp" />
