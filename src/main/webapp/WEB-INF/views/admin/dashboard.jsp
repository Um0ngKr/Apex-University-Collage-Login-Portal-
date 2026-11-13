<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<jsp:include page="/WEB-INF/views/common/header.jsp">
    <jsp:param name="pageTitle" value="Admin Dashboard • Apex University of Technology" />
</jsp:include>

<div class="dashboard-wrapper">
    <jsp:include page="/WEB-INF/views/common/sidebar.jsp">
        <jsp:param name="activeNav" value="dashboard" />
    </jsp:include>

    <main class="dashboard-content">
        <div class="container-fluid">
            
            <jsp:include page="/WEB-INF/views/common/alerts.jsp" />

            <!-- Dashboard Welcome Banner -->
            <div class="d-flex justify-content-between align-items-center mb-4 flex-wrap gap-2">
                <div>
                    <h2 class="h3 fw-bold mb-1">Controller of Examinations Workspace</h2>
                    <p class="text-secondary small mb-0">Overview of student enrollments, institutional records, examination routines, and financial clearance.</p>
                </div>
                <div class="d-flex gap-2">
                    <a href="${pageContext.request.contextPath}/admin/students" class="btn btn-apex-primary btn-sm">
                        <i class="bi bi-person-plus me-1"></i> Add Student
                    </a>
                    <a href="${pageContext.request.contextPath}/admin/examinations" class="btn btn-apex-gold btn-sm">
                        <i class="bi bi-calendar-plus me-1"></i> Create Exam
                    </a>
                </div>
            </div>

            <!-- Stats Metrics Row 1 -->
            <div class="row g-3 mb-4">
                <div class="col-xl-3 col-md-6 col-12">
                    <div class="stat-card">
                        <div class="stat-icon bg-primary bg-opacity-10 text-primary">
                            <i class="bi bi-people-fill"></i>
                        </div>
                        <div>
                            <p class="stat-number">${stats.totalStudents}</p>
                            <p class="stat-label">Total Students</p>
                        </div>
                    </div>
                </div>

                <div class="col-xl-3 col-md-6 col-12">
                    <div class="stat-card">
                        <div class="stat-icon bg-success bg-opacity-10 text-success">
                            <i class="bi bi-person-video3"></i>
                        </div>
                        <div>
                            <p class="stat-number">${stats.totalTeachers}</p>
                            <p class="stat-label">Total Teachers</p>
                        </div>
                    </div>
                </div>

                <div class="col-xl-3 col-md-6 col-12">
                    <div class="stat-card">
                        <div class="stat-icon bg-info bg-opacity-10 text-info">
                            <i class="bi bi-buildings-fill"></i>
                        </div>
                        <div>
                            <p class="stat-number">${stats.totalColleges}</p>
                            <p class="stat-label">Affiliated Colleges</p>
                        </div>
                    </div>
                </div>

                <div class="col-xl-3 col-md-6 col-12">
                    <div class="stat-card">
                        <div class="stat-icon bg-warning bg-opacity-10 text-warning">
                            <i class="bi bi-journal-bookmark-fill"></i>
                        </div>
                        <div>
                            <p class="stat-number">${stats.totalCourses}</p>
                            <p class="stat-label">Degree Courses</p>
                        </div>
                    </div>
                </div>
            </div>

            <!-- Stats Metrics Row 2 -->
            <div class="row g-3 mb-4">
                <div class="col-xl-3 col-md-6 col-12">
                    <div class="stat-card">
                        <div class="stat-icon bg-danger bg-opacity-10 text-danger">
                            <i class="bi bi-calendar2-week"></i>
                        </div>
                        <div>
                            <p class="stat-number">${stats.totalExams}</p>
                            <p class="stat-label">Examinations</p>
                        </div>
                    </div>
                </div>

                <div class="col-xl-3 col-md-6 col-12">
                    <div class="stat-card">
                        <div class="stat-icon bg-success bg-opacity-10 text-success">
                            <i class="bi bi-award-fill"></i>
                        </div>
                        <div>
                            <p class="stat-number">${stats.publishedResults}</p>
                            <p class="stat-label">Published Results</p>
                        </div>
                    </div>
                </div>

                <div class="col-xl-3 col-md-6 col-12">
                    <div class="stat-card">
                        <div class="stat-icon bg-warning bg-opacity-10 text-warning">
                            <i class="bi bi-clock-history"></i>
                        </div>
                        <div>
                            <p class="stat-number">${stats.pendingPayments}</p>
                            <p class="stat-label">Pending Payments</p>
                        </div>
                    </div>
                </div>

                <div class="col-xl-3 col-md-6 col-12">
                    <div class="stat-card">
                        <div class="stat-icon bg-primary bg-opacity-10 text-primary">
                            <i class="bi bi-currency-rupee"></i>
                        </div>
                        <div>
                            <p class="stat-number">₹${stats.totalCollections}</p>
                            <p class="stat-label">Fee Collections</p>
                        </div>
                    </div>
                </div>
            </div>

            <!-- Activities & Overview -->
            <div class="row g-4 mb-4">
                <!-- Recent Fee Transactions -->
                <div class="col-lg-6 col-12">
                    <div class="card shadow-sm border rounded-4 h-100">
                        <div class="card-header bg-transparent border-bottom d-flex justify-content-between align-items-center py-3">
                            <h5 class="fw-bold mb-0"><i class="bi bi-credit-card me-1 text-primary"></i> Recent Fee Transactions</h5>
                            <a href="${pageContext.request.contextPath}/admin/payments" class="small fw-semibold text-decoration-none">View All</a>
                        </div>
                        <div class="card-body p-0">
                            <div class="table-responsive">
                                <table class="table table-hover align-middle mb-0">
                                    <thead class="table-light small">
                                        <tr>
                                            <th>Receipt No</th>
                                            <th>Student</th>
                                            <th>Amount</th>
                                            <th>Status</th>
                                        </tr>
                                    </thead>
                                    <tbody>
                                        <c:forEach var="p" items="${recentPayments}">
                                            <tr>
                                                <td class="fw-semibold small">${p.receiptNo}</td>
                                                <td>
                                                    <div class="fw-bold small">${p.studentName}</div>
                                                    <small class="text-muted">${p.rollNo}</small>
                                                </td>
                                                <td class="fw-bold">₹${p.amount}</td>
                                                <td>
                                                    <span class="badge ${p.paymentStatus == 'SUCCESS' ? 'bg-success' : 'bg-warning'}">${p.paymentStatus}</span>
                                                </td>
                                            </tr>
                                        </c:forEach>
                                        <c:if test="${empty recentPayments}">
                                            <tr><td colspan="4" class="text-center py-3 text-muted">No recent transactions recorded.</td></tr>
                                        </c:if>
                                    </tbody>
                                </table>
                            </div>
                        </div>
                    </div>
                </div>

                <!-- Recent System Audit Logs -->
                <div class="col-lg-6 col-12">
                    <div class="card shadow-sm border rounded-4 h-100">
                        <div class="card-header bg-transparent border-bottom d-flex justify-content-between align-items-center py-3">
                            <h5 class="fw-bold mb-0"><i class="bi bi-shield-check me-1 text-success"></i> Recent Activity Audit Trail</h5>
                            <a href="${pageContext.request.contextPath}/admin/security" class="small fw-semibold text-decoration-none">Audit Logs</a>
                        </div>
                        <div class="card-body p-3">
                            <div class="d-flex flex-column gap-3">
                                <c:forEach var="log" items="${recentLogs}">
                                    <div class="d-flex align-items-start gap-2 border-bottom pb-2">
                                        <div class="badge bg-secondary mt-1">${log.action}</div>
                                        <div class="flex-grow-1">
                                            <div class="small fw-semibold">${log.details}</div>
                                            <small class="text-muted">By <strong>${log.username}</strong> (${log.userType}) • ${log.createdAt}</small>
                                        </div>
                                    </div>
                                </c:forEach>
                                <c:if test="${empty recentLogs}">
                                    <div class="text-center text-muted py-3">No audit actions logged yet.</div>
                                </c:if>
                            </div>
                        </div>
                    </div>
                </div>
            </div>

        </div>
    </main>
</div>

<jsp:include page="/WEB-INF/views/common/footer.jsp" />
