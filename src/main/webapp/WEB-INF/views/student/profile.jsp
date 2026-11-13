<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<jsp:include page="/WEB-INF/views/common/header.jsp">
    <jsp:param name="pageTitle" value="Student Profile • Apex University" />
</jsp:include>

<div class="dashboard-wrapper">
    <jsp:include page="/WEB-INF/views/common/sidebar.jsp">
        <jsp:param name="activeNav" value="profile" />
    </jsp:include>

    <main class="dashboard-content">
        <div class="container-fluid">
            
            <jsp:include page="/WEB-INF/views/common/alerts.jsp" />

            <div class="row g-4">
                <!-- Academic & Personal Dossier -->
                <div class="col-lg-7 col-12">
                    <div class="card shadow-sm border rounded-4 p-4">
                        <div class="d-flex align-items-center gap-3 mb-4 pb-3 border-bottom">
                            <div class="bg-primary text-white rounded-circle d-flex align-items-center justify-content-center fw-bold fs-2" style="width: 70px; height: 70px;">
                                ${student.fullName.substring(0, 1)}
                            </div>
                            <div>
                                <h4 class="fw-bold mb-1">${student.fullName}</h4>
                                <span class="badge bg-primary me-2">Reg: ${student.registrationNo}</span>
                                <span class="badge bg-secondary">Roll: ${student.rollNo}</span>
                            </div>
                        </div>

                        <div class="row g-3">
                            <div class="col-md-6 col-12">
                                <label class="text-muted small">Academic Program / Degree</label>
                                <div class="fw-bold">${student.courseName}</div>
                            </div>
                            <div class="col-md-6 col-12">
                                <label class="text-muted small">Current Semester</label>
                                <div class="fw-bold">Semester ${student.semesterNumber}</div>
                            </div>
                            <div class="col-md-6 col-12">
                                <label class="text-muted small">Department</label>
                                <div>${student.departmentName}</div>
                            </div>
                            <div class="col-md-6 col-12">
                                <label class="text-muted small">Affiliated College</label>
                                <div>${student.collegeName}</div>
                            </div>
                            <div class="col-md-6 col-12">
                                <label class="text-muted small">Session</label>
                                <div>${student.sessionCode}</div>
                            </div>
                            <div class="col-md-6 col-12">
                                <label class="text-muted small">Date of Birth</label>
                                <div>${student.dob}</div>
                            </div>
                            <div class="col-md-6 col-12">
                                <label class="text-muted small">Email Address</label>
                                <div>${student.email}</div>
                            </div>
                            <div class="col-md-6 col-12">
                                <label class="text-muted small">Phone</label>
                                <div>${student.phone}</div>
                            </div>
                            <div class="col-md-6 col-12">
                                <label class="text-muted small">Father's Name</label>
                                <div>${student.fatherName}</div>
                            </div>
                            <div class="col-md-6 col-12">
                                <label class="text-muted small">Mother's Name</label>
                                <div>${student.motherName}</div>
                            </div>
                            <div class="col-12">
                                <label class="text-muted small">Permanent Address</label>
                                <div>${student.address}</div>
                            </div>
                        </div>
                    </div>
                </div>

                <!-- Password Update Card -->
                <div class="col-lg-5 col-12">
                    <div class="card shadow-sm border rounded-4 p-4">
                        <h4 class="h5 fw-bold mb-3"><i class="bi bi-key-fill text-warning me-2"></i> Update Password</h4>
                        <form action="${pageContext.request.contextPath}/change-password" method="POST">
                            <div class="mb-3">
                                <label class="form-label fw-semibold">Current Password</label>
                                <input type="password" name="old_password" class="form-control" required>
                            </div>
                            <div class="mb-3">
                                <label class="form-label fw-semibold">New Password</label>
                                <input type="password" name="new_password" class="form-control" required minlength="6">
                            </div>
                            <div class="mb-4">
                                <label class="form-label fw-semibold">Confirm Password</label>
                                <input type="password" name="confirm_password" class="form-control" required minlength="6">
                            </div>
                            <button type="submit" class="btn btn-apex-primary w-100">Update Password</button>
                        </form>
                    </div>
                </div>
            </div>

        </div>
    </main>
</div>

<jsp:include page="/WEB-INF/views/common/footer.jsp" />
