<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<jsp:include page="/WEB-INF/views/common/header.jsp">
    <jsp:param name="pageTitle" value="Examination Form & Admit Card • Apex University" />
</jsp:include>

<div class="dashboard-wrapper">
    <jsp:include page="/WEB-INF/views/common/sidebar.jsp">
        <jsp:param name="activeNav" value="exam" />
    </jsp:include>

    <main class="dashboard-content">
        <div class="container-fluid">
            
            <jsp:include page="/WEB-INF/views/common/alerts.jsp" />

            <div class="d-flex justify-content-between align-items-center mb-4 flex-wrap gap-2">
                <div>
                    <h2 class="h3 fw-bold mb-1">Examination Form Fillup &amp; Hall Ticket</h2>
                    <p class="text-secondary small mb-0">Apply for semester examinations, monitor approval progression, and access official Admit Cards.</p>
                </div>
            </div>

            <!-- Active Examinations Available for Form Fillup -->
            <div class="card shadow-sm border rounded-4 p-4 mb-4">
                <h4 class="h5 fw-bold mb-3"><i class="bi bi-pencil-square text-primary me-2"></i> Active Examinations Open for Registration</h4>

                <div class="row g-3">
                    <c:forEach var="ex" items="${activeExams}">
                        <div class="col-lg-6 col-12">
                            <div class="p-3 bg-light rounded-4 border h-100 d-flex flex-column justify-content-between">
                                <div>
                                    <div class="d-flex justify-content-between align-items-center mb-2">
                                        <span class="badge bg-primary">${ex.examCode}</span>
                                        <span class="badge bg-warning text-dark">Semester ${ex.semesterNumber}</span>
                                    </div>
                                    <h5 class="fw-bold text-dark mb-1">${ex.examName}</h5>
                                    <p class="small text-muted mb-2">Dates: <strong>${ex.startDate}</strong> to <strong>${ex.endDate}</strong></p>
                                    <p class="small text-secondary mb-2">Examination Fee: <strong>INR ${ex.feeAmount}</strong></p>
                                </div>
                                <div class="mt-3">
                                    <form action="${pageContext.request.contextPath}/student/exam" method="POST">
                                        <input type="hidden" name="exam_id" value="${ex.id}">
                                        <button type="submit" class="btn btn-apex-primary btn-sm w-100">
                                            <i class="bi bi-check2-circle me-1"></i> Submit Examination Form
                                        </button>
                                    </form>
                                </div>
                            </div>
                        </div>
                    </c:forEach>
                    <c:if test="${empty activeExams}">
                        <div class="col-12 text-center py-4 text-muted">
                            <i class="bi bi-info-circle fs-3 d-block mb-2"></i>
                            No examinations are currently open for form fill-up.
                        </div>
                    </c:if>
                </div>
            </div>

            <!-- Submitted Examination Forms & Admit Card Status -->
            <div class="card shadow-sm border rounded-4 overflow-hidden mb-4">
                <div class="card-header bg-transparent border-bottom py-3">
                    <h5 class="fw-bold mb-0"><i class="bi bi-clipboard2-check text-success me-2"></i> My Submitted Examination Forms</h5>
                </div>
                <div class="table-responsive">
                    <table class="table table-hover align-middle mb-0 apex-table">
                        <thead>
                            <tr>
                                <th>Exam Title</th>
                                <th>Submission Date</th>
                                <th>College Verification</th>
                                <th>University Approval</th>
                                <th>Fee Payment</th>
                                <th>Admit Card (Hall Ticket)</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach var="f" items="${myForms}">
                                <tr>
                                    <td>
                                        <div class="fw-bold">${f.examName}</div>
                                        <small class="text-muted">${f.examCode}</small>
                                    </td>
                                    <td><small class="text-muted">${f.submissionDate}</small></td>
                                    <td>
                                        <span class="badge ${f.collegeApprovalStatus == 'APPROVED' ? 'bg-success' : (f.collegeApprovalStatus == 'REJECTED' ? 'bg-danger' : 'bg-warning')}">
                                            ${f.collegeApprovalStatus}
                                        </span>
                                    </td>
                                    <td>
                                        <span class="badge ${f.adminApprovalStatus == 'APPROVED' ? 'bg-success' : (f.adminApprovalStatus == 'REJECTED' ? 'bg-danger' : 'bg-warning')}">
                                            ${f.adminApprovalStatus}
                                        </span>
                                    </td>
                                    <td>
                                        <c:choose>
                                            <c:when test="${f.paymentStatus == 'PAID'}">
                                                <span class="badge bg-success"><i class="bi bi-check-circle me-1"></i> PAID</span>
                                            </c:when>
                                            <c:otherwise>
                                                <a href="${pageContext.request.contextPath}/student/payments" class="btn btn-sm btn-outline-warning">
                                                    <i class="bi bi-credit-card me-1"></i> Pay ₹${f.feeAmount}
                                                </a>
                                            </c:otherwise>
                                        </c:choose>
                                    </td>
                                    <td>
                                        <c:choose>
                                            <c:when test="${f.admitCardIssued}">
                                                <a href="${pageContext.request.contextPath}/export/pdf?type=admit_card&form_id=${f.id}" 
                                                   class="btn btn-sm btn-apex-gold">
                                                    <i class="bi bi-file-earmark-pdf-fill me-1"></i> Download Admit Card (PDF)
                                                </a>
                                            </c:when>
                                            <c:otherwise>
                                                <span class="text-muted small">
                                                    <i class="bi bi-hourglass-split me-1"></i> Pending Verification &amp; Fee
                                                </span>
                                            </c:otherwise>
                                        </c:choose>
                                    </td>
                                </tr>
                            </c:forEach>
                            <c:if test="${empty myForms}">
                                <tr><td colspan="6" class="text-center py-4 text-muted">You have not submitted any examination forms yet.</td></tr>
                            </c:if>
                        </tbody>
                    </table>
                </div>
            </div>

            <!-- Regular Semester Subjects Registered -->
            <div class="card shadow-sm border rounded-4 p-4">
                <h5 class="fw-bold mb-3"><i class="bi bi-journal-text text-primary me-2"></i> Registered Semester Subjects</h5>
                <div class="table-responsive">
                    <table class="table table-bordered table-sm align-middle small mb-0">
                        <thead class="table-light">
                            <tr>
                                <th>Subject Code</th>
                                <th>Subject Title</th>
                                <th>Credit</th>
                                <th>Type</th>
                                <th>Max Marks (CIE + ESE)</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach var="sub" items="${regularSubjects}">
                                <tr>
                                    <td class="fw-bold">${sub.subjectCode}</td>
                                    <td>${sub.name}</td>
                                    <td>${sub.credit}</td>
                                    <td><span class="badge bg-light text-dark border">${sub.subjectType}</span></td>
                                    <td>${sub.maxInternalMarks} + ${sub.maxExternalMarks} = <strong>${sub.maxInternalMarks + sub.maxExternalMarks}</strong></td>
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
