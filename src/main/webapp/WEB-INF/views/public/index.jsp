<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<jsp:include page="/WEB-INF/views/common/header.jsp">
    <jsp:param name="pageTitle" value="Apex University of Technology • Examination & Academic System" />
</jsp:include>

<!-- University Hero Section -->
<section class="apex-hero text-center">
    <div class="container py-4">
        <div class="hero-badge">
            <i class="bi bi-mortarboard-fill"></i> Centralized Automated Examination Portal
        </div>
        <h1 class="display-5 fw-bolder mb-2 text-white">APEX UNIVERSITY OF TECHNOLOGY</h1>
        <p class="lead text-warning fw-semibold mb-4">Official Examination &amp; Academic Management System</p>
        <p class="text-white-50 mx-auto mb-4" style="max-width: 780px;">
            A unified digital governance platform connecting students, affiliated institutions, faculty members, and the examination controller for automated form submission, admit cards, internal evaluation, fee clearance, and tamper-proof marksheet generation.
        </p>
        <div class="d-flex justify-content-center gap-3 flex-wrap">
            <a href="${pageContext.request.contextPath}/result-search" class="btn btn-apex-gold px-4 py-2">
                <i class="bi bi-search me-1"></i> Check Published Results
            </a>
            <a href="${pageContext.request.contextPath}/login?role=STUDENT" class="btn btn-outline-light px-4 py-2">
                <i class="bi bi-person-fill me-1"></i> Student Login
            </a>
            <a href="${pageContext.request.contextPath}/instructions" class="btn btn-outline-warning px-4 py-2">
                <i class="bi bi-info-circle me-1"></i> Instructions
            </a>
        </div>
    </div>
</section>

<!-- 5 Main Portal Cards -->
<div class="container portal-cards-container">
    <div class="row g-4">
        
        <!-- 1. Student Portal -->
        <div class="col-xl col-md-6 col-12">
            <a href="${pageContext.request.contextPath}/login?role=STUDENT" class="portal-card card-student">
                <div class="portal-icon">
                    <i class="bi bi-mortarboard"></i>
                </div>
                <h3 class="portal-title">Student Portal</h3>
                <p class="portal-desc">
                    Fill examination forms, verify subjects, make online fee payments, download Admit Cards, view attendance, and access published semester marksheets.
                </p>
                <div class="portal-action">
                    <span>Access Student Portal</span> <i class="bi bi-arrow-right"></i>
                </div>
            </a>
        </div>

        <!-- 2. Admin Portal -->
        <div class="col-xl col-md-6 col-12">
            <a href="${pageContext.request.contextPath}/login?role=ADMIN" class="portal-card card-admin">
                <div class="portal-icon">
                    <i class="bi bi-shield-lock"></i>
                </div>
                <h3 class="portal-title">Admin Portal</h3>
                <p class="portal-desc">
                    Controller of Examinations workspace: manage students, faculty, colleges, course schedules, form approvals, marks evaluation, results, and audit trails.
                </p>
                <div class="portal-action">
                    <span>Access Admin Portal</span> <i class="bi bi-arrow-right"></i>
                </div>
            </a>
        </div>

        <!-- 3. Teacher Portal -->
        <div class="col-xl col-md-6 col-12">
            <a href="${pageContext.request.contextPath}/login?role=TEACHER" class="portal-card card-teacher">
                <div class="portal-icon">
                    <i class="bi bi-person-video3"></i>
                </div>
                <h3 class="portal-title">Teacher Portal</h3>
                <p class="portal-desc">
                    Faculty dashboard for assigned courses: mark student class attendance, enter and review continuous internal evaluation (CIE) marks, and view routines.
                </p>
                <div class="portal-action">
                    <span>Access Teacher Portal</span> <i class="bi bi-arrow-right"></i>
                </div>
            </a>
        </div>

        <!-- 4. College Portal -->
        <div class="col-xl col-md-6 col-12">
            <a href="${pageContext.request.contextPath}/login?role=COLLEGE" class="portal-card card-college">
                <div class="portal-icon">
                    <i class="bi bi-buildings"></i>
                </div>
                <h3 class="portal-title">College Portal</h3>
                <p class="portal-desc">
                    Affiliated institute administration: student enrollment, exam form verification, college-level clearance, payment audits, and student roster exports.
                </p>
                <div class="portal-action">
                    <span>Access College Portal</span> <i class="bi bi-arrow-right"></i>
                </div>
            </a>
        </div>

        <!-- 5. Result Details -->
        <div class="col-xl col-md-6 col-12">
            <a href="${pageContext.request.contextPath}/result-search" class="portal-card card-result">
                <div class="portal-icon">
                    <i class="bi bi-award"></i>
                </div>
                <h3 class="portal-title">Result Details</h3>
                <p class="portal-desc">
                    Public search portal: instantly look up official examination results, letter grades, SGPA scores, and download certified semester marksheets via Roll Number.
                </p>
                <div class="portal-action">
                    <span>Search Results</span> <i class="bi bi-arrow-right"></i>
                </div>
            </a>
        </div>

    </div>
</div>

<!-- Important Information & Updates Section -->
<section class="container mb-5">
    <div class="d-flex align-items-center justify-content-between mb-3 flex-wrap gap-2">
        <div>
            <h2 class="h3 fw-bold mb-1">Important Information &amp; Updates</h2>
            <p class="text-secondary small mb-0">Live announcements, technical guidelines, and official notifications dynamically synchronized from University Database.</p>
        </div>
        <div>
            <span class="badge bg-primary px-3 py-2"><i class="bi bi-arrow-clockwise me-1"></i> Live Database Feed</span>
        </div>
    </div>

    <!-- Category Filter Tabs -->
    <div class="updates-tabs d-flex gap-2 flex-wrap">
        <button type="button" class="updates-tab-btn active" data-filter="all">
            <i class="bi bi-grid-fill me-1"></i> All Updates
        </button>
        <button type="button" class="updates-tab-btn" data-filter="instructions">
            <i class="bi bi-journal-check me-1"></i> Instructions
        </button>
        <button type="button" class="updates-tab-btn" data-filter="helpdesk">
            <i class="bi bi-headset me-1"></i> HelpDesk Details
        </button>
        <button type="button" class="updates-tab-btn" data-filter="news">
            <i class="bi bi-megaphone-fill me-1"></i> News &amp; Announcements
        </button>
    </div>

    <!-- Dynamic Content Cards -->
    <div class="row g-3" id="updatesContainer">
        
        <!-- 1. Instructions Cards -->
        <c:forEach var="inst" items="${instructions}">
            <div class="col-lg-6 col-12 update-card" data-category="instructions">
                <div class="d-flex align-items-center justify-content-between mb-2">
                    <span class="badge bg-info text-dark update-badge">
                        <i class="bi bi-journal-text me-1"></i> ${inst.category}
                    </span>
                    <small class="text-secondary">Official University Guideline</small>
                </div>
                <h4 class="h5 fw-bold mb-2">${inst.title}</h4>
                <p class="text-secondary small mb-3" style="white-space: pre-line;">${inst.content}</p>
                <a href="${pageContext.request.contextPath}/instructions" class="text-decoration-none fw-semibold small text-primary">
                    Read Detailed Guide <i class="bi bi-arrow-right"></i>
                </a>
            </div>
        </c:forEach>

        <!-- 2. HelpDesk Details Cards -->
        <c:forEach var="hd" items="${helpdeskList}">
            <div class="col-lg-6 col-12 update-card" data-category="helpdesk">
                <div class="d-flex align-items-center justify-content-between mb-2">
                    <span class="badge bg-warning text-dark update-badge">
                        <i class="bi bi-telephone-fill me-1"></i> HelpDesk: ${hd.category}
                    </span>
                    <small class="text-secondary">${hd.timing}</small>
                </div>
                <h4 class="h5 fw-bold mb-1">${hd.title}</h4>
                <p class="text-secondary small mb-2">Officer/Desk: <strong>${hd.contactPerson}</strong></p>
                <div class="d-flex flex-wrap gap-3 small mb-2">
                    <c:if test="${not empty hd.phoneNumber}">
                        <div>
                            <i class="bi bi-telephone text-primary"></i>
                            <a href="tel:${hd.phoneNumber}" class="fw-bold text-decoration-none">${hd.phoneNumber}</a>
                        </div>
                    </c:if>
                    <c:if test="${not empty hd.email}">
                        <div>
                            <i class="bi bi-envelope text-primary"></i>
                            <a href="mailto:${hd.email}" class="text-decoration-none">${hd.email}</a>
                        </div>
                    </c:if>
                </div>
                <small class="text-muted d-block"><i class="bi bi-geo-alt"></i> ${hd.address}</small>
            </div>
        </c:forEach>

        <!-- 3. News & Announcements Cards -->
        <c:forEach var="notice" items="${notices}">
            <div class="col-lg-6 col-12 update-card" data-category="news">
                <div class="d-flex align-items-center justify-content-between mb-2">
                    <span class="badge bg-danger update-badge">
                        <i class="bi bi-bell-fill me-1"></i> ${notice.category}
                    </span>
                    <small class="text-secondary"><i class="bi bi-calendar3"></i> ${notice.publishedAt}</small>
                </div>
                <h4 class="h5 fw-bold mb-2">${notice.title}</h4>
                <p class="text-secondary small mb-3">${notice.description}</p>
                <div class="d-flex align-items-center justify-content-between">
                    <span class="badge bg-light text-dark border">Audience: ${notice.targetAudience}</span>
                    <a href="${pageContext.request.contextPath}/notices" class="text-decoration-none fw-semibold small text-primary">
                        View Announcement <i class="bi bi-arrow-right"></i>
                    </a>
                </div>
            </div>
        </c:forEach>

    </div>
</section>

<!-- University Stats Highlights -->
<section class="bg-primary text-white py-4 my-4">
    <div class="container">
        <div class="row text-center g-4">
            <div class="col-md-3 col-6">
                <div class="fs-2 fw-bolder">${stats.totalStudents != null ? stats.totalStudents : '2,500+'}</div>
                <div class="text-warning text-uppercase small fw-bold">Registered Students</div>
            </div>
            <div class="col-md-3 col-6">
                <div class="fs-2 fw-bolder">${stats.totalColleges != null ? stats.totalColleges : '15+'}</div>
                <div class="text-warning text-uppercase small fw-bold">Affiliated Colleges</div>
            </div>
            <div class="col-md-3 col-6">
                <div class="fs-2 fw-bolder">${stats.totalCourses != null ? stats.totalCourses : '24+'}</div>
                <div class="text-warning text-uppercase small fw-bold">Academic Programs</div>
            </div>
            <div class="col-md-3 col-6">
                <div class="fs-2 fw-bolder">100%</div>
                <div class="text-warning text-uppercase small fw-bold">Digital Examination Records</div>
            </div>
        </div>
    </div>
</section>

<jsp:include page="/WEB-INF/views/common/footer.jsp" />
