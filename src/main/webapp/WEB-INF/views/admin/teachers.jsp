<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<jsp:include page="/WEB-INF/views/common/header.jsp">
    <jsp:param name="pageTitle" value="Teacher Management • Apex University" />
</jsp:include>

<div class="dashboard-wrapper">
    <jsp:include page="/WEB-INF/views/common/sidebar.jsp">
        <jsp:param name="activeNav" value="teachers" />
    </jsp:include>

    <main class="dashboard-content">
        <div class="container-fluid">
            
            <jsp:include page="/WEB-INF/views/common/alerts.jsp" />

            <div class="d-flex justify-content-between align-items-center mb-4 flex-wrap gap-2">
                <div>
                    <h2 class="h3 fw-bold mb-1">Teacher Management</h2>
                    <p class="text-secondary small mb-0">Manage faculty members, assign subject classes, academic courses, and monitor evaluations.</p>
                </div>
                <button type="button" class="btn btn-apex-primary btn-sm" data-bs-toggle="modal" data-bs-target="#addTeacherModal">
                    <i class="bi bi-person-plus me-1"></i> Add New Faculty
                </button>
            </div>

            <!-- Teachers List Table -->
            <div class="card shadow-sm border rounded-4 overflow-hidden mb-4">
                <div class="table-responsive">
                    <table class="table table-hover align-middle mb-0 apex-table apex-table-compact">
                        <thead>
                            <tr>
                                <th style="width: 110px;">Employee ID</th>
                                <th style="min-width: 170px;">Faculty Name</th>
                                <th style="width: 140px;">Designation</th>
                                <th style="width: 160px;">Department</th>
                                <th style="min-width: 180px;">Contact Information</th>
                                <th class="text-center" style="width: 90px;">Status</th>
                                <th class="text-center text-nowrap" style="width: 195px; min-width: 195px;">Actions</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach var="t" items="${teachers}">
                                <tr>
                                    <td class="fw-bold text-success text-nowrap">${t.employeeId}</td>
                                    <td>
                                        <div class="fw-bold">${t.fullName}</div>
                                        <div class="text-muted small text-wrap lh-sm">${t.qualification}</div>
                                    </td>
                                    <td><span class="badge bg-light text-dark border text-wrap">${t.designation}</span></td>
                                    <td><span class="badge bg-secondary text-wrap">${t.departmentName}</span></td>
                                    <td>
                                        <div class="small text-break"><i class="bi bi-envelope me-1"></i>${t.email}</div>
                                        <div class="small text-nowrap"><i class="bi bi-telephone me-1"></i>${t.phone}</div>
                                    </td>
                                    <td class="text-center">
                                        <span class="badge ${t.status == 'ACTIVE' ? 'bg-success' : 'bg-danger'}">${t.status}</span>
                                    </td>
                                    <td class="text-center text-nowrap">
                                        <div class="teacher-actions">
                                            <a href="${pageContext.request.contextPath}/admin/teachers?view_id=${t.id}" class="btn btn-sm btn-outline-info" title="Assign Subjects">
                                                <i class="bi bi-journal-plus me-1"></i> Assignments
                                            </a>
                                            <button type="button" class="btn btn-sm btn-outline-primary btn-icon-only"
                                                    data-id="${t.id}"
                                                    data-empid="<c:out value='${t.employeeId}' />"
                                                    data-name="<c:out value='${t.fullName}' />"
                                                    data-email="<c:out value='${t.email}' />"
                                                    data-phone="<c:out value='${t.phone}' />"
                                                    data-designation="<c:out value='${t.designation}' />"
                                                    data-dept="${t.departmentId}"
                                                    data-qualification="<c:out value='${t.qualification}' />"
                                                    data-status="${t.status}"
                                                    onclick="populateEditTeacher(this)"
                                                    title="Edit Faculty">
                                                <i class="bi bi-pencil-square"></i>
                                            </button>
                                            <form action="${pageContext.request.contextPath}/admin/teacher-action" method="POST">
                                                <input type="hidden" name="action" value="delete">
                                                <input type="hidden" name="id" value="${t.id}">
                                                <button type="submit" class="btn btn-sm btn-outline-danger btn-icon-only btn-confirm-delete" data-confirm="Delete teacher ${t.fullName}?" title="Delete Faculty">
                                                    <i class="bi bi-trash"></i>
                                                </button>
                                            </form>
                                        </div>
                                    </td>
                                </tr>
                            </c:forEach>
                            <c:if test="${empty teachers}">
                                <tr>
                                    <td colspan="7" class="text-center py-4 text-muted">No faculty members found. Click "Add New Faculty" above.</td>
                                </tr>
                            </c:if>
                        </tbody>
                    </table>
                </div>
            </div>

            <!-- Assigned Subjects Panel (When view_id is active) -->
            <c:if test="${not empty selectedTeacher}">
                <div class="card shadow-sm border border-info rounded-4 p-4 mb-4">
                    <div class="d-flex justify-content-between align-items-center mb-3">
                        <h4 class="h5 fw-bold text-info mb-0">
                            <i class="bi bi-journal-bookmark-fill me-1"></i> Assigned Subjects for ${selectedTeacher.fullName} (${selectedTeacher.employeeId})
                        </h4>
                        <button type="button" class="btn btn-primary btn-sm" data-bs-toggle="modal" data-bs-target="#assignSubjectModal">
                            <i class="bi bi-plus-lg me-1"></i> Assign New Subject
                        </button>
                    </div>

                    <div class="table-responsive">
                        <table class="table table-bordered align-middle small mb-0">
                            <thead class="table-light">
                                <tr>
                                    <th>Subject Code</th>
                                    <th>Subject Title</th>
                                    <th>Course</th>
                                    <th>Semester</th>
                                    <th>Session</th>
                                    <th>Section</th>
                                    <th class="text-center">Action</th>
                                </tr>
                            </thead>
                            <tbody>
                                <c:forEach var="asg" items="${assignments}">
                                    <tr>
                                        <td class="fw-bold">${asg.subjectCode}</td>
                                        <td>${asg.subjectName}</td>
                                        <td>${asg.courseName}</td>
                                        <td>Semester ${asg.semesterNumber}</td>
                                        <td>${asg.sessionCode}</td>
                                        <td>Section ${asg.section}</td>
                                        <td class="text-center">
                                            <form action="${pageContext.request.contextPath}/admin/teacher-action" method="POST" class="d-inline">
                                                <input type="hidden" name="action" value="remove_assignment">
                                                <input type="hidden" name="assignment_id" value="${asg.assignmentId}">
                                                <input type="hidden" name="teacher_id" value="${selectedTeacher.id}">
                                                <button type="submit" class="btn btn-sm btn-outline-danger">
                                                    <i class="bi bi-x-circle me-1"></i> Remove
                                                </button>
                                            </form>
                                        </td>
                                    </tr>
                                </c:forEach>
                                <c:if test="${empty assignments}">
                                    <tr><td colspan="7" class="text-center py-3 text-muted">No classes currently assigned to this faculty.</td></tr>
                                </c:if>
                            </tbody>
                        </table>
                    </div>
                </div>
            </c:if>

        </div>
    </main>
</div>

<!-- Add Teacher Modal -->
<div class="modal fade" id="addTeacherModal" tabindex="-1">
    <div class="modal-dialog">
        <div class="modal-content rounded-4 border-0 shadow">
            <div class="modal-header bg-success text-white">
                <h5 class="modal-title fw-bold"><i class="bi bi-person-plus me-1"></i> Register New Faculty</h5>
                <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal"></button>
            </div>
            <form action="${pageContext.request.contextPath}/admin/teacher-action" method="POST">
                <input type="hidden" name="action" value="add">
                <div class="modal-body p-4">
                    <div class="row g-3">
                        <div class="col-12">
                            <label class="form-label fw-semibold">Employee ID <span class="text-muted small">(Leave blank for auto-generation)</span></label>
                            <input type="text" name="employee_id" class="form-control" placeholder="e.g. TCH105">
                        </div>
                        <div class="col-12">
                            <label class="form-label fw-semibold">Faculty Full Name <span class="text-danger">*</span></label>
                            <input type="text" name="full_name" class="form-control" placeholder="Prof. / Dr. ..." required>
                        </div>
                        <div class="col-md-6 col-12">
                            <label class="form-label fw-semibold">Email <span class="text-danger">*</span></label>
                            <input type="email" name="email" class="form-control" required>
                        </div>
                        <div class="col-md-6 col-12">
                            <label class="form-label fw-semibold">Phone <span class="text-danger">*</span></label>
                            <input type="text" name="phone" class="form-control" required>
                        </div>
                        <div class="col-md-6 col-12">
                            <label class="form-label fw-semibold">Designation</label>
                            <input type="text" name="designation" class="form-control" placeholder="Assistant Professor, HOD..." required>
                        </div>
                        <div class="col-md-6 col-12">
                            <label class="form-label fw-semibold">Department <span class="text-danger">*</span></label>
                            <select name="department_id" class="form-select" required>
                                <c:forEach var="d" items="${departments}">
                                    <option value="${d.id}">${d.name}</option>
                                </c:forEach>
                            </select>
                        </div>
                        <div class="col-12">
                            <label class="form-label fw-semibold">Qualification</label>
                            <input type="text" name="qualification" class="form-control" placeholder="e.g. M.Tech, Ph.D. in Computer Science">
                        </div>
                    </div>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancel</button>
                    <button type="submit" class="btn btn-success">Save Faculty</button>
                </div>
            </form>
        </div>
    </div>
</div>

<!-- Assign Subject Modal -->
<c:if test="${not empty selectedTeacher}">
<div class="modal fade" id="assignSubjectModal" tabindex="-1">
    <div class="modal-dialog">
        <div class="modal-content rounded-4 border-0 shadow">
            <div class="modal-header bg-primary text-white">
                <h5 class="modal-title fw-bold">Assign Subject to ${selectedTeacher.fullName}</h5>
                <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal"></button>
            </div>
            <form action="${pageContext.request.contextPath}/admin/teacher-action" method="POST">
                <input type="hidden" name="action" value="assign">
                <input type="hidden" name="teacher_id" value="${selectedTeacher.id}">
                <div class="modal-body p-4">
                    <div class="row g-3">
                        <div class="col-12">
                            <label class="form-label fw-semibold">Course Program</label>
                            <select name="course_id" class="form-select" required>
                                <c:forEach var="c" items="${courses}">
                                    <option value="${c.id}">${c.name}</option>
                                </c:forEach>
                            </select>
                        </div>
                        <div class="col-md-6 col-12">
                            <label class="form-label fw-semibold">Semester</label>
                            <select name="semester_id" class="form-select" required>
                                <c:forEach var="sem" items="${semesters}">
                                    <option value="${sem.id}">${sem.courseName} - Sem ${sem.semesterNumber}</option>
                                </c:forEach>
                            </select>
                        </div>
                        <div class="col-md-6 col-12">
                            <label class="form-label fw-semibold">Academic Session</label>
                            <select name="session_id" class="form-select" required>
                                <c:forEach var="ses" items="${sessions}">
                                    <option value="${ses.id}">${ses.sessionCode}</option>
                                </c:forEach>
                            </select>
                        </div>
                        <div class="col-12">
                            <label class="form-label fw-semibold">Subject</label>
                            <select name="subject_id" class="form-select" required>
                                <c:forEach var="sub" items="${subjects}">
                                    <option value="${sub.id}">${sub.subjectCode} - ${sub.name}</option>
                                </c:forEach>
                            </select>
                        </div>
                        <div class="col-12">
                            <label class="form-label fw-semibold">Section</label>
                            <input type="text" name="section" class="form-control" value="A" required>
                        </div>
                    </div>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancel</button>
                    <button type="submit" class="btn btn-primary">Confirm Assignment</button>
                </div>
            </form>
        </div>
    </div>
</div>
</c:if>

<!-- Edit Teacher Modal -->
<div class="modal fade" id="editTeacherModal" tabindex="-1">
    <div class="modal-dialog">
        <div class="modal-content rounded-4 border-0 shadow">
            <div class="modal-header bg-primary text-white">
                <h5 class="modal-title fw-bold"><i class="bi bi-pencil-square me-1"></i> Edit Faculty</h5>
                <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal"></button>
            </div>
            <form action="${pageContext.request.contextPath}/admin/teacher-action" method="POST">
                <input type="hidden" name="action" value="edit">
                <input type="hidden" name="id" id="editTeacherId">
                <div class="modal-body p-4">
                    <div class="row g-3">
                        <div class="col-12">
                            <label class="form-label fw-semibold">Employee ID <span class="text-danger">*</span></label>
                            <input type="text" name="employee_id" id="editTeacherEmpId" class="form-control" required>
                        </div>
                        <div class="col-12">
                            <label class="form-label fw-semibold">Faculty Full Name <span class="text-danger">*</span></label>
                            <input type="text" name="full_name" id="editTeacherName" class="form-control" required>
                        </div>
                        <div class="col-md-6 col-12">
                            <label class="form-label fw-semibold">Email <span class="text-danger">*</span></label>
                            <input type="email" name="email" id="editTeacherEmail" class="form-control" required>
                        </div>
                        <div class="col-md-6 col-12">
                            <label class="form-label fw-semibold">Phone <span class="text-danger">*</span></label>
                            <input type="text" name="phone" id="editTeacherPhone" class="form-control" required>
                        </div>
                        <div class="col-md-6 col-12">
                            <label class="form-label fw-semibold">Designation <span class="text-danger">*</span></label>
                            <input type="text" name="designation" id="editTeacherDesignation" class="form-control" required>
                        </div>
                        <div class="col-md-6 col-12">
                            <label class="form-label fw-semibold">Department <span class="text-danger">*</span></label>
                            <select name="department_id" id="editTeacherDept" class="form-select" required>
                                <c:forEach var="d" items="${departments}">
                                    <option value="${d.id}">${d.name}</option>
                                </c:forEach>
                            </select>
                        </div>
                        <div class="col-12">
                            <label class="form-label fw-semibold">Qualification</label>
                            <input type="text" name="qualification" id="editTeacherQual" class="form-control">
                        </div>
                        <div class="col-12">
                            <label class="form-label fw-semibold">Status</label>
                            <select name="status" id="editTeacherStatus" class="form-select">
                                <option value="ACTIVE">ACTIVE</option>
                                <option value="INACTIVE">INACTIVE</option>
                            </select>
                        </div>
                    </div>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancel</button>
                    <button type="submit" class="btn btn-primary">Update Faculty</button>
                </div>
            </form>
        </div>
    </div>
</div>

<script>
function populateEditTeacher(elemOrId, empId, name, email, phone, desig, deptId, qual, status) {
    if (typeof elemOrId === 'object' && elemOrId !== null) {
        var d = elemOrId.dataset;
        document.getElementById('editTeacherId').value = d.id;
        document.getElementById('editTeacherEmpId').value = d.empid || '';
        document.getElementById('editTeacherName').value = d.name || '';
        document.getElementById('editTeacherEmail').value = d.email || '';
        document.getElementById('editTeacherPhone').value = d.phone || '';
        document.getElementById('editTeacherDesignation').value = d.designation || '';
        document.getElementById('editTeacherDept').value = d.dept || '';
        document.getElementById('editTeacherQual').value = d.qualification || '';
        document.getElementById('editTeacherStatus').value = d.status || 'ACTIVE';
    } else {
        document.getElementById('editTeacherId').value = elemOrId;
        document.getElementById('editTeacherEmpId').value = empId || '';
        document.getElementById('editTeacherName').value = name || '';
        document.getElementById('editTeacherEmail').value = email || '';
        document.getElementById('editTeacherPhone').value = phone || '';
        document.getElementById('editTeacherDesignation').value = desig || '';
        document.getElementById('editTeacherDept').value = deptId || '';
        document.getElementById('editTeacherQual').value = qual || '';
        document.getElementById('editTeacherStatus').value = status || 'ACTIVE';
    }
    new bootstrap.Modal(document.getElementById('editTeacherModal')).show();
}
</script>

<jsp:include page="/WEB-INF/views/common/footer.jsp" />
