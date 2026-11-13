<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<jsp:include page="/WEB-INF/views/common/header.jsp">
    <jsp:param name="pageTitle" value="Marks & Results Management • Apex University" />
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
                    <h2 class="h3 fw-bold mb-1">Marks Evaluation &amp; Results Management</h2>
                    <p class="text-secondary small mb-0">Record internal (CIE) and external (ESE) marks, compute SGPA and letter grades, compile batch results, and publish.</p>
                </div>
                <c:if test="${not empty selectedExam}">
                    <div class="d-flex gap-2">
                        <form action="${pageContext.request.contextPath}/admin/marks-action" method="POST" class="d-inline">
                            <input type="hidden" name="action" value="compile_results">
                            <input type="hidden" name="exam_id" value="${selectedExam.id}">
                            <button type="submit" class="btn btn-primary btn-sm">
                                <i class="bi bi-calculator me-1"></i> Compile All Results
                            </button>
                        </form>

                        <form action="${pageContext.request.contextPath}/admin/marks-action" method="POST" class="d-inline">
                            <input type="hidden" name="action" value="publish_results">
                            <input type="hidden" name="exam_id" value="${selectedExam.id}">
                            <input type="hidden" name="publish" value="${selectedExam.resultPublished ? '0' : '1'}">
                            <button type="submit" class="btn ${selectedExam.resultPublished ? 'btn-danger' : 'btn-success'} btn-sm">
                                <i class="bi ${selectedExam.resultPublished ? 'bi-eye-slash' : 'bi-send-check'} me-1"></i>
                                ${selectedExam.resultPublished ? 'Unpublish Results' : 'Publish Results'}
                            </button>
                        </form>

                        <a href="${pageContext.request.contextPath}/export/csv?type=results&exam_id=${selectedExam.id}" class="btn btn-outline-success btn-sm">
                            <i class="bi bi-download me-1"></i> Export Results CSV
                        </a>
                    </div>
                </c:if>
            </div>

            <!-- Examination and Subject Selectors -->
            <div class="card shadow-sm border rounded-4 p-4 mb-4">
                <form action="${pageContext.request.contextPath}/admin/marks" method="GET" class="row g-3 align-items-end">
                    <div class="col-md-5 col-12">
                        <label class="form-label fw-semibold">Select Examination Session</label>
                        <select name="exam_id" class="form-select" required onchange="this.form.submit()">
                            <option value="">-- Choose Examination --</option>
                            <c:forEach var="ex" items="${exams}">
                                <option value="${ex.id}" ${selectedExam.id == ex.id ? 'selected' : ''}>
                                    ${ex.examName} (${ex.examCode})
                                </option>
                            </c:forEach>
                        </select>
                    </div>

                    <div class="col-md-5 col-12">
                        <label class="form-label fw-semibold">Select Subject</label>
                        <select name="subject_id" class="form-select">
                            <option value="">-- View Compiled Results / Choose Subject --</option>
                            <c:forEach var="sub" items="${subjects}">
                                <option value="${sub.id}" ${selectedSubjectId == sub.id ? 'selected' : ''}>
                                    ${sub.subjectCode} - ${sub.name} (Max: ${sub.maxInternalMarks}+${sub.maxExternalMarks})
                                </option>
                            </c:forEach>
                        </select>
                    </div>

                    <div class="col-md-2 col-12">
                        <button type="submit" class="btn btn-apex-primary w-100">Load Sheet</button>
                    </div>
                </form>
            </div>

            <!-- Marks Entry Table (When Subject Selected) -->
            <c:if test="${not empty selectedSubjectId && not empty selectedExam}">
                <div class="card shadow-sm border rounded-4 p-4 mb-4">
                    <h4 class="h5 fw-bold mb-3">
                        <i class="bi bi-pencil-fill me-1 text-primary"></i> Mark Evaluation Sheet for Selected Subject
                    </h4>

                    <div class="table-responsive">
                        <table class="table table-hover align-middle mb-0 apex-table apex-table-compact">
                            <thead>
                                <tr>
                                    <th>Roll Number</th>
                                    <th>Student Name</th>
                                    <th>Internal (CIE)</th>
                                    <th>External (ESE)</th>
                                    <th>Total</th>
                                    <th>Grade</th>
                                    <th>Pass/Fail</th>
                                    <th class="text-center">Action</th>
                                </tr>
                            </thead>
                            <tbody>
                                <c:forEach var="st" items="${students}">
                                    <!-- Look for existing mark in list -->
                                    <c:set var="mRecord" value="${null}" />
                                    <c:forEach var="m" items="${marks}">
                                        <c:if test="${m.studentId == st.id}">
                                            <c:set var="mRecord" value="${m}" />
                                        </c:if>
                                    </c:forEach>

                                    <tr>
                                        <td class="fw-bold">${st.rollNo}</td>
                                        <td>${st.fullName}</td>
                                        <form action="${pageContext.request.contextPath}/admin/marks-action" method="POST">
                                            <input type="hidden" name="action" value="upload_mark">
                                            <input type="hidden" name="exam_id" value="${selectedExam.id}">
                                            <input type="hidden" name="subject_id" value="${selectedSubjectId}">
                                            <input type="hidden" name="student_id" value="${st.id}">

                                            <td>
                                                <input type="number" step="0.5" name="internal_marks" class="form-control form-control-sm" style="width: 90px;" 
                                                       value="${mRecord != null ? mRecord.internalMarks : 0}" required>
                                            </td>
                                            <td>
                                                <input type="number" step="0.5" name="external_marks" class="form-control form-control-sm" style="width: 90px;" 
                                                       value="${mRecord != null ? mRecord.externalMarks : 0}" required>
                                            </td>
                                            <td class="fw-bold">${mRecord != null ? mRecord.totalMarks : '-'}</td>
                                            <td><span class="badge bg-light text-dark border">${mRecord != null ? mRecord.letterGrade : '-'}</span></td>
                                            <td>
                                                <c:if test="${mRecord != null}">
                                                    <span class="badge ${mRecord.pass ? 'bg-success' : 'bg-danger'}">${mRecord.pass ? 'PASS' : 'FAIL'}</span>
                                                </c:if>
                                            </td>
                                            <td class="text-center">
                                                <button type="submit" class="btn btn-sm btn-outline-primary">Save Mark</button>
                                            </td>
                                        </form>
                                    </tr>
                                </c:forEach>
                            </tbody>
                        </table>
                    </div>
                </div>
            </c:if>

            <!-- Compiled Results Table (When Exam Selected) -->
            <c:if test="${not empty selectedExam}">
                <div class="card shadow-sm border rounded-4 p-4 mb-4">
                    <div class="d-flex justify-content-between align-items-center mb-3">
                        <h4 class="h5 fw-bold mb-0">Compiled Semester Results: ${selectedExam.examName}</h4>
                        <div>
                            <span class="badge ${selectedExam.resultPublished ? 'bg-success' : 'bg-warning text-dark'} fs-6">
                                ${selectedExam.resultPublished ? 'Status: Published Online' : 'Status: Unpublished Draft'}
                            </span>
                        </div>
                    </div>

                    <div class="table-responsive">
                        <table class="table table-hover align-middle mb-0 apex-table apex-table-compact">
                            <thead>
                                <tr>
                                    <th>Registration No</th>
                                    <th>Roll No</th>
                                    <th>Student Name</th>
                                    <th>Total Obtained</th>
                                    <th>Percentage</th>
                                    <th>SGPA</th>
                                    <th>Result Status</th>
                                    <th class="text-center text-nowrap" style="width: 170px; min-width: 170px;">Action</th>
                                </tr>
                            </thead>
                            <tbody>
                                <c:forEach var="res" items="${results}">
                                    <tr>
                                        <td class="fw-bold text-primary">${res.registrationNo}</td>
                                        <td>${res.rollNo}</td>
                                        <td>${res.studentName}</td>
                                        <td class="fw-bold">${res.totalObtained} / ${res.totalMax}</td>
                                        <td>${res.percentage} %</td>
                                        <td><span class="badge bg-success fs-6">${res.sgpa}</span></td>
                                        <td>
                                            <span class="badge ${res.resultStatus == 'PASS' ? 'bg-success' : 'bg-danger'}">${res.resultStatus}</span>
                                        </td>
                                        <td class="text-center text-nowrap">
                                            <div class="table-actions">
                                                <a href="${pageContext.request.contextPath}/export/pdf?type=marksheet&exam_id=${res.examId}&student_id=${res.studentId}" class="btn btn-sm btn-outline-primary" title="Marksheet PDF">
                                                    <i class="bi bi-file-earmark-pdf"></i> PDF
                                                </a>
                                                <button type="button" class="btn btn-sm btn-outline-primary btn-icon-only"
                                                        data-id="${res.id}"
                                                        data-student="<c:out value='${res.studentName}' />"
                                                        data-reg="<c:out value='${res.registrationNo}' />"
                                                        data-obtained="${res.totalObtained}"
                                                        data-max="${res.totalMax}"
                                                        data-sgpa="${res.sgpa}"
                                                        data-status="${res.resultStatus}"
                                                        onclick="populateEditResult(this)"
                                                        title="Edit Compiled Result">
                                                    <i class="bi bi-pencil-square"></i>
                                                </button>
                                            </div>
                                        </td>
                                    </tr>
                                </c:forEach>
                                <c:if test="${empty results}">
                                    <tr><td colspan="8" class="text-center py-4 text-muted">No compiled results found. Click "Compile All Results" button above once marks are recorded.</td></tr>
                                </c:if>
                            </tbody>
                        </table>
                    </div>
                </div>
            </c:if>

        </div>
    </main>
</div>

<!-- Modal: Edit Compiled Result -->
<c:if test="${not empty selectedExam}">
<div class="modal fade" id="editResultModal" tabindex="-1">
    <div class="modal-dialog">
        <div class="modal-content rounded-4 border-0 shadow">
            <div class="modal-header bg-primary text-white">
                <h5 class="modal-title fw-bold">Edit Student Compiled Result</h5>
                <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal"></button>
            </div>
            <form action="${pageContext.request.contextPath}/admin/marks-action" method="POST">
                <input type="hidden" name="action" value="edit_result">
                <input type="hidden" name="result_id" id="editResultId">
                <input type="hidden" name="exam_id" value="${selectedExam.id}">
                <div class="modal-body p-4">
                    <div class="alert alert-light border py-2 px-3 mb-3 small">
                        <strong>Student: </strong><span id="editResultStudentInfo"></span>
                    </div>
                    <div class="row g-2 mb-3">
                        <div class="col-6">
                            <label class="form-label fw-semibold">Total Obtained Marks</label>
                            <input type="number" step="0.5" name="total_obtained" id="editResultObtained" class="form-control" required>
                        </div>
                        <div class="col-6">
                            <label class="form-label fw-semibold">Total Maximum Marks</label>
                            <input type="number" step="0.5" name="total_max" id="editResultMax" class="form-control" required>
                        </div>
                    </div>
                    <div class="row g-2 mb-3">
                        <div class="col-6">
                            <label class="form-label fw-semibold">SGPA (0.00 - 10.00)</label>
                            <input type="number" step="0.01" min="0" max="10" name="sgpa" id="editResultSgpa" class="form-control" required>
                        </div>
                        <div class="col-6">
                            <label class="form-label fw-semibold">Result Status</label>
                            <select name="result_status" id="editResultStatus" class="form-select" required>
                                <option value="PASS">PASS</option>
                                <option value="FAIL">FAIL</option>
                                <option value="WITHHELD">WITHHELD</option>
                            </select>
                        </div>
                    </div>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancel</button>
                    <button type="submit" class="btn btn-primary">Update Result</button>
                </div>
            </form>
        </div>
    </div>
</div>
</c:if>

<script>
function populateEditResult(elem) {
    if (!elem) return;
    const d = elem.dataset;
    document.getElementById('editResultId').value = d.id || '';
    const infoElem = document.getElementById('editResultStudentInfo');
    if (infoElem) {
        infoElem.textContent = (d.student || '') + ' (' + (d.reg || '') + ')';
    }
    document.getElementById('editResultObtained').value = d.obtained || '0';
    document.getElementById('editResultMax').value = d.max || '100';
    document.getElementById('editResultSgpa').value = d.sgpa || '0.00';
    document.getElementById('editResultStatus').value = d.status || 'PASS';
    new bootstrap.Modal(document.getElementById('editResultModal')).show();
}
</script>

<jsp:include page="/WEB-INF/views/common/footer.jsp" />
