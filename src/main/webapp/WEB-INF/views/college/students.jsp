<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<jsp:include page="/WEB-INF/views/common/header.jsp">
    <jsp:param name="pageTitle" value="College Student Roster • Apex University" />
</jsp:include>

<div class="dashboard-wrapper">
    <jsp:include page="/WEB-INF/views/common/sidebar.jsp">
        <jsp:param name="activeNav" value="students" />
    </jsp:include>

    <main class="dashboard-content">
        <div class="container-fluid">
            
            <jsp:include page="/WEB-INF/views/common/alerts.jsp" />

            <div class="d-flex justify-content-between align-items-center mb-4 flex-wrap gap-2">
                <div>
                    <h2 class="h3 fw-bold mb-1">Student Enrollment &amp; Verification</h2>
                    <p class="text-secondary small mb-0">Register admitted students, manage student profiles, and maintain college enrollment records.</p>
                </div>
                <div class="d-flex gap-2">
                    <a href="${pageContext.request.contextPath}/export/csv?type=students&college_id=${sessionScope.CURRENT_USER.collegeId}" class="btn btn-outline-success btn-sm">
                        <i class="bi bi-file-earmark-spreadsheet me-1"></i> Download Student List (CSV)
                    </a>
                    <button type="button" class="btn btn-apex-primary btn-sm" data-bs-toggle="modal" data-bs-target="#collegeAddStudentModal">
                        <i class="bi bi-person-plus me-1"></i> Register Student
                    </button>
                </div>
            </div>

            <!-- Search Filter -->
            <div class="card shadow-sm border rounded-4 p-3 mb-4">
                <form action="${pageContext.request.contextPath}/college/students" method="GET" class="row g-2 align-items-center">
                    <div class="col-md-10 col-12">
                        <div class="input-group">
                            <span class="input-group-text"><i class="bi bi-search"></i></span>
                            <input type="text" name="search" class="form-control" placeholder="Search college students by name, registration number, roll number...">
                        </div>
                    </div>
                    <div class="col-md-2 col-12">
                        <button type="submit" class="btn btn-primary w-100">Search</button>
                    </div>
                </form>
            </div>

            <!-- College Students Table -->
            <div class="card shadow-sm border rounded-4 overflow-hidden mb-4">
                <div class="table-responsive">
                    <table class="table table-hover align-middle mb-0 apex-table apex-table-compact">
                        <thead>
                            <tr>
                                <th>Registration No</th>
                                <th>Roll No</th>
                                <th>Student Name</th>
                                <th>Course / Program</th>
                                <th>Current Semester</th>
                                <th>Session</th>
                                <th>Status</th>
                                <th class="text-center text-nowrap" style="width: 100px; min-width: 100px;">Action</th>
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
                                    <td><span class="badge bg-light text-dark border">${s.courseName}</span></td>
                                    <td><span class="badge bg-secondary">Semester ${s.semesterNumber}</span></td>
                                    <td>${s.sessionCode}</td>
                                    <td>
                                        <span class="badge ${s.status == 'ACTIVE' ? 'bg-success' : 'bg-danger'}">${s.status}</span>
                                    </td>
                                    <td class="text-center text-nowrap">
                                        <button type="button" class="btn btn-sm btn-outline-primary"
                                                data-id="${s.id}"
                                                data-regno="<c:out value='${s.registrationNo}' />"
                                                data-rollno="<c:out value='${s.rollNo}' />"
                                                data-name="<c:out value='${s.fullName}' />"
                                                data-email="<c:out value='${s.email}' />"
                                                data-phone="<c:out value='${s.phone}' />"
                                                data-dob="${s.dob}"
                                                data-gender="${s.gender}"
                                                data-bloodgroup="<c:out value='${s.bloodGroup}' />"
                                                data-father="<c:out value='${s.fatherName}' />"
                                                data-mother="<c:out value='${s.motherName}' />"
                                                data-address="<c:out value='${s.address}' />"
                                                data-dept="${s.departmentId}"
                                                data-course="${s.courseId}"
                                                data-semester="${s.currentSemesterId}"
                                                data-session="${s.sessionId}"
                                                data-status="${s.status}"
                                                onclick="openCollegeEditStudent(this)">
                                            <i class="bi bi-pencil-square me-1"></i> Edit
                                        </button>
                                    </td>
                                </tr>
                            </c:forEach>
                            <c:if test="${empty students}">
                                <tr><td colspan="8" class="text-center py-5 text-muted">No student records enrolled under this college.</td></tr>
                            </c:if>
                        </tbody>
                    </table>
                </div>
            </div>

        </div>
    </main>
</div>

<!-- Modal: College Add Student -->
<div class="modal fade" id="collegeAddStudentModal" tabindex="-1">
    <div class="modal-dialog modal-lg">
        <div class="modal-content rounded-4 border-0 shadow">
            <div class="modal-header bg-primary text-white">
                <h5 class="modal-title fw-bold"><i class="bi bi-person-plus me-1"></i> Register Student (Institutional Enrollment)</h5>
                <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal"></button>
            </div>
            <form action="${pageContext.request.contextPath}/college/student-action" method="POST">
                <input type="hidden" name="action" value="add">
                <div class="modal-body p-4">
                    <div class="row g-3">
                        <div class="col-md-6 col-12">
                            <label class="form-label fw-semibold">Student Full Name <span class="text-danger">*</span></label>
                            <input type="text" name="full_name" class="form-control" required>
                        </div>
                        <div class="col-md-6 col-12">
                            <label class="form-label fw-semibold">Email Address <span class="text-danger">*</span></label>
                            <input type="email" name="email" class="form-control" required>
                        </div>
                        <div class="col-md-6 col-12">
                            <label class="form-label fw-semibold">Phone <span class="text-danger">*</span></label>
                            <input type="text" name="phone" class="form-control" required>
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
                            <input type="text" name="blood_group" class="form-control" placeholder="e.g. O+">
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
                        <div class="col-md-6 col-12">
                            <label class="form-label fw-semibold">Department <span class="text-danger">*</span></label>
                            <select name="department_id" class="form-select" required>
                                <c:forEach var="d" items="${departments}">
                                    <option value="${d.id}">${d.name}</option>
                                </c:forEach>
                            </select>
                        </div>
                        <div class="col-md-6 col-12">
                            <label class="form-label fw-semibold">Course / Program <span class="text-danger">*</span></label>
                            <select name="course_id" class="form-select" required>
                                <c:forEach var="co" items="${courses}">
                                    <option value="${co.id}">${co.name} (${co.courseCode})</option>
                                </c:forEach>
                            </select>
                        </div>
                        <div class="col-md-6 col-12">
                            <label class="form-label fw-semibold">Semester <span class="text-danger">*</span></label>
                            <select name="semester_id" class="form-select" required>
                                <c:forEach var="sem" items="${semesters}">
                                    <option value="${sem.id}">${sem.courseName} - Semester ${sem.semesterNumber}</option>
                                </c:forEach>
                            </select>
                        </div>
                        <div class="col-md-6 col-12">
                            <label class="form-label fw-semibold">Academic Session <span class="text-danger">*</span></label>
                            <select name="session_id" class="form-select" required>
                                <c:forEach var="ses" items="${sessions}">
                                    <option value="${ses.id}">${ses.sessionCode}</option>
                                </c:forEach>
                            </select>
                        </div>
                    </div>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancel</button>
                    <button type="submit" class="btn btn-primary">Enroll Student</button>
                </div>
            </form>
        </div>
    </div>
</div>

<!-- Modal: College Edit Student -->
<div class="modal fade" id="collegeEditStudentModal" tabindex="-1">
    <div class="modal-dialog modal-lg">
        <div class="modal-content rounded-4 border-0 shadow">
            <div class="modal-header bg-dark text-white">
                <h5 class="modal-title fw-bold"><i class="bi bi-pencil-square me-1"></i> Edit Student Profile</h5>
                <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal"></button>
            </div>
            <form action="${pageContext.request.contextPath}/college/student-action" method="POST">
                <input type="hidden" name="action" value="edit">
                <input type="hidden" name="id" id="collegeEditStudentId">
                <div class="modal-body p-4">
                    <div class="row g-3">
                        <div class="col-md-6 col-12">
                            <label class="form-label fw-semibold">Registration Number</label>
                            <input type="text" name="registration_no" id="collegeEditRegNo" class="form-control bg-light" readonly>
                        </div>
                        <div class="col-md-6 col-12">
                            <label class="form-label fw-semibold">Roll Number</label>
                            <input type="text" name="roll_no" id="collegeEditRollNo" class="form-control bg-light" readonly>
                        </div>
                        <div class="col-md-6 col-12">
                            <label class="form-label fw-semibold">Student Full Name <span class="text-danger">*</span></label>
                            <input type="text" name="full_name" id="collegeEditFullName" class="form-control" required>
                        </div>
                        <div class="col-md-6 col-12">
                            <label class="form-label fw-semibold">Email Address <span class="text-danger">*</span></label>
                            <input type="email" name="email" id="collegeEditEmail" class="form-control" required>
                        </div>
                        <div class="col-md-6 col-12">
                            <label class="form-label fw-semibold">Phone <span class="text-danger">*</span></label>
                            <input type="text" name="phone" id="collegeEditPhone" class="form-control" required>
                        </div>
                        <div class="col-md-3 col-6">
                            <label class="form-label fw-semibold">Date of Birth</label>
                            <input type="date" name="dob" id="collegeEditDob" class="form-control">
                        </div>
                        <div class="col-md-3 col-6">
                            <label class="form-label fw-semibold">Gender</label>
                            <select name="gender" id="collegeEditGender" class="form-select">
                                <option value="MALE">Male</option>
                                <option value="FEMALE">Female</option>
                                <option value="OTHER">Other</option>
                            </select>
                        </div>
                        <div class="col-md-4 col-12">
                            <label class="form-label fw-semibold">Blood Group</label>
                            <input type="text" name="blood_group" id="collegeEditBloodGroup" class="form-control" placeholder="e.g. O+">
                        </div>
                        <div class="col-md-4 col-12">
                            <label class="form-label fw-semibold">Father's Name</label>
                            <input type="text" name="father_name" id="collegeEditFatherName" class="form-control">
                        </div>
                        <div class="col-md-4 col-12">
                            <label class="form-label fw-semibold">Mother's Name</label>
                            <input type="text" name="mother_name" id="collegeEditMotherName" class="form-control">
                        </div>
                        <div class="col-12">
                            <label class="form-label fw-semibold">Permanent Address</label>
                            <textarea name="address" id="collegeEditAddress" class="form-control" rows="2"></textarea>
                        </div>
                        <div class="col-md-6 col-12">
                            <label class="form-label fw-semibold">Department <span class="text-danger">*</span></label>
                            <select name="department_id" id="collegeEditDeptId" class="form-select" required>
                                <c:forEach var="d" items="${departments}">
                                    <option value="${d.id}">${d.name}</option>
                                </c:forEach>
                            </select>
                        </div>
                        <div class="col-md-6 col-12">
                            <label class="form-label fw-semibold">Course / Program <span class="text-danger">*</span></label>
                            <select name="course_id" id="collegeEditCourseId" class="form-select" required>
                                <c:forEach var="co" items="${courses}">
                                    <option value="${co.id}">${co.name} (${co.courseCode})</option>
                                </c:forEach>
                            </select>
                        </div>
                        <div class="col-md-4 col-12">
                            <label class="form-label fw-semibold">Semester <span class="text-danger">*</span></label>
                            <select name="semester_id" id="collegeEditSemesterId" class="form-select" required>
                                <c:forEach var="sem" items="${semesters}">
                                    <option value="${sem.id}">${sem.courseName} - Semester ${sem.semesterNumber}</option>
                                </c:forEach>
                            </select>
                        </div>
                        <div class="col-md-4 col-12">
                            <label class="form-label fw-semibold">Academic Session <span class="text-danger">*</span></label>
                            <select name="session_id" id="collegeEditSessionId" class="form-select" required>
                                <c:forEach var="ses" items="${sessions}">
                                    <option value="${ses.id}">${ses.sessionCode}</option>
                                </c:forEach>
                            </select>
                        </div>
                        <div class="col-md-4 col-12">
                            <label class="form-label fw-semibold">Enrollment Status <span class="text-danger">*</span></label>
                            <select name="status" id="collegeEditStatus" class="form-select" required>
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
function openCollegeEditStudent(elemOrId, name, email, phone, dob, gender, bg, father, mother, addr, deptId, courseId, semId, sesId, status, regNo, rollNo) {
    let id, reg, roll, fullName, mail, ph, birth, gen, blood, fName, mName, address, dept, course, sem, session, st;
    if (elemOrId && typeof elemOrId === 'object' && elemOrId.dataset) {
        const d = elemOrId.dataset;
        id = d.id || '';
        reg = d.regno || '';
        roll = d.rollno || '';
        fullName = d.name || '';
        mail = d.email || '';
        ph = d.phone || '';
        birth = d.dob || '';
        gen = d.gender || 'MALE';
        blood = d.bloodgroup || '';
        fName = d.father || '';
        mName = d.mother || '';
        address = d.address || '';
        dept = d.dept || '';
        course = d.course || '';
        sem = d.semester || '';
        session = d.session || '';
        st = d.status || 'ACTIVE';
    } else {
        id = elemOrId || '';
        fullName = name || '';
        mail = email || '';
        ph = phone || '';
        birth = dob || '';
        gen = gender || 'MALE';
        blood = bg || '';
        fName = father || '';
        mName = mother || '';
        address = addr || '';
        dept = deptId || '';
        course = courseId || '';
        sem = semId || '';
        session = sesId || '';
        st = status || 'ACTIVE';
        reg = regNo || '';
        roll = rollNo || '';
    }

    document.getElementById('collegeEditStudentId').value = id;
    if (document.getElementById('collegeEditRegNo')) document.getElementById('collegeEditRegNo').value = reg;
    if (document.getElementById('collegeEditRollNo')) document.getElementById('collegeEditRollNo').value = roll;
    document.getElementById('collegeEditFullName').value = fullName;
    document.getElementById('collegeEditEmail').value = mail;
    document.getElementById('collegeEditPhone').value = ph;
    document.getElementById('collegeEditDob').value = birth;
    document.getElementById('collegeEditGender').value = (gen || 'MALE').toUpperCase();
    document.getElementById('collegeEditBloodGroup').value = blood;
    document.getElementById('collegeEditFatherName').value = fName;
    document.getElementById('collegeEditMotherName').value = mName;
    document.getElementById('collegeEditAddress').value = address;
    document.getElementById('collegeEditDeptId').value = dept;
    document.getElementById('collegeEditCourseId').value = course;
    document.getElementById('collegeEditSemesterId').value = sem;
    document.getElementById('collegeEditSessionId').value = session;
    if (document.getElementById('collegeEditStatus')) document.getElementById('collegeEditStatus').value = (st || 'ACTIVE').toUpperCase();

    new bootstrap.Modal(document.getElementById('collegeEditStudentModal')).show();
}
</script>

<jsp:include page="/WEB-INF/views/common/footer.jsp" />
