<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<jsp:include page="/WEB-INF/views/common/header.jsp">
    <jsp:param name="pageTitle" value="Attendance Records • Apex University" />
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
                    <h2 class="h3 fw-bold mb-1">Central Attendance Records</h2>
                    <p class="text-secondary small mb-0">Audit faculty-recorded classroom attendance, verify 75% examination eligibility thresholds.</p>
                </div>
            </div>

            <!-- Subject & Date Selector -->
            <div class="card shadow-sm border rounded-4 p-4 mb-4">
                <form action="${pageContext.request.contextPath}/admin/attendance" method="GET" class="row g-3 align-items-end">
                    <div class="col-md-6 col-12">
                        <label class="form-label fw-semibold">Subject</label>
                        <select name="subject_id" class="form-select" required>
                            <option value="">-- Choose Subject --</option>
                            <c:forEach var="sub" items="${subjects}">
                                <option value="${sub.id}" ${selectedSubjectId == sub.id ? 'selected' : ''}>
                                    ${sub.subjectCode} - ${sub.name} (${sub.courseName})
                                </option>
                            </c:forEach>
                        </select>
                    </div>

                    <div class="col-md-4 col-12">
                        <label class="form-label fw-semibold">Class Date</label>
                        <input type="date" name="class_date" class="form-control" value="${selectedDate}">
                    </div>

                    <div class="col-md-2 col-12">
                        <button type="submit" class="btn btn-apex-primary w-100">Fetch Roster</button>
                    </div>
                </form>
            </div>

            <!-- Attendance Records Table -->
            <div class="card shadow-sm border rounded-4 overflow-hidden mb-4">
                <div class="table-responsive">
                    <table class="table table-hover align-middle mb-0 apex-table">
                        <thead>
                            <tr>
                                <th>Roll Number</th>
                                <th>Registration No</th>
                                <th>Student Name</th>
                                <th>Class Date</th>
                                <th>Attendance Status</th>
                                <th>Marked By Faculty</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach var="rec" items="${records}">
                                <tr>
                                    <td class="fw-bold">${rec.rollNo}</td>
                                    <td>${rec.registrationNo}</td>
                                    <td>${rec.studentName}</td>
                                    <td>${rec.classDate}</td>
                                    <td>
                                        <span class="badge ${rec.status == 'PRESENT' ? 'bg-success' : 'bg-danger'} px-3 py-2">
                                            ${rec.status}
                                        </span>
                                    </td>
                                    <td><small class="text-muted">${rec.teacherName != null ? rec.teacherName : 'Academic Section'}</small></td>
                                </tr>
                            </c:forEach>
                            <c:if test="${empty records}">
                                <tr><td colspan="6" class="text-center py-5 text-muted">Select a subject and date above to inspect attendance logs.</td></tr>
                            </c:if>
                        </tbody>
                    </table>
                </div>
            </div>

        </div>
    </main>
</div>

<jsp:include page="/WEB-INF/views/common/footer.jsp" />
