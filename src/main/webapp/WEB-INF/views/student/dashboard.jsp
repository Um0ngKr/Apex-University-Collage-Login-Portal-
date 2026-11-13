<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<jsp:include page="/WEB-INF/views/common/header.jsp">
    <jsp:param name="pageTitle" value="Student Dashboard • Apex University" />
</jsp:include>

<div class="dashboard-wrapper">
    <jsp:include page="/WEB-INF/views/common/sidebar.jsp">
        <jsp:param name="activeNav" value="dashboard" />
    </jsp:include>

    <main class="dashboard-content">
        <div class="container-fluid">
            
            <jsp:include page="/WEB-INF/views/common/alerts.jsp" />

            <!-- Student Profile Header Card -->
            <div class="card shadow-sm border-0 rounded-4 p-4 mb-4 text-white" style="background: linear-gradient(135deg, #0f2b5c 0%, #1e3a8a 100%) !important;">
                <div class="row align-items-center g-3">
                    <div class="col-md-8">
                        <div class="d-flex align-items-center gap-3">
                            <div class="bg-warning text-dark rounded-circle d-flex align-items-center justify-content-center fw-bold fs-3" style="width: 64px; height: 64px;">
                                ${student.fullName.substring(0, 1)}
                            </div>
                            <div>
                                <h3 class="fw-bold mb-1">${student.fullName}</h3>
                                <p class="text-warning mb-0 small">
                                    Registration: <strong>${student.registrationNo}</strong> | Roll: <strong>${student.rollNo}</strong>
                                </p>
                                <small class="text-white-50">${student.courseName} • Semester ${student.semesterNumber} • ${student.sessionCode}</small>
                            </div>
                        </div>
                    </div>
                    <div class="col-md-4 text-md-end">
                        <span class="badge bg-success fs-6 px-3 py-2"><i class="bi bi-patch-check-fill me-1"></i> Active Student Status</span>
                        <div class="small text-white-50 mt-2">${student.collegeName}</div>
                    </div>
                </div>
            </div>

            <!-- Stats Metrics -->
            <div class="row g-3 mb-4">
                <div class="col-md-4 col-12">
                    <div class="stat-card">
                        <div class="stat-icon bg-primary bg-opacity-10 text-primary">
                            <i class="bi bi-file-earmark-text-fill"></i>
                        </div>
                        <div>
                            <p class="stat-number">${stats.submittedForms}</p>
                            <p class="stat-label">Exam Forms Submitted</p>
                        </div>
                    </div>
                </div>

                <div class="col-md-4 col-12">
                    <div class="stat-card">
                        <div class="stat-icon bg-success bg-opacity-10 text-success">
                            <i class="bi bi-credit-card-check-fill"></i>
                        </div>
                        <div>
                            <p class="stat-number">${stats.successfulPayments}</p>
                            <p class="stat-label">Paid Transactions</p>
                        </div>
                    </div>
                </div>

                <div class="col-md-4 col-12">
                    <div class="stat-card">
                        <div class="stat-icon bg-warning bg-opacity-10 text-warning">
                            <i class="bi bi-award-fill"></i>
                        </div>
                        <div>
                            <p class="stat-number">${stats.publishedResults}</p>
                            <p class="stat-label">Results Announced</p>
                        </div>
                    </div>
                </div>
            </div>

            <!-- Quick Action Cards -->
            <div class="row g-3 mb-4">
                <div class="col-md-3 col-6">
                    <a href="${pageContext.request.contextPath}/student/exam" class="card p-3 text-center border text-decoration-none shadow-sm rounded-4 h-100 hover-lift">
                        <i class="bi bi-pencil-square fs-2 text-primary mb-2"></i>
                        <h6 class="fw-bold text-dark mb-1">Fill Exam Form</h6>
                        <small class="text-muted">Register for upcoming exams</small>
                    </a>
                </div>

                <div class="col-md-3 col-6">
                    <a href="${pageContext.request.contextPath}/student/exam" class="card p-3 text-center border text-decoration-none shadow-sm rounded-4 h-100 hover-lift">
                        <i class="bi bi-person-badge fs-2 text-success mb-2"></i>
                        <h6 class="fw-bold text-dark mb-1">Download Admit Card</h6>
                        <small class="text-muted">Print official hall ticket PDF</small>
                    </a>
                </div>

                <div class="col-md-3 col-6">
                    <a href="${pageContext.request.contextPath}/student/results" class="card p-3 text-center border text-decoration-none shadow-sm rounded-4 h-100 hover-lift">
                        <i class="bi bi-award fs-2 text-warning mb-2"></i>
                        <h6 class="fw-bold text-dark mb-1">View Results</h6>
                        <small class="text-muted">Grade card &amp; SGPA statement</small>
                    </a>
                </div>

                <div class="col-md-3 col-6">
                    <a href="${pageContext.request.contextPath}/student/payments" class="card p-3 text-center border text-decoration-none shadow-sm rounded-4 h-100 hover-lift">
                        <i class="bi bi-receipt fs-2 text-info mb-2"></i>
                        <h6 class="fw-bold text-dark mb-1">Fee Payments</h6>
                        <small class="text-muted">Clear dues &amp; download receipt</small>
                    </a>
                </div>
            </div>

            <!-- Recent Notices -->
            <div class="card shadow-sm border rounded-4 p-4 mb-4">
                <h5 class="fw-bold mb-3"><i class="bi bi-megaphone-fill text-danger me-2"></i> University Notices for Students</h5>
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
