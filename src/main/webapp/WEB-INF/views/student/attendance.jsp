<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<jsp:include page="/WEB-INF/views/common/header.jsp">
    <jsp:param name="pageTitle" value="My Attendance • Apex University" />
</jsp:include>

<div class="dashboard-wrapper">
    <jsp:include page="/WEB-INF/views/common/sidebar.jsp">
        <jsp:param name="activeNav" value="attendance" />
    </jsp:include>

    <main class="dashboard-content">
        <div class="container-fluid">
            
            <jsp:include page="/WEB-INF/views/common/alerts.jsp" />

            <div class="d-flex justify-content-between align-items-center mb-4 flex-wrap gap-2">
                <div>
                    <h2 class="h3 fw-bold mb-1">Classroom Attendance Records</h2>
                    <p class="text-secondary small mb-0">Track your continuous lecture attendance and ensure compliance with University examination criteria.</p>
                </div>
            </div>

            <!-- Overall Percentage Header Card -->
            <div class="row g-3 mb-4">
                <div class="col-md-4 col-12">
                    <div class="stat-card">
                        <div class="stat-icon bg-primary bg-opacity-10 text-primary">
                            <i class="bi bi-calendar-event"></i>
                        </div>
                        <div>
                            <p class="stat-number">${totalClassesAll}</p>
                            <p class="stat-label">Total Conducted Classes</p>
                        </div>
                    </div>
                </div>

                <div class="col-md-4 col-12">
                    <div class="stat-card">
                        <div class="stat-icon bg-success bg-opacity-10 text-success">
                            <i class="bi bi-check-circle"></i>
                        </div>
                        <div>
                            <p class="stat-number">${totalAttendedAll}</p>
                            <p class="stat-label">Attended Lectures</p>
                        </div>
                    </div>
                </div>

                <div class="col-md-4 col-12">
                    <div class="stat-card">
                        <div class="stat-icon bg-warning bg-opacity-10 text-warning">
                            <i class="bi bi-pie-chart-fill"></i>
                        </div>
                        <div>
                            <p class="stat-number ${overallPct >= 75.0 ? 'text-success' : 'text-danger'}">${overallPct}%</p>
                            <p class="stat-label">Aggregate Attendance</p>
                        </div>
                    </div>
                </div>
            </div>

            <!-- Eligibility Status Banner -->
            <div class="alert ${overallPct >= 75.0 ? 'alert-success' : 'alert-danger'} d-flex align-items-center gap-3 p-3 rounded-4 mb-4">
                <i class="bi ${overallPct >= 75.0 ? 'bi-shield-check' : 'bi-exclamation-octagon-fill'} fs-3"></i>
                <div>
                    <strong>Examination Eligibility Status:</strong>
                    <c:choose>
                        <c:when test="${overallPct >= 75.0}">
                            Eligible to appear for upcoming semester university examinations (Exceeds mandatory 75% attendance threshold).
                        </c:when>
                        <c:otherwise>
                            Attendance is currently below the mandatory 75% university requirement. Please contact your respective Department Head.
                        </c:otherwise>
                    </c:choose>
                </div>
            </div>

            <!-- Subject-Wise Attendance Breakdown Table -->
            <div class="card shadow-sm border rounded-4 overflow-hidden mb-4">
                <div class="card-header bg-transparent border-bottom py-3">
                    <h5 class="fw-bold mb-0"><i class="bi bi-journal-check text-primary me-2"></i> Subject-Wise Attendance Breakdown</h5>
                </div>
                <div class="table-responsive">
                    <table class="table table-hover align-middle mb-0 apex-table text-center">
                        <thead>
                            <tr>
                                <th>Subject Code</th>
                                <th class="text-start">Subject Title</th>
                                <th>Conducted Classes</th>
                                <th>Attended Classes</th>
                                <th>Attendance %</th>
                                <th>Eligibility Status</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach var="item" items="${attendanceList}">
                                <tr>
                                    <td class="fw-bold text-primary">${item.subjectCode}</td>
                                    <td class="text-start fw-semibold">${item.subjectName}</td>
                                    <td>${item.totalClasses}</td>
                                    <td>${item.attendedClasses}</td>
                                    <td class="fw-bold">${item.percentage}%</td>
                                    <td>
                                        <c:choose>
                                            <c:when test="${item.percentage >= 75.0}">
                                                <span class="badge bg-success px-3 py-1">ELIGIBLE</span>
                                            </c:when>
                                            <c:otherwise>
                                                <span class="badge bg-danger px-3 py-1">DEFICIT</span>
                                            </c:otherwise>
                                        </c:choose>
                                    </td>
                                </tr>
                            </c:forEach>
                        </tbody>
                    </table>
                </div>
            </div>

        </div>
    </main>
</div>

<jsp:include page="/WEB-INF/views/common/footer.jsp" />
