<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<jsp:include page="/WEB-INF/views/common/header.jsp">
    <jsp:param name="pageTitle" value="Academic Curriculum Management • Apex University" />
</jsp:include>

<div class="dashboard-wrapper">
    <jsp:include page="/WEB-INF/views/common/sidebar.jsp">
        <jsp:param name="activeNav" value="academics" />
    </jsp:include>

    <main class="dashboard-content">
        <div class="container-fluid">
            
            <jsp:include page="/WEB-INF/views/common/alerts.jsp" />

            <div class="d-flex justify-content-between align-items-center mb-4 flex-wrap gap-2">
                <div>
                    <h2 class="h3 fw-bold mb-1">Academic Structure &amp; Curriculum</h2>
                    <p class="text-secondary small mb-0">Configure University departments, degree courses, semester subjects, credits, and academic sessions.</p>
                </div>
            </div>

            <!-- Tab Buttons -->
            <ul class="nav nav-pills mb-4 p-1 bg-light rounded-3">
                <li class="nav-item">
                    <a class="nav-link ${activeTab == 'departments' ? 'active bg-primary' : 'text-dark'}" href="${pageContext.request.contextPath}/admin/academics?tab=departments">
                        <i class="bi bi-diagram-3-fill me-1"></i> Departments (${departments.size()})
                    </a>
                </li>
                <li class="nav-item">
                    <a class="nav-link ${activeTab == 'courses' ? 'active bg-primary' : 'text-dark'}" href="${pageContext.request.contextPath}/admin/academics?tab=courses">
                        <i class="bi bi-book-fill me-1"></i> Courses &amp; Programs (${courses.size()})
                    </a>
                </li>
                <li class="nav-item">
                    <a class="nav-link ${activeTab == 'subjects' ? 'active bg-primary' : 'text-dark'}" href="${pageContext.request.contextPath}/admin/academics?tab=subjects">
                        <i class="bi bi-journal-code me-1"></i> Subjects &amp; Credits (${subjects.size()})
                    </a>
                </li>
                <li class="nav-item">
                    <a class="nav-link ${activeTab == 'sessions' ? 'active bg-primary' : 'text-dark'}" href="${pageContext.request.contextPath}/admin/academics?tab=sessions">
                        <i class="bi bi-calendar-range me-1"></i> Academic Sessions (${sessions.size()})
                    </a>
                </li>
            </ul>

            <!-- ================= TAB 1: DEPARTMENTS ================= -->
            <c:if test="${activeTab == 'departments'}">
                <div class="card shadow-sm border rounded-4 p-4 mb-4">
                    <div class="d-flex justify-content-between align-items-center mb-3">
                        <h4 class="h5 fw-bold mb-0">University Departments</h4>
                        <button type="button" class="btn btn-apex-primary btn-sm" data-bs-toggle="modal" data-bs-target="#addDeptModal">
                            <i class="bi bi-plus-lg me-1"></i> Add Department
                        </button>
                    </div>

                    <div class="table-responsive">
                        <table class="table table-hover align-middle mb-0 apex-table apex-table-compact">
                            <thead>
                                <tr>
                                    <th>Code</th>
                                    <th>Department Name</th>
                                    <th>Description</th>
                                    <th>Status</th>
                                    <th class="text-center text-nowrap" style="width: 110px; min-width: 110px;">Action</th>
                                </tr>
                            </thead>
                            <tbody>
                                <c:forEach var="d" items="${departments}">
                                    <tr>
                                        <td class="fw-bold text-primary">${d.deptCode}</td>
                                        <td class="fw-semibold">${d.name}</td>
                                        <td class="text-secondary small">${d.description}</td>
                                        <td><span class="badge ${d.status == 'ACTIVE' ? 'bg-success' : 'bg-danger'}">${d.status}</span></td>
                                        <td class="text-center text-nowrap">
                                            <div class="table-actions">
                                                <button type="button" class="btn btn-sm btn-outline-primary btn-icon-only"
                                                        data-id="${d.id}"
                                                        data-code="<c:out value='${d.deptCode}' />"
                                                        data-name="<c:out value='${d.name}' />"
                                                        data-description="<c:out value='${d.description}' />"
                                                        data-status="${d.status}"
                                                        onclick="populateEditDept(this)"
                                                        title="Edit Department">
                                                    <i class="bi bi-pencil-square"></i>
                                                </button>
                                                <form action="${pageContext.request.contextPath}/admin/academic-action" method="POST" class="d-inline">
                                                    <input type="hidden" name="entity" value="dept">
                                                    <input type="hidden" name="action" value="delete">
                                                    <input type="hidden" name="id" value="${d.id}">
                                                    <button type="submit" class="btn btn-sm btn-outline-danger btn-icon-only btn-confirm-delete" data-confirm="Delete department ${d.name}?" title="Delete Department">
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

            <!-- ================= TAB 2: COURSES ================= -->
            <c:if test="${activeTab == 'courses'}">
                <div class="card shadow-sm border rounded-4 p-4 mb-4">
                    <div class="d-flex justify-content-between align-items-center mb-3">
                        <h4 class="h5 fw-bold mb-0">Degree Courses &amp; Programs</h4>
                        <button type="button" class="btn btn-apex-primary btn-sm" data-bs-toggle="modal" data-bs-target="#addCourseModal">
                            <i class="bi bi-plus-lg me-1"></i> Add Course
                        </button>
                    </div>

                    <div class="table-responsive">
                        <table class="table table-hover align-middle mb-0 apex-table apex-table-compact">
                            <thead>
                                <tr>
                                    <th>Course Code</th>
                                    <th>Course Title</th>
                                    <th>Department</th>
                                    <th>Level</th>
                                    <th>Duration</th>
                                    <th>Semesters</th>
                                    <th>Status</th>
                                    <th class="text-center text-nowrap" style="width: 110px; min-width: 110px;">Action</th>
                                </tr>
                            </thead>
                            <tbody>
                                <c:forEach var="c" items="${courses}">
                                    <tr>
                                        <td class="fw-bold text-primary">${c.courseCode}</td>
                                        <td class="fw-semibold">${c.name}</td>
                                        <td>${c.departmentName}</td>
                                        <td><span class="badge bg-light text-dark border">${c.degreeLevel}</span></td>
                                        <td>${c.durationYears} Years</td>
                                        <td><span class="badge bg-secondary">${c.totalSemesters} Semesters</span></td>
                                        <td><span class="badge ${c.status == 'ACTIVE' ? 'bg-success' : 'bg-danger'}">${c.status}</span></td>
                                        <td class="text-center text-nowrap">
                                            <div class="table-actions">
                                                <button type="button" class="btn btn-sm btn-outline-primary btn-icon-only"
                                                        data-id="${c.id}"
                                                        data-dept="${c.departmentId}"
                                                        data-code="<c:out value='${c.courseCode}' />"
                                                        data-name="<c:out value='${c.name}' />"
                                                        data-duration="${c.durationYears}"
                                                        data-semesters="${c.totalSemesters}"
                                                        data-level="<c:out value='${c.degreeLevel}' />"
                                                        data-status="${c.status}"
                                                        onclick="populateEditCourse(this)"
                                                        title="Edit Course">
                                                    <i class="bi bi-pencil-square"></i>
                                                </button>
                                                <form action="${pageContext.request.contextPath}/admin/academic-action" method="POST" class="d-inline">
                                                    <input type="hidden" name="entity" value="course">
                                                    <input type="hidden" name="action" value="delete">
                                                    <input type="hidden" name="id" value="${c.id}">
                                                    <button type="submit" class="btn btn-sm btn-outline-danger btn-icon-only btn-confirm-delete" data-confirm="Delete course ${c.name}?" title="Delete Course">
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

            <!-- ================= TAB 3: SUBJECTS ================= -->
            <c:if test="${activeTab == 'subjects'}">
                <div class="card shadow-sm border rounded-4 p-4 mb-4">
                    <div class="d-flex justify-content-between align-items-center mb-3">
                        <h4 class="h5 fw-bold mb-0">Curriculum Subjects &amp; Credit Scheme</h4>
                        <button type="button" class="btn btn-apex-primary btn-sm" data-bs-toggle="modal" data-bs-target="#addSubjectModal">
                            <i class="bi bi-plus-lg me-1"></i> Add Subject
                        </button>
                    </div>

                    <div class="table-responsive">
                        <table class="table table-hover align-middle mb-0 apex-table apex-table-compact">
                            <thead>
                                <tr>
                                    <th>Subject Code</th>
                                    <th>Subject Title</th>
                                    <th>Course</th>
                                    <th>Semester</th>
                                    <th>Credits</th>
                                    <th>Max Marks (Int/Ext)</th>
                                    <th>Pass Marks</th>
                                    <th>Type</th>
                                    <th class="text-center text-nowrap" style="width: 110px; min-width: 110px;">Action</th>
                                </tr>
                            </thead>
                            <tbody>
                                <c:forEach var="sub" items="${subjects}">
                                    <tr>
                                        <td class="fw-bold text-primary">${sub.subjectCode}</td>
                                        <td class="fw-semibold">${sub.name}</td>
                                        <td><small>${sub.courseName}</small></td>
                                        <td>Semester ${sub.semesterNumber}</td>
                                        <td><span class="badge bg-warning text-dark">${sub.credit} Credits</span></td>
                                        <td>${sub.maxInternalMarks} + ${sub.maxExternalMarks} = <strong>${sub.maxInternalMarks + sub.maxExternalMarks}</strong></td>
                                        <td>${sub.passMarks}</td>
                                        <td><span class="badge bg-secondary">${sub.subjectType}</span></td>
                                        <td class="text-center text-nowrap">
                                            <div class="table-actions">
                                                <button type="button" class="btn btn-sm btn-outline-primary btn-icon-only"
                                                        data-id="${sub.id}"
                                                        data-course="${sub.courseId}"
                                                        data-semester="${sub.semesterId}"
                                                        data-code="<c:out value='${sub.subjectCode}' />"
                                                        data-name="<c:out value='${sub.name}' />"
                                                        data-credit="${sub.credit}"
                                                        data-internal="${sub.maxInternalMarks}"
                                                        data-external="${sub.maxExternalMarks}"
                                                        data-pass="${sub.passMarks}"
                                                        data-type="<c:out value='${sub.subjectType}' />"
                                                        data-status="${sub.status}"
                                                        onclick="populateEditSubject(this)"
                                                        title="Edit Subject">
                                                    <i class="bi bi-pencil-square"></i>
                                                </button>
                                                <form action="${pageContext.request.contextPath}/admin/academic-action" method="POST" class="d-inline">
                                                    <input type="hidden" name="entity" value="subject">
                                                    <input type="hidden" name="action" value="delete">
                                                    <input type="hidden" name="id" value="${sub.id}">
                                                    <button type="submit" class="btn btn-sm btn-outline-danger btn-icon-only btn-confirm-delete" data-confirm="Delete subject ${sub.name}?" title="Delete Subject">
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

            <!-- ================= TAB 4: SESSIONS ================= -->
            <c:if test="${activeTab == 'sessions'}">
                <div class="card shadow-sm border rounded-4 p-4 mb-4">
                    <div class="d-flex justify-content-between align-items-center mb-3">
                        <h4 class="h5 fw-bold mb-0">Academic Sessions</h4>
                        <button type="button" class="btn btn-apex-primary btn-sm" data-bs-toggle="modal" data-bs-target="#addSessionModal">
                            <i class="bi bi-plus-lg me-1"></i> Add Academic Session
                        </button>
                    </div>

                    <div class="table-responsive">
                        <table class="table table-hover align-middle mb-0 apex-table apex-table-compact">
                            <thead>
                                <tr>
                                    <th>Session Code</th>
                                    <th>Start Year</th>
                                    <th>End Year</th>
                                    <th>Current Active</th>
                                    <th>Status</th>
                                    <th class="text-center text-nowrap" style="width: 90px; min-width: 90px;">Action</th>
                                </tr>
                            </thead>
                            <tbody>
                                <c:forEach var="ses" items="${sessions}">
                                    <tr>
                                        <td class="fw-bold text-primary">${ses.sessionCode}</td>
                                        <td>${ses.startYear}</td>
                                        <td>${ses.endYear}</td>
                                        <td>
                                            <c:choose>
                                                <c:when test="${ses.current}">
                                                    <span class="badge bg-success px-3 py-1"><i class="bi bi-check-circle me-1"></i> Current Active Session</span>
                                                </c:when>
                                                <c:otherwise>
                                                    <form action="${pageContext.request.contextPath}/admin/academic-action" method="POST" class="d-inline">
                                                        <input type="hidden" name="entity" value="session">
                                                        <input type="hidden" name="action" value="set_current">
                                                        <input type="hidden" name="id" value="${ses.id}">
                                                        <button type="submit" class="btn btn-sm btn-outline-primary">Set as Current</button>
                                                    </form>
                                                </c:otherwise>
                                            </c:choose>
                                        </td>
                                        <td><span class="badge ${ses.status == 'ACTIVE' ? 'bg-light text-dark border' : 'bg-danger'}">${ses.status}</span></td>
                                        <td class="text-center text-nowrap">
                                            <div class="table-actions">
                                                <button type="button" class="btn btn-sm btn-outline-primary btn-icon-only"
                                                        data-id="${ses.id}"
                                                        data-code="<c:out value='${ses.sessionCode}' />"
                                                        data-start="${ses.startYear}"
                                                        data-end="${ses.endYear}"
                                                        data-current="${ses.current}"
                                                        data-status="${ses.status}"
                                                        onclick="populateEditSession(this)"
                                                        title="Edit Academic Session">
                                                    <i class="bi bi-pencil-square"></i>
                                                </button>
                                            </div>
                                        </td>
                                    </tr>
                                </c:forEach>
                            </tbody>
                        </table>
                    </div>
                </div>
            </c:if>

        </div>
    </main>
</div>

<!-- Modal: Add Department -->
<div class="modal fade" id="addDeptModal" tabindex="-1">
    <div class="modal-dialog">
        <div class="modal-content rounded-4 border-0 shadow">
            <div class="modal-header bg-primary text-white">
                <h5 class="modal-title fw-bold">Add Academic Department</h5>
                <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal"></button>
            </div>
            <form action="${pageContext.request.contextPath}/admin/academic-action" method="POST">
                <input type="hidden" name="entity" value="dept">
                <input type="hidden" name="action" value="add">
                <div class="modal-body p-4">
                    <div class="mb-3">
                        <label class="form-label fw-semibold">Department Code</label>
                        <input type="text" name="dept_code" class="form-control" placeholder="e.g. IT, ME, CE" required>
                    </div>
                    <div class="mb-3">
                        <label class="form-label fw-semibold">Department Name</label>
                        <input type="text" name="name" class="form-control" placeholder="Department of..." required>
                    </div>
                    <div class="mb-3">
                        <label class="form-label fw-semibold">Description</label>
                        <textarea name="description" class="form-control" rows="2"></textarea>
                    </div>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancel</button>
                    <button type="submit" class="btn btn-primary">Save Department</button>
                </div>
            </form>
        </div>
    </div>
</div>

<!-- Modal: Add Course -->
<div class="modal fade" id="addCourseModal" tabindex="-1">
    <div class="modal-dialog">
        <div class="modal-content rounded-4 border-0 shadow">
            <div class="modal-header bg-primary text-white">
                <h5 class="modal-title fw-bold">Add Degree Course</h5>
                <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal"></button>
            </div>
            <form action="${pageContext.request.contextPath}/admin/academic-action" method="POST">
                <input type="hidden" name="entity" value="course">
                <input type="hidden" name="action" value="add">
                <div class="modal-body p-4">
                    <div class="mb-3">
                        <label class="form-label fw-semibold">Department</label>
                        <select name="department_id" class="form-select" required>
                            <c:forEach var="d" items="${departments}">
                                <option value="${d.id}">${d.name}</option>
                            </c:forEach>
                        </select>
                    </div>
                    <div class="mb-3">
                        <label class="form-label fw-semibold">Course Code</label>
                        <input type="text" name="course_code" class="form-control" placeholder="e.g. BTECH-IT, MTECH-AI" required>
                    </div>
                    <div class="mb-3">
                        <label class="form-label fw-semibold">Course Name</label>
                        <input type="text" name="name" class="form-control" placeholder="Full title of academic degree" required>
                    </div>
                    <div class="row g-2 mb-3">
                        <div class="col-6">
                            <label class="form-label fw-semibold">Duration (Years)</label>
                            <input type="number" name="duration_years" class="form-control" value="4" required>
                        </div>
                        <div class="col-6">
                            <label class="form-label fw-semibold">Total Semesters</label>
                            <input type="number" name="total_semesters" class="form-control" value="8" required>
                        </div>
                    </div>
                    <div class="mb-3">
                        <label class="form-label fw-semibold">Degree Level</label>
                        <select name="degree_level" class="form-select">
                            <option value="UG">Undergraduate (UG)</option>
                            <option value="PG">Postgraduate (PG)</option>
                            <option value="DIPLOMA">Diploma</option>
                            <option value="PHD">Doctoral (Ph.D.)</option>
                        </select>
                    </div>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancel</button>
                    <button type="submit" class="btn btn-primary">Save Course</button>
                </div>
            </form>
        </div>
    </div>
</div>

<!-- Modal: Add Subject -->
<div class="modal fade" id="addSubjectModal" tabindex="-1">
    <div class="modal-dialog">
        <div class="modal-content rounded-4 border-0 shadow">
            <div class="modal-header bg-primary text-white">
                <h5 class="modal-title fw-bold"><i class="bi bi-journal-plus me-1"></i> Add Subject</h5>
                <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal"></button>
            </div>
            <form action="${pageContext.request.contextPath}/admin/academic-action" method="POST">
                <input type="hidden" name="entity" value="subject">
                <input type="hidden" name="action" value="add">
                <div class="modal-body p-4">
                    <div class="mb-3">
                        <label class="form-label fw-semibold">Course</label>
                        <select name="course_id" id="addSubjectCourse" class="form-select" onchange="filterSemestersByCourse('addSubjectCourse', 'addSubjectSemester')" required>
                            <c:forEach var="co" items="${courses}">
                                <option value="${co.id}">${co.name}</option>
                            </c:forEach>
                        </select>
                    </div>
                    <div class="mb-3">
                        <label class="form-label fw-semibold">Semester</label>
                        <select name="semester_id" id="addSubjectSemester" class="form-select" required>
                            <c:forEach var="sem" items="${semesters}">
                                <option value="${sem.id}" data-course="${sem.courseId}">${sem.courseName} - Sem ${sem.semesterNumber}</option>
                            </c:forEach>
                        </select>
                    </div>
                    <div class="mb-3">
                        <label class="form-label fw-semibold">Subject Code</label>
                        <input type="text" name="subject_code" class="form-control" placeholder="e.g. CS501" required>
                    </div>
                    <div class="mb-3">
                        <label class="form-label fw-semibold">Subject Title</label>
                        <input type="text" name="name" class="form-control" required>
                    </div>
                    <div class="row g-2 mb-3">
                        <div class="col-4">
                            <label class="form-label fw-semibold">Credits</label>
                            <input type="number" step="0.5" name="credit" class="form-control" value="3.0" required>
                        </div>
                        <div class="col-4">
                            <label class="form-label fw-semibold">Max Int</label>
                            <input type="number" name="max_internal_marks" class="form-control" value="30" required>
                        </div>
                        <div class="col-4">
                            <label class="form-label fw-semibold">Max Ext</label>
                            <input type="number" name="max_external_marks" class="form-control" value="70" required>
                        </div>
                    </div>
                    <div class="row g-2 mb-3">
                        <div class="col-6">
                            <label class="form-label fw-semibold">Pass Marks</label>
                            <input type="number" name="pass_marks" class="form-control" value="40" required>
                        </div>
                        <div class="col-6">
                            <label class="form-label fw-semibold">Type</label>
                            <select name="subject_type" class="form-select">
                                <option value="THEORY">THEORY</option>
                                <option value="PRACTICAL">PRACTICAL</option>
                                <option value="SESSIONAL">SESSIONAL</option>
                            </select>
                        </div>
                    </div>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancel</button>
                    <button type="submit" class="btn btn-primary">Save Subject</button>
                </div>
            </form>
        </div>
    </div>
</div>

<!-- Modal: Add Session -->
<div class="modal fade" id="addSessionModal" tabindex="-1">
    <div class="modal-dialog">
        <div class="modal-content rounded-4 border-0 shadow">
            <div class="modal-header bg-primary text-white">
                <h5 class="modal-title fw-bold"><i class="bi bi-calendar-plus me-1"></i> Add Academic Session</h5>
                <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal"></button>
            </div>
            <form action="${pageContext.request.contextPath}/admin/academic-action" method="POST">
                <input type="hidden" name="entity" value="session">
                <input type="hidden" name="action" value="add">
                <div class="modal-body p-4">
                    <div class="mb-3">
                        <label class="form-label fw-semibold">Session Code</label>
                        <input type="text" name="session_code" class="form-control" placeholder="e.g. 2026-2027" required>
                    </div>
                    <div class="row g-2 mb-3">
                        <div class="col-6">
                            <label class="form-label fw-semibold">Start Year</label>
                            <input type="number" name="start_year" class="form-control" value="2026" required>
                        </div>
                        <div class="col-6">
                            <label class="form-label fw-semibold">End Year</label>
                            <input type="number" name="end_year" class="form-control" value="2027" required>
                        </div>
                    </div>
                    <div class="form-check mb-3">
                        <input class="form-check-input" type="checkbox" name="is_current" value="1" id="isCurrentCheck">
                        <label class="form-check-label" for="isCurrentCheck">Set as Current Active Session</label>
                    </div>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancel</button>
                    <button type="submit" class="btn btn-primary">Save Session</button>
                </div>
            </form>
        </div>
    </div>
</div>

<!-- ================= EDIT MODALS ================= -->

<!-- Modal: Edit Department -->
<div class="modal fade" id="editDeptModal" tabindex="-1">
    <div class="modal-dialog">
        <div class="modal-content rounded-4 border-0 shadow">
            <div class="modal-header bg-primary text-white">
                <h5 class="modal-title fw-bold"><i class="bi bi-pencil-square me-1"></i> Edit Academic Department</h5>
                <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal"></button>
            </div>
            <form action="${pageContext.request.contextPath}/admin/academic-action" method="POST">
                <input type="hidden" name="entity" value="dept">
                <input type="hidden" name="action" value="edit">
                <input type="hidden" name="id" id="editDeptId">
                <div class="modal-body p-4">
                    <div class="mb-3">
                        <label class="form-label fw-semibold">Department Code <span class="text-danger">*</span></label>
                        <input type="text" name="dept_code" id="editDeptCode" class="form-control" placeholder="e.g. IT, ME, CE" required>
                    </div>
                    <div class="mb-3">
                        <label class="form-label fw-semibold">Department Name <span class="text-danger">*</span></label>
                        <input type="text" name="name" id="editDeptName" class="form-control" placeholder="Department of..." required>
                    </div>
                    <div class="mb-3">
                        <label class="form-label fw-semibold">Description</label>
                        <textarea name="description" id="editDeptDescription" class="form-control" rows="2"></textarea>
                    </div>
                    <div class="mb-3">
                        <label class="form-label fw-semibold">Status <span class="text-danger">*</span></label>
                        <select name="status" id="editDeptStatus" class="form-select" required>
                            <option value="ACTIVE">ACTIVE</option>
                            <option value="INACTIVE">INACTIVE</option>
                        </select>
                    </div>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancel</button>
                    <button type="submit" class="btn btn-primary">Update Department</button>
                </div>
            </form>
        </div>
    </div>
</div>

<!-- Modal: Edit Course -->
<div class="modal fade" id="editCourseModal" tabindex="-1">
    <div class="modal-dialog">
        <div class="modal-content rounded-4 border-0 shadow">
            <div class="modal-header bg-primary text-white">
                <h5 class="modal-title fw-bold"><i class="bi bi-pencil-square me-1"></i> Edit Degree Course</h5>
                <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal"></button>
            </div>
            <form action="${pageContext.request.contextPath}/admin/academic-action" method="POST">
                <input type="hidden" name="entity" value="course">
                <input type="hidden" name="action" value="edit">
                <input type="hidden" name="id" id="editCourseId">
                <div class="modal-body p-4">
                    <div class="mb-3">
                        <label class="form-label fw-semibold">Department <span class="text-danger">*</span></label>
                        <select name="department_id" id="editCourseDept" class="form-select" required>
                            <c:forEach var="d" items="${departments}">
                                <option value="${d.id}">${d.name}</option>
                            </c:forEach>
                        </select>
                    </div>
                    <div class="mb-3">
                        <label class="form-label fw-semibold">Course Code <span class="text-danger">*</span></label>
                        <input type="text" name="course_code" id="editCourseCode" class="form-control" placeholder="e.g. BTECH-IT, MTECH-AI" required>
                    </div>
                    <div class="mb-3">
                        <label class="form-label fw-semibold">Course Name <span class="text-danger">*</span></label>
                        <input type="text" name="name" id="editCourseName" class="form-control" placeholder="Full title of academic degree" required>
                    </div>
                    <div class="row g-2 mb-3">
                        <div class="col-6">
                            <label class="form-label fw-semibold">Duration (Years) <span class="text-danger">*</span></label>
                            <input type="number" name="duration_years" id="editCourseDuration" class="form-control" min="1" max="10" required>
                        </div>
                        <div class="col-6">
                            <label class="form-label fw-semibold">Total Semesters <span class="text-danger">*</span></label>
                            <input type="number" name="total_semesters" id="editCourseSemesters" class="form-control" min="1" max="20" required>
                        </div>
                        <div class="col-12">
                            <div class="form-text small text-muted"><i class="bi bi-info-circle me-1"></i>Existing semester records are preserved. Ensure this covers current term requirements.</div>
                        </div>
                    </div>
                    <div class="mb-3">
                        <label class="form-label fw-semibold">Degree Level <span class="text-danger">*</span></label>
                        <select name="degree_level" id="editCourseLevel" class="form-select" required>
                            <option value="UG">Undergraduate (UG)</option>
                            <option value="PG">Postgraduate (PG)</option>
                            <option value="DIPLOMA">Diploma</option>
                            <option value="PHD">Doctoral (Ph.D.)</option>
                        </select>
                    </div>
                    <div class="mb-3">
                        <label class="form-label fw-semibold">Status <span class="text-danger">*</span></label>
                        <select name="status" id="editCourseStatus" class="form-select" required>
                            <option value="ACTIVE">ACTIVE</option>
                            <option value="INACTIVE">INACTIVE</option>
                        </select>
                    </div>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancel</button>
                    <button type="submit" class="btn btn-primary">Update Course</button>
                </div>
            </form>
        </div>
    </div>
</div>

<!-- Modal: Edit Subject -->
<div class="modal fade" id="editSubjectModal" tabindex="-1">
    <div class="modal-dialog">
        <div class="modal-content rounded-4 border-0 shadow">
            <div class="modal-header bg-primary text-white">
                <h5 class="modal-title fw-bold"><i class="bi bi-pencil-square me-1"></i> Edit Subject</h5>
                <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal"></button>
            </div>
            <form action="${pageContext.request.contextPath}/admin/academic-action" method="POST">
                <input type="hidden" name="entity" value="subject">
                <input type="hidden" name="action" value="edit">
                <input type="hidden" name="id" id="editSubjectId">
                <div class="modal-body p-4">
                    <div class="mb-3">
                        <label class="form-label fw-semibold">Course <span class="text-danger">*</span></label>
                        <select name="course_id" id="editSubjectCourse" class="form-select" onchange="filterSemestersByCourse('editSubjectCourse', 'editSubjectSemester')" required>
                            <c:forEach var="co" items="${courses}">
                                <option value="${co.id}">${co.name}</option>
                            </c:forEach>
                        </select>
                    </div>
                    <div class="mb-3">
                        <label class="form-label fw-semibold">Semester <span class="text-danger">*</span></label>
                        <select name="semester_id" id="editSubjectSemester" class="form-select" required>
                            <c:forEach var="sem" items="${semesters}">
                                <option value="${sem.id}" data-course="${sem.courseId}">${sem.courseName} - Sem ${sem.semesterNumber}</option>
                            </c:forEach>
                        </select>
                    </div>
                    <div class="mb-3">
                        <label class="form-label fw-semibold">Subject Code <span class="text-danger">*</span></label>
                        <input type="text" name="subject_code" id="editSubjectCode" class="form-control" placeholder="e.g. CS501" required>
                    </div>
                    <div class="mb-3">
                        <label class="form-label fw-semibold">Subject Title <span class="text-danger">*</span></label>
                        <input type="text" name="name" id="editSubjectName" class="form-control" required>
                    </div>
                    <div class="row g-2 mb-3">
                        <div class="col-4">
                            <label class="form-label fw-semibold">Credits <span class="text-danger">*</span></label>
                            <input type="number" step="0.5" name="credit" id="editSubjectCredit" class="form-control" required>
                        </div>
                        <div class="col-4">
                            <label class="form-label fw-semibold">Max Int <span class="text-danger">*</span></label>
                            <input type="number" name="max_internal_marks" id="editSubjectInternal" class="form-control" required>
                        </div>
                        <div class="col-4">
                            <label class="form-label fw-semibold">Max Ext <span class="text-danger">*</span></label>
                            <input type="number" name="max_external_marks" id="editSubjectExternal" class="form-control" required>
                        </div>
                    </div>
                    <div class="row g-2 mb-3">
                        <div class="col-6">
                            <label class="form-label fw-semibold">Pass Marks <span class="text-danger">*</span></label>
                            <input type="number" name="pass_marks" id="editSubjectPass" class="form-control" required>
                        </div>
                        <div class="col-6">
                            <label class="form-label fw-semibold">Type <span class="text-danger">*</span></label>
                            <select name="subject_type" id="editSubjectType" class="form-select" required>
                                <option value="THEORY">THEORY</option>
                                <option value="PRACTICAL">PRACTICAL</option>
                                <option value="SESSIONAL">SESSIONAL</option>
                            </select>
                        </div>
                    </div>
                    <div class="mb-3">
                        <label class="form-label fw-semibold">Status <span class="text-danger">*</span></label>
                        <select name="status" id="editSubjectStatus" class="form-select" required>
                            <option value="ACTIVE">ACTIVE</option>
                            <option value="INACTIVE">INACTIVE</option>
                        </select>
                    </div>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancel</button>
                    <button type="submit" class="btn btn-primary">Update Subject</button>
                </div>
            </form>
        </div>
    </div>
</div>

<!-- Modal: Edit Session -->
<div class="modal fade" id="editSessionModal" tabindex="-1">
    <div class="modal-dialog">
        <div class="modal-content rounded-4 border-0 shadow">
            <div class="modal-header bg-primary text-white">
                <h5 class="modal-title fw-bold"><i class="bi bi-pencil-square me-1"></i> Edit Academic Session</h5>
                <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal"></button>
            </div>
            <form action="${pageContext.request.contextPath}/admin/academic-action" method="POST">
                <input type="hidden" name="entity" value="session">
                <input type="hidden" name="action" value="edit">
                <input type="hidden" name="id" id="editSessionId">
                <div class="modal-body p-4">
                    <div class="mb-3">
                        <label class="form-label fw-semibold">Session Code <span class="text-danger">*</span></label>
                        <input type="text" name="session_code" id="editSessionCode" class="form-control" placeholder="e.g. 2026-2027" required>
                    </div>
                    <div class="row g-2 mb-3">
                        <div class="col-6">
                            <label class="form-label fw-semibold">Start Year <span class="text-danger">*</span></label>
                            <input type="number" name="start_year" id="editSessionStartYear" class="form-control" required>
                        </div>
                        <div class="col-6">
                            <label class="form-label fw-semibold">End Year <span class="text-danger">*</span></label>
                            <input type="number" name="end_year" id="editSessionEndYear" class="form-control" required>
                        </div>
                    </div>
                    <div class="mb-3">
                        <label class="form-label fw-semibold">Status <span class="text-danger">*</span></label>
                        <select name="status" id="editSessionStatus" class="form-select" required>
                            <option value="ACTIVE">ACTIVE</option>
                            <option value="INACTIVE">INACTIVE</option>
                        </select>
                    </div>
                    <div class="form-check mb-2" id="editSessionCurrentWrapper">
                        <input class="form-check-input" type="checkbox" name="is_current" value="1" id="editSessionCurrent">
                        <label class="form-check-label" for="editSessionCurrent">Set as Current Active Session</label>
                    </div>
                    <div id="editSessionAlreadyCurrentNotice" class="text-success small d-none">
                        <i class="bi bi-check-circle-fill me-1"></i> This is the currently active academic session.
                    </div>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancel</button>
                    <button type="submit" class="btn btn-primary">Update Session</button>
                </div>
            </form>
        </div>
    </div>
</div>

<script>
function populateEditDept(elem) {
    if (!elem) return;
    const d = elem.dataset;
    document.getElementById('editDeptId').value = d.id || '';
    document.getElementById('editDeptCode').value = d.code || '';
    document.getElementById('editDeptName').value = d.name || '';
    document.getElementById('editDeptDescription').value = d.description || '';
    document.getElementById('editDeptStatus').value = d.status || 'ACTIVE';
    new bootstrap.Modal(document.getElementById('editDeptModal')).show();
}

function populateEditCourse(elem) {
    if (!elem) return;
    const d = elem.dataset;
    document.getElementById('editCourseId').value = d.id || '';
    document.getElementById('editCourseDept').value = d.dept || '';
    document.getElementById('editCourseCode').value = d.code || '';
    document.getElementById('editCourseName').value = d.name || '';
    document.getElementById('editCourseDuration').value = d.duration || '4';
    document.getElementById('editCourseSemesters').value = d.semesters || '8';
    document.getElementById('editCourseLevel').value = d.level || 'UG';
    document.getElementById('editCourseStatus').value = d.status || 'ACTIVE';
    new bootstrap.Modal(document.getElementById('editCourseModal')).show();
}

function filterSemestersByCourse(courseSelectId, semesterSelectId, selectedSemesterId) {
    const courseSelect = document.getElementById(courseSelectId);
    const semSelect = document.getElementById(semesterSelectId);
    if (!courseSelect || !semSelect) return;
    const courseId = courseSelect.value;
    let matched = false;
    for (let i = 0; i < semSelect.options.length; i++) {
        const opt = semSelect.options[i];
        if (!opt.value) continue;
        const optCourse = opt.getAttribute('data-course');
        if (!courseId || optCourse === courseId) {
            opt.style.display = '';
            opt.disabled = false;
            if (selectedSemesterId && opt.value === String(selectedSemesterId)) {
                opt.selected = true;
                matched = true;
            }
        } else {
            opt.style.display = 'none';
            opt.disabled = true;
            if (opt.selected) {
                opt.selected = false;
            }
        }
    }
    if (!matched && semSelect.selectedIndex === -1) {
        for (let i = 0; i < semSelect.options.length; i++) {
            const opt = semSelect.options[i];
            if (!opt.disabled && opt.value) {
                opt.selected = true;
                break;
            }
        }
    }
}

function populateEditSubject(elem) {
    if (!elem) return;
    const d = elem.dataset;
    document.getElementById('editSubjectId').value = d.id || '';
    document.getElementById('editSubjectCourse').value = d.course || '';
    filterSemestersByCourse('editSubjectCourse', 'editSubjectSemester', d.semester);
    document.getElementById('editSubjectCode').value = d.code || '';
    document.getElementById('editSubjectName').value = d.name || '';
    document.getElementById('editSubjectCredit').value = d.credit || '3.0';
    document.getElementById('editSubjectInternal').value = d.internal || '30';
    document.getElementById('editSubjectExternal').value = d.external || '70';
    document.getElementById('editSubjectPass').value = d.pass || '40';
    document.getElementById('editSubjectType').value = d.type || 'THEORY';
    document.getElementById('editSubjectStatus').value = d.status || 'ACTIVE';
    new bootstrap.Modal(document.getElementById('editSubjectModal')).show();
}

function populateEditSession(elem) {
    if (!elem) return;
    const d = elem.dataset;
    document.getElementById('editSessionId').value = d.id || '';
    document.getElementById('editSessionCode').value = d.code || '';
    document.getElementById('editSessionStartYear').value = d.start || '';
    document.getElementById('editSessionEndYear').value = d.end || '';
    document.getElementById('editSessionStatus').value = d.status || 'ACTIVE';
    const isCur = d.current === 'true' || d.current === '1';
    const curCheckbox = document.getElementById('editSessionCurrent');
    const notice = document.getElementById('editSessionAlreadyCurrentNotice');
    if (isCur) {
        curCheckbox.checked = true;
        curCheckbox.disabled = true;
        notice.classList.remove('d-none');
    } else {
        curCheckbox.checked = false;
        curCheckbox.disabled = false;
        notice.classList.add('d-none');
    }
    new bootstrap.Modal(document.getElementById('editSessionModal')).show();
}

document.addEventListener('DOMContentLoaded', function() {
    filterSemestersByCourse('addSubjectCourse', 'addSubjectSemester');
});
</script>

<jsp:include page="/WEB-INF/views/common/footer.jsp" />
