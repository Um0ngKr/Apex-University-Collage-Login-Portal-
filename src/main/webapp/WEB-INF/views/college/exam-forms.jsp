<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<jsp:include page="/WEB-INF/views/common/header.jsp">
    <jsp:param name="pageTitle" value="Verify Student Exam Forms • Apex University" />
</jsp:include>

<div class="dashboard-wrapper">
    <jsp:include page="/WEB-INF/views/common/sidebar.jsp">
        <jsp:param name="activeNav" value="exam-forms" />
    </jsp:include>

    <main class="dashboard-content">
        <div class="container-fluid">
            
            <jsp:include page="/WEB-INF/views/common/alerts.jsp" />

            <div class="d-flex justify-content-between align-items-center mb-4 flex-wrap gap-2">
                <div>
                    <h2 class="h3 fw-bold mb-1">Institutional Exam Form Verification</h2>
                    <p class="text-secondary small mb-0">Verify eligibility, attendance criteria, and institutional clearance for enrolled students applying for university examinations.</p>
                </div>
            </div>

            <!-- Status Filter Buttons -->
            <div class="d-flex gap-2 mb-3">
                <a href="${pageContext.request.contextPath}/college/exam-forms" class="btn btn-sm ${empty selectedStatus ? 'btn-primary' : 'btn-outline-secondary'}">All</a>
                <a href="${pageContext.request.contextPath}/college/exam-forms?status=PENDING" class="btn btn-sm ${selectedStatus == 'PENDING' ? 'btn-warning' : 'btn-outline-warning'}">Pending Review</a>
                <a href="${pageContext.request.contextPath}/college/exam-forms?status=APPROVED" class="btn btn-sm ${selectedStatus == 'APPROVED' ? 'btn-success' : 'btn-outline-success'}">Approved</a>
                <a href="${pageContext.request.contextPath}/college/exam-forms?status=REJECTED" class="btn btn-sm ${selectedStatus == 'REJECTED' ? 'btn-danger' : 'btn-outline-danger'}">Rejected</a>
            </div>

            <!-- Forms Table -->
            <div class="card shadow-sm border rounded-4 overflow-hidden mb-4">
                <div class="table-responsive">
                    <table class="table table-hover align-middle mb-0 apex-table">
                        <thead>
                            <tr>
                                <th>Reg No</th>
                                <th>Roll No</th>
                                <th>Student Name</th>
                                <th>Course / Sem</th>
                                <th>Examination Title</th>
                                <th>Submission Date</th>
                                <th>Verification Status</th>
                                <th class="text-center">Review Action</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach var="f" items="${forms}">
                                <tr>
                                    <td class="fw-bold text-primary">${f.registrationNo}</td>
                                    <td class="fw-semibold">${f.rollNo}</td>
                                    <td>${f.studentName}</td>
                                    <td>${f.courseName} (Sem ${f.semesterNumber})</td>
                                    <td><small class="fw-semibold">${f.examName}</small></td>
                                    <td><small class="text-muted">${f.submissionDate}</small></td>
                                    <td>
                                        <span class="badge ${f.collegeApprovalStatus == 'APPROVED' ? 'bg-success' : (f.collegeApprovalStatus == 'REJECTED' ? 'bg-danger' : 'bg-warning')}">
                                            ${f.collegeApprovalStatus}
                                        </span>
                                    </td>
                                    <td class="text-center">
                                        <form action="${pageContext.request.contextPath}/college/exam-form-action" method="POST" class="d-inline-flex gap-1">
                                            <input type="hidden" name="form_id" value="${f.id}">
                                            <button type="submit" name="status" value="APPROVED" class="btn btn-sm btn-success" title="Approve & Recommend">
                                                <i class="bi bi-check-lg"></i> Approve
                                            </button>
                                            <button type="button" class="btn btn-sm btn-outline-danger" title="Reject with Reason"
                                                    onclick="openRejectModal(${f.id}, '${f.studentName}')">
                                                <i class="bi bi-x-lg"></i> Reject
                                            </button>
                                        </form>
                                    </td>
                                </tr>
                            </c:forEach>
                            <c:if test="${empty forms}">
                                <tr><td colspan="8" class="text-center py-5 text-muted">No examination forms awaiting verification for this filter.</td></tr>
                            </c:if>
                        </tbody>
                    </table>
                </div>
            </div>

        </div>
    </main>
</div>

<!-- Modal: Reject Form with Reason -->
<div class="modal fade" id="rejectFormModal" tabindex="-1">
    <div class="modal-dialog">
        <div class="modal-content rounded-4 border-0 shadow">
            <div class="modal-header bg-danger text-white">
                <h5 class="modal-title fw-bold">Reject Examination Form</h5>
                <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal"></button>
            </div>
            <form action="${pageContext.request.contextPath}/college/exam-form-action" method="POST">
                <input type="hidden" name="form_id" id="rejectFormId">
                <input type="hidden" name="status" value="REJECTED">
                <div class="modal-body p-4">
                    <p class="text-secondary small mb-3">Please specify the official institutional reason for rejecting candidate <strong id="rejectStudentName"></strong>'s examination application.</p>
                    <div class="mb-3">
                        <label class="form-label fw-semibold">Rejection Reason</label>
                        <select name="reason" class="form-select mb-2" required>
                            <option value="Attendance below mandatory 75% university requirement">Attendance below mandatory 75% university requirement</option>
                            <option value="Institutional semester fee dues pending">Institutional semester fee dues pending</option>
                            <option value="Continuous internal evaluation (CIE) incomplete">Continuous internal evaluation (CIE) incomplete</option>
                            <option value="Registration / Matriculation documents unverified">Registration / Matriculation documents unverified</option>
                            <option value="Disciplinary hold under university regulations">Disciplinary hold under university regulations</option>
                        </select>
                    </div>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancel</button>
                    <button type="submit" class="btn btn-danger">Confirm Rejection</button>
                </div>
            </form>
        </div>
    </div>
</div>

<script>
function openRejectModal(formId, studentName) {
    document.getElementById('rejectFormId').value = formId;
    document.getElementById('rejectStudentName').textContent = studentName;
    new bootstrap.Modal(document.getElementById('rejectFormModal')).show();
}
</script>

<jsp:include page="/WEB-INF/views/common/footer.jsp" />
