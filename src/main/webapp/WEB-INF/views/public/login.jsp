<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<jsp:include page="/WEB-INF/views/common/header.jsp">
    <jsp:param name="pageTitle" value="Apex Portal Login • Apex University of Technology" />
</jsp:include>

<div class="container py-5">
    <div class="row justify-content-center">
        <div class="col-lg-5 col-md-8 col-12">
            
            <jsp:include page="/WEB-INF/views/common/alerts.jsp" />

            <div class="card shadow-lg border-0 rounded-4 overflow-hidden">
                <!-- Login Header Banner -->
                <div class="p-4 text-center bg-primary text-white" style="background: linear-gradient(135deg, #0a192f 0%, #1e3a8a 100%) !important;">
                    <img src="${pageContext.request.contextPath}/static/images/logo.svg" alt="Apex Logo" style="width: 60px; height: 60px;" class="mb-2">
                    <h3 class="h4 fw-bold mb-1">University Portal Login</h3>
                    <p class="text-warning small mb-0 text-uppercase letter-spacing-1">Secure Academic Authentication</p>
                </div>

                <div class="card-body p-4">
                    
                    <!-- Role Switcher Tabs -->
                    <ul class="nav nav-pills nav-fill mb-4 p-1 bg-light rounded-3" id="loginRoleTabs">
                        <li class="nav-item">
                            <a class="nav-link ${empty selectedRole || selectedRole == 'STUDENT' ? 'active bg-primary text-white' : 'text-dark'}" 
                               href="${pageContext.request.contextPath}/login?role=STUDENT">
                               <i class="bi bi-mortarboard me-1"></i> Student
                            </a>
                        </li>
                        <li class="nav-item">
                            <a class="nav-link ${selectedRole == 'TEACHER' ? 'active bg-success text-white' : 'text-dark'}" 
                               href="${pageContext.request.contextPath}/login?role=TEACHER">
                               <i class="bi bi-person-video3 me-1"></i> Teacher
                            </a>
                        </li>
                        <li class="nav-item">
                            <a class="nav-link ${selectedRole == 'COLLEGE' ? 'active bg-purple text-white' : 'text-dark'}" 
                               href="${pageContext.request.contextPath}/login?role=COLLEGE">
                               <i class="bi bi-buildings me-1"></i> College
                            </a>
                        </li>
                        <li class="nav-item">
                            <a class="nav-link ${selectedRole == 'ADMIN' ? 'active bg-dark text-white' : 'text-dark'}" 
                               href="${pageContext.request.contextPath}/login?role=ADMIN">
                               <i class="bi bi-shield-lock me-1"></i> Admin
                            </a>
                        </li>
                    </ul>

                    <!-- Login Form -->
                    <form action="${pageContext.request.contextPath}/login" method="POST">
                        <input type="hidden" name="csrf_token" value="${csrf_token}">
                        <input type="hidden" name="role" value="${not empty selectedRole ? selectedRole : 'STUDENT'}">

                        <div class="mb-3">
                            <label class="form-label fw-semibold">
                                <c:choose>
                                    <c:when test="${selectedRole == 'ADMIN'}">Admin Username or Email</c:when>
                                    <c:when test="${selectedRole == 'TEACHER'}">Employee ID or Faculty Email</c:when>
                                    <c:when test="${selectedRole == 'COLLEGE'}">College Code or Official Email</c:when>
                                    <c:otherwise>Registration No. / Roll No. / Email</c:otherwise>
                                </c:choose>
                            </label>
                            <div class="input-group">
                                <span class="input-group-text"><i class="bi bi-person"></i></span>
                                <input type="text" name="identifier" class="form-control" 
                                       placeholder="<c:choose><c:when test='${selectedRole == \"ADMIN\"}'>e.g. admin</c:when><c:when test='${selectedRole == \"TEACHER\"}'>e.g. TCH101</c:when><c:when test='${selectedRole == \"COLLEGE\"}'>e.g. COL101</c:when><c:otherwise>e.g. APEX202200101 or 11500122001</c:otherwise></c:choose>" 
                                       required autofocus>
                            </div>
                        </div>

                        <div class="mb-3">
                            <label class="form-label fw-semibold">Password</label>
                            <div class="input-group">
                                <span class="input-group-text"><i class="bi bi-key"></i></span>
                                <input type="password" name="password" id="loginPassword" class="form-control" placeholder="Enter portal password" required>
                                <button type="button" class="btn btn-outline-secondary" onclick="togglePasswordVisibility()">
                                    <i class="bi bi-eye" id="togglePasswordIcon"></i>
                                </button>
                            </div>
                        </div>

                        <div class="d-flex justify-content-between align-items-center mb-4">
                            <div class="form-check">
                                <input class="form-check-input" type="checkbox" id="rememberMe">
                                <label class="form-check-label small" for="rememberMe">Remember Session</label>
                            </div>
                            <a href="${pageContext.request.contextPath}/helpdesk" class="small text-decoration-none">Forgot Password?</a>
                        </div>

                        <button type="submit" class="btn btn-apex-primary w-100 py-2 fs-6">
                            <i class="bi bi-box-arrow-in-right me-1"></i> Sign In to ${selectedRole != null ? selectedRole : 'Student'} Portal
                        </button>
                    </form>

                    <!-- Test Credentials helper badge -->
                    <div class="mt-4 p-3 rounded-3 bg-light border small">
                        <div class="fw-bold text-secondary mb-1"><i class="bi bi-info-circle me-1"></i> Demonstration Credentials:</div>
                        <ul class="list-unstyled mb-0 text-muted" style="font-size: 0.8rem;">
                            <li><strong>Student:</strong> <code>APEX202200101</code> or <code>11500122001</code> / <code>student123</code></li>
                            <li><strong>Teacher:</strong> <code>TCH101</code> / <code>teacher123</code></li>
                            <li><strong>College:</strong> <code>COL101</code> / <code>college123</code></li>
                            <li><strong>Admin:</strong> <code>admin</code> / <code>admin123</code></li>
                        </ul>
                    </div>

                </div>
            </div>

        </div>
    </div>
</div>

<script>
function togglePasswordVisibility() {
    const input = document.getElementById('loginPassword');
    const icon = document.getElementById('togglePasswordIcon');
    if (input.type === 'password') {
        input.type = 'text';
        icon.className = 'bi bi-eye-slash';
    } else {
        input.type = 'password';
        icon.className = 'bi bi-eye';
    }
}
</script>

<jsp:include page="/WEB-INF/views/common/footer.jsp" />
