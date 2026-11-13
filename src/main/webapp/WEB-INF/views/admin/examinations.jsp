<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<jsp:include page="/WEB-INF/views/common/header.jsp">
    <jsp:param name="pageTitle" value="Examination Management • Apex University" />
</jsp:include>

<div class="dashboard-wrapper">
    <jsp:include page="/WEB-INF/views/common/sidebar.jsp">
        <jsp:param name="activeNav" value="examinations" />
    </jsp:include>

    <main class="dashboard-content">
        <div class="container-fluid">
            
            <jsp:include page="/WEB-INF/views/common/alerts.jsp" />

            <div class="d-flex justify-content-between align-items-center mb-4 flex-wrap gap-2">
                <div>
                    <h2 class="h3 fw-bold mb-1">Examination Management</h2>
                    <p class="text-secondary small mb-0">Create examinations, manage exam dates, review submitted exam forms, and issue digital hall tickets.</p>
                </div>
                <button type="button" class="btn btn-apex-primary btn-sm" data-bs-toggle="modal" data-bs-target="#createExamModal">
                    <i class="bi bi-calendar-plus me-1"></i> Create Examination
                </button>
            </div>

            <!-- Tab Buttons -->
            <ul class="nav nav-pills mb-4 p-1 bg-light rounded-3">
                <li class="nav-item">
                    <a class="nav-link ${empty activeTab || activeTab == 'list' ? 'active bg-primary' : 'text-dark'}" href="${pageContext.request.contextPath}/admin/examinations?tab=list">
                        <i class="bi bi-calendar2-check-fill me-1"></i> All Examinations (${exams.size()})
                    </a>
                </li>
                <c:if test="${not empty selectedExam}">
                    <li class="nav-item">
                        <a class="nav-link ${activeTab == 'schedule' ? 'active bg-primary' : 'text-dark'}" href="${pageContext.request.contextPath}/admin/examinations?tab=schedule&exam_id=${selectedExam.id}">
                            <i class="bi bi-clock-history me-1"></i> Routine: ${selectedExam.examCode}
                        </a>
                    </li>
                    <li class="nav-item">
                        <a class="nav-link ${activeTab == 'forms' ? 'active bg-primary' : 'text-dark'}" href="${pageContext.request.contextPath}/admin/examinations?tab=forms&exam_id=${selectedExam.id}">
                            <i class="bi bi-card-checklist me-1"></i> Exam Forms (${forms.size()})
                        </a>
                    </li>
                </c:if>
            </ul>

            <!-- ================= TAB: EXAM LIST ================= -->
            <c:if test="${empty activeTab || activeTab == 'list'}">
                <div class="card shadow-sm border rounded-4 overflow-hidden mb-4">
                    <div class="table-responsive">
                        <table class="table table-hover align-middle mb-0 apex-table apex-table-compact">
                            <thead>
                                <tr>
                                    <th>Exam Code</th>
                                    <th>Examination Title</th>
                                    <th>Academic Session</th>
                                    <th>Semester</th>
                                    <th>Dates</th>
                                    <th>Fee (INR)</th>
                                    <th>Status</th>
                                    <th>Results</th>
                                    <th class="text-center text-nowrap" style="width: 220px; min-width: 220px;">Manage</th>
                                </tr>
                            </thead>
                            <tbody>
                                <c:forEach var="e" items="${exams}">
                                    <tr>
                                        <td class="fw-bold text-primary">${e.examCode}</td>
                                        <td class="fw-semibold">${e.examName}</td>
                                        <td>${e.sessionCode}</td>
                                        <td><span class="badge bg-secondary">Semester ${e.semesterNumber}</span></td>
                                        <td><small>${e.startDate} to ${e.endDate}</small></td>
                                        <td class="fw-bold">₹${e.feeAmount}</td>
                                        <td>
                                            <span class="badge ${e.status == 'ACTIVE' ? 'bg-success' : 'bg-info text-dark'}">${e.status}</span>
                                        </td>
                                        <td>
                                            <c:choose>
                                                <c:when test="${e.resultPublished}">
                                                    <span class="badge bg-success"><i class="bi bi-check-all"></i> Published</span>
                                                </c:when>
                                                <c:otherwise>
                                                    <span class="badge bg-secondary">Draft / Unpublished</span>
                                                </c:otherwise>
                                            </c:choose>
                                        </td>
                                        <td class="text-center text-nowrap">
                                            <div class="table-actions">
                                                <a href="${pageContext.request.contextPath}/admin/examinations?tab=schedule&exam_id=${e.id}" class="btn btn-sm btn-outline-primary" title="Exam Routine">
                                                    <i class="bi bi-calendar3"></i> Routine
                                                </a>
                                                <a href="${pageContext.request.contextPath}/admin/examinations?tab=forms&exam_id=${e.id}" class="btn btn-sm btn-outline-info" title="Review Forms">
                                                    <i class="bi bi-ui-checks"></i> Forms
                                                </a>
                                                <button type="button" class="btn btn-sm btn-outline-primary btn-icon-only"
                                                        data-id="${e.id}"
                                                        data-code="<c:out value='${e.examCode}' />"
                                                        data-name="<c:out value='${e.examName}' />"
                                                        data-session="${e.sessionId}"
                                                        data-semester="${e.semesterNumber}"
                                                        data-start="${e.startDate}"
                                                        data-end="${e.endDate}"
                                                        data-type="${e.examType}"
                                                        data-fee="${e.feeAmount}"
                                                        data-status="${e.status}"
                                                        onclick="populateEditExam(this)"
                                                        title="Edit Examination">
                                                    <i class="bi bi-pencil-square"></i>
                                                </button>
                                                <form action="${pageContext.request.contextPath}/admin/exam-action" method="POST" class="d-inline">
                                                    <input type="hidden" name="action" value="delete">
                                                    <input type="hidden" name="id" value="${e.id}">
                                                    <button type="submit" class="btn btn-sm btn-outline-danger btn-icon-only btn-confirm-delete" data-confirm="Delete examination ${e.examName}?" title="Delete Examination">
                                                        <i class="bi bi-trash"></i>
                                                    </button>
                                                </form>
                                            </div>
                                        </td>
                                    </tr>
                                </c:forEach>
                            </tbody>
                        </table>
                    </div>
                </div>
            </c:if>

            <!-- ================= TAB: EXAM ROUTINE / SCHEDULE ================= -->
            <c:if test="${activeTab == 'schedule' && not empty selectedExam}">
                <div class="card shadow-sm border rounded-4 p-4 mb-4">
                    <div class="d-flex justify-content-between align-items-center mb-3">
                        <h4 class="h5 fw-bold mb-0">Examination Routine: ${selectedExam.examName} (${selectedExam.examCode})</h4>
                        <button type="button" class="btn btn-apex-primary btn-sm" data-bs-toggle="modal" data-bs-target="#addScheduleModal">
                            <i class="bi bi-plus-lg me-1"></i> Add Routine Slot
                        </button>
                    </div>

                    <div class="table-responsive">
                        <table class="table table-bordered align-middle mb-0 apex-table apex-table-compact">
                            <thead>
                                <tr>
                                    <th>Subject Code</th>
                                    <th>Subject Name</th>
                                    <th>Date</th>
                                    <th>Time</th>
                                    <th>Examination Hall / Room</th>
                                    <th class="text-center text-nowrap" style="width: 110px; min-width: 110px;">Action</th>
                                </tr>
                            </thead>
                            <tbody>
                                <c:forEach var="sc" items="${schedules}">
                                    <tr>
                                        <td class="fw-bold">${sc.subjectCode}</td>
                                        <td>${sc.subjectName}</td>
                                        <td>${sc.examDate}</td>
                                        <td>${sc.startTime} - ${sc.endTime}</td>
                                        <td><span class="badge bg-light text-dark border">${sc.roomNo}</span></td>
                                        <td class="text-center text-nowrap">
                                            <div class="table-actions">
                                                <button type="button" class="btn btn-sm btn-outline-primary btn-icon-only"
                                                        data-id="${sc.id}"
                                                        data-subject="${sc.subjectId}"
                                                        data-date="${sc.examDate}"
                                                        data-start="${sc.startTime}"
                                                        data-end="${sc.endTime}"
                                                        data-room="<c:out value='${sc.roomNo}' />"
                                                        onclick="populateEditSchedule(this)"
                                                        title="Edit Slot">
                                                    <i class="bi bi-pencil-square"></i>
                                                </button>
                                                <form action="${pageContext.request.contextPath}/admin/exam-action" method="POST" class="d-inline">
                                                    <input type="hidden" name="action" value="delete_schedule">
                                                    <input type="hidden" name="schedule_id" value="${sc.id}">
                                                    <input type="hidden" name="exam_id" value="${selectedExam.id}">
                                                    <button type="submit" class="btn btn-sm btn-outline-danger btn-icon-only btn-confirm-delete" data-confirm="Delete this routine slot?" title="Delete Slot">
                                                        <i class="bi bi-trash"></i>
                                                    </button>
                                                </form>
                                            </div>
                                        </td>
                                    </tr>
                                </c:forEach>
                                <c:if test="${empty schedules}">
                                    <tr><td colspan="6" class="text-center py-4 text-muted">No examination routine slots configured yet. Click "Add Routine Slot".</td></tr>
                                </c:if>
                            </tbody>
                        </table>
                    </div>
                </div>
            </c:if>

            <!-- ================= TAB: EXAM FORMS REVIEW ================= -->
            <c:if test="${activeTab == 'forms' && not empty selectedExam}">
                <div class="card shadow-sm border rounded-4 p-4 mb-4">
                    <h4 class="h5 fw-bold mb-3">Student Examination Forms for ${selectedExam.examName}</h4>

                    <div class="table-responsive">
                        <table class="table table-hover align-middle mb-0 apex-table apex-table-compact">
                            <thead>
                                <tr>
                                    <th>Reg No</th>
                                    <th>Roll No</th>
                                    <th>Student Name</th>
                                    <th>College Approval</th>
                                    <th>Admin Approval</th>
                                    <th>Payment Status</th>
                                    <th>Admit Card</th>
                                    <th class="text-center text-nowrap" style="width: 170px; min-width: 170px;">Action</th>
                                </tr>
                            </thead>
                            <tbody>
                                <c:forEach var="f" items="${forms}">
                                    <tr>
                                        <td class="fw-bold text-primary">${f.registrationNo}</td>
                                        <td>${f.rollNo}</td>
                                        <td>${f.studentName}</td>
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
                                            <span class="badge ${f.paymentStatus == 'PAID' ? 'bg-success' : 'bg-warning'}">${f.paymentStatus}</span>
                                        </td>
                                        <td>
                                            <c:choose>
                                                <c:when test="${f.admitCardIssued}">
                                                    <a href="${pageContext.request.contextPath}/export/pdf?type=admit_card&form_id=${f.id}" class="btn btn-sm btn-outline-success">
                                                        <i class="bi bi-file-earmark-pdf"></i> Download PDF
                                                    </a>
                                                </c:when>
                                                <c:otherwise>
                                                    <span class="text-muted small">Not Issued</span>
                                                </c:otherwise>
                                            </c:choose>
                                        </td>
                                        <td class="text-center text-nowrap">
                                            <div class="table-actions">
                                                <button type="button" class="btn btn-sm btn-outline-primary btn-icon-only"
                                                        data-id="${f.id}"
                                                        data-reg="<c:out value='${f.registrationNo}' />"
                                                        data-name="<c:out value='${f.studentName}' />"
                                                        data-college="${f.collegeApprovalStatus}"
                                                        data-admin="${f.adminApprovalStatus}"
                                                        data-payment="${f.paymentStatus}"
                                                        data-reason="<c:out value='${f.rejectionReason}' />"
                                                        data-admit="${f.admitCardIssued}"
                                                        onclick="populateEditForm(this)"
                                                        title="Edit Form Record">
                                                    <i class="bi bi-pencil-square"></i>
                                                </button>
                                                <form action="${pageContext.request.contextPath}/admin/exam-action" method="POST" class="d-inline">
                                                    <input type="hidden" name="action" value="approve_form">
                                                    <input type="hidden" name="form_id" value="${f.id}">
                                                    <input type="hidden" name="exam_id" value="${selectedExam.id}">
                                                    <input type="hidden" name="approval_status" value="APPROVED">
                                                    <button type="submit" class="btn btn-sm btn-success btn-icon-only" title="Approve Form">
                                                        <i class="bi bi-check-lg"></i>
                                                    </button>
                                                </form>
                                                <form action="${pageContext.request.contextPath}/admin/exam-action" method="POST" class="d-inline">
                                                    <input type="hidden" name="action" value="approve_form">
                                                    <input type="hidden" name="form_id" value="${f.id}">
                                                    <input type="hidden" name="exam_id" value="${selectedExam.id}">
                                                    <input type="hidden" name="approval_status" value="REJECTED">
                                                    <button type="submit" class="btn btn-sm btn-danger btn-icon-only" title="Reject Form">
                                                        <i class="bi bi-x-lg"></i>
                                                    </button>
                                                </form>
                                            </div>
                                        </td>
                                    </tr>
                                </c:forEach>
                                <c:if test="${empty forms}">
                                    <tr><td colspan="8" class="text-center py-4 text-muted">No examination forms submitted yet.</td></tr>
                                </c:if>
                            </tbody>
                        </table>
                    </div>
                </div>
            </c:if>

        </div>
    </main>
</div>

<!-- Modal: Create Exam -->
<div class="modal fade" id="createExamModal" tabindex="-1">
    <div class="modal-dialog">
        <div class="modal-content rounded-4 border-0 shadow">
            <div class="modal-header bg-primary text-white">
                <h5 class="modal-title fw-bold">Create Examination Session</h5>
                <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal"></button>
            </div>
            <form action="${pageContext.request.contextPath}/admin/exam-action" method="POST">
                <input type="hidden" name="action" value="add">
                <div class="modal-body p-4">
                    <div class="mb-3">
                        <label class="form-label fw-semibold">Examination Title</label>
                        <input type="text" name="exam_name" class="form-control" placeholder="e.g. Even Semester Examination 2025" required>
                    </div>
                    <div class="mb-3">
                        <label class="form-label fw-semibold">Exam Code <span class="text-muted small">(Auto if blank)</span></label>
                        <input type="text" name="exam_code" class="form-control" placeholder="e.g. EXAM-2025-EVEN-6">
                    </div>
                    <div class="row g-2 mb-3">
                        <div class="col-6">
                            <label class="form-label fw-semibold">Academic Session</label>
                            <select name="session_id" class="form-select" required>
                                <c:forEach var="ses" items="${sessions}">
                                    <option value="${ses.id}">${ses.sessionCode}</option>
                                </c:forEach>
                            </select>
                        </div>
                        <div class="col-6">
                            <label class="form-label fw-semibold">Semester Number</label>
                            <input type="number" name="semester_number" class="form-control" value="6" required>
                        </div>
                    </div>
                    <div class="row g-2 mb-3">
                        <div class="col-6">
                            <label class="form-label fw-semibold">Start Date</label>
                            <input type="date" name="start_date" class="form-control" required>
                        </div>
                        <div class="col-6">
                            <label class="form-label fw-semibold">End Date</label>
                            <input type="date" name="end_date" class="form-control" required>
                        </div>
                    </div>
                    <div class="row g-2 mb-3">
                        <div class="col-6">
                            <label class="form-label fw-semibold">Exam Type</label>
                            <select name="exam_type" class="form-select">
                                <option value="REGULAR">REGULAR</option>
                                <option value="BACKLOG">BACKLOG</option>
                                <option value="SPECIAL">SPECIAL</option>
                            </select>
                        </div>
                        <div class="col-6">
                            <label class="form-label fw-semibold">Exam Fee (INR)</label>
                            <input type="number" step="50" name="fee_amount" class="form-control" value="1200" required>
                        </div>
                    </div>
                    <div class="mb-3">
                        <label class="form-label fw-semibold">Status</label>
                        <select name="status" class="form-select">
                            <option value="ACTIVE">ACTIVE</option>
                            <option value="FORM_FILLUP">FORM FILLUP OPEN</option>
                            <option value="UPCOMING">UPCOMING</option>
                            <option value="CLOSED">CLOSED</option>
                        </select>
                    </div>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancel</button>
                    <button type="submit" class="btn btn-primary">Schedule Examination</button>
                </div>
            </form>
        </div>
    </div>
</div>

<!-- Modal: Edit Exam -->
<div class="modal fade" id="editExamModal" tabindex="-1">
    <div class="modal-dialog">
        <div class="modal-content rounded-4 border-0 shadow">
            <div class="modal-header bg-primary text-white">
                <h5 class="modal-title fw-bold">Edit Examination Session</h5>
                <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal"></button>
            </div>
            <form action="${pageContext.request.contextPath}/admin/exam-action" method="POST">
                <input type="hidden" name="action" value="edit">
                <input type="hidden" name="id" id="editExamId">
                <div class="modal-body p-4">
                    <div class="mb-3">
                        <label class="form-label fw-semibold">Examination Title</label>
                        <input type="text" name="exam_name" id="editExamName" class="form-control" required>
                    </div>
                    <div class="mb-3">
                        <label class="form-label fw-semibold">Exam Code</label>
                        <input type="text" name="exam_code" id="editExamCode" class="form-control" required>
                    </div>
                    <div class="row g-2 mb-3">
                        <div class="col-6">
                            <label class="form-label fw-semibold">Academic Session</label>
                            <select name="session_id" id="editExamSession" class="form-select" required>
                                <c:forEach var="ses" items="${sessions}">
                                    <option value="${ses.id}">${ses.sessionCode}</option>
                                </c:forEach>
                            </select>
                        </div>
                        <div class="col-6">
                            <label class="form-label fw-semibold">Semester Number</label>
                            <input type="number" name="semester_number" id="editExamSemester" class="form-control" min="1" max="12" required>
                        </div>
                    </div>
                    <div class="row g-2 mb-3">
                        <div class="col-6">
                            <label class="form-label fw-semibold">Start Date</label>
                            <input type="date" name="start_date" id="editExamStartDate" class="form-control" required>
                        </div>
                        <div class="col-6">
                            <label class="form-label fw-semibold">End Date</label>
                            <input type="date" name="end_date" id="editExamEndDate" class="form-control" required>
                        </div>
                    </div>
                    <div class="row g-2 mb-3">
                        <div class="col-6">
                            <label class="form-label fw-semibold">Exam Type</label>
                            <select name="exam_type" id="editExamType" class="form-select">
                                <option value="REGULAR">REGULAR</option>
                                <option value="BACKLOG">BACKLOG</option>
                                <option value="SPECIAL">SPECIAL</option>
                            </select>
                        </div>
                        <div class="col-6">
                            <label class="form-label fw-semibold">Exam Fee (INR)</label>
                            <input type="number" step="50" name="fee_amount" id="editExamFee" class="form-control" required>
                        </div>
                    </div>
                    <div class="mb-3">
                        <label class="form-label fw-semibold">Status</label>
                        <select name="status" id="editExamStatus" class="form-select">
                            <option value="ACTIVE">ACTIVE</option>
                            <option value="FORM_FILLUP">FORM FILLUP OPEN</option>
                            <option value="UPCOMING">UPCOMING</option>
                            <option value="CLOSED">CLOSED</option>
                        </select>
                    </div>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancel</button>
                    <button type="submit" class="btn btn-primary">Update Examination</button>
                </div>
            </form>
        </div>
    </div>
</div>

<!-- Modal: Add Schedule Slot -->
<c:if test="${not empty selectedExam}">
<div class="modal fade" id="addScheduleModal" tabindex="-1">
    <div class="modal-dialog">
        <div class="modal-content rounded-4 border-0 shadow">
            <div class="modal-header bg-primary text-white">
                <h5 class="modal-title fw-bold">Add Examination Routine Slot</h5>
                <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal"></button>
            </div>
            <form action="${pageContext.request.contextPath}/admin/exam-action" method="POST">
                <input type="hidden" name="action" value="add_schedule">
                <input type="hidden" name="exam_id" value="${selectedExam.id}">
                <div class="modal-body p-4">
                    <div class="mb-3">
                        <label class="form-label fw-semibold">Subject</label>
                        <select name="subject_id" class="form-select" required>
                            <c:forEach var="sub" items="${subjects}">
                                <option value="${sub.id}">${sub.subjectCode} - ${sub.name}</option>
                            </c:forEach>
                        </select>
                    </div>
                    <div class="mb-3">
                        <label class="form-label fw-semibold">Exam Date</label>
                        <input type="date" name="exam_date" class="form-control" required>
                    </div>
                    <div class="row g-2 mb-3">
                        <div class="col-6">
                            <label class="form-label fw-semibold">Start Time</label>
                            <input type="time" name="start_time" class="form-control" value="10:00" required>
                        </div>
                        <div class="col-6">
                            <label class="form-label fw-semibold">End Time</label>
                            <input type="time" name="end_time" class="form-control" value="13:00" required>
                        </div>
                    </div>
                    <div class="mb-3">
                        <label class="form-label fw-semibold">Room / Exam Hall</label>
                        <input type="text" name="room_no" class="form-control" value="Examination Hall 101" required>
                    </div>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancel</button>
                    <button type="submit" class="btn btn-primary">Save Slot</button>
                </div>
            </form>
        </div>
    </div>
</div>

<!-- Modal: Edit Schedule Slot -->
<div class="modal fade" id="editScheduleModal" tabindex="-1">
    <div class="modal-dialog">
        <div class="modal-content rounded-4 border-0 shadow">
            <div class="modal-header bg-primary text-white">
                <h5 class="modal-title fw-bold">Edit Examination Routine Slot</h5>
                <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal"></button>
            </div>
            <form action="${pageContext.request.contextPath}/admin/exam-action" method="POST">
                <input type="hidden" name="action" value="edit_schedule">
                <input type="hidden" name="schedule_id" id="editScheduleId">
                <input type="hidden" name="exam_id" value="${selectedExam.id}">
                <div class="modal-body p-4">
                    <div class="mb-3">
                        <label class="form-label fw-semibold">Subject</label>
                        <select name="subject_id" id="editScheduleSubject" class="form-select" required>
                            <c:forEach var="sub" items="${subjects}">
                                <option value="${sub.id}">${sub.subjectCode} - ${sub.name}</option>
                            </c:forEach>
                        </select>
                    </div>
                    <div class="mb-3">
                        <label class="form-label fw-semibold">Exam Date</label>
                        <input type="date" name="exam_date" id="editScheduleDate" class="form-control" required>
                    </div>
                    <div class="row g-2 mb-3">
                        <div class="col-6">
                            <label class="form-label fw-semibold">Start Time</label>
                            <input type="time" name="start_time" id="editScheduleStart" class="form-control" required>
                        </div>
                        <div class="col-6">
                            <label class="form-label fw-semibold">End Time</label>
                            <input type="time" name="end_time" id="editScheduleEnd" class="form-control" required>
                        </div>
                    </div>
                    <div class="mb-3">
                        <label class="form-label fw-semibold">Room / Exam Hall</label>
                        <input type="text" name="room_no" id="editScheduleRoom" class="form-control" required>
                    </div>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancel</button>
                    <button type="submit" class="btn btn-primary">Update Slot</button>
                </div>
            </form>
        </div>
    </div>
</div>

<!-- Modal: Edit Exam Application Form -->
<div class="modal fade" id="editFormModal" tabindex="-1">
    <div class="modal-dialog">
        <div class="modal-content rounded-4 border-0 shadow">
            <div class="modal-header bg-primary text-white">
                <h5 class="modal-title fw-bold">Edit Examination Application Form</h5>
                <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal"></button>
            </div>
            <form action="${pageContext.request.contextPath}/admin/exam-action" method="POST">
                <input type="hidden" name="action" value="edit_form">
                <input type="hidden" name="form_id" id="editFormId">
                <input type="hidden" name="exam_id" value="${selectedExam.id}">
                <div class="modal-body p-4">
                    <div class="alert alert-light border py-2 px-3 mb-3 small">
                        <strong>Student: </strong><span id="editFormStudentInfo"></span>
                    </div>
                    <div class="row g-2 mb-3">
                        <div class="col-6">
                            <label class="form-label fw-semibold">College Approval</label>
                            <select name="college_approval_status" id="editFormCollegeStatus" class="form-select">
                                <option value="PENDING">PENDING</option>
                                <option value="APPROVED">APPROVED</option>
                                <option value="REJECTED">REJECTED</option>
                            </select>
                        </div>
                        <div class="col-6">
                            <label class="form-label fw-semibold">Admin Approval</label>
                            <select name="admin_approval_status" id="editFormAdminStatus" class="form-select">
                                <option value="PENDING">PENDING</option>
                                <option value="APPROVED">APPROVED</option>
                                <option value="REJECTED">REJECTED</option>
                            </select>
                        </div>
                    </div>
                    <div class="row g-2 mb-3">
                        <div class="col-6">
                            <label class="form-label fw-semibold">Payment Status</label>
                            <select name="payment_status" id="editFormPaymentStatus" class="form-select">
                                <option value="PENDING">PENDING</option>
                                <option value="PAID">PAID</option>
                                <option value="FAILED">FAILED</option>
                                <option value="WAIVED">WAIVED</option>
                            </select>
                        </div>
                        <div class="col-6 d-flex align-items-center pt-4">
                            <div class="form-check form-switch">
                                <input class="form-check-input" type="checkbox" name="admit_card_issued" value="1" id="editFormAdmitCard">
                                <label class="form-check-label fw-semibold" for="editFormAdmitCard">Admit Card Issued</label>
                            </div>
                        </div>
                    </div>
                    <div class="mb-3">
                        <label class="form-label fw-semibold">Rejection Reason / Remark</label>
                        <textarea name="rejection_reason" id="editFormRejectionReason" class="form-control" rows="2" placeholder="Optional remark or reason for rejection..."></textarea>
                    </div>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancel</button>
                    <button type="submit" class="btn btn-primary">Update Application</button>
                </div>
            </form>
        </div>
    </div>
</div>
</c:if>

<script>
function populateEditExam(elem) {
    if (!elem) return;
    const d = elem.dataset;
    document.getElementById('editExamId').value = d.id || '';
    document.getElementById('editExamName').value = d.name || '';
    document.getElementById('editExamCode').value = d.code || '';
    document.getElementById('editExamSession').value = d.session || '';
    document.getElementById('editExamSemester').value = d.semester || '';
    document.getElementById('editExamStartDate').value = d.start || '';
    document.getElementById('editExamEndDate').value = d.end || '';
    document.getElementById('editExamType').value = d.type || 'REGULAR';
    document.getElementById('editExamFee').value = d.fee || '0';
    document.getElementById('editExamStatus').value = d.status || 'ACTIVE';
    new bootstrap.Modal(document.getElementById('editExamModal')).show();
}

function populateEditSchedule(elem) {
    if (!elem) return;
    const d = elem.dataset;
    document.getElementById('editScheduleId').value = d.id || '';
    document.getElementById('editScheduleSubject').value = d.subject || '';
    document.getElementById('editScheduleDate').value = d.date || '';
    let start = d.start || '';
    if (start.length > 5) start = start.substring(0, 5);
    let end = d.end || '';
    if (end.length > 5) end = end.substring(0, 5);
    document.getElementById('editScheduleStart').value = start;
    document.getElementById('editScheduleEnd').value = end;
    document.getElementById('editScheduleRoom').value = d.room || '';
    new bootstrap.Modal(document.getElementById('editScheduleModal')).show();
}

function populateEditForm(elem) {
    if (!elem) return;
    const d = elem.dataset;
    document.getElementById('editFormId').value = d.id || '';
    const infoElem = document.getElementById('editFormStudentInfo');
    if (infoElem) {
        infoElem.textContent = (d.name || '') + ' (' + (d.reg || '') + ')';
    }
    document.getElementById('editFormCollegeStatus').value = d.college || 'PENDING';
    document.getElementById('editFormAdminStatus').value = d.admin || 'PENDING';
    document.getElementById('editFormPaymentStatus').value = d.payment || 'PENDING';
    const admitCheckbox = document.getElementById('editFormAdmitCard');
    if (admitCheckbox) {
        admitCheckbox.checked = (d.admit === 'true' || d.admit === '1');
    }
    document.getElementById('editFormRejectionReason').value = d.reason || '';
    new bootstrap.Modal(document.getElementById('editFormModal')).show();
}
</script>

<jsp:include page="/WEB-INF/views/common/footer.jsp" />
