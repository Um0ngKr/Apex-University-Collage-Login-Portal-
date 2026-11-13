<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<jsp:include page="/WEB-INF/views/common/header.jsp">
    <jsp:param name="pageTitle" value="Student Management • Apex University" />
</jsp:include>

<div class="dashboard-wrapper">
    <jsp:include page="/WEB-INF/views/common/sidebar.jsp">
        <jsp:param name="activeNav" value="students" />
    </jsp:include>

    <main class="dashboard-content">
        <div class="container-fluid">
            
            <jsp:include page="/WEB-INF/views/common/alerts.jsp" />

            <!-- Page Header -->
            <div class="d-flex justify-content-between align-items-center mb-4 flex-wrap gap-2">
                <div>
                    <h2 class="h3 fw-bold mb-1">Student Management</h2>
                    <p class="text-secondary small mb-0">Enroll scholars, assign registration and roll numbers, manage academic profiles, and audit records.</p>
                </div>
                <div class="d-flex gap-2">
                    <a href="${pageContext.request.contextPath}/export/csv?type=students" class="btn btn-outline-success btn-sm">
                        <i class="bi bi-file-earmark-spreadsheet me-1"></i> Export CSV
                    </a>
                    <button type="button" class="btn btn-apex-primary btn-sm" data-bs-toggle="modal" data-bs-target="#addStudentModal">
                        <i class="bi bi-person-plus me-1"></i> Add New Student
                    </button>
                </div>
            </div>

            <!-- Filters Bar -->
            <div class="card shadow-sm border rounded-4 p-3 mb-4">
                <form action="${pageContext.request.contextPath}/admin/students" method="GET" class="row g-2 align-items-center">
                    <div class="col-md-5 col-12">
                        <div class="input-group">
                            <span class="input-group-text"><i class="bi bi-search"></i></span>
                            <input type="text" name="search" class="form-control" placeholder="Search by name, registration no, roll no, email..." value="${currentSearch}">
                        </div>
                    </div>
                    <div class="col-md-5 col-12">
                        <select name="college_id" class="form-select">
                            <option value="">-- All Affiliated Colleges --</option>
                            <c:forEach var="c" items="${colleges}">
                                <option value="${c.id}" ${selectedCollegeId == c.id ? 'selected' : ''}>${c.name} (${c.collegeCode})</option>
                            </c:forEach>
                        </select>
                    </div>
                    <div class="col-md-2 col-12 d-flex gap-2">
                        <button type="submit" class="btn btn-primary w-100">Filter</button>
                        <a href="${pageContext.request.contextPath}/admin/students" class="btn btn-outline-secondary">Reset</a>
                    </div>
                </form>
            </div>

            <!-- Students Table -->
            <div class="card shadow-sm border rounded-4 overflow-hidden mb-4">
                <div class="table-responsive">
                    <table class="table table-hover align-middle mb-0 apex-table" id="studentsTable">
                        <thead>
                            <tr>
                                <th>Reg No.</th>
                                <th>Roll No.</th>
                                <th>Student Name</th>
                                <th>College</th>
                                <th>Degree / Course</th>
                                <th>Semester</th>
                                <th>Status</th>
                                <th class="text-center">Actions</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach var="s" items="${students}">
                                <tr>
                                    <td class="fw-bold text-primary">${s.registrationNo}</td>
                                    <td class="fw-semibold">${s.rollNo}</td>
                                    <td>
                                        <div class="fw-bold">${s.fullName}</div>
                                        <small class="text-muted"><i class="bi bi-envelope"></i> ${s.email} | <i class="bi bi-telephone"></i> ${s.phone}</small>
                                    </td>
                                    <td><small class="fw-semibold">${s.collegeName}</small></td>
                                    <td><span class="badge bg-light text-dark border">${s.courseName}</span></td>
                                    <td><span class="badge bg-secondary">Semester ${s.semesterNumber}</span></td>
                                    <td>
                                        <span class="badge ${s.status == 'ACTIVE' ? 'bg-success' : 'bg-danger'}">${s.status}</span>
                                    </td>
                                    <td class="text-center">
                                        <div class="btn-group btn-group-sm">
                                            <button type="button" class="btn btn-outline-primary" 
                                                    onclick="populateEditStudent(${s.id}, '${s.registrationNo}', '${s.rollNo}', '${s.fullName}', '${s.email}', '${s.phone}', '${s.dob}', '${s.gender}', '${s.bloodGroup}', '${s.fatherName}', '${s.motherName}', '${s.address}', ${s.collegeId}, ${s.departmentId}, ${s.courseId}, ${s.currentSemesterId}, ${s.sessionId}, '${s.status}')">
                                                <i class="bi bi-pencil-square"></i>
                                            </button>
                                            <form action="${pageContext.request.contextPath}/admin/student-action" method="POST" class="d-inline">
                                                <input type="hidden" name="action" value="delete">
                                                <input type="hidden" name="id" value="${s.id}">
                                                <button type="submit" class="btn btn-outline-danger btn-confirm-delete" data-confirm="Are you sure you want to permanently delete student ${s.fullName}?">
                                                    <i class="bi bi-trash"></i>
                                                </button>
                                            </form>
                                        </div>
                                    </td>
                                </tr>
                            </c:forEach>
                            <c:if test="${empty students}">
                                <tr>
                                    <td colspan="8" class="text-center py-5 text-muted">
                                        <i class="bi bi-inbox fs-2 d-block mb-2"></i>
                                        No student records matching query. Click "Add New Student" above.
                                    </td>
                                </tr>
                            </c:if>
                        </tbody>
                    </table>
                </div>
            </div>

        </div>
    </main>
</div>

<!-- Add Student Modal -->
<div class="modal fade" id="addStudentModal" tabindex="-1">
    <div class="modal-dialog modal-lg">
        <div class="modal-content rounded-4 border-0 shadow">
            <div class="modal-header bg-primary text-white">
                <h5 class="modal-title fw-bold"><i class="bi bi-person-plus me-1"></i> Enroll New Student</h5>
                <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal"></button>
            </div>
            <form action="${pageContext.request.contextPath}/admin/student-action" method="POST">
                <input type="hidden" name="action" value="add">
                <div class="modal-body p-4">
                    <div class="row g-3">
                        <div class="col-md-6 col-12">
                            <label class="form-label fw-semibold">Full Name <span class="text-danger">*</span></label>
                            <input type="text" name="full_name" class="form-control" placeholder="Student's complete name" required>
                        </div>
                        <div class="col-md-6 col-12">
                            <label class="form-label fw-semibold">Email Address <span class="text-danger">*</span></label>
                            <input type="email" name="email" class="form-control" placeholder="student@example.com" required>
                        </div>
                        <div class="col-md-6 col-12">
                            <label class="form-label fw-semibold">Phone Number <span class="text-danger">*</span></label>
                            <input type="text" name="phone" class="form-control" placeholder="10-digit mobile number" required>
                        </div>
                        <div class="col-md-3 col-6">
                            <label class="form-label fw-semibold">Date of Birth</label>
                            <input type="date" name="dob" class="form-control">
                        </div>
                        <div class="col-md-3 col-6">
                            <label class="form-label fw-semibold">Gender</label>
                            <select name="gender" class="form-select">
                                <option value="MALE">Male</option>
                                <option value="FEMALE">Female</option>
                                <option value="OTHER">Other</option>
                            </select>
                        </div>
                        <div class="col-md-4 col-12">
                            <label class="form-label fw-semibold">Blood Group</label>
                            <input type="text" name="blood_group" class="form-control" placeholder="e.g. O+, B+">
                        </div>
                        <div class="col-md-4 col-12">
                            <label class="form-label fw-semibold">Father's Name</label>
                            <input type="text" name="father_name" class="form-control">
                        </div>
                        <div class="col-md-4 col-12">
                            <label class="form-label fw-semibold">Mother's Name</label>
                            <input type="text" name="mother_name" class="form-control">
                        </div>
                        <div class="col-12">
                            <label class="form-label fw-semibold">Permanent Address</label>
                            <textarea name="address" class="form-control" rows="2"></textarea>
                        </div>

                        <!-- Academic Allocations -->
                        <div class="col-md-6 col-12">
                            <label class="form-label fw-semibold">Affiliated College <span class="text-danger">*</span></label>
                            <select name="college_id" class="form-select" required>
                                <c:forEach var="c" items="${colleges}">
                                    <option value="${c.id}">${c.name}</option>
                                </c:forEach>
                            </select>
                        </div>
                        <div class="col-md-6 col-12">
                            <label class="form-label fw-semibold">Department <span class="text-danger">*</span></label>
                            <select name="department_id" class="form-select" required>
                                <c:forEach var="d" items="${departments}">
                                    <option value="${d.id}">${d.name} (${d.deptCode})</option>
                                </c:forEach>
                            </select>
                        </div>
                        <div class="col-md-6 col-12">
                            <label class="form-label fw-semibold">Academic Course <span class="text-danger">*</span></label>
                            <select name="course_id" class="form-select" required>
                                <c:forEach var="co" items="${courses}">
                                    <option value="${co.id}">${co.name} (${co.courseCode})</option>
                                </c:forEach>
                            </select>
                        </div>
                        <div class="col-md-3 col-6">
                            <label class="form-label fw-semibold">Semester <span class="text-danger">*</span></label>
                            <select name="semester_id" class="form-select" required>
                                <c:forEach var="sem" items="${semesters}">
                                    <option value="${sem.id}">${sem.courseName} - Semester ${sem.semesterNumber}</option>
                                </c:forEach>
                            </select>
                        </div>
                        <div class="col-md-3 col-6">
                            <label class="form-label fw-semibold">Session <span class="text-danger">*</span></label>
                            <select name="session_id" class="form-select" required>
                                <c:forEach var="ses" items="${sessions}">
                                    <option value="${ses.id}">${ses.sessionCode}</option>
                                </c:forEach>
                            </select>
                        </div>

                        <div class="col-md-6 col-12">
                            <label class="form-label fw-semibold">Registration Number <span class="text-muted small">(Leave empty for auto-generation)</span></label>
                            <input type="text" name="registration_no" class="form-control" placeholder="Auto-generated if empty">
                        </div>
                        <div class="col-md-6 col-12">
                            <label class="form-label fw-semibold">Roll Number <span class="text-muted small">(Leave empty for auto-generation)</span></label>
                            <input type="text" name="roll_no" class="form-control" placeholder="Auto-generated if empty">
                        </div>
                    </div>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancel</button>
                    <button type="submit" class="btn btn-apex-primary">Confirm &amp; Enroll</button>
                </div>
            </form>
        </div>
    </div>
</div>

<!-- Edit Student Modal -->
<div class="modal fade" id="editStudentModal" tabindex="-1">
    <div class="modal-dialog modal-lg">
        <div class="modal-content rounded-4 border-0 shadow">
            <div class="modal-header bg-dark text-white">
                <h5 class="modal-title fw-bold"><i class="bi bi-pencil-square me-1"></i> Edit Student Profile</h5>
                <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal"></button>
            </div>
            <form action="${pageContext.request.contextPath}/admin/student-action" method="POST">
                <input type="hidden" name="action" value="edit">
                <input type="hidden" name="id" id="editStudentId">
                <div class="modal-body p-4">
                    <div class="row g-3">
                        <div class="col-md-6 col-12">
                            <label class="form-label fw-semibold">Registration Number</label>
                            <input type="text" name="registration_no" id="editRegNo" class="form-control" required>
                        </div>
                        <div class="col-md-6 col-12">
                            <label class="form-label fw-semibold">Roll Number</label>
                            <input type="text" name="roll_no" id="editRollNo" class="form-control" required>
                        </div>
                        <div class="col-md-6 col-12">
                            <label class="form-label fw-semibold">Full Name</label>
                            <input type="text" name="full_name" id="editFullName" class="form-control" required>
                        </div>
                        <div class="col-md-6 col-12">
                            <label class="form-label fw-semibold">Email</label>
                            <input type="email" name="email" id="editEmail" class="form-control" required>
                        </div>
                        <div class="col-md-6 col-12">
                            <label class="form-label fw-semibold">Phone</label>
                            <input type="text" name="phone" id="editPhone" class="form-control" required>
                        </div>
                        <div class="col-md-3 col-6">
                            <label class="form-label fw-semibold">DOB</label>
                            <input type="date" name="dob" id="editDob" class="form-control">
                        </div>
                        <div class="col-md-3 col-6">
                            <label class="form-label fw-semibold">Gender</label>
                            <select name="gender" id="editGender" class="form-select">
                                <option value="MALE">Male</option>
                                <option value="FEMALE">Female</option>
                                <option value="OTHER">Other</option>
                            </select>
                        </div>
                        <div class="col-md-4 col-12">
                            <label class="form-label fw-semibold">Blood Group</label>
                            <input type="text" name="blood_group" id="editBloodGroup" class="form-control">
                        </div>
                        <div class="col-md-4 col-12">
                            <label class="form-label fw-semibold">Father's Name</label>
                            <input type="text" name="father_name" id="editFatherName" class="form-control">
                        </div>
                        <div class="col-md-4 col-12">
                            <label class="form-label fw-semibold">Mother's Name</label>
                            <input type="text" name="mother_name" id="editMotherName" class="form-control">
                        </div>
                        <div class="col-12">
                            <label class="form-label fw-semibold">Address</label>
                            <textarea name="address" id="editAddress" class="form-control" rows="2"></textarea>
                        </div>
                        <div class="col-md-4 col-12">
                            <label class="form-label fw-semibold">College</label>
                            <select name="college_id" id="editCollegeId" class="form-select">
                                <c:forEach var="c" items="${colleges}">
                                    <option value="${c.id}">${c.name}</option>
                                </c:forEach>
                            </select>
                        </div>
                        <div class="col-md-4 col-12">
                            <label class="form-label fw-semibold">Department</label>
                            <select name="department_id" id="editDeptId" class="form-select">
                                <c:forEach var="d" items="${departments}">
                                    <option value="${d.id}">${d.name}</option>
                                </c:forEach>
                            </select>
                        </div>
                        <div class="col-md-4 col-12">
                            <label class="form-label fw-semibold">Course</label>
                            <select name="course_id" id="editCourseId" class="form-select">
                                <c:forEach var="co" items="${courses}">
                                    <option value="${co.id}">${co.name}</option>
                                </c:forEach>
                            </select>
                        </div>
                        <div class="col-md-4 col-12">
                            <label class="form-label fw-semibold">Semester</label>
                            <select name="semester_id" id="editSemesterId" class="form-select">
                                <c:forEach var="sem" items="${semesters}">
                                    <option value="${sem.id}">${sem.courseName} - Sem ${sem.semesterNumber}</option>
                                </c:forEach>
                            </select>
                        </div>
                        <div class="col-md-4 col-12">
                            <label class="form-label fw-semibold">Session</label>
                            <select name="session_id" id="editSessionId" class="form-select">
                                <c:forEach var="ses" items="${sessions}">
                                    <option value="${ses.id}">${ses.sessionCode}</option>
                                </c:forEach>
                            </select>
                        </div>
                        <div class="col-md-4 col-12">
                            <label class="form-label fw-semibold">Status</label>
                            <select name="status" id="editStatus" class="form-select">
                                <option value="ACTIVE">ACTIVE</option>
                                <option value="INACTIVE">INACTIVE</option>
                                <option value="SUSPENDED">SUSPENDED</option>
                            </select>
                        </div>
                    </div>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancel</button>
                    <button type="submit" class="btn btn-apex-primary">Save Changes</button>
                </div>
            </form>
        </div>
    </div>
</div>

<script>
function populateEditStudent(id, regNo, rollNo, name, email, phone, dob, gender, bg, father, mother, addr, colId, deptId, courseId, semId, sesId, status) {
    document.getElementById('editStudentId').value = id;
    document.getElementById('editRegNo').value = regNo;
    document.getElementById('editRollNo').value = rollNo;
    document.getElementById('editFullName').value = name;
    document.getElementById('editEmail').value = email;
    document.getElementById('editPhone').value = phone;
    document.getElementById('editDob').value = dob;
    document.getElementById('editGender').value = gender;
    document.getElementById('editBloodGroup').value = bg;
    document.getElementById('editFatherName').value = father;
    document.getElementById('editMotherName').value = mother;
    document.getElementById('editAddress').value = addr;
    document.getElementById('editCollegeId').value = colId;
    document.getElementById('editDeptId').value = deptId;
    document.getElementById('editCourseId').value = courseId;
    document.getElementById('editSemesterId').value = semId;
    document.getElementById('editSessionId').value = sesId;
    document.getElementById('editStatus').value = status;

    new bootstrap.Modal(document.getElementById('editStudentModal')).show();
}
</script>

<jsp:include page="/WEB-INF/views/common/footer.jsp" />
