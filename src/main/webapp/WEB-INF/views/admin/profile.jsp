<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<jsp:include page="/WEB-INF/views/common/header.jsp">
    <jsp:param name="pageTitle" value="Admin Profile • Apex University" />
</jsp:include>

<div class="dashboard-wrapper">
    <jsp:include page="/WEB-INF/views/common/sidebar.jsp">
        <jsp:param name="activeNav" value="profile" />
    </jsp:include>

    <main class="dashboard-content">
        <div class="container-fluid">
            
            <jsp:include page="/WEB-INF/views/common/alerts.jsp" />

            <div class="row g-4">
                <div class="col-lg-6 col-12">
                    <div class="card shadow-sm border rounded-4 p-4">
                        <h4 class="h5 fw-bold mb-3"><i class="bi bi-person-circle text-primary me-2"></i> Administrator Profile</h4>
                        <div class="mb-3">
                            <label class="form-label text-muted small">Username</label>
                            <input type="text" class="form-control" value="${sessionScope.CURRENT_USER.username}" readonly>
                        </div>
                        <div class="mb-3">
                            <label class="form-label text-muted small">Full Name</label>
                            <input type="text" class="form-control" value="${sessionScope.CURRENT_USER.fullName}" readonly>
                        </div>
                        <div class="mb-3">
                            <label class="form-label text-muted small">Official Email</label>
                            <input type="text" class="form-control" value="${sessionScope.CURRENT_USER.email}" readonly>
                        </div>
                        <div class="mb-3">
                            <label class="form-label text-muted small">Assigned Role</label>
                            <input type="text" class="form-control" value="${sessionScope.CURRENT_USER.role}" readonly>
                        </div>
                    </div>
                </div>

                <div class="col-lg-6 col-12">
                    <div class="card shadow-sm border rounded-4 p-4">
                        <h4 class="h5 fw-bold mb-3"><i class="bi bi-key-fill text-warning me-2"></i> Change Password</h4>
                        <form action="${pageContext.request.contextPath}/change-password" method="POST">
                            <div class="mb-3">
                                <label class="form-label fw-semibold">Current Password</label>
                                <input type="password" name="old_password" class="form-control" required>
                            </div>
                            <div class="mb-3">
                                <label class="form-label fw-semibold">New Password (Min 6 Characters)</label>
                                <input type="password" name="new_password" class="form-control" required minlength="6">
                            </div>
                            <div class="mb-4">
                                <label class="form-label fw-semibold">Confirm New Password</label>
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
