<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<jsp:include page="/WEB-INF/views/common/header.jsp">
    <jsp:param name="pageTitle" value="College Management • Apex University" />
</jsp:include>

<div class="dashboard-wrapper">
    <jsp:include page="/WEB-INF/views/common/sidebar.jsp">
        <jsp:param name="activeNav" value="colleges" />
    </jsp:include>

    <main class="dashboard-content">
        <div class="container-fluid">
            
            <jsp:include page="/WEB-INF/views/common/alerts.jsp" />

            <div class="d-flex justify-content-between align-items-center mb-4 flex-wrap gap-2">
                <div>
                    <h2 class="h3 fw-bold mb-1">Affiliated College Management</h2>
                    <p class="text-secondary small mb-0">Manage institutions, audit college-wise student rosters, and oversee institutional clearance accounts.</p>
                </div>
                <button type="button" class="btn btn-apex-primary btn-sm" data-bs-toggle="modal" data-bs-target="#addCollegeModal">
                    <i class="bi bi-building-add me-1"></i> Register New College
                </button>
            </div>

            <!-- Colleges List Table -->
            <div class="card shadow-sm border rounded-4 overflow-hidden mb-4">
                <div class="table-responsive">
                    <table class="table table-hover align-middle mb-0 apex-table apex-table-compact">
                        <thead>
                            <tr>
                                <th style="width: 110px;">College Code</th>
                                <th style="min-width: 180px;">College / Institution Name</th>
                                <th style="width: 160px;">Principal / Director</th>
                                <th style="min-width: 180px;">Contact Information</th>
                                <th style="min-width: 170px;">Campus Address</th>
                                <th class="text-center" style="width: 100px;">Enrolled Students</th>
                                <th class="text-center" style="width: 90px;">Status</th>
                                <th class="text-center text-nowrap" style="width: 175px; min-width: 175px;">Actions</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach var="c" items="${colleges}">
                                <tr>
                                    <td class="fw-bold text-primary text-nowrap">${c.collegeCode}</td>
                                    <td>
                                        <div class="fw-bold">${c.name}</div>
                                        <c:if test="${not empty c.website}">
                                            <a href="${c.website}" target="_blank" class="small text-muted text-decoration-none">
                                                <i class="bi bi-globe me-1"></i> ${c.website}
                                            </a>
                                        </c:if>
                                    </td>
                                    <td>${c.principalName}</td>
                                    <td>
                                        <div class="small text-break"><i class="bi bi-envelope me-1"></i>${c.email}</div>
                                        <div class="small text-nowrap"><i class="bi bi-telephone me-1"></i>${c.phone}</div>
                                    </td>
                                    <td><small class="text-muted text-break">${c.address}</small></td>
                                    <td class="text-center"><span class="badge bg-primary fs-6">${c.studentCount}</span></td>
                                    <td class="text-center"><span class="badge ${c.status == 'ACTIVE' ? 'bg-success' : 'bg-danger'}">${c.status}</span></td>
                                    <td class="text-center text-nowrap">
                                        <div class="table-actions">
                                            <a href="${pageContext.request.contextPath}/admin/colleges?view_students=${c.id}" class="btn btn-sm btn-outline-info" title="View Students">
                                                <i class="bi bi-people me-1"></i> Roster
                                            </a>
                                            <button type="button" class="btn btn-sm btn-outline-primary btn-icon-only"
                                                    data-id="${c.id}"
                                                    data-code="<c:out value='${c.collegeCode}' />"
                                                    data-name="<c:out value='${c.name}' />"
                                                    data-email="<c:out value='${c.email}' />"
                                                    data-phone="<c:out value='${c.phone}' />"
                                                    data-principal="<c:out value='${c.principalName}' />"
                                                    data-website="<c:out value='${c.website}' />"
                                                    data-address="<c:out value='${c.address}' />"
                                                    data-status="${c.status}"
                                                    onclick="populateEditCollege(this)"
                                                    title="Edit College">
                                                <i class="bi bi-pencil-square"></i>
                                            </button>
                                            <form action="${pageContext.request.contextPath}/admin/college-action" method="POST">
                                                <input type="hidden" name="action" value="delete">
                                                <input type="hidden" name="id" value="${c.id}">
                                                <button type="submit" class="btn btn-sm btn-outline-danger btn-icon-only btn-confirm-delete" data-confirm="Are you sure you want to delete ${c.name}?" title="Delete College">
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

            <!-- College Students Roster (When selected) -->
            <c:if test="${not empty selectedCollege}">
                <div class="card shadow-sm border border-primary rounded-4 p-4 mb-4">
                    <div class="d-flex justify-content-between align-items-center mb-3">
                        <h4 class="h5 fw-bold text-primary mb-0">
                            <i class="bi bi-mortarboard me-1"></i> Enrolled Students at ${selectedCollege.name} (${selectedCollege.collegeCode})
                        </h4>
                        <a href="${pageContext.request.contextPath}/export/csv?type=students&college_id=${selectedCollege.id}" class="btn btn-outline-success btn-sm">
                            <i class="bi bi-download me-1"></i> Download College Student List (CSV)
                        </a>
                    </div>

                    <div class="table-responsive">
                        <table class="table table-bordered table-sm align-middle small mb-0">
                            <thead class="table-light">
                                <tr>
                                    <th>Registration No</th>
                                    <th>Roll No</th>
                                    <th>Student Name</th>
                                    <th>Course</th>
                                    <th>Semester</th>
                                    <th>Status</th>
                                </tr>
                            </thead>
                            <tbody>
                                <c:forEach var="s" items="${collegeStudents}">
                                    <tr>
                                        <td class="fw-bold">${s.registrationNo}</td>
                                        <td>${s.rollNo}</td>
                                        <td>${s.fullName}</td>
                                        <td>${s.courseName}</td>
                                        <td>Semester ${s.semesterNumber}</td>
                                        <td><span class="badge bg-success">${s.status}</span></td>
                                    </tr>
                                </c:forEach>
                                <c:if test="${empty collegeStudents}">
                                    <tr><td colspan="6" class="text-center py-3 text-muted">No students registered under this college yet.</td></tr>
                                </c:if>
                            </tbody>
                        </table>
                    </div>
                </div>
            </c:if>

        </div>
    </main>
</div>

<!-- Add College Modal -->
<div class="modal fade" id="addCollegeModal" tabindex="-1">
    <div class="modal-dialog">
        <div class="modal-content rounded-4 border-0 shadow">
            <div class="modal-header bg-primary text-white">
                <h5 class="modal-title fw-bold"><i class="bi bi-building-add me-1"></i> Register Affiliated College</h5>
                <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal"></button>
            </div>
            <form action="${pageContext.request.contextPath}/admin/college-action" method="POST">
                <input type="hidden" name="action" value="add">
                <div class="modal-body p-4">
                    <div class="row g-3">
                        <div class="col-12">
                            <label class="form-label fw-semibold">College Code <span class="text-muted small">(Leave empty for auto-generation)</span></label>
                            <input type="text" name="college_code" class="form-control" placeholder="e.g. COL103">
                        </div>
                        <div class="col-12">
                            <label class="form-label fw-semibold">College Name <span class="text-danger">*</span></label>
                            <input type="text" name="name" class="form-control" placeholder="Apex College of Engineering..." required>
                        </div>
                        <div class="col-md-6 col-12">
                            <label class="form-label fw-semibold">Official Email <span class="text-danger">*</span></label>
                            <input type="email" name="email" class="form-control" required>
                        </div>
                        <div class="col-md-6 col-12">
                            <label class="form-label fw-semibold">Phone <span class="text-danger">*</span></label>
                            <input type="text" name="phone" class="form-control" required>
                        </div>
                        <div class="col-md-6 col-12">
                            <label class="form-label fw-semibold">Principal / Director Name</label>
                            <input type="text" name="principal_name" class="form-control">
                        </div>
                        <div class="col-md-6 col-12">
                            <label class="form-label fw-semibold">Official Website</label>
                            <input type="url" name="website" class="form-control" placeholder="https://...">
                        </div>
                        <div class="col-12">
                            <label class="form-label fw-semibold">Campus Address <span class="text-danger">*</span></label>
                            <textarea name="address" class="form-control" rows="2" required></textarea>
                        </div>
                    </div>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancel</button>
                    <button type="submit" class="btn btn-primary">Enroll College</button>
                </div>
            </form>
        </div>
    </div>
</div>

<!-- Edit College Modal -->
<div class="modal fade" id="editCollegeModal" tabindex="-1">
    <div class="modal-dialog">
        <div class="modal-content rounded-4 border-0 shadow">
            <div class="modal-header bg-primary text-white">
                <h5 class="modal-title fw-bold"><i class="bi bi-pencil-square me-1"></i> Edit Affiliated College</h5>
                <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal"></button>
            </div>
            <form action="${pageContext.request.contextPath}/admin/college-action" method="POST">
                <input type="hidden" name="action" value="edit">
                <input type="hidden" name="id" id="editCollegeId">
                <div class="modal-body p-4">
                    <div class="row g-3">
                        <div class="col-12">
                            <label class="form-label fw-semibold">College Code <span class="text-danger">*</span></label>
                            <input type="text" name="college_code" id="editCollegeCode" class="form-control" required>
                        </div>
                        <div class="col-12">
                            <label class="form-label fw-semibold">College Name <span class="text-danger">*</span></label>
                            <input type="text" name="name" id="editCollegeName" class="form-control" required>
                        </div>
                        <div class="col-md-6 col-12">
                            <label class="form-label fw-semibold">Official Email <span class="text-danger">*</span></label>
                            <input type="email" name="email" id="editCollegeEmail" class="form-control" required>
                        </div>
                        <div class="col-md-6 col-12">
                            <label class="form-label fw-semibold">Phone <span class="text-danger">*</span></label>
                            <input type="text" name="phone" id="editCollegePhone" class="form-control" required>
                        </div>
                        <div class="col-md-6 col-12">
                            <label class="form-label fw-semibold">Principal / Director Name</label>
                            <input type="text" name="principal_name" id="editCollegePrincipal" class="form-control">
                        </div>
                        <div class="col-md-6 col-12">
                            <label class="form-label fw-semibold">Official Website</label>
                            <input type="url" name="website" id="editCollegeWebsite" class="form-control" placeholder="https://...">
                        </div>
                        <div class="col-12">
                            <label class="form-label fw-semibold">Campus Address <span class="text-danger">*</span></label>
                            <textarea name="address" id="editCollegeAddress" class="form-control" rows="2" required></textarea>
                        </div>
                        <div class="col-12">
                            <label class="form-label fw-semibold">Status <span class="text-danger">*</span></label>
                            <select name="status" id="editCollegeStatus" class="form-select" required>
                                <option value="ACTIVE">ACTIVE</option>
                                <option value="INACTIVE">INACTIVE</option>
                            </select>
                        </div>
                    </div>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancel</button>
                    <button type="submit" class="btn btn-primary">Update College</button>
                </div>
            </form>
        </div>
    </div>
</div>

<script>
function populateEditCollege(elemOrId, code, name, email, phone, principal, website, address, status) {
    if (typeof elemOrId === 'object' && elemOrId !== null) {
        var d = elemOrId.dataset;
        document.getElementById('editCollegeId').value = d.id || '';
        document.getElementById('editCollegeCode').value = d.code || '';
        document.getElementById('editCollegeName').value = d.name || '';
        document.getElementById('editCollegeEmail').value = d.email || '';
        document.getElementById('editCollegePhone').value = d.phone || '';
        document.getElementById('editCollegePrincipal').value = d.principal || '';
        document.getElementById('editCollegeWebsite').value = d.website || '';
        document.getElementById('editCollegeAddress').value = d.address || '';
        document.getElementById('editCollegeStatus').value = d.status || 'ACTIVE';
    } else {
        document.getElementById('editCollegeId').value = elemOrId || '';
        document.getElementById('editCollegeCode').value = code || '';
        document.getElementById('editCollegeName').value = name || '';
        document.getElementById('editCollegeEmail').value = email || '';
        document.getElementById('editCollegePhone').value = phone || '';
        document.getElementById('editCollegePrincipal').value = principal || '';
        document.getElementById('editCollegeWebsite').value = website || '';
        document.getElementById('editCollegeAddress').value = address || '';
        document.getElementById('editCollegeStatus').value = status || 'ACTIVE';
    }
    new bootstrap.Modal(document.getElementById('editCollegeModal')).show();
}
</script>

<jsp:include page="/WEB-INF/views/common/footer.jsp" />
