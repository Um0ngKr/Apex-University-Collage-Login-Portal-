<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<jsp:include page="/WEB-INF/views/common/header.jsp">
    <jsp:param name="pageTitle" value="Internal Marks Upload • Apex University" />
</jsp:include>

<div class="dashboard-wrapper">
    <jsp:include page="/WEB-INF/views/common/sidebar.jsp">
        <jsp:param name="activeNav" value="marks" />
    </jsp:include>

    <main class="dashboard-content">
        <div class="container-fluid">
            
            <jsp:include page="/WEB-INF/views/common/alerts.jsp" />

            <div class="d-flex justify-content-between align-items-center mb-4 flex-wrap gap-2">
                <div>
                    <h2 class="h3 fw-bold mb-1">Continuous Internal Evaluation (CIE) Marks</h2>
                    <p class="text-secondary small mb-0">Record and submit mid-term and internal assessment marks for your assigned subjects.</p>
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

            <!-- Subject & Exam Selector -->
            <div class="card shadow-sm border rounded-4 p-4 mb-4">
                <form action="${pageContext.request.contextPath}/teacher/marks" method="GET" class="row g-3 align-items-end">
                    <div class="col-md-5 col-12">
                        <label class="form-label fw-semibold">Examination</label>
                        <select name="exam_id" class="form-select" required ${empty exams ? 'disabled' : ''}>
                            <option value="">-- Choose Examination --</option>
                            <c:forEach var="ex" items="${exams}">
                                <option value="${ex.id}" ${selectedExamId == ex.id ? 'selected' : ''}>
                                    ${ex.examName} (${ex.examCode})
                                </option>
                            </c:forEach>
                        </select>
                    </div>

                    <div class="col-md-5 col-12">
                        <label class="form-label fw-semibold">Assigned Subject</label>
                        <select name="subject_id" class="form-select" required ${empty assignments ? 'disabled' : ''}>
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

                    <div class="col-md-2 col-12">
                        <button type="submit" class="btn btn-apex-primary w-100" ${empty assignments ? 'disabled' : ''}>Load Roster</button>
                    </div>
                </form>
            </div>

            <!-- Marks Entry Table -->
            <c:if test="${not empty selectedExamId && not empty selectedSubjectId}">
                <div class="card shadow-sm border rounded-4 p-4 mb-4">
                    <div class="mb-3">
                        <h4 class="h5 fw-bold mb-1">Internal Assessment Entry Sheet</h4>
                        <c:if test="${not empty selectedAssignment}">
                            <small class="text-muted">
                                ${selectedAssignment.subjectCode} - ${selectedAssignment.subjectName} | ${selectedAssignment.courseName} (Sem ${selectedAssignment.semesterNumber}, Section ${selectedAssignment.section})
                            </small>
                        </c:if>
                    </div>

                    <div class="table-responsive">
                        <table class="table table-hover align-middle mb-0 apex-table">
                            <thead>
                                <tr>
                                    <th>Roll Number</th>
                                    <th>Student Name</th>
                                    <th>Internal Marks (Max 30)</th>
                                    <th>Current Status</th>
                                    <th class="text-center">Action</th>
                                </tr>
                            </thead>
                            <tbody>
                                <c:forEach var="st" items="${students}">
                                    <c:set var="mRecord" value="${null}" />
                                    <c:forEach var="m" items="${marks}">
                                        <c:if test="${m.studentId == st.id}">
                                            <c:set var="mRecord" value="${m}" />
                                        </c:if>
                                    </c:forEach>

                                    <tr>
                                        <td class="fw-bold">${st.rollNo}</td>
                                        <td>${st.fullName}</td>
                                        <form action="${pageContext.request.contextPath}/teacher/marks-action" method="POST">
                                            <input type="hidden" name="exam_id" value="${selectedExamId}">
                                            <input type="hidden" name="subject_id" value="${selectedSubjectId}">
                                            <input type="hidden" name="student_id" value="${st.id}">

                                            <td>
                                                <input type="number" step="0.5" name="internal_marks" class="form-control form-control-sm" style="width: 110px;" 
                                                       value="${mRecord != null ? mRecord.internalMarks : 0}" max="40" required>
                                            </td>
                                            <td>
                                                <c:choose>
                                                    <c:when test="${mRecord != null}">
                                                        <span class="badge bg-success">Recorded: ${mRecord.internalMarks} pts</span>
                                                    </c:when>
                                                    <c:otherwise>
                                                        <span class="badge bg-secondary">Pending Entry</span>
                                                    </c:otherwise>
                                                </c:choose>
                                            </td>
                                            <td class="text-center">
                                                <button type="submit" class="btn btn-sm btn-outline-primary">
                                                    <i class="bi bi-save me-1"></i> Save
                                                </button>
                                            </td>
                                        </form>
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

<jsp:include page="/WEB-INF/views/common/footer.jsp" />
