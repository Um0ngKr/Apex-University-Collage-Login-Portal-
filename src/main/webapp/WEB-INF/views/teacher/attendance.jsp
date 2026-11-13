<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<jsp:include page="/WEB-INF/views/common/header.jsp">
    <jsp:param name="pageTitle" value="Faculty Attendance Marking • Apex University" />
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
                    <h2 class="h3 fw-bold mb-1">Class Attendance Recording</h2>
                    <p class="text-secondary small mb-0">Record daily classroom attendance for your assigned teaching subjects.</p>
                </div>
            </div>

            <!-- Empty Assignments Notice -->
            <c:if test="${empty assignments}">
                <div class="alert alert-warning d-flex align-items-center mb-4 rounded-3 shadow-sm" role="alert">
                    <i class="bi bi-exclamation-triangle-fill flex-shrink-0 me-2 fs-5"></i>
                    <div>
                        No subjects have been assigned to you for the current academic session. Please contact the administrator.
                    </div>
                </div>
            </c:if>

            <!-- Subject & Date Selector Form -->
            <div class="card shadow-sm border rounded-4 p-4 mb-4">
                <form action="${pageContext.request.contextPath}/teacher/attendance" method="GET" class="row g-3 align-items-end">
                    <div class="col-md-6 col-12">
                        <label class="form-label fw-semibold">Select Assigned Subject</label>
                        <select name="subject_id" class="form-select" required onchange="this.form.submit()" ${empty assignments ? 'disabled' : ''}>
                            <c:choose>
                                <c:when test="${empty assignments}">
                                    <option value="">-- No subjects assigned --</option>
                                </c:when>
                                <c:otherwise>
                                    <option value="">-- Choose Assigned Subject --</option>
                                    <c:forEach var="asg" items="${assignments}">
                                        <option value="${asg.subjectId}" ${selectedSubjectId == asg.subjectId ? 'selected' : ''}>
                                            ${asg.subjectCode} - ${asg.subjectName} (${asg.courseName} Sem ${asg.semesterNumber})
                                        </option>
                                    </c:forEach>
                                </c:otherwise>
                            </c:choose>
                        </select>
                    </div>

                    <div class="col-md-4 col-12">
                        <label class="form-label fw-semibold">Class Date</label>
                        <input type="date" name="class_date" class="form-control" value="${selectedDate}">
                    </div>

                    <div class="col-md-2 col-12">
                        <button type="submit" class="btn btn-apex-primary w-100" ${empty assignments ? 'disabled' : ''}>Load Students</button>
                    </div>
                </form>
            </div>

            <!-- Student Attendance Marking Sheet -->
            <c:if test="${not empty selectedSubjectId}">
                <div class="card shadow-sm border rounded-4 p-4 mb-4">
                    <form action="${pageContext.request.contextPath}/teacher/attendance-action" method="POST">
                        <input type="hidden" name="subject_id" value="${selectedSubjectId}">
                        <input type="hidden" name="session_id" value="${selectedSessionId != null ? selectedSessionId : 2}">
                        <input type="hidden" name="class_date" value="${selectedDate}">

                        <div class="d-flex justify-content-between align-items-center mb-3">
                            <div>
                                <h4 class="h5 fw-bold mb-1">Student Attendance Roster for Date: ${selectedDate}</h4>
                                <c:if test="${not empty selectedAssignment}">
                                    <small class="text-muted">
                                        ${selectedAssignment.subjectCode} - ${selectedAssignment.subjectName} | ${selectedAssignment.courseName} (Sem ${selectedAssignment.semesterNumber}, Section ${selectedAssignment.section})
                                    </small>
                                </c:if>
                            </div>
                            <button type="submit" class="btn btn-success px-4">
                                <i class="bi bi-check2-circle me-1"></i> Save Attendance
                            </button>
                        </div>
                                <i class="bi bi-check2-circle me-1"></i> Save Attendance
                            </button>
                        </div>

                        <div class="table-responsive">
                            <table class="table table-hover align-middle mb-0 apex-table">
                                <thead>
                                    <tr>
                                        <th>Roll Number</th>
                                        <th>Student Name</th>
                                        <th>College</th>
                                        <th class="text-center" style="width: 250px;">Status</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <c:forEach var="st" items="${students}">
                                        <input type="hidden" name="student_id" value="${st.id}">
                                        <tr>
                                            <td class="fw-bold">${st.rollNo}</td>
                                            <td>${st.fullName}</td>
                                            <td><small class="text-muted">${st.collegeName}</small></td>
                                            <td class="text-center">
                                                <div class="btn-group btn-group-sm" role="group">
                                                    <input type="radio" class="btn-check" name="status_${st.id}" id="present_${st.id}" value="PRESENT" checked>
                                                    <label class="btn btn-outline-success" for="present_${st.id}">Present</label>

                                                    <input type="radio" class="btn-check" name="status_${st.id}" id="absent_${st.id}" value="ABSENT">
                                                    <label class="btn btn-outline-danger" for="absent_${st.id}">Absent</label>

                                                    <input type="radio" class="btn-check" name="status_${st.id}" id="late_${st.id}" value="LATE">
                                                    <label class="btn btn-outline-warning" for="late_${st.id}">Late</label>
                                                </div>
                                            </td>
                                        </tr>
                                    </c:forEach>
                                    <c:if test="${empty students}">
                                        <tr><td colspan="4" class="text-center py-4 text-muted">No students currently registered in this semester.</td></tr>
                                    </c:if>
                                </tbody>
                            </table>
                        </div>

                        <div class="mt-4 text-end">
                            <button type="submit" class="btn btn-success px-4 py-2">
                                <i class="bi bi-check2-circle me-1"></i> Save Attendance
                            </button>
                        </div>
                    </form>
                </div>
            </c:if>

        </div>
    </main>
</div>

<jsp:include page="/WEB-INF/views/common/footer.jsp" />
